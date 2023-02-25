package co.nevisa.commonlib.admob.models;

import com.google.android.gms.ads.nativead.NativeAd;

import co.nevisa.commonlib.admob.cells.NativeAddCell;

public  class NativeObject {
    private final NativeAd nativeAd;
    private final NativeAddCell nativeAddCell;

    public NativeObject(NativeAd nativeAd, NativeAddCell nativeAddCell) {
        this.nativeAd = nativeAd;
        this.nativeAddCell = nativeAddCell;
    }

    public NativeAd getNativeAd() {
        return nativeAd;
    }

    public NativeAddCell getNativeAddCell() {
        return nativeAddCell;
    }
}