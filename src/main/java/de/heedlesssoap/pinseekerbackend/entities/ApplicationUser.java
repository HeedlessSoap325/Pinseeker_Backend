package de.heedlesssoap.pinseekerbackend.entities;

import jakarta.persistence.*;
import org.springframework.security.core.userdetails.UserDetails;

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
    private Boolean isPremium;

    @Column(nullable = false)
    private Boolean hasProfilePicture;

    private String profilePictureURL;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role_junction",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id")}
    )
    private Set<Role> authorities;

    public ApplicationUser() {
        super();
        authorities = new HashSet<>();
        isPremium = false;
        hasProfilePicture = false;
        profilePictureURL = null;
    }

    public ApplicationUser(String username, String password, Boolean isPremium, Boolean hasProfilePicture, String profilePictureURL, Set<Role> authorities) {
        this.username = username;
        this.password = password;
        this.isPremium = isPremium;
        this.hasProfilePicture = hasProfilePicture;
        this.profilePictureURL = profilePictureURL;
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
        return isPremium;
    }

    public void setIsPremium(Boolean premium) {
        isPremium = premium;
    }

    @Override
    public Set<Role> getAuthorities() {
        return this.authorities;
    }

    public void setAuthorities(Set<Role> authorities) {
        this.authorities = authorities;
    }

    public Boolean getHasProfilePicture() {
        return hasProfilePicture;
    }

    public void setHasProfilePicture(Boolean hasProfilePicture) {
        this.hasProfilePicture = hasProfilePicture;
    }

    public String getProfilePictureURL() {
        return profilePictureURL;
    }

    public void setProfilePictureURL(String profilePictureURL) {
        this.profilePictureURL = profilePictureURL;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public String toString() {
        return "ApplicationUser{" +
                "user_id=" + user_id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", isPremium=" + isPremium +
                ", authorities=" + authorities +
                '}';
    }
}