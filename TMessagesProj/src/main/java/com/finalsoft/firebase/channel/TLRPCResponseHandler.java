package com.finalsoft.firebase.channel;


import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public interface TLRPCResponseHandler<T extends TLObject> {
    void onFailed(TLRPC.TL_error tL_error);

    void onStart();

    void onSuccess(T t);
}
