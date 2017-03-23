package com.example.henzoshimada.feeltrip;

import io.searchbox.annotations.JestId;

/**
 * Created by Esus2 on 2017-03-21.
 */

public class FollowRequest {
    private String sender;
    private String receiver;
    @JestId
    private String id;

    public FollowRequest(String sender, String receiver){
        this.sender = sender;
        this.receiver = receiver;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString(){
        return sender;
    }
}
