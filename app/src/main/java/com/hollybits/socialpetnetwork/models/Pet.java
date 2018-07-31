package com.hollybits.socialpetnetwork.models;

import com.hollybits.socialpetnetwork.enums.Attitude;
import com.hollybits.socialpetnetwork.enums.Sex;

public class Pet {
    private Long id;
    private String name;
    private Breed breed;
    private Long age;
    private Sex sex;
    private String tagNumber;
    private Long weight;
    private Attitude attitude;

    public Pet(){

    }

    public Pet(String name, Breed breed, Long age, Sex sex, String tagNumber, Long weight, Attitude attitude) {
        this.name = name;
        this.breed = breed;
        this.age = age;
        this.sex = sex;
        this.tagNumber = tagNumber;
        this.weight = weight;
        this.attitude = attitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Breed getBreed() {
        return breed;
    }

    public void setBreed(Breed breed) {
        this.breed = breed;
    }

    public Long getAge() {
        return age;
    }

    public void setAge(Long age) {
        this.age = age;
    }

    public Sex getSex() {
        return sex;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public String getTagNumber() {
        return tagNumber;
    }

    public void setTagNumber(String tagNumber) {
        this.tagNumber = tagNumber;
    }

    public Long getWeight() {
        return weight;
    }

    public void setWeight(Long weight) {
        this.weight = weight;
    }

    public Attitude getAttitude() {
        return attitude;
    }

    public void setAttitude(Attitude attitude) {
        this.attitude = attitude;
    }
}
