package com.firebig.activity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.firebig.Adapter.NewsAdapter;
import com.firebig.Data.BmobDataHandle;
import com.firebig.Data.FileUtil;
import com.firebig.app.AppContext;
import com.firebig.app.NetWorkListener;
import com.firebig.bean.Bean_News;
import com.firebig.bean.Bmob_MyUser;
import com.firebig.bean.BombNewsFourWord;
import com.firebig.explosionField.ExplosionField;
import com.firebig.util.CommandUtil;
import com.firebig.util.DataPort;
import com.firebig.util.KeyBoardUtils;
import com.firebig.widget.FBLargeImageDisPlay;
import com.igexin.sdk.PushManager;

import net.youmi.android.listener.Interface_ActivityListener;
import net.youmi.android.offers.OffersManager;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;


/**
 * Created by FireBig-CH on 16-8-16.
 *
 * @version 1.0
 *          <p/>
 *          主界面类：
 *          1.使用RecyclerView + SwipeRefreshLayout 实现下拉刷新
 *          2.继承NavigationView实现左滑页面
 *          3.RecyclerView Item项长按事件的破碎效果使用开源项目：explosionField
 *          4.图片加载库使用：Fresco
 *          5.继承OnLayoutChangeListener用于判断软键盘到弹起到隐藏
 *          6.实现网络变化到判断(只考虑有网和没网两种情况,3G,4G,5G,还是Wifi不判断)
 */
public class Activity_Home extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, SwipeRefreshLayout.OnRefreshListener, View.OnLayoutChangeListener {

    /**
     * 下拉刷新控件
     **/
    private SwipeRefreshLayout swipeRefreshLayout;
    /**
     * 没有内容提示
     */
    private LinearLayout no_news_show;
    /**
     * 列表
     **/
    private RecyclerView list_view;
    /**
     * 保存列表数据
     */
    public static List<Bean_News> datalist = new ArrayList<>();
    /**
     * 数据设配器
     **/
    private NewsAdapter newsAdapter;
    /**
     * 标题
     */
    private TextView title;
    /**
     * 消息通知
     */
    private ImageView message;
    /**
     * 搜索
     */
    private ImageView search;
    /**
     * 广播广播
     */
    private ImageView addNews;

    /**
     * 接受线程信息
     **/
    private Handlers handlers;
    /**
     * 侧滑菜单
     */
    private DrawerLayout drawer;

    /**
     * 最外层layout 监听高度判断软键盘都打开和关闭
     **/
    private LinearLayout four_word_input;

    /**
     * 四字评论输入
     */
    private EditText four_word_ed;
    /**
     * 四字评论发送按钮
     */
    private Button four_word_btn;

    /**
     * 四字评论要发送都对象广播识别ID
     */
    private String newsId;
    /**
     * 四字评论Item 位置
     */
    private int Temp_Position;
    /**
     * Item 长按效果
     */
    private ExplosionField mExplosionField;

    /**
     * 头像显示
     */
    private SimpleDraweeView header;

    /**
     * 账号或用户名
     */
    private TextView username;
    /**
     * 用户签名
     */
    // private TextView user_singtrue;

    /**
     * 四字评论画布
     */
    private FrameLayout canvas;

    /**
     * 显示大图
     */
    private FBLargeImageDisPlay largeImageDisPlay;
    /**
     * 网络监听
     */
    private NetWorkListener myReceiver;

    /**
     * 消息红点提示
     */
    private TextView message_Red;

    /**
     * 监听最外层layout的高度变化，来判断键盘到显示和隐藏
     */
    @Override
    public void onLayoutChange(View view, int left, int top, int right,
                               int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {

        if (oldBottom != 0 && bottom != 0 && (bottom - oldBottom > screen_height / 3)) {
            four_word_input.setVisibility(View.GONE);
        }
    }

    /**
     * 监听网络变化
     */
    private void registerReceiver() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        myReceiver = new NetWorkListener();
        this.registerReceiver(myReceiver, filter);
        myReceiver.setOnNetChangeListener(new DataPort.onNetChange() {
            @Override
            public void onNetChange(int type) {
                updataUIfromNetChange();
            }
        });

        // 恢复可以加载更多
        if (newsAdapter != null) {
            newsAdapter.setLoaded();
        }
    }

//    @Override
//    public void onMessageReceive(List<MessageEvent> list) {
//        for (MessageEvent messageEvent : list) {
//            if (!messageEvent.getFromUserInfo().getName().equals(AppContext.getINSTANCE().UserAccount))
//                // TODO 接收到消息
//                message_Red.setVisibility(View.VISIBLE);
//        }
//    }

    public class Handlers extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            reLoadData(false);
        }
    }

    /**
     * 更新数据
     */
    public void reLoadData(boolean isloadMore) {
        if (!AppContext.getINSTANCE().isConnection) {
            toast("没有网络连接呢!");
            swipeRefreshLayout.setRefreshing(false);
            return;
        }
        BmobDataHandle.getInstance().setContext(this);
        BmobDataHandle.getInstance().getData(isloadMore, datalist.size());
        BmobDataHandle.getInstance().setgetDataListener(new DataPort.getDataListener() {
            @Override
            public void done(List<Bean_News> list, boolean isloadmore) {
                if (isloadmore) {
                    if (list.size() == 0) {
                        newsAdapter.isNoMore(true);
                        newsAdapter.notifyDataSetChanged();
                        newsAdapter.setLoaded();
                        return;
                    } else {
                        //移除刷新的progressBar
                        datalist.remove(datalist.size() - 1);
                        for (int i = 0; i < list.size(); i++) {
                            datalist.add(list.get(i));
                        }
                        newsAdapter.isNoMore(false);
                        newsAdapter.notifyDataSetChanged();
                        newsAdapter.setLoaded();
                        return;
                    }
                }else {
                    if (!datalist.isEmpty())
                        datalist.clear();
                    for (int i = 0; i < list.size(); i++) {
                        datalist.add(list.get(i));
                    }
                    newsAdapter = new NewsAdapter(datalist, Activity_Home.this, list_view);
                    list_view.setAdapter(newsAdapter);
                    adapterListener();
                    if (list.size() == 0) {
                        no_news_show.setVisibility(View.VISIBLE);
                        newsAdapter.setMoreShow(false);
                        newsAdapter.notifyDataSetChanged();
                    } else {
                        no_news_show.setVisibility(View.GONE);
                    }
                    newsAdapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                }

            }

            @Override
            public void error() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    /**
     * 当网络发生变化时更改UI界面
     */
    private void updataUIfromNetChange() {
        if (!AppContext.getINSTANCE().isNetworkAvailable(Activity_Home.this)) {
            AppContext.getINSTANCE().isConnection = false;
            title.setText("(无网络连接)广播");
        } else {
            AppContext.getINSTANCE().isConnection = true;
            title.setText("广播");
        }
    }

    @Override
    public void initUI() {
        // 标题栏
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getLayoutInflater().inflate(R.layout.toolbar_home, toolbar);
        no_news_show = (LinearLayout) findViewById(R.id.home_no_news_show);
        // 实例化破碎效果
        mExplosionField = new ExplosionField(this);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View view = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AppContext.getINSTANCE().LOGIN)
                    goToActivityForResult(Activity_MyData.class, null);
                else
                    goToActivity(Activity_Login.class, null, true);
            }
        });

        // 左侧个人信息组件
        header = (SimpleDraweeView) view.findViewById(R.id.header_imageView);
        username = (TextView) view.findViewById(R.id.username);
        //user_singtrue = (TextView) view.findViewById(R.id.user_singture);
        // 标题控件
        title = (TextView) findViewById(R.id.home_toolbar_title);
        title.setText(R.string.home_news);
        message = (ImageView) findViewById(R.id.home_toolbar_message);
        // TODO 消息通知控件
        message.setVisibility(View.VISIBLE);
        message.setOnClickListener(this);
        message.setVisibility(View.GONE);
        search = (ImageView) findViewById(R.id.home_toolbar_ser);
        search.setOnClickListener(this);
        // TODO 搜索控件
        search.setVisibility(View.GONE);
        addNews = (ImageView) findViewById(R.id.home_toolbar_add);
        addNews.setOnClickListener(this);
        // TODO 消息红点提示
        message_Red = (TextView) findViewById(R.id.home_toolbar_message_red);
        message_Red.setVisibility(View.GONE);
        // 消息接受
        handlers = new Handlers();
        // 下拉刷新控件
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.news_srl);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.color01, R.color.color02, R.color.color03, R.color.color04);
        list_view = (RecyclerView) findViewById(R.id.news_list);
        list_view.setLayoutManager(new LinearLayoutManager(this));
        // 检查用户缓存信息
        checkBmobUserCache();
        // 最外层layout
        four_word_input = (LinearLayout) findViewById(R.id.four_word_hide);
        four_word_input.setVisibility(View.GONE);

        four_word_ed = (EditText) findViewById(R.id.four_word_input);
        four_word_btn = (Button) findViewById(R.id.four_word_btn_send);
        four_word_btn.setOnClickListener(this);

        newsAdapter = new NewsAdapter(datalist, this, list_view);
        list_view.setAdapter(newsAdapter);
        adapterListener();
    }

    /**
     * 列表监听
     */
    private void adapterListener() {
        // 广播长按事件
        newsAdapter.setRecyclerViewOnLongClick(new DataPort.RecyclerViewOnItemLongClick() {
            @Override
            public void onItemLongClick(View view, int position, FrameLayout layout) {
                if (AppContext.getINSTANCE().Colse_NEWSPARTICLE) return;
                boolean temp = datalist.get(position).isshowdata();
                datalist.get(position).setIsshowdata(!temp);
                mExplosionField.explode(view);
                if (!AppContext.getINSTANCE().SHOW_FWCOMMENT) return;
                if (temp)
                    layout.setVisibility(View.GONE);
                else
                    layout.setVisibility(View.VISIBLE);
            }
        });

        // 广播点击事件
        newsAdapter.setRecyclerViewOnItemClick(new DataPort.RecyclerViewOnItemClick() {
            @Override
            public void onItemClick(View view, int position) {
                if (AppContext.getINSTANCE().LOGIN) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("data", datalist.get(position));
                    goToActivity(Activity_ShowNewsItem.class, bundle, false);
                }
            }

            @Override
            public void onNewsFourWord(FrameLayout canvas, int position) {
                if (!AppContext.getINSTANCE().LOGIN) {
                    toast(getResources().getString(R.string.home_unable));
                    return;
                }
                Activity_Home.this.canvas = canvas;
                showInput(position);
            }
        });
        // 点击图片事件
        newsAdapter.setClickImage(new DataPort.onClickImage() {
            @Override
            public void onClick(View view, int position) {
                showHightImage(position);
            }
        });

        //判断并加载更多
        if (datalist.size() < BmobDataHandle.LIMITNEWSNUMBER) {
            datalist.add(null);
            newsAdapter.isNoMore(true);
            newsAdapter.notifyDataSetChanged();
        } else {
            newsAdapter.setLoadMoreListener(new DataPort.loadMoreListener() {
                @Override
                public void load() {
                    if (!newsAdapter.isNoMore()) {
                        datalist.add(null);
                        // 判断没有网络的情况
                        if (!AppContext.getINSTANCE().isConnection) {
                            newsAdapter.notifyDataSetChanged();
                            return;
                        }
                        newsAdapter.notifyDataSetChanged();
                        reLoadData(true);
                    }
                }
            });
        }
    }

    /**
     * 显示输入键盘
     */
    private void showInput(int i) {
        newsId = datalist.get(i).getNews_id();
        Temp_Position = i;
        four_word_ed.setFocusable(true);
        four_word_ed.setFocusableInTouchMode(true);
        four_word_ed.requestFocus();
        four_word_ed.findFocus();
        KeyBoardUtils.openKeybord(four_word_ed, this);
        four_word_input.setVisibility(View.VISIBLE);
    }


    /**
     * 获取用户缓存数据
     */
    private void checkBmobUserCache() {
        Bmob_MyUser myUser = BmobUser.getCurrentUser(Bmob_MyUser.class);
        if (myUser != null) {
            updateMyDataUI(true, myUser);
            AppContext.getINSTANCE().LOGIN = true;
            AppContext.getINSTANCE().UserAccount = myUser.getUsername();
        } else {
            updateMyDataUI(false, null);
            AppContext.getINSTANCE().LOGIN = false;
        }
        // 判断用户当前网络状态
        updataUIfromNetChange();
    }

    /**
     * 更新用户数据
     *
     * @param islogin
     * @param myUser
     */
    private void updateMyDataUI(boolean islogin, Bmob_MyUser myUser) {
        if (islogin) {
            if (myUser.getHeader_thumbnail_url() != null) {
                header.setImageURI(myUser.getHeader_thumbnail_url());
            }
            username.setText(myUser.getShowname());
            //user_singtrue.setText(myUser.getSingtrue());
        } else {
            username.setText(R.string.home_mydata_show01);
            //user_singtrue.setText(R.string.home_mydata_show02);
            title.setText(R.string.home_no_login);
        }
    }

    @Override
    public void loadData() {
        // 加载本地数据
        FileUtil<Bean_News> fileUtil = new FileUtil<>();
        if (fileUtil.getFile() != null) {
            if(!datalist.isEmpty()){
                datalist.clear();
            }
            if (fileUtil.getFile().size() == 0) {
                no_news_show.setVisibility(View.VISIBLE);
            } else {
                no_news_show.setVisibility(View.GONE);
            }
            for (int i = 0; i < fileUtil.getFile().size(); i++) {
                datalist.add(fileUtil.getFile().get(i));
            }
            newsAdapter.notifyDataSetChanged();
        }

        // 加载配置数据
        SharedPreferences sp = getSharedPreferences("setting", 0);
        AppContext.getINSTANCE().SHOUND = sp.getBoolean("SHOUND", true);
        AppContext.getINSTANCE().SHOW_FWCOMMENT = sp.getBoolean("SHOW_FWCOMMENT", true);
        AppContext.getINSTANCE().Colse_NEWSPARTICLE = sp.getBoolean("Colse_NEWSPARTICLE", true);
        AppContext.getINSTANCE().VIBARTION = sp.getBoolean("VIBARTION", true);

        // 打开软件自动加载数据
        if (AppContext.getINSTANCE().isConnection) {
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(true);
                }
            });
            this.onRefresh();
        }
    }

    /**
     * 显示大图
     */
    public void showHightImage(int i) {
        if (largeImageDisPlay == null) {
            largeImageDisPlay = new FBLargeImageDisPlay(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            addContentView(largeImageDisPlay.getRootView(), params);
            largeImageDisPlay.setUrl(datalist.get(i).getNews_image_url(), datalist.get(i).getNews_image_name());
            largeImageDisPlay.Show();
        } else {
            largeImageDisPlay.setUrl(datalist.get(i).getNews_image_url(), datalist.get(i).getNews_image_name());
            largeImageDisPlay.Show();
        }
    }

    @Override
    public void setContentView() {
        // 图片缓存框架
        Fresco.initialize(this);
        // 布局
        setContentView(R.layout.activity_main);
        // 注册网络变化广播
        registerReceiver();
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.four_word_btn_send) {
            sendFourWord();
        } else if (id == R.id.home_toolbar_message) {
            if (!AppContext.getINSTANCE().LOGIN) {
                toast(getResources().getString(R.string.home_unable));
                return;
            }
            message_Red.setVisibility(View.INVISIBLE);
            goToActivityForResult(Activity_Message.class, null);
        } else if (id == R.id.home_toolbar_ser) {
            toast("搜索");
        } else if (id == R.id.home_toolbar_add) {
            if (!AppContext.getINSTANCE().LOGIN) {
                toast(getResources().getString(R.string.home_unable));
                return;
            }
            goToActivityForResult(Activity_UploadNews.class, null);
        }
    }

    /**
     * 发送四字评论
     */
    private void sendFourWord() {
        if (!AppContext.getINSTANCE().isConnection) {
            toast("没有网络连接呢!");
            return;
        }
        int i = four_word_ed.getText().toString().length();
        if (i > 0 && i <= 4) {
            final BombNewsFourWord word = new BombNewsFourWord();
            word.setAccount(AppContext.getINSTANCE().UserAccount);
            word.setId(newsId);
            word.setContent(four_word_ed.getText().toString());
            word.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    if (e == null) {
                        newsAdapter.addFourWords(canvas, four_word_ed.getText().toString());
                        // 添加到数据列表
                        datalist.get(Temp_Position).getShowdata().add(word);
                        // 关闭键盘
                        KeyBoardUtils.closeKeybord(four_word_ed, Activity_Home.this);
                        // 清空上次输入
                        four_word_ed.setText("");
                    } else {
                        AppContext.getINSTANCE().showBmobException(Activity_Home.this, e);
                    }
                }
            });
        } else if (i >= 5) {
            toast("不能超过四个字呢！");
        } else {
            toast("不能发送空评论呢！");
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            goToActivityForResult(Activity_MyNews.class, null);
        } else if (id == R.id.nav_gallery) {
            OffersManager.getInstance(this).onAppLaunch();
            OffersManager.getInstance(this).showOffersWall(new Interface_ActivityListener() {
                @Override
                public void onActivityDestroy(Context context) {
                    OffersManager.getInstance(context).onAppExit();
                }
            });
        } else if (id == R.id.nav_slideshow) {
            goToActivityForResult(Activity_Setting.class, null);
        } else if (id == R.id.nav_manage) {
            goToActivity(Activity_About.class, null, false);
        } else if (id == R.id.nav_help) {
            Bundle bundle = new Bundle();
            bundle.putInt("position", 2);
            bundle.putString("title", "帮助");
            goToActivity(Activity_Web.class, bundle, false);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        // drawer.
        return true;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        // 获取返回键
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (largeImageDisPlay != null && largeImageDisPlay.getRootView() != null && largeImageDisPlay.getRootView().getVisibility() == View.VISIBLE) {
                largeImageDisPlay.hide();
            } else {
                ExitApp();
            }
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        menu.add(Menu.NONE, Menu.FIRST + 1, 5, "退出").setIcon(
//
//                android.R.drawable.ic_lock_power_off);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case Menu.FIRST + 5:
                finish();
                break;
        }
        return false;
    }

    //    /**
//     * 保存时间差
//     */
//    private long exitTime = 0;

    /**
     * 退出
     */
    public void ExitApp() {
//        if ((System.currentTimeMillis() - exitTime) > 2000) {
//            Toast.makeText(Activity_Home.this, "再按一次退出", Toast.LENGTH_SHORT).show();
//            exitTime = System.currentTimeMillis();
//        } else {
        moveTaskToBack(true);
        //      }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == CommandUtil.RESULCODE_REFRESH) {
            reLoadData(false);
        }
        if (requestCode == 0 && resultCode == CommandUtil.RESULCODE_UPDATECACHE) {
            checkBmobUserCache();
        }
        if (requestCode == 0 && resultCode == CommandUtil.RESULCODE_FINISH) {
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRefresh() {
        reLoadData(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        four_word_input.addOnLayoutChangeListener(this);
        if (newsAdapter != null)
            newsAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(myReceiver);
    }
}
