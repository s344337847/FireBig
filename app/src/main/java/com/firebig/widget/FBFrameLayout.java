package com.firebig.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.firebig.activity.BaseActivity;
import com.firebig.activity.R;
import com.firebig.app.AppContext;
import com.firebig.bean.BombNewsFourWord;

import java.util.List;

/**
 * Created by FireBig-CH on 16-8-30.
 * 显示四字评论类:
 * 实现、
 * 1.显示四字评论到广播内容上
 * 2.只显示50条四字评论(其余评论在广播详细页面可以查看)
 * <p/>
 * 加载显示四字评论时，必须先获取当前View的宽高，TextView的位置为随机位置（可能覆盖）
 */
public class FBFrameLayout extends FrameLayout {
    /**
     * Layout 宽
     */
    private int w = 500;

    /**
     * Layout 高
     */
    private int h = 200;
    /**
     * 最多在广播上显示多少条四字评论
     */
    public static final int MAX_NUMBER = 50;

    private int position = -1;

    public FBFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public FBFrameLayout(Context context) {
        super(context);
    }

    public FBFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 显示四字评论
     *
     * @param word 四字评论内容列表,只会显示前一百条，后面到四字评论不会在主界面上对应到广播内容上显示
     */
    public void show(List<BombNewsFourWord> word) {
        if (word == null)
            return;
        // 判断移除存在的四字评论
        if (getChildCount() == word.size()) {
            return;
        } else {
            removeAllViews();
        }
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        for (int i = 0; i < word.size(); i++) {
            if (i >= MAX_NUMBER)
                return;
            TextView view = new TextView(getContext());
            view.setText(word.get(i).getContent());
            view.setX(getrondom(BaseActivity.width - 100));
            view.setY(getrondom(h - 50));
            view.setGravity(Gravity.CENTER);
            view.setScaleX(0.7f);
            view.setScaleY(0.7f);
            if (word.get(i).getAccount().equals(AppContext.getINSTANCE().UserAccount))
                view.setBackgroundResource(R.drawable.textstyle_red);
            else
                view.setBackgroundResource(R.drawable.textstyle);
            addView(view, params);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        h = MeasureSpec.getSize(heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 简单获取随机位置
     *
     * @param max
     * @return
     */
    public int getrondom(int max) {
        Double i = Math.random() * max;
        return i.intValue();
    }
}
