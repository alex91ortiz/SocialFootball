package jcsoluciones.com.socialfootball.models;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class RequestTeamBody  implements Serializable {
    @SerializedName("_id")
    private String id;
    private String name;
    private int countComply;
    private int complyValue;
    private String phone;
    private String city;
    private String desc;
    private Boolean active;
    private String email;
    private String registrationId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCountComply() {
        return countComply;
    }

    public void setCountComply(int countComply) {
        this.countComply = countComply;
    }

    public int getComplyValue() {
        return complyValue;
    }

    public void setComplyValue(int complyValue) {
        this.complyValue = complyValue;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }
}
