package de.heedlesssoap.pinseekerbackend.entities.DTOs;

import de.heedlesssoap.pinseekerbackend.entities.Pin;
import de.heedlesssoap.pinseekerbackend.entities.enums.PinSize;
import de.heedlesssoap.pinseekerbackend.entities.enums.PinStatus;
import de.heedlesssoap.pinseekerbackend.entities.enums.PinType;
import de.heedlesssoap.pinseekerbackend.utils.DateUtils;

import java.util.Date;

public class PinDTO {
    private Integer pin_id;

    private double latitude;

    private double longitude;

    private String name;

    private PinType type;

    private Boolean premium;

    private Float difficulty;

    private Float terrain;

    private PinSize size;

    private BasicApplicationUserDTO hider;

    private String created_at;

    private String hint;

    private String description;

    private PinStatus status;

    public PinDTO() {
        super();
    }

    public PinDTO(Integer pin_id, double latitude, double longitude, String name, PinType type, Boolean premium, Float difficulty, PinSize size, Float terrain, String created_at, BasicApplicationUserDTO hider, String hint, String description, PinStatus status) {
        this.pin_id = pin_id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.type = type;
        this.premium = premium;
        this.difficulty = difficulty;
        this.size = size;
        this.terrain = terrain;
        this.created_at = created_at;
        this.hider = hider;
        this.hint = hint;
        this.description = description;
        this.status = status;
    }

    public void setPin_id(Integer pin_id) {
        this.pin_id = pin_id;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(PinType type) {
        this.type = type;
    }

    public void setDifficulty(Float difficulty) {
        this.difficulty = difficulty;
    }

    public void setPremium(Boolean premium) {
        this.premium = premium;
    }

    public void setTerrain(Float terrain) {
        this.terrain = terrain;
    }

    public void setSize(PinSize size) {
        this.size = size;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public void setHider(BasicApplicationUserDTO hider) {
        this.hider = hider;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(PinStatus status) {
        this.status = status;
    }

    public double getLatitude() {
        return latitude;
    }

    public Integer getPin_id() {
        return pin_id;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getName() {
        return name;
    }

    public PinType getType() {
        return type;
    }

    public Boolean getPremium() {
        return premium;
    }

    public Float getDifficulty() {
        return difficulty;
    }

    public Float getTerrain() {
        return terrain;
    }

    public PinSize getSize() {
        return size;
    }

    public BasicApplicationUserDTO getHider() {
        return hider;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getHint() {
        return hint;
    }

    public String getDescription() {
        return description;
    }

    public PinStatus getStatus() {
        return status;
    }

    public PinDTO fromPin(Pin pin) {
        this.pin_id = pin.getPinId();
        this.latitude = pin.getLatitude();
        this.longitude = pin.getLongitude();
        this.name = pin.getName();
        this.type = pin.getType();
        this.premium = pin.getPremium();
        this.difficulty = pin.getDifficulty();
        this.terrain = pin.getTerrain();
        this.size = pin.getSize();
        this.hider = new BasicApplicationUserDTO().fromApplicationUser(pin.getHider(), null);
        this.created_at = DateUtils.formatDate(pin.getCreatedAt());
        this.hint = pin.getHint();
        this.description = pin.getDescription();
        this.status = pin.getStatus();
        return this;
    }

    public Pin toPin(){
        Pin pin = new Pin();
        pin.setPinId(this.pin_id);
        pin.setLatitude(this.latitude);
        pin.setLongitude(this.longitude);
        pin.setName(this.name);
        pin.setType(this.type);
        pin.setPremium(this.premium);
        pin.setDifficulty(this.difficulty);
        pin.setTerrain(this.terrain);
        pin.setSize(this.size);
        pin.setCreatedAt(new Date());
        pin.setHint(this.hint);
        pin.setDescription(this.description);
        pin.setStatus(this.status);
        return pin;
    }
}
