package com.hnhy.framework.system.net;

import com.hnhy.framework.util.Utils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Author: hardcattle
 * Time: 2018/4/20 上午10:47
 * Description:
 */
public abstract class RequestCallback<T> implements Callback<T> {
    public Type respType;

    public RequestCallback() {
        //获取接口泛型T的class，Type，必须要在子类才能获取Interface的T
        ParameterizedType genType = Utils.getParameterizedType(getClass());
        if (genType != null) {
            Type[] params = genType.getActualTypeArguments();
            if (params != null && params.length > 0)
                this.respType = params[0];
        }
    }


    @Override
    public void onStart() {

    }

    @Override
    public void onSuccess(T data, Response resp) {

    }

    @Override
    public void onFailure(Response resp) {

    }

    @Override
    public void onComplete() {

    }
}