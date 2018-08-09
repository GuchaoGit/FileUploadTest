package com.guc.fileuploadtest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.gson.Gson;
import com.guc.fileuploadtest.adapter.AdapterFileUpload;
import com.guc.fileuploadtest.app.CustomApplication;
import com.guc.fileuploadtest.bean.BeanWaitUpload;
import com.guc.fileuploadtest.greendao.BeanWaitUploadDao;

import org.greenrobot.greendao.query.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {


    @BindView(R.id.rcv)
    RecyclerView mRcv;
    private String mFilePath1 = "/storage/emulated/0/DCIM/camera/IMG_20180808_151507.jpg";
    private String mFilePath2 = "/storage/emulated/0/DCIM/camera/IMG_20180808_151517.jpg";
    private String mFilePath3 = "/storage/emulated/0/DCIM/camera/IMG_20180808_151522.jpg";

    private List<BeanWaitUpload> mDatas;
    private AdapterFileUpload mAdaper;
    private BeanWaitUpload mBean;
    private List<String> mPaths;
    private Map<String,String> mParams;
    private Gson mGson;

    private BeanWaitUploadDao mDao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        mGson = new Gson();
        mDatas = new ArrayList<>();
        mDao = CustomApplication.getInstances().getDaoSession().getBeanWaitUploadDao();
        getDatas();
        mAdaper = new AdapterFileUpload(this,mDatas);
        mRcv.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        mRcv.setAdapter(mAdaper);
    }

    private void getDatas(){
        Query query = mDao.queryBuilder().build();
        List<BeanWaitUpload> list = query.list();
        if (list!=null && list.size()>0){
            mDatas.addAll(list);
        }else {
            mBean = new BeanWaitUpload();
            mPaths = new ArrayList<>();
            mParams = new HashMap<>();
            mParams.put("params1","你好啊");
            mParams.put("params2","你好啊");
            mPaths.add(mFilePath1);
            mBean.paths = mGson.toJson(mPaths);
            mBean.params = mGson.toJson(mParams);
            list.add(mBean);

            mBean = new BeanWaitUpload();
            mPaths = new ArrayList<>();
            mPaths.add(mFilePath2);
            mParams = new HashMap<>();
            mParams.put("params1","第二个");
            mParams.put("params2","第二个");
            mBean.paths = mGson.toJson(mPaths);
            mBean.params = mGson.toJson(mParams);
            list.add(mBean);

            mBean = new BeanWaitUpload();
            mPaths = new ArrayList<>();
            mPaths.add(mFilePath3);
            mBean.paths = mGson.toJson(mPaths);
            list.add(mBean);
            mDao.insertInTx(list);
            getDatas();
        }
    }
}
