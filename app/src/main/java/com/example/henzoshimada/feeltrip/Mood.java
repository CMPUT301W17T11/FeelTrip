package com.example.henzoshimada.feeltrip;

import android.location.Location;
import java.util.Date;

/**
 * Created by Esus2 on 2017-03-07.
 */

public class Mood {
    private String user;
    private String mood;
    private String description;
    private Date date;

    private String socialSit;
    private Boolean isPrivate;

    private byte[] image;
    private Location geoLocation;

    public Mood(){
        user = null;
        mood = null;
        description = null;
        date = null;
        socialSit = null;
        isPrivate = false;
    }


    public Mood(String user, String description){
        this.user = user;
        this.description = description;
        this.date = new Date();

    }



    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSocialSit() {
        return socialSit;
    }

    public void setSocialSit(String socialSit) {
        this.socialSit = socialSit;
    }

    public void setMapPosition(Location location){
        this.geoLocation = location;
    }

    public Location getMapPosition(){
        return this.geoLocation;
    }

    public Boolean getPrivate() {
        return isPrivate;
    }

    public void setPrivate() {
        isPrivate = true;
    }

    public String getMood() {
        return mood;
    }

    public void setHappyMood() {
        this.mood = "Happy";
    }

    public void setSadMood() {
        this.mood = "Sad";
    }
    // more moods goes here
}
