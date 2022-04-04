package com.finalsoft.ui.admob;


import com.google.android.gms.ads.formats.UnifiedNativeAd;

import org.telegram.tgnet.TLRPC;

public class DialogAddCell extends TLRPC.Dialog {


    private UnifiedNativeAd ad;


    public DialogAddCell(UnifiedNativeAd ad) {
        this.ad = ad;
//        add =true;
    }

    public UnifiedNativeAd getAd() {
        return ad;
    }

/*
    public void setAd(UnifiedNativeAd ad) {
        this.ad = ad;
        add =true;
    }*/
}
