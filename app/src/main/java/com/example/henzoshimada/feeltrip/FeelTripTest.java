import android.app.Activity;
import android.location.Location;
import android.test.ActivityInstrumentationTestCase2;

import com.example.henzoshimada.feeltrip.MainScreen;

import java.util.ArrayList;
import java.util.Date;

public class FeelTripTest extends ActivityInstrumentationTestCase2 {

    public FeelTripTest() {
        super(MainScreen.class);
    }

    // The following are tests for the MainActivity class

    public void testCreateMoodEvent() throws Exception {
        //TODO
    }

    public void testGetMoodEvent() {
        //TODO
    }

    // The following are tests for the NewMoodEvent class

    public void testSetDescription() {
        Mood mood = new Mood();
        mood.setDescription("test description");
        assertNotNull(mood.getDescription());
    }

    public void testGetDescription() {
        Mood mood = new Mood();
        mood.setDescription("test description");
        assertEquals(mood.getDescription(), "test description");
    }

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

    public void testSetMapPosition() {
        Mood mood = new Mood();
        Location loc = null; //TODO: properly initialize a test loc
        mood.setMapPosition(loc);
        assertNotNull(mood.getMapPosition());
    }

    public void testGetMapPosition() {
        Mood mood = new Mood();
        Location loc = null; //TODO: properly initialize a test loc
        mood.setMapPosition(loc);
        assertEquals(mood.getMapPosition(), loc);
    }

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

    public void testSetImageFile() {
        //TODO: Figure out what data type an image file is stored in
    }

    public void testGetImageFile() {
        //TODO: Figure out what data type an image file is stored in
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

    //



}
