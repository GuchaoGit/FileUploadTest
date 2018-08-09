package com.hnhy.framework;

/**
 * Author: hardcattle
 * Time: 2018/3/9 下午4:24
 * Description:
 */
public final class Configuration {
    final boolean mEnableLog;
    final boolean mEnableHttpLog;
    final boolean mEnableCrashReset;
    final boolean mEnableSaveCrashLog;

    private Configuration(Builder builder) {
        mEnableLog = builder.mEnableLog;
        mEnableHttpLog = builder.mEnableHttpLog;
        mEnableCrashReset = builder.mEnableCrashReset;
        mEnableSaveCrashLog = builder.mEnableSaveCrashLog;
    }

    public boolean isEnableLog() {
        return mEnableLog;
    }

    public boolean isEnableHttpLog() {
        return mEnableHttpLog;
    }

    public boolean isEnableCrashReset() {
        return mEnableCrashReset;
    }

    public boolean isEnableSaveCrashLog() {
        return mEnableSaveCrashLog;
    }

    public static class Builder {
        private boolean mEnableLog;
        private boolean mEnableHttpLog;
        private boolean mEnableCrashReset;
        private boolean mEnableSaveCrashLog;

        public Builder setEnableLog(boolean enableLog) {
            mEnableLog = enableLog;
            return this;
        }

        public Builder setEbableHttpLog(boolean enableHttpLog) {
            mEnableHttpLog = enableHttpLog;
            return this;
        }

        public Builder setEnableCrashReset(boolean enableCrashReset) {
            mEnableCrashReset = enableCrashReset;
            return this;
        }

        public Builder setEnableSaveCrashLog(boolean enableSaveCrashLog) {
            mEnableSaveCrashLog = enableSaveCrashLog;
            return this;
        }

        public Builder setEnableDebugModel(boolean debugModel) {
            if (debugModel) {
                mEnableLog = true;
                mEnableHttpLog = true;
                mEnableCrashReset = false;
                mEnableSaveCrashLog = false;
            } else {
                mEnableLog = false;
                mEnableHttpLog = false;
                mEnableCrashReset = true;
                mEnableSaveCrashLog = true;
            }
            return this;
        }

        public Configuration build() {
            return new Configuration(this);
        }
    }
}
