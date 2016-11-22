package com.firebig.app;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.firebig.util.CommandUtil;
import com.firebig.util.DataPort;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by FireBig-CH on 16-8-25.
 * <p/>
 * 监听网络状态
 */
public class NetWorkListener extends BroadcastReceiver {

    private DataPort.onNetChange onNetChange;

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifiNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (onNetChange == null || mobNetInfo == null || wifiNetInfo == null)
            return;
        if (!mobNetInfo.isConnected() && !wifiNetInfo.isConnected()) {
            // 网络不可用
            onNetChange.onNetChange(CommandUtil.NET_TYPE_UNKOWE);
        } else if (mobNetInfo.isConnected()) {
            // 手机网络
            onNetChange.onNetChange(CommandUtil.NET_TYPE_MOBILET);
        } else if (wifiNetInfo.isConnected()) {
            // WIFI网络
            onNetChange.onNetChange(CommandUtil.NET_TYPE_WIFI);
        }
    }

    public void setOnNetChangeListener(DataPort.onNetChange onNetChange) {
        this.onNetChange = onNetChange;
    }

}
