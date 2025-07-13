package de.heedlesssoap.pinseekerbackend.entities.DTOs;

import de.heedlesssoap.pinseekerbackend.entities.ApplicationUser;

public class UpdateApplicationUserDTO {
    private Integer user_id;

    private String username;

    private String password;

    private String profile_location;

    private String email;

    private String about;

    private Boolean is_profile_private;

    private String public_rsa_key;

    public UpdateApplicationUserDTO() {
        super();
    }

    public UpdateApplicationUserDTO(Integer user_id, String username, String password, String profile_location, String email, String about, Boolean is_profile_private, String public_rsa_key) {
        this.user_id = user_id;
        this.username = username;
        this.password = password;
        this.profile_location = profile_location;
        this.email = email;
        this.about = about;
        this.is_profile_private = is_profile_private;
        this.public_rsa_key = public_rsa_key;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfileLocation() {
        return profile_location;
    }

    public void setProfileLocation(String profile_location) {
        this.profile_location = profile_location;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public Boolean getIsProfilePrivate() {
        return is_profile_private;
    }

    public void setIsProfilePrivate(Boolean is_profile_private) {
        this.is_profile_private = is_profile_private;
    }

    public String getPublicRsaKey() {
        return public_rsa_key;
    }

    public void setPublicRsaKey(String public_rsa_key) {
        this.public_rsa_key = public_rsa_key;
    }

    public ApplicationUser toApplicationUser() {
        ApplicationUser applicationUser = new ApplicationUser();
        applicationUser.setUserId(this.user_id);
        applicationUser.setUsername(this.username);
        applicationUser.setPassword(this.password);
        applicationUser.setProfileLocation(this.profile_location);
        applicationUser.setEmail(this.email);
        applicationUser.setAbout(this.about);
        applicationUser.setIsProfilePrivate(this.is_profile_private);
        applicationUser.setPublicRSAKey(this.public_rsa_key);
        return applicationUser;
    }
}