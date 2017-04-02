package com.example.henzoshimada.feeltrip;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.robotium.solo.Solo;

/**
 * Created by Kevin on 31-Mar-17.
 */

public class LoginActivityTest extends ActivityInstrumentationTestCase2<loginActivity> {
    private Solo solo;
    private EditText userNameView;
    private EditText passwordView;
    private View loginButtonView;
    private View registerButtonView;
    private String userName = "testcaseuser";
    private String password = "testcaseuser";
    private String invalidPassword = "sdfgh";

    public LoginActivityTest() {
        super(loginActivity.class);
    }

    //will be run before test
    public void setUp() throws Exception{
        solo = new Solo(getInstrumentation(), getActivity()); //(get connection to device, get activity)
        userNameView = (EditText) solo.getView(R.id.user_text);
        passwordView = (EditText) solo.getView(R.id.pass_text);
        loginButtonView = solo.getView(R.id.login_button);
        registerButtonView = solo.getView(R.id.reg_button);
    }

    public void testStart() throws Exception {
        Activity activity = getActivity();
    }

    public void testInputUserName() {
        String inputText = userName;
        solo.enterText(userNameView,inputText);
        assertTrue(solo.waitForText(inputText));
    }

    public void testInputPassword() {
        String inputText = password;
        solo.enterText(passwordView,inputText);
        assertTrue(solo.waitForText(inputText));
    }

    private void deleteTestParticipant(){
        ElasticSearchController.DeleteParticipantTask deleteParticipantTask = new ElasticSearchController.DeleteParticipantTask();
        Participant participant = FeelTripApplication.getParticipant();
        deleteParticipantTask.execute(participant);
    }

    public void testExistingAccount() {
        //some user that is not in ES
        testInputUserName();
        testInputPassword();

        solo.clickOnView(loginButtonView);
        solo.assertCurrentActivity("Wrong Activity", loginActivity.class);

        solo.clickOnView(registerButtonView);
        solo.clickOnView(loginButtonView);
        solo.assertCurrentActivity("Wrong Activity", MainScreen.class);

        deleteTestParticipant();

    }

    public void testNonExistingAccount() {
        //some user that is not in ES
        testInputUserName();
        testInputPassword();

        solo.clickOnView(loginButtonView);
        solo.assertCurrentActivity("Wrong Activity", loginActivity.class);

        deleteTestParticipant();
    }

    public void testCreateAccount() {
        //some user that is not in ES
        testInputUserName();
        testInputPassword();

        solo.clickOnView(registerButtonView);

        assertTrue(solo.waitForText("User Creation successful!")); //message from toast

        deleteTestParticipant();
    }

    public void testUniqueUserName() {
        //some user that is not in ES
        testInputUserName();
        testInputPassword();

        solo.clickOnView(registerButtonView);
        solo.clickOnView(registerButtonView);
        assertTrue(solo.waitForText("exists!")); //message from toast

        deleteTestParticipant();
    }

    public void testIncorrectLoginInfo() {
        //some user that is not in ES
        testInputUserName();
        testInputPassword();

        solo.clickOnView(registerButtonView);
        assertTrue(solo.waitForText("exists!")); //message from toast

        solo.enterText(passwordView, invalidPassword);
        solo.clickOnView(registerButtonView);
        assertTrue(solo.waitForText("Incorrect!")); //message from toast

        deleteTestParticipant();
    }


    public void testEditMood(){
        //login
        testInputUserName();
        testInputPassword();
        solo.clickOnView(registerButtonView);
        solo.clickOnView(loginButtonView);
        solo.assertCurrentActivity("Wrong Activity", MainScreen.class);

        solo.clickOnView(solo.getView(R.id.add_mood));

        String inputText = "Some test";
        solo.enterText((EditText) solo.getView(R.id.moodEventDescription),inputText);
        assertTrue(solo.waitForText(inputText));

        String emotion = "Angry";
        solo.clickOnScreen(71, 1624); //coordinate of angry (Phone: Pixel)
        TextView view = (TextView) solo.getView(R.id.emotionString);
        assertEquals(emotion, view.getText());

        String modePost = "Public";
        solo.clickOnView(solo.getView(R.id.private_toggle));

        solo.clickOnView(solo.getView(R.id.check_bottom_button));

        solo.clickInList(0, 0);
        solo.assertCurrentActivity("wrong activity", MainScreen.class);
        solo.clickOnView(solo.getView(R.id.navigation_profile));
        solo.clickInList(0, 0);
        solo.assertCurrentActivity("wrong activity", EditMoodActivity.class);

        solo.goBack();
        solo.waitForText(inputText);
        solo.waitForText(emotion);
        solo.waitForText(modePost);

        deleteTestParticipant();
    }

    public void testCreateMood(){
        //login
        testInputUserName();
        testInputPassword();
        solo.clickOnView(loginButtonView);
        solo.assertCurrentActivity("Wrong Activity", loginActivity.class);
        solo.clickOnView(registerButtonView);
        solo.clickOnView(loginButtonView);
        solo.assertCurrentActivity("Wrong Activity", MainScreen.class);

        solo.clickOnView(solo.getView(R.id.add_mood));

        String inputText = "Some test";
        solo.enterText((EditText) solo.getView(R.id.moodEventDescription),inputText);
        assertTrue(solo.waitForText(inputText));

        String emotion = "Angry";
        solo.clickOnScreen(71, 1624); //coordinate of angry (Phone: Pixel)
        TextView view = (TextView) solo.getView(R.id.emotionString);
        assertEquals(emotion, view.getText());

        solo.clickOnView(solo.getView(R.id.check_bottom_button));

        solo.clickOnView(solo.getView(R.id.navigation_profile));
        solo.waitForText(inputText);
        solo.waitForText(emotion);

        deleteTestParticipant();
    }
}
