package com.firebig.activity;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.firebig.Adapter.NewsItem_Comment_Adapter;
import com.firebig.Data.BmobDataHandle;
import com.firebig.app.AppContext;
import com.firebig.bean.Bean_Message;
import com.firebig.bean.Bean_News;
import com.firebig.bean.Bean_NewsItem_Comment;
import com.firebig.bean.BmobNewsComment;
import com.firebig.bean.Bmob_MyUser;
import com.firebig.util.DataPort;
import com.firebig.util.KeyBoardUtils;
import com.firebig.util.StringUtils;
import com.firebig.widget.FBLargeImageDisPlay;
import com.firebig.widget.FBListView;
import com.firebig.widget.FBScrollView;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;


/**
 * Created by FireBig-CH on 16-8-31.
 * 显示广播详细类
 * 1、四字评论在广播内容上以弹幕的形式显示（循环显示）(难度大，性能差，弃用)
 * 2、基础评论（正常评论内容在广播下显示）
 * 3、点击图片显示大图
 * 4、四字评论也用列表展示
 * 5、点击头像查看头像大图
 * 6、集合BmobPush功能进行评论的推送
 * 7、基础评论不进行数据保存，每次查询需要从服务器更新
 * 8、优化页面，更友好到显示错误信息和提示信息
 */
public class Activity_ShowNewsItem extends BaseActivity implements FBScrollView.ScrollViewListener, View.OnLayoutChangeListener {
    /**
     * 最外层Layout
     */
    private LinearLayout f_layout;
    /**
     * 输入layout
     */
    private LinearLayout input_layout;
    /**
     * 广播头像
     */
    private SimpleDraweeView header;

    /**
     * 用户名
     */
    private TextView username;

    /**
     * 发布时间
     */
    private TextView time;

    /**
     * 广播内容
     */
    private TextView content;

    /**
     * 广播配图
     */
    private SimpleDraweeView image;

    /**
     * 四字回复图标
     */
    private ImageView hide;

    /**
     * 切换显示评论
     */
    private TextView comment;

    /**
     * 切换显示四字评论
     */
    private TextView four_word_comment;

    /**
     * 显示评论列表
     */
    private FBListView comment_list;
    /**
     * 显示评论列表
     */
    private FBListView comment_four_list;

    /**
     * 显示广播到所占屏幕高度
     */
    private int newsHeight;

    /**
     * 点击的y点
     */
    private float oy;

    /**
     * 移动到y点
     */
    private float my;

    /**
     * 广播显示Card
     */
    private CardView cardView;

    /**
     * 测试数据
     */
    private List<Bean_NewsItem_Comment> data = new ArrayList<>();

    /**
     * 测试数据二
     */
    private List<BmobNewsComment> data2 = new ArrayList<>();

    /**
     * 添加评论列表
     */
    private FloatingActionButton add_comment;

    /**
     * 评论输入
     */
    private EditText comment_input;

    /**
     * 发送评论
     */
    private Button comment_send;

    /**
     * TextView_ID
     */
    private int[] ids = {R.id.newsitem_comment, R.id.newsitem_four_word_comment, R.id.newsitem_comment_hide, R.id.newsitem_four_word_comment_hide};

    /**
     * TextView
     */
    private TextView[] titles = new TextView[ids.length];

    /**
     * 滑动布局
     */
    private FBScrollView scrollView;

    /**
     * 评论列表显示模式
     */
    private int ListViewMode;

    /**
     * 数据设配器
     */
    private NewsItem_Comment_Adapter adapter, adapter2;

    /**
     * 普通评论
     */
    public static final int GENERAL_LIST = 0x11;

    /**
     * 四字评论
     */
    public static final int FOUR_WORD_LIST = 0x22;

    /**
     * idss
     */
    private int[] lines_id = {R.id.move_line, R.id.move_line_2, R.id.move_line_3, R.id.move_line_4};

    /**
     * 切换列表提示线段
     */
    private View[] move_lines = new View[lines_id.length];

    /**
     * 没有评论提示
     */
    private LinearLayout newsitem_no_news_show;

    /**
     * 显示大图
     */
    private FBLargeImageDisPlay largeImageDisPlay;

    /**
     * 数据
     */
    private Bean_News bean_news;

    /**
     * 评论列表
     */
    private List<BmobNewsComment> comment_data = new ArrayList<>();

    /**
     * 加载提示
     */
    private ProgressDialog dialog;

    /**
     * 评论类型：0、评论 1、回复评论
     */
    private int REPLY_TYPE = 0;

    /**
     * 回复评论接收者
     */
    private String toAccount = null;
    /**
     * 回复评论接收者名
     */
    private String toShownName = null;
    /**
     * 加载提示
     */
    private ProgressBar progressBar;

    /**
     * 没有内容和没有网络提示
     */
    private TextView newsitem_no_news_text;

    /**
     * 我的本地资料
     */
    private Bmob_MyUser myUser;

    /**
     * 加载
     */
    private BmobDataHandle loadHandle = BmobDataHandle.getInstance();

    /**
     * 评论数量
     */
    private Integer CommentNumber;
    /**
     * 四字评论数量
     */
    private Integer FourCommentNumber;

    private Handlers handlers = new Handlers();

    private class Handlers extends Handler {
    }

    @Override
    public void initUI() {
        newsitem_no_news_show = (LinearLayout) findViewById(R.id.newsitem_no_news_show);
        newsitem_no_news_text = (TextView) findViewById(R.id.newsitem_no_news_text);
        f_layout = (LinearLayout) findViewById(R.id.newsitem_f_layout);
        f_layout.addOnLayoutChangeListener(this);
        input_layout = (LinearLayout) findViewById(R.id.input_layout);
        header = (SimpleDraweeView) findViewById(R.id.news_header);
        username = (TextView) findViewById(R.id.news_username);
        time = (TextView) findViewById(R.id.news_time);
        content = (TextView) findViewById(R.id.news_content);
        image = (SimpleDraweeView) findViewById(R.id.news_image);
        hide = (ImageView) findViewById(R.id.news_four_word);
        hide.setVisibility(View.INVISIBLE);
        comment = (TextView) findViewById(R.id.newsitem_comment);
        four_word_comment = (TextView) findViewById(R.id.newsitem_four_word_comment);
        comment_four_list = (FBListView) findViewById(R.id.newsitem_four_comment_list);
        comment_list = (FBListView) findViewById(R.id.newsitem_comment_list);
        add_comment = (FloatingActionButton) findViewById(R.id.add_comment);
        add_comment.setOnClickListener(this);
        comment_input = (EditText) findViewById(R.id.comment_input);
        comment_send = (Button) findViewById(R.id.comment_send);
        comment_send.setOnClickListener(this);
        cardView = (CardView) findViewById(R.id.card_view);
        scrollView = (FBScrollView) findViewById(R.id.news_item_scroll);
        scrollView.setScrollViewListener(this);
        progressBar = (ProgressBar) findViewById(R.id.newsitem_load_bar);
        for (int i = 0; i < ids.length; i++) {
            titles[i] = (TextView) findViewById(ids[i]);
            titles[i].setOnClickListener(this);
        }
        for (int i = 0; i < lines_id.length; i++) {
            move_lines[i] = (View) findViewById(lines_id[i]);
        }
        move_lines[1].setVisibility(View.GONE);
        move_lines[3].setVisibility(View.GONE);
        ListViewMode = GENERAL_LIST;
        dialog = new ProgressDialog(this);
    }

    @Override
    public void loadData() {
        Bundle data = getIntent().getExtras();
        bean_news = (Bean_News) data.getSerializable("data");
        // 设置头像
        if (bean_news.getHeader_thumbnail_url() != null)
            header.setImageURI(Uri.parse(bean_news.getHeader_thumbnail_url()));
        header.setOnClickListener(this);
        // 设置用户名
        username.setText(bean_news.getShowname());
        // 时间
        time.setText(StringUtils.friendly_time(bean_news.getTime()));
        // 广播内容
        content.setText(bean_news.getNews_content());
        // 广播图片
        String url = bean_news.getThumbnail_url();
        if (url == null) {
            image.setVisibility(View.GONE);
        } else {
            image.setImageURI(Uri.parse(url));
            image.setOnClickListener(this);
        }

        // 加载广播评论
        if (!AppContext.getINSTANCE().isConnection) {
            newsitem_no_news_text.setText("没有网络连接");
            progressBar.setVisibility(View.GONE);
            newsitem_no_news_show.setVisibility(View.VISIBLE);
            comment_list.hideFooter();
            comment_four_list.hideFooter();
        }
        // 加载评论资料
        comment_data.clear();
        loadHandle.setContext(this);
        loadHandle.loadComment(bean_news.getNews_id(), false, 0);
        loadHandle.setloadCommentListener(new DataPort.loadCommentListener() {
            @Override
            public void done(List<BmobNewsComment> list, boolean isSkip) {
                // 把加载的内容添加进列表
                if (list.size() == 0 && !isSkip) {
                    newsitem_no_news_show.setVisibility(View.VISIBLE);
                    comment_list.hideFooter();
                }
                for (BmobNewsComment bc : list) {
                    comment_data.add(bc);
                }
                comment_list.setFooter_is_show(true);
                if (list.size() > 0 && list.size() <= BmobDataHandle.LIMITNUMBER) {
                    if (comment_data.size() == CommentNumber) {
                        comment_list.setFooterText("没有更多了");
                        comment_list.setCanLoadmore(false);
                    } else {
                        comment_list.setFooterText("加载更多");
                        comment_list.setCanLoadmore(true);
                    }
                } else {
                    comment_list.setFooterText("没有更多了");
                    comment_list.setCanLoadmore(false);
                }
                if (isSkip) {
                    comment_list.switchMode(FBListView.SHOWFOOTERTEXT);
                    comment_list.setLoading(false);
                    adapter.notifyDataSetChanged();
                    setListViewHeight(comment_list);
                    return;
                }
                adapter = new NewsItem_Comment_Adapter(comment_data, Activity_ShowNewsItem.this);
                comment_list.setAdapter(adapter);
                comment_list.setFocusable(false);
                adapter.notifyDataSetChanged();
                setListViewHeight(comment_list);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void commentNumber(Integer integer) {
                // 标题显示评论数量
                CommentNumber = integer;
                titles[0].setText("评论(" + integer + ")");
                titles[2].setText("评论(" + integer + ")");
            }

            @Override
            public void error() {
                newsitem_no_news_show.setVisibility(View.VISIBLE);
            }
        });


        loadHandle.loadCommentFourWord(bean_news.getNews_id(), false, 0);
        loadHandle.setLoadFourWordListener(new DataPort.loadFourWord() {
            @Override
            public void done(List<BmobNewsComment> list, boolean isSkip) {
                for (BmobNewsComment bc : list) {
                    data2.add(bc);
                }
                comment_list.setFooter_is_show(true);
                if (list.size() > 0 && list.size() <= BmobDataHandle.LIMITNUMBER) {
                    if (data2.size() == FourCommentNumber) {
                        comment_four_list.setFooterText("没有更多了");
                        comment_four_list.setCanLoadmore(false);
                    } else {
                        comment_four_list.setFooterText("加载更多");
                        comment_four_list.setCanLoadmore(true);
                    }
                } else {
                    comment_four_list.setFooterText("没有更多了");
                    comment_four_list.setCanLoadmore(false);
                }
                if (isSkip) {
                    comment_four_list.setLoading(false);
                    comment_four_list.switchMode(FBListView.SHOWFOOTERTEXT);
                    adapter2.notifyDataSetChanged();
                    setListViewHeight(comment_four_list);
                    return;
                }
                adapter2 = new NewsItem_Comment_Adapter(data2, Activity_ShowNewsItem.this);
                comment_four_list.setAdapter(adapter2);
                comment_four_list.setVisibility(View.INVISIBLE);
                adapter2.notifyDataSetChanged();
                setListViewHeight(comment_four_list);
                comment_four_list.setFocusable(false);
            }

            @Override
            public void allFourNumber(Integer integer) {
                FourCommentNumber = integer;
                titles[1].setText("四字评论(" + integer + ")");
                titles[3].setText("四字评论(" + integer + ")");
            }

            @Override
            public void error() {
                newsitem_no_news_show.setVisibility(View.VISIBLE);
            }
        });

        comment_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // TODO 点击评论项，回复评论内容
                if (i == comment_data.size()) {
                    if (comment_list.isCanLoadmore() && !comment_list.isLoading() && comment_list.isFooter_is_show()) {
                        comment_list.setLoading(true);
                        loadHandle.loadComment(bean_news.getNews_id(), true, comment_data.size());
                        comment_list.switchMode(FBListView.SHOWPROGRESSBAR);
                    }
                    return;
                }
                // 判断是否加载了用户名
                if (comment_data.get(i).getUsername() != null)
                    toShownName = comment_data.get(i).getUsername();
                else
                    toShownName = comment_data.get(i).getAccount();
                toAccount = comment_data.get(i).getAccount();
                REPLY_TYPE = 1;
                showCommentInput();
            }
        });

        comment_four_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // 点击加载更多
                if (i == data2.size()) {
                    if (comment_four_list.isCanLoadmore() && !comment_four_list.isLoading() && comment_four_list.isFooter_is_show()) {
                        comment_four_list.setLoading(true);
                        loadHandle.loadCommentFourWord(bean_news.getNews_id(), true, data2.size());
                        comment_four_list.switchMode(FBListView.SHOWPROGRESSBAR);
                        return;
                    }
                }
            }
        });
        // 加载我的缓存资料
        myUser = BmobUser.getCurrentUser(Bmob_MyUser.class);
        setListViewHeight(comment_list);
    }

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_shownewsitem);
        setToolBar(R.string.newsItem);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.add_comment) {
            REPLY_TYPE = 0;
            showCommentInput();
        } else if (id == R.id.comment_send) {
            // 发送广播评论
            if (REPLY_TYPE == 0)
                sendComment(null);
            else
                sendComment(toAccount);
        } else if (id == R.id.newsitem_comment || id == R.id.newsitem_comment_hide) {
            if (ListViewMode == GENERAL_LIST)
                return;
            // 显示评论
            ListViewMode = GENERAL_LIST;
            move_lines[0].startAnimation(AnimationUtils.loadAnimation(this, R.anim.move_left));
            move_lines[2].startAnimation(AnimationUtils.loadAnimation(this, R.anim.move_left));
            move_lines[1].startAnimation(AnimationUtils.loadAnimation(this, R.anim.move_left2));
            move_lines[3].startAnimation(AnimationUtils.loadAnimation(this, R.anim.move_left2));
            add_comment.startAnimation(AnimationUtils.loadAnimation(this, R.anim.add_comment_left_show));
            add_comment.setOnClickListener(this);
            switchListViewMode(ListViewMode);

        } else if (id == R.id.newsitem_four_word_comment || id == R.id.newsitem_four_word_comment_hide) {
            if (ListViewMode == FOUR_WORD_LIST)
                return;
            // 显示四字评论
            ListViewMode = FOUR_WORD_LIST;
            move_lines[0].startAnimation(AnimationUtils.loadAnimation(this, R.anim.move_right));
            move_lines[2].startAnimation(AnimationUtils.loadAnimation(this, R.anim.move_right));
            move_lines[1].startAnimation(AnimationUtils.loadAnimation(this, R.anim.move_right2));
            move_lines[3].startAnimation(AnimationUtils.loadAnimation(this, R.anim.move_right2));
            add_comment.startAnimation(AnimationUtils.loadAnimation(this, R.anim.add_comment_right_hide));
            add_comment.setOnClickListener(null);
            switchListViewMode(ListViewMode);
        } else if (id == R.id.news_image) {
            showHightImage(bean_news.getNews_image_url(), bean_news.getNews_image_name());
        } else if (id == R.id.news_header) {
            if (bean_news.getHeader_url() != null)
                showHightImage(bean_news.getHeader_url(), bean_news.getHeader_name());
        }

    }

    /**
     * 发送评论,评论字的数量不得超过50
     *
     * @param toAccout 回复评论
     */
    public void sendComment(final String toAccout) {
        if (comment_input.getText().toString().length() > 50) {
            toast("不能多于50个字呢！");
            toAccount = null;
            toShownName = null;
            return;
        }
        if (comment_input.getText().toString().length() > 0) {
            // 隐藏软键盘
            KeyBoardUtils.closeKeybord(comment_input, Activity_ShowNewsItem.this);
            dialog.setMessage("请求中..");
            dialog.show();
            final BmobNewsComment newsComment = new BmobNewsComment();
            newsComment.setAccount(AppContext.getINSTANCE().UserAccount);
            newsComment.setId(bean_news.getNews_id());
            newsComment.setContent(comment_input.getText().toString());
            if (toAccout != null && toShownName != null) {
                newsComment.setToaccount(toShownName);
            }
            newsComment.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    if (e == null) {
//                        if (toAccout == null) { // 直接回复
//                            // 判断是不是自己评论自己，如果是则不进行消息推送
////                            if (!bean_news.getUsername().equals(AppContext.getINSTANCE().UserAccount)) {
////                                sendToast(new String[]{bean_news.getUsername(), myUser.getShowname(), bean_news.getNews_id(), comment_input.getText().toString()}, 0);
////                            }
//                        } else { //回复评论
////                            // 判断是不是自己评论自己，如果是则不进行消息推送
////                            if (!toAccout.equals(AppContext.getINSTANCE().UserAccount)) {
////                                sendToast(new String[]{toAccout, toShownName, bean_news.getNews_id(), comment_input.getText().toString()}, 1);
////                            }
//                        }
                        newsComment.setUsername(myUser.getShowname());
                        newsComment.setAvaterUrl(myUser.getAvater().getFileUrl());
                        newsComment.setAvater_thumbnailUrl(myUser.getHeader_thumbnail_url());
                        comment_data.add(0, newsComment);
                        adapter.notifyDataSetChanged();
                        setListViewHeight(comment_list);
                        comment_list.setFooterText("没有更多了");
                        comment_list.switchMode(FBListView.SHOWFOOTERTEXT);
                        handlers.post(new Runnable() {
                            @Override
                            public void run() {
                                scrollView.fullScroll(ScrollView.FOCUS_UP);
                            }
                        });
                        comment_input.setText("");
                        newsitem_no_news_show.setVisibility(View.GONE);
                        CommentNumber++;
                        titles[0].setText("评论(" + CommentNumber + ")");
                        titles[2].setText("评论(" + CommentNumber + ")");
                    } else {
                        AppContext.getINSTANCE().showBmobException(Activity_ShowNewsItem.this, e);
                    }
                    dialog.dismiss();
                }
            });
            toAccount = null;
            toShownName = null;
        } else {
            toast("请输入评论内容...");
            toAccount = null;
            toShownName = null;
        }
    }

    /**
     * 发送通知
     *
     * @param data String from_account, String from_name, int type,String newsId,String message
     */
    public void sendToast(String[] data, int type) {
        Bean_Message bean_message = new Bean_Message();
        bean_message.setFrom_account(data[0]);
        bean_message.setFrom_name(data[1]);
        bean_message.setMESSAGE_TYPE(type);
        bean_message.setNesw_id(data[2]);
        bean_message.setMessage(data[3]);
        bean_message.setTime(StringUtils.getNowTime());
        /****** Bmob IM********/
//        BmobIMHandle.getInstance().sendMessage(bean_message);
//        BmobIMHandle.getInstance().setSendTextListener(new DataPort.sendText() {
//            @Override
//            public void done() {
//                toast("Send Done");
//            }
//
//            @Override
//            public void error() {
//                toast("Send Error");
//            }
//        });
    }

    /**
     * 显示大图
     */
    public void showHightImage(@NonNull String url, @NonNull String im_name) {
        if (largeImageDisPlay == null) {
            largeImageDisPlay = new FBLargeImageDisPlay(this);
            largeImageDisPlay.setUrl(url, im_name);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            addContentView(largeImageDisPlay.getRootView(), params);
            largeImageDisPlay.Show();
        } else {
            largeImageDisPlay.setUrl(url, im_name);
            largeImageDisPlay.Show();
        }
    }

    /**
     * 切换ListView显示模式
     *
     * @param mode
     */
    private void switchListViewMode(int mode) {
        // 显示评论数量
        titles[1].setText("四字评论(" + FourCommentNumber + ")");
        titles[3].setText("四字评论(" + FourCommentNumber + ")");
        int temp1 = 0;
        int temp2 = 2;
        if (mode == FOUR_WORD_LIST) {
            temp1 = 1;
            temp2 = 3;
        }
        for (int i = 0; i < lines_id.length; i++) {
            if (i == temp1 || i == temp2) {
                titles[i].setTextColor(Color.parseColor("#80adff"));
                move_lines[i].setVisibility(View.VISIBLE);
            } else {
                titles[i].setTextColor(Color.parseColor("#000000"));
                move_lines[i].setVisibility(View.GONE);
            }
        }
        if (mode == GENERAL_LIST) {
            comment_four_list.setVisibility(View.GONE);
            comment_list.setVisibility(View.VISIBLE);
            setListViewHeight(comment_list);
            comment_list.setFocusable(false);
            if (comment_list.getChildCount() <= 1) {
                comment_list.hideFooter();
                newsitem_no_news_show.setVisibility(View.VISIBLE);
            } else {
                newsitem_no_news_show.setVisibility(View.GONE);
            }
        } else {
            comment_four_list.setVisibility(View.VISIBLE);
            comment_list.setVisibility(View.GONE);
            setListViewHeight(comment_four_list);
            comment_four_list.setFocusable(false);
            if (comment_four_list.getChildCount() <= 1) {
                comment_four_list.hideFooter();
                newsitem_no_news_show.setVisibility(View.VISIBLE);
            } else {
                newsitem_no_news_show.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 显示键盘输入
     */
    private void showCommentInput() {

        final Animation show_input = AnimationUtils.loadAnimation(this, R.anim.add_comment_btn);
        final ScaleAnimation animation2 = new ScaleAnimation(0.0f, 1.0f, 1.0f, 1.0f,
                Animation.RELATIVE_TO_PARENT, 0.5f, Animation.RELATIVE_TO_PARENT, 0.5f);
        animation2.setDuration(300);

        Animation.AnimationListener listener = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (animation == show_input) {
                    add_comment.setVisibility(View.GONE);
                    add_comment.setOnClickListener(null);
                    input_layout.setVisibility(View.VISIBLE);
                    input_layout.startAnimation(animation2);
                    comment_input.setFocusable(true);
                    comment_input.setFocusableInTouchMode(true);
                    comment_input.requestFocus();
                    comment_input.findFocus();
                    if (REPLY_TYPE == 0) {
                        comment_input.setHint("请输入评论");
                    } else {
                        comment_input.setHint("回复@" + toShownName);
                    }
                } else if (animation == animation2) {
                    KeyBoardUtils.openKeybord(comment_input, Activity_ShowNewsItem.this);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        };
        show_input.setAnimationListener(listener);
        animation2.setAnimationListener(listener);
        add_comment.startAnimation(show_input);

    }


    /**
     * 设置listView高度
     *
     * @param listView
     */
    private void setListViewHeight(ListView listView) {
        if (listView == null) return;
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
            Log.d("Height", "istItem.getMeasuredHeight():" + "I:" + i + listItem.getMeasuredHeight());
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    @Override
    public void onScrollChanged(FBScrollView scrollView, int x, int y, int oldx, int oldy) {
        scrollView.scrollTo(x, y);
        if (y >= cardView.getHeight()) {
            LinearLayout layout = (LinearLayout) findViewById(R.id.news_item_list_title);
            layout.setVisibility(View.VISIBLE);
        } else if (y < cardView.getHeight()) {
            LinearLayout layout = (LinearLayout) findViewById(R.id.news_item_list_title);
            layout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBottom() {
        //  1.判断模式
        // 2.判断有没有到达加载数量（第一次加载数量为10条，没有到达10条则不用加载）
        // 3.由于ScrollView多次触发此函数,需判断是不是正在加载，如果是return
        if (ListViewMode == GENERAL_LIST) {
            if (adapter != null)
                adapter.notifyDataSetChanged();
            if (!comment_list.isLoading() && comment_list.isCanLoadmore()) {
                comment_list.setLoading(true);
                loadHandle.loadComment(bean_news.getNews_id(), true, comment_data.size());
                comment_list.switchMode(FBListView.SHOWPROGRESSBAR);
            }
        } else {
            if (adapter2 != null)
                adapter2.notifyDataSetChanged();
            if (!comment_four_list.isLoading() && comment_four_list.isCanLoadmore()) {
                comment_four_list.setLoading(true);
                loadHandle.loadCommentFourWord(bean_news.getNews_id(), true, data2.size());
                comment_four_list.switchMode(FBListView.SHOWPROGRESSBAR);
            }
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        // 获取返回键
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (largeImageDisPlay != null && largeImageDisPlay.getRootView() != null && largeImageDisPlay.getRootView().getVisibility() == View.VISIBLE) {
                largeImageDisPlay.hide();
            } else {
                finish();
            }
        }
        return false;
    }

    @Override
    public void onLayoutChange(View view, int left, int top, int right,
                               int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {

        if (oldBottom != 0 && bottom != 0 && (bottom - oldBottom > screen_height / 3)) {
            add_comment.setVisibility(View.VISIBLE);
            add_comment.setOnClickListener(this);
            add_comment.startAnimation(AnimationUtils.loadAnimation(this, R.anim.show_add_comment));
            input_layout.setVisibility(View.GONE);
        }
    }
}
