package com.example.henzoshimada.feeltrip;

import io.searchbox.annotations.JestId;

/**
 * Created by Brett on 2017-03-20.
 */

class User {
    private String username;
    private String password;

    @JestId
    private String id;

    public User(String name, String pass) {
        username = name;
        password = pass;
        id = null;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }



}
