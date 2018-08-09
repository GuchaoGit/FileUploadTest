package com.hnhy.framework.util;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hnhy.framework.R;

/**
 * Created by guc on 2018/6/12.
 * 描述：水印工具
 */
public class WaterMarkUtil {

    public static String mWaterMarkDesc;

    /**
     * 显示水印布局
     *
     * @param activity
     */
    public static boolean showWatermarkView(final Activity activity) {
        final ViewGroup rootView = activity.getWindow().getDecorView().findViewById(android.R.id.content);
        View framView = LayoutInflater.from(activity).inflate(R.layout.layout_watermark, null);
        if (!TextUtils.isEmpty(mWaterMarkDesc)) {
            ((TextView) framView.findViewById(R.id.tv_1)).setText(mWaterMarkDesc);
            ((TextView) framView.findViewById(R.id.tv_2)).setText(mWaterMarkDesc);
            ((TextView) framView.findViewById(R.id.tv_3)).setText(mWaterMarkDesc);
            ((TextView) framView.findViewById(R.id.tv_4)).setText(mWaterMarkDesc);
            ((TextView) framView.findViewById(R.id.tv_5)).setText(mWaterMarkDesc);
            ((TextView) framView.findViewById(R.id.tv_6)).setText(mWaterMarkDesc);
            ((TextView) framView.findViewById(R.id.tv_7)).setText(mWaterMarkDesc);
            ((TextView) framView.findViewById(R.id.tv_8)).setText(mWaterMarkDesc);
            ((TextView) framView.findViewById(R.id.tv_9)).setText(mWaterMarkDesc);
            ((TextView) framView.findViewById(R.id.tv_10)).setText(mWaterMarkDesc);
            ((TextView) framView.findViewById(R.id.tv_11)).setText(mWaterMarkDesc);
            ((TextView) framView.findViewById(R.id.tv_12)).setText(mWaterMarkDesc);
            ((TextView) framView.findViewById(R.id.tv_13)).setText(mWaterMarkDesc);
            ((TextView) framView.findViewById(R.id.tv_14)).setText(mWaterMarkDesc);
            ((TextView) framView.findViewById(R.id.tv_15)).setText(mWaterMarkDesc);
            ((TextView) framView.findViewById(R.id.tv_16)).setText(mWaterMarkDesc);
            ((TextView) framView.findViewById(R.id.tv_17)).setText(mWaterMarkDesc);
            ((TextView) framView.findViewById(R.id.tv_18)).setText(mWaterMarkDesc);
        }
        rootView.addView(framView);
        return true;
    }
}
