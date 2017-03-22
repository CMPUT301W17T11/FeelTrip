package com.example.henzoshimada.feeltrip;

/**
 * Created by Brett on 2017-03-21.
 */

class FilterController {
    private boolean pastweekfilter;
    private boolean mostrecentfilter;
    private boolean friendsonlyfilter;

    private String emotionfilter;
    private String keywordfilter;

    public FilterController() {
        pastweekfilter = false;
        mostrecentfilter = false;
        friendsonlyfilter = false;
        emotionfilter = "";
        keywordfilter = "";
    }

    public boolean isPastweekfilter() {
        return pastweekfilter;
    }

    public void setPastweekfilter(boolean pastweekfilter) {
        this.pastweekfilter = pastweekfilter;
    }

    public boolean isMostrecentfilter() {
        return mostrecentfilter;
    }

    public void setMostrecentfilter(boolean mostrecentfilter) {
        this.mostrecentfilter = mostrecentfilter;
    }

    public boolean isFriendsonlyfilter() {
        return friendsonlyfilter;
    }

    public void setFriendsonlyfilter(boolean friendsonlyfilter) {
        this.friendsonlyfilter = friendsonlyfilter;
    }

    public void resetAllFilters() {
        this.friendsonlyfilter = false;
        this.pastweekfilter = false;
        this.mostrecentfilter = false;
    }

    public String getEmotionfilter() {
        return emotionfilter;
    }

    public void setEmotionfilter(String emotionfilter) {
        this.emotionfilter = emotionfilter;
    }

    public String getKeywordfilter() {
        return keywordfilter;
    }

    public void setKeywordfilter(String keywordfilter) {
        this.keywordfilter = keywordfilter;
    }

}
