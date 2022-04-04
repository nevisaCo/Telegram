package com.finalsoft.proxy.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Proxy implements Serializable {
    @SerializedName("a")
    private String ip;

    @SerializedName("u")
    private String user;

    @SerializedName("p")
    private int port;

    @SerializedName("pw")
    private String password;

    @SerializedName("s")
    private String secret;

    @SerializedName("sponser")
    private boolean sponsor;

    @SerializedName("l")
    private boolean lock;

    private boolean ss;




    public String getIp() {
        return ip;
    }

    public String getUser() {
        return user;
    }

    public int getPort() {
        return port;
    }

    public String getPassword() {
        return password;
    }

    public String getSecret() {
        return secret;
    }

    public boolean isSponsor() {
        return sponsor || ss;
    }
}
