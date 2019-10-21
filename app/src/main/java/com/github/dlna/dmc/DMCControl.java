package com.github.dlna.dmc;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.github.dlna.R;
import com.github.dlna.dmp.DeviceItem;
import com.github.dlna.util.Action;

import org.fourthline.cling.android.AndroidUpnpService;
import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.UDAServiceType;

public class DMCControl {

    private static final int TYPE_IMAGE = 1;

    private static final int TYPE_AUDIO = 2;

    private static final int TYPE_VIDEO = 3;

    private static final int CUT_VOC = 0;

    private static final int ADD_VOC = 1;

    private static boolean isExit = false;

    private Activity activity;

    private int controlType = 1;

    private DeviceItem executeDeviceItem;

    private boolean isMute = false;

    private String metaData;

    private AndroidUpnpService upnpService;

    private String uriString;

    @SuppressLint("HandlerLeak")
    private Handler mHandle = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DMCControlMessage.ADDVOLUME:
                case DMCControlMessage.CONNECTIONFAILED:
                case DMCControlMessage.GETMEDIA:
                case DMCControlMessage.GETTRANSPORTINFO:
                case DMCControlMessage.GET_CURRENT_VOLUME:
                case DMCControlMessage.PAUSE:
                case DMCControlMessage.PLAYAUDIOFAILED:
                case DMCControlMessage.PLAYIMAGEFAILED:
                case DMCControlMessage.REDUCEVOLUME:
                case DMCControlMessage.PLAYVIDEOFAILED:
                case DMCControlMessage.REMOTE_NOMEDIA:
                case DMCControlMessage.STOP:
                case DMCControlMessage.UPDATE_PLAY_TRACK: {
                    break;
                }

                case DMCControlMessage.CONNECTIONSUCESSED: {
                    getTransportInfo();
                    break;
                }

                case DMCControlMessage.GETMUTE: {
                    DMCControl.this.isMute = msg.getData().getBoolean("mute");
                    DMCControl.this.setMuteToActivity();
                    break;
                }

                case DMCControlMessage.GETPOTITION: {
                    getPositionInfo();
                    if (!isExit && controlType != TYPE_IMAGE) {
                        mHandle.sendEmptyMessageDelayed(
                                DMCControlMessage.GETPOTITION, 500);
                    }
                    break;
                }

                case DMCControlMessage.PLAY: {
                    mHandle.sendEmptyMessageDelayed(DMCControlMessage.GETPOTITION, 500);
                    play();
                    break;
                }

                case DMCControlMessage.PLAYMEDIAFAILED: {
                    setPlayErrorMessage();
                    stopGetPosition();
                    break;
                }

                case DMCControlMessage.SETMUTE: {
                    isMute = msg.getData().getBoolean("mute");
                    setMute(!isMute);
                    break;
                }

                case DMCControlMessage.SETMUTESUC: {
                    isMute = msg.getData().getBoolean("mute");
                    setMuteToActivity();
                    break;
                }

                case DMCControlMessage.SETURL: {
                    setAvURL();
                    break;
                }

                case DMCControlMessage.SETVOLUME: {
                    if (msg.getData().getInt("isSetVolume") == CUT_VOC) {
                        setVolume(msg.getData().getLong("getVolume"), CUT_VOC);
                    } else {
                        setVolume(msg.getData().getLong("getVolume"), ADD_VOC);
                    }
                    break;
                }
            }
        }
    };

    public DMCControl(Activity paramActivity, int paramInt,
                      DeviceItem paramDeviceItem,
                      AndroidUpnpService paramAndroidUpnpService,
                      String paramString1,
                      String paramString2) {
        this.activity = paramActivity;
        this.controlType = paramInt;
        this.executeDeviceItem = paramDeviceItem;
        this.upnpService = paramAndroidUpnpService;
        this.uriString = paramString1;
        this.metaData = paramString2;
    }

    private void setPlayErrorMessage() {
        Intent localIntent = new Intent();
        if (this.controlType == TYPE_VIDEO) {
            localIntent.setAction(Action.PLAY_ERR_VIDEO);
        } else if (this.controlType == TYPE_AUDIO) {
            localIntent.setAction(Action.PLAY_ERR_AUDIO);
        } else {
            localIntent.setAction(Action.PLAY_ERR_IMAGE);
        }
        activity.sendBroadcast(localIntent);
    }

    private void stopGetPosition() {
        Message msg = new Message();
        msg.what = DMCControlMessage.GETPOTITION;
        msg.arg1 = 1;
        mHandle.sendMessage(msg);
    }

    private void getPositionInfo() {
        try {
            Service localService =
                    executeDeviceItem.getDevice().findService(new UDAServiceType("AVTransport"));
            if (localService != null) {
                upnpService
                        .getControlPoint()
                        .execute(
                                new GetPositionInfoCallback(localService, mHandle, this.activity)
                        );
            }
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }

    public void getProtocolInfos(String paramString) {
        try {
            Service localService =
                    executeDeviceItem
                            .getDevice()
                            .findService(new UDAServiceType("ConnectionManager"));
            if (localService != null) {
                upnpService
                        .getControlPoint()
                        .execute(
                                new GetProtocolInfoCallback(
                                        localService,
                                        this.upnpService.getControlPoint(),
                                        paramString,
                                        mHandle
                                )
                        );
            }
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }

    private void getTransportInfo() {
        try {
            Service localService = this.executeDeviceItem.getDevice()
                    .findService(new UDAServiceType("AVTransport"));
            if (localService != null) {
                upnpService
                        .getControlPoint()
                        .execute(
                                new GetTransportInfoCallback(
                                        localService,
                                        mHandle,
                                        false,
                                        controlType
                                )
                        );
            }
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }

    private void play() {
        try {
            Service localService =
                    executeDeviceItem
                            .getDevice()
                            .findService(new UDAServiceType("AVTransport"));
            if (localService != null) {
                Log.e("start play", "start play");
                upnpService
                        .getControlPoint()
                        .execute(
                                new PlayerCallback(localService, mHandle)
                        );
            } else {
                Log.e("null", "null");
            }
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }

    private void setAvURL() {
        try {
            Service localService =
                    executeDeviceItem
                            .getDevice()
                            .findService(new UDAServiceType("AVTransport"));
            if (localService != null) {
                Log.e("set url", "set url" + this.uriString);
                upnpService
                        .getControlPoint()
                        .execute(
                                new SetAVTransportURIActionCallback(
                                        localService,
                                        uriString,
                                        metaData,
                                        mHandle,
                                        controlType
                                )
                        );
            } else {
                Log.e("null", "null");
            }
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }

    public void setCurrentPlayPath(String paramString1, String paramString2) {
        uriString = paramString1;
        metaData = paramString2;
    }

    private void setMute(boolean paramBoolean) {
        try {
            Service localService =
                    executeDeviceItem
                            .getDevice()
                            .findService(new UDAServiceType("RenderingControl"));
            if (localService != null) {
                ControlPoint localControlPoint = upnpService.getControlPoint();
                localControlPoint.execute(
                        new SetMuteCalllback(localService, paramBoolean, mHandle)
                );
            } else {
                Log.e("null", "null");
            }
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }

    private void setMuteToActivity() {
    }

    private void setVolume(long paramLong, int paramInt) {
        Service localService;
        try {
            localService =
                    executeDeviceItem
                            .getDevice()
                            .findService(new UDAServiceType("RenderingControl"));
            if (localService != null) {
                if (paramInt == CUT_VOC) {
                    if (paramLong >= 0L) {
                        paramLong -= 1L;
                    } else {
                        Toast.makeText(activity, R.string.min_voc, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    paramLong += 1L;
                }
                upnpService
                        .getControlPoint()
                        .execute(
                                new SetVolumeCallback(localService, paramLong)
                        );
            }
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }

    public void stop(Boolean paramBoolean) {
        try {
            Service localService =
                    executeDeviceItem
                            .getDevice()
                            .findService(new UDAServiceType("AVTransport"));
            if (localService != null) {
                upnpService
                        .getControlPoint()
                        .execute(
                                new StopCallback(localService, mHandle, paramBoolean, controlType)
                        );
            } else {
                Log.e("null", "null");
            }
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }

}
