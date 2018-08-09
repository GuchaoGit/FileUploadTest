package com.hnhy.framework.frame;

import android.content.Context;

import com.hnhy.framework.BaseProfile;
import com.hnhy.framework.frame.interfaces.ISystem;

/**
 * Author: hardcattle
 * Time: 2018/3/9 下午4:25
 * Description:
 */
public class BaseSystem implements ISystem {
    protected static final String TAG = BaseSystem.class.getSimpleName();
    protected Context mContext;

    //不能修改此public void createSystem()方法，在fxtv/proguard-rules.pro将不混淆此方法
    @Override
    public void createSystem() {
        mContext = BaseProfile.mContext;
        init();
    }

    @Override
    public void destroySystem() {
        destroy();
        mContext = null;
    }

    protected void init() {
    }

    protected void destroy() {
    }

    protected <T extends BaseSystem> T getSystem(Class<T> className) {
        return SystemManager.getInstance().getSystem(className);
    }
}
