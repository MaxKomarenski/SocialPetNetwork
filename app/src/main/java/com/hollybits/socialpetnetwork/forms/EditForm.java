package com.hollybits.socialpetnetwork.forms;

import com.hollybits.socialpetnetwork.enums.Attitude;
import com.hollybits.socialpetnetwork.models.Breed;
import com.hollybits.socialpetnetwork.models.City;

public class EditForm {
    private String petName;
    private Breed breed;
    private City city;
    private String ownerName;
    private String email;
    private String phone;
    private Double weight;
    private Long age;
    private Attitude attitude;

    public EditForm() {
    }

    public EditForm(String petName,
                    Breed breed,
                    City city,
                    String ownerName,
                    String email,
                    String phone,
                    Double weight,
                    Long age) {
        this.petName = petName;
        this.breed = breed;
        this.city = city;
        this.ownerName = ownerName;
        this.email = email;
        this.phone = phone;
        this.weight = weight;
        this.age = age;
    }

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public Breed getBreed() {
        return breed;
    }

    public void setBreed(Breed breed) {
        this.breed = breed;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Long getAge() {
        return age;
    }

    public void setAge(Long age) {
        this.age = age;
    }

    public Attitude getAttitude() {
        return attitude;
    }

    public void setAttitude(Attitude attitude) {
        this.attitude = attitude;
    }
}
