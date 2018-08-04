package com.hollybits.socialpetnetwork.models;

import com.hollybits.socialpetnetwork.enums.MassUnit;

public class Weight {

    private Double mass;
    private MassUnit massUnit;

    public Weight(Double mass, MassUnit massUnit) {
        this.mass = mass;
        this.massUnit = massUnit;
    }

    public Double getMass() {
        return mass;
    }

    public void setMass(Double mass) {
        this.mass = mass;
    }

    public MassUnit getMassUnit() {
        return massUnit;
    }

    public void setMassUnit(MassUnit massUnit) {
        this.massUnit = massUnit;
    }
}
