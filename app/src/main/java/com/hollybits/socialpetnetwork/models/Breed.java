package com.hollybits.socialpetnetwork.models;

import com.hollybits.socialpetnetwork.enums.PetType;

/**
 * Created by Victor on 27.07.2018.
 */

public class Breed {

    private Long id;

    private PetType type;

    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PetType getType() {
        return type;
    }

    public void setType(PetType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
