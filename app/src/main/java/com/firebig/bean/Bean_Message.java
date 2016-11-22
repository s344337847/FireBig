package com.firebig.bean;

import java.io.Serializable;

/**
 * Created by FireBig-CH on 16-9-19.
 * 消息通知Bean
 */
public class Bean_Message implements Serializable {
    /**
     * 发送方账号
     */
    private String from_account;
    /**
     * 发送方用户名
     */
    private String from_name;
    /**
     * 广播识别ID
     */
    private String nesw_id;
    /**
     * 通知内容
     */
    private String message;
    /**
     * 推送时间
     */
    private String time;
    /**
     * 消息类型 0:广播回复　1:回复广播评论 3:其他
     */
    private int MESSAGE_TYPE = 0;

    public int getMESSAGE_TYPE() {
        return MESSAGE_TYPE;
    }

    public void setMESSAGE_TYPE(int MESSAGE_TYPE) {
        this.MESSAGE_TYPE = MESSAGE_TYPE;
    }

    public String getFrom_account() {
        return from_account;
    }

    public void setFrom_account(String from_account) {
        this.from_account = from_account;
    }

    public String getFrom_name() {
        return from_name;
    }

    public void setFrom_name(String from_name) {
        this.from_name = from_name;
    }

    public String getNesw_id() {
        return nesw_id;
    }

    public void setNesw_id(String nesw_id) {
        this.nesw_id = nesw_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
