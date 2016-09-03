package jcsoluciones.com.socialfootball.models;

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
    private String datedayOfMonth;
    private String datemonthOfYear;
    private String dateyear;
    private boolean status;

    @SerializedName("friends")
    private String friends;

    private String id;

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getFriends() {
        return friends;
    }

    public void setFriends(String friends) {
        this.friends = friends;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getDateyear() {
        return dateyear;
    }

    public void setDateyear(String dateyear) {
        this.dateyear = dateyear;
    }

    public String getDatemonthOfYear() {
        return datemonthOfYear;
    }

    public void setDatemonthOfYear(String datemonthOfYear) {
        this.datemonthOfYear = datemonthOfYear;
    }

    public String getDatedayOfMonth() {
        return datedayOfMonth;
    }

    public void setDatedayOfMonth(String datedayOfMonth) {
        this.datedayOfMonth = datedayOfMonth;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isAcceptinvite() {
        return Acceptinvite;
    }

    public void setAcceptinvite(boolean acceptinvite) {
        Acceptinvite = acceptinvite;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
