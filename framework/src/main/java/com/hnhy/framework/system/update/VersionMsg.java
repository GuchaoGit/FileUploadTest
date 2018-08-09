package com.hnhy.framework.system.update;

/**
 * Created by hzk on 2018/1/18.
 */

public class VersionMsg {

    /**
     * success : true
     * message : 错误信息
     * data : {"needUpdate":true,"forceUpdate":true,"fileUrl":"http://test.com/download?fileId=xxsasdf","fileMd5":"23sdfsdfadfasdfasdfsf","newVersion":"3.23.1","updateJournal":"本次更新了xxx内容\n还有xxx内容"}
     */

    private boolean success;
    private String message;
    private DataBean data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * needUpdate : true
         * forceUpdate : true
         * fileUrl : http://test.com/download?fileId=xxsasdf
         * fileMd5 : 23sdfsdfadfasdfasdfsf
         * newVersion : 3.23.1
         * updateJournal : 本次更新了xxx内容
         * 还有xxx内容
         */

        private boolean needUpdate;
        private boolean forceUpdate;
        private String fileUrl;
        private String fileMd5;
        private String fileSize;
        private String newVersion;
        private String updateJournal;

        public String getFileSize() {
            return fileSize;
        }

        public void setFileSize(String fileSize) {
            this.fileSize = fileSize;
        }

        public boolean isNeedUpdate() {
            return needUpdate;
        }

        public void setNeedUpdate(boolean needUpdate) {
            this.needUpdate = needUpdate;
        }

        public boolean isForceUpdate() {
            return forceUpdate;
        }

        public void setForceUpdate(boolean forceUpdate) {
            this.forceUpdate = forceUpdate;
        }

        public String getFileUrl() {
            return fileUrl;
        }

        public void setFileUrl(String fileUrl) {
            this.fileUrl = fileUrl;
        }

        public String getFileMd5() {
            return fileMd5;
        }

        public void setFileMd5(String fileMd5) {
            this.fileMd5 = fileMd5;
        }

        public String getNewVersion() {
            return newVersion;
        }

        public void setNewVersion(String newVersion) {
            this.newVersion = newVersion;
        }

        public String getUpdateJournal() {
            return updateJournal;
        }

        public void setUpdateJournal(String updateJournal) {
            this.updateJournal = updateJournal;
        }
    }
}
