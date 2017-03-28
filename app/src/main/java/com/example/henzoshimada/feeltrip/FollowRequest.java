package com.example.henzoshimada.feeltrip;

import io.searchbox.annotations.JestId;

/**
 * Created by Esus2 on 2017-03-21.
 */

public class FollowRequest {
    private String sender;
    private String receiver;
    private boolean accepted;
    @JestId
    private String id;

    public FollowRequest(String sender, String receiver){
        this.sender = sender;
        this.receiver = receiver;
        accepted = false;
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

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    @Override
    public String toString(){
        return sender;
    }
}
