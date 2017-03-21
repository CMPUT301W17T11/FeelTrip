package com.example.henzoshimada.feeltrip;

import android.os.AsyncTask;
import android.test.ActivityInstrumentationTestCase2;

import java.util.ArrayList;
// Using calendar instead of date
import java.util.Date;
import java.util.concurrent.ExecutionException;

/**
 * Created by HenzoShimada on 2017-03-05.
 */


public class FeelTripTests extends ActivityInstrumentationTestCase2 {

    public FeelTripTests() {
        super(MainScreen.class);
    }

    /*
    // The following are tests for the MainActivity class
    public void testCreateMoodEvent() throws Exception {
        //TODO
    }

    public void testGetMoodEvent() {
        //TODO
    }
    */

    public void testGetParticipant(){
        Participant p1 = FeelTripApplication.getParticipant();
        p1.setUserName("user");
        Participant p2 = FeelTripApplication.getParticipant();
        assertEquals("username is: ", "user", p2.getUserName());
    }




    public void testUpdateQueueController(){
        UpdateQueueController qc = FeelTripApplication.getUpdateQueueController();
        Mood mood = new Mood("user1");
        qc.addMood(mood);

        UpdateQueueController qc2 = FeelTripApplication.getUpdateQueueController();
        assertTrue("msg", qc2.getSize() == 1);

        assertTrue("MSG", qc2.popMood().getUser().equals("user1"));
    }

    public void testAddMoodTask(){
        ElasticSearchController.AddMoodTask addMoodTask = new ElasticSearchController.AddMoodTask();
        Mood mood = new Mood("user1");
        try {
            mood.setDescription("description1", " -Feeling sleepy");
            mood.setPrivate();
        } catch (DescriptionTooLongException e) {
            e.printStackTrace();
        }
        addMoodTask.execute(mood);
    }

    public void testGetMoodTask(){
        ArrayList<Mood> moods = new ArrayList<>();
        ElasticSearchController.GetMoodTask get = new ElasticSearchController.GetMoodTask();
        get.execute();
        try {
            moods.addAll(get.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        assertEquals("test getMood", "user1", moods.get(0).getUser());
    }

    public void testEditTask() throws DescriptionTooLongException {
        ArrayList<Mood> moods = new ArrayList<Mood>();
        ElasticSearchController.GetMoodTask getMoodTask = new ElasticSearchController.GetMoodTask();
        ElasticSearchController.EditMoodTask editMoodTask = new ElasticSearchController.EditMoodTask();
        getMoodTask.execute();
        try {
            moods.addAll(getMoodTask.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        Date date = new Date();
        Mood mood = moods.get(0);
        mood.setDescription("test edit", " -Feeling tested");
        mood.setEmotionalState("happy");
        mood.setMapPosition(12320.23143, 901273.000);
        mood.setPrivate();
        mood.setDate(date);
        mood.setImage("Mona Lisa");
        mood.setSocialSit("With friends");
        mood.setDel();
        editMoodTask.execute(mood);
    }

    public void testSearchMoodTask(){
        ArrayList<Mood> moods = new ArrayList<>();
        ElasticSearchController.GetMoodTask get = new ElasticSearchController.GetMoodTask("description");
        get.execute("test edit");
        try {
            moods.addAll(get.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        assertEquals("test getMood", "test edit", moods.get(0).getDescription());
    }

    public void testDeleteMoodTask(){
        ArrayList<Mood> moods = new ArrayList<Mood>();
        ElasticSearchController.DeleteMoodTask deleteMoodTask = new ElasticSearchController.DeleteMoodTask();
        ElasticSearchController.GetMoodTask getMoodTask = new ElasticSearchController.GetMoodTask();
        getMoodTask.execute();
        try {
            moods.addAll(getMoodTask.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        Mood mood = moods.get(0);
        deleteMoodTask.execute(mood);
    }

    public void testAddUserTask(){
        ElasticSearchController.AddUserTask addUserTask = new ElasticSearchController.AddUserTask();
        User user = new User("user1","pass1");

        addUserTask.execute(user);
    }

    public void testGetUserTask(){
        ArrayList<User> users = new ArrayList<>();
        ElasticSearchController.GetUserTask get = new ElasticSearchController.GetUserTask("user1","pass1");
        get.execute();
        try {
            users.addAll(get.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        assertEquals("test getUser", "user1", users.get(0).getUsername());
    }

    public void testDeleteUserTask(){
        ArrayList<User> users = new ArrayList<>();
        ElasticSearchController.DeleteUserTask deleteUserTask = new ElasticSearchController.DeleteUserTask();
        ElasticSearchController.GetUserTask getUserTask = new ElasticSearchController.GetUserTask("user1","pass1");
        getUserTask.execute();
        try {
            users.addAll(getUserTask.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        User user = users.get(0);
        deleteUserTask.execute(user);
    }


    // The following are tests for the NewMoodEvent class
/*
    public void testSetDescription() throws DescriptionTooLongException {
        Mood mood = new Mood();
        mood.setDescription("test description");
        assertNotNull(mood.getDescription());
    }

    public void testGetDescription() throws DescriptionTooLongException {
        Mood mood = new Mood();
        mood.setDescription("test description");
        assertEquals(mood.getDescription(), "test description");
    }
*/
    public void testSetMoodOption() {
        Mood mood = new Mood();
        mood.setMoodOption("Happy");
        assertNotNull(mood.getMoodOption());
    }

    public void testGetMoodOption() {
        Mood mood = new Mood();
        mood.setMoodOption("Happy");
        assertEquals(mood.getMoodOption(), "Happy");
    }

    public void testSetSocialSit() {
        Mood mood = new Mood();
        mood.setSocialSit("Alone");
        assertNotNull(mood.getSocialSit());
    }

    public void testGetSocialSit() {
        Mood mood = new Mood();
        mood.setSocialSit("Alone");
        assertEquals(mood.getSocialSit(), "Alone");
    }

    public void testSetPrivate() {
        Mood mood = new Mood();
        assertFalse(mood.getPrivate());
        mood.setPrivate();
        assertTrue(mood.getPrivate());
    }

    public void testGetPrivate() {
        Mood mood = new Mood();
        assertFalse(mood.getPrivate());
        mood.setPrivate();
        assertTrue(mood.getPrivate());
    }

    public void testSettMapPosition() {
        Mood mood = new Mood();
        Double lat = 123213.000324;
        Double lon = 324324.0000;
        mood.setMapPosition(lat, lon);
        assertEquals(mood.getMapPosition()[0], lat);
        assertEquals(mood.getMapPosition()[1], lon);
    }

    public void testGetMapPosition() {
        Mood mood = new Mood();
        Double lat = 8324892.32948324;
        Double lon = 32904239048.3333;
        mood.setMapPosition(lat, lon);
        assertEquals(mood.getMapPosition()[0], lat);
        assertEquals(mood.getMapPosition()[1], lon);
    }

    public void testGetinvalidID() {
        Mood mood = new Mood();
        String moodID = mood.getId();
        assertEquals("test for invalid ID mood",moodID, "-1");
    }

    /*
    public void testSetDate() {
        Mood mood = new Mood();
        Date date = null;
        mood.setDate(date);
        assertNotNull(mood.getDate());
    }

    public void testGetDate() {
        Mood mood = new Mood();
        Date date = null;
        mood.setDate(date);
        assertEquals(mood.getDate(), date);
    }
    */

/*
    public void testSetImageFile() {
        //
    }

    public void testGetImageFile() {
        //
    }

    // The following are tests for the Participant class

    public void testAddFollowing() {
        Participant participant = new Participant();
        Participant follower = new Participant();
        assertNull(participant.getFollowing());
        participant.addFollowing(follower.getUsername());
        assertNotNull(participant.getFollowing());
    }

    public void testGetFollowing() {
        Participant participant = new Participant();
        Participant follower = new Participant();
        assertNull(participant.getFollowing());
        participant.addFollowing(follower.getUsername());
        assertEquals(participant.getFollowing(),follower.getUsername());
    }

    public void testAddFollowRequests() {
        Participant participant = new Participant();
        assertNull(participant.getFollowRequests());
        participant.addFollowRequests("username");
        assertNotNull(participant.getFollowRequests());
    }

    public void testGetFollowRequests() {
        Participant participant = new Participant();
        ArrayList<String> followerArray = new ArrayList<String>();
        followerArray.add("user1");
        followerArray.add("user2");
        followerArray.add("user3");
        participant.addFollowRequests("user1");
        participant.addFollowRequests("user2");
        participant.addFollowRequests("user3");
        assertEquals(participant.getFollowRequests(), followerArray);
    }

    public void testDelFollowRequests() {
        Participant participant = new Participant();
        participant.addFollowRequests("username");
        assertNotNull(participant.getFollowRequests());
        participant.delFollowRequests("username");
        assertNull(participant.getFollowRequests());
    }

    public void testSetUsername() {
        Participant participant = new Participant();
        participant.setUsername("testuser");
        assertNotNull(participant.getUsername());
    }

    public void testGetUsername() {
        Participant participant = new Participant();
        participant.setUsername("testuser");
        assertEquals(participant.getUsername(),"testuser");
    }
*/
}
