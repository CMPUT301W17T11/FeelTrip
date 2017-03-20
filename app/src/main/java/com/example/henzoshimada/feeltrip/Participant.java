package com.example.henzoshimada.feeltrip;

import java.util.ArrayList;

import io.searchbox.annotations.JestId;

/**
 * Created by Esus2 on 2017-03-20.
 */

public class Participant {
    private String userName;
    private String password;
    private ArrayList<String> followers;
    private ArrayList<String> following;

    @JestId
    private String id;

    public Participant(){
        this.userName = null;
        this.password = null;
        this.followers = new ArrayList<>();
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

    public void addFollowing(String nameWantToFollow){
        this.following.add(nameWantToFollow);
    }

    public void addFollower(String followerName){
        followers.add(followerName);
    }

    public ArrayList<String> getFollowers() {
        return followers;
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
