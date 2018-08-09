package com.guc.fileuploadtest.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.guc.fileuploadtest.MainActivity;
import com.guc.fileuploadtest.R;
import com.guc.fileuploadtest.app.CustomApplication;
import com.guc.fileuploadtest.bean.BeanWaitUpload;
import com.guc.fileuploadtest.greendao.BeanWaitUploadDao;
import com.guc.fileuploadtest.utils.URLUtils;
import com.guc.fileuploadtest.utils.fileuploaduitl.BeanUploadFile;
import com.guc.fileuploadtest.utils.fileuploaduitl.UploadFileCallback;
import com.guc.fileuploadtest.utils.fileuploaduitl.UploadFileUtil;

import org.greenrobot.greendao.query.Query;

import java.util.List;
import java.util.Map;

/**
 * Created by guc on 2018/8/9.
 * 描述：
 */
public class ServiceUpload extends Service {
    protected static final String TAG = "gucc_service";
    // 守护进程 Service ID
    private final static int DAEMON_SERVICE_ID = -5121;
    private static Notification notification;
    private BeanWaitUploadDao mDao;
    private Binder mBinder = new UploadBinder();

    private OnUploadCallback mCallback;
    private List<BeanWaitUpload> mDatas;
    private Gson mGson;

    public void setOnUploadCallback(OnUploadCallback callback) {
        this.mCallback = callback;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate");
        mGson = new Gson();
        mDao = CustomApplication.getInstances().getDaoSession().getBeanWaitUploadDao();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "work");
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setTicker("新消息!注意查看");
        builder.setContentTitle("正在上传信息!");
        builder.setLargeIcon(bitmap);
        Intent intent1 = new Intent(Intent.ACTION_MAIN);
        intent1.addCategory(Intent.CATEGORY_LAUNCHER);
        intent1.setClass(this, MainActivity.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        Context mContext = getApplicationContext();
        PendingIntent contextIntent = PendingIntent.getActivity(mContext, 0, intent1, 0);
        builder.setContentIntent(contextIntent);
        notification = builder.build();
        notification.flags = Notification.FLAG_NO_CLEAR;

        // 利用 Android 漏洞提高进程优先级，
        try {
            startForeground(DAEMON_SERVICE_ID, notification);
        } catch (Exception e) {
            e.printStackTrace();
        }
        startUploadData();
        return START_STICKY;
    }

    /**
     * 开始上传数据
     */
    private void startUploadData() {
        Query query = mDao.queryBuilder().build();
        mDatas = query.list();
        if (mDatas != null && mDatas.size() > 0) {
            final BeanWaitUpload data = mDatas.get(0);
            if (data.isUpload) return;
            List<String> paths = mGson.fromJson(data.paths, new TypeToken<List<String>>() {
            }.getType());
            Map<String, String> params = mGson.fromJson(data.params, new TypeToken<Map<String, String>>() {
            }.getType());
            data.isUpload = true;
            mDao.update(data);
            if (mCallback != null) {
                mCallback.onUploadCallback(false, data);
            }
            UploadFileUtil.imageUpLoad(this, data, paths, params, URLUtils.URL_FILE_SERVER, new UploadFileCallback() {
                @Override
                public void onFailure(Object tag) {
                    data.isUpload = false;
                    mDao.update(data);
                    if (mCallback != null) {
                        mCallback.onUploadCallback(false, data);
                    }
                }

                @Override
                public void onSuccess(Object tag, List<BeanUploadFile> uploadFile) {
                    mDao.deleteByKey(data._id);
                    if (mCallback != null) {
                        mCallback.onUploadCallback(true, data);
                    }
                }
            });
        }

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, "onBind");
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.e(TAG, "onUnbind");
        mCallback = null;
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy");
    }

    public interface OnUploadCallback {
        void onUploadCallback(boolean success, BeanWaitUpload beanWaitUpload);
    }

    public class UploadBinder extends Binder {
        public ServiceUpload getService() {
            return ServiceUpload.this;
        }
    }
}
