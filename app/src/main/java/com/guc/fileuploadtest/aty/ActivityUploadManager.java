package com.guc.fileuploadtest.aty;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.guc.fileuploadtest.R;
import com.guc.fileuploadtest.adapter.AdapterFileUpload;
import com.guc.fileuploadtest.app.CustomApplication;
import com.guc.fileuploadtest.bean.BeanWaitUpload;
import com.guc.fileuploadtest.greendao.BeanWaitUploadDao;
import com.guc.fileuploadtest.service.ServiceUpload;

import org.greenrobot.greendao.query.Query;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by guc on 2018/8/9.
 * 描述：上传管理列表
 */
public class ActivityUploadManager extends AppCompatActivity {
    protected static final String TAG = "gucc";
    @BindView(R.id.rcv)
    RecyclerView mRcv;
    private ServiceConnection mConn;
    private ServiceUpload mService;

    private BeanWaitUploadDao mDao;
    private List<BeanWaitUpload> mDatas;
    private AdapterFileUpload mAdaper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_manager);
        ButterKnife.bind(this);
        initService();
        initData();
    }

    private void initData() {
        mDao = CustomApplication.getInstances().getDaoSession().getBeanWaitUploadDao();
        Query query = mDao.queryBuilder().build();
        mDatas = query.list();
        mAdaper = new AdapterFileUpload(this, mDatas);
        mRcv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRcv.setAdapter(mAdaper);
    }

    private void initService() {
        mConn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.e(TAG, "e绑定成功调用：onServiceConnected");
                mService = ((ServiceUpload.UploadBinder) service).getService();
                mService.setOnUploadCallback(new ServiceUpload.OnUploadCallback() {
                    @Override
                    public void onUploadCallback(boolean success, BeanWaitUpload beanWaitUpload) {
                        Query query = mDao.queryBuilder().build();
                        mDatas = query.list();
                        mAdaper.update(mDatas);
                    }
                });
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.e(TAG, "绑定成功调用：onServiceConnected");
                mService = null;
            }
        };
        Intent intent = new Intent(this, ServiceUpload.class);
        bindService(intent, mConn, Service.BIND_AUTO_CREATE);
        startService(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConn);
    }
}
