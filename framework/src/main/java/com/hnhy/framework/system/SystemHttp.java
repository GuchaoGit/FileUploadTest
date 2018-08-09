package com.hnhy.framework.system;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.hnhy.framework.BaseProfile;
import com.hnhy.framework.Constant;
import com.hnhy.framework.frame.BaseSystem;
import com.hnhy.framework.logger.Logger;
import com.hnhy.framework.system.net.Request;
import com.hnhy.framework.system.net.RequestCallback;
import com.hnhy.framework.system.net.RequestTag;
import com.hnhy.framework.system.net.Response;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Author: hardcattle
 * Time: 2018/3/11 下午1:34
 * Description:
 */

public class SystemHttp extends BaseSystem {

    private OkHttpClient mOkHttpClient;
    private Map<Object, List<Call>> mPoolCall;
    private Gson mGson;

    @Override
    protected void init() {
        super.init();
        mGson = new Gson();
        mPoolCall = new HashMap<>();
        mOkHttpClient = createOkHttpClient();
    }

    @Override
    protected void destroy() {
        super.destroy();
        Set<Object> keySets = mPoolCall.keySet();
        for (Object tag : keySets) {
            cancelRequest(tag);
        }
        mPoolCall = null;
        mGson = null;
        mOkHttpClient = null;
    }

    public <T> void net(final Object tag, final Request request, final RequestCallback<T> callBack) {
        Observable.create(new Observable.OnSubscribe<Response>() {
            @Override
            public void call(final Subscriber<? super Response> subscriber) {
                final Call call = mOkHttpClient.newCall(request.getRequest(tag));
                if (mPoolCall.get(tag) == null) {
                    mPoolCall.put(tag, new ArrayList<Call>() {
                        {
                            add(call);
                        }
                    });
                } else {
                    mPoolCall.get(tag).add(call);
                }
                callBack.onStart();
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                        RequestTag t;
                        if ((t = getRequestTag(call)) != null && !t.isHasCanceled()) {
                            removeCall(tag, call);
                            handleError(subscriber, 1, e.getMessage(), request, callBack);
                        }
                    }

                    @Override
                    public void onResponse(Call call, okhttp3.Response response) throws IOException {
                        RequestTag t;
                        if ((t = getRequestTag(call)) != null && !t.isHasCanceled()) {
                            removeCall(tag, call);
                            if (response.isSuccessful()) {
                                handle(subscriber, response, request, callBack);
                            } else {
                                handleError(subscriber, response.code(), "http code is " + response.code(), request, callBack);
                            }
                        }
                    }
                });
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers
                        .mainThread())
                .subscribe(new Observer<Response>() {
                    @Override
                    public void onCompleted() {
                        callBack.onComplete();
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(com.hnhy.framework.system.net.Response response) {
                        if (response.mCode == 0) {
                            callBack.onSuccess((T) response.mWrapperData, response);
                        } else {
                            callBack.onFailure(response);
                        }
                    }
                });
    }

    public void cancelRequest(Object tag) {
        List<Call> calls = mPoolCall.get(tag);
        if (calls != null) {
            for (Call call : calls) {
                if (call != null && call.isExecuted())
                    call.cancel();
            }
        }
        mPoolCall.remove(tag);
    }

    private void removeCall(Object tag, Call call) {
        List<Call> calls = mPoolCall.get(tag);
        if (calls != null) {
            calls.remove(call);
        }
    }
    private RequestTag getRequestTag(Call call) {
        return (RequestTag) call.request().tag();
    }

    private OkHttpClient createOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        if (BaseProfile.mConfiguration.isEnableHttpLog()) {
            HttpLoggingInterceptor loggingInterceptor = createHttpLoggingInterceptor();
            builder.addInterceptor(loggingInterceptor);
        }

        return builder.connectTimeout(Constant.TIME_OUT_CONNECT, TimeUnit.SECONDS)
                .readTimeout(Constant.TIME_OUT_READ, TimeUnit.SECONDS)
                .writeTimeout(Constant.TIME_OUT_WRITE, TimeUnit.SECONDS)
                .build();
    }

    private HttpLoggingInterceptor createHttpLoggingInterceptor() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Log.i("hnhy-http", "OkHttp====Message:" + message);
            }
        });
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return loggingInterceptor;
    }

    /**
     * 处理失败数据
     *
     * @param subscriber 订阅者
     * @param code       返回错误码
     * @param msg        错误信息
     * @param request    原始请求数据
     * @param callback   结果回调
     * @param <T>
     */
    private <T> void handleError(Subscriber<? super com.hnhy.framework.system.net.Response> subscriber, int code, String msg, Request request, RequestCallback<T> callback) {
        if (callback != null) {
            com.hnhy.framework.system.net.Response response = new com.hnhy.framework.system.net.Response();
            response.mUrl = request.getUrl();
            response.mMessage = msg;
            response.mFromCache = request.isUseCache();
            response.mTime = System.currentTimeMillis();
            response.mCode = code;
            subscriber.onNext(response);
            subscriber.onCompleted();
        }
        Logger.e("SystemHttp_handleError", msg);
    }

    /**
     * 处理数据
     *
     * @param subscriber 订阅者
     * @param resp       请求返回数据
     * @param request    原始请求数据
     * @param callback   结果回调
     * @param <T>
     */
    private <T> void handle(Subscriber<? super Response> subscriber, okhttp3.Response resp, Request request, RequestCallback<T> callback) {
        if (callback != null) {
            try {
                String respJson = resp.body().string();
                com.hnhy.framework.system.net.Response response = getResponse(request.isWrapperResponse(), respJson, callback.respType);
                response.mFromCache = request.isUseCache();
                response.mTime = System.currentTimeMillis();
                subscriber.onNext(response);
                subscriber.onCompleted();
            } catch (Exception e) {
                handleError(subscriber, 1, "convert body to string error", request, callback);
            }
        }

    }

    /**
     * 处理返回结果
     *
     * @param iswrapper 是否包裹数据
     * @param respJson  原始返回数据字符串
     * @param respType  所需数据类型
     * @param <T>
     * @return com.hnhy.framework.system.net.Response<T>
     */
    private <T> Response<T> getResponse(boolean iswrapper, String respJson, Type respType) {
        com.hnhy.framework.system.net.Response response = new Response();
        JsonObject root = mGson.fromJson(respJson, JsonObject.class);
        int status = root.getAsJsonPrimitive("code").getAsInt();
        String msg = root.getAsJsonPrimitive("msg").getAsString();
        String data = null;
        if (root.has("data")) {
            data = root.get("data").toString();
        } else if (root.has("rows")) {
            data = root.get("rows").toString();
        }
        response.mCode = status;
        response.mMetadata = respJson;
        response.mMessage = msg;
        if (iswrapper && status == 0 && !TextUtils.isEmpty(data)) {
            response.mWrapperData = mGson.fromJson(data, respType);
        }
        return response;
    }

    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }
}
