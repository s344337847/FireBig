package com.firebig.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebig.activity.R;

/**
 * Created by SeaOrange on 16-11-1.
 */

public class FBEditText extends FrameLayout implements View.OnFocusChangeListener, TextWatcher, View.OnClickListener {

    private TextView title;
    private EditText input;
    private ImageView delete_btn;
    private Animation up;
    private Animation down;

    public FBEditText(Context context) {
        super(context);
    }

    public FBEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FBEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * init
     */
    public void init(@NonNull String hint) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fbedittext, null);
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(view, layoutParams);
        // load UI
        title = (TextView) view.findViewById(R.id.fbedittext_textview);
        input = (EditText) view.findViewById(R.id.fbedittext_input);
        input.setOnFocusChangeListener(this);
        delete_btn = (ImageView) view.findViewById(R.id.fbedittext_delete);
        delete_btn.setOnClickListener(this);
        up = AnimationUtils.loadAnimation(getContext(), R.anim.fbedittext_hint_up);
        down = AnimationUtils.loadAnimation(getContext(), R.anim.fbedittext_hint_down);
        input.addTextChangedListener(this);
        title.setText(hint);
    }

    public void setHintString(@NonNull String hint) {
        title.setText(hint);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!input.getText().toString().isEmpty())
            return;
        if (hasFocus) {
            title.setTextColor(getResources().getColor(R.color.login_btn_color));
            title.startAnimation(up);
        } else {
            title.setTextColor(getResources().getColor(R.color.crop__button_text));
            title.startAnimation(down);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.length() > 0) {
            delete_btn.setVisibility(View.VISIBLE);
        } else {
            delete_btn.setVisibility(View.GONE);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    public void setTextType(boolean isPass) {
        if (isPass) {
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }else{
            input.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        }
    }

    @Override
    public void onClick(View v) {
        input.setText("");
    }

    public String getText() {
        return input.getText().toString();
    }
}
