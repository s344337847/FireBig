package com.firebig.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.firebig.Data.DataUtil;
import com.firebig.app.AppContext;
import com.firebig.bean.Bmob_MyUser;
import com.firebig.util.CommandUtil;
import com.firebig.util.Md5Util;
import com.firebig.util.StringUtils;
import com.firebig.widget.FBEditText;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * Register Activity
 * Created by FireBig-CH on 16-8-16.
 */
public class Activity_Register extends BaseActivity {
    private FBEditText username;
    private FBEditText pass;
    private FBEditText pass2;
    private Button register_btn;
    private TextView agreement;
    private ProgressDialog pgd;

    @Override
    public void initUI() {
        username = (FBEditText) findViewById(R.id.register_username);
        username.init(getResources().getString(R.string.account_ts));
        pass = (FBEditText) findViewById(R.id.register_pass);
        pass.init(getResources().getString(R.string.password_hint_txt));
        pass.setTextType(true);
        pass2 = (FBEditText) findViewById(R.id.register_conpass);
        pass2.init(getResources().getString(R.string.password_confirm));
        pass2.setTextType(true);
        register_btn = (Button) findViewById(R.id.register_btn1);
        register_btn.setOnClickListener(this);
        agreement = (TextView) findViewById(R.id.register_agreement);
        agreement.setOnClickListener(this);
    }

    @Override
    public void loadData() {

    }

    @Override
    public void setContentView() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_register);
        ActionBar bar = getSupportActionBar();
        bar.setTitle(R.string.btn_register);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.register_btn1) {
            registerAction();
        } else if (view.getId() == R.id.register_agreement) {
            // Go To FireBigAgreement WEB
            Bundle bundle = new Bundle();
            bundle.putInt("position", 1);
            bundle.putString("title", "FireBig协议");
            goToActivity(Activity_Web.class, bundle, false);
        }
    }

    private void registerAction() {
        final String user = username.getText();
        final String passs = pass.getText();
        String comfirm_pass = pass2.getText();
        if (user.length() < 6) {
            toast("账号最少6位！密码也是哦！");
            return;
        }
        if (pass.getText().length() < 6) {
            toast("密码最少6位！");
            return;
        }

        if (!passs.equals(comfirm_pass)) {
            toast(getResources().getString(R.string.register_toast01));
            return;
        }

        if (user == null || passs == null) {
            toast(getResources().getString(R.string.register_toast02));
            return;
        }

        if (!StringUtils.StringFilter(user).equals(user)) {
            toast(getResources().getString(R.string.register_toast03));
            return;
        }

        if(!StringUtils.isCharAt(user)){
            toast(getResources().getString(R.string.register_toast04));
            return;
        }
        // colose Input
        View view = getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        pgd = ProgressDialog.show(this, "", getResources().getString(R.string.logining));
        Bmob_MyUser bmobUser = new Bmob_MyUser();
        bmobUser.setUsername(user);
        bmobUser.setPassword(Md5Util.encryptPass(passs, 5));
        bmobUser.setShowname(user);
        bmobUser.setSingtrue("We are FireBig!!");
        bmobUser.signUp(new SaveListener<Bmob_MyUser>() {

            @Override
            public void done(Bmob_MyUser bmobUser, BmobException e) {
                if (e != null) {
                    AppContext.getINSTANCE().showBmobException(Activity_Register.this, e);
                    pgd.dismiss();
                } else {
                    login(user, Md5Util.encryptPass(passs, 5));
                }
            }
        });
    }

    /**
     * 注册完成后进行登录操作
     *
     * @param user
     * @param pass
     */
    private void login(String user, String pass) {
        BmobUser user1 = new BmobUser();
        user1.setUsername(user);
        user1.setPassword(pass);
        final String md5pass = pass;
        user1.login(new SaveListener<BmobUser>() {
            @Override
            public void done(BmobUser bmobUser, BmobException e) {
                if (e == null) {
                    // sava SharedPreferencesCache
                    saveSharedPreferencesCache(getSharedPreferences("account", 0), "user_account", bmobUser.getUsername());
                    // save sqlite
                    DataUtil.DataUtil(Activity_Register.this).insertAccount(bmobUser.getUsername(), md5pass, bmobUser.getObjectId());
                    // colose Activity_Wellcome Screen
                    Activity_Register.this.setResult(CommandUtil.RESULCODE_FINISH);
                    //
                    pgd.dismiss();
                    goToActivity(Activity_Home.class, null, true);
                } else {
                    pgd.dismiss();
                    AppContext.getINSTANCE().showBmobException(Activity_Register.this, e);
                    toast(getResources().getString(R.string.login_toast02));
                }
            }
        });
    }
}
