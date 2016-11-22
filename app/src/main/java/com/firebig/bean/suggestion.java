package com.firebig.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by root on 16-8-19.
 */
public class suggestion extends BmobObject{

    private String sugg_text;
    private String name;
    private String contact;

    public String getSugg_text() {
        return sugg_text;
    }

    public void setSugg_text(String sugg_text) {
        this.sugg_text = sugg_text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }
}
