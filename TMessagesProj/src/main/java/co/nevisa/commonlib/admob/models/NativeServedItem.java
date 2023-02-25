package co.nevisa.commonlib.admob.models;

import com.google.android.gms.ads.nativead.NativeAd;

public class NativeServedItem {
    private NativeAd nativeAd;
    private String name;
    private long time;
    private int used;

    public void setNativeAd(NativeAd nativeAd) {
        this.nativeAd = nativeAd;
    }
    public NativeAd getNativeAd() {
        return nativeAd;
    }



    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        if (name==null){
            name = "";
        }
        return name;
    }

    public long getTime() {
        return time;
    }
    public void setTime(long time) {
        this.time = time;
    }

    public int getUsed() {
        return used;
    }
    public void increaseUsed() {
        this.used += 1;
    }




}
