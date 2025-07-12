package de.heedlesssoap.pinseekerbackend.entities.DTOs;

import de.heedlesssoap.pinseekerbackend.entities.ApplicationUser;
import de.heedlesssoap.pinseekerbackend.entities.enums.LogType;
import de.heedlesssoap.pinseekerbackend.repositories.LogRepository;

public class BasicApplicationUserDTO {
    private Integer user_id;

    private String username;

    private Boolean is_premium;

    private Boolean has_profile_picture;

    private String profile_picture;

    private Integer number_of_finds;

    public BasicApplicationUserDTO() {
        super();
    }

    public BasicApplicationUserDTO(Integer user_id, String username, Boolean is_premium, Boolean has_profile_picture, String profile_picture, Integer number_of_finds) {
        this.user_id = user_id;
        this.username = username;
        this.is_premium = is_premium;
        this.has_profile_picture = has_profile_picture;
        this.profile_picture = profile_picture;
        this.number_of_finds = number_of_finds;
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
        return is_premium;
    }

    public void setIsPremium(Boolean is_premium) {
        this.is_premium = is_premium;
    }

    public Boolean getHasProfilePicture() {
        return has_profile_picture;
    }

    public void setHasProfilePicture(Boolean has_profile_picture) {
        this.has_profile_picture = has_profile_picture;
    }

    public String getProfilePicture() {
        return profile_picture;
    }

    public void setProfilePicture(String profile_picture) {
        this.profile_picture = profile_picture;
    }

    public Integer getNumberOfFinds() {
        return number_of_finds;
    }

    public void setNumberOfFinds(Integer number_of_finds) {
        this.number_of_finds = number_of_finds;
    }

    public BasicApplicationUserDTO fromApplicationUser(ApplicationUser applicationUser, Integer number_of_finds) {
        this.user_id = applicationUser.getUserId();
        this.username = applicationUser.getUsername();
        this.is_premium = applicationUser.getIsPremium();
        this.has_profile_picture = applicationUser.getHasProfilePicture();
        this.profile_picture = applicationUser.getProfilePicture();
        this.number_of_finds = number_of_finds;
        return this;
    }
}
