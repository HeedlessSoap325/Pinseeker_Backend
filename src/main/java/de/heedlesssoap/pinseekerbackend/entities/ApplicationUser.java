package de.heedlesssoap.pinseekerbackend.entities;

import jakarta.persistence.*;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
public class ApplicationUser implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer user_id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private Boolean is_premium;

    @Column(nullable = false)
    private Boolean has_profile_picture;

    private String profile_picture;

    private String profile_location;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private Date joined_at;

    private String about;

    @Column(nullable = false)
    private Boolean is_profile_private;

    @Column(nullable = false)
    private Boolean is_enabled;

    private String public_rsa_key;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role_junction",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id")}
    )
    private Set<Role> authorities;

    public ApplicationUser() {
        super();
        is_premium = false;
        has_profile_picture = false;
        profile_picture = null;
        email = "";
        joined_at = new Date();
        about = "";
        is_profile_private = false;
        is_enabled = false;
        authorities = new HashSet<>();
    }

    public ApplicationUser(String username, String password, Boolean is_premium, Boolean has_profile_picture, String profile_picture, String profile_location, String email, Date joined_at, String about, Boolean is_profile_private, Boolean is_enabled, String public_rsa_key, Set<Role> authorities) {
        this.username = username;
        this.password = password;
        this.is_premium = is_premium;
        this.has_profile_picture = has_profile_picture;
        this.profile_picture = profile_picture;
        this.profile_location = profile_location;
        this.email = email;
        this.joined_at = joined_at;
        this.about = about;
        this.is_profile_private = is_profile_private;
        this.is_enabled = is_enabled;
        this.public_rsa_key = public_rsa_key;
        this.authorities = authorities;
    }

    public Integer getUserId() {
        return user_id;
    }

    public void setUserId(Integer user_id) {
        this.user_id = user_id;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getIsPremium() {
        return is_premium;
    }

    public void setIsPremium(Boolean premium) {
        is_premium = premium;
    }

    @Override
    public Set<Role> getAuthorities() {
        return this.authorities;
    }

    public void setAuthorities(Set<Role> authorities) {
        this.authorities = authorities;
    }

    public Boolean getHasProfilePicture() {
        return has_profile_picture;
    }

    public void setHasProfilePicture(Boolean hasProfilePicture) {
        this.has_profile_picture = hasProfilePicture;
    }

    public String getProfilePicture() {
        return profile_picture;
    }

    public void setProfilePicture(String profilePictureURL) {
        this.profile_picture = profilePictureURL;
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

    public Date getJoinedAt() {
        return joined_at;
    }

    public void setJoinedAt(Date joined_at) {
        this.joined_at = joined_at;
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

    public String getPublicRSAKey() {
        return public_rsa_key;
    }

    public void setPublicRSAKey(String public_rsa_key) {
        this.public_rsa_key = public_rsa_key;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.is_enabled;
    }

    public void setIsEnabled(Boolean is_enabled) {
        this.is_enabled = is_enabled;
    }

    @Override
    public String toString() {
        return "ApplicationUser{" +
                "user_id=" + user_id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", is_premium=" + is_premium +
                ", has_profile_picture=" + has_profile_picture +
                ", profile_picture='" + profile_picture + '\'' +
                ", profile_location='" + profile_location + '\'' +
                ", email='" + email + '\'' +
                ", joined_at=" + joined_at +
                ", about='" + about + '\'' +
                ", is_profile_private=" + is_profile_private +
                ", is_enabled=" + is_enabled +
                ", public_rsa_key='" + public_rsa_key + '\'' +
                ", authorities=" + authorities +
                '}';
    }
}