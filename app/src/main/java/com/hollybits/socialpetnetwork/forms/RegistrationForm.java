package com.hollybits.socialpetnetwork.forms;

import com.hollybits.socialpetnetwork.models.City;
import com.hollybits.socialpetnetwork.models.Country;
import com.hollybits.socialpetnetwork.models.Pet;

public class RegistrationForm {

    private String name;
    private String surname;
    private String phone;
    private City city;
    private String email;
    private String password;
    private Pet pet;

    public RegistrationForm(String name, String surname, String phone, City city, String email, String password, Pet pet) {
        this.name = name;
        this.surname = surname;
        this.phone = phone;
        this.city = city;
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

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
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
