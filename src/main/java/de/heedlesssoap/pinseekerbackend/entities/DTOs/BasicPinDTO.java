package de.heedlesssoap.pinseekerbackend.entities.DTOs;

public class BasicPinDTO {
    private Integer pin_id;

    private double latitude;

    private double longitude;

    public BasicPinDTO() {
        super();
    }

    public BasicPinDTO(Integer pin_id, double latitude, double longitude) {
        this.pin_id = pin_id;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Integer getPinId() {
        return pin_id;
    }

    public void setPinId(Integer pin_id) {
        this.pin_id = pin_id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}