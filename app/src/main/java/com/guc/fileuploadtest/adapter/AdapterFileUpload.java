package com.guc.fileuploadtest.adapter;

import android.content.Context;
import android.view.View;

import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.guc.fileuploadtest.R;
import com.guc.fileuploadtest.app.CustomApplication;
import com.guc.fileuploadtest.bean.BeanWaitUpload;
import com.guc.fileuploadtest.greendao.BeanWaitUploadDao;
import com.guc.fileuploadtest.utils.URLUtils;
import com.guc.fileuploadtest.utils.fileuploaduitl.BeanUploadFile;
import com.guc.fileuploadtest.utils.fileuploaduitl.UploadFileCallback;
import com.guc.fileuploadtest.utils.fileuploaduitl.UploadFileUtil;
import com.hnhy.framework.frame.common.CommonRecycleAdapter;
import com.hnhy.framework.frame.common.CommonViewHolder;

import java.util.List;
import java.util.Map;

/**
 * Created by guc on 2018/8/8.
 * 描述：
 */
public class AdapterFileUpload extends CommonRecycleAdapter<BeanWaitUpload> {
    private Gson mGson;
    private BeanWaitUploadDao mDao;
    public AdapterFileUpload(Context context,List<BeanWaitUpload> filePaths){
        super(context,filePaths, R.layout.item_file_upload);
        mDao = CustomApplication.getInstances().getDaoSession().getBeanWaitUploadDao();
        mGson = new Gson();
    }

    @Override
    public void bindData(CommonViewHolder holder, final BeanWaitUpload data, final int position) {
        holder.setText(R.id.tv_file_path,data.paths);
        holder.setText(R.id.btn_upload,data.isUpload?"正在上传":"上传");
        holder.getView(R.id.btn_upload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//上传文件
                if (data.isUpload){
                    ToastUtils.showShort("该数据正在上传");
                    return;
                }
                data.isUpload = true;
                notifyDataSetChanged();
                List<String> paths = mGson.fromJson(data.paths,new TypeToken<List<String>>(){}.getType());
                Map<String,String> params = mGson.fromJson(data.params,new TypeToken<Map<String,String>>(){}.getType());
                mDao.update(data);
                UploadFileUtil.imageUpLoad(mContext,data,paths,params, URLUtils.URL_FILE_SERVER,new UploadFileCallback(){
                    @Override
                    public void onFailure(Object tag) {
                        data.isUpload = false;
                        mDao.update(data);
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onSuccess(Object tag, List<BeanUploadFile> uploadFile) {
                        mDao.deleteByKey(data._id);
                        dataList.remove(getRemoveFile(data._id));
                        notifyDataSetChanged();
                    }
                });
            }
        });

    }

    private BeanWaitUpload getRemoveFile(Long id){
        for (BeanWaitUpload beanWaitUpload:dataList){
            if (beanWaitUpload._id == id){
                return beanWaitUpload;
            }
        }
        return null;
    }
}
