package de.heedlesssoap.pinseekerbackend.entities.DTOs;

public class LoginResponseDTO {
    private BasicApplicationUserDTO user;

    private String jwt;

    private String public_rsa_key;

    public LoginResponseDTO(){
        super();
    }

    public LoginResponseDTO(BasicApplicationUserDTO user, String jwt, String public_rsa_key){
        this.user = user;
        this.jwt = jwt;
        this.public_rsa_key = public_rsa_key;
    }

    public BasicApplicationUserDTO getUser(){
        return this.user;
    }

    public void setUser(BasicApplicationUserDTO user){
        this.user = user;
    }

    public String getJwt(){
        return this.jwt;
    }

    public void setJwt(String jwt){
        this.jwt = jwt;
    }

    public String getPublicRsaKey() {
        return public_rsa_key;
    }

    public void setPublicRsaKey(String public_rsa_key) {
        this.public_rsa_key = public_rsa_key;
    }
}