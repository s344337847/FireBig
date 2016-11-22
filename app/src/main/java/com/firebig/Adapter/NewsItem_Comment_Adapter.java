package com.firebig.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.firebig.activity.R;
import com.firebig.bean.Bean_NewsItem_Comment;
import com.firebig.bean.BmobNewsComment;
import com.firebig.util.DataPort;
import com.firebig.util.StringUtils;

import java.util.List;

/**
 * Created by FireBig-CH on 16-9-7.
 * 广播评论包括四字评论数据设配器
 */
public class NewsItem_Comment_Adapter extends BaseAdapter {

    private List<BmobNewsComment> data;
    private Context context;
    private DataPort.replayList replayList_onclick;

    public NewsItem_Comment_Adapter(List<BmobNewsComment> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.newsitem_comment_list_item, null);
            holder.header = (SimpleDraweeView) view.findViewById(R.id.comment_list_item_headers);
            holder.username = (TextView) view.findViewById(R.id.comment_list_item_username);
            holder.time = (TextView) view.findViewById(R.id.comment_list_item_time);
            holder.contents = (TextView) view.findViewById(R.id.comment_list_item_contents);
            holder.replayList = (TextView) view.findViewById(R.id.comment_list_item_replaylist);
            holder.replayList.setVisibility(View.GONE);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        // 头像
        if (data.get(i).getAvater_thumbnailUrl() != null) {
            holder.header.setImageURI(Uri.parse(data.get(i).getAvater_thumbnailUrl()));
        }
        // 用户名
        if (data.get(i).getUsername() != null)
            holder.username.setText(data.get(i).getUsername());
        else
            holder.username.setText(data.get(i).getAccount());
        // 时间
        if (data.get(i).getTime() == null)
            holder.time.setText(StringUtils.friendly_time(data.get(i).getCreatedAt()));
        else
            holder.time.setText(StringUtils.friendly_time(data.get(i).getTime()));
        // 内容
        if (data.get(i).getToaccount() != null) {
            String contents = "回复@" + data.get(i).getToaccount() + ":" + data.get(i).getContent();
            SpannableString spannableString = new SpannableString(contents);
            //setSpan时需要指定的 flag,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE(前后都不包括).
            spannableString.setSpan(new ForegroundColorSpan(Color.RED), 2, data.get(i).getToaccount().length() + 3,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.contents.setText(spannableString);
        } else {
            holder.contents.setText(data.get(i).getContent());
        }
        return view;
    }

    public class ViewHolder {
        public SimpleDraweeView header;
        public TextView username;
        public TextView time;
        public TextView contents;
        public TextView replayList;
    }
}
