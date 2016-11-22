package com.firebig.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by FireBig-CH on 16-8-25.
 */
public class Log extends BmobObject {
    private BmobFile txt;

    public BmobFile getTxt() {
        return txt;
    }

    public void setTxt(BmobFile txt) {
        this.txt = txt;
    }
}
