package com.github.dlna.dmp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.dlna.BaseApplication;
import com.github.dlna.ConfigData;
import com.github.dlna.R;
import com.github.dlna.Settings;
import com.github.dlna.dmc.DMCControl;
import com.github.dlna.dmc.GenerateXml;
import com.github.dlna.util.FileUtil;
import com.github.dlna.util.ImageUtil;
import com.github.dlna.util.ShakeListener;
import com.github.dlna.util.ShakeListener.OnShakeListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.io.File;
import java.util.ArrayList;

public class ImageDisplay extends Activity implements OnClickListener, OnTouchListener {
    private static final String TAG = "ImageDisplay";

    private static final int NONE = 0;

    private static final int DRAG = 1;

    private static final int ZOOM = 2;

    protected static final int MSG_SLIDE_START = 1000;

    private int mode = NONE;

    private PointF start = new PointF();

    private PointF mid = new PointF();

    private SuperImageView superIV;

    private Button slideBtn;

    private LinearLayout mButtonLayout;

    private String mPlayUri = null;

    private String currentContentFormatMimeType = "";

    private boolean isLocalDmr = true;

    private DMCControl dmcControl = null;

    private ArrayList<ContentItem> mListPhotos = new ArrayList<ContentItem>();

    private ProgressBar mSpinner;
    DisplayImageOptions options;

    private int mCurrentPosition;

    private boolean isSlidePlaying = false;
    private volatile Bitmap mCurrentBitmap;
    private Context mContext;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == MSG_SLIDE_START && !nextImage()) {
                int time = Settings.getSlideTime();
                if (time < 5) {
                    time = 5;
                }
                mHandler.sendEmptyMessageDelayed(MSG_SLIDE_START, time * 1000);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_display);

        mContext = this;
        options =
                new DisplayImageOptions
                        .Builder()
                        .showImageForEmptyUri(R.drawable.ic_empty)
                        .showImageOnFail(R.drawable.ic_error).resetViewBeforeLoading()
                        .cacheOnDisc().imageScaleType(ImageScaleType.EXACTLY)
                        .bitmapConfig(Bitmap.Config.RGB_565)
                        .displayer(new FadeInBitmapDisplayer(300))
                        .build();
        initView();
        initData();
        showImage(mPlayUri);

        addShake();
    }

    private void initView() {
        superIV = this.findViewById(R.id.imageView);
        Button previousBtn = this.findViewById(R.id.preButton);
        Button nextBtn = this.findViewById(R.id.nextButton);
        mButtonLayout = this.findViewById(R.id.buttonLayout);
        previousBtn.setOnClickListener(this);
        nextBtn.setOnClickListener(this);
        superIV.setOnTouchListener(this);
        mSpinner = findViewById(R.id.loading);

        Button downloadBtn = this.findViewById(R.id.downloadButton);
        downloadBtn.setOnClickListener(this);
        Button sharedBtn = this.findViewById(R.id.sharedButton);
        sharedBtn.setOnClickListener(this);
        slideBtn = this.findViewById(R.id.slideButton);
        slideBtn.setOnClickListener(this);
        Button rotateBtn = this.findViewById(R.id.rotateButton);
        rotateBtn.setOnClickListener(this);
    }

    private void initData() {
        Intent localIntent = getIntent();
        mPlayUri = localIntent.getStringExtra("playURI");

        mCurrentPosition = ConfigData.photoPosition;
        mListPhotos = ConfigData.listPhotos;

        isLocalDmr = BaseApplication.isLocalDmr;
        if (!isLocalDmr) {
            currentContentFormatMimeType = localIntent.getStringExtra("currentContentFormatMimeType");
            String metaData = localIntent.getStringExtra("metaData");
            dmcControl = new DMCControl(
                    this,
                    1,
                    BaseApplication.dmrDeviceItem,
                    BaseApplication.upnpService,
                    mPlayUri,
                    metaData
            );
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.preButton: {
                prevImage();
                break;
            }
            case R.id.nextButton: {
                nextImage();
                break;
            }
            case R.id.slideButton: {
                if (!isSlidePlaying) {
                    isSlidePlaying = true;
                    slideBtn.setBackgroundResource(R.drawable.ic_slide_pause);
                    mHandler.sendEmptyMessageDelayed(MSG_SLIDE_START, 5000);
                    Toast.makeText(mContext, R.string.info_image_slide_start, Toast.LENGTH_SHORT).show();
                } else {
                    isSlidePlaying = false;
                    slideBtn.setBackgroundResource(R.drawable.ic_slide_start);
                    mHandler.removeMessages(MSG_SLIDE_START);
                    Toast.makeText(mContext, R.string.info_image_slide_pause, Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case R.id.downloadButton: {
                String path = saveCurrentBitmap();
                if (!TextUtils.isEmpty(path)) {
                    Toast.makeText(
                            mContext,
                            mContext.getString(R.string.info_download_image) + path,
                            Toast.LENGTH_SHORT
                    ).show();
                } else {
                    Toast.makeText(mContext, R.string.info_download_image_error, Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case R.id.sharedButton: {
                String path = saveCurrentBitmap();
                if (!TextUtils.isEmpty(path)) {
                    share(Uri.parse(path));
                }
                break;
            }
            default:
                break;
        }
    }

    private boolean nextImage() {
        boolean isLast;
        if (mCurrentPosition >= mListPhotos.size() - 1) {
            isLast = true;
            Toast.makeText(ImageDisplay.this, R.string.info_last_image, Toast.LENGTH_SHORT).show();
        } else {
            isLast = false;
            mCurrentPosition = mCurrentPosition + 1;
            String uri = mListPhotos.get(mCurrentPosition).getItem().getFirstResource().getValue();
            if (!TextUtils.isEmpty(uri)) {
                mPlayUri = uri;
                showImage(mPlayUri);

                if (!isLocalDmr) {
                    dmcControl.stop(true);
                    try {
                        dmcControl.setCurrentPlayPath(
                                mPlayUri,
                                new GenerateXml().generate(mListPhotos.get(mCurrentPosition))
                        );
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    dmcControl.getProtocolInfos(currentContentFormatMimeType);
                }
            }
        }
        return isLast;
    }

    private void prevImage() {
        if (mCurrentPosition == 0) {
            Toast.makeText(ImageDisplay.this, R.string.info_first_image, Toast.LENGTH_SHORT).show();
        } else {
            mCurrentPosition = mCurrentPosition - 1;
            String uri = mListPhotos.get(mCurrentPosition).getItem().getFirstResource().getValue();
            if (!TextUtils.isEmpty(uri)) {
                mPlayUri = uri;
                showImage(mPlayUri);
                if (!isLocalDmr) {
                    dmcControl.stop(true);
                    try {
                        dmcControl.setCurrentPlayPath(
                                mPlayUri,
                                new GenerateXml().generate(mListPhotos.get(mCurrentPosition))
                        );
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    dmcControl.getProtocolInfos(currentContentFormatMimeType);
                }
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            ImageDisplay.this.finish();
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!isLocalDmr) {
            dmcControl.stop(true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!isLocalDmr) {
            dmcControl.stop(true);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                if (mButtonLayout.getVisibility() == View.VISIBLE) {
                    mButtonLayout.setVisibility(View.GONE);
                } else {
                    mButtonLayout.setVisibility(View.VISIBLE);
                }
                start.set(event.getX(), event.getY());
                mode = DRAG;
                break;
            case MotionEvent.ACTION_UP:
                if (mode == DRAG) {
                    if (event.getX() - start.x > 100) {
                        // go to prev pic
                        prevImage();
                    } else if (event.getX() - start.x < -100) {
                        // go to next pic
                        nextImage();
                    }
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                float oldDist = spacing(event);
                if (oldDist > 10f) {
                    midPoint(mid, event);
                    mode = ZOOM;
                }
                break;
            default:
                break;
        }

        return false;
    }

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    private void showImage(String url) {
        fetchBitmap2(url);
        if (!isLocalDmr) {
            try {
                dmcControl.setCurrentPlayPath(
                        mPlayUri,
                        new GenerateXml().generate(mListPhotos.get(mCurrentPosition))
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
            dmcControl.getProtocolInfos(currentContentFormatMimeType);
        }
    }

    private void fetchBitmap2(String url) {
        SimpleImageLoadingListener imageLoadingListener = new SimpleImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                mSpinner.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri,
                                        View view,
                                        FailReason failReason) {
                int message = R.string.network_denied;
                switch (failReason.getType()) {
                    case IO_ERROR:
                        message = R.string.io_error;
                        break;
                    case DECODING_ERROR:
                        message = R.string.decoding_error;
                        break;
                    case OUT_OF_MEMORY:
                        message = R.string.oom_error;
                        break;
                    case UNKNOWN:
                        message = R.string.unknown_error;
                        break;
                }
                Toast.makeText(
                        ImageDisplay.this,
                        message,
                        Toast.LENGTH_SHORT
                ).show();
                mSpinner.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingComplete(String imageUri,
                                          View view,
                                          Bitmap loadedImage) {
                mSpinner.setVisibility(View.GONE);
                mCurrentBitmap = loadedImage;
            }
        };
        ImageLoader.getInstance().displayImage(url, superIV, options, imageLoadingListener);
    }

    private String saveCurrentBitmap() {
        String path = "";
        if (null != mCurrentBitmap && !mCurrentBitmap.isRecycled()) {
            String sdPath = FileUtil.getSDPath();
            String filename = mPlayUri.substring(mPlayUri.lastIndexOf("/"));
            if (FileUtil.getFileSuffix(filename).equals("")) {
                filename = filename + ".jpg";
            }

            path = sdPath + FileUtil.IMAGE_DOWNLOAD_PATH;
            File path1 = new File(path);
            if (!path1.exists()) {
                path1.mkdirs();
            }
            path = path + filename;
            try {
                ImageUtil.saveBitmapWithFilePathSuffix(mCurrentBitmap, path);
            } catch (Exception e) {
                path = "";
                Log.w(TAG, "saveCurrentBitmap", e);
            }
        }
        return path;
    }

    private void share(Uri uri) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        if (uri != null) {
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            shareIntent.setType("image/*");
            startActivity(Intent.createChooser(shareIntent, getText(R.string.info_share_image)));
        }
    }

    private void addShake() {
        ShakeListener shakeListener = new ShakeListener(this);
        shakeListener.setOnShakeListener(new OnShakeListener() {
            @Override
            public void onShake() {
                nextImage();
            }
        });
    }
}
