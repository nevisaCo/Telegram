package com.finalsoft.admob.models;

import com.google.android.gms.ads.nativead.NativeAd;

public class NativeServedItem {
    public NativeAd nativeAd;
//    public String name;
    public long time;
    public int used;

    public NativeAd getNativeAd() {
        return nativeAd;
    }

/*
    public String getName() {
        return name;
    }
*/

    public long getTime() {
        return time;
    }

    public int isUsed() {
        return used;
    }
}
