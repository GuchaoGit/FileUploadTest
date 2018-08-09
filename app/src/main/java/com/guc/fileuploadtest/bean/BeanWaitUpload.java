package com.guc.fileuploadtest.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by guc on 2018/8/8.
 * 描述：待上传数据
 */
@Entity
public class BeanWaitUpload {
    @Id
    public Long _id;
    public String paths;
    public String params;
    public boolean isUpload = false;
    @Generated(hash = 830557886)
    public BeanWaitUpload(Long _id, String paths, String params, boolean isUpload) {
        this._id = _id;
        this.paths = paths;
        this.params = params;
        this.isUpload = isUpload;
    }
    @Generated(hash = 420758965)
    public BeanWaitUpload() {
    }
    public Long get_id() {
        return this._id;
    }
    public void set_id(Long _id) {
        this._id = _id;
    }
    public String getPaths() {
        return this.paths;
    }
    public void setPaths(String paths) {
        this.paths = paths;
    }
    public String getParams() {
        return this.params;
    }
    public void setParams(String params) {
        this.params = params;
    }
    public boolean getIsUpload() {
        return this.isUpload;
    }
    public void setIsUpload(boolean isUpload) {
        this.isUpload = isUpload;
    }
}
