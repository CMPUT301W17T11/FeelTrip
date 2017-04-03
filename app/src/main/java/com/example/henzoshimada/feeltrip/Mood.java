package com.example.henzoshimada.feeltrip;

// Removed unused imports

import android.text.Html;
import android.text.TextUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import io.searchbox.annotations.JestId;

/**
 * Created by Esus2 on 2017-03-07.
 *
 * This mood class is the mood object that will store everything that is associated with a mood
 * this include the username and the description and the image, etc.
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

    private boolean delState; // 1 for delete, 0 for add

    private int emoji;

    @JestId
    private String id;

    // vector for tracking states of different attributes
    private static final int size = 8;

    /**
     * Gets size.
     *
     * @return the size
     */
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

    private boolean[] stateVector;


    /**
     * Instantiates a new Mood.
     */
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
        resetState();
        delState = false;
        made = null;
        id = null;
        emoji = 0;
    }


    /**
     * Instantiates a new Mood.
     *
     * @param user the user
     */
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
        resetState();
        delState = false;
        made = null;
        id = null;
        emoji = 0;
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public String getId() {
        if (id == null) { //mood does not yet exist within the Elasticsearch database
            return "-1";
        } else {//return ID that the mood is stored under in the Elasticsearch database
            return id;
        }
    }

    /**
     * Sets id.
     *
     * @param id the id
     */
    public void setId(String id) {
            this.id = id;
    }

    /**
     * Is changed boolean.
     *
     * @return the boolean
     */
// Evaluate stateVector and check if this mood event is changed.
    public boolean isChanged() {
        for (boolean value : stateVector) {
            if (value) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get all state boolean [ ].
     *
     * @return the boolean [ ]
     */
    public boolean[] getAllState() {
        return stateVector;
    }

    /**
     * Reset state.
     */
    public void resetState() {
        Arrays.fill(stateVector, false);
    }

    /**
     * Gets state by index.
     *
     * @param index the index
     * @return the state by index
     */
    public boolean getStateByIndex(int index) {
        return stateVector[index];
    }

    private void setStateByIndex(int index) {
        if (!stateVector[index]) {
            stateVector[index] = true;
        }
    }


    /**
     * Gets username.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets image.
     *
     * @return the image
     */
    public String getImage() {
        if (image != null) {
            return Html.fromHtml(image).toString();
        }
        return image;
    }

    /**
     * Gets emotional state.
     *
     * @return the emotional state
     */
    public String getEmotionalState() {
        return emotionalState;
    }

    /**
     * Sets emotional state.
     *
     * @param emotionalState the emotional state
     */
    public void setEmotionalState(String emotionalState) {
        if (!getEmotionalState().equals(emotionalState)) {
            this.emotionalState = emotionalState;
            setStateByIndex(0);
        }
    }

    /**
     * Sets image.
     *
     * @param image the image
     */
    public void setImage(String image) {
        if (getImage() == null) {
            this.image = TextUtils.htmlEncode(image);
            setStateByIndex(5);
        } else if (!getImage().equals(image)) {
            this.image = TextUtils.htmlEncode(image);
            setStateByIndex(5);
        }
    }

    /**
     * Gets description.
     *
     * @return the description
     */
    public String getDescription() {
        return Html.fromHtml(description).toString();
    }

    /**
     * Sets description.
     *
     * @param descriptionin the descriptionin
     * @throws DescriptionTooLongException the description too long exception
     */
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

    /**
     * Gets social sit.
     *
     * @return the social sit
     */
    public String getSocialSit() {
        return socialSit;
    }

    /**
     * Sets social sit.
     *
     * @param socialSit the social sit
     */
    public void setSocialSit(String socialSit) {
        if (!getSocialSit().equals(socialSit)) {
            this.socialSit = socialSit;
            setStateByIndex(3);
        }
    }

    /**
     * Sets map position.
     *
     * @param latitude  the latitude
     * @param longitude the longitude
     */
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

    /**
     * Get map position double [ ].
     *
     * @return the double [ ]
     */
    public Double[] getMapPosition() {
        Double[] loc = new Double[2];
        loc[0] = this.latitude;
        loc[1] = this.longitude;
        return loc;
    }

    /**
     * Gets private.
     *
     * @return the private
     */
    public boolean getPrivate() {
        return isPrivate;
    }

    /**
     * Sets private.
     */
    public void setPrivate() {
        if (!this.isPrivate) {
            isPrivate = true;
            setStateByIndex(4);
        }
    }

    /**
     * Sets public.
     */
    public void setPublic() {
        if (this.isPrivate) {
            isPrivate = false;
            setStateByIndex(4);
        }
    }

    /**
     * Gets mood option.
     *
     * @return the mood option
     */
    public String getMoodOption() {
        return emotionalState;
    }

    /**
     * Sets mood option.
     *
     * @param emotionalState the emotional state
     */
    public void setMoodOption(String emotionalState) {
        if (!getEmotionalState().equals(emotionalState)) {
            this.emotionalState = emotionalState;
            setStateByIndex(0);
        }
    }

    /**
     * Gets date.
     *
     * @return the date
     */
    public Date getDate() {
        trueDate = new Date(date);
        return trueDate;
    }

    /**
     * Sets date.
     *
     * @param date the date
     */
    public void setDate(Date date) {
        if (this.date != date.getTime()) {
            this.date = date.getTime();
            this.trueDate = date;
            setStateByIndex(2);
        }
    }

    /**
     * Sets del.
     */
    public void setDel() {
        this.delState = true;
    }

    /**
     * Sets add.
     */
    public void setAdd() {
        this.delState = false;
    }

    /**
     * Gets del state.
     *
     * @return the del state
     */
    public boolean getDelState() {
        return delState;
    }

    /**
     * Gets latitude.
     *
     * @return the latitude
     */
    public Double getLatitude() {
        List<String> loclist = Arrays.asList(location.split(","));
        return Double.parseDouble(loclist.get(0).trim());
    }

    /**
     * Sets latitude.
     *
     * @param latitude the latitude
     */
    public void setLatitude(Double latitude) {
        if (getLatitude() != latitude) {
            this.latitude = latitude;
            this.location = latitude + ", " + longitude;
            setStateByIndex(6);
        }
    }

    /**
     * Gets longitude.
     *
     * @return the longitude
     */
    public Double getLongitude() {
        List<String> loclist = Arrays.asList(location.split(","));
        return Double.parseDouble(loclist.get(1).trim());
    }

    /**
     * Sets longitude.
     *
     * @param longitude the longitude
     */
    public void setLongitude(Double longitude) {
        if (getLongitude() != longitude) {
            this.longitude = longitude;
            this.location = latitude + ", " + longitude;
            setStateByIndex(6);
        }
    }

    /**
     * Gets made.
     *
     * @return the made
     */
    public Long getMade() {
        return made;
    }

    /**
     * Sets made.
     *
     * @param date the date
     */
    public void setMade(Date date) {
        this.made = date.getTime();
    }

    /**
     * Gets location.
     *
     * @return the location
     */
    public String getLocation() {
        return location;
    }

    /**
     * Sets null image.
     */
    public void setNullImage() {
        if (this.image != null) {
            this.image = null;
            setStateByIndex(5);
        }
    }

    /**
     * Sets null location.
     */
    public void setNullLocation() {
        if (this.location != null) {
            this.location = null;
            setStateByIndex(6);
        }
    }

    /**
     * Gets emoji.
     *
     * @return the emoji
     */
    public int getEmoji() {
        return emoji;
    }

    /**
     * Sets emoji.
     *
     * @param emoji the emoji
     */
    public void setEmoji(int emoji) {
        if(this.emoji != emoji) {
            this.emoji = emoji;
            setStateByIndex(7);
        }
    }

}
