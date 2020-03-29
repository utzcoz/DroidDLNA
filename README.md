# cling-test-instrument

This project is forked from [DroidDLNA](https://github.com/offbye/DroidDLNA), and removes almost
all `Activity` logic and control logic. I just keep the basic renderer logic as test code, and
provide a lib called `cling-test-instrument` as test lib. I use it to test the basic logic
of renderer based on `cling`. It will help to test renderer app based on `cling`.

## Download

```shell
git clone --recursive https://github.com/utzcoz/cling-test-instrument.git
```

## Build

```shell
./gradlew build
```

## Run tests

```shell    
./run-test.sh
```

And it will run all tests to connected device, and show the summary result of tests.

## Generate aar

```shell
./generate-aar.sh
```

And it will generate `aar` to `release` directory.

## Usage

### Add dependencies

Add `aar` as your dependency, and add following projects to your `gradle` dependencies:

```groovy
implementation 'androidx.test:runner:1.2.0'
implementation 'androidx.test.ext:junit:1.1.1'
implementation 'androidx.test.espresso:espresso-core:3.2.0'
implementation 'org.fourthline.cling:cling-core:2.1.2-SNAPSHOT'
implementation 'org.fourthline.cling:cling-support:2.1.2-SNAPSHOT'
implementation 'org.eclipse.jetty:jetty-client:8.1.8.v20121106'
implementation 'org.eclipse.jetty:jetty-servlet:8.1.8.v20121106'
implementation 'org.eclipse.jetty:jetty-server:8.1.8.v20121106'
```

### Set up and clean up
In your test file, setup and clean up `ControlPointUpnpService` as following code:

```java
private ControlPointUpnpService upnpService;

@Before
public void setUp() {
    upnpService = new ControlPointUpnpService();
}

@After
public void tearDown() {
    upnpService.shutdown();
}
```

The `ControlPointUpnpService` will run another `UpnpService` to another `jetty` server. The default
`UpnpServiceImpl` instances in a process will run the same `jetty` server, because of the `cling`
implementation.

### Search device

We provide a class called `TesetHelper`, and we can use its 
`searchRemoteDevice(ControlPointUpnpService)` to search device. For example:

```java
@Test
public void testTestUpnpServiceSearchDeviceSucceed() {
    RemoteDevice remoteDevice = TestHelper.searchRemoteDevice(upnpService);
    assertNotNull(remoteDevice);
    Assert.assertEquals(Utils.uniqueSystemIdentifier(), remoteDevice.getIdentity().getUdn());
    assertEquals(UDADeviceType.DEFAULT_NAMESPACE, remoteDevice.getType().getNamespace());
    assertEquals("MediaRenderer", remoteDevice.getType().getType());
    assertEquals(1, remoteDevice.getType().getVersion());
    DeviceDetails deviceDetails = remoteDevice.getDetails();
    assertEquals(
        Utils.getRenderName() + " (" + android.os.Build.MODEL + ")",
        deviceDetails.getFriendlyName()
    );
    assertEquals(Utils.MANUFACTURER, deviceDetails.getManufacturerDetails().getManufacturer());
    ModelDetails modelDetails = deviceDetails.getModelDetails();
    assertEquals(Utils.DMR_NAME, modelDetails.getModelName());
    assertEquals(Utils.DMR_DESC, modelDetails.getModelDescription());
    assertEquals("1", modelDetails.getModelNumber());
    assertEquals(Utils.DMR_MODEL_URL, modelDetails.getModelURI().toString());
    DLNADoc doc = deviceDetails.getDlnaDocs()[0];
    assertEquals("DMR", doc.getDevClass());
    assertEquals(DLNADoc.Version.V1_5.toString(), doc.getVersion());
}
```

### Fetch service

The most important three services `cling` uses are `AVTransport`, `RenderingControl` and
`ConnectionManager`. So we provide helper method to fetch those services, and use them
to execute action to control the rendering service later.

For example:

```java
private GetCurrentConnectionInfoAction executeGetCurrentConnectionInfoAction(
        ControlPointUpnpService upnpService) {
    GetCurrentConnectionInfoAction action =
        new GetCurrentConnectionInfoAction(UpnpServiceFetcher.getConnectionManagerService(upnpService), 0);
    TestHelper.executeAction(upnpService, action);
    return action;
}

@Test
public void testListPresentsSucceed() {
    ListPresetsAction action =
        new ListPresetsAction(
                UpnpServiceFetcher.getAudioRenderingControl(upnpService),
                Utils.getDefaultInstanceId()
        );
    TestHelper.executeAction(upnpService, action);
    assertEquals(PresetName.FactoryDefaults.toString(), action.getCurrentPresentNameList());
}

@Test
public void testSetNextAVTransportURISucceed() {
    String uri = "next-some-uri";
    String uriMetaData = "next-some-meta-data";
    SetNextAVTransportURIAction action =
         new SetNextAVTransportURIAction(
                UpnpServiceFetcher.getAVTransportService(upnpService),
                Utils.getDefaultInstanceId(),
                uri,
                uriMetaData
         );
    TestHelper.executeAction(upnpService, action);
    assertEquals(uri, ClingLocalRenderer.getLocalRender().getNextPlayURI());
    assertEquals(uriMetaData, ClingLocalRenderer.getLocalRender().getNextURIMetaData());
}
```

### Execute action

From previous example, after fetching service, we can use it to execute action to control
the rendering service, or get its state.

We provide a method called `executeAction(UpnpService, ActionInvocation<RemoteService>)` in 
`TestHelper` to execute action and wait the result blocked. And we provide heavy use action
wrapper with `ActionInvocation`, and you can use them directly.

The supported list is:

```
AVTransport

GetDeviceCapabilitiesAction
GetMediaInfoAction
GetPositionInfoAction
GetTransportInfoAction
GetTransportSettingsAction
PauseAction
PlayAction
SeekAction
SetAVTransportURIAction
SetNextAVTransportURIAction
SetPlayModeAction
StopAction

ConnectionManager

GetCurrentConnectionIDsAction
GetCurrentConnectionInfoAction
GetProtocolInfoAction

RenderingControl

GetMuteAction
GetVolumeAction
ListPresetsAction
SetMuteAction
SetVolumeAction
```

## Example

The `test-app` directory contains a very simple rendering service without real rendering implementation.
And it gives some examples to use `clint-test-instrument` with instrumentation to test rendering
service.
