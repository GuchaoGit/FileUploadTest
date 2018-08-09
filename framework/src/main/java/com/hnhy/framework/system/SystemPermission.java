package com.hnhy.framework.system;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.util.Log;

import com.hnhy.framework.frame.BaseSystem;
import com.hnhy.framework.system.permission.ActivityHook;
import com.hnhy.framework.system.permission.CallbackPermission;
import com.hnhy.framework.system.permission.PermissionOption;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Author: hardcattle
 * Time: 2018/4/2 下午2:47
 * Description:
 */
public class SystemPermission extends BaseSystem {

    private static final int REQUEST_CODE_PERMISSION = 0x38;
    private static final int REQUEST_CODE_SETTING = 0x39;
    private Activity mHookActivity;
    private List<String> mDeniedPermissions;
    private Set<String> mManifestPermissions;

    private PermissionOption mPermissionOption;
    private CallbackPermission mCallbackPermission;

    @Override
    protected void init() {
        super.init();
        mDeniedPermissions = new LinkedList<>();
        mManifestPermissions = new HashSet<>(1);
        getManifestPermissions();
    }

    @Override
    protected void destroy() {
        super.destroy();
    }

    public void request(PermissionOption option, CallbackPermission callback) {
        this.mPermissionOption = option;
        this.mCallbackPermission = callback;
        checkSelfPermission();
    }

    /**
     * 响应向系统请求权限结果
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    public synchronized void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION:
                LinkedList<String> grantedPermissions = new LinkedList<>();
                LinkedList<String> deniedPermissions = new LinkedList<>();
                for (int i = 0; i < permissions.length; i++) {
                    String permission = permissions[i];
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED)
                        grantedPermissions.add(permission);
                    else deniedPermissions.add(permission);
                }
                //全部允许才回调 onGranted 否则只要有一个拒绝都回调 onDenied
                if (!grantedPermissions.isEmpty() && deniedPermissions.isEmpty()) {
                    if (mCallbackPermission != null)
                        mCallbackPermission.onGranted();
                    destroyHookActivity();
                } else if (!deniedPermissions.isEmpty()) {
                    showDeniedDialog(deniedPermissions);
                }
                break;
        }
    }

    public synchronized void checkRequestPermissionRationale(Activity activity) {
        mHookActivity = activity;
        boolean rationale = false;
        //如果有拒绝则提示申请理由提示框，否则直接向系统请求权限
        for (String permission : mDeniedPermissions) {
            rationale = rationale || shouldShowRequestPermissionRationale(activity, permission);
        }
        String[] permissions = mDeniedPermissions.toArray(new String[mDeniedPermissions.size()]);
        if (rationale) {
            showRationalDialog(permissions);
        } else {
            requestPermissions(mHookActivity, permissions, REQUEST_CODE_PERMISSION);
        }
    }

    /**
     * 响应设置权限返回结果
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public synchronized void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mCallbackPermission == null || mPermissionOption == null
                || requestCode != REQUEST_CODE_SETTING) {
            destroyHookActivity();
            return;
        }
        checkSelfPermission();
    }

    private synchronized void checkSelfPermission() {
        mDeniedPermissions.clear();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (mCallbackPermission != null) {
                mCallbackPermission.onGranted();
            }
        } else {
            String[] permissions = mPermissionOption.getPermissions();
            for (String permission : permissions) {
                //检查申请的权限是否在 AndroidManifest.xml 中
                if (mManifestPermissions.contains(permission)) {
                    int checkSelfPermission = checkSelfPermission(mContext, permission);
                    if (checkSelfPermission == PackageManager.PERMISSION_DENIED) {
                        mDeniedPermissions.add(permission);
                    }
                }
            }
            //检查如果没有一个拒绝响应 onGranted 回调
            if (mDeniedPermissions.isEmpty()) {
                Log.i(TAG, "mDeniedPermissions.isEmpty()");
                if (mCallbackPermission != null)
                    mCallbackPermission.onGranted();
            } else {
                    startHookActivity();
            }
        }
    }

    private boolean shouldShowRequestPermissionRationale(Activity activity, String permission) {
        boolean shouldShowRational = ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
        return shouldShowRational;
    }

    private synchronized void getManifestPermissions() {
        PackageInfo packageInfo = null;
        try {
            packageInfo = mContext.getPackageManager().getPackageInfo(
                    mContext.getPackageName(), PackageManager.GET_PERMISSIONS);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageInfo != null) {
            String[] permissions = packageInfo.requestedPermissions;
            if (permissions != null) {
                for (String perm : permissions) {
                    mManifestPermissions.add(perm);
                }
            }
        }
    }

    private void startHookActivity() {
        Intent intent = new Intent(mContext, ActivityHook.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    private int checkSelfPermission(Context context, String permission) {
        try {
            final PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            int targetSdkVersion = info.applicationInfo.targetSdkVersion;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (targetSdkVersion >= Build.VERSION_CODES.M) {
                    return ContextCompat.checkSelfPermission(context, permission);
                } else {
                    return PermissionChecker.checkSelfPermission(context, permission);
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return ContextCompat.checkSelfPermission(context, permission);
    }

    /**
     * 申请理由对话框
     *
     * @param permissions
     */
    private synchronized void showRationalDialog(final String[] permissions) {
        new AlertDialog.Builder(mHookActivity)
                .setMessage(mPermissionOption.getRationalMessage())
                .setPositiveButton(mPermissionOption.getRationalBtnText(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestPermissions(mHookActivity, permissions, REQUEST_CODE_PERMISSION);
                    }
                }).show();
    }

    /**
     * 拒绝权限提示框
     *
     * @param permissions
     */
    private synchronized void showDeniedDialog(final List<String> permissions) {
        new AlertDialog.Builder(mHookActivity)
                .setMessage(mPermissionOption.getDeniedMessage())
                .setCancelable(false)
                .setNegativeButton(mPermissionOption.getDeniedCloseBtn(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (mCallbackPermission != null)
                            mCallbackPermission.onDenied(permissions);
                        destroyHookActivity();
                    }
                })
                .setPositiveButton(mPermissionOption.getDeniedSettingBtn(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startSetting();
                    }
                }).show();
    }

    /**
     * 跳转到设置界面
     */
    private void startSetting() {
//        if (MiuiOs.isMIUI()) {
//            Intent intent = MiuiOs.getSettingIntent(mActivity);
//            if (MiuiOs.isIntentAvailable(mActivity, intent)) {
//                mActivity.startActivityForResult(intent, REQUEST_CODE_SETTING);
//                return;
//            }
//        }
        try {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    .setData(Uri.parse("package:" + mHookActivity.getPackageName()));
            mHookActivity.startActivityForResult(intent, REQUEST_CODE_SETTING);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                mHookActivity.startActivityForResult(intent, REQUEST_CODE_SETTING);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }

    }

    private void destroyHookActivity() {
        if (mHookActivity != null) {
            mHookActivity.finish();
            mHookActivity = null;
        }
        mCallbackPermission = null;
    }

    private void requestPermissions(Activity activity, String[] permissions, int requestCode) {
        ActivityCompat.requestPermissions(activity, permissions, requestCode);
    }

}
