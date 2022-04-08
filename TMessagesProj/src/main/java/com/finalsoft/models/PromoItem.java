package com.finalsoft.models;

import com.google.gson.annotations.SerializedName;

import org.telegram.tgnet.TLRPC;

import java.util.ArrayList;

public class PromoItem {

    @SerializedName("link")
    private String link;
    @SerializedName("label")
    private String label;
    @SerializedName("last_message")
    private String last_message;
    @SerializedName("tabs")
    int[] tabs;
    TLRPC.Dialog dialog;

    public PromoItem(String link, String label, String last_message, int[] tabs) {
        this.link = link;
        this.label = label;
        this.last_message = last_message;
        this.tabs = tabs;

    }

    public String getLink() {
        return link;
    }

    public int[] getTabs() {
        return tabs;
    }

    public String getLast_message() {
        return last_message;
    }

    public TLRPC.Dialog getDialog() {
        return dialog;
    }

    public String getLabel() {
        return label;
    }

    public void setDialog(TLRPC.Dialog dialog) {
        this.dialog = dialog;
    }
}
