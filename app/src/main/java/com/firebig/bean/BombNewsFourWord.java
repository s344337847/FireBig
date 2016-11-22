package com.firebig.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by Firebig-CH on 16-8-20.
 */
public class BombNewsFourWord extends BmobObject {
    String id;
    String content;
    String account;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }
}
