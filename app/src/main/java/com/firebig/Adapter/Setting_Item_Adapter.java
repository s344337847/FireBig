package com.firebig.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.firebig.activity.R;
import com.firebig.bean.Bean_Setting_item;
import com.firebig.util.CommandUtil;
import com.firebig.util.DataPort;

import java.util.List;

/**
 * Created by FireBig-CH on 16-10-17.
 */
public class Setting_Item_Adapter extends BaseAdapter {

    private List<Bean_Setting_item> data;
    private Context context;
    private DataPort.onSwitchChecked checked;

    public Setting_Item_Adapter(Context context, List<Bean_Setting_item> bean_setting_items) {
        this.data = bean_setting_items;
        this.context = context;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.setting_layout_item, null);
            holder.title = (TextView) convertView.findViewById(R.id.setting_title_text01);
            holder.item_text01 = (TextView) convertView.findViewById(R.id.setting_text01);
            holder.item_text02 = (TextView) convertView.findViewById(R.id.setting_text02);
            holder.item_text03 = (TextView) convertView.findViewById(R.id.setting_text03);
            holder.item_switch = (Switch) convertView.findViewById(R.id.setting_switch);
            for (int i = 0; i < holder.ids.length; i++) {
                holder.layouts[i] = (LinearLayout) convertView.findViewById(holder.ids[i]);
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        for (int i = 0; i < holder.ids.length; i++) {
            holder.layouts[i].setVisibility(View.GONE);
            if (data.get(position).getSetting_Show_Mode() == i) {
                holder.layouts[i].setVisibility(View.VISIBLE);
            }
        }

        holder.title.setText(data.get(position).getTitle());
        holder.item_text01.setText(data.get(position).getTitle());
        if (data.get(position).isHide_depict()) {
            holder.item_text02.setVisibility(View.GONE);
        } else {
            holder.item_text02.setVisibility(View.VISIBLE);
        }
        holder.item_text02.setText(data.get(position).getItem_depict());
        holder.item_text03.setText(data.get(position).getTitle());
        holder.item_switch.setChecked(data.get(position).isSw());
        holder.item_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (checked != null)
                    checked.Change(position, isChecked);
            }
        });
        return convertView;
    }

    public class ViewHolder {
        public TextView title;
        public TextView item_text01;
        public TextView item_text02;
        public TextView item_text03;
        public Switch item_switch;
        public int[] ids = {R.id.setting_title, R.id.setting_item_02, R.id.setting_item_04};
        public LinearLayout[] layouts = new LinearLayout[ids.length];
    }

    /**
     * 开关回调接口
     *
     * @param checked
     */
    public void setonSwitchCheckedChanged(DataPort.onSwitchChecked checked) {
        this.checked = checked;
    }


}
