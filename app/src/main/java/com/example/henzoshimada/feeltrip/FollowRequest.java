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

    /**
     * Instantiates a new Follow request.
     * The FollowRequest class is what keeps track of the Participant, as the sender, that wants to
     * follow a certain user, as the receiver. It will also keep track of whether or not the receiver
     * has accepted the request of the sender or not.
     *
     * @param sender   the sender
     * @param receiver the receiver
     */
    public FollowRequest(String sender, String receiver){
        this.sender = sender;
        this.receiver = receiver;
        accepted = false;
    }

    /**
     * Gets sender.
     *
     * @return the sender
     */
    public String getSender() {
        return sender;
    }

    /**
     * Sets sender.
     *
     * @param sender the sender
     */
    public void setSender(String sender) {
        this.sender = sender;
    }

    /**
     * Gets receiver.
     *
     * @return the receiver
     */
    public String getReceiver() {
        return receiver;
    }

    /**
     * Sets receiver.
     *
     * @param receiver the receiver
     */
    public void setReceiver(String receiver) {
        this.receiver = receiver;
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

    /**
     * Is accepted boolean.
     *
     * @return the boolean
     */
    public boolean isAccepted() {
        return accepted;
    }

    /**
     * Sets accepted.
     *
     * @param accepted the accepted
     */
    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    @Override
    public String toString(){
        return sender;
    }
}
