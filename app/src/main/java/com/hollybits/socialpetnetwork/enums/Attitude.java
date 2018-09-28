package com.hollybits.socialpetnetwork.enums;

/**
 * Created by Victor on 28.06.2018.
 */
public enum Attitude {
    GOODWITHALL("Good with all"),
    GOODWITHMALE("Good only with male"),
    GOODWITHFEMALE("Good only with female"),
    BAD("Bad");

    private String name;

    Attitude(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
