package de.heedlesssoap.pinseekerbackend.entities.DTOs;

public class LoginResponseDTO {
    private BasicApplicationUserDTO user;

    private String jwt;

    public LoginResponseDTO(){
        super();
    }

    public LoginResponseDTO(BasicApplicationUserDTO user, String jwt){
        this.user = user;
        this.jwt = jwt;
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
}