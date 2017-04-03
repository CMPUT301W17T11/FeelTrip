package com.example.henzoshimada.feeltrip;

/**
 * Created by Brett on 2017-03-21.
 * Controller for the usage of filters being used in our application with queries to elastic search
 */
class FilterController {
    private boolean pastweekfilter;
    private boolean mostrecentfilter;
    private boolean friendsonlyfilter;

    private static String emotionfilter;
    private static String keywordfilter;

    /**
     * Instantiates a new Filter controller.
     */
    public FilterController() {
        pastweekfilter = false;
        mostrecentfilter = false;
        friendsonlyfilter = false;
        emotionfilter = "";
        keywordfilter = "";
    }

    /**
     * Is pastweekfilter boolean.
     *
     * @return the boolean
     */
    public boolean isPastweekfilter() {
        return pastweekfilter;
    }

    /**
     * Sets pastweekfilter.
     *
     * @param pastweekfilter the pastweekfilter
     */
    public void setPastweekfilter(boolean pastweekfilter) {
        this.pastweekfilter = pastweekfilter;
    }

    /**
     * Is mostrecentfilter boolean.
     *
     * @return the boolean
     */
    public boolean isMostrecentfilter() {
        return mostrecentfilter;
    }

    /**
     * Sets mostrecentfilter.
     *
     * @param mostrecentfilter the mostrecentfilter
     */
    public void setMostrecentfilter(boolean mostrecentfilter) {
        this.mostrecentfilter = mostrecentfilter;
    }

    /**
     * Is friendsonlyfilter boolean.
     *
     * @return the boolean
     */
    public boolean isFriendsonlyfilter() {
        return friendsonlyfilter;
    }

    /**
     * Sets friendsonlyfilter.
     *
     * @param friendsonlyfilter the friendsonlyfilter
     */
    public void setFriendsonlyfilter(boolean friendsonlyfilter) {
        this.friendsonlyfilter = friendsonlyfilter;
    }

    /**
     * Reset all filters.
     */
    public void resetAllFilters() {
        this.friendsonlyfilter = false;
        this.pastweekfilter = false;
        this.mostrecentfilter = false;
    }

    /**
     * Gets emotionfilter.
     *
     * @return the emotionfilter
     */
    public String getEmotionfilter() {
        return emotionfilter;
    }

    /**
     * Sets emotionfilter.
     *
     * @param emotion the emotion
     */
    public static void setEmotionfilter(String emotion) {
        emotionfilter = emotion;
    }

    /**
     * Gets keywordfilter.
     *
     * @return the keywordfilter
     */
    public String getKeywordfilter() {
        return keywordfilter;
    }

    /**
     * Sets keywordfilter.
     *
     * @param keyword the keyword
     */
    public static void setKeywordfilter(String keyword) {
        keywordfilter = keyword;
    }

}
