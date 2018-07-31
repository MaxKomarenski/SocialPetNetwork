package com.hollybits.socialpetnetwork.models;

public class Country {
    private Long id;
    private String code;
    private String name;

    public Country(Long id, String code, String name) {
        this.id = id;
        this.code = code;
        this.name = name;
    }

    public Country(String name) {
        this.name = name;
    }

    public Country(){

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
