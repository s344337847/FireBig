package com.firebig.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.firebig.Data.ACache;
import com.firebig.activity.R;
import com.igexin.sdk.PushManager;

import net.youmi.android.AdManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobConfig;
import cn.bmob.v3.exception.BmobException;
import im.fir.sdk.FIR;

/**
 * Created by FireBig-CH on 16-8-16.
 */
public class AppContext extends Application {

    private static volatile AppContext INSTANCE;
    /**
     * APP Acount
     */
    public String UserAccount = null;
    /**
     * Login STATE
     */
    public boolean LOGIN = false;
    /**
     * four word comment show
     **/
    public boolean SHOW_FWCOMMENT = true;
    /**
     * news Long Click particle show
     */
    public boolean Colse_NEWSPARTICLE = false;
    /**
     * sound
     */
    public boolean SHOUND = true;

    /**
     * vibration
     */
    public boolean VIBARTION = true;

    /**
     * SimpleACache
     */
    public ACache aCache;

    /**
     * 网络连接状态
     */
    public boolean isConnection = true;

    /**
     * Singleton pattern
     */
    public static AppContext getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new AppContext();
            return INSTANCE;
        } else {
            return INSTANCE;
        }
    }

    @Override
    public void onCreate() {

        aCache = ACache.get(this);
        // 异常监听
        CrashHandler.getInstance().init(getApplicationContext());
        // 实例化
        INSTANCE = new AppContext();

        ApplicationInfo appInfo = null;
        try {
            appInfo = getApplicationContext().getPackageManager()
                    .getApplicationInfo(getPackageName(),
                            PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String Bmob_APP_KEY = appInfo.metaData.getString("Bmob_APP_KEY");
        // init Bmob Data server
        //Bmob.initialize(getApplicationContext(), "8ffccc4cd857a483d904c9d9ee4a2b87");
        //第二：自v3.4.7版本开始,设置BmobConfig,允许设置请求超时时间、文件分片上传时每片的大小、文件的过期时间(单位为秒)，
        BmobConfig config = new BmobConfig.Builder(getApplicationContext())
                //设置appkey
                .setApplicationId(Bmob_APP_KEY)
                //请求超时时间（单位为秒）：默认15s
                .setConnectTimeout(8)
                //文件分片上传时每片的大小（单位字节），默认512*1024
                .setUploadBlockSize(1024 * 1024)
                //文件的过期时间(单位为秒)：默认1800s
                .setFileExpiration(2500)
                .build();
        Bmob.initialize(config);
        // 开启推送服务(v 1.0)
        // BmobInstallation.getInstallationId(getApplicationContext());
        // BmobPush.startWork(getApplicationContext());
        //使用个推
        PushManager.getInstance().initialize(this.getApplicationContext());
        AdManager.getInstance(this).init("1318ef6ca171f8a6", "730ff37b5592dbd8", false, false);
        // BugHD
        //FIR.init(this.getApplicationContext());
        super.onCreate();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    /**
     * option BmobException and output Error msg
     *
     * @param e BmobException
     * @see BmobException
     */
    public void showBmobException(@NonNull Context context, BmobException e) {
        // no NetWork
        if (e.getErrorCode() == 9016) {
            toast(context, context.getResources().getString(R.string.output_error01));
            return;
        }
        // connection timeout
        if (e.getErrorCode() == 9010) {
            toast(context, context.getResources().getString(R.string.output_error02));
            return;
        }
        // same account error
        if (e.getErrorCode() == 202) {
            toast(context, context.getResources().getString(R.string.output_error03));
            return;
        }
        toast(context, "发生错误！错误代码：" + e.getErrorCode() + "," + e.toString());
    }

    public void toast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    public boolean isNetworkAvailable(Activity activity) {
        Context context = activity.getApplicationContext();
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) {
            return false;
        } else {
            // 获取NetworkInfo对象
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();

            if (networkInfo != null && networkInfo.length > 0) {
                for (int i = 0; i < networkInfo.length; i++) {
                    // System.out.println(i + "===状态===" + networkInfo[i].getState());
                    //System.out.println(i + "===类型===" + networkInfo[i].getTypeName());
                    // 判断当前网络状态是否为连接状态
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    /**
     * 获取当前运行的进程名
     *
     * @return
     */
    public static String getMyProcessName() {
        try {
            File file = new File("/proc/" + android.os.Process.myPid() + "/" + "cmdline");
            BufferedReader mBufferedReader = new BufferedReader(new FileReader(file));
            String processName = mBufferedReader.readLine().trim();
            mBufferedReader.close();
            return processName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
