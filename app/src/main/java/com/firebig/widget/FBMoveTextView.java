package com.firebig.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

/**
 * Created by root on 16-8-17.
 */
public class FBMoveTextView extends TextView {

    public class THandlers extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            move();
        }
    };

    private boolean isStart = true;
    private THandlers handlers;

    public FBMoveTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        handlers = new THandlers();
    }

    public FBMoveTextView(Context context) {
        super(context);
        handlers = new THandlers();
    }

    public void start() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isStart){
                    try {
                        Thread.sleep(33);
                        handlers.sendEmptyMessage(0);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void move(){
        float x = getX();
        x-=5;
        setX(x);
        if(x<0){
            setX(1080);
            //this.setVisibility(View.GONE);
        }
    }
}
