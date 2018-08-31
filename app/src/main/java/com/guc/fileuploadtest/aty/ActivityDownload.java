package com.guc.fileuploadtest.aty;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.guc.fileuploadtest.R;
import com.zero.cdownload.CDownload;
import com.zero.cdownload.listener.CDownloadListener;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by guc on 2018/8/31.
 * 描述：
 */
public class ActivityDownload extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_dowmload)
    public void onViewClicked() {
        String url = "https://qd.myapp.com/myapp/qqteam/Androidlite/qqlite_3.7.1.704_android_r110206_GuanWang_537057973_release_10000484.apk";
        CDownload.getInstance().create(url, new CDownloadListener() {
            @Override
            public void onPreStart() {
                Log.e("guc", "onPreStart");
            }

            @Override
            public void onProgress(long maxSIze, long currentSize) {
                Log.e("guc", "in onProgress maxSIze:" + maxSIze + ";currentSize:" + currentSize);
            }

            @Override
            public void onComplete(String localFilePath) {
                Log.e("guc", "onComplete localFilePath:" + localFilePath);
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("guc", "onError");
            }

            @Override
            public void onCancel() {
                Log.e("guc", "onCancel");
            }
        });
        CDownload.getInstance().start(url);
    }
}
