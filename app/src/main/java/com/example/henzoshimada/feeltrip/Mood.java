package com.example.henzoshimada.feeltrip;

// Removed unused imports

import android.text.Html;
import android.text.TextUtils;

import org.w3c.dom.Text;

import android.graphics.Bitmap;


import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

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

    private int emoji;

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
    7 ----> emoji image
    */

    private static boolean[] stateVector;


    public Mood() {
        username = null;
        emotionalState = "";
        description = "";
        date = 0L;
        socialSit = "";
        isPrivate = false;
        image = null;
        latitude = null;
        longitude = null;
        stateVector = new boolean[size];
        delState = false;
        made = null;
        id = null;
        emoji = 0;
    }


    public Mood(String user) {
        this.username = user;
        emotionalState = "";
        description = "";
        socialSit = "";
        trueDate = new Date();
        date = trueDate.getTime();
        isPrivate = false;
        image = null;
        latitude = null;
        longitude = null;
        stateVector = new boolean[size];
        delState = false;
        made = null;
        id = null;
        emoji = 0;
    }

    public String getId() {
        if (id == null) { //mood does not yet exist within the Elasticsearch database
            return "-1";
        } else {//return ID that the mood is stored under in the Elasticsearch database
            return id;
        }
    }

    public void setId(String id) {
        if (this.id != id) {
            this.id = id;
        }
    }

    // Evaluate stateVector and check if this mood event is changed.
    public boolean isChanged() {
        for (boolean value : stateVector) {
            if (value) {
                return true;
            }
        }
        return false;
    }

    public boolean[] getAllState() {
        return stateVector;
    }

    public void resetState() {
        Arrays.fill(stateVector, false);
    }

    public boolean getStateByIndex(int index) {
        return stateVector[index];
    }

    public void setStateByIndex(int index) {
        if (!stateVector[index]) {
            stateVector[index] = true;
        }
    }


    public String getUsername() {
        return username;
    }

    public String getImage() {
        if (image != null) {
            return Html.fromHtml(image).toString();
        }
        return image;
    }

    public String getEmotionalState() {
        return emotionalState;
    }

    public void setEmotionalState(String emotionalState) {
        if (!getEmotionalState().equals(emotionalState)) {
            this.emotionalState = emotionalState;
            setStateByIndex(0);
        }
    }

    public void setImage(String image) {
        if (getImage() == null) {
            this.image = TextUtils.htmlEncode(image);
            setStateByIndex(5);
        } else if (!getImage().equals(image)) {
            this.image = TextUtils.htmlEncode(image);
            setStateByIndex(5);
        }
    }

    public String getDescription() {
        return Html.fromHtml(description).toString();
    }

    public void setDescription(String descriptionin) throws DescriptionTooLongException {
        if (!getDescription().equals(descriptionin)) {
            int count = 0;
            String description = descriptionin.trim();
            for (int i = 0; i < description.length(); i++) {
                if (description.charAt(i) == ' ') {
                    count++;
                }
            }
            if (description.length() > 20 || count > 2) {
                throw new DescriptionTooLongException();
            } else {
                this.description = TextUtils.htmlEncode(description);
            }
            setStateByIndex(1);
        }
    }

    public String getSocialSit() {
        return socialSit;
    }

    public void setSocialSit(String socialSit) {
        if (!getSocialSit().equals(socialSit)) {
            this.socialSit = socialSit;
            setStateByIndex(3);
        }
    }

    public void setMapPosition(Double latitude, Double longitude) {
        if (this.location == null) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.location = latitude + ", " + longitude;
            setStateByIndex(6);
        } else if (!getLocation().equals(latitude + ", " + longitude)) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.location = latitude + ", " + longitude;
            setStateByIndex(6);
        }
    }

    public Double[] getMapPosition() {
        Double[] loc = new Double[2];
        loc[0] = this.latitude;
        loc[1] = this.longitude;
        return loc;
    }

    public boolean getPrivate() {
        return isPrivate;
    }

    public void setPrivate() {
        if (!this.isPrivate) {
            isPrivate = true;
            setStateByIndex(4);
        }
    }

    public void setPublic() {
        if (this.isPrivate) {
            isPrivate = false;
            setStateByIndex(4);
        }
    }

    public String getMoodOption() {
        return emotionalState;
    }

    public void setMoodOption(String emotionalState) {
        if (!getEmotionalState().equals(emotionalState)) {
            this.emotionalState = emotionalState;
            setStateByIndex(0);
        }
    }

    public Date getDate() {
        trueDate = new Date(date);
        return trueDate;
    }

    public void setDate(Date date) {
        if (this.date != date.getTime()) {
            this.date = date.getTime();
            this.trueDate = date;
            setStateByIndex(2);
        }
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
        List<String> loclist = Arrays.asList(location.split(","));
        return Double.parseDouble(loclist.get(0).trim());
    }

    public void setLatitude(Double latitude) {
        if (getLatitude() != latitude) {
            this.latitude = latitude;
            this.location = latitude + ", " + longitude;
            setStateByIndex(6);
        }
    }

    public Double getLongitude() {
        List<String> loclist = Arrays.asList(location.split(","));
        return Double.parseDouble(loclist.get(1).trim());
    }

    public void setLongitude(Double longitude) {
        if (getLongitude() != longitude) {
            this.longitude = longitude;
            this.location = latitude + ", " + longitude;
            setStateByIndex(6);
        }
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

    public void setNullImage() {
        if (this.image != null) {
            this.image = null;
            setStateByIndex(5);
        }
    }

    public void setNullLocation() {
        if (this.location != null) {
            this.location = null;
            setStateByIndex(6);
        }
    }

    public int getEmoji() {
        return emoji;
    }

    public void setEmoji(int emoji) {
        if(this.emoji != emoji) {
            this.emoji = emoji;
            setStateByIndex(7);
        }
    }

}
