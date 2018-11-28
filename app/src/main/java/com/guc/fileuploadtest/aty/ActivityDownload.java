package com.guc.fileuploadtest.aty;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Button;
import android.widget.ProgressBar;

import com.guc.fileuploadtest.R;
import com.zero.cdownload.CDownload;
import com.zero.cdownload.listener.CDownloadListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by guc on 2018/8/31.
 * 描述：下载工具
 */
public class ActivityDownload extends Activity {
    @BindView(R.id.pb_progressbar)
    ProgressBar mPbProgressbar;
    @BindView(R.id.btn_dowmload)
    Button mBtnDowmload;

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
            public void onProgress(final long maxSIze, final long currentSize) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int cur = (int) (currentSize * 100 / maxSIze);
                        mPbProgressbar.setProgress(cur);
                        Log.e("guc", "in onProgress cur percent:" + cur);
                        mBtnDowmload.setText(String.format("已下载：%d/100", cur));
                    }
                });
                Log.e("guc", "in onProgress maxSIze:" + maxSIze + ";currentSize:" + currentSize);
            }

            @Override
            public void onComplete(final String localFilePath) {
                Log.e("guc", "onComplete localFilePath:" + localFilePath);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mBtnDowmload.setText(String.format("已下载：%s", localFilePath));
                    }
                });
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
