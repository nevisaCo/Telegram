package com.finalsoft.firebase.channel;


import org.telegram.tgnet.TLObject;

public abstract class TLRPCResponseChannelHandler<T extends TLObject> implements TLRPCResponseHandler {
    public abstract void onChannelNotFound();

    public abstract void onUserHasChannel();
}
