package com.hnhy.ui.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Author:guc
 * Time:2018/5/20
 * Description:常用工具类
 */
public class CommonUtil {
    /**
     * yyyy年MM月dd日  HH:mm E
     */
    public static final String pattern1 = "yyyy年MM月dd日  HH:mm E";
    /**
     * yyyy年MM月dd日  HH:mm
     */
    public static final String pattern2 = "yyyy年MM月dd日  HH:mm";
    /**
     * yyyy-MM-dd
     */
    public static final String pattern3 = "yyyy-MM-dd";

    /**
     * 标准年月 yyyyMM
     */
    public static final String pattern4 = "yyyyMM";

    /**
     * 时分  HHmm
     */
    public static final String pattern5 = "HHmm";
    /**
     * 年月日 yyyyMMdd
     */
    public static final String pattern6 = "yyyyMMdd";
    /**
     * 月-日 MM-dd
     */
    public static final String pattern7 = "MM-dd";
    /**
     * 年月日时分 yyyyMMddHHmm
     */
    public static final String pattern8 = "yyyyMMddHHmm";
    /**
     * 年月日时分 yyyy-MM-dd HH:mm
     */
    public static final String pattern9 = "yyyy-MM-dd HH:mm";
    /**
     * 月日时分 MM-dd HH:mm
     */
    public static final String pattern10 = "MM-dd HH:mm";
    /**
     * 年月日时分秒 yyyyMMddHHmmss
     */
    public static final String pattern11 = "yyyyMMddHHmmss";
    /**
     * yyyy年MM月dd日
     */
    public static final String pattern12 = "yyyy年MM月dd日";
    /**
     * 时分  HH:mm
     */
    public static final String pattern13 = "HH:mm";

    /**
     * 返回当前程序版本名
     */
    public static String GetAppVersionName(Context context) {
        String versionName = "";
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
        }
        return versionName;
    }

    /**
     * @param c Calendar pattern "格式化的时间格式"
     * @return 格式化后的字符串
     */
    public static String getStrFromCalendar(Calendar c, String pattern) {
        String result = "";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        Date date = new Date(c.getTimeInMillis());
        result = sdf.format(date);
        return result;
    }

    /**
     * @param milliseconds 时间毫秒数,pattern 格式化后的格式
     * @return 格式化后的格式
     */
    public static String getStrFromMilliseconds(long milliseconds, String pattern) {
        String result = "";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        Date date = new Date(milliseconds);
        result = sdf.format(date);
        return result;
    }

    /**
     * 将pattern格式的字符串转为Calendar对象
     *
     * @param dateStr "yyyy年MM月dd日  HH:mm E"
     * @return Calendar对象
     */
    public static Calendar getCalendarFromStr(String dateStr, String pattern) {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        Date date = null;
        try {
            date = sdf.parse(dateStr);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (date != null) {
            c.setTime(date);
        }
        return c;
    }

    /**
     * 日期1是否在日期2之前 判断日期
     *
     * @param c1 日期1
     * @param c2 日期2
     * @return
     */
    public static boolean isBefore(Calendar c1, Calendar c2) {
        boolean isBefore = false;
        isBefore = c1.getTimeInMillis() / 1000 / 60 / 60 < c2.getTimeInMillis() / 1000 / 60 / 60;
        return isBefore;
    }

    /**
     * 日期1是否在日期2之前 判断时间
     *
     * @param c1 日期1
     * @param c2 日期2
     * @return
     */
    public static boolean isBeforeTime(Calendar c1, Calendar c2) {
        return c1.getTimeInMillis() < c2.getTimeInMillis();
    }

    /**
     * 从 Unicode 形式的字符串转换成对应的编码的特殊字符串。 如 "\u9EC4" to "黄".
     * <p>
     * Converts encoded \\uxxxx to unicode chars
     * <p>
     * and changes special saved chars to their original forms
     *
     * @param in  Unicode编码的字符数组。
     * @param off 转换的起始偏移量。
     * @param len 转换的字符长度。
     *            <p>
     *            转换的缓存字符数组。
     * @return 完成转换，返回编码前的特殊字符串。
     */

    public static String fromEncodedUnicode(char[] in, int off, int len) {

        char aChar;

        char[] out = new char[len]; // 只短不长

        int outLen = 0;

        int end = off + len;

        String temp = "%";


        while (off < end) {

            aChar = in[off++];

            if (aChar == '%') {

                aChar = in[off++];

                if (aChar == 'u') {//是汉字

                    // Read the xxxx

                    int value = 0;

                    for (int i = 0; i < 4; i++) {

                        aChar = in[off++];

                        switch (aChar) {

                            case '0':

                            case '1':

                            case '2':

                            case '3':

                            case '4':

                            case '5':

                            case '6':

                            case '7':

                            case '8':

                            case '9':

                                value = (value << 4) + aChar - '0';

                                break;

                            case 'a':

                            case 'b':

                            case 'c':

                            case 'd':

                            case 'e':

                            case 'f':

                                value = (value << 4) + 10 + aChar - 'a';

                                break;

                            case 'A':

                            case 'B':

                            case 'C':

                            case 'D':

                            case 'E':

                            case 'F':

                                value = (value << 4) + 10 + aChar - 'A';

                                break;

                            default:

                                throw new IllegalArgumentException("Malformed \\uxxxx encoding.");

                        }

                    }

                    out[outLen++] = (char) value;

                } else {


                    if (aChar == 't') {
                        aChar = '\t';
                        out[outLen++] = aChar;

                    } else if (aChar == 'r') {

                        aChar = '\r';
                        out[outLen++] = aChar;

                    } else if (aChar == 'n') {

                        aChar = '\n';
                        out[outLen++] = aChar;

                    } else if (aChar == 'f') {

                        aChar = '\f';
                        out[outLen++] = aChar;
                    } else {
                        temp = temp + aChar;
                        aChar = in[off++];
                        temp += aChar;
                        try {
                            temp = URLDecoder.decode(temp, "UTF-8");
                            out[outLen++] = temp.charAt(0);
                        } catch (UnsupportedEncodingException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        temp = "%";
                    }


                }

            } else {

                out[outLen++] = (char) aChar;

            }

        }

        return new String(out, 0, outLen);

    }

    /**
     * 将字符串转为Unicode编码
     *
     * @param string
     * @return
     */
    public static String string2Unicode(String string) {
        char[] utfBytes = string.toCharArray();
        String unicodeBytes = "";
        for (int byteIndex = 0; byteIndex < utfBytes.length; byteIndex++) {
            String hexB = Integer.toHexString(utfBytes[byteIndex]);
            if (hexB.length() <= 2) {
                hexB = "00" + hexB;
            } else if (hexB.length() == 3) {
                hexB = "0" + hexB;
            }
            unicodeBytes = unicodeBytes + "%u" + hexB;
        }
        return unicodeBytes;
    }

    /**
     * 获取百度地图上两个坐标点的距离
     *
     * @param lat1 定位点的纬度
     * @param lon1 定位点的经度
     * @param lat2 目标点的纬度
     * @param lon2 目标点的经度
     * @return
     */
    public static float getDistanceBetweenTwoPoint(double lat1, double lon1, double lat2, double lon2) {

        lat1 = (Math.PI / 180) * lat1;
        lat2 = (Math.PI / 180) * lat2;

        lon1 = (Math.PI / 180) * lon1;
        lon2 = (Math.PI / 180) * lon2;
        //地球半径
        double R = 6371;
        //两点间距离 km，如果想要米的话，结果*1000就可以了
        double d = Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon2 - lon1)) * R;
        //转换成m
        d = d * 1000;
        //保留两位小数
        d = Math.round(d * 100) / 100.0;
        return (float) d;
    }

    /**
     * @param patternFrom 原时间格式
     * @param patternTo   目标时间格式
     * @param dateStr     原时间字符串
     * @return 目标时间字符串
     */
    public static String formExchange(String patternFrom, String patternTo, String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat(patternFrom);
        SimpleDateFormat sdfTo = new SimpleDateFormat(patternTo);
        Date date = null;
        try {
            date = sdf.parse(dateStr);
            return sdfTo.format(date);
        } catch (ParseException e) {
            return dateStr;
        }
    }


}
