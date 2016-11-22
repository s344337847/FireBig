//package com.firebig.app;
//
///**
// * Created by root on 16-10-25.
// */
//
//import com.firebig.bean.Log;
//import com.gexin.rp.sdk.base.IPushResult;
//import com.gexin.rp.sdk.base.impl.SingleMessage;
//import com.gexin.rp.sdk.base.impl.Target;
//import com.gexin.rp.sdk.exceptions.RequestException;
//import com.gexin.rp.sdk.http.IGtPush;
//import com.gexin.rp.sdk.template.LinkTemplate;
//
//public class PushtoSingle implements Runnable {
//    //采用"Java SDK 快速入门"， "第二步 获取访问凭证 "中获得的应用配置，用户可以自行替换
//    //定义常量, appId、appKey、masterSecret 采用本文档 "第二步 获取访问凭证 "中获得的应用配置
//    private static String appId = "89rnV95S0r61fUvGPNruB4";
//    private static String appKey = "rcQwvbKR1H9wPOpUp2dJT6";
//    private static String masterSecret = "5mscRfWiLI8eqJeNmOfXu6";
//
//    static String host = "https://api.getui.com/apiex.htm";
//
//    public PushtoSingle() {
//    }
//
//    public void pushMessage() throws Exception {
//        IGtPush push = new IGtPush(host, appKey, masterSecret);
//        LinkTemplate template = linkTemplateDemo();
//        SingleMessage message = new SingleMessage();
//        message.setOffline(true);
//        // 离线有效时间，单位为毫秒，可选
//        message.setOfflineExpireTime(24 * 3600 * 1000);
//        message.setData(template);
//        // 可选，1为wifi，0为不限制网络环境。根据手机处于的网络情况，决定是否下发
//        message.setPushNetWorkType(0);
//
//        Target target = new Target();
//        target.setAppId(appId);
//        target.setClientId("f57ae271b8c2c91e782e823bf2015d6f");
//        IPushResult ret = null;
//        try {
//            ret = push.pushMessageToSingle(message, target);
//            System.out.println("Message Push Successful");
//        } catch (RequestException e) {
//            e.printStackTrace();
//            ret = push.pushMessageToSingle(message, target, e.getRequestId());
//        }
//        if (ret != null) {
//            System.out.println(ret.getResponse().toString());
//        } else {
//            System.out.println("服务器响应异常");
//        }
//    }
//
//    public LinkTemplate linkTemplateDemo() {
//        LinkTemplate template = new LinkTemplate();
//        // 设置APPID与APPKEY
//        template.setAppId(appId);
//        template.setAppkey(appKey);
//        // 设置通知栏标题与内容
//        template.setTitle("FireBig");
//        template.setText("Hello World");
//        // 设置通知是否响铃，震动，或者可清除
//        template.setIsRing(true);
//        template.setIsVibrate(true);
//        template.setIsClearable(true);
//        // 设置打开的网址地址
//        template.setUrl("http://www.baidu.com");
//        return template;
//    }
//
//    @Override
//    public void run() {
//        try {
//            pushMessage();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}
