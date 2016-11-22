package com.firebig.bean;

import java.io.Serializable;

/**
 * Created by FireBig-CH on 16-9-7.
 */
public class Bean_NewsItem_Comment implements Serializable {
    private String header_url;
    private String username;
    private String time;
    private String contents;

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

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }
}
