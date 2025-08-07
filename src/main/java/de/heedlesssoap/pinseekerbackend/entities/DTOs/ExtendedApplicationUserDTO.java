package de.heedlesssoap.pinseekerbackend.entities.DTOs;

import de.heedlesssoap.pinseekerbackend.entities.ApplicationUser;
import de.heedlesssoap.pinseekerbackend.entities.enums.LogType;
import de.heedlesssoap.pinseekerbackend.entities.enums.PinType;
import de.heedlesssoap.pinseekerbackend.repositories.LogRepository;
import de.heedlesssoap.pinseekerbackend.repositories.PinRepository;

import java.util.Date;
import java.util.HashMap;

public class ExtendedApplicationUserDTO {
    private Integer user_id;

    private String username;

    private Boolean is_premium;

    private String profile_picture;

    private String profile_location;

    private String email;

    private long joined_at;

    private String about;

    private Integer number_of_finds;

    private Integer number_of_hides;

    private HashMap<PinType, Integer> found_pins;

    private HashMap<PinType, Integer> hidden_pins;

    private Boolean is_profile_private;

    public ExtendedApplicationUserDTO() {
        super();
        this.found_pins = new HashMap<>();
        this.hidden_pins = new HashMap<>();
    }

    public ExtendedApplicationUserDTO(Integer user_id, String username, Boolean is_premium, String profile_picture, String profile_location, String email, long joined_at, String about, Integer number_of_finds, Integer number_of_hides, HashMap<PinType, Integer> found_pins, HashMap<PinType, Integer> hidden_pins, Boolean is_profile_private) {
        this.user_id = user_id;
        this.username = username;
        this.is_premium = is_premium;
        this.profile_picture = profile_picture;
        this.profile_location = profile_location;
        this.email = email;
        this.joined_at = joined_at;
        this.about = about;
        this.number_of_finds = number_of_finds;
        this.number_of_hides = number_of_hides;
        this.found_pins = found_pins;
        this.hidden_pins = hidden_pins;
        this.is_profile_private = is_profile_private;
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

    public String getProfileLocation() {
        return profile_location;
    }

    public void setProfileLocation(String profile_location) {
        this.profile_location = profile_location;
    }

    public String getProfilePicture() {
        return profile_picture;
    }

    public void setProfilePicture(String profile_picture) {
        this.profile_picture = profile_picture;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getJoinedAt() {
        return joined_at;
    }

    public void setJoinedAt(long joined_at) {
        this.joined_at = joined_at;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public Integer getNumberOfFinds() {
        return number_of_finds;
    }

    public void setNumberOfFinds(Integer number_of_finds) {
        this.number_of_finds = number_of_finds;
    }

    public Integer getNumberOfHides() {
        return number_of_hides;
    }

    public void setNumberOfHides(Integer number_of_hides) {
        this.number_of_hides = number_of_hides;
    }

    public HashMap<PinType, Integer> getFoundPins() {
        return found_pins;
    }

    public void setFoundPins(HashMap<PinType, Integer> found_pins) {
        this.found_pins = found_pins;
    }

    public HashMap<PinType, Integer> getHiddenPins() {
        return hidden_pins;
    }

    public void setHiddenPins(HashMap<PinType, Integer> hidden_pins) {
        this.hidden_pins = hidden_pins;
    }

    public Boolean getIsProfilePrivate() {
        return is_profile_private;
    }

    public void setIsProfilePrivate(Boolean is_profile_private) {
        this.is_profile_private = is_profile_private;
    }

    public ExtendedApplicationUserDTO fromApplicationUser(ApplicationUser applicationUser, LogRepository logRepository, PinRepository pinRepository) {
        if(applicationUser.getIsProfilePrivate()){
            this.profile_location = null;
            this.email = null;
            this.about = null;
            this.number_of_finds = null;
            this.number_of_hides = null;
            this.found_pins = null;
            this.hidden_pins = null;
        }else{
            this.profile_location = applicationUser.getProfileLocation();
            this.email = applicationUser.getEmail();
            this.about = applicationUser.getAbout();
            this.number_of_finds = logRepository.getNumberOfPinsByLoggerAndType(applicationUser, LogType.FOUND);
            this.number_of_hides = pinRepository.getNumberOfPinsByHider(applicationUser);

            for(PinType pinType : PinType.values()){
                this.found_pins.put(pinType, logRepository.getNumberOfPinsByLoggerAndLogTypeAndPinType(applicationUser, LogType.FOUND, pinType));
            }

            for(PinType pinType : PinType.values()){
                this.hidden_pins.put(pinType, pinRepository.getNumberOfPinsByHiderAndType(applicationUser, pinType));
            }
        }
        this.user_id = applicationUser.getUserId();
        this.username = applicationUser.getUsername();
        this.is_premium = applicationUser.getIsPremium();
        this.profile_picture = applicationUser.getProfilePicture();
        this.joined_at = applicationUser.getJoinedAt().getTime();
        this.is_profile_private = applicationUser.getIsProfilePrivate();
        return this;
    }
}