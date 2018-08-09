package com.hnhy.framework.system;

import android.app.Activity;
import android.os.Environment;
import android.widget.Toast;

import com.blankj.utilcode.util.AppUtils;
import com.google.gson.Gson;
import com.hnhy.framework.BaseProfile;
import com.hnhy.framework.frame.BaseSystem;
import com.hnhy.framework.system.update.CustomHttpManager;
import com.hnhy.framework.system.update.VersionMsg;
import com.vector.update_app.UpdateAppBean;
import com.vector.update_app.UpdateAppManager;
import com.vector.update_app.UpdateCallback;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by guc on 2018/4/26.
 * 描述：升级检测
 */
public class SystemCheckUpdate extends BaseSystem {

    @Override
    protected void init() {
        super.init();
    }

    public void upApp(Activity activity, String updateUrl) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("versionCode", AppUtils.getAppVersionCode() + "");
        params.put("pkgName", AppUtils.getAppPackageName());
        //TODO:配置自己的下载地址
        new UpdateAppManager
                .Builder()
                //当前Activity
                .setActivity(activity)
                //实现httpManager接口的对象
                .setHttpManager(new CustomHttpManager())
                //更新地址
                .setUpdateUrl(updateUrl)
                .setParams(params)
                /*   .setTopPic(R.drawable.top_6)
                   .setThemeColor(0xff39c1e9)*/
                /*
                *TODO:添加上传参数
                .setParams(params)
                */
                .build()
                //检测是否有新版本
                .checkNewApp(new UpdateCallback() {
                    @Override
                    protected UpdateAppBean parseJson(String json) {
                        Gson gson = new Gson();
                        VersionMsg versionMsg = gson.fromJson(json, VersionMsg.class);
                        VersionMsg.DataBean data = versionMsg.getData();
                        UpdateAppBean updateAppBean = new UpdateAppBean();
                        if (data.isNeedUpdate()) {
                            updateAppBean.setUpdate("Yes");
                        }
                        updateAppBean.setApkFileUrl(data.getFileUrl());
                        updateAppBean.setNewVersion(data.getNewVersion());
                        updateAppBean.setTargetSize(data.getFileSize().substring(0, 2) + "." + data.getFileSize().charAt(2) + "MB");
                        updateAppBean.setUpdateLog(data.getUpdateJournal());
                        updateAppBean.setConstraint(data.isForceUpdate());
                        updateAppBean.setTargetPath(getApkDownloadTargetPath());
                        return updateAppBean;
                    }

                    /**
                     * 有新版本
                     * @param updateApp        新版本信息
                     * @param updateAppManager app更新管理器
                     */
                    @Override
                    public void hasNewApp(UpdateAppBean updateApp, UpdateAppManager updateAppManager) {
                        updateAppManager.showDialogFragment();
                    }

                    /**
                     * 网络请求之前
                     */
                    @Override
                    public void onBefore() {
                        //CProgressDialogUtils.showProgressDialog(LoginActivity.this);
                    }

                    /**
                     * 网路请求之后
                     */
                    @Override
                    public void onAfter() {
                        //CProgressDialogUtils.cancelProgressDialog(LoginActivity.this);
                    }

                    /**
                     * 没有新版本
                     */

                    @Override
                    public void noNewApp() {
                        Toast.makeText(mContext, "没有新版本", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * 获取apk下载目录
     *
     * @return
     */
    private String getApkDownloadTargetPath() {
        String path;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            path = Environment.getExternalStorageDirectory() + "/huiyun/petiton/apk/";
        } else {
            path = BaseProfile.mContext.getCacheDir().getPath();
        }
        return path;
    }
}
