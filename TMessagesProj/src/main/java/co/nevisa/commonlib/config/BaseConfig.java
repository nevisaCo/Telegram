package co.nevisa.commonlib.config;

import co.nevisa.commonlib.Config;

public class BaseConfig {
    private int icLauncher;
    private boolean debugMode;
    private String applicationId;
    private String tag;

    private boolean volleyDataCacheStatus = true;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setIcLauncher(int icLauncher) {
        this.icLauncher = icLauncher;
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }

    public int getIcLauncher() {
        return icLauncher;
    }

    public boolean isDebugMode() {
        return debugMode;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public boolean volleyDataCacheStatus() {
        return volleyDataCacheStatus;
    }

    public void volleyDataCacheStatus(boolean volleyDataCacheStatus) {
        this.volleyDataCacheStatus = volleyDataCacheStatus;
    }
}
