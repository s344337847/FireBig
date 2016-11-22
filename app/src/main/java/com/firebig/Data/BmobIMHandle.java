//package com.firebig.Data;
//
//import android.content.Context;
//import android.support.annotation.NonNull;
//import android.util.Log;
//
//import com.firebig.app.AppContext;
//import com.firebig.app.MessageHandler;
//import com.firebig.bean.Bean_Message;
//import com.firebig.bean.Bmob_MyUser;
//import com.firebig.util.DataPort;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.List;
//import java.util.Objects;
//import java.util.logging.Logger;
//
//import cn.bmob.newim.BmobIM;
//import cn.bmob.newim.bean.BmobIMConversation;
//import cn.bmob.newim.bean.BmobIMMessage;
//import cn.bmob.newim.bean.BmobIMTextMessage;
//import cn.bmob.newim.bean.BmobIMUserInfo;
//import cn.bmob.newim.core.BmobIMClient;
//import cn.bmob.newim.listener.ConnectListener;
//import cn.bmob.newim.listener.ConversationListener;
//import cn.bmob.newim.listener.MessageSendListener;
//import cn.bmob.v3.BmobQuery;
//import cn.bmob.v3.BmobUser;
//import cn.bmob.v3.exception.BmobException;
//import cn.bmob.v3.listener.FindListener;
//
///**
// * Created by FireBig-CH on 16-9-22.
// * FireBig消息推送类，先连接Bmob服务器后进行操作,不要忘了设置Context
// */
//public class BmobIMHandle {
//    /**
//     * 接口
//     */
//    private DataPort.sendText listener;
//
//    private static volatile BmobIMHandle instance;
//    private static Object object = new Object();
//
//    /**
//     * 单例
//     *
//     * @return
//     */
//    public static BmobIMHandle getInstance() {
//        if (instance == null) {
//            synchronized (object) {
//                if (instance == null) {
//                    instance = new BmobIMHandle();
//                }
//            }
//        }
//        return instance;
//    }
//
//
//    /**
//     * 与BmobIM服务器连接
//     */
//    public void init(@NonNull Context context) {
//        //开启IM服务,注册消息接收器
//        Bmob_MyUser myUser = BmobUser.getCurrentUser(Bmob_MyUser.class);
//        BmobIM.init(context);
//        BmobIM.registerDefaultMessageHandler(new MessageHandler(context));
//        BmobIM.connect(myUser.getObjectId(), new ConnectListener() {
//            @Override
//            public void done(String uid, BmobException e) {
//                if (e == null) {
//                    // TODO 服务器连接成功
//                    Log.d("BmobIM::", "IsConnect,IM服务器连接成功");
//                } else {
//                    Log.d("BmobIM::", "IM服务器连接SB" + e.toString() + ",ErrorCode:" + e.getErrorCode());
//                }
//            }
//        });
//    }
//
//    /**
//     * 断开连接
//     */
//    public void disConnect() {
//        BmobIM.getInstance().disConnect();
//    }
//
//    public void chatUpdateLocalCache() {
//
//    }
//
//    /**
//     * 发送消息口*对外
//     * 发送消息流程:--->获取发送者的Uid,name --->创建会话 --->发送消息
//     */
//    public void sendMessage(Bean_Message ben) {
//        JSONObject object = new JSONObject();
//        try {
//            object.put("account", ben.getFrom_account());
//            object.put("name", ben.getFrom_name());
//            object.put("id", ben.getNesw_id());
//            object.put("message", ben.getMessage());
//            object.put("time", ben.getTime());
//            object.put("type", ben.getMESSAGE_TYPE());
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        getSendToData(object.toString(), ben.getFrom_account());
//    }
//
//    /**
//     * 获取要发送消息对象的资料
//     */
//    private void getSendToData(final String content, String sendToName) {
//        BmobQuery<Bmob_MyUser> query = new BmobQuery<>();
//        query.addWhereEqualTo("username", sendToName);
//        query.findObjects(new FindListener<Bmob_MyUser>() {
//            @Override
//            public void done(List<Bmob_MyUser> list, BmobException e) {
//                if (e == null) {
//                    createConversation(content, list.get(0));
//                } else {
//                    if (listener == null)
//                        listener.error();
//                }
//            }
//        });
//    }
//
//    /**
//     * 创建会话
//     */
//    private void createConversation(final String content, Bmob_MyUser sendToUser) {
//        BmobIMUserInfo info = new BmobIMUserInfo(sendToUser.getObjectId(), sendToUser.getUsername(), null);
//        BmobIM.getInstance().startPrivateConversation(info, new ConversationListener() {
//            @Override
//            public void done(BmobIMConversation bmobIMConversation, BmobException e) {
//                if (e == null) {
//                    send(content, bmobIMConversation);
//                } else {
//                    if (listener == null)
//                        listener.error();
//                }
//            }
//        });
//    }
//
//    /**
//     * 开始发送消息
//     */
//    private void send(String content, BmobIMConversation conversation) {
//        BmobIMConversation imConversation = BmobIMConversation.obtain(BmobIMClient.getInstance(), conversation);
//        BmobIMTextMessage message = new BmobIMTextMessage();
//        message.setContent(content);
//        imConversation.sendMessage(message, new MessageSendListener() {
//            @Override
//            public void done(BmobIMMessage bmobIMMessage, BmobException e) {
//                if (e == null) {
//                    // TODO 发送成功
//                    if (listener == null)
//                        listener.done();
//                } else {
//                    if (listener == null)
//                        listener.error();
//                }
//            }
//        });
//    }
//
//    /**
//     * 推送消息回调
//     *
//     * @param listener
//     */
//    public void setSendTextListener(DataPort.sendText listener) {
//        this.listener = listener;
//    }
//}
//
