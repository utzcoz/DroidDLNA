
package com.github.dlna.dmp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.dlna.R;
import com.github.dlna.util.Action;
import com.github.dlna.util.Utils;

import java.io.IOException;

public class GPlayer extends Activity implements OnCompletionListener, OnErrorListener,
        OnInfoListener, OnPreparedListener, SurfaceHolder.Callback {
    private final static String TAG = "GPlayer";

    private static final int MEDIA_PLAYER_PREPARED = 4005;
    private static final int MEDIA_PLAYER_PROGRESS_UPDATE = 4006;
    private static final int MEDIA_PLAYER_HIDDEN_CONTROL = 4009;

    private static MediaListener mediaListener;

    Display currentDisplay;

    SurfaceView surfaceView;

    SurfaceHolder surfaceHolder;

    MediaPlayer mediaPlayer;

    MediaController mediaController;

    String playURI;

    private AudioManager audioManager;

    private TextView textViewTime;

    private SeekBar seekBarProgress;

    private TextView textViewLength;

    private ImageButton pauseButton;

    private LinearLayout layoutBottom;

    private RelativeLayout layoutTop;

    private TextView videoTitle;

    private int backCount;

    private PlayBroadcastReceiver playReceiverBroadcast = new PlayBroadcastReceiver();

    public static void setMediaListener(MediaListener mediaListener) {
        GPlayer.mediaListener = mediaListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gplayer);
        audioManager = (AudioManager) getSystemService(Service.AUDIO_SERVICE);

        surfaceView = findViewById(R.id.gplayer_surfaceview);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);

        mediaPlayer = new MediaPlayer();

        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnInfoListener(this);
        mediaPlayer.setOnPreparedListener(this);

        initControl();

        Intent intent = getIntent();
        playURI = intent.getStringExtra("playURI");
        if (!TextUtils.isEmpty(playURI)) {
            setUri(playURI);
        }

        setTitle(intent);
        currentDisplay = getWindowManager().getDefaultDisplay();

        registerBroadcast();
    }

    private void setTitle(Intent intent) {
        String name = intent.getStringExtra("name");
        if (!TextUtils.isEmpty(name)) {
            videoTitle.setText(name);
        }
    }

    private void initControl() {
        mediaController = new MediaController(this);

        layoutTop = findViewById(R.id.layout_top);
        videoTitle = findViewById(R.id.video_title);

        textViewTime = findViewById(R.id.current_time);
        textViewLength = findViewById(R.id.total_time);
        pauseButton = findViewById(R.id.play);
        layoutBottom = findViewById(R.id.layout_control);

        seekBarProgress = findViewById(R.id.seekBar_progress);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        playURI = intent.getStringExtra("playURI");
        if (!TextUtils.isEmpty(playURI)) {
            setUri(playURI);
        }
        setTitle(intent);
        super.onNewIntent(intent);
    }

    @Override
    protected void onStop() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        exit();
        unregisterBroadcast();
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (backCount > 0) {
                exit();
            } else {
                backCount++;
                Toast.makeText(this, R.string.player_exit, Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void exit() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        mediaListener = null;
        finish();
    }

    private void updatePausePlay() {
        if (mediaPlayer == null || pauseButton == null) {
            return;
        }
        int resource =
                mediaPlayer.isPlaying() ?
                        R.drawable.button_pause : R.drawable.button_play;
        pauseButton.setBackgroundResource(resource);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int visibility = layoutTop.getVisibility();
            if (visibility != View.VISIBLE) {
                layoutTop.setVisibility(View.VISIBLE);
                layoutBottom.setVisibility(View.VISIBLE);
            } else {
                layoutTop.setVisibility(View.GONE);
                layoutBottom.setVisibility(View.GONE);
            }
        }

        return false;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.v(TAG, "surfaceChanged Called");
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.v(TAG, "surfaceCreated Called");
        mediaPlayer.setDisplay(holder);
        try {
            mediaPlayer.prepare();
        } catch (IllegalStateException e) {
            Log.v(TAG, "IllegalStateException", e);
        } catch (IOException e) {
            Log.v(TAG, "IOException", e);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.v(TAG, "surfaceDestroyed Called");
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.v(TAG, "onPrepared Called");
        int videoWidth = mp.getVideoWidth();
        int videoHeight = mp.getVideoHeight();
        if (videoWidth > currentDisplay.getWidth() || videoHeight > currentDisplay.getHeight()) {
            float heightRatio = (float) videoHeight / (float) currentDisplay.getHeight();
            float widthRatio = (float) videoWidth / (float) currentDisplay.getWidth();
            if (heightRatio > 1 || widthRatio > 1) {
                if (heightRatio > widthRatio) {
                    videoHeight = (int) Math.ceil((float) videoHeight / heightRatio);
                    videoWidth = (int) Math.ceil((float) videoWidth / heightRatio);
                } else {
                    videoHeight = (int) Math.ceil((float) videoHeight / widthRatio);
                    videoWidth = (int) Math.ceil((float) videoWidth / widthRatio);
                }
            }
        }
        surfaceView.getHolder().setFixedSize(videoWidth, videoHeight);
        mp.start();
        if (null != mediaListener) {
            mediaListener.start();
        }

        mHandler.sendEmptyMessage(MEDIA_PLAYER_PREPARED);

        mHandler.sendEmptyMessage(MEDIA_PLAYER_PROGRESS_UPDATE);
        mHandler.sendEmptyMessageDelayed(MEDIA_PLAYER_HIDDEN_CONTROL, 10000);
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int whatInfo, int extra) {
        if (whatInfo == MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING) {
            Log.v(TAG, "Media Info, Media Info Bad Interleaving " + extra);
        } else if (whatInfo == MediaPlayer.MEDIA_INFO_NOT_SEEKABLE) {
            Log.v(TAG, "Media Info, Media Info Not Seekable " + extra);
        } else if (whatInfo == MediaPlayer.MEDIA_INFO_UNKNOWN) {
            Log.v(TAG, "Media Info, Media Info Unknown " + extra);
        } else if (whatInfo == MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING) {
            Log.v(TAG, "MediaInfo, Media Info Video Track Lagging " + extra);
        } else if (whatInfo == MediaPlayer.MEDIA_INFO_METADATA_UPDATE) {
            Log.v(TAG, "MediaInfo, Media Info Metadata Update " + extra);
        }
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.v(TAG, "onCompletion Called");
        if (null != mediaListener) {
            mediaListener.endOfMedia();
        }
        exit();
    }

    @Override
    public boolean onError(MediaPlayer mp, int whatError, int extra) {
        Log.d(TAG, "onError Called" + whatError + "  " + extra);
        if (whatError == MediaPlayer.MEDIA_ERROR_SERVER_DIED) {
            Log.v(TAG, "Media Error, Server Died " + extra);
        } else if (whatError == MediaPlayer.MEDIA_ERROR_UNKNOWN) {
            Log.v(TAG, "Media Error, Error Unknown " + extra);
        }

        return false;
    }

    public void setUri(String uri) {
        try {
            mediaPlayer.reset();
            playURI = uri;
            mediaPlayer.setDataSource(playURI);
        } catch (IllegalArgumentException | IllegalStateException | IOException e) {
            Log.v(TAG, "Catch exception when setUri " + uri, e);
        }
    }

    public void pause() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            if (null != mediaListener) {
                mediaListener.pause();
            }
        }
    }

    public void start() {
        try {
            mediaPlayer.start();
            mHandler.sendEmptyMessageDelayed(MEDIA_PLAYER_PROGRESS_UPDATE, 200);

            if (null != mediaListener) {
                mediaListener.start();
            }
        } catch (Exception e) {
            Log.e(TAG, "start()", e);
        }
    }

    public void stop() {
        try {
            mediaPlayer.stop();
            if (null != mediaListener) {
                mediaListener.stop();
            }
        } catch (Exception e) {
            Log.e(TAG, "stop()", e);
        }

    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            Log.d(TAG, "msg=" + msg.what);
            switch (msg.what) {
                case MEDIA_PLAYER_PROGRESS_UPDATE: {
                    if (null == mediaPlayer || !mediaPlayer.isPlaying()) {
                        break;
                    }

                    int position = mediaPlayer.getCurrentPosition();
                    int duration = mediaPlayer.getDuration();
                    if (null != mediaListener) {
                        mediaListener.positionChanged(position);
                        mediaListener.durationChanged(duration);
                    }

                    textViewLength.setText(Utils.secToTime(duration / 1000));
                    seekBarProgress.setMax(duration);
                    textViewTime.setText(Utils.secToTime(position / 1000));
                    seekBarProgress.setProgress(position);
                    mHandler.sendEmptyMessageDelayed(MEDIA_PLAYER_PROGRESS_UPDATE, 500);
                    break;
                }
                case MEDIA_PLAYER_HIDDEN_CONTROL: {
                    layoutTop.setVisibility(View.GONE);
                    layoutBottom.setVisibility(View.GONE);
                    break;
                }
                default:
                    break;
            }
        }
    };


    public void registerBroadcast() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Action.DMR);
        intentFilter.addAction(Action.VIDEO_PLAY);
        registerReceiver(this.playReceiverBroadcast, intentFilter);
    }

    public void unregisterBroadcast() {
        unregisterReceiver(this.playReceiverBroadcast);
    }

    class PlayBroadcastReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            String helpAction = intent.getStringExtra("helpAction");
            if (helpAction == null) {
                return;
            }
            switch (helpAction) {
                case Action.PLAY:
                    start();
                    updatePausePlay();
                    break;
                case Action.PAUSE:
                    pause();
                    updatePausePlay();
                    break;
                case Action.SEEK:
                    boolean isPaused = false;
                    if (!mediaPlayer.isPlaying()) {
                        isPaused = true;
                    }
                    int position = intent.getIntExtra("position", 0);
                    mediaPlayer.seekTo(position);
                    if (isPaused) {
                        pause();
                    } else {
                        start();
                    }
                    break;
                case Action.SET_VOLUME:
                    int volume =
                            (int) (intent.getDoubleExtra("volume", 0)
                                    * audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
                    break;
                case Action.STOP:
                    stop();
                    break;
            }
        }
    }

    public interface MediaListener {
        void pause();

        void start();

        void stop();

        void endOfMedia();

        void positionChanged(int position);

        void durationChanged(int duration);
    }
}
