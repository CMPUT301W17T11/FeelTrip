package com.example.henzoshimada.feeltrip;

import java.util.ArrayList;

import io.searchbox.annotations.JestId;

/**
 * Created by Esus2 on 2017-03-20.
 */
public class Participant {
    private String userName;
    private String password;
    private ArrayList<FollowRequest> followRequest;
    private ArrayList<String> following;


    // store most recent location
    private double longitude;
    private double latitude;

    @JestId
    private String id;

    /**
     * Instantiates a new Participant.
     */
    public Participant(){
        this.userName = null;
        this.password = null;
        this.followRequest = new ArrayList<>();
        this.following = new ArrayList<>();
    }

    /**
     * Instantiates a new Participant.
     *
     * @param userName the user name
     * @param password the password
     */
    public Participant(String userName, String password){
        this.userName = userName;
        this.password = password;
        this.followRequest = new ArrayList<>();
        this.following = new ArrayList<>();
    }

    /**
     * Gets user name.
     *
     * @return the user name
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Sets user name.
     *
     * @param userName the user name
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Gets password.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets password.
     *
     * @param password the password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Add all following.
     *
     * @param following the following
     */
    public void addAllFollowing(ArrayList<String> following){
        this.following.addAll(following);
    }

    /**
     * Clear following.
     */
    public void clearFollowing(){
        this.following.clear();
    }

    /**
     * Add following.
     *
     * @param userName the user name
     */
    public void addFollowing(String userName){
        if (!this.following.contains(userName)) {
            this.following.add(userName);
        }
    }

    /**
     * Add all follow request.
     *
     * @param followRequest the follow request
     */
    public void addAllFollowRequest(ArrayList<FollowRequest> followRequest){
        for (FollowRequest request: followRequest){
            if (!this.followRequest.contains(request)){
                this.followRequest.add(request);
            }
        }

    }

    /**
     * Un follow.
     *
     * @param nameUnfollowing the name unfollowing
     */
    public void unFollow(String nameUnfollowing){
        if (this.following.contains(nameUnfollowing)) {
            this.following.remove(nameUnfollowing);
        }
    }


    /*
    public void addFollowRequest(String followerName){
        followRequest.add(followerName);
    }


    public void deleteFollowRequest(String nameWantToDelete){
        if (followRequest.contains(nameWantToDelete)){
            followRequest.remove(nameWantToDelete);
        }
    }
    */

    /**
     * Gets follow request.
     *
     * @return the follow request
     */
    public ArrayList<FollowRequest> getFollowRequest() {
        return followRequest;
    }

    /**
     * Gets following.
     *
     * @return the following
     */
    public ArrayList<String> getFollowing() {
        return following;
    }

    /**
     * Gets longitude.
     *
     * @return the longitude
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Sets longitude.
     *
     * @param longitude the longitude
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /**
     * Gets latitude.
     *
     * @return the latitude
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Sets latitude.
     *
     * @param latitude the latitude
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Sets id.
     *
     * @param id the id
     */
    public void setId(String id) {
        this.id = id;
    }
}
