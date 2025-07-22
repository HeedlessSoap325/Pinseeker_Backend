package de.heedlesssoap.pinseekerbackend.entities.DTOs;

public class FindPinsNearMeDTO {
    private double latitude;

    private double longitude;

    private double radius;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
