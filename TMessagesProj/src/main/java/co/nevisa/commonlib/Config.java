package co.nevisa.commonlib;

import co.nevisa.commonlib.config.AdConfig;
import co.nevisa.commonlib.config.BaseConfig;

public class Config {
    public static String TAG = "finalLibrary";
    private static String flurryId;


    public static String getFlurryId() {
        return flurryId;
    }

    public static void setFlurryAppId(String flurryId) {

        Config.flurryId = flurryId;
    }


    public static void setTAG(String tag) {
        if (tag != null && !tag.isEmpty()) {
            Config.TAG = tag;
        }
    }

    private static AdConfig adConfig;

    public static void setAdConfig(AdConfig adConfig) {
        Config.adConfig = adConfig;
    }

    public static AdConfig getAdConfig() {
        return adConfig;
    }

    private static BaseConfig baseConfig;

    public static void setBaseConfig(BaseConfig baseConfig) {
        Config.baseConfig = baseConfig;
        setTAG(baseConfig.getTag());
    }

    public static BaseConfig getBaseConfig() {
        return baseConfig;
    }

    public static boolean isDebugMode() {
        return getBaseConfig().isDebugMode();
    }
}
