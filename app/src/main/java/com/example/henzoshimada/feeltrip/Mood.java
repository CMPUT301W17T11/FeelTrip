package com.example.henzoshimada.feeltrip;

// Removed unused imports
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import io.searchbox.annotations.JestId;

/**
 * Created by Esus2 on 2017-03-07.
 */

public class Mood {
    private String user;

    private String emotionalState;
    private String description;
    private long date; //for actually storing value in Elasticsearch, since it doesn't have a Date type
    private static Date trueDate; //the real date as a Date object

    private String socialSit;
    private boolean isPrivate;

    private String image;

    private Double latitude;
    private Double longitude;

    private static boolean delState; // 1 for delete, 0 for add
    //TODO: Test deleting a mood from ES after popping from local queue

    @JestId
    private String id;

    // vector for tracking states of different attributes
    private static final int size = 8;

    public static int getSize() {
        return size;
    }

    /* Index - attribute mapping:
    0 ----> emotionalStateChanged;
    1 ----> descriptionChanged;
    2 ----> dateChanged;
    3 ----> socialSitChanged;
    4 ----> isPrivateChanged;
    5 ----> imageChanged;
    6 ----> latitudeChanged;
    7 ----> longitudeChanged;
    */

    private static boolean[] stateVector;


    public Mood(){
        user = null;
        emotionalState = null;
        description = null;
        date = 0;
        socialSit = null;
        isPrivate = false;
        image = null;
        latitude = null;
        longitude = null;
        stateVector = new boolean[size];
        delState = false;
    }


    public Mood(String user){
        this.user = user;
        emotionalState = null;
        description = null;
        socialSit = null;
        trueDate = new Date();
        date = trueDate.getTime();
        isPrivate = false;
        image = null;
        latitude = null;
        longitude = null;
        stateVector = new boolean[size];
        delState = false;
    }

    public String getId() {
        if (id == null){ //mood does not yet exist within the Elasticsearch database
            return "-1";
        }
        else {//return ID that the mood is stored under in the Elasticsearch database
            return id;
        }
    }

    public void setId(String id) {
        this.id = id;
    }

    // Evaluate stateVector and check if this mood event is changed.
    public boolean isChanged(){
        for (boolean value: stateVector){
            if(value){return true;}
        }
        return false;
    }

    public boolean[] getAllState(){
        return stateVector;
    }

    public void resetState(){
        Arrays.fill(stateVector, false);
    }

    public boolean getStateByIndex(int index) {
        return stateVector[index];
    }

    public void setStateByIndex(int index){
        stateVector[index] = true;
    }


    public String getUser(){
        return user;
    }

    public String getImage() {
        return image;
    }

    public String getEmotionalState() {
        return emotionalState;
    }

    public void setEmotionalState(String emotionalState) {
        this.emotionalState = emotionalState;
        setStateByIndex(0);
    }

    public void setImage(String image) {
        this.image = image;
        setStateByIndex(5);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description, String append) throws DescriptionTooLongException {
        int count = 0;
        for (int i = 0; i < description.length(); i++){
            if (description.charAt(i) == ' '){
                count++;
            }
        }
        if (description.length() > 20 || count > 2){
            throw new DescriptionTooLongException();
        }else {
            this.description = description + append;
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

    public void setMapPosition(Double latitude, Double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
        setStateByIndex(6);
        setStateByIndex(7);
    }

    public Double[] getMapPosition(){
        Double[] location = new Double[2];
        location[0] = this.latitude;
        location[1] = this.longitude;
        return location;
    }

    public boolean getPrivate() {
        return isPrivate;
    }

    public void setPrivate() {
        isPrivate = true;
        setStateByIndex(4);
    }

    public void setPublic() {
        isPrivate = false;
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
        trueDate = new Date(date);
        return trueDate;
    }

    public void setDate(Date date) {
        this.date = date.getTime();
        this.trueDate = date;
        setStateByIndex(2);
    }

    public void setDel() {
        this.delState = true;
    }

    public void setAdd() {
        this.delState = false;
    }

    public boolean getDelState() {
        return delState;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
        setStateByIndex(6);
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
        setStateByIndex(7);
    }


}
