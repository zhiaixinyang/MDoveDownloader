package com.suapp.dcdownloader.system.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by zhaojing on 2018/3/21.
 */

public class DateUtils {

    public static final long SECOND_IN_MILLIS = 1000;
    public static final long MINUTE_IN_MILLIS = SECOND_IN_MILLIS * 60;
    public static final long HOUR_IN_MILLIS = MINUTE_IN_MILLIS * 60;
    public static final long DAY_IN_MILLIS = HOUR_IN_MILLIS * 24;
    public static final long WEEK_IN_MILLIS = DAY_IN_MILLIS * 7;
    public static final long MONTH_IN_MILLIS = DAY_IN_MILLIS * 30;

    /**
     * This constant is actually the length of 364 days, not of a year!
     */
    public static final long YEAR_IN_MILLIS = WEEK_IN_MILLIS * 52;
    private static ThreadLocal<DateFormat> DEFAULT_DATE_FORMAT = new ThreadLocalDateFormat("yyyy-MM-dd HH:mm:ss");

    public static class ThreadLocalDateFormat extends ThreadLocal<DateFormat> {
        private String mDatePattern;

        public ThreadLocalDateFormat(String datePattern) {
            super();
            mDatePattern = datePattern;
        }

        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat(mDatePattern, Locale.US);
        }
    }

    public static String formatDefault(long time) {
        return DEFAULT_DATE_FORMAT.get().format(new Date(time));
    }

    public static String simpleFormat(@Nullable String timeZone, long time, @NonNull String datePattern) {
        Date date = new Date(time);
        DateFormat format = new SimpleDateFormat(datePattern);
        if (timeZone != null) {
            format.setTimeZone(TimeZone.getTimeZone(timeZone));
        }
        return format.format(date);
    }

    public static String simpleFormat(long time, @NonNull String datePattern) {
        Date date = new Date(time);
        DateFormat format = new SimpleDateFormat(datePattern);
        return format.format(date);
    }

    public static String simpleFormat(@NonNull String datePattern) {
        Date date = new Date();
        DateFormat format = new SimpleDateFormat(datePattern);
        return format.format(date);
    }

    public static boolean isInDay(long time) {
        long current = System.currentTimeMillis();
        if (current - time <= DAY_IN_MILLIS && current - time > 0) {
            return true;
        }
        return false;
    }

    /**
     * 计算给定时间戳距离现在有多少天了
     *
     * @param timeStamp mills
     * @return 天数，以24小时计算
     */
    public static int getNumDaysFrom(long timeStamp) {
        long currentTimeMillis = System.currentTimeMillis();
        if (timeStamp > currentTimeMillis) {
            return 0;
        }
        long interval = currentTimeMillis - timeStamp;
        //向上取整
        return (int) Math.ceil(interval / (double) DAY_IN_MILLIS);
    }

    public static boolean isSameDay(long time1, long time2) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(time1));
        int day1 = calendar.get(Calendar.DAY_OF_YEAR);
        calendar.setTime(new Date(time2));
        int day2 = calendar.get(Calendar.DAY_OF_YEAR);
        return day1 == day2;
    }

    public static boolean isToday(long time) {
        return isSameDay(time, System.currentTimeMillis());
    }

    public static int getHourOfDay() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.HOUR_OF_DAY);
    }

    public static int getMinute() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.MINUTE);
    }

    public static int getDayInMonth() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取默认时区，默认Locale de week，默认时间戳来自System.currentTimeMillis()
     *
     * @return
     */
    public static int getDayOfWeek() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * 获取指定 timezone，时间戳的week
     *
     * @param timeZone  {@link TimeZone#getDisplayName()}
     * @param timestamp the new time in UTC milliseconds from the epoch.
     * @return
     */
    public static int getDayOfWeek(String timeZone, long timestamp) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(timeZone));
        calendar.setTimeInMillis(timestamp);
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * 获取指定 timeZone,timestamp, 默认Locale 的月份
     *
     * @param timeZone  {@link TimeZone#getDisplayName()}
     * @param timestamp the new time in UTC milliseconds from the epoch.
     * @param abbrev
     * @return
     */
    public static String getSimpleMonth(String timeZone, long timestamp, boolean abbrev) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(timeZone));
        calendar.setTimeInMillis(timestamp);
        int month = calendar.get(Calendar.MONTH);
        return parseMonth(month, abbrev);
    }

    /**
     * 获取默认时区，默认Locale 的月份，默认时间戳来自System.currentTimeMillis()
     *
     * @param abbrev
     * @return
     */
    public static String getSimpleMonth(boolean abbrev) {
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH);
        return parseMonth(month, abbrev);
    }

    private static String parseMonth(int month, boolean abbrev) {
        switch (month) {
            case Calendar.JANUARY:
                return abbrev ? "Ja." : "January";
            case Calendar.FEBRUARY:
                return abbrev ? "Feb." : "February";
            case Calendar.MARCH:
                return abbrev ? "Mar." : "March";
            case Calendar.APRIL:
                return abbrev ? "Apr." : "April";
            case Calendar.MAY:
                return abbrev ? "May." : "May";
            case Calendar.JUNE:
                return abbrev ? "Jun." : "June";
            case Calendar.JULY:
                return abbrev ? "Jul." : "July";
            case Calendar.AUGUST:
                return abbrev ? "Aug." : "August";
            case Calendar.SEPTEMBER:
                return abbrev ? "Sep." : "September";
            case Calendar.OCTOBER:
                return abbrev ? "Oct." : "October";
            case Calendar.NOVEMBER:
                return abbrev ? "Nov." : "November";
            case Calendar.DECEMBER:
                return abbrev ? "Dec." : "December";
            default:
                return "";
        }
    }

    /**
     * 获取默认时区，默认 Locale 的 week，时间戳来源 System.currentTimeMillis()
     *
     * @param abbrev 是否缩略
     * @return
     */
    public static String getSimpleWeek(boolean abbrev) {
        Calendar calendar = Calendar.getInstance();
        int weekday = calendar.get(Calendar.DAY_OF_WEEK);
        return parseWeek(weekday, abbrev);
    }

    private static String parseWeek(int weekday, boolean abbrev) {
        String dayInWeek;
        switch (weekday) {
            case Calendar.SUNDAY:
                dayInWeek = abbrev ? "Sun" : "Sunday";
                break;
            case Calendar.MONDAY:
                dayInWeek = abbrev ? "Mon" : "Monday";
                break;
            case Calendar.TUESDAY:
                dayInWeek = abbrev ? "Tue" : "Tuesday";
                break;
            case Calendar.WEDNESDAY:
                dayInWeek = abbrev ? "Wed" : "Wednesday";
                break;
            case Calendar.THURSDAY:
                dayInWeek = abbrev ? "Thu" : "Thursday";
                break;
            case Calendar.FRIDAY:
                dayInWeek = abbrev ? "Fri" : "Friday";
                break;
            case Calendar.SATURDAY:
                dayInWeek = abbrev ? "Sat" : "Saturday";
                break;
            default:
                dayInWeek = abbrev ? "Sun" : "Sunday";
                break;
        }
        return dayInWeek;
    }

    /**
     * 根据timeZone 和 timestamp 获取 week
     *
     * @param timeZone
     * @param timestamp the new time in UTC milliseconds from the epoch.
     * @param abbrev    是否缩略
     * @return
     */
    public static String getSimpleWeek(@Nullable String timeZone, long timestamp, boolean abbrev) {
        if (TextUtils.isEmpty(timeZone)) {
            timeZone = TimeZone.getDefault().getDisplayName();
        }
        int dayOfWeek = getDayOfWeek(timeZone, timestamp);
        return parseWeek(dayOfWeek, abbrev);
    }
}
