//package com.firebig.app;
//
///**
// * Created by FireBig-CH on 16-9-4.
// * 接收Bmob推送:接收Json--->解析json--->保存推送通知--->触发观察者--->更新界面UI
// */
//
//import android.app.Notification;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.BitmapFactory;
//import android.net.Uri;
//import android.util.Log;
//import android.widget.Toast;
//
//import com.firebig.Data.DataUtil;
//import com.firebig.activity.Activity_Home;
//import com.firebig.activity.Activity_Message;
//import com.firebig.activity.R;
//import com.firebig.bean.Bean_Message;
//import com.firebig.util.DataPort;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//import org.json.JSONTokener;
//
//import cn.bmob.push.PushConstants;
//
//public class MyPushMessageReceiver extends BroadcastReceiver {
//
//    private DataPort.onMessageReceiver onMessageReceiver;
//
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        if (intent.getAction().equals(PushConstants.ACTION_MESSAGE)) {
//            Log.d("Tag", "BmobPushDemo收到消息：" + intent.getStringExtra(PushConstants.EXTRA_PUSH_MESSAGE_STRING));
//            Toast.makeText(context, "BmobPushDemo收到消息：" + intent.getStringExtra(PushConstants.EXTRA_PUSH_MESSAGE_STRING), Toast.LENGTH_SHORT).show();
//            // 保存通知
//            saveNotification(context, intent);
//        }
//    }
//
//    /**
//     * 显示通知
//     *
//     * @param context
//     * @param content
//     */
//    private void showNotification(Context context, String content) {
//        // 显示推送通知
//        Notification.Builder builder = new Notification.Builder(context);
//        Intent mIntent = new Intent(context, Activity_Message.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, mIntent, 0);
//        builder.setContentIntent(pendingIntent);
//        builder.setSmallIcon(R.mipmap.header);
//        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.header));
//        builder.setAutoCancel(true);
//        builder.setContentTitle("FireBig通知");
//        builder.setContentText(content);
//        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.notify(2, builder.build());
//    }
//
//    /**
//     * 保存通知内容
//     *
//     * @param intent
//     */
//    private void saveNotification(Context context, Intent intent) {
//        // 解析Json
//        String rootStr = intent.getStringExtra(PushConstants.EXTRA_PUSH_MESSAGE_STRING);
//        JSONTokener tokener = new JSONTokener(rootStr);
//        try {
//            JSONObject message = (JSONObject) tokener.nextValue();
//            Bean_Message bean_message = new Bean_Message();
//            bean_message.setFrom_account(message.getString("account"));
//            bean_message.setFrom_name(message.getString("name"));
//            bean_message.setNesw_id(message.getString("id"));
//            bean_message.setMessage(message.getString("message"));
//            showNotification(context, message.getString("message"));
//            bean_message.setTime(message.getString("time"));
//            bean_message.setMESSAGE_TYPE(message.getInt("type"));
//            DataUtil.DataUtil(context).insertMessageNotice(bean_message);
//            // 通知UI
//            if (onMessageReceiver != null)
//                onMessageReceiver.onMessageReceiver();
//        } catch (JSONException e) {
//
//        }
//
//    }
//
//    /**
//     * 监听接口
//     *
//     * @param onMessageReceiver
//     */
//    public void setOnMessageReceiver(DataPort.onMessageReceiver onMessageReceiver) {
//        this.onMessageReceiver = onMessageReceiver;
//    }
//
//}
