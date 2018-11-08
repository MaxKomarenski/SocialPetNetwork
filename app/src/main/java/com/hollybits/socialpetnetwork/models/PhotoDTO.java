package com.hollybits.socialpetnetwork.models;

/**
 * Created by Victor on 26.10.2018.
 */

public class PhotoDTO {

    private String caption;
    private byte[] photo;


    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }


}
