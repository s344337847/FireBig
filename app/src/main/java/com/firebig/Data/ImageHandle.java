package com.firebig.Data;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.firebig.util.CommandUtil;
import com.firebig.util.DataPort;
import com.firebig.util.StringUtils;

/**
 * Created by FireBig-CH on 16-9-12.
 * 处理图片类：把大图优化成小图和缩略图
 */
public class ImageHandle implements Runnable {

    private String OriginaImagePath;
    private String LargeImagePath;
    private String ThumbnailImagePath;
    private DataPort.handleImage listener;
    private int smallImageWidth = 240;
    private int smallImageHeght = 160;

    private int largeImageWidth = 800;
    private int largeImageHeight = 480;

    public ImageHandle(@NonNull String OriginaImagePath) {
        this.OriginaImagePath = OriginaImagePath;
        LargeImagePath = CommandUtil.Temp_IM_PATH + StringUtils.getUUIDName() + ".png";
        ThumbnailImagePath = CommandUtil.Temp_IM_PATH + StringUtils.getUUIDName() + ".png";
    }

    /**
     * 设置缩略图像素大小，默认为240 x 160
     *
     * @param Width
     * @param Height
     */
    public void setThumbnailImagePixel(@NonNull int Width, @NonNull int Height) {
        this.smallImageHeght = Height;
        this.smallImageWidth = Width;
    }

    /**
     * 设置大图像素大小，默认为800 x 480
     *
     * @param Width
     * @param Height
     */
    public void setLargeImagePixel(@NonNull int Width, @NonNull int Height) {
        this.largeImageHeight = Height;
        this.largeImageWidth = Width;
    }

    @Override
    public void run() {
        /**
         * 缩略图和大图像素写死
         */
        // 生成缩略图
        Bitmap bitmap = FileUtil.getBitmapFromApp(OriginaImagePath, smallImageWidth, smallImageHeght);
        FileUtil.saveImage(bitmap, ThumbnailImagePath);
        // 生成大图
        Bitmap temp_bitmap = FileUtil.getBitmapFromApp(OriginaImagePath, largeImageWidth, largeImageHeight);
        FileUtil.saveImage(temp_bitmap, LargeImagePath);

        if (listener != null)
            listener.done(LargeImagePath, ThumbnailImagePath);
    }

    // 回调接口
    public void setImagePathListener(DataPort.handleImage listener) {
        this.listener = listener;
    }
}
