package co.nevisa.commonlib.admob.cells;


import com.google.android.gms.ads.nativead.NativeAd;

import org.telegram.tgnet.TLRPC;

public class AdDialogCell extends TLRPC.Dialog {


    private NativeAd ad;


    public AdDialogCell(NativeAd ad) {
        this.ad = ad;
    }

    public NativeAd getAd() {
        return ad;
    }

}
