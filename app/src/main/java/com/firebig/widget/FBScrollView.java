package com.firebig.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

/**
 * Created by root on 16-9-2.
 */
public class FBScrollView extends ScrollView {

    private ScrollViewListener scrollViewListener;

    public FBScrollView(Context context) {
        super(context);
    }

    public FBScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FBScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setScrollViewListener(ScrollViewListener scrollViewListener) {
        this.scrollViewListener = scrollViewListener;
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);
        if (scrollViewListener != null) {
            scrollViewListener.onScrollChanged(this, x, y, oldx, oldy);
            if (getChildAt(0).getMeasuredHeight() <= getScrollY() + getHeight()) {
                scrollViewListener.onBottom();
            }
        }
    }

    public interface ScrollViewListener {
        void onScrollChanged(FBScrollView scrollView, int x, int y, int oldx, int oldy);

        void onBottom();
    }
}
