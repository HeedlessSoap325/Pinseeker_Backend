package de.heedlesssoap.pinseekerbackend.entities.DTOs;

import de.heedlesssoap.pinseekerbackend.entities.ApplicationUser;

public class ApplicationUserDTO {
    private Integer user_id;

    private String username;

    private Boolean isPremium;

    public ApplicationUserDTO() {
    }

    public ApplicationUserDTO(Integer user_id, String username, Boolean isPremium) {
        this.username = username;
        this.user_id = user_id;
        this.isPremium = isPremium;
    }

    public Integer getUserId() {
        return user_id;
    }

    public void setUserId(Integer user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Boolean getIsPremium() {
        return isPremium;
    }

    public void setIsPremium(Boolean premium) {
        isPremium = premium;
    }

    public ApplicationUserDTO fromApplicationUser(ApplicationUser applicationUser) {
        this.user_id = applicationUser.getUserId();
        this.username = applicationUser.getUsername();
        this.isPremium = applicationUser.getIsPremium();
        return this;
    }
}