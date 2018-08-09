package com.hnhy.framework.system.net;

import com.hnhy.framework.BaseProfile;
import com.hnhy.framework.util.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Author: hardcattle
 * Time: 2018/4/20 上午9:25
 * Description:
 */
public class Request2 {
    public static final int TYPE_GET = 0;
    public static final int TYPE_POST = 1;
    public static final int TYPE_FILE = 2;//上传文件

    private final String mUrl;
    private final int mRequestType;
    private final Map<String, String> mRequestParams;
    private final List<String> mLocalFilePaths;
    private final boolean mUseCache;
    private final boolean mWrapperResponse;

    private final boolean isJsonRPC;
    private final String mJsonRPCParams;


    public Request2(Builder builder) {
        mUrl = builder.url;
        mRequestType = builder.requestType;
        mRequestParams = builder.requestParams;
        mLocalFilePaths = builder.localFilesPaths;
        mUseCache = builder.useCache;
        mWrapperResponse = builder.wrapperResponse;
        isJsonRPC = builder.isJsonRPC;
        mJsonRPCParams = builder.jsonRPCParams;
    }

    public String getUrl() {
        return mUrl;
    }

    public int getRequestType() {
        return mRequestType;
    }

    public Map<String, String> getRequestParams() {
        return mRequestParams;
    }

    public boolean isUseCache() {
        return mUseCache;
    }

    public boolean isWrapperResponse() {
        return mWrapperResponse;
    }

    public okhttp3.Request getRequest(Object tag) {
        okhttp3.Request.Builder builder = new okhttp3.Request.Builder();
        if (tag != null) {
            builder.tag(new RequestTag(tag));
        } else {
            builder.tag(new RequestTag(""));
        }

        if (mRequestType == TYPE_GET) {
            builder.get();
            builder.url(getGetUrl(mUrl, mRequestParams));
        } else if (mRequestType == TYPE_POST) {
            builder.url(mUrl);
            RequestBody requestBody = getRequestBody(mRequestParams);
            builder.post(requestBody);
        } else if (mRequestType == TYPE_FILE) {
            builder.url(mUrl);
            MultipartBody requestBody = getMultipartBody(mRequestParams, mLocalFilePaths);
            builder.post(requestBody);
        }

        return builder.build();
    }

    /**
     * 获取GET 请求的Url
     *
     * @param url    url
     * @param params 参数
     * @return 完整url
     */
    private String getGetUrl(String url, Map<String, String> params) {
        StringBuilder urlSb = new StringBuilder();
        urlSb.append(url);
        if (params != null) {
            urlSb.append("?");
            List<String> keyList = new ArrayList<>(params.keySet());
            for (int i = 0; i < keyList.size(); i++) {
                urlSb.append(keyList.get(i));
                urlSb.append("=");
                urlSb.append(params.get(keyList.get(i)));
                if (i != keyList.size() - 1) {
                    urlSb.append("&");
                }
            }
        }
        return urlSb.toString();
    }

    /**
     * 适用存参数的POST 请求
     *
     * @param params 请求参数
     * @return 请求体
     */
    private RequestBody getRequestBody(Map<String, String> params) {
        if (isJsonRPC) {
            return null;
        } else {
            FormBody.Builder formBody = new FormBody.Builder();//创建表单请求体
            if (params != null) {
                List<String> keyList = new ArrayList<>(params.keySet());
                for (int i = 0; i < keyList.size(); i++) {
                    formBody.add(keyList.get(i), params.get(keyList.get(i)));//添加键值对参数
                }
            }
            return formBody.build();
        }

    }

    /**
     * 文件上传
     *
     * @param params     参数
     * @param mFilePaths 文件路径
     * @return
     */
    private MultipartBody getMultipartBody(Map<String, String> params, List<String> mFilePaths) {
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        if (params != null) {
            // map 里面是请求中所需要的 参数
            for (Map.Entry entry : params.entrySet()) {
                builder.addFormDataPart(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
            }
        }
        if (mFilePaths != null) {
            for (int i = 0; i < mFilePaths.size(); i++) {
                if (mFilePaths.get(i) != null) {
                    File file = new File(mFilePaths.get(i));
                    String mMimeType = Utils.getMimeType(mFilePaths.get(i));
                    builder.addFormDataPart("files", file.getName(), RequestBody.create(MediaType.parse(mMimeType), file));
                }
            }
        }
        return builder.build();
    }

    public static class Builder {
        String url;
        String baseUrl;
        String relativeUrl;
        int requestType = TYPE_GET;
        Map<String, String> requestParams;
        List<String> localFilesPaths;
        boolean useCache = false;
        boolean wrapperResponse = true;
        boolean isJsonRPC = true;
        String jsonRPCParams;

        public Builder() {
            baseUrl = BaseProfile.mBaseUrl;
        }

        public Builder setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder setJsonRPC(boolean jsonRPC) {
            isJsonRPC = jsonRPC;
            if (isJsonRPC) {
                requestType = TYPE_POST;
            }
            return this;
        }

        public Builder setJsonRPCParams(String jsonRPCParams) {
            this.jsonRPCParams = jsonRPCParams;
            return this;
        }

        public Builder setRelativeUrl(String relativeUrl) {
            this.relativeUrl = relativeUrl;
            return this;
        }

        public Builder setRequestType(int requestType) {
            this.requestType = requestType;
            if (isJsonRPC) {
                this.requestType = TYPE_POST;
            }
            return this;
        }

        public Builder setRequestParams(Map<String, String> requestParams) {
            this.requestParams = requestParams;
            return this;
        }

        public Builder setUseCache(boolean useCache) {
            this.useCache = useCache;
            return this;
        }

        public Builder setWrapperResponse(boolean wrapperResponse) {
            this.wrapperResponse = wrapperResponse;
            return this;
        }
        public Builder setLocalFilesPaths(List<String> localFilesPaths) {
            this.localFilesPaths = localFilesPaths;
            return this;
        }

        public Request2 build() {
            url = baseUrl + relativeUrl;
            return new Request2(this);
        }
    }
}
