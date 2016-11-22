package com.firebig.util;

import android.os.Environment;

import java.io.File;

/**
 * Created by root on 16-8-24.
 */
public interface CommandUtil {
    // Activity 回调码
    static final int RESULCODE_FINISH = 25;
    static final int RESULCODE_REFRESH = 500;
    static final int RESULCODE_UPDATECACHE = 600;
    // 软件根目录
    static final String PATH = Environment.getDataDirectory().toString() + File.separator + "data" + File.separator +
            "com.firebig.activity" + File.separator + "data" + File.separator;
    // 临时图片路径
    static final String Temp_IM_PATH = Environment.getExternalStorageDirectory() + File.separator + "FireBig" + File.separator + "Photo" + File.separator;
    // 异常日志保存
    static final String LOG_PATH = Environment.getExternalStorageDirectory() + File.separator + "FireBig" + File.separator + "log" + File.separator;
    // 网络状态
    static final int NET_TYPE_MOBILET = 0x11;
    static final int NET_TYPE_WIFI = 0x22;
    static final int NET_TYPE_UNKOWE = 0x33;

    // 设置页面ListView Item 显示模式
    static final int TITLE_MODE = 0;
    static final int LIST_ITEM_MODE_1 = 1;
    static final int LIST_ITEM_MODE_2 = 2;
}
