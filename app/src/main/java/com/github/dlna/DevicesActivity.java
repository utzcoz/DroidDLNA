package com.github.dlna;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.dlna.application.BaseApplication;
import com.github.dlna.dmp.DeviceItem;
import com.github.dlna.dmr.ZxtMediaRenderer;
import com.github.dlna.dms.MediaServer;
import com.github.dlna.util.FixedAndroidHandler;
import com.github.dlna.util.Utils;

import org.fourthline.cling.android.AndroidUpnpService;
import org.fourthline.cling.android.AndroidUpnpServiceImpl;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.registry.DefaultRegistryListener;
import org.fourthline.cling.registry.Registry;
import org.seamless.util.logging.LoggingUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DevicesActivity extends Activity {

    private static final Logger log = Logger.getLogger(DevicesActivity.class
            .getName());

    private final static String LOGTAG = "DevicesActivity";

    private ListView mDmrLv;

    private ArrayList<DeviceItem> mDmrList = new ArrayList<DeviceItem>();

    private long exitTime = 0;

    private DevAdapter mDmrDevAdapter;

    private AndroidUpnpService upnpService;

    private DeviceListRegistryListener deviceListRegistryListener;

    private MediaServer mediaServer;

    private ServiceConnection serviceConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder service) {

            mDmrList.clear();

            upnpService = (AndroidUpnpService) service;
            BaseApplication.upnpService = upnpService;

            Log.v(LOGTAG, "Connected to UPnP Service");

            if (mediaServer == null
                    && Settings.getDmsOn()) {
                try {
                    mediaServer = new MediaServer(DevicesActivity.this);
                    upnpService.getRegistry()
                            .addDevice(mediaServer.getDevice());
                } catch (Exception ex) {
                    // TODO: handle exception
                    log.log(Level.SEVERE, "Creating demo device failed", ex);
                    Toast.makeText(DevicesActivity.this,
                            R.string.create_demo_failed, Toast.LENGTH_SHORT)
                            .show();
                    return;
                }
            }

            if (Settings.getRenderOn()) {
                ZxtMediaRenderer mediaRenderer = new ZxtMediaRenderer(1,
                        DevicesActivity.this);
                upnpService.getRegistry().addDevice(mediaRenderer.getDevice());
                deviceListRegistryListener.dmrAdded(new DeviceItem(
                        mediaRenderer.getDevice()));
            }

            // xgf

            // Getting ready for future device advertisements
            upnpService.getRegistry().addListener(deviceListRegistryListener);
            // Refresh device list
            upnpService.getControlPoint().search();

            if (null != mDmrList && mDmrList.size() > 0
                    && null == BaseApplication.dmrDeviceItem) {
                BaseApplication.dmrDeviceItem = mDmrList.get(0);
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            upnpService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Fix the logging integration between java.util.logging and Android
        // internal logging
        LoggingUtil.resetRootHandler(new FixedAndroidHandler());
        Logger.getLogger("org.teleal.cling").setLevel(Level.INFO);

        setContentView(R.layout.devices);
        init();

        deviceListRegistryListener = new DeviceListRegistryListener();

        getApplicationContext().bindService(
                new Intent(this, AndroidUpnpServiceImpl.class),
                serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void init() {
        mDmrLv = findViewById(R.id.renderer_list);

        if (null != mDmrList && mDmrList.size() > 0) {
            BaseApplication.dmrDeviceItem = mDmrList.get(0);
        }

        mDmrDevAdapter = new DevAdapter(DevicesActivity.this, 0, mDmrList);
        mDmrLv.setAdapter(mDmrDevAdapter);
        mDmrLv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {

                if (null != mDmrList && mDmrList.size() > 0) {

                    BaseApplication.isLocalDmr = null != mDmrList.get(arg2).getDevice()
                            && null != BaseApplication.deviceItem
                            && null != mDmrList.get(arg2).getDevice()
                            .getDetails().getModelDetails()
                            && Utils.DMR_NAME.equals(mDmrList.get(arg2)
                            .getDevice().getDetails().getModelDetails()
                            .getModelName())
                            && Utils.getDevName(
                            mDmrList.get(arg2).getDevice().getDetails()
                                    .getFriendlyName()).equals(
                            Utils.getDevName(BaseApplication.deviceItem
                                    .getDevice().getDetails()
                                    .getFriendlyName()));
                    BaseApplication.dmrDeviceItem = mDmrList.get(arg2);
                    mDmrDevAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (upnpService != null) {
            upnpService.getRegistry()
                    .removeListener(deviceListRegistryListener);
        }
        getApplicationContext().unbindService(serviceConnection);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, R.string.search_lan).setIcon(
                android.R.drawable.ic_menu_search);
        menu.add(0, 1, 0, R.string.menu_exit).setIcon(
                android.R.drawable.ic_menu_close_clear_cancel);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                searchNetwork();
                break;
            case 1: {
                finish();
                System.exit(0);
                break;
            }
        }
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), R.string.exit,
                        Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void searchNetwork() {
        if (upnpService == null)
            return;
        Toast.makeText(this, R.string.searching_lan, Toast.LENGTH_SHORT).show();
        upnpService.getRegistry().removeAllRemoteDevices();
        upnpService.getControlPoint().search();
    }

    public class DeviceListRegistryListener extends DefaultRegistryListener {

        /* Discovery performance optimization for very slow Android devices! */

        @Override
        public void remoteDeviceDiscoveryStarted(Registry registry,
                                                 RemoteDevice device) {
        }

        @Override
        public void remoteDeviceDiscoveryFailed(Registry registry,
                                                final RemoteDevice device, final Exception ex) {
        }

        /*
         * End of optimization, you can remove the whole block if your Android
         * handset is fast (>= 600 Mhz)
         */

        @SuppressLint("LongLogTag")
        @Override
        public void remoteDeviceAdded(Registry registry, RemoteDevice device) {
            Log.e("DeviceListRegistryListener",
                    "remoteDeviceAdded:" + device.toString()
                            + device.getType().getType());

            if (device.getType().getNamespace().equals("schemas-upnp-org")
                    && device.getType().getType().equals("MediaRenderer")) {
                final DeviceItem dmrDisplay = new DeviceItem(device, device
                        .getDetails().getFriendlyName(),
                        device.getDisplayString(), "(REMOTE) "
                        + device.getType().getDisplayString());
                dmrAdded(dmrDisplay);
            }
        }

        @Override
        public void remoteDeviceRemoved(Registry registry, RemoteDevice device) {
            if (device.getType().getNamespace().equals("schemas-upnp-org")
                    && device.getType().getType().equals("MediaRenderer")) {
                final DeviceItem dmrDisplay = new DeviceItem(device, device
                        .getDetails().getFriendlyName(),
                        device.getDisplayString(), "(REMOTE) "
                        + device.getType().getDisplayString());
                dmrRemoved(dmrDisplay);
            }
        }

        @SuppressLint("LongLogTag")
        @Override
        public void localDeviceAdded(Registry registry, LocalDevice device) {
            Log.e("DeviceListRegistryListener",
                    "localDeviceAdded:" + device.toString()
                            + device.getType().getType());
        }

        @SuppressLint("LongLogTag")
        @Override
        public void localDeviceRemoved(Registry registry, LocalDevice device) {
            Log.e("DeviceListRegistryListener",
                    "localDeviceRemoved:" + device.toString()
                            + device.getType().getType());
        }

        void dmrAdded(final DeviceItem di) {
            runOnUiThread(new Runnable() {
                public void run() {
                    if (!mDmrList.contains(di)) {
                        mDmrList.add(di);
                        mDmrDevAdapter.notifyDataSetChanged();
                    }
                }
            });
        }

        void dmrRemoved(final DeviceItem di) {
            runOnUiThread(new Runnable() {
                public void run() {
                    mDmrList.remove(di);
                    mDmrDevAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    class DevAdapter extends ArrayAdapter<DeviceItem> {

        private LayoutInflater mInflater;

        private List<DeviceItem> deviceItems;

        DevAdapter(Context context, int textViewResourceId,
                   List<DeviceItem> objects) {
            super(context, textViewResourceId, objects);
            this.mInflater = ((LayoutInflater) context
                    .getSystemService("layout_inflater"));
            this.deviceItems = objects;
        }

        public int getCount() {
            return this.deviceItems.size();
        }

        public DeviceItem getItem(int paramInt) {
            return this.deviceItems.get(paramInt);
        }

        public long getItemId(int paramInt) {
            return paramInt;
        }

        public View getView(int position, View view, ViewGroup viewGroup) {

            DevHolder holder;
            if (view == null) {
                view = this.mInflater.inflate(R.layout.dmr_item, null);
                holder = new DevHolder();
                holder.filename = view.findViewById(R.id.dmr_name_tv);
                holder.checkBox = view.findViewById(R.id.dmr_cb);
                view.setTag(holder);
            } else {
                holder = (DevHolder) view.getTag();
            }

            DeviceItem item = this.deviceItems.get(position);
            holder.filename.setText(item.toString());
            if (null != BaseApplication.deviceItem
                    && BaseApplication.deviceItem.equals(item)) {
                holder.checkBox.setChecked(true);
            } else if (null != BaseApplication.dmrDeviceItem
                    && BaseApplication.dmrDeviceItem.equals(item)) {
                holder.checkBox.setChecked(true);
            } else {
                holder.checkBox.setChecked(false);
            }
            return view;
        }

        final class DevHolder {
            TextView filename;
            CheckBox checkBox;
        }

    }
}
