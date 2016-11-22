package com.firebig.bean;

import android.content.Context;

/**
 * Created by FireBig-CH on 16-9-19.
 * Bmob自定义Installation表
 */
public class BmobInstallation extends cn.bmob.v3.BmobInstallation {
    /**
     * 用户id-这样可以将设备与用户之间进行绑定
     */
    private String uid;

    public BmobInstallation(Context context) {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
