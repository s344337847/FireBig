package com.firebig.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by FireBig-CH on 16-9-8.
 */
public class BmobNewsComment extends BmobObject {
    /**
     * 用户账号
     */
    private String account;
    /**
     * 用户名
     */
    private String username;
    /**
     * 头像下载地址
     */
    private String avaterUrl;
    /**
     * 头像搜略图下载地址
     */
    private String avater_thumbnailUrl;
    /**
     * 评论内容
     */
    private String content;
    /**
     * 对谁评论
     */
    private String toaccount;
    /**
     * 广播识别ID
     */
    private String id;
    /**
     * 时间
     */
    private String time;

    public String getAvater_thumbnailUrl() {
        return avater_thumbnailUrl;
    }

    public void setAvater_thumbnailUrl(String avater_thumbnailUrl) {
        this.avater_thumbnailUrl = avater_thumbnailUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvaterUrl() {
        return avaterUrl;
    }

    public void setAvaterUrl(String avaterUrl) {
        this.avaterUrl = avaterUrl;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getToaccount() {
        return toaccount;
    }

    public void setToaccount(String toaccount) {
        this.toaccount = toaccount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
