package com.hnhy.framework.system.net;

/**
 * Author: hardcattle
 * Time: 2018/4/20 上午10:44
 * Description:
 */
interface Callback<T> {
    void onStart();

    void onSuccess(T data, Response resp);

    void onFailure(Response resp);

    void onComplete();
}