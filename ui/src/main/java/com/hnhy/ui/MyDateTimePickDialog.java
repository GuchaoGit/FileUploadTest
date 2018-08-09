package com.hnhy.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.hnhy.ui.util.CommonUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Author:guc
 * Time:2018/5/20
 * Description:选择日期时间控件
 */
public class MyDateTimePickDialog implements DatePicker.OnDateChangedListener,
        TimePicker.OnTimeChangedListener {

    private static String[] displayvalue = new String[]{"00", "30"};
    private Context mContext;
    private AlertDialog.Builder b;
    private AlertDialog dialog;
    private TextView mTitle;
    private DatePicker dp;
    private TimePicker tp;
    private int year;
    private int monthOfYear;
    private int dayOfMonth;
    private int hourOfDay;
    private int minute;
    private Calendar c;
    private OnDateTimeSetListener mListener;
    private String mPattern;


    /**
     * 指定选择时间格式
     *
     * @param context
     * @param c
     * @param pattern
     */
    public MyDateTimePickDialog(Context context, Calendar c, String pattern) {
        this(context, c);
        if (!TextUtils.isEmpty(pattern)) {
            mPattern = pattern;
        }
    }

    public MyDateTimePickDialog(Context context, Calendar c) {
        this.mContext = context;
        this.c = c;
        year = c.get(Calendar.YEAR);
        monthOfYear = c.get(Calendar.MONTH);
        dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
        hourOfDay = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);
        mPattern = CommonUtil.pattern9;
        init();
    }

    private void init() {
        View view = LayoutInflater.from(mContext).inflate(
                R.layout.view_common_datetime, null);
        dp = (DatePicker) view.findViewById(R.id.datepicker);
        tp = (TimePicker) view.findViewById(R.id.timepicker);
        tp.setIs24HourView(true);
        resizePikcer(dp, false);
        resizePikcer(tp, true);
        dp.init(year, monthOfYear, dayOfMonth, this);
        tp.setCurrentHour(hourOfDay);
        tp.setCurrentMinute(minute);
        tp.setOnTimeChangedListener(this);
        b = new AlertDialog.Builder(mContext);
        b.setTitle("请选择时间");
        b.setView(view);
        b.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Toast.makeText(
                // mContext,
                // "" + year + "-" + (monthOfYear + 1) + "-" + dayOfMonth
                // + " " + hourOfDay + ":" + minute,
                // Toast.LENGTH_SHORT).show();
                if (null != mListener) {
                    mListener.onDateTimeSet(true, c, dp, tp, year, monthOfYear,
                            dayOfMonth, hourOfDay, minute, CommonUtil.getStrFromCalendar(c, mPattern).equals("") ? "请选择时间" : CommonUtil.getStrFromCalendar(c, mPattern));
                }
            }
        });
        b.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                if (null != mListener) {
                    mListener.onDateTimeSet(false, c, dp, tp, year, monthOfYear,
                            dayOfMonth, hourOfDay, minute, CommonUtil.getStrFromCalendar(c, mPattern).equals("") ? "请选择时间" : CommonUtil.getStrFromCalendar(c, mPattern));
                }
            }
        });
        dialog = b.create();
        dialog.show();

    }

    @Override
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        // TODO Auto-generated method stub
        this.tp = view;
        this.hourOfDay = hourOfDay;
//        this.minute = Integer.valueOf(displayvalue[minute]);
        this.minute = minute;
        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, this.minute);
        dialog.setTitle(CommonUtil.getStrFromCalendar(c, mPattern).equals("") ? "请选择时间" : CommonUtil.getStrFromCalendar(c, mPattern));
    }

    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
        // TODO Auto-generated method stub
        this.dp = view;
        this.year = year;
        this.monthOfYear = monthOfYear;
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, monthOfYear);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        dialog.setTitle(CommonUtil.getStrFromCalendar(c, mPattern).equals("") ? "请选择时间" : CommonUtil.getStrFromCalendar(c, mPattern));
    }

    public void setOnDateTimeSetListener(OnDateTimeSetListener listener) {
        this.mListener = listener;
    }

    /**
     * 调整FrameLayout大小
     *
     * @param tp
     */
    private void resizePikcer(FrameLayout tp, boolean isTimePicker) {
        List<NumberPicker> npList = findNumberPicker(tp);
        for (NumberPicker np : npList) {
            resizeNumberPicker(np);
        }
//        if (isTimePicker) {
//            NumberPicker mMinuteSpinner = npList.get(npList.size()-1);
//            mMinuteSpinner.setMinValue(0);
//            mMinuteSpinner.setMaxValue(1);
//            mMinuteSpinner.setDisplayedValues(displayvalue);
//            mMinuteSpinner.setOnLongPressUpdateInterval(100);
//        }
    }

    /*
     * 调整numberpicker大小
     */
    private void resizeNumberPicker(NumberPicker np) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dp2px(mContext, 50.0f),
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10, 0, 10, 0);
        np.setLayoutParams(params);
    }

    /**
     * 得到viewGroup里面的numberpicker组件
     *
     * @param viewGroup
     * @return
     */
    private List<NumberPicker> findNumberPicker(ViewGroup viewGroup) {
        List<NumberPicker> npList = new ArrayList<NumberPicker>();
        View child = null;
        if (null != viewGroup) {
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                child = viewGroup.getChildAt(i);
                if (child instanceof NumberPicker) {
                    npList.add((NumberPicker) child);
                } else if (child instanceof LinearLayout) {
                    List<NumberPicker> result = findNumberPicker((ViewGroup) child);
                    if (result.size() > 0) {
                        return result;
                    }
                }
            }
        }
        return npList;
    }

    /**
     * @param context
     * @param dp
     * @return dp转换为px
     */
    private int dp2px(Context context, float dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale);
    }

    public interface OnDateTimeSetListener {
        void onDateTimeSet(Boolean isSet, Calendar c, DatePicker dp, TimePicker tp,
                           int year, int monthOfYear, int dayOfMonth, int hourOfDay,
                           int minute, String dateStr);
    }
}
