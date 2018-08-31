package com.guc.fileuploadtest.utils.fileuploaduitl;

import android.content.Context;
import android.graphics.Bitmap;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hnhy.framework.frame.SystemManager;
import com.hnhy.framework.system.SystemHttp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by guc on 2018/5/5.
 * 描述：文件上传工具
 */
public class UploadFileUtil {

    public static void imageUpLoad(Context context, final Object tag, List<String> localPath, Map<String,String> params, String url, final UploadFileCallback callBack) {
        MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
        final OkHttpClient client = SystemManager.getInstance().getSystem(SystemHttp.class).getOkHttpClient();
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        boolean allNull = true;
        for (int i = 0; i < localPath.size(); i++) {
            if (localPath.get(i) != null) {
                allNull = false;
                File file = new File(localPath.get(i));
                builder.addFormDataPart("files", file.getName(), RequestBody.create(MEDIA_TYPE_PNG, file));
            }
        }
        if (params!=null&&params.size()>0){//参数
            for (Map.Entry<String,String> entry:params.entrySet()){
                builder.addFormDataPart(entry.getKey(),entry.getValue());
            }
        }

        if (allNull) {
//            callBack.onSuccess(null);
            return;
        }

        final MultipartBody requestBody = builder.build();
        //构建请求
        final Request request = new Request.Builder()
                .url(url)//地址
                .post(requestBody)//添加请求体
                .build();
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter<String> emitter) {
                client.newCall(request).enqueue(new okhttp3.Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            e.printStackTrace();
                            emitter.onError(e);
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            if (response.isSuccessful()) {
                                emitter.onNext(response.body().string());
                            } else {
                                emitter.onNext("");
                            }
                        }
                    });
            }
        }).observeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String s) {
                List<BeanUploadFile> listFiles = new Gson().fromJson(s, new TypeToken<List<BeanUploadFile>>() {
                }.getType());
                callBack.onSuccess(tag, listFiles);
            }

                    @Override
                    public void onError(Throwable e) {
                        callBack.onFailure(tag);
                    }

                    @Override
                    public void onComplete() {

                    }
        });
//        Observable.create(new Observable.OnSubscribe<String>() {
//            @Override
//            public void call(final Subscriber<? super String> subscriber) {
//                if (!subscriber.isUnsubscribed()) {
//                    client.newCall(request).enqueue(new okhttp3.Callback() {
//                        @Override
//                        public void onFailure(Call call, IOException e) {
//                            e.printStackTrace();
//                            subscriber.onError(e);
//                        }
//
//                        @Override
//                        public void onResponse(Call call, Response response) throws IOException {
//                            if (response.isSuccessful()) {
//                                subscriber.onNext(response.body().string());
//                            } else {
//                                subscriber.onNext("fail");
//                            }
//                        }
//                    });
//                }
//            }
//        }).observeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<String>() {
//            @Override
//            public void onCompleted() {
//
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                callBack.onFailure(tag);
//            }
//
//            @Override
//            public void onNext(String s) {
//                List<BeanUploadFile> listFiles = new Gson().fromJson(s, new TypeToken<List<BeanUploadFile>>() {
//                }.getType());
//                callBack.onSuccess(tag, listFiles);
//            }
//        });
    }

    /**
     * 压缩图片
     *
     * @param bitmap
     * @param filePath
     */
    public static void compressBitmapToFile(Bitmap bitmap, String filePath) {
        // 0-100 100为不压缩
        int options = 25;

        try {
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
            } else {
                file.createNewFile();
            }

            FileOutputStream fos = new FileOutputStream(file);
            // 把压缩后的数据存放到baos中
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getLivePicIds(List<BeanUploadFile> uploadFiles) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < uploadFiles.size(); i++) {
            sb.append(uploadFiles.get(i).fileId);
            if (i != uploadFiles.size() - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }
}
