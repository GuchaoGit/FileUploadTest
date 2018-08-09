package com.hnhy.framework.system.permission;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.WindowManager;

import com.hnhy.framework.frame.BaseActivity;
import com.hnhy.framework.system.SystemPermission;

/**
 * Author: hardcattle
 * Time: 2018/4/2 下午4:08
 * Description:
 */
public class ActivityHook extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        if (savedInstanceState == null)
            getSystem(SystemPermission.class).checkRequestPermissionRationale(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        getSystem(SystemPermission.class).checkRequestPermissionRationale(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        getSystem(SystemPermission.class).onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        getSystem(SystemPermission.class).onActivityResult(requestCode, resultCode, data);
    }
}
