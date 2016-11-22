package com.firebig.bean;

/**
 * Created by FireBig-CH on 16-10-17.
 */
public class Bean_Setting_item {

    private String title = null;
    private String item_depict = null;
    private boolean isHide_depict = true;
    private boolean sw = true;
    private int Setting_Show_Mode = 0;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getItem_depict() {
        return item_depict;
    }

    public void setItem_depict(String item_depict) {
        this.item_depict = item_depict;
    }

    public int getSetting_Show_Mode() {
        return Setting_Show_Mode;
    }

    public void setSetting_Show_Mode(int setting_Show_Mode) {
        Setting_Show_Mode = setting_Show_Mode;
    }

    public boolean isHide_depict() {
        return isHide_depict;
    }

    public void setHide_depict(boolean hide_depict) {
        isHide_depict = hide_depict;
    }

    public boolean isSw() {
        return sw;
    }

    public void setSw(boolean sw) {
        this.sw = sw;
    }
}
