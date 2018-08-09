package com.hnhy.framework.frame;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.hnhy.framework.system.SystemHttp;
import com.hnhy.ui.dialog.LoadingDialog;

/**
 * Author: hardcattle
 * Time: 2018/3/9 下午4:43
 * Description:
 */

public abstract class BaseFragment extends Fragment {
    protected Context mContext;
    private View mView;//要导入的布局view
    private LoadingDialog mLoadingDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(getLayoutId(), container, false);
        mContext = this.getActivity();
        initView(mView);
        return mView;
    }

    /**
     * 加載页面布局文件
     *
     * @return
     */
    protected abstract int getLayoutId();

    /**
     * 加载控件
     */
    protected abstract void initView(View rootView);

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

    private LoadingDialog createLoadingDialog() {
        LoadingDialog mLoadingDialog = new LoadingDialog(getActivity()).setTip("加载中...");
        mLoadingDialog.setCancelable(false);
        return mLoadingDialog;
    }

    protected void showToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getSystem(SystemHttp.class).cancelRequest(this);
    }

    protected <T extends BaseSystem> T getSystem(Class<T> system) {
        return SystemManager.getInstance().getSystem(system);
    }
}
