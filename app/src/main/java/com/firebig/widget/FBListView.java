package com.firebig.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebig.activity.R;

/**
 * Created by FireBig-CH on 16-10-12
 */
public class FBListView extends ListView {

    private LinearLayout footer_layout;

    private TextView footer_tv;

    private ProgressBar footer_pb;

    /**
     * 加载状态，防止多次进行加载
     */
    private boolean isLoading = false;

    /**
     * 能否加载更多，每次加载10条，如果不满10条则，不能加载更多
     */
    private boolean canLoadmore = true;

    private boolean footer_is_show = true;

    public static final boolean SHOWPROGRESSBAR = false;
    public static final boolean SHOWFOOTERTEXT = true;

    public FBListView(Context context) {
        super(context);
        init();
    }

    public FBListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FBListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 添加脚文件
     */
    private void init() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.item_footer, null);
        addFooterView(view);
        footer_layout = (LinearLayout) view.findViewById(R.id.footer_no_more);
        footer_tv = (TextView) view.findViewById(R.id.no_more_text);
        footer_pb = (ProgressBar) view.findViewById(R.id.news_item_footer_pb);
        footer_pb.setVisibility(INVISIBLE);
    }

    /**
     * 设置脚部显示文字
     *
     * @param text
     */
    public void setFooterText(@NonNull String text) {
        footer_tv.setText(text);
    }

    /**
     * 切换显示,正在加载和加载完成的时候
     *
     * @param isshow
     */
    public void switchMode(boolean isshow) {
        if (isshow) {
            footer_layout.setVisibility(VISIBLE);
            footer_pb.setVisibility(INVISIBLE);
        } else {
            footer_layout.setVisibility(INVISIBLE);
            footer_pb.setVisibility(VISIBLE);
        }
    }

    /**
     * 隐藏脚部
     */
    public void hideFooter() {
        footer_layout.setVisibility(GONE);
        footer_pb.setVisibility(GONE);
        setFooter_is_show(false);
    }

    /**
     * 显示脚部
     */
    public void showFooter() {
        footer_layout.setVisibility(VISIBLE);
        footer_pb.setVisibility(VISIBLE);
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    public boolean isCanLoadmore() {
        return canLoadmore;
    }

    public void setCanLoadmore(boolean canLoadmore) {
        this.canLoadmore = canLoadmore;
    }

    public boolean isFooter_is_show() {
        return footer_is_show;
    }

    public void setFooter_is_show(boolean footer_is_show) {
        this.footer_is_show = footer_is_show;
    }
}
