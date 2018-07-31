package com.hollybits.socialpetnetwork.models;

import com.hollybits.socialpetnetwork.activity.LoginActivity;

import java.util.List;

/**
 * Created by Victor on 24.07.2018.
 */

public class User {

    private Long id;
    private String authorizationCode;
    private LoginActivity.Credentials credentials;
    private String name;
    private String surname;
    private String phone;
    private Country country;
    private List<Pet> pets;


    public User() {
        credentials = new LoginActivity.Credentials();
    }

    public User(String name, String surname, String phone, Country country) {
        this.name = name;
        this.surname = surname;
        this.phone = phone;
        this.country = country;
    }

    public User(String name, String surname, String phone, Country country, List<Pet> pets) {
        this.name = name;
        this.surname = surname;
        this.phone = phone;
        this.country = country;
        this.pets = pets;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAuthorizationCode() {
        return authorizationCode;
    }

    public void setAuthorizationCode(String authorizationCode) {
        this.authorizationCode = authorizationCode;
    }


    public List<Pet> getPets() {
        return pets;
    }

    public void setPets(List<Pet> pets) {
        this.pets = pets;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public void setCredentials(LoginActivity.Credentials credentials) {
        this.credentials.email = credentials.email;
        this.credentials.password = credentials.password;
    }

    public LoginActivity.Credentials getCredentials() {
        return credentials;
    }
}
