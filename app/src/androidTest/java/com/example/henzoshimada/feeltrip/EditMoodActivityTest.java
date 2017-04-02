package com.example.henzoshimada.feeltrip;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.robotium.solo.Solo;

import java.util.Arrays;
import java.util.List;

import static java.lang.Thread.sleep;

/**
 * Created by Kevin on 31-Mar-17.
 */
public class EditMoodActivityTest extends ActivityInstrumentationTestCase2<EditMoodActivity> {
    private Solo solo;

    /**
     * Instantiates a new Edit mood activity test.
     */
    public EditMoodActivityTest() {
        super(com.example.henzoshimada.feeltrip.EditMoodActivity.class);
    }

    //will be run before test
    public void setUp() throws Exception{
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
     * Test input description.
     */
    public void testInputDescription() {
        String inputText = "Some test";
        solo.enterText((EditText) solo.getView(R.id.moodEventDescription),inputText);
        assertTrue(solo.waitForText(inputText));
    }

    /**
     * Test select social sit.
     */
    public void testSelectSocialSit() {
        int i = 0;
        solo.pressSpinnerItem(0, i);
        boolean spinnerTextSelected = solo.isSpinnerTextSelected(0, "Alone");
        assertTrue(spinnerTextSelected);
    }

    /**
     * Test edit location.
     */
    public void testEditLocation() {
        solo.clickOnMenuItem("GPS Toggle");
        solo.assertCurrentActivity("Wrong Activity", MapsActivity.class);
    }

    /**
     * Test private toggle.
     */
    public void testPrivateToggle(){
        solo.clickOnView(solo.getView(R.id.private_toggle));
        TextView view = (TextView) solo.getView(R.id.modePost);
        assertEquals("Public", view.getText());
        solo.clickOnView(solo.getView(R.id.private_toggle));
        view = (TextView) solo.getView(R.id.modePost);
        assertEquals("Private", view.getText());
    }

    /**
     * Test choose emoji.
     */
    public void testChooseEmoji(){
        solo.pressSoftKeyboardDoneButton();
        solo.clickOnScreen(71, 1488); //coordinate of angry (Phone: Pixel)
        TextView view = (TextView) solo.getView(R.id.emotionString);
        assertEquals("Angry", view.getText());
    }

    /**
     * Test description too long.
     */
    public void testDescriptionTooLong(){
        testChooseEmoji();
        solo.enterText((EditText) solo.getView(R.id.moodEventDescription),"123456789012345678901");

        solo.clickOnView(solo.getView(R.id.check_bottom_button));
        solo.waitForText("too long.");

        solo.enterText((EditText) solo.getView(R.id.moodEventDescription),"1234 67 89 01");

        solo.clickOnView(solo.getView(R.id.check_bottom_button));
        solo.waitForText("too long.");
    }

}
