package com.hnhy.framework.system.permission;

/**
 * Author: hardcattle
 * Time: 2018/4/2 下午3:44
 * Description:
 */
public class PermissionOption {
    final String mRationalMessage;
    final String mDeniedMessage;
    final String mDeniedCloseBtn;
    final String mDeniedSettingBtn;
    final String mRationalBtn;
    final String[] mPermissions;

    private PermissionOption(Builder builder) {
        this.mRationalMessage = builder.mRationalMessage;
        this.mDeniedMessage = builder.mDeniedMessage;
        this.mDeniedCloseBtn = builder.mDeniedCloseBtn;
        this.mDeniedSettingBtn = builder.mDeniedSettingBtn;
        this.mRationalBtn = builder.mRationalBtn;
        this.mPermissions = builder.mPermissions;
    }

    public String getRationalMessage() {
        return mRationalMessage;
    }

    public String getDeniedMessage() {
        return mDeniedMessage;
    }

    public String getDeniedCloseBtn() {
        return mDeniedCloseBtn;
    }

    public String getDeniedSettingBtn() {
        return mDeniedSettingBtn;
    }

    public String getRationalBtnText() {
        return mRationalBtn;
    }

    public String[] getPermissions() {
        return mPermissions;
    }

    public static class Builder {
        private static final String DEF_RATIONAL_MESSAGE = "此功能需要您授权，否则将不能正常使用";
        private static final String DEF_DENIED_MESSAGE = "您拒绝权限申请，此功能将不能正常使用，您可以去设置页面重新授权";
        private static final String DEF_DENIED_CLOSE_BTN_TEXT = "关闭";
        private static final String DEF_DENIED_SETTINGS_BTN_TEXT = "设置权限";
        private static final String DEF_RATIONAL_BTN_TEXT = "我知道了";
        private String mRationalMessage = DEF_RATIONAL_MESSAGE;
        private String mDeniedMessage = DEF_DENIED_MESSAGE;
        private String mDeniedCloseBtn = DEF_DENIED_CLOSE_BTN_TEXT;
        private String mDeniedSettingBtn = DEF_DENIED_SETTINGS_BTN_TEXT;
        private String mRationalBtn = DEF_RATIONAL_BTN_TEXT;
        private String[] mPermissions;

        /**
         * 申请权限理由框提示语
         *
         * @param rationalMessage
         * @return
         */
        public Builder setRationalMessage(String rationalMessage) {
            mRationalMessage = rationalMessage;
            return this;
        }

        /**
         * 申请权限理由框按钮
         *
         * @param rationalBtnText
         * @return
         */
        public Builder setRationalBtn(String rationalBtnText) {
            this.mRationalBtn = rationalBtnText;
            return this;
        }

        /**
         * 拒绝框提示语
         *
         * @param deniedMessage
         * @return
         */
        public Builder setDeniedMessage(String deniedMessage) {
            mDeniedMessage = deniedMessage;
            return this;
        }

        /**
         * 拒绝框关闭按钮
         *
         * @param deniedCloseBtnText
         * @return
         */
        public Builder setDeniedCloseBtn(String deniedCloseBtnText) {
            this.mDeniedCloseBtn = deniedCloseBtnText;
            return this;
        }

        /**
         * 拒绝框跳转设置权限按钮
         *
         * @param deniedSettingText
         * @return
         */
        public Builder setDeniedSettingBtn(String deniedSettingText) {
            this.mDeniedSettingBtn = deniedSettingText;
            return this;
        }

        /**
         * 需要申请的权限
         *
         * @param mPermissions {@linkplain android.Manifest.permission android.Manifest.permission}
         * @return
         */
        public Builder setPermissions(String... mPermissions) {
            this.mPermissions = mPermissions;
            return this;
        }

        public PermissionOption build() {
            if (this.mPermissions == null || this.mPermissions.length == 0) {
                throw new IllegalArgumentException("permissions is empty!");
            }
            return new PermissionOption(this);
        }
    }
}
