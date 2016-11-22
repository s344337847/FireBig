package com.firebig.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebig.Data.DataHepler;
import com.firebig.Data.DataUtil;
import com.firebig.app.AppContext;
import com.firebig.bean.Bmob_MyUser;
import com.firebig.util.CommandUtil;
import com.firebig.util.KeyBoardUtils;
import com.firebig.util.Md5Util;
import com.firebig.widget.FBEditText;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

import static android.R.attr.password;

/**
 * Created by FireBig on 16-8-16.
 * 登录
 */
public class Activity_Login extends BaseActivity {
    public final static String TAG = "Activity_Login";
    private Button login;
    private FBEditText username;
    private FBEditText pass;
    private ProgressDialog pgd;
    private TextView tv4;

    @Override
    public void initUI() {
        login = (Button) findViewById(R.id.login_btn1);
        username = (FBEditText) findViewById(R.id.login_username);
        username.init(getResources().getString(R.string.login_hint_txt));
        pass = (FBEditText) findViewById(R.id.login_pass);
        pass.init(getResources().getString(R.string.password_hint_txt));
        pass.setTextType(true);
        tv4 = (TextView) findViewById(R.id.login_tv4);
        tv4.setOnClickListener(this);
        login.setOnClickListener(this);
    }

    @Override
    public void loadData() {

    }

    @Override
    public void setContentView() {
        // 设置软键盘不自动弹出
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_login);
        ActionBar bar = getSupportActionBar();
        bar.setTitle(R.string.btn_login);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_btn1:
                optionLogin();
                break;
            case R.id.login_tv4:// 注册
                goToActivityForResult(Activity_Register.class, null);
                break;
        }
    }

    /**
     * 登录操作
     */
    public void optionLogin() {
        if (username.getText().isEmpty() || pass.getText().isEmpty()) {
            toast(getResources().getString(R.string.login_toast01));
            return;
        }
        // 关闭软键盘
        View view = getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        // show ProgressDialog
        pgd = ProgressDialog.show(this, "", getResources().getString(R.string.logining));
        final String passs = pass.getText();
        String user = username.getText();
        final String md5pass = Md5Util.encryptPass(passs, 5);
        Bmob_MyUser user1 = new Bmob_MyUser();
        user1.setUsername(user);
        user1.setPassword(md5pass);
        user1.login(new SaveListener<Bmob_MyUser>() {
            @Override
            public void done(Bmob_MyUser bmobUser, BmobException e) {
                if (e == null) {
                    // sava SharedPreferencesCache
                    saveSharedPreferencesCache(getSharedPreferences("account", 0), "user_account", bmobUser.getUsername());
                    // save to sqlite
                    DataUtil.DataUtil(Activity_Login.this).insertAccount(bmobUser.getUsername(), md5pass, bmobUser.getObjectId());
                    // toast("Login Scuessful!");
                    goToActivity(Activity_Home.class, null, true);
                } else {
                    toast(getResources().getString(R.string.login_toast02));
                }
                pgd.dismiss();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == CommandUtil.RESULCODE_FINISH) {
            // colose this Activity
            finish();
        }
    }

}
