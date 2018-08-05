package com.hollybits.socialpetnetwork.forms;

/**
 * Created by Victor on 05.08.2018.
 */

public class UpdateTokenForm {

    private String id;
    private String token;
    private DeviceOS deviceOS;

    public void setId(String id) {
        this.id = id;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setDeviceOS(DeviceOS deviceOS) {
        this.deviceOS = deviceOS;
    }
}
