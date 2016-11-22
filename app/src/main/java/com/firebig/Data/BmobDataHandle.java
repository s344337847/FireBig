package com.firebig.Data;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.firebig.Adapter.NewsItem_Comment_Adapter;
import com.firebig.app.AppContext;
import com.firebig.bean.Bean_News;
import com.firebig.bean.BmobNews;
import com.firebig.bean.BmobNewsComment;
import com.firebig.bean.Bmob_MyUser;
import com.firebig.bean.BombNewsFourWord;
import com.firebig.util.DataPort;
import com.firebig.util.StringUtils;
import com.firebig.widget.FBFrameLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;

/**
 * Created by FireBig on 16-8-20.
 * 处理Bmob数据类
 */
public class BmobDataHandle {
    /**
     * 单例模式
     */
    public static volatile BmobDataHandle INSTANCE;
    private static Object LOCKINSTANCE = new Object();

    private Context context;
    /**
     * 广播内容
     */
    public List<Bean_News> datalist = new ArrayList<>();
    /**
     * 广播数量
     */
    public int index = 0;
    /**
     * 加载评论接口
     */
    public DataPort.loadCommentListener listener;

    /**
     * 每次加载评论最多数量
     */
    public static final int LIMITNUMBER = 10;

    /**
     * 每次加载广播最多数量
     */
    public static final int LIMITNEWSNUMBER = 10;

    /**
     * 单例 双重锁定
     *
     * @return
     */
    public static BmobDataHandle getInstance() {
        if (INSTANCE == null) {
            synchronized (LOCKINSTANCE) {
                if (INSTANCE == null) {
                    INSTANCE = new BmobDataHandle();
                    return INSTANCE;
                }
                return INSTANCE;
            }
        }
        return INSTANCE;
    }

    // 接口
    private DataPort.getDataListener getDataListener;
    private DataPort.loadFourWord loadCommentFourWord;

    /**
     * 刷新数据，从Bmob获取数据
     *
     * @param isSetSkip 跳过数据（跳过已经加载到数据，如当前加载30条数据，设置跳过为true，int skip为30，就会不加载前面30条数据）
     * @param skip      跳过的数量
     */
    public void getData(boolean isSetSkip, int skip) {
        if (!datalist.isEmpty())
            datalist.clear();
        index = 0;
        getNowTime(isSetSkip, skip);
    }

    public void setContext(Context context) {
        this.context = context;
    }

    /**
     * 获取服务器时间，手机当前时间不一定准确，须从服务器获取时间
     */
    private void getNowTime(final boolean isSetSkip, final int skip) {
        Bmob.getServerTime(new QueryListener<Long>() {
            @Override
            public void done(Long aLong, BmobException e) {
                if (e == null) {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    String times = formatter.format(new Date(aLong * 1000L));
                    start(times, isSetSkip, skip);
                } else
                    AppContext.getINSTANCE().showBmobException(context, e);
            }
        });
    }

    /**
     * 加载三天内广播数据
     *
     * @param time      时间
     * @param isSetSkip
     * @param skip
     */
    private void start(String time, final boolean isSetSkip, int skip) {
        BmobQuery<BmobNews> query = new BmobQuery<BmobNews>();
        // 获取三天前的日期
        String start = StringUtils.getDay(time) + " 00:00:00";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = sdf.parse(start);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        query.addWhereGreaterThanOrEqualTo("createdAt", new BmobDate(date));
        query.order("-createdAt");
        if (isSetSkip)
            query.setSkip(skip);
        query.setLimit(LIMITNEWSNUMBER);
        query.findObjects(new FindListener<BmobNews>() {
            @Override
            public void done(List<BmobNews> list, BmobException e) {
                if (e == null) {
                    index = list.size();
                    // 如果数据为0，返回
                    if (index == 0) {
                        // 数据回调
                        if (getDataListener != null)
                            getDataListener.done(datalist, isSetSkip);
                        if (isSetSkip)
                            return;
                        // 保存数据到本地
                        if (!datalist.isEmpty()) {
                            FileUtil<Bean_News> fileUtil = new FileUtil<>();
                            fileUtil.saveFile(datalist);
                        }
                        return;
                    }
                    for (int i = 0; i < index; i++) {
                        Bean_News data = new Bean_News();
                        data.setTime(list.get(i).getCreatedAt());
                        data.setUsername(list.get(i).getAccount());
                        data.setNews_content(list.get(i).getNewscontent());
                        data.setListid(list.get(i).getListid());
                        data.setNews_id(list.get(i).getObjectId());
                        data.setFour_word(list.get(i).isFour_word_state());
                        data.setThumbnail_url(list.get(i).getThumbnail_url());
                        // 广播图片
                        if (list.get(i).getNewsimage() != null) {
                            data.setNews_image_name(list.get(i).getNewsimage().getFilename());
                            data.setNews_image_url(list.get(i).getNewsimage().getFileUrl());
                        } else {
                            data.setNews_image_name(null);
                        }
                        datalist.add(data);
                        loadFourWord(i, isSetSkip);
                    }

                } else {
                    if (getDataListener != null)
                        getDataListener.error();
                    AppContext.getINSTANCE().showBmobException(context, e);
                }
            }
        });
    }

    /**
     * 获取信息,头像,用户名
     *
     * @param i 位置
     */
    public void getPrivateData(final int i, final boolean isloadmore) {
        BmobQuery<Bmob_MyUser> query = new BmobQuery<>();
        query.addWhereEqualTo("username", datalist.get(i).getUsername());
        query.findObjects(new FindListener<Bmob_MyUser>() {
            @Override
            public void done(List<Bmob_MyUser> list, BmobException e) {
                // 获取广播头像
                if (e == null) {
                    for (Bmob_MyUser myUser : list) {
                        datalist.get(i).setShowname(myUser.getShowname());
                        if (list.get(0).getAvater() != null) {
                            datalist.get(i).setHeader_name(myUser.getAvater().getFilename());
                            datalist.get(i).setHeader_url(myUser.getAvater().getFileUrl());
                            datalist.get(i).setHeader_thumbnail_url(myUser.getHeader_thumbnail_url());
                        } else {
                            datalist.get(i).setHeader_name(null);
                        }
                    }

                } else {
                    if (getDataListener != null)
                        getDataListener.error();
                    AppContext.getINSTANCE().showBmobException(context, e);
                }
                datalist.set(i, datalist.get(i));
                if (datalist.size() == index) {
                    // 数据回调
                    if (getDataListener != null)
                        getDataListener.done(datalist, isloadmore);
                    if (isloadmore)
                        return;
                    FileUtil<Bean_News> fileUtil = new FileUtil<>();
                    fileUtil.saveFile(datalist);
                    return;
                }

            }
        });
    }

//    /**
//     * 获取头像资料
//     *
//     * @param account
//     */
//    public void getAvater(final int position, String account, final int type) {
//        BmobQuery<Bmob_MyUser> query = new BmobQuery<>();
//        query.addWhereEqualTo("username", account);
//        query.findObjects(new FindListener<Bmob_MyUser>() {
//            @Override
//            public void done(List<Bmob_MyUser> list, BmobException e) {
//                // 获取评论头像数据
//                if (e == null) {
//                    if (loadAvater != null)
//                        loadAvater.privateData(list, position, type);
//                } else {
//                    AppContext.getINSTANCE().showBmobException(context, e);
//                }
//            }
//        });
//    }

    /**
     * 加载广播四字评论
     */
    public void loadFourWord(final int i, final boolean isloadmore) {
        BmobQuery<BombNewsFourWord> query = new BmobQuery<>();
        query.addWhereEqualTo("id", datalist.get(i).getNews_id());
//        boolean isCache = query.hasCachedResult(BombNewsFourWord.class);
//        if (isCache) {
//            query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);    // 如果有缓存的话，则设置策略为CACHE_ELSE_NETWORK
//        } else {
//            query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);    // 如果没有缓存的话，则设置策略为NETWORK_ELSE_CACHE
//        }
        query.setLimit(FBFrameLayout.MAX_NUMBER);
        query.findObjects(new FindListener<BombNewsFourWord>() {
            @Override
            public void done(List<BombNewsFourWord> list, BmobException e) {
                if (e == null) {
                    List<BombNewsFourWord> fourWords = new ArrayList<BombNewsFourWord>();
                    if (list.size() != 0) {
                        for (int k = 0; k < list.size(); k++) {
                            fourWords.add(list.get(k));
                        }
                        Bean_News bean_news = datalist.get(i);
                        bean_news.setShowdata(fourWords);
                        datalist.set(i, bean_news);
                        getPrivateData(i, isloadmore);
                    } else {
                        getPrivateData(i, isloadmore);
                    }

                } else {
                    if (getDataListener != null)
                        getDataListener.error();
                    AppContext.getINSTANCE().showBmobException(context, e);
                }

            }
        });
    }

    /**
     * 加载评论
     *
     * @param id         广播识别ID
     * @param isSkip     是否跳过前面的数据
     * @param SkipNumber 跳过的数量
     */
    public void loadComment(@NonNull String id, @NonNull final boolean isSkip, @NonNull int SkipNumber) {
        BmobQuery<BmobNewsComment> query = new BmobQuery<>();
        query.addWhereEqualTo("id", id);
        query.order("-createdAt");
        if (isSkip)
            query.setSkip(SkipNumber);
        query.setLimit(LIMITNUMBER);
        query.findObjects(new FindListener<BmobNewsComment>() {
            @Override
            public void done(final List<BmobNewsComment> list, BmobException e) {
                if (e == null) {
                    if (list.size() == 0) {
                        if (listener != null)
                            listener.done(list, isSkip);
                    }
                    for (int i = 0; i < list.size(); i++) {
                        final int j = i;
                        BmobQuery<Bmob_MyUser> query = new BmobQuery<>();
                        query.addWhereEqualTo("username", list.get(i).getAccount());
                        query.findObjects(new FindListener<Bmob_MyUser>() {
                            @Override
                            public void done(List<Bmob_MyUser> avater_data, BmobException e) {
                                // 获取评论头像数据
                                if (e == null) {
                                    list.get(j).setUsername(avater_data.get(0).getShowname());
                                    if (avater_data.get(0).getHeader_thumbnail_url() != null) {
                                        list.get(j).setAvater_thumbnailUrl(avater_data.get(0).getHeader_thumbnail_url());
                                    }
                                    // 获取评论者头像
                                    if (avater_data.get(0).getAvater() != null) {
                                        list.get(j).setAvaterUrl(avater_data.get(0).getAvater().getFileUrl());
                                    }
                                    if (j == (list.size() - 1)) {
                                        if (listener != null)
                                            listener.done(list, isSkip);
                                    }
                                } else {
                                    if (listener != null)
                                        listener.error();
                                    AppContext.getINSTANCE().showBmobException(context, e);
                                }
                            }
                        });
                    }
                } else {
                    if (listener != null)
                        listener.error();
                    AppContext.getINSTANCE().showBmobException(context, e);
                }
            }
        });
        if (isSkip)
            return;
        BmobQuery<BmobNewsComment> query1 = new BmobQuery<>();
        query1.addWhereEqualTo("id", id);
        query1.count(BmobNewsComment.class, new CountListener() {
            @Override
            public void done(Integer integer, BmobException e) {
                if (e == null) {
                    if (listener != null)
                        listener.commentNumber(integer);
                    Log.d("TAG", "共查询到" + integer + "条数据");
                } else {
                    if (listener != null)
                        listener.error();
                    AppContext.getINSTANCE().showBmobException(context, e);
                }
            }
        });
    }

    /**
     * 加载四字评论
     */
    public void loadCommentFourWord(@NonNull String newsId, @NonNull final boolean isSkip, @NonNull int SkipNumber) {
        // 加载四字评论数据
        BmobQuery<BombNewsFourWord> query = new BmobQuery<>();
        query.addWhereEqualTo("id", newsId);
        if (isSkip)
            query.setSkip(SkipNumber);
        query.setLimit(LIMITNUMBER);
        query.findObjects(new FindListener<BombNewsFourWord>() {
            @Override
            public void done(final List<BombNewsFourWord> list, BmobException e) {
                if (e == null) {
                    final List<BmobNewsComment> data2 = new ArrayList<>();
                    for (int i = 0; i < list.size(); i++) {
                        BmobNewsComment d = new BmobNewsComment();
                        d.setAccount(list.get(i).getAccount());
                        d.setContent(list.get(i).getContent());
                        d.setTime(list.get(i).getCreatedAt());
                        data2.add(d);
                        final int j = i;
                        BmobQuery<Bmob_MyUser> query = new BmobQuery<>();
                        query.addWhereEqualTo("username", list.get(i).getAccount());
                        query.findObjects(new FindListener<Bmob_MyUser>() {
                            @Override
                            public void done(List<Bmob_MyUser> avater_data, BmobException e) {
                                // 获取评论头像数据
                                if (e == null) {
                                    data2.get(j).setUsername(avater_data.get(0).getShowname());
                                    if (avater_data.get(0).getHeader_thumbnail_url() != null) {
                                        data2.get(j).setAvater_thumbnailUrl(avater_data.get(0).getHeader_thumbnail_url());
                                    }
                                    // 获取评论者头像
                                    if (avater_data.get(0).getAvater() != null) {
                                        data2.get(j).setAvaterUrl(avater_data.get(0).getAvater().getFileUrl());
                                    }
                                    if (j == (list.size() - 1)) {
                                        // TODO 全部加载完成
                                        if (loadCommentFourWord != null)
                                            loadCommentFourWord.done(data2, isSkip);
                                    }
                                } else {
                                    if (loadCommentFourWord != null)
                                        loadCommentFourWord.error();
                                    AppContext.getINSTANCE().showBmobException(context, e);
                                }
                            }
                        });
                    }
                } else {
                    if (loadCommentFourWord != null)
                        loadCommentFourWord.error();
                    AppContext.getINSTANCE().showBmobException(context, e);
                }
            }
        });
        if (isSkip)
            return;
        BmobQuery<BombNewsFourWord> query1 = new BmobQuery<>();
        query1.addWhereEqualTo("id", newsId);
        query1.count(BombNewsFourWord.class, new CountListener() {
            @Override
            public void done(Integer integer, BmobException e) {
                if (e == null) {
                    if (loadCommentFourWord != null)
                        loadCommentFourWord.allFourNumber(integer);
                    Log.d("TAG", "共查询到" + integer + "条数据");
                } else {
                    if (loadCommentFourWord != null)
                        loadCommentFourWord.error();
                    AppContext.getINSTANCE().showBmobException(context, e);
                }
            }
        });
    }

    /**
     * 加载评论监听接口
     *
     * @param listener
     */
    public void setloadCommentListener(DataPort.loadCommentListener listener) {
        this.listener = listener;
    }

    /**
     * 加载广播监听接口
     *
     * @param getDataListener
     */
    public void setgetDataListener(DataPort.getDataListener getDataListener) {
        this.getDataListener = getDataListener;
    }

    /**
     * 加载头像资料监听接口
     */
    public void setLoadFourWordListener(DataPort.loadFourWord loadAvater) {
        this.loadCommentFourWord = loadAvater;
    }


}
