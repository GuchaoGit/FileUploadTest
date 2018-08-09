package com.hnhy.framework;

import android.content.Context;

/**
 * Author: hardcattle
 * Time: 2018/3/9 下午4:23
 * Description:
 */
public class BaseProfile {
    public static Context mContext;
    public static Configuration mConfiguration;
    public static String mBaseUrl;

    public static void initProfile(Context context, Configuration configuration) {
        mContext = context;
        mConfiguration = configuration;
        if (mConfiguration == null) {
            mConfiguration = new Configuration.Builder().setEnableDebugModel(true).build();
        }
    }
}
