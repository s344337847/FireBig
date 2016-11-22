package com.firebig.Data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import com.firebig.bean.Bean_News;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

/**
 * Created by FireBig-CH on 16-8-23.
 */
public class FileUtil<T> {


    public String Path = Environment.getDataDirectory().toString() + File.separator + "data" + File.separator +
            "com.firebig.activity" + File.separator + "data" + File.separator;
    /**
     * 缩略图大小
     */
    public static int Thumbnail_h = 100;
    public static int Thumbnail_w = 100;

    /**
     * 保存Bean文件到本地
     *
     * @param list
     */
    public void saveFile(List<T> list) {
        File file = new File(Path + "newsList.dat");
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        try {
            if (file.exists())
                file.delete();
            if (!file.exists())
                file.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(file.toString());
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(list);
            Log.d("ATg", "保存成功！");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从本地加载Bean
     *
     * @return
     */
    public List<T> getFile() {
        List<T> list = null;
        try {
            File file = new File(Path + "newsList.dat");
            FileInputStream fileInputStream = new FileInputStream(file);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            list = (List<T>) objectInputStream.readObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {

        }
        return list;
    }

    /**
     * 删除
     */
    @SuppressWarnings("unchecked")
    public void delete() {
        File file = new File(Path + "newsList.dat");
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * 生成一张缩略图
     *
     * @param Path      图片路径
     * @param reqWidth  缩略图宽
     * @param reqHeight 缩略图高
     * @return
     */
    public static Bitmap getBitmapFromApp(String Path, int reqWidth, int reqHeight) {
        Bitmap bitmap = null;
        File file = new File(Path);
        if (file.exists()) {
            // 把图片压缩成100x100的缩略图
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;//不加载bitmap到内存中
            BitmapFactory.decodeFile(Path, options);
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            final int height = options.outHeight;
            final int width = options.outWidth;
            int inSampleSize = 1;
            if (height > reqHeight || width > reqWidth) {
                final int heightRatio = Math.round((float) height / (float) reqHeight);
                final int widthRatio = Math.round((float) width / (float) reqWidth);
                inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
            }
            options.inSampleSize = inSampleSize;
            options.inJustDecodeBounds = false;//把bitmap重新加载到内存中
            bitmap = BitmapFactory.decodeFile(Path, options);
            if (bitmap == null) {
                return null;
            }
            return bitmap;
        }
        return null;
    }

    /**
     * 生成临时图片,把Bitmap保存为图片
     *
     * @param photo
     * @param spath
     */
    public static void saveImage(Bitmap photo, String spath) {
        try {
            BufferedOutputStream bos = new BufferedOutputStream(
                    new FileOutputStream(spath, false));
            photo.compress(Bitmap.CompressFormat.PNG, 80, bos);
            bos.flush();
            bos.close();
            photo = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 计算图片的缩放值
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    /**
     * 删除生成的图片
     */
    @SuppressWarnings("unchecked")
    public static void deleteTemp(@NonNull String Url) {
        // 删除临时图片
        File file = new File(Url);
        if (file.exists()) {
            Log.d("DeleteTemp----->", "删除图片:" + Url);
            file.delete();
        } else {
            Log.d("DeleteTemp----->", "图片删除失败:" + Url);
        }
    }

}
