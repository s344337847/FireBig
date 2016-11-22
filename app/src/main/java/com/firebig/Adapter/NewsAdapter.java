package com.firebig.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.firebig.Data.BmobDataHandle;
import com.firebig.Data.FBImageLoader;
import com.firebig.activity.BaseActivity;
import com.firebig.activity.R;
import com.firebig.app.AppContext;
import com.firebig.bean.Bean_News;
import com.firebig.bean.BombNewsFourWord;
import com.firebig.util.DataPort;
import com.firebig.util.StringUtils;
import com.firebig.widget.FBFrameLayout;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by FireBig-CH on 16-8-17.
 */
public class NewsAdapter extends RecyclerView.Adapter implements View.OnClickListener, View.OnLongClickListener {

    private static final int VIEW_ITEM = 0;
    private static final int VIEW_INIT = 1;
    /**
     * 列表数据
     **/
    private List<Bean_News> datalist;
    /**
     * 上下文
     **/
    private Context context;
    /**
     * 长按回调
     **/
    private DataPort.RecyclerViewOnItemLongClick recyclerViewOnLongClick = null;
    /**
     * 点击回调
     **/
    private DataPort.RecyclerViewOnItemClick recyclerViewOnItemClick = null;
    /**
     *
     */
    private RecyclerView mrecyclerView;
    private MyHandler handler;

    /**
     * 是否加载中
     */
    private boolean isLoading;

    /**
     * 当前列表总数
     */
    private int totalItemCount;

    /**
     * 当前底部Item位置
     */
    private int lastVisibleItemPosition;

    /**
     * 加载更多接口
     */
    private DataPort.loadMoreListener loadMoreListener;

    /**
     * 图片点击
     */
    private DataPort.onClickImage clickImage;

    /**
     * 删除按钮出来的layout
     */
    private DataPort.setMyNewsDeleteOnClick deleteLayoutListener;

    /**
     * 滑动监听接口
     */
    private DataPort.addOnScroll addOnScroll;

    /**
     * 没有更多了
     */
    private boolean isNoMore = false;
    /**
     * 是否显示脚
     */
    private boolean isMoreShow = true;


    private class MyHandler extends Handler {

        public void handleMessage(Message message) {
            if (message.what == 0)
                update();
        }
    }

    /**
     * 更新列表
     */
    public void update() {
        this.notifyDataSetChanged();
    }

    /**
     * 构造器
     *
     * @param datalist
     * @param context
     */
    public NewsAdapter(List<Bean_News> datalist, Context context, final RecyclerView view) {
        this.datalist = datalist;
        this.context = context;
        handler = new MyHandler();
        this.mrecyclerView = view;
        // 实现滑动到底部到时候进行加载更多
        if (view.getLayoutManager() instanceof LinearLayoutManager) {
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) view.getLayoutManager();
            view.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        int lastVisiblePosition = linearLayoutManager.findLastVisibleItemPosition();
                        if (!isLoading && lastVisiblePosition >= linearLayoutManager.getItemCount() - 1) {
                            isLoading = true;
                            if (loadMoreListener != null)
                                loadMoreListener.load();
                        }
                    }
                    if (newState == RecyclerView.SCROLL_STATE_SETTLING) {
                        if (addOnScroll != null)
                            addOnScroll.onScroll();
                    }
                }
            });
        }
    }

    /**
     * 加载完成后设置
     */
    public void setLoaded() {
        isLoading = false;
    }

    /**
     * 没有更多了
     *
     * @param isNoMore
     */
    public void isNoMore(boolean isNoMore) {
        this.isNoMore = isNoMore;
    }

    public boolean isNoMore() {
        return isNoMore;
    }

    public boolean isMoreShow() {
        return isMoreShow;
    }

    public void setMoreShow(boolean moreShow) {
        isMoreShow = moreShow;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 图片缓存框架
        Fresco.initialize(context);
        RecyclerView.ViewHolder holder = null;
        if (viewType == VIEW_ITEM) {
            View view = LayoutInflater.from(context).inflate(R.layout.news_list_itemc, parent, false);
            holder = new NewsViewHolder(view);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        } else if (viewType == VIEW_INIT) {
            holder = new NewsItemFooter(LayoutInflater.from(context).inflate(R.layout.item_footer, parent, false));
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof NewsViewHolder) {
            // 设置Tag 用于定位哪个Item点击的
            holder.itemView.setTag(R.id.tag_first, position);
            // 判断是否显示删除视图
            if (datalist.get(position).isshowDelete()) {
                // TODO
                ((NewsViewHolder) holder).delete_layout.setVisibility(View.VISIBLE);
                ((NewsViewHolder) holder).delete_btn.setTag(R.id.tag_first, position);
                ((NewsViewHolder) holder).delete_btn.setOnClickListener(this);
                ((NewsViewHolder) holder).delete_btn.startAnimation(AnimationUtils.loadAnimation(context, R.anim.mynews_item_option_menu_show));
                ((NewsViewHolder) holder).cancle_btn.setTag(R.id.tag_first, position);
                ((NewsViewHolder) holder).cancle_btn.setOnClickListener(this);
                ((NewsViewHolder) holder).cancle_btn.startAnimation(AnimationUtils.loadAnimation(context, R.anim.mynews_item_option_menu_show));
            } else {
                // TODO 隐藏删除视图
                ((NewsViewHolder) holder).delete_layout.setVisibility(View.GONE);
                ((NewsViewHolder) holder).delete_btn.setOnClickListener(null);
                ((NewsViewHolder) holder).cancle_btn.setOnClickListener(null);
            }
            // 设置用户名
            if (datalist.get(position).getShowname() != null)
                ((NewsViewHolder) holder).username.setText(datalist.get(position).getShowname());
            else
                ((NewsViewHolder) holder).username.setText(datalist.get(position).getUsername());
            // 判断当前广播是不是3天前的广播，如果是在时间后面添加“其他人不可见”
            if (StringUtils.isOutTime(datalist.get(position).getTime()))
                // 显示时间(时间经过处理,使其显示更友好)
                ((NewsViewHolder) holder).time.setText(StringUtils.friendly_time(datalist.get(position).getTime()) + "(其他人不可见)");
            else
                ((NewsViewHolder) holder).time.setText(StringUtils.friendly_time(datalist.get(position).getTime()));
            // 显示广播内容（最多150字符）
            ((NewsViewHolder) holder).news_content.setText(datalist.get(position).getNews_content());
            // 添加四字评论按钮
            ((NewsViewHolder) holder).four_word_btn.setOnClickListener(this);
            // 加载头像
            if (datalist.get(position).getHeader_thumbnail_url() != null) {
                ((NewsViewHolder) holder).header.setVisibility(View.VISIBLE);
                ((NewsViewHolder) holder).header.setImageURI(Uri.parse(datalist.get(position).getHeader_thumbnail_url()));
            } else {
                ((NewsViewHolder) holder).header.setVisibility(View.GONE);
            }
            // 加载图片
            if (datalist.get(position).getThumbnail_url() != null) {
                ((NewsViewHolder) holder).news_image.setVisibility(View.VISIBLE);
                ((NewsViewHolder) holder).news_image.setImageURI(Uri.parse(datalist.get(position).getThumbnail_url()));
                ((NewsViewHolder) holder).news_image.setTag(R.id.tag_three, position);
                ((NewsViewHolder) holder).news_image.setOnClickListener(this);
            } else {
                ((NewsViewHolder) holder).news_image.setVisibility(View.GONE);
            }

            // 设置Tag：ID为R.id.tag_second,把四字评论主Layout返回HomeActivity 方便破碎效果的实现
            holder.itemView.setTag(R.id.tag_second, ((NewsViewHolder) holder).showcomment);
            ((NewsViewHolder) holder).four_word_btn.setTag(R.id.tag_first, position);
            ((NewsViewHolder) holder).four_word_btn.setTag(R.id.tag_second, ((NewsViewHolder) holder).showcomment);

            // 设置四字评论按钮的可见性
            if (!datalist.get(position).isFour_word())
                ((NewsViewHolder) holder).four_word.setVisibility(View.GONE);
            else
                ((NewsViewHolder) holder).four_word.setVisibility(View.VISIBLE);

            // 设置四字评论的可见性
            if (!AppContext.getINSTANCE().SHOW_FWCOMMENT) {
                ((NewsViewHolder) holder).showcomment.setVisibility(View.INVISIBLE);
            } else {
                ((NewsViewHolder) holder).showcomment.setVisibility(View.VISIBLE);
                // 加载显示四字评论
                if (datalist.get(position).getShowdata() != null) {
                    ((NewsViewHolder) holder).showcomment.setVisibility(View.VISIBLE);
                    ((NewsViewHolder) holder).showcomment.show(datalist.get(position).getShowdata());
                } else {
                    ((NewsViewHolder) holder).showcomment.setVisibility(View.INVISIBLE);
                }
            }
        } else if (holder instanceof NewsItemFooter) {
            if (!isMoreShow) {
                ((NewsItemFooter) holder).no_more.setVisibility(View.INVISIBLE);
                ((NewsItemFooter) holder).bar.setVisibility(View.INVISIBLE);
            }
            // 显示网络连接失败
            if (!AppContext.getINSTANCE().isConnection) {
                ((NewsItemFooter) holder).bar.setVisibility(View.INVISIBLE);
                ((NewsItemFooter) holder).no_more.setVisibility(View.VISIBLE);
                ((NewsItemFooter) holder).textView.setText("网络连接失败");
            }
            // 底部显示没有更多了
            if (isNoMore && isMoreShow) {
                ((NewsItemFooter) holder).bar.setVisibility(View.INVISIBLE);
                ((NewsItemFooter) holder).no_more.setVisibility(View.VISIBLE);
                ((NewsItemFooter) holder).textView.setText("没有更多了");
            }
            // 设置加载提示
            if(isLoading && !isNoMore){
                ((NewsItemFooter) holder).bar.setVisibility(View.VISIBLE);
                ((NewsItemFooter) holder).no_more.setVisibility(View.INVISIBLE);
                ((NewsItemFooter) holder).bar.setIndeterminate(true);
            }
        }
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

    /**
     * 添加四字评论
     *
     * @param text 文字
     */
    public void addFourWords(FrameLayout canvas, String text) {
        TextView view = new TextView(context);
        view.setText(text);
        view.setX(getrondom(canvas.getWidth() - 100));
        view.setY(getrondom(canvas.getHeight() - 50));
        view.setGravity(Gravity.CENTER);
        view.setScaleX(0.7f);
        view.setScaleY(0.7f);
        view.setBackgroundResource(R.drawable.textstyle_red);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        canvas.addView(view, params);
    }

    @Override
    public int getItemViewType(int position) {
        return datalist.get(position) != null ? VIEW_ITEM : VIEW_INIT;
    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }

    @Override
    public void onClick(View view) {
        // 点击右下角图标事件
        if (view.getId() == R.id.news_four_word) {
            if (recyclerViewOnItemClick != null) {
                recyclerViewOnItemClick.onNewsFourWord((FrameLayout) view.getTag(R.id.tag_second), (int) view.getTag(R.id.tag_first));
            }
            return;
        }

        // 点击图片事件
        if (view.getId() == R.id.news_image) {
            if (clickImage != null) {
                clickImage.onClick(view, (int) view.getTag(R.id.tag_three));
            }
            return;
        }

        // 我的广播页面删除按钮点击事件
        if (view.getId() == R.id.item_option_menu_delete) {
            if (deleteLayoutListener != null)
                deleteLayoutListener.deleteOnClick((int) view.getTag(R.id.tag_first));
            return;
        }

        // 我的广播页面取消按钮点击事件
        if (view.getId() == R.id.item_option_menu_cancle) {
            if (deleteLayoutListener != null)
                deleteLayoutListener.cancleOnClick((int) view.getTag(R.id.tag_first));
            return;
        }

        // 点击广播事件
        if (recyclerViewOnItemClick != null) {
            recyclerViewOnItemClick.onItemClick(view, (int) view.getTag(R.id.tag_first));
        }
    }

    @Override
    public boolean onLongClick(View view) {
        // 长按事件
        if (recyclerViewOnLongClick != null) {
            recyclerViewOnLongClick.onItemLongClick(view, (int) view.getTag(R.id.tag_first), (FrameLayout) view.getTag(R.id.tag_second));
        }
        return true;
    }

    /**
     * 点击监听
     *
     * @param recyclerViewOnItemClick
     */
    public void setRecyclerViewOnItemClick(DataPort.RecyclerViewOnItemClick recyclerViewOnItemClick) {
        this.recyclerViewOnItemClick = recyclerViewOnItemClick;
    }

    /**
     * 长按监听
     *
     * @param recyclerViewOnLongClick
     */
    public void setRecyclerViewOnLongClick(DataPort.RecyclerViewOnItemLongClick recyclerViewOnLongClick) {
        this.recyclerViewOnLongClick = recyclerViewOnLongClick;
    }

    /**
     * 加载更多监听
     *
     * @param loadMoreListener
     */
    public void setLoadMoreListener(DataPort.loadMoreListener loadMoreListener) {
        this.loadMoreListener = loadMoreListener;
    }

    /**
     * 点击图片监听
     *
     * @param clickImage
     */

    public void setClickImage(DataPort.onClickImage clickImage) {
        this.clickImage = clickImage;
    }

    /**
     * 我的广播页面删除视图监听
     *
     * @param deleteLayoutListener
     */
    public void setDeleteLayoutListener(DataPort.setMyNewsDeleteOnClick deleteLayoutListener) {
        this.deleteLayoutListener = deleteLayoutListener;
    }

    /**
     * 滑动监听
     *
     * @param addOnScroll
     */
    public void addOnScrllListener(DataPort.addOnScroll addOnScroll) {
        this.addOnScroll = addOnScroll;
    }

    /**
     * item 点击和评论按钮监听接口
     */
    public class NewsViewHolder extends RecyclerView.ViewHolder {

        SimpleDraweeView header;
        TextView username;
        TextView news_content;
        TextView time;
        /**
         * 广播图片
         */
        SimpleDraweeView news_image;
        ImageView four_word;
        ImageView four_word_btn;
        /**
         * 四字评论显示layout
         */
        FBFrameLayout showcomment;
        /**
         * 长按显示删除按钮layout
         */
        RelativeLayout delete_layout;
        /**
         * 删除按钮
         */
        LinearLayout delete_btn;
        /**
         * 取消按钮
         */
        LinearLayout cancle_btn;

        /**
         * @param view
         */

        public NewsViewHolder(View view) {
            super(view);
            header = (SimpleDraweeView) view.findViewById(R.id.news_header);
            username = (TextView) view.findViewById(R.id.news_username);
            news_content = (TextView) view.findViewById(R.id.news_content);
            time = (TextView) view.findViewById(R.id.news_time);
            news_image = (SimpleDraweeView) view.findViewById(R.id.news_image);
            showcomment = (FBFrameLayout) view.findViewById(R.id.showcomment);
            four_word = (ImageView) view.findViewById(R.id.news_four_word);
            four_word_btn = (ImageView) view.findViewById(R.id.news_four_word);
            delete_layout = (RelativeLayout) view.findViewById(R.id.mynews_item_option_menu_layout);
            delete_btn = (LinearLayout) view.findViewById(R.id.item_option_menu_delete);
            cancle_btn = (LinearLayout) view.findViewById(R.id.item_option_menu_cancle);
        }
    }

    class NewsItemFooter extends RecyclerView.ViewHolder {
        ProgressBar bar;
        LinearLayout no_more;
        TextView textView;

        public NewsItemFooter(View itemView) {
            super(itemView);
            bar = (ProgressBar) itemView.findViewById(R.id.news_item_footer_pb);
            no_more = (LinearLayout) itemView.findViewById(R.id.footer_no_more);
            textView = (TextView) itemView.findViewById(R.id.no_more_text);
        }
    }
}
