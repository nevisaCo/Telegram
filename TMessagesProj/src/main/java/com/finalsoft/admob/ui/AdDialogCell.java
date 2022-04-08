package com.finalsoft.admob.ui;


import com.google.android.gms.ads.nativead.NativeAd;

import org.telegram.tgnet.TLRPC;

public class AdDialogCell extends TLRPC.Dialog {


    private NativeAd ad;


    public AdDialogCell(NativeAd ad) {
        this.ad = ad;
//        add =true;
    }

    public NativeAd getAd() {
        return ad;
    }

/*
    public void setAd(UnifiedNativeAd ad) {
        this.ad = ad;
        add =true;
    }*/
}
