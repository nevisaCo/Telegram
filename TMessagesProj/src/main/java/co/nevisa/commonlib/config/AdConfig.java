package co.nevisa.commonlib.config;

public class AdConfig {
    private boolean testDevice;
    private String testDeviceIds;
    private int retryOnFail = 2;
    private int  nativeRefreshTime= 15;
    private boolean preServe = false;
    private boolean loadSingleNative = true;
    /**
     * @param preServe if true : admob serve ad on app open and after show ad. default is false
     */
    public void setPreServe(boolean preServe) {
        this.preServe = preServe;
    }

    /**
     * @param retryOnFail when ad load fail,retry on . default is 2
     */
    public void setRetryOnFail(int retryOnFail) {
        this.retryOnFail = retryOnFail;
    }

    public void setTestDevice(boolean testDevice) {
        this.testDevice = testDevice;
    }

    public void setTestDeviceIds(String testDeviceIds) {
        this.testDeviceIds = testDeviceIds;
    }

    public boolean isTestDevice() {
        return testDevice;
    }

    public String getTestDeviceIds() {
        return testDeviceIds;
    }

    public int getRetryOnFail() {
        return retryOnFail;
    }

    public boolean isPreServe() {
        return preServe;
    }

    public boolean isLoadSingleNative() {
        return loadSingleNative;
    }

    public int nativeRefreshTime() {
        return nativeRefreshTime;
    }

    public void setNativeRefreshTime(int nativeRefreshTime) {
        this.nativeRefreshTime = nativeRefreshTime;
    }

    public void setLoadSingleNative(boolean loadSingleNative) {
        this.loadSingleNative = loadSingleNative;
    }


}
