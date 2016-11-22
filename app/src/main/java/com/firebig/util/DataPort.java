package com.firebig.util;

import android.view.View;
import android.widget.FrameLayout;

import com.firebig.bean.Bean_News;
import com.firebig.bean.BmobNewsComment;
import com.firebig.bean.Bmob_MyUser;

import java.util.List;

/**
 * Created by root on 16-9-6.
 */
public interface DataPort {
    /**
     * 加载广播数据
     */
    interface getDataListener {
        void done(List<Bean_News> list, boolean isloadMore);

        void error();
    }

    /**
     * 加载更多接口
     */
    interface loadMoreListener {
        void load();
    }

    /**
     * 获取更多数据
     */
    interface loadDataFromBmob {
        void loadDone(List<Bean_News> list);
    }

    /**
     * 点击图片
     */
    interface onClickImage {
        void onClick(View view, int position);
    }

    /**
     * 加载评论
     */
    interface loadCommentListener {
        void done(List<BmobNewsComment> list, boolean isSkip);

        void commentNumber(Integer integer);

        void error();
    }

    /**
     * 加载头像数据
     */
    interface loadFourWord {
        void done(List<BmobNewsComment> list, boolean isSkip);

        void allFourNumber(Integer integer);

        void error();
    }

    /**
     * 处理图片
     */
    interface handleImage {
        void done(String LargeImage_Path, String smallImage_Path);
    }

    /**
     * 网络状态
     */
    interface onNetChange {
        void onNetChange(int type);
    }

    /**
     * 通知监听接口
     */
    interface onMessageReceiver {
        void onMessageReceiver();
    }

    /**
     * item 点击和评论按钮监听接口
     */
    interface noticeOnItemClick {
        void onItemClick(View view, int position);

        void onMessageClick(View view, int position);
    }

    /**
     * 广播点击监听接口
     */
    interface RecyclerViewOnItemClick {
        void onItemClick(View view, int position);

        void onNewsFourWord(FrameLayout layout, int position);
    }

    /**
     * 长按监听接口
     */
    interface RecyclerViewOnItemLongClick {
        void onItemLongClick(View view, int position, FrameLayout layout);
    }

    /**
     * 滑动监听
     */
    interface addOnScroll {
        void onScroll();
    }

    /**
     * 我的广播删除按钮监听
     */
    interface setMyNewsDeleteOnClick {
        void deleteOnClick(int position);

        void cancleOnClick(int position);
    }

    /**
     * 消息推送监听接口
     */
    interface sendText {
        void done();

        void error();
    }

    /**
     * 点击查看回复列表
     */
    interface replayList {
        void onClick(View view, int position);
    }

    /**
     * 设置页面，开关按钮
     */
    public interface onSwitchChecked{
        void Change(int position,boolean isChecked);
    }

}
