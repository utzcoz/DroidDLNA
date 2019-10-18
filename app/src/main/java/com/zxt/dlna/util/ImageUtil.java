package com.zxt.dlna.util;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore.Video.Thumbnails;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;

public class ImageUtil {

    /**
     * png后缀
     */
    public static final String PNG_SUFFIX = ".png";

    /**
     * jpg后缀
     */
    public static final String JPG_SUFFIX = ".jpg";

    /**
     * jpeg后缀
     */
    public static final String JPEG_SUFFIX = ".jpeg";

    public static final String VIDEO_THUMBNAIL_PREFIX = "video_thumb_";

    /**
     * 拼装保存的videoThumb的路径
     *
     * @return
     */
    public static String getSaveVideoFilePath(String path, String id) {
        return FileUtil.getSDPath() + FileUtil.VIDEO_THUMB_PATH
                + File.separator + VIDEO_THUMBNAIL_PREFIX + id + PNG_SUFFIX;
    }

    /**
     * 将Bitmap转化成图片存在本地，图片格式由文件路径的后缀名获得，支持png和jpg
     *
     * @param bitmap
     * @param filePath
     * @throws Exception
     */
    public static void saveBitmapWithFilePathSuffix(Bitmap bitmap,
                                                    String filePath) throws Exception {
        if (null == bitmap || TextUtils.isEmpty(filePath)) {
            return;
        }

        String suffix = FileUtil.getFileSuffix(filePath);

        Bitmap.CompressFormat format;

        if (PNG_SUFFIX.equalsIgnoreCase(suffix)) {
            format = Bitmap.CompressFormat.PNG;
        } else if (JPEG_SUFFIX.equalsIgnoreCase(suffix)
                || JPG_SUFFIX.equalsIgnoreCase(suffix)) {
            format = Bitmap.CompressFormat.JPEG;
        } else {
            return;
        }

        File file = new File(filePath);
        FileOutputStream out;
        out = new FileOutputStream(file);
        if (bitmap.compress(format, 70, out)) {
            out.flush();
            out.close();
        }
    }

    @SuppressLint("NewApi")
    public static Bitmap getThumbnailForVideo(String videoAbsPath) {
        Bitmap bitmap = null;

        try {
            bitmap = ThumbnailUtils.createVideoThumbnail(videoAbsPath,
                    Thumbnails.MINI_KIND);
        } catch (Exception e) {
            // It will not get here.
        }
        return bitmap;
    }

    // 用于生成缩略图。

}
