package com.firebig.activity;

import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.firebig.Adapter.MessageNoticeAdapter;
import com.firebig.Data.DataUtil;
import com.firebig.app.AppContext;
import com.firebig.bean.Bean_Message;
import com.firebig.bean.BmobNewsComment;
import com.firebig.bean.Bmob_MyUser;
import com.firebig.util.DataPort;
import com.firebig.util.KeyBoardUtils;
import com.firebig.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by FireBig-CH on 16-9-12.
 * 显示消息通知类
 */
public class Activity_Message extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener, View.OnLayoutChangeListener {
    /**
     * 消息列表
     */
    private RecyclerView message_list;

    /**
     * 下拉刷新
     */
    private SwipeRefreshLayout refresh;

    /**
     * 没有通知提示
     */
    private LinearLayout no_message_show;

    /**
     * 数据列表
     */
    private List<Bean_Message> messages = new ArrayList<>();

    /**
     * 数据设配器
     */
    private MessageNoticeAdapter adapter;

    /**
     * 消息处理
     */
    private NoticeHandler handler;

    /**
     * 键盘布局
     */
    private LinearLayout inputLayout;
    /**
     * 加载提示
     */
    private ProgressDialog dialog;

    /**
     * 输入
     */
    private EditText editText_input;

    /**
     * 输入发送按钮
     */
    private Button reply_btn;
    /**
     * 底层layout，监听软键盘显示和隐藏
     */
    private RelativeLayout notice_layout;
    /**
     * 记录点击位置
     */
    private int temp_index = 0;

    @Override
    public void onLayoutChange(View view, int left, int top, int right,
                               int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        if (oldBottom != 0 && bottom != 0 && (bottom - oldBottom > screen_height / 3)) {
            showEditText(false);
        }
    }

    public class NoticeHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            refresh.setRefreshing(false);
            loadData();
        }
    }

    @Override
    public void initUI() {
        notice_layout = (RelativeLayout) findViewById(R.id.notice_layout);
        notice_layout.addOnLayoutChangeListener(this);
        no_message_show = (LinearLayout) findViewById(R.id.message_no_show);
        message_list = (RecyclerView) findViewById(R.id.notice_list);
        message_list.setLayoutManager(new LinearLayoutManager(this));
        refresh = (SwipeRefreshLayout) findViewById(R.id.message_res);
        refresh.setOnRefreshListener(this);
        refresh.setColorSchemeResources(R.color.color01, R.color.color02, R.color.color03, R.color.color04);
        inputLayout = (LinearLayout) findViewById(R.id.notice_reply_input);
        inputLayout.setVisibility(View.GONE);
        editText_input = (EditText) findViewById(R.id.notice_input_ed);
        reply_btn = (Button) findViewById(R.id.notice_input_btn);
        reply_btn.setOnClickListener(this);
        dialog = new ProgressDialog(this);
        handler = new NoticeHandler();
    }

    @Override
    public void loadData() {
        if (!messages.isEmpty())
            messages.clear();
        // 获取数据
        messages = DataUtil.DataUtil(this).getNotice();
        if (messages.size() <= 0)
            no_message_show.setVisibility(View.VISIBLE);
        else
            no_message_show.setVisibility(View.GONE);
        adapter = new MessageNoticeAdapter(this, messages);
        message_list.setAdapter(adapter);
        adapter.setOnClick(new DataPort.noticeOnItemClick() {
            @Override
            public void onItemClick(View view, int position) {
                // TODO 跳转到广播详细页面
                gotoNewsItem(position);
            }

            @Override
            public void onMessageClick(View view, int position) {
                // TODO 回复按钮
                showEditText(true);
                temp_index = position;
            }
        });
    }

    /**
     * 通过广播识别ID，判断跳转传值
     *
     * @param position
     */
    public void gotoNewsItem(int position) {
//        for (int i = 0; i < Activity_Home.datalist.size(); i++) {
//            if (Activity_Home.datalist.get(i).getNews_id() == messages.get(position).getNesw_id()) {
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("data", Activity_Home.datalist.get(i));
//                goToActivity(Activity_ShowNewsItem.class, bundle, false);
//            }
//        }
    }

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_message);
        setToolBar(R.string.message);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.notice_input_btn) {
            // TODO 发送评论
            sendReply();
        }
    }

    @Override
    public void onRefresh() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    handler.sendEmptyMessage(0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 发送回复
     */
    private void sendReply() {
        if (editText_input.getText().length() == 0) {
            toast("请输入要回复的内容..");
            return;
        }
        if (editText_input.getText().length() > 50) {
            toast("输入的太多了..");
            return;
        }
        dialog.setMessage("请求中..");
        dialog.show();
        final BmobNewsComment newsComment = new BmobNewsComment();
        newsComment.setAccount(AppContext.getINSTANCE().UserAccount);
        newsComment.setId(messages.get(temp_index).getNesw_id());
        newsComment.setContent(editText_input.getText().toString());
        // 列表显示　xxx回复了@xxx:xxxx
        newsComment.setToaccount(messages.get(temp_index).getFrom_name());
        newsComment.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    // 推送
                    Bean_Message bean_message = new Bean_Message();
                    Bmob_MyUser myUser = BmobUser.getCurrentUser(Bmob_MyUser.class);
                    bean_message.setFrom_account(myUser.getUsername());
                    bean_message.setFrom_name(myUser.getShowname());
                    bean_message.setNesw_id(messages.get(temp_index).getNesw_id());
                    bean_message.setMessage(editText_input.getText().toString());
                    bean_message.setTime(StringUtils.getNowTime());
                    bean_message.setMESSAGE_TYPE(1);
                    //BmobIMHandle.getInstance().sendMessage(bean_message);
                    toast("已发送！");
                    KeyBoardUtils.closeKeybord(editText_input, Activity_Message.this);
                    editText_input.setText("");
                } else {
                    AppContext.getINSTANCE().showBmobException(Activity_Message.this, e);
                }
                dialog.dismiss();
            }
        });
    }

    /**
     * 显示键盘/隐藏键盘
     */
    private void showEditText(boolean show) {
        if (show) {
            editText_input.setFocusable(true);
            editText_input.setFocusableInTouchMode(true);
            editText_input.requestFocus();
            editText_input.findFocus();
            inputLayout.setVisibility(View.VISIBLE);
            KeyBoardUtils.openKeybord(editText_input, this);
        } else {
            inputLayout.setVisibility(View.GONE);
        }
    }
}
