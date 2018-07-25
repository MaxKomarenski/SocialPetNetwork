package com.hollybits.socialpetnetwork.models;

import com.hollybits.socialpetnetwork.activity.LoginActivity;

/**
 * Created by Victor on 24.07.2018.
 */

public class User {

    private Long id;
    private String authorizationCode;
    private LoginActivity.Credentials credentials;







    public User() {
        credentials = new LoginActivity.Credentials();
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


    public void setCredentials(LoginActivity.Credentials credentials) {
        this.credentials.email = credentials.email;
        this.credentials.password = credentials.password;
    }

    public LoginActivity.Credentials getCredentials() {
        return credentials;
    }
}
