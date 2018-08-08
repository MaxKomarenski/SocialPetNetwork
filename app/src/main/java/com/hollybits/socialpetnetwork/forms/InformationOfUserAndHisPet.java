package com.hollybits.socialpetnetwork.forms;

import com.hollybits.socialpetnetwork.models.City;
import com.hollybits.socialpetnetwork.models.Pet;

import java.util.List;

public class InformationOfUserAndHisPet {

    private List<Pet> pet;
    private String name;
    private String surname;
    private String phone;
    private City city;

    public InformationOfUserAndHisPet(List<Pet> pet, String name, String surname, String phone, City city) {
        this.pet = pet;
        this.name = name;
        this.surname = surname;
        this.phone = phone;
        this.city = city;
    }

    public List<Pet> getPet() {
        return pet;
    }

    public void setPet(List<Pet> pet) {
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
}
