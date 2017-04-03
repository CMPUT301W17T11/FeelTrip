package com.example.henzoshimada.feeltrip;

import android.test.ActivityInstrumentationTestCase2;
import android.text.TextUtils;

import java.util.ArrayList;
// Using calendar instead of date
import java.util.Date;
import java.util.concurrent.ExecutionException;

/**
 * Created by HenzoShimada on 2017-03-05.
 */


public class FeelTripTests extends ActivityInstrumentationTestCase2 {

    /**
     * Instantiates a new Feel trip tests.
     */
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

    /**
     * Test get participant.
     */
    public void testGetParticipant(){
        Participant participant = new Participant("user1", "pass");
        Participant p1 = FeelTripApplication.getParticipant();
        p1.setUserName(participant.getUserName());
        Participant p2 = FeelTripApplication.getParticipant();
        assertEquals("username is: ", "user1", p2.getUserName());
    }


    /**
     * Test update queue controller.
     */
    public void testUpdateQueueController(){
        UpdateQueueController qc = FeelTripApplication.getUpdateQueueController();
        Mood mood = new Mood("user1");
        qc.addMood(mood);

        UpdateQueueController qc2 = FeelTripApplication.getUpdateQueueController();
        assertTrue("msg", qc2.getSize() == 1);

        assertTrue("MSG", qc2.popMood().getUsername().equals("user1"));
    }

    /**
     * Test add mood task.
     */
    public void testAddMoodTask(){
        ElasticSearchController.AddMoodTask addMoodTask = new ElasticSearchController.AddMoodTask();
        Mood mood = new Mood("user1");
        try {
            mood.setDescription("description1"+" -Feeling sleepy");
            mood.setPrivate();
        } catch (DescriptionTooLongException e) {
            e.printStackTrace();
        }
        addMoodTask.execute(mood);
    }

    /**
     * Test get mood task.
     */
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
        assertEquals("test getMood", "user1", moods.get(0).getUsername());
    }

    /**
     * Test edit task.
     *
     * @throws DescriptionTooLongException the description too long exception
     */
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
        mood.setDescription("test edit"+" -Feeling tested");
        mood.setEmotionalState("happy");
        mood.setMapPosition(12320.23143, 901273.000);
        mood.setPrivate();
        mood.setDate(date);
        mood.setImage("Mona Lisa");
        mood.setSocialSit("With friends");
        mood.setDel();
        editMoodTask.execute(mood);
    }

    /**
     * Test search mood task.
     */
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

    /**
     * Test delete mood task.
     */
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

    /*
    public void testAddUserTask(){
        ElasticSearchController.AddParticipantTask addParticipantTask = new ElasticSearchController.AddParticipantTask();
        User user = new User("user1","pass1");

        addParticipantTask.execute(user);
    }

    public void testGetUserTask(){
        ArrayList<User> users = new ArrayList<>();
        ElasticSearchController.GetParticipantTask get = new ElasticSearchController.GetParticipantTask("user1","pass1");
        get.execute();
        try {
            users.addAll(get.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        assertEquals("test getUsername", "user1", users.get(0).getUsername());
    }

    public void testDeleteUserTask(){
        ArrayList<User> users = new ArrayList<>();
        ElasticSearchController.DeleteParticipantTask deleteParticipantTask = new ElasticSearchController.DeleteParticipantTask();
        ElasticSearchController.GetParticipantTask getParticipantTask = new ElasticSearchController.GetParticipantTask("user1","pass1");
        getParticipantTask.execute();
        try {
            users.addAll(getParticipantTask.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        User user = users.get(0);
        deleteParticipantTask.execute(user);
    }
    */

    /**
     * Test arrayto string.
     */
    public void testArraytoString() {
        ArrayList<String> stringArrayList = new ArrayList<>();
        stringArrayList.add("Hello");
        stringArrayList.add("Darkness");
        stringArrayList.add("My");
        stringArrayList.add("Old");
        stringArrayList.add("Friend");
        String output = TextUtils.join(" ", stringArrayList);
        String output2 = "[\"" + TextUtils.join("\",\"", stringArrayList) + "\"]";
        assertEquals("Hello Darkness My Old Friend", output);
        assertEquals("[\"Hello\",\"Darkness\",\"My\",\"Old\",\"Friend\"]", output2);
    }


    /**
     * Test set mood option.
     */
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

    /**
     * Test get mood option.
     */
    public void testGetMoodOption() {
        Mood mood = new Mood();
        mood.setMoodOption("Happy");
        assertEquals(mood.getMoodOption(), "Happy");
    }

    /**
     * Test set social sit.
     */
    public void testSetSocialSit() {
        Mood mood = new Mood();
        mood.setSocialSit("Alone");
        assertNotNull(mood.getSocialSit());
    }

    /**
     * Test get social sit.
     */
    public void testGetSocialSit() {
        Mood mood = new Mood();
        mood.setSocialSit("Alone");
        assertEquals(mood.getSocialSit(), "Alone");
    }

    /**
     * Test set private.
     */
    public void testSetPrivate() {
        Mood mood = new Mood();
        assertFalse(mood.getPrivate());
        mood.setPrivate();
        assertTrue(mood.getPrivate());
    }

    /**
     * Test get private.
     */
    public void testGetPrivate() {
        Mood mood = new Mood();
        assertFalse(mood.getPrivate());
        mood.setPrivate();
        assertTrue(mood.getPrivate());
    }

    /**
     * Test sett map position.
     */
    public void testSettMapPosition() {
        Mood mood = new Mood();
        Double lat = 123213.000324;
        Double lon = 324324.0000;
        mood.setMapPosition(lat, lon);
        assertEquals(mood.getMapPosition()[0], lat);
        assertEquals(mood.getMapPosition()[1], lon);
    }

    /**
     * Test get map position.
     */
    public void testGetMapPosition() {
        Mood mood = new Mood();
        Double lat = 8324892.32948324;
        Double lon = 32904239048.3333;
        mood.setMapPosition(lat, lon);
        assertEquals(mood.getMapPosition()[0], lat);
        assertEquals(mood.getMapPosition()[1], lon);
    }

    /**
     * Test getinvalid id.
     */
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
