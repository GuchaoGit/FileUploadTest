package com.hnhy.framework.system;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.hnhy.framework.BaseProfile;
import com.hnhy.framework.Constant;
import com.hnhy.framework.frame.BaseSystem;
import com.hnhy.framework.system.net.Request2;
import com.hnhy.framework.system.net.RequestCallback;
import com.hnhy.framework.system.net.RequestTag;
import com.hnhy.framework.system.net.Response;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
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

public class SystemHttp2 extends BaseSystem {

    private OkHttpClient mOkHttpClient;
    private Map<Object, Call> mPoolCall;
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

    public <T> void net(final Object tag, final Request2 request, final RequestCallback<T> callBack) {
        Observable.create(new Observable.OnSubscribe<Response>() {
            @Override
            public void call(final Subscriber<? super Response> subscriber) {
                Call call = mOkHttpClient.newCall(request.getRequest(tag));
                mPoolCall.put(tag, call);
                callBack.onStart();
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                        RequestTag t;
                        if ((t = getRequestTag(call)) != null && !t.isHasCanceled()) {
                            mPoolCall.remove(tag);
                            handleError(subscriber, e.getMessage(), request, callBack);
                        }
                    }

                    @Override
                    public void onResponse(Call call, okhttp3.Response response) throws IOException {
                        RequestTag t;
                        if ((t = getRequestTag(call)) != null && !t.isHasCanceled()) {
                            mPoolCall.remove(tag);
                            if (response.isSuccessful()) {
                                handle(subscriber, response, request, callBack);
                            } else {
                                handleError(subscriber, "http code is not 200", request, callBack);
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
                    public void onNext(Response response) {
                        if (response.mCode == 1) {
                            callBack.onSuccess((T) response.mWrapperData, response);
                        } else {
                            callBack.onFailure(response);
                        }
                    }
                });
    }

    public void cancelRequest(Object tag) {
        Call call = mPoolCall.get(tag);
        if (call != null) {
            call.cancel();
        }
        mPoolCall.remove(tag);
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
                Log.d("hnhy-http", "OkHttp====Message:" + message);
            }
        });
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return loggingInterceptor;
    }

    /**
     * 处理失败数据
     *
     * @param subscriber 订阅者
     * @param msg        错误信息
     * @param request    原始请求数据
     * @param callback   结果回调
     * @param <T>
     */
    private <T> void handleError(Subscriber<? super Response> subscriber, String msg, Request2 request, RequestCallback<T> callback) {
        if (callback != null) {
            Response response = new Response();
            response.mUrl = request.getUrl();
            response.mMessage = msg;
            response.mFromCache = request.isUseCache();
            response.mTime = System.currentTimeMillis();
            response.mCode = 0;
            subscriber.onNext(response);
            subscriber.onCompleted();
        }
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
    private <T> void handle(Subscriber<? super Response> subscriber, okhttp3.Response resp, Request2 request, RequestCallback<T> callback) {
        if (callback != null) {
            try {
                String respJson = resp.body().string();
                Response response = getResponse(request.isWrapperResponse(), respJson, callback.respType);
                response.mFromCache = request.isUseCache();
                response.mTime = System.currentTimeMillis();
                subscriber.onNext(response);
                subscriber.onCompleted();
            } catch (Exception e) {
                handleError(subscriber, "convert body to string error", request, callback);
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
        Response response = new Response();
        JsonObject root = mGson.fromJson(respJson, JsonObject.class);
        int status = root.getAsJsonPrimitive("status").getAsInt();
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
        if (iswrapper && status == 1 && !TextUtils.isEmpty(data)) {
            response.mWrapperData = mGson.fromJson(data, respType);
        }
        return response;
    }
}
