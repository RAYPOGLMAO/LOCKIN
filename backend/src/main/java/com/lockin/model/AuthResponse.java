package com.lockin.model;

public class AuthResponse {

    private String token;
    private String tokenType;
    private PublicUser user;

    public AuthResponse() {
    }

    public AuthResponse(String token, PublicUser user) {
        this.token = token;
        this.tokenType = "Bearer";
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public PublicUser getUser() {
        return user;
    }

    public void setUser(PublicUser user) {
        this.user = user;
    }
}
