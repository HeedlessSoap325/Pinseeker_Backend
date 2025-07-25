package de.heedlesssoap.pinseekerbackend.entities.DTOs;

import de.heedlesssoap.pinseekerbackend.entities.ApplicationUser;

public class ChatApplicationUserDTO {
    private Integer user_id;

    private String username;

    private String profile_picture;

    private String public_rsa_key;

    public ChatApplicationUserDTO() {
        super();
    }

    public ChatApplicationUserDTO(Integer user_id, String username, String profile_picture, String public_rsa_key) {
        this.user_id = user_id;
        this.username = username;
        this.profile_picture = profile_picture;
        this.public_rsa_key = public_rsa_key;
    }

    public Integer getUserId() {
        return user_id;
    }

    public String getUsername() {
        return username;
    }

    public String getProfilePicture() {
        return profile_picture;
    }

    public String getPublicRsaKey() {
        return public_rsa_key;
    }

    public ChatApplicationUserDTO fromApplicationUser(ApplicationUser applicationUser) {
        this.user_id = applicationUser.getUserId();
        this.username = applicationUser.getUsername();
        this.profile_picture = applicationUser.getProfilePicture();
        this.public_rsa_key = applicationUser.getPublicRSAKey();
        return this;
    }
}