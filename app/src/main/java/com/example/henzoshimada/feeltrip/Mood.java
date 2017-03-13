package com.example.henzoshimada.feeltrip;

import android.location.Location;

import java.lang.reflect.Array;
import java.lang.reflect.Modifier;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Date;

import io.searchbox.annotations.JestId;

/**
 * Created by Esus2 on 2017-03-07.
 */

public class Mood {
    private String user;
    private String emotionalState;
    private String description;
    private Date date;

    private String socialSit;
    private Boolean isPrivate;

    private byte[] image;
    private Location geoLocation;

    @JestId
    private String id;

    // vector for tracking states of different attributes
    private int size = 7;

    /* Index - attribute mapping:
    0 ----> emotionalStateChanged;
    1 ----> descriptionChanged;
    2 ----> dateChanged;
    3 ----> socialSitChanged;
    4 ----> isPrivateChanged;
    5 ----> imageChanged;
    6 ----> geoLocationChanged;
    */

    private ArrayList<Boolean> stateVector;


    public Mood(){
        user = null;
        emotionalState = null;
        description = null;
        date = null;
        socialSit = null;
        isPrivate = false;
        stateVector = new ArrayList<>(size);

        for (int i = 0; i < size; i++){
            stateVector.add(Boolean.FALSE);
        }

    }


    public Mood(String user){
        this.user = user;
        this.date = new Date();
        stateVector = new ArrayList<>(size);
        isPrivate = false;
        for (int i = 0; i < size; i++){
            stateVector.add(Boolean.FALSE);
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // Evaluate stateVector and check if this mood event is changed.
    public Boolean isChanged(){
        Boolean result = Boolean.FALSE;
        for (int i = 0; i < size; i++){
            result = result || stateVector.get(i);
        }
        return result;
    }

    public ArrayList<Boolean> getAllState(){
        return stateVector;
    }

    public void resetState(){
        for (int i = 0; i < size; i++){
            stateVector.set(i, Boolean.FALSE);
        }
    }

    public Boolean getStateByIndex(int index) {
        return stateVector.get(index);
    }

    public void setStateByIndex(int index){
        stateVector.set(index, Boolean.TRUE);
    }


    public String getUser(){
        return user;
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

    public void setDescription(String description) throws DescriptionTooLongException {
        int count = 0;
        for (int i = 0; i < description.length(); i++){
            if (description.charAt(i) == ' '){
                count++;
            }
        }
        if (description.length() > 20 || count > 2){
            throw new DescriptionTooLongException();
        }else {
            this.description = description;
        }
        setStateByIndex(1);
    }

    public String getSocialSit() {
        return socialSit;
    }

    public void setSocialSit(String socialSit) {
        this.socialSit = socialSit;
        setStateByIndex(3);
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
        isPrivate = !isPrivate;
        setStateByIndex(4);

    }

    public String getMoodOption() {
        return emotionalState;
    }

    public void setMoodOption(String emotionalState) {
        this.emotionalState = emotionalState;
        setStateByIndex(0);
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
        setStateByIndex(2);
    }
}
