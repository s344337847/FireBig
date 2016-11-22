package com.firebig.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.igexin.sdk.PushConsts;

/**
 * Created by root on 16-10-25.
 */
public class GetuiBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();

        switch (bundle.getInt(PushConsts.CMD_ACTION)) {
            case PushConsts.GET_CLIENTID:

                String cid = bundle.getString("clientid");
                // TODO:处理cid返回
                Log.d("TAG", cid);
                break;
            case PushConsts.GET_MSG_DATA:

                String taskid = bundle.getString("taskid");
                String messageid = bundle.getString("messageid");
                byte[] payload = bundle.getByteArray("payload");
                if (payload != null) {
                    String data = new String(payload);
                    // TODO:接收处理透传（payload）数据
                }
                break;
            default:
                break;
        }
    }
}
