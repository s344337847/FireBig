package com.firebig.bean;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by FireBig-CH on 16-8-17.
 */
public class Bean_News implements Serializable {
    /**
     * 头像图片名
     */
    private String header_name;
    /**
     * 头像图片下载地址
     */
    private String header_url;
    /**
     * 头像缩略图下载地址
     */
    private String header_thumbnail_url;
    /**
     * 用户账号
     */
    private String username;
    /**
     * 用户名
     */
    private String showname;
    /**
     * 时间
     */
    private String time;
    /**
     * 广播内容
     */
    private String news_content;
    /**
     * 广播图片
     */
    private String news_image_name;
    /**
     * 广播图片下载地址
     */
    private String news_image_url;
    /**
     * 广播图片缩略图下载地址
     */
    private String thumbnail_url;
    /**
     * 广播识别ID
     **/
    private String news_id;
    /**
     * 广播自增ID
     **/
    private int listid;
    /**
     * 四字评论可评论状态
     **/
    private boolean four_word;
    /**
     * 四字评论内容列表
     */
    private List<BombNewsFourWord> showdata = new ArrayList<>();

    /**
     * ITEM 长按切换状态
     */
    private boolean isshowdata = true;
    /**
     * 是否显示删除视图
     */
    private boolean isshowDelete = false;

    public boolean isshowDelete() {
        return isshowDelete;
    }

    public void setIsshowDelete(boolean isshowDelete) {
        this.isshowDelete = isshowDelete;
    }

    public String getHeader_thumbnail_url() {
        return header_thumbnail_url;
    }

    public void setHeader_thumbnail_url(String header_thumbnail_url) {
        this.header_thumbnail_url = header_thumbnail_url;
    }

    public String getThumbnail_url() {
        return thumbnail_url;
    }

    public void setThumbnail_url(String thumbnail_url) {
        this.thumbnail_url = thumbnail_url;
    }

    public String getShowname() {
        return showname;
    }

    public void setShowname(String showname) {
        this.showname = showname;
    }

    public boolean isFour_word() {
        return four_word;
    }

    public void setFour_word(boolean four_word) {
        this.four_word = four_word;
    }

    public int getListid() {
        return listid;
    }

    public void setListid(int listid) {
        this.listid = listid;
    }

    public boolean isshowdata() {
        return isshowdata;
    }

    public void setIsshowdata(boolean isshowdata) {
        this.isshowdata = isshowdata;
    }

    public List<BombNewsFourWord> getShowdata() {
        return showdata;
    }

    public void setShowdata(List<BombNewsFourWord> showdata) {
        this.showdata = showdata;
    }

    public String getNews_content() {
        return news_content;
    }

    public void setNews_content(String news_content) {
        this.news_content = news_content;
    }

    public String getHeader_name() {
        return header_name;
    }

    public void setHeader_name(String header_name) {
        this.header_name = header_name;
    }

    public String getHeader_url() {
        return header_url;
    }

    public void setHeader_url(String header_url) {
        this.header_url = header_url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getNews_image_name() {
        return news_image_name;
    }

    public void setNews_image_name(String news_image_name) {
        this.news_image_name = news_image_name;
    }

    public String getNews_image_url() {
        return news_image_url;
    }

    public void setNews_image_url(String news_image_url) {
        this.news_image_url = news_image_url;
    }

    public String getNews_id() {
        return news_id;
    }

    public void setNews_id(String news_id) {
        this.news_id = news_id;
    }
}
