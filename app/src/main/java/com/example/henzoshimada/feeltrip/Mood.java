package com.example.henzoshimada.feeltrip;

// Removed unused imports
import java.util.Arrays;
import java.util.Date;

import io.searchbox.annotations.JestId;

/**
 * Created by Esus2 on 2017-03-07.
 */

public class Mood {
    private String username;

    private String emotionalState;
    private String description;
    private long date; //for actually storing value in Elasticsearch, since it doesn't have a Date type
    private static Date trueDate; //the real date as a Date object

    private String socialSit;
    private boolean isPrivate;

    private String image;

    private static Double latitude;
    private static Double longitude;

    private String location;

    private Long made; //for storing when the mood event is first posted to elasticsearch

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
    6 ----> locationChanged;
    */

    private static boolean[] stateVector;


    public Mood(){
        username = null;
        emotionalState = null;
        description = null;
        date = 0L;
        socialSit = null;
        isPrivate = false;
        image = null;
        latitude = null;
        longitude = null;
        stateVector = new boolean[size];
        delState = false;
        made = null;
    }


    public Mood(String user){
        this.username = user;
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
        made = null;
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


    public String getUsername(){
        return username;
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

    public void setDescription(String descriptionin, String append) throws DescriptionTooLongException {
        int count = 0;
        String description = descriptionin.trim();
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
        this.location = latitude + ", " + longitude;
        setStateByIndex(6);
    }

    public Double[] getMapPosition(){
        Double[] loc = new Double[2];
        loc[0] = this.latitude;
        loc[1] = this.longitude;
        return loc;
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
        this.location = latitude + ", " + longitude;
        setStateByIndex(6);
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
        this.location = latitude + ", " + longitude;
        setStateByIndex(6);
    }

    public Long getMade() {
        return made;
    }

    public void setMade(Date date) {
        this.made = date.getTime();
    }

    public String getLocation() {
        return location;
    }

}
