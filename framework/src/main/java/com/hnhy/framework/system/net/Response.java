package com.hnhy.framework.system.net;

/**
 * Author: hardcattle
 * Time: 2018/4/20 上午10:44
 * Description:
 */
public class Response<T> {
    public String mUrl;
    public String mMetadata;
    public T mWrapperData;
    public int mCode;
    public String mMessage;
    public long mTime;

    public boolean mFromCache;
}
