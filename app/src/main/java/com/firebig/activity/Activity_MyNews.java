package com.firebig.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebig.Adapter.NewsAdapter;
import com.firebig.Data.BmobDataHandle;
import com.firebig.Data.FileUtil;
import com.firebig.app.AppContext;
import com.firebig.bean.Bean_News;
import com.firebig.bean.BmobNews;
import com.firebig.bean.BmobNewsComment;
import com.firebig.bean.Bmob_MyUser;
import com.firebig.bean.BombNewsFourWord;
import com.firebig.util.CommandUtil;
import com.firebig.util.DataPort;
import com.firebig.util.StringUtils;
import com.firebig.widget.FBLargeImageDisPlay;
import com.firebig.widget.FBMoveTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by FireBig-CH on 16-8-17.
 * 我的广播
 */
public class Activity_MyNews extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {
    /**
     * 下拉刷新
     */
    private SwipeRefreshLayout refreshLayout;
    /**
     * 列表
     */
    private RecyclerView recyclerView;
    /**
     * 设配器
     */
    private NewsAdapter newsAdapter;
    /**
     * 消息处理
     */
    private Handlers handlers;
    /**
     * 数据
     */
    public List<Bean_News> datalist = new ArrayList<>();
    /**
     * 没有网络提示
     */
    private LinearLayout no_Net_show;

    /**
     * 我的本地资料
     */
    private Bmob_MyUser myUser_data;

    /**
     * 没有网络或者没有发布广播提示
     */
    private TextView mynews_newsitem_no_text;

    /**
     * 跟layout
     */
    private RelativeLayout mynews_rootView;

    /**
     * 加载提示
     */
    private ProgressDialog dialog;

    /**
     * 显示大图
     */
    private FBLargeImageDisPlay largeImageDisPlay;

    public class Handlers extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            refreshLayout.setRefreshing(false);
            loadFromBmob(false);
        }
    }

    @Override
    public void initUI() {
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.mynews_sfl);
        recyclerView = (RecyclerView) findViewById(R.id.mynews_list);
        no_Net_show = (LinearLayout) findViewById(R.id.newsitem_no_news_show);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(R.color.color01, R.color.color02, R.color.color03, R.color.color04);
        handlers = new Handlers();
        mynews_newsitem_no_text = (TextView) findViewById(R.id.mynews_newsitem_no_text);
        myUser_data = BmobUser.getCurrentUser(Bmob_MyUser.class);
        mynews_rootView = (RelativeLayout) findViewById(R.id.mynews_rootView);
        dialog = new ProgressDialog(this);
    }

    @Override
    public void loadData() {
        // 打开软件自动加载数据
        if (AppContext.getINSTANCE().isConnection) {
            refreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    refreshLayout.setRefreshing(true);
                }
            });
            this.onRefresh();
        }
    }

    /**
     * @param isSkip
     */
    public void loadFromBmob(final boolean isSkip) {
        // 从服务器加载数据,每次加载30条
        if (AppContext.getINSTANCE().isConnection) {
            BmobQuery<BmobNews> query = new BmobQuery<>();
            query.addWhereEqualTo("account", myUser_data.getUsername());
            if (isSkip)
                query.setSkip(datalist.size());
            query.setLimit(BmobDataHandle.LIMITNEWSNUMBER);
            query.findObjects(new FindListener<BmobNews>() {
                @Override
                public void done(List<BmobNews> list, BmobException e) {
                    refreshLayout.setRefreshing(false);
                    if (e == null) {
                        if (isSkip) {
                            addItemToList(list);
                        } else {
                            showToList(list);
                        }
                    } else {
                        AppContext.getINSTANCE().showBmobException(Activity_MyNews.this, e);
                    }
                }
            });
        } else {
            no_Net_show.setVisibility(View.VISIBLE);
            mynews_newsitem_no_text.setText(R.string.no_net);
            refreshLayout.setRefreshing(false);
        }
    }

    /**
     * 显示列表数据
     *
     * @param list 数据
     */
    private void showToList(List<BmobNews> list) {
        if (list.size() == 0) {
            no_Net_show.setVisibility(View.VISIBLE);
            mynews_newsitem_no_text.setText(R.string.no_news);
            return;
        }
        if (!datalist.isEmpty())
            datalist.clear();
        if (datalist.isEmpty()) {
            for (int i = 0; i < list.size(); i++) {
                Bean_News bean_news = new Bean_News();
                bean_news.setHeader_thumbnail_url(myUser_data.getHeader_thumbnail_url());
                bean_news.setUsername(AppContext.getINSTANCE().UserAccount);
                bean_news.setShowname(myUser_data.getShowname());
                bean_news.setTime(list.get(i).getCreatedAt());
                bean_news.setNews_content(list.get(i).getNewscontent());
                bean_news.setThumbnail_url(list.get(i).getThumbnail_url());
                if (list.get(i).getNewsimage() != null) {
                    bean_news.setNews_image_url(list.get(i).getNewsimage().getFileUrl());
                    bean_news.setNews_image_name(list.get(i).getNewsimage().getFilename());
                }
                bean_news.setNews_id(list.get(i).getObjectId());
                datalist.add(bean_news);
            }
            newsAdapter = new NewsAdapter(datalist, this, recyclerView);
            recyclerView.setAdapter(newsAdapter);
            newsAdapter.notifyDataSetChanged();
            for (int i = 0; i < datalist.size(); i++) {
                datalist.get(i).setIsshowdata(false);
                datalist.get(i).setFour_word(false);
                newsAdapter.notifyDataSetChanged();
            }

            // 长按监听
            newsAdapter.setRecyclerViewOnLongClick(new DataPort.RecyclerViewOnItemLongClick() {
                @Override
                public void onItemLongClick(View view, int position, FrameLayout layout) {
                    datalist.get(position).setIsshowDelete(true);
                    newsAdapter.notifyDataSetChanged();
                }
            });
            //判断并加载更多
            if (datalist.size() < BmobDataHandle.LIMITNEWSNUMBER) {
                datalist.add(null);
                newsAdapter.isNoMore(true);
                newsAdapter.notifyDataSetChanged();
            } else {
                // 滑动到底部监听
                newsAdapter.setLoadMoreListener(new DataPort.loadMoreListener() {
                    @Override
                    public void load() {
                        if (!newsAdapter.isNoMore()) {
                            datalist.add(null);
                            if (!AppContext.getINSTANCE().isConnection) {
                                newsAdapter.notifyDataSetChanged();
                                return;
                            }
                            newsAdapter.notifyDataSetChanged();
                            loadFromBmob(true);
                        }
                    }
                });
            }

            newsAdapter.setDeleteLayoutListener(new DataPort.setMyNewsDeleteOnClick() {
                @Override
                public void deleteOnClick(int position) {
                    deleteToBmob(position);
                }

                @Override
                public void cancleOnClick(int position) {
                    datalist.get(position).setIsshowDelete(false);
                    newsAdapter.notifyDataSetChanged();
                }
            });

            newsAdapter.setRecyclerViewOnItemClick(new DataPort.RecyclerViewOnItemClick() {
                @Override
                public void onItemClick(View view, int position) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("data", datalist.get(position));
                    goToActivity(Activity_ShowNewsItem.class, bundle, false);
                }

                @Override
                public void onNewsFourWord(FrameLayout layout, int position) {

                }
            });

            newsAdapter.setClickImage(new DataPort.onClickImage() {
                @Override
                public void onClick(View view, int position) {
                    showHightImage(position);
                }
            });
        }
    }

    public void addItemToList(List<BmobNews> list) {
        if (list.size() == 0) {
            newsAdapter.isNoMore(true);
            newsAdapter.notifyDataSetChanged();
            newsAdapter.setLoaded();
            return;
        } else {
            //移除刷新的progressBar
            datalist.remove(datalist.size() - 1);
            newsAdapter.notifyDataSetChanged();
            for (int i = 0; i < list.size(); i++) {
                Bean_News bean_news = new Bean_News();
                bean_news.setHeader_thumbnail_url(myUser_data.getHeader_thumbnail_url());
                bean_news.setUsername(AppContext.getINSTANCE().UserAccount);
                bean_news.setShowname(myUser_data.getShowname());
                bean_news.setTime(list.get(i).getCreatedAt());
                bean_news.setNews_content(list.get(i).getNewscontent());
                bean_news.setThumbnail_url(list.get(i).getThumbnail_url());
                if (list.get(i).getNewsimage() != null) {
                    bean_news.setNews_image_url(list.get(i).getNewsimage().getFileUrl());
                    bean_news.setNews_image_name(list.get(i).getNewsimage().getFilename());
                }
                bean_news.setNews_id(list.get(i).getObjectId());
                datalist.add(bean_news);
            }
            newsAdapter.isNoMore(false);
            newsAdapter.notifyDataSetChanged();
            newsAdapter.setLoaded();
            return;
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
        setContentView(R.layout.activity_mynews);
        setToolBar(R.string.nva_mynews);
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onRefresh() {
        loadFromBmob(false);
    }

    /**
     * 删除服务器端的数据(包括图片数据) 逻辑：先删除列表数据－－－》删除缩略图－－－》删除大图－－－》更新ＵＩ
     *
     * @param position
     */
    public void deleteToBmob(final int position) {
        dialog.setMessage("正在删除...");
        dialog.show();
        BmobNews bmobNews = new BmobNews();
        bmobNews.setObjectId(datalist.get(position).getNews_id());
        bmobNews.delete(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    // 主界面刷新
                    setResult(CommandUtil.RESULCODE_REFRESH);
                    if (datalist.get(position).getThumbnail_url() != null) {
                        deleteFileToBmob(datalist.get(position).getThumbnail_url(), position, 0);
                    } else {
                        datalist.remove(position);
                        newsAdapter.notifyDataSetChanged();
                    }
                    dialog.dismiss();
                } else {
                    AppContext.getINSTANCE().showBmobException(Activity_MyNews.this, e);
                    dialog.dismiss();
                }
            }
        });
        deleteFourWord(position);
        deleteComment(position);
    }

    /**
     * 删除图片文件
     *
     * @param Url
     * @param position
     * @param type     删除进度type==0 删除缩略图　type == 1 删除大图
     */
    public void deleteFileToBmob(String Url, final int position, final int type) {
        BmobFile imagefile = new BmobFile();
        imagefile.setUrl(Url);
        imagefile.delete(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    if (type == 0) {
                        if (datalist.get(position).getNews_image_url() != null) {
                            deleteFileToBmob(datalist.get(position).getNews_image_url(), position, 1);
                        } else {
                            datalist.remove(position);
                            newsAdapter.notifyDataSetChanged();
                        }
                        dialog.dismiss();
                    } else {
                        datalist.remove(position);
                        newsAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                } else {
                    AppContext.getINSTANCE().showBmobException(Activity_MyNews.this, e);
                    dialog.dismiss();
                }
            }
        });
    }

    /**
     * 删除四字评论
     *
     * @param position
     */
    private void deleteFourWord(final int position) {
        BombNewsFourWord fourWord = new BombNewsFourWord();
        fourWord.setId(datalist.get(position).getNews_id());
        fourWord.delete(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Log.d("MyNews", "删除成功");
                }
            }
        });
    }

    /**
     * 删除评论
     *
     * @param position
     */
    private void deleteComment(final int position) {
        BmobNewsComment comment = new BmobNewsComment();
        comment.setId(datalist.get(position).getNews_id());
        comment.delete(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Log.d("Comment", "删除成功");
                }
            }
        });
    }
}
