package com.firebig.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.firebig.Adapter.Setting_Item_Adapter;
import com.firebig.app.AppContext;
import com.firebig.bean.Bean_Setting_item;
import com.firebig.util.CommandUtil;
import com.firebig.util.DataPort;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by FireBig-CH on 16-8-17.
 */
public class Activity_Setting extends BaseActivity {

    private ListView setting_list;
    private Setting_Item_Adapter adapter;
    private List<Bean_Setting_item> data = new ArrayList<>();
    private String[] save_name_commam = {"", "SHOUND", "VIBARTION", "", "SHOW_FWCOMMENT", "Colse_NEWSPARTICLE", ""};

    @Override
    public void initUI() {
        setting_list = (ListView) findViewById(R.id.setting_list);
    }

    @Override
    public void loadData() {
        // load setting.xml
        SharedPreferences sp = getSharedPreferences("setting", Context.MODE_PRIVATE);
        String[] str = getResources().getStringArray(R.array.setting_list);
        int[] listshowModes = {0, 1, 1, 0, 1, 1, 2};
        for (int i = 0; i < str.length; i++) {
            Bean_Setting_item item_data = new Bean_Setting_item();
            item_data.setSetting_Show_Mode(listshowModes[i]);
            item_data.setTitle(str[i]);
            data.add(item_data);
            data.get(i).setSw(sp.getBoolean(save_name_commam[i], true));
        }
        data.get(4).setHide_depict(false);
        data.get(5).setHide_depict(false);

        data.get(4).setItem_depict(getResources().getString(R.string.setting_dec01));
        data.get(5).setItem_depict(getResources().getString(R.string.setting_dec02));

        adapter = new Setting_Item_Adapter(this, data);
        setting_list.setAdapter(adapter);
        setting_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0 || position == 3)
                    return;
                if (position == 6) {
                    clearCache();
                    return;
                }
                data.get(position).setSw(!data.get(position).isSw());
                onCheckedChanged(position, data.get(position).isSw());
                adapter.notifyDataSetChanged();
            }
        });

        adapter.setonSwitchCheckedChanged(new DataPort.onSwitchChecked() {
            @Override
            public void Change(int position, boolean isChecked) {
                onCheckedChanged(position, isChecked);
            }
        });

    }

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_setting);
        setToolBar(R.string.nva_setting);
    }

    @Override
    public void onClick(View view) {

    }

    /**
     * clear App cache including picture and web cache
     */
    private void clearCache() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("确认要清除缓存吗？");
        builder.setTitle("提示");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // TODO clear Cache
                dialogInterface.dismiss();
                toast("清除成功！");
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }

    public void onCheckedChanged(int position, boolean b) {
        saveSharedPreferencesCache(getSharedPreferences("setting", 0), save_name_commam[position], b);
        if (position == 1) {
            AppContext.getINSTANCE().SHOUND = b;
        }
        if (position == 2) {
            AppContext.getINSTANCE().VIBARTION = b;
        }
        if (position == 4) {
            AppContext.getINSTANCE().SHOW_FWCOMMENT = b;
        }
        if (position == 5) {
            AppContext.getINSTANCE().Colse_NEWSPARTICLE = b;
        }
    }

}
