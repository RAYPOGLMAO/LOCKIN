package com.lockin.model;

import java.time.LocalDateTime;

public class PublicUser {

    private long id;
    private String username;
    private String email;
    private LocalDateTime createdAt;

    public PublicUser() {
    }

    public static PublicUser from(User user) {
        PublicUser dto = new PublicUser();
        dto.id = user.getId();
        dto.username = user.getUsername();
        dto.email = user.getEmail();
        dto.createdAt = user.getCreatedAt();
        return dto;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
