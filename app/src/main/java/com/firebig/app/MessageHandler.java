//package com.firebig.app;
//
//import android.app.Notification;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.BitmapFactory;
//import android.util.Log;
//
//import com.firebig.Data.DataUtil;
//import com.firebig.activity.Activity_Message;
//import com.firebig.activity.R;
//import com.firebig.bean.Bean_Message;
//import com.firebig.util.DataPort;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//import org.json.JSONTokener;
//
//import java.util.List;
//import java.util.Map;
//
//import cn.bmob.newim.BmobIM;
//import cn.bmob.newim.event.MessageEvent;
//import cn.bmob.newim.event.OfflineMessageEvent;
//import cn.bmob.newim.listener.BmobIMMessageHandler;
//
///**
// * Created by FireBig-CH on 16-9-22.
// * 处理消息通知类
// */
//public class MessageHandler extends BmobIMMessageHandler {
//
//    private Context context;
//
//    public MessageHandler(Context context) {
//        this.context = context;
//    }
//
//    @Override
//    public void onMessageReceive(MessageEvent messageEvent) {
//        Log.d("MessageHandler", "FromAccount:" + messageEvent.getFromUserInfo().getName() + ",UserAccount:" + AppContext.getINSTANCE().UserAccount);
//        // 保存通知
//        saveNotification(context, messageEvent.getMessage().getContent());
//        for (int i = 0; i < BmobIM.getInstance().loadAllConversation().size(); i++) {
//            BmobIM.getInstance().loadAllConversation().get(i).updateLocalCache();
//        }
//    }
//
//    @Override
//    public void onOfflineReceive(OfflineMessageEvent event) {
//        super.onOfflineReceive(event);
//        //每次调用connect方法时会查询一次离线消息，如果有，此方法会被调用
//        Map<String, List<MessageEvent>> map = event.getEventMap();
//        for (Map.Entry<String, List<MessageEvent>> entry : map.entrySet()) {
//            List<MessageEvent> list = entry.getValue();
//            Log.d("TAG", "Content------>" + list.get(0).getMessage().getContent());
//            showNotification(context, list.get(0).getMessage().getContent());
//        }
//    }
//
//    /**
//     * 保存通知内容
//     *
//     * @param content
//     */
//    private void saveNotification(Context context, String content) {
//        // 解析Json
//        JSONTokener tokener = new JSONTokener(content);
//        try {
//            JSONObject message = (JSONObject) tokener.nextValue();
//            Bean_Message bean_message = new Bean_Message();
//            bean_message.setFrom_account(message.getString("account"));
//            bean_message.setFrom_name(message.getString("name"));
//            bean_message.setNesw_id(message.getString("id"));
//            bean_message.setMessage(message.getString("message"));
//            bean_message.setTime(message.getString("time"));
//            bean_message.setMESSAGE_TYPE(message.getInt("type"));
//            DataUtil.DataUtil(context).insertMessageNotice(bean_message);
//            showNotification(context, bean_message.getFrom_name() + "回复了您:" + bean_message.getMessage());
//        } catch (JSONException e) {
//
//        }
//
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
//        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.header));
//        builder.setAutoCancel(true);
//        builder.setContentTitle("FireBig通知");
//        builder.setContentText(content);
//        // 声音
//        if (AppContext.getINSTANCE().SHOUND)
//            builder.setDefaults(Notification.DEFAULT_SOUND);
//        // 震动
//        if (AppContext.getINSTANCE().VIBARTION)
//            builder.setVibrate(new long[]{100, 200, 300});
//        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.notify(2, builder.build());
//    }
//}
