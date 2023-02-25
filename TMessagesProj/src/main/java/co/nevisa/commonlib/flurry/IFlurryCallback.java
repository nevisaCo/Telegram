package co.nevisa.commonlib.flurry;

import com.flurry.android.FlurryConfig;

public interface IFlurryCallback {
    void onFetched(boolean isCache, FlurryConfig flurryConfig);
}
