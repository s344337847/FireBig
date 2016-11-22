package com.firebig.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebig.activity.R;
import com.firebig.bean.Bean_Message;
import com.firebig.util.DataPort;
import com.firebig.util.StringUtils;

import java.util.List;

/**
 * Created by FireBig-CH on 16-9-19.
 */
public class MessageNoticeAdapter extends RecyclerView.Adapter implements View.OnClickListener {

    private Context context;
    private List<Bean_Message> messages;
    private DataPort.noticeOnItemClick onItemClick;

    public MessageNoticeAdapter(Context context, List<Bean_Message> messages) {
        this.context = context;
        this.messages = messages;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.message_notice_list_item, parent, false);
        RecyclerView.ViewHolder holder = new NoticeItemView(view);
        view.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        NoticeItemView itemView = (NoticeItemView) holder;
        // 设置Tag 用于定位哪个Item点击的
        itemView.itemView.setTag(R.id.tag_first, position);
        itemView.account.setText(messages.get(position).getFrom_name() + "回复了您:");
        itemView.message.setText(messages.get(position).getMessage());
        itemView.notice_message_reply_btn.setOnClickListener(this);
        itemView.notice_message_reply_btn.setTag(R.id.tag_second, position);
        itemView.time.setText(StringUtils.friendly_time(messages.get(position).getTime()));
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class NoticeItemView extends RecyclerView.ViewHolder {
        private TextView account;
        private TextView message;
        private TextView time;
        /**
         * 回复按钮
         */
        private Button notice_message_reply_btn;

        public NoticeItemView(View view) {
            super(view);
            account = (TextView) view.findViewById(R.id.notice_message_title);
            message = (TextView) view.findViewById(R.id.notice_message);
            time = (TextView) view.findViewById(R.id.notice_message_time);
            notice_message_reply_btn = (Button) view.findViewById(R.id.notice_message_reply_btn);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.notice_message_reply_btn) {
            if (onItemClick != null) {
                onItemClick.onMessageClick(view, (int) view.getTag(R.id.tag_second));
            }
            return;
        }
        if (onItemClick != null)
            onItemClick.onItemClick(view, (int) view.getTag(R.id.tag_first));
    }

    /**
     * 监听
     *
     * @param onItemClick
     */
    public void setOnClick(DataPort.noticeOnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

}
