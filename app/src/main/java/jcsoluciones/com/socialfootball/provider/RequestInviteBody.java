package jcsoluciones.com.socialfootball.provider;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Admin on 28/08/2016.
 */
public class RequestInviteBody implements Serializable {
    @SerializedName("_creator")
    private String creator;
    private boolean Acceptinvite;
    private String message;
    private int datedayOfMonth;
    private int datemonthOfYear;
    private int dateyear;
    private int timehour;
    private int timeminute;
    private boolean status;
    @SerializedName("friends")
    private String friends;
    private String id;
    private String location;
    private String result;


    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public boolean isAcceptinvite() {
        return Acceptinvite;
    }

    public void setAcceptinvite(boolean acceptinvite) {
        Acceptinvite = acceptinvite;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getDatedayOfMonth() {
        return datedayOfMonth;
    }

    public void setDatedayOfMonth(int datedayOfMonth) {
        this.datedayOfMonth = datedayOfMonth;
    }

    public int getDatemonthOfYear() {
        return datemonthOfYear;
    }

    public void setDatemonthOfYear(int datemonthOfYear) {
        this.datemonthOfYear = datemonthOfYear;
    }

    public int getDateyear() {
        return dateyear;
    }

    public void setDateyear(int dateyear) {
        this.dateyear = dateyear;
    }

    public int getTimehour() {
        return timehour;
    }

    public void setTimehour(int timehour) {
        this.timehour = timehour;
    }

    public int getTimeminute() {
        return timeminute;
    }

    public void setTimeminute(int timeminute) {
        this.timeminute = timeminute;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getFriends() {
        return friends;
    }

    public void setFriends(String friends) {
        this.friends = friends;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
