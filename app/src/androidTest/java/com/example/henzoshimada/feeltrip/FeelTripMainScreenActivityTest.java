package com.example.henzoshimada.feeltrip;

import android.app.Activity;
import android.app.Fragment;
import android.support.design.widget.BottomNavigationView;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.robotium.solo.Solo;

import static java.lang.Thread.sleep;

/**
 * Created by Kevin on 31-Mar-17.
 */
public class FeelTripMainScreenActivityTest extends ActivityInstrumentationTestCase2<MainScreen> {
    private Solo solo;

    /**
     * Instantiates a new Feel trip main screen activity test.
     */
    public FeelTripMainScreenActivityTest() {
        super(com.example.henzoshimada.feeltrip.MainScreen.class);
    }

    //will be run before test
    public void setUp() throws Exception {
        solo = new Solo(getInstrumentation(), getActivity()); //(get connection to device, get activity)
    }

    /**
     * Test start.
     *
     * @throws Exception the exception
     */
    public void testStart() throws Exception {
        Activity activity = getActivity();
    }

    /**
     * Test go to edit mood.
     */
    public void testGoToEditMood() {
        solo.clickOnImageButton(0);
        solo.assertCurrentActivity("Wrong class", EditMoodActivity.class);
    }


    /**
     * Test input text.
     */
    public void testInputText() {
        String inputText = "Some test";
        solo.enterText((EditText) solo.getView(R.id.keyword),inputText);
        assertTrue(solo.waitForText(inputText));
    }


    /**
     * Test go to profile frag.
     */
    public void testGoToProfileFrag(){
        solo.clickOnView(solo.getView(R.id.navigation_profile));
        try {
            sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String frag = FeelTripApplication.getFrag();
        //Log.d("test", "frag is :"+frag);
        assertEquals("profile", frag);
    }

    /**
     * Test go to map frag.
     */
    public void testGoToMapFrag(){
        solo.clickOnView(solo.getView(R.id.navigation_map));
        try {
            sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String frag = FeelTripApplication.getFrag();
        //Log.d("test", "frag is :"+frag);
        assertEquals("map", frag);
    }

    /**
     * Test go to home frag.
     */
    public void testGoToHomeFrag(){
        solo.clickOnView(solo.getView(R.id.navigation_home));
        try {
            sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String frag = FeelTripApplication.getFrag();
        //Log.d("test", "frag is :"+frag);
        assertEquals("main", frag);
    }


}