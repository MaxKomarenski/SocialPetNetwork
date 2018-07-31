package com.hollybits.socialpetnetwork.forms;

import com.hollybits.socialpetnetwork.models.Country;
import com.hollybits.socialpetnetwork.models.Pet;

public class RegistrationForm {
    private String name;
    private String surname;
    private String phone;
    private Country country;
    private String email;
    private String password;
    private Pet pet;

    public RegistrationForm() {
    }

    public RegistrationForm(String name, String surname, String phone, Country country, String email, String password, Pet pet) {
        this.name = name;
        this.surname = surname;
        this.phone = phone;
        this.country = country;
        this.email = email;
        this.password = password;
        this.pet = pet;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Pet getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }
}
