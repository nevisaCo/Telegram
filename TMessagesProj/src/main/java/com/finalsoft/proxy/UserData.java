package com.finalsoft.proxy;

import com.finalsoft.SharedStorage;

import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.messenger.ApplicationLoader;

public class UserData {
  public String id;
  String deviceId;
  public String rsa;
  String uniqueId;
  public String packageName;

  public JSONObject getJson() {
    try {
      JSONObject j = new JSONObject();
      j.put("Id", SharedStorage.repositoryId());
      j.put("DeviceId", deviceId);
      j.put("RSA", ApplicationLoader.RSA);
      j.put("Token", uniqueId);
      j.put("PackageName", packageName);
      return j;
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return null;
  }
}
