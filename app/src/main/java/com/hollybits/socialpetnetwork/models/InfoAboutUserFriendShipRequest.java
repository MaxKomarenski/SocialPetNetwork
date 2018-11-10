package com.hollybits.socialpetnetwork.models;

public class InfoAboutUserFriendShipRequest {
    private Long id;
    private String name;
    private Long requestId;
    private String surname;
    private String city;
    private String country;
    private String petName;
    private String petBreed;

    public InfoAboutUserFriendShipRequest() {
    }

    public InfoAboutUserFriendShipRequest(Long id, String name, String surname, String city, String country, String petName, String petBreed) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.city = city;
        this.country = country;
        this.petName = petName;
        this.petBreed = petBreed;
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

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
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

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public String getPetBreed() {
        return petBreed;
    }

    public void setPetBreed(String pet_breed) {
        this.petBreed = pet_breed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InfoAboutUserFriendShipRequest that = (InfoAboutUserFriendShipRequest) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (requestId != null ? !requestId.equals(that.requestId) : that.requestId != null)
            return false;
        if (surname != null ? !surname.equals(that.surname) : that.surname != null) return false;
        if (city != null ? !city.equals(that.city) : that.city != null) return false;
        if (country != null ? !country.equals(that.country) : that.country != null) return false;
        if (petName != null ? !petName.equals(that.petName) : that.petName != null) return false;
        return petBreed != null ? petBreed.equals(that.petBreed) : that.petBreed == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (requestId != null ? requestId.hashCode() : 0);
        result = 31 * result + (surname != null ? surname.hashCode() : 0);
        result = 31 * result + (city != null ? city.hashCode() : 0);
        result = 31 * result + (country != null ? country.hashCode() : 0);
        result = 31 * result + (petName != null ? petName.hashCode() : 0);
        result = 31 * result + (petBreed != null ? petBreed.hashCode() : 0);
        return result;
    }
}