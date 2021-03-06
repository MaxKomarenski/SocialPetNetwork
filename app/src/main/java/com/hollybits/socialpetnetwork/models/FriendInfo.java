package com.hollybits.socialpetnetwork.models;

import java.sql.Timestamp;

/**
 * Created by Victor on 14.08.2018.
 */

public class FriendInfo {

    private Long id;
    private String name;
    private String surname;
    private String petName;
    private String petBreedName;
    private Timestamp lastActiveTime;

    public FriendInfo() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public String getPetBreedName() {
        return petBreedName;
    }

    public void setPetBreedName(String petBreedName) {
        this.petBreedName = petBreedName;
    }

    public Timestamp getLastActiveTime() {
        return lastActiveTime;
    }
    public void setLastActiveTime(Timestamp lastActiveTime) {
        this.lastActiveTime = lastActiveTime;
    }
}

