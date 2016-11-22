package com.firebig.bean;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by FireBig-CH on 16-8-18.
 */
public class Bmob_MyUser extends BmobUser {

    private String showname;

    private String singtrue;

    private BmobFile avater;

    private String header_thumbnail_url;

    public String getHeader_thumbnail_url() {
        return header_thumbnail_url;
    }

    public void setHeader_thumbnail_url(String header_thumbnail_url) {
        this.header_thumbnail_url = header_thumbnail_url;
    }

    public BmobFile getAvater() {
        return avater;
    }

    public void setAvater(BmobFile avater) {
        this.avater = avater;
    }

    public String getShowname() {
        return showname;
    }

    public void setShowname(String showname) {
        this.showname = showname;
    }

    public String getSingtrue() {
        return singtrue;
    }

    public void setSingtrue(String singtrue) {
        this.singtrue = singtrue;
    }
}
