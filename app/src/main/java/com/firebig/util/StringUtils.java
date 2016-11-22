package com.firebig.util;


import android.support.annotation.NonNull;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by root on 16-8-17.
 */

public class StringUtils {
    private static final SimpleDateFormat dateFormater;
    private static final SimpleDateFormat dateFormater2;
    private static final Pattern emailer = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");

    static {
        dateFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormater2 = new SimpleDateFormat("yyyy-MM-dd");
    }

    public static String friendly_time(String paramString) {
        Date localDate = toDate(paramString);
        String str;
        if (localDate == null)
            str = "Unknown";
        int i;
        do {
            str = "";
            Calendar localCalendar = Calendar.getInstance();
            if (dateFormater2.format(localCalendar.getTime()).equals(dateFormater2.format(localDate))) {
                int k = (int) ((localCalendar.getTimeInMillis() - localDate.getTime()) / 3600000L);
                if (k == 0)
                    return Math.max((localCalendar.getTimeInMillis() - localDate.getTime()) / 60000L, 1L) + "分钟前";
                return k + "小时前";
            }
            long l = localDate.getTime() / 86400000L;
            i = (int) (localCalendar.getTimeInMillis() / 86400000L - l);
            if (i == 0) {
                int j = (int) ((localCalendar.getTimeInMillis() - localDate.getTime()) / 3600000L);
                if (j == 0)
                    return Math.max((localCalendar.getTimeInMillis() - localDate.getTime()) / 60000L, 1L) + "分钟前";
                return j + "小时前";
            }
            if (i == 1)
                return "昨天";
            if (i == 2)
                return "前天";
            if ((i > 2) && (i <= 10))
                return i + "天前";
        }
        while (i <= 10);
        return dateFormater2.format(localDate);
    }

    /**
     * 获取系统当前时间
     *
     * @return
     */
    public static String getNowTime() {
        return dateFormater.format(new Date());
    }

    public static int getNowYear() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        return Integer.parseInt(sdf.format(new Date()));
    }

    /**
     * 获取随机一串字符
     *
     * @return
     */
    public static String getUUIDName() {
        String[] arrayOfString = UUID.randomUUID().toString().split("-");
        String str = "";
        for (int i = 0; ; i++) {
            if (i >= arrayOfString.length)
                return str;
            str = str + arrayOfString[i];
        }
    }

    public static boolean isCharAt(String str) {
        if (str.matches("^[A-Za-z]+$")) {
            //是字母
            return true;
        } else {
            //不是字母
            return false;
        }
    }

    /**
     * 判断是不是邮箱
     *
     * @param paramString
     * @return
     */
    public static boolean isEmail(String paramString) {
        if ((paramString == null) || (paramString.trim().length() == 0))
            return false;
        return emailer.matcher(paramString).matches();
    }


    public static boolean toBool(String paramString) {
        try {
            boolean bool = Boolean.parseBoolean(paramString);
            return bool;
        } catch (Exception localException) {
        }
        return false;
    }

    public static Date toDate(String paramString) {
        try {
            Date localDate = dateFormater.parse(paramString);
            return localDate;
        } catch (ParseException localParseException) {
        }
        return null;
    }

    public static int toInt(Object paramObject) {
        if (paramObject == null)
            return 0;
        return toInt(paramObject.toString(), 0);
    }

    public static int toInt(String paramString, int paramInt) {
        try {
            int i = Integer.parseInt(paramString);
            return i;
        } catch (Exception localException) {
        }
        return paramInt;
    }

    public static long toLong(String paramString) {
        try {
            long l = Long.parseLong(paramString);
            return l;
        } catch (Exception localException) {
        }
        return 0L;
    }

    /**
     * 判断广播是否过期
     *
     * @param newsCrateAt
     * @return
     */
    public static boolean isOutTime(String newsCrateAt) {
        Date localDate = toDate(newsCrateAt);
        String str;
        if (localDate == null)
            return false;
        int i = 0;
        Calendar localCalendar = Calendar.getInstance();
        long l = localDate.getTime() / 86400000L;
        i = (int) (localCalendar.getTimeInMillis() / 86400000L - l);
        if ((i >= 3) && (i <= 10))
            return true;
        return false;
    }

    // 过滤特殊字符
    public static String StringFilter(@NonNull String str) {
        // 只允许字母和数字
        //String regEx = "[^a-zA-Z0-9]";
        // 清除掉所有特殊字符
        String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

    // get three day ago
    public static String getDay(String date) {

        String[] tempday = date.split("-");
        int year = Integer.parseInt(tempday[0]);
        int mm = Integer.parseInt(tempday[1]);
        int day = Integer.parseInt(tempday[2]);

        // 判断闰年
        if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
            System.out.println("day:" + (day - 2));
            if ((mm - 1) == 2) {
                if (day == 1) {
                    mm -= 1;
                    day = 28;
                    return year + "-" + mm + "-" + day;
                }
                if (day == 2) {
                    mm -= 1;
                    day = 29;
                    return year + "-" + mm + "-" + day;
                }
            }
        } else {
            System.out.println("day:" + (day - 2));
            if ((mm - 1) == 2) {
                if (day == 1) {
                    mm -= 1;
                    day = 27;
                    return year + "-" + mm + "-" + day;
                }
                if (day == 2) {
                    mm -= 1;
                    day = 28;
                    return year + "-" + mm + "-" + day;
                }
            }
        }

        // 判断大小月
        if ((mm - 1) == 4 || (mm - 1) == 6 || (mm - 1) == 8 || (mm - 1) == 11) { // 30 day
            if (day == 1) {
                mm -= 1;
                day = 29;
                return year + "-" + mm + "-" + day;
            }
            if (day == 2) {
                mm -= 1;
                day = 30;
                return year + "-" + mm + "-" + day;
            }
        } else if ((mm - 1) == 2) { // 29day
        } else { // 31day
            if (day == 1) {
                mm -= 1;
                day = 30;
                return year + "-" + mm + "-" + day;
            }
            if (day == 2) {
                mm -= 1;
                day = 31;
                return year + "-" + mm + "-" + day;
            }
        }
        return year + "-" + mm + "-" + (day - 2);
    }
}
