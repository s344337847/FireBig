package com.firebig.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.firebig.app.AppContext;
import com.firebig.bean.Bean_Message;
import com.firebig.bean.Bean_News;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by FireBig-CH on 16-8-18.
 */
public class DataUtil {

    private static volatile DataUtil INSTANCE;
    private static Object LOCKINSTANCE = new Object();

    private DataHepler hepler;
    public SQLiteDatabase db;

    /**
     * Singleton pattern
     */
    public static DataUtil DataUtil(Context context) {
        if (INSTANCE == null) {
            synchronized (LOCKINSTANCE) {
                if (INSTANCE == null) {
                    INSTANCE = new DataUtil(context);
                    return INSTANCE;
                }
                return INSTANCE;
            }
        }
        return INSTANCE;
    }

    /**
     * 实例化数据库
     *
     * @param context
     */
    public DataUtil(Context context) {
        hepler = new DataHepler(context, "firebigdb", null, 1, null);
        db = hepler.getWritableDatabase();
    }

    /**
     * get Login account
     *
     * @param account
     * @return Cursor
     */
    public Cursor getAccount(String account) {
        return db.rawQuery("select * from useraccount where account = ?", new String[]{account});
    }

    /**
     * insert account to sqlite
     *
     * @param account
     * @param password
     * @param objectid
     */
    public void insertAccount(String account, String password, String objectid) {
        db.execSQL("insert into useraccount values(?,?,?)", new String[]{account, password, objectid});
    }

    /**
     * update account data
     *
     * @param values
     * @param account
     */
    public void updateAccount(ContentValues values, String account) {
        db.update("useraccount", values, "account = ?", new String[]{account});
    }

    /**
     * delete account data
     *
     * @param account
     */
    public void deleteAccount(String account) {
        db.delete("useraccount", "account = ?", new String[]{account});
    }

    /**
     * 保存广播列表
     *
     * @param bean_news bean
     */
    public void addNewsData(Bean_News bean_news) {
        ContentValues values = new ContentValues();
        values.put("account", AppContext.getINSTANCE().UserAccount);
        values.put("objectid", bean_news.getNews_id());
        values.put("newscontent", bean_news.getNews_content());
        values.put("newsuser", bean_news.getUsername());
        values.put("newstime", bean_news.getTime());
        values.put("listid", bean_news.getListid());
        if (bean_news.isFour_word())
            values.put("fourword", 0);
        else
            values.put("fourword", 1);
        values.put("newsimagename", bean_news.getNews_image_name());
        values.put("newsimageurl", bean_news.getNews_image_url());
        values.put("headerimagename", bean_news.getHeader_name());
        values.put("headerimageurl", bean_news.getHeader_url());
        db.insert("news", null, values);
    }

    /**
     * 获取广播列表
     *
     * @return
     */
    public List<Bean_News> getNewsData() {
        List<Bean_News> list = new ArrayList<>();
        Cursor cursor = db.rawQuery("select *from news where account = ?", new String[]{AppContext.getINSTANCE().UserAccount});
        while (cursor.moveToNext()) {
            cursor.moveToFirst();
            Bean_News news = new Bean_News();
            news.setNews_id(cursor.getString(cursor.getColumnIndex("objectid")));
            news.setUsername(cursor.getString(cursor.getColumnIndex("newsuser")));
            news.setTime(cursor.getString(cursor.getColumnIndex("newstime")));
            news.setNews_content(cursor.getString(cursor.getColumnIndex("newscontent")));
            news.setListid(cursor.getInt(cursor.getColumnIndex("listid")));
            news.setNews_image_name(cursor.getString(cursor.getColumnIndex("newsimagename")));
            news.setNews_image_url(cursor.getString(cursor.getColumnIndex("newsimageurl")));
            news.setHeader_name(cursor.getString(cursor.getColumnIndex("headerimagename")));
            news.setHeader_url(cursor.getString(cursor.getColumnIndex("headerimageurl")));
            if (cursor.getInt(cursor.getColumnIndex("fourword")) == 0) {
                news.setFour_word(true);
            } else {
                news.setFour_word(false);
            }
        }
        return list;
    }

    /**
     * 更新播数据
     */
    public void updateNewsData(Bean_News bean_news) {
        ContentValues values = new ContentValues();
        values.put("objectid", bean_news.getNews_id());
        values.put("newscontent", bean_news.getNews_content());
        values.put("newsuser", bean_news.getUsername());
        values.put("newstime", bean_news.getTime());
        values.put("listid", bean_news.getListid());
        if (bean_news.isFour_word())
            values.put("fourword", 0);
        else
            values.put("fourword", 1);
        values.put("newsimagename", bean_news.getNews_image_name());
        values.put("newsimageurl", bean_news.getNews_image_url());
        values.put("headerimagename", bean_news.getHeader_name());
        values.put("headerimageurl", bean_news.getHeader_url());
        db.update("news", values, "account = ?", new String[]{AppContext.getINSTANCE().UserAccount});
    }

    /**
     * 删除一条广播数据
     *
     * @param objectid
     */
    public void deleteNewsData(String objectid) {
        db.delete("news", "objectid = ?", new String[]{objectid});
    }

    /**
     * 保存消息通知
     *
     * @param bean_message
     */
    public void insertMessageNotice(Bean_Message bean_message) {
        if (AppContext.getINSTANCE().UserAccount == null)
            return;
        ContentValues values = new ContentValues();
        values.put("user_account", AppContext.getINSTANCE().UserAccount);
        values.put("from_account", bean_message.getFrom_account());
        values.put("from_name", bean_message.getFrom_name());
        values.put("news_id", bean_message.getNesw_id());
        values.put("message", bean_message.getMessage());
        values.put("time", bean_message.getTime());
        values.put("type", bean_message.getMESSAGE_TYPE());
        db.insert("notice", null, values);
    }

    /**
     * 获取全部消息通知
     *
     * @return
     */
    public List<Bean_Message> getNotice() {
        if (AppContext.getINSTANCE().UserAccount == null)
            return null;
        List<Bean_Message> list = new ArrayList<>();
        Cursor cursor = db.rawQuery("select *from notice where user_account = ?", new String[]{AppContext.getINSTANCE().UserAccount});
        while (cursor.moveToNext()) {
            Bean_Message message = new Bean_Message();
            message.setFrom_account(cursor.getString(cursor.getColumnIndex("from_account")));
            message.setFrom_name(cursor.getString(cursor.getColumnIndex("from_name")));
            message.setNesw_id(cursor.getString(cursor.getColumnIndex("news_id")));
            message.setMessage(cursor.getString(cursor.getColumnIndex("message")));
            message.setTime(cursor.getString(cursor.getColumnIndex("time")));
            message.setMESSAGE_TYPE(cursor.getInt(cursor.getColumnIndex("type")));
            list.add(message);
        }
        for (int i = list.size() - 1; i >= 0; i--) {
            list.set(i, list.get(list.size() - 1 - i));
        }
        return list;
    }


}
