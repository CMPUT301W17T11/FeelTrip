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

    @JestId
    private String id;

    public Participant(){
        this.userName = null;
        this.password = null;
        this.followRequest = new ArrayList<>();
        this.following = new ArrayList<>();
    }

    public Participant(String userName, String password){
        this.userName = userName;
        this.password = password;
        this.followRequest = new ArrayList<>();
        this.following = new ArrayList<>();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void addAllFollowing(ArrayList<String> following){
        this.following.addAll(following);
    }

    public void addAllFollowRequest(ArrayList<FollowRequest> followRequest){
        for (FollowRequest request: followRequest){
            if (!this.followRequest.contains(request)){
                this.followRequest.add(request);
            }
        }

    }

    public void addFollowing(String nameWantToFollow){
        this.following.add(nameWantToFollow);
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

    public ArrayList<FollowRequest> getFollowRequest() {
        return followRequest;
    }

    public ArrayList<String> getFollowing() {
        return following;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
