package com.hnhy.framework.frame;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.AppUtils;
import com.hnhy.framework.logger.Logger;
import com.hnhy.framework.system.SystemHttp;
import com.hnhy.framework.util.WaterMarkUtil;
import com.hnhy.ui.dialog.LoadingDialog;

/**
 * Author: hardcattle
 * Time: 2018/3/9 下午4:35
 * Description:
 */

public class BaseActivity extends AppCompatActivity {

    private LoadingDialog mLoadingDialog;
    private boolean isAddWatermark;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getSystem(SystemHttp.class).cancelRequest(this);
    }

    protected void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    protected <T extends BaseSystem> T getSystem(Class<T> system) {
        return SystemManager.getInstance().getSystem(system);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            AppUtils.launchApp(this.getPackageName());
            Logger.e("BaseActivity", "重新启动");
            ActivityUtils.finishAllActivities();
            return;
        }
    }

    protected void showLoadingDialog(boolean show) {
        showLoadingDialog(show, true);
    }

    protected void showLoadingDialog(boolean show, boolean canCancel) {
        if (mLoadingDialog == null) {
            mLoadingDialog = createLoadingDialog();
        }
        mLoadingDialog.setCanCancle(canCancel);
        if (show && !mLoadingDialog.isShowing()) {
            mLoadingDialog.show();
        } else {
            mLoadingDialog.dismiss();
        }
    }

    protected void showDialog(String msg) {
        showDialog(msg, true);
    }

    protected void showDialog(String msg, boolean canCancel) {
        if (mLoadingDialog == null) {
            mLoadingDialog = createLoadingDialog();
        }
        mLoadingDialog.setCanCancle(canCancel);
        if (!TextUtils.isEmpty(msg)) {
            mLoadingDialog.setTip(msg);
        } else {
            mLoadingDialog.setTip("加载中...");
        }
        mLoadingDialog.show();
    }

    protected void dismissDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

    private LoadingDialog createLoadingDialog() {
        LoadingDialog loadingDialog = new LoadingDialog(this).setTip("加载中...");
        loadingDialog.setCancelable(false);
        return loadingDialog;
    }

    public void setStatusBar() {
        // 设置图片沉浸式状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!isAddWatermark) {
            isAddWatermark = WaterMarkUtil.showWatermarkView(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

}

