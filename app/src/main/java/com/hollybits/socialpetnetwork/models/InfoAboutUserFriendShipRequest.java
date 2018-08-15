package com.hollybits.socialpetnetwork.models;

public class InfoAboutUserFriendShipRequest {
    private Long id;
    private String name;
    private String surname;
    private String city;
    private String country;
    private String pet_name;
    private String pet_breed;

    public InfoAboutUserFriendShipRequest(Long id, String name, String surname, String city, String country, String pet_name, String pet_breed) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.city = city;
        this.country = country;
        this.pet_name = pet_name;
        this.pet_breed = pet_breed;
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPet_name() {
        return pet_name;
    }

    public void setPet_name(String pet_name) {
        this.pet_name = pet_name;
    }

    public String getPet_breed() {
        return pet_breed;
    }

    public void setPet_breed(String pet_breed) {
        this.pet_breed = pet_breed;
    }
}