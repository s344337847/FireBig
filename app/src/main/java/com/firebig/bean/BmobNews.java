package com.firebig.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by FireBig-CH on 16-8-20.
 */
public class BmobNews extends BmobObject {

    private String account;
    private String newscontent;
    private BmobFile newsimage;
    private Integer listid;
    private boolean four_word_state;
    private String thumbnail_url;

    public boolean isFour_word_state() {
        return four_word_state;
    }


    public void setFour_word_state(boolean four_word_state) {
        this.four_word_state = four_word_state;
    }

    public String getThumbnail_url() {
        return thumbnail_url;
    }

    public void setThumbnail_url(String thumbnail_url) {
        this.thumbnail_url = thumbnail_url;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getNewscontent() {
        return newscontent;
    }

    public void setNewscontent(String newscontent) {
        this.newscontent = newscontent;
    }

    public BmobFile getNewsimage() {
        return newsimage;
    }

    public void setNewsimage(BmobFile newsimage) {
        this.newsimage = newsimage;
    }

    public Integer getListid() {
        return listid;
    }

    public void setListid(Integer listid) {
        this.listid = listid;
    }
}

