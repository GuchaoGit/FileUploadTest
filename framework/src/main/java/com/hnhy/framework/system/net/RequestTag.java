package com.hnhy.framework.system.net;

/**
 * Author: hardcattle
 * Time: 2018/4/20 上午11:03
 * Description:
 */
public class RequestTag {
    private final Object mTag;
    private boolean mHasCanceled;

    public RequestTag(Object tag) {
        mTag = tag;
    }

    public Object getTag() {
        return mTag;
    }

    public boolean isHasCanceled() {
        return mHasCanceled;
    }

    public void setHasCanceled(boolean hasCanceled) {
        mHasCanceled = hasCanceled;
    }
}
