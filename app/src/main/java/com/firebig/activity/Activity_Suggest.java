package com.firebig.activity;

import android.animation.StateListAnimator;
import android.app.ProgressDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebig.app.AppContext;
import com.firebig.bean.suggestion;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by FireBig-CH on 16-8-19.
 */
public class Activity_Suggest extends BaseActivity implements View.OnFocusChangeListener, TextWatcher {
    private TextView tv1, tv2;
    private EditText ed1, ed2;
    private Button btn;
    private ProgressDialog pgd;
    /**
     * 按钮
     */
    private ImageView sendBtn;

    @Override
    public void initUI() {
        tv1 = (TextView) findViewById(R.id.suggest_tv1);
        tv2 = (TextView) findViewById(R.id.suggest_tv2);
        ed1 = (EditText) findViewById(R.id.suggest_input1);
        ed1.addTextChangedListener(this);
        ed1.setOnFocusChangeListener(this);
        ed2 = (EditText) findViewById(R.id.suggest_input2);
        ed2.setOnFocusChangeListener(this);
        btn = (Button) findViewById(R.id.suggest_btn);
        btn.setOnClickListener(this);
        btn.setVisibility(View.GONE);
        pgd = new ProgressDialog(this);
        pgd.setMessage(getResources().getString(R.string.suggest_upload_loading));
    }

    @Override
    public void loadData() {

    }

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_suggest);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.suggest_title));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getLayoutInflater().inflate(R.layout.toolbar_sendnews, toolbar);
        sendBtn = (ImageView) findViewById(R.id.upload_news_toolbar_send);
        sendBtn.setOnClickListener(this);
        sendBtn.setVisibility(View.GONE);

    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.suggest_btn)
            uploadSuggest();
        if (i == R.id.upload_news_toolbar_send)
            uploadSuggest();
    }

    private void uploadSuggest() {
        if (ed1.getText().toString().isEmpty()) {
            toast(getResources().getString(R.string.suggest_wring));
            return;
        }

        pgd.show();
        suggestion suggestion = new suggestion();
        suggestion.setName(AppContext.getINSTANCE().UserAccount);
        suggestion.setSugg_text(ed1.getText().toString());
        suggestion.setContact(ed2.getText().toString());
        suggestion.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    ed1.setText("");
                    ed2.setText("");
                    toast(getResources().getString(R.string.suggest_thank));
                } else {
                    AppContext.getINSTANCE().showBmobException(Activity_Suggest.this, e);
                }
                pgd.dismiss();
            }
        });
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        int id = view.getId();
        if (id == R.id.suggest_input1) {
            if (!ed1.getText().toString().isEmpty())
                return;
            if (b) {
                tv1.setVisibility(View.VISIBLE);
                tv1.startAnimation(AnimationUtils.loadAnimation(this, R.anim.textview_show));
                ed1.setHint("");
            } else {
                Animation animation = AnimationUtils.loadAnimation(this, R.anim.textview_hide);
                tv1.startAnimation(animation);
                tv1.setVisibility(View.INVISIBLE);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        ed1.setHint(getResources().getString(R.string.suggest_input_title1));
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }
        } else {
            if (!ed2.getText().toString().isEmpty())
                return;
            if (b) {
                tv2.setVisibility(View.VISIBLE);
                tv2.startAnimation(AnimationUtils.loadAnimation(this, R.anim.textview_show));
                ed2.setHint("");
            } else {
                Animation animation = AnimationUtils.loadAnimation(this, R.anim.textview_hide);
                tv2.startAnimation(animation);
                tv2.setVisibility(View.INVISIBLE);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        ed2.setHint(getResources().getString(R.string.suggest_input_title2));
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.length() > 0) {
            sendBtn.setVisibility(View.VISIBLE);
        } else {
            sendBtn.setVisibility(View.GONE);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
