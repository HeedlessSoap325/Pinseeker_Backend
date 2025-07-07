package de.heedlesssoap.pinseekerbackend.entities.DTOs;

import de.heedlesssoap.pinseekerbackend.entities.ApplicationUser;

public class ApplicationUserDTO {
    private Integer user_id;

    private String username;

    public ApplicationUserDTO() {
    }

    public ApplicationUserDTO(Integer user_id, String username) {
        this.username = username;
        this.user_id = user_id;
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

    public ApplicationUserDTO fromApplicationUser(ApplicationUser applicationUser) {
        this.user_id = applicationUser.getUserId();
        this.username = applicationUser.getUsername();
        return this;
    }
}