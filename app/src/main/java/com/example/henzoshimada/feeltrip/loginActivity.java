package com.example.henzoshimada.feeltrip;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.SVBar;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class loginActivity extends AppCompatActivity implements ColorPicker.OnColorChangedListener, SeekBar.OnSeekBarChangeListener {

    private ColorPicker picker;
    private SVBar svBar;
    private Button button;
    private TextView text;
    android.support.v4.widget.NestedScrollView bottomSheet;
    private BottomSheetBehavior mBottomSheetBehavior;
    private SeekBar themeSeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        setTheme(R.style.NaughtyPenguins); //TODO - theme
//        setTheme(R.style.DefaultTheme);
        setTheme(FeelTripApplication.getThemeID());

        setContentView(R.layout.activity_login);

        bottomSheet = (android.support.v4.widget.NestedScrollView) findViewById( R.id.bottom_sheet );

        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.setPeekHeight(100);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        String custom = null;
        ColorPicker colorpicker = (ColorPicker) findViewById(R.id.picker);
        SVBar sv = (SVBar) findViewById(R.id.svbar);
        TableLayout themeSeekbarTable = (TableLayout) findViewById(R.id.theme_seekBar_table);
        SeekBar themeSeekbar = (SeekBar) findViewById(R.id.theme_seekBar);
        try {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                custom = extras.getString("custom");
                switch (custom) {
                    case "LIGHT":
                        colorpicker.setVisibility(View.VISIBLE);
                        sv.setVisibility(View.VISIBLE);
                        themeSeekbarTable.setVisibility(View.VISIBLE);
                        themeSeekbar.setProgress(0);
                        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        break;
                    case "DARK":
                        colorpicker.setVisibility(View.VISIBLE);
                        sv.setVisibility(View.VISIBLE);
                        themeSeekbarTable.setVisibility(View.VISIBLE);
                        themeSeekbar.setProgress(100);
                        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        break;
                    case "NONE":
                        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        break;
                    default:
                        break;
                }
                EditText userField = (EditText) this.findViewById(R.id.user_text);
                EditText passField = (EditText) this.findViewById(R.id.pass_text);
                userField.setText(extras.getString("user"));
                passField.setText(extras.getString("pass"));
            }
        } catch(Exception e) {
            Log.d("myTag", "Loading new intent, no data found");
        }

        if(FeelTripApplication.getThemeID() == R.style.CustomTheme_Light || FeelTripApplication.getThemeID() == R.style.CustomTheme_Dark) {
            EditText editUser = (EditText) findViewById(R.id.user_text);
            editUser.getBackground().setColorFilter(FeelTripApplication.getTEXTCOLORSECONDARY(), PorterDuff.Mode.SRC_IN);
            EditText editPass = (EditText) findViewById(R.id.pass_text);
            editPass.getBackground().setColorFilter(FeelTripApplication.getTEXTCOLORSECONDARY(), PorterDuff.Mode.SRC_IN);
            Button loginButton = (Button) findViewById(R.id.login_button);
            loginButton.setTextColor(FeelTripApplication.getTEXTCOLORPRIMARY());
            Button regButton = (Button) findViewById(R.id.reg_button);
            regButton.setTextColor(FeelTripApplication.getTEXTCOLORPRIMARY());
        }

        // set up a NetworkStateListener
        // This listener remains valid during the lifetime of this activity
        NetworkStateListener networkStateListener = new NetworkStateListener();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        intentFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");

        registerReceiver(networkStateListener, intentFilter);

        picker = (ColorPicker) findViewById(R.id.picker);
        picker.setShowOldCenterColor(false);
        svBar = (SVBar) findViewById(R.id.svbar);

        picker.addSVBar(svBar);
        picker.setOnColorChangedListener(this);

        themeSeekBar = (SeekBar)findViewById(R.id.theme_seekBar);
        themeSeekBar.setOnSeekBarChangeListener(this);

    } //end of onCreate

    @Override
    public void onColorChanged(int color) {
        int colorprimary;
        int textcolorprimary;
        int textcolorsecondary;
        int textcolortertiary;
        int backgroundcolor;

        textcolorprimary = color;
        textcolorsecondary = lighter(textcolorprimary, 0.6f);
        textcolortertiary = lighter(textcolorprimary, 0.4f);
        colorprimary = lighter(textcolorprimary, 0.2f);

        FeelTripApplication.setCOLORPRIMARY(colorprimary);
        FeelTripApplication.setTEXTCOLORPRIMARY(textcolorprimary);
        FeelTripApplication.setTEXTCOLORSECONDARY(textcolorsecondary);
        FeelTripApplication.setTEXTCOLORTERTIARY(textcolortertiary);


        RelativeLayout loginBackground = (RelativeLayout) findViewById(R.id.login_background);
        int alpha = 169;
        backgroundcolor = Color.argb(alpha, Color.red(FeelTripApplication.getTEXTCOLORPRIMARY()), Color.green(FeelTripApplication.getTEXTCOLORPRIMARY()), Color.blue(FeelTripApplication.getTEXTCOLORPRIMARY()));
        FeelTripApplication.setBACKGROUNDCOLOR(backgroundcolor);
        loginBackground.setBackgroundColor(FeelTripApplication.getBACKGROUNDCOLOR());
        EditText editUser = (EditText) findViewById(R.id.user_text);
        editUser.getBackground().setColorFilter(FeelTripApplication.getTEXTCOLORSECONDARY(), PorterDuff.Mode.SRC_IN);
        EditText editPass = (EditText) findViewById(R.id.pass_text);
        editPass.getBackground().setColorFilter(FeelTripApplication.getTEXTCOLORSECONDARY(), PorterDuff.Mode.SRC_IN);
        Button loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setTextColor(FeelTripApplication.getTEXTCOLORPRIMARY());
        Button regButton = (Button) findViewById(R.id.reg_button);
        regButton.setTextColor(FeelTripApplication.getTEXTCOLORPRIMARY());

    }

    /**
     * Lightens a color by a given factor.
     *
     * @param color
     *            The color to lighten
     * @param factor
     *            The factor to lighten the color. 0 will make the color unchanged. 1 will make the
     *            color white.
     * @return lighter version of the specified color.
     */
    public static int lighter(int color, float factor) {
        int red = (int) ((Color.red(color) * (1 - factor) / 255 + factor) * 255);
        int green = (int) ((Color.green(color) * (1 - factor) / 255 + factor) * 255);
        int blue = (int) ((Color.blue(color) * (1 - factor) / 255 + factor) * 255);
        return Color.argb(Color.alpha(color), red, green, blue);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress,
                                  boolean fromUser) {
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        int mProgress = seekBar.getProgress();
        if(mProgress < 50) {
            seekBar.setProgress(0);
            swapToCustomThemeLight();
        } else {
            seekBar.setProgress(100);
            swapToCustomThemeDark();
        }
    }


    @Override
    public void onBackPressed() {
        if (mBottomSheetBehavior.getState()==BottomSheetBehavior.STATE_EXPANDED) {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event){
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (mBottomSheetBehavior.getState()==BottomSheetBehavior.STATE_EXPANDED) {

                Rect outRect = new Rect();
                bottomSheet.getGlobalVisibleRect(outRect);

                if(!outRect.contains((int)event.getRawX(), (int)event.getRawY()))
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        }

        return super.dispatchTouchEvent(event);
    }

    public void checkUser(View v){ // TODO: Check and fix cases where pass or username contains special chars.
        EditText userField = (EditText) this.findViewById(R.id.user_text);
        EditText passField = (EditText) this.findViewById(R.id.pass_text);
        ArrayList<Participant> participants = new ArrayList<>();
        ElasticSearchController.GetParticipantTask get = new ElasticSearchController.GetParticipantTask(userField.getText().toString(),
        passField.getText().toString());
        get.execute();
        try {
            participants.addAll(get.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if(!participants.isEmpty()) {
            getUserInfo(participants);

            Intent intent = new Intent(this, MainScreen.class);
            startActivity(intent);
        }
        else {
            Toast.makeText(getApplicationContext(),"Incorrect username or password!",Toast.LENGTH_SHORT).show();
        }
        return;
    }
    public void regUser(View v){
        EditText userField = (EditText) this.findViewById(R.id.user_text);
        EditText passField = (EditText) this.findViewById(R.id.pass_text);
        ArrayList<Participant> participants = new ArrayList<>();
        ElasticSearchController.GetUsernameTask get = new ElasticSearchController.GetUsernameTask(userField.getText().toString());
        get.execute();
        try {
            participants.addAll(get.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if(participants.isEmpty()) {
            String userString = userField.getText().toString();
            String passString = passField.getText().toString();
            if(!passString.isEmpty() && !userString.isEmpty()) {
                ElasticSearchController.AddParticipantTask addParticipantTask = new ElasticSearchController.AddParticipantTask();
                Participant participant = new Participant(userString, passString);
                addParticipantTask.execute(participant);
                Toast.makeText(getApplicationContext(),"User Creation sucessful!",Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getApplicationContext(),"Both username and password must be filled!",Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(getApplicationContext(),"This user already exists!",Toast.LENGTH_SHORT).show();
        }
        return;
    }

    public void getUserInfo(ArrayList<Participant> participants){
        ArrayList<FollowRequest> followRequests = new ArrayList<>();
        ArrayList<FollowRequest> acceptedRequests = new ArrayList<>();

        // get all requests this participant receives
        ElasticSearchController.GetRequestTask getRequestTask = new ElasticSearchController.GetRequestTask(false);
        getRequestTask.execute(participants.get(0).getUserName());

        // get all accepted requests this participant sends
        ElasticSearchController.GetRequestTask getAcceptedRequest = new ElasticSearchController.GetRequestTask(true);
        getAcceptedRequest.execute(participants.get(0).getUserName());

        try {
            followRequests.addAll(getRequestTask.get());
            acceptedRequests.addAll(getAcceptedRequest.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        Participant participant = FeelTripApplication.getParticipant();
        participant.setUserName(participants.get(0).getUserName());
        participant.setPassword(participants.get(0).getPassword());
        participant.setId(participants.get(0).getId());
        participant.addAllFollowing(participants.get(0).getFollowing());
        participant.addAllFollowRequest(followRequests);

        // extra work need to be done when other user accepted participant's request
        ElasticSearchController.DeleteRequestTask deleteRequestTask = new ElasticSearchController.DeleteRequestTask();
        for (FollowRequest request : acceptedRequests){
            participant.addFollowing(request.getReceiver());
            deleteRequestTask.execute(request);
        }
    }

    public void swapToCustomTheme(View v) {
        ColorPicker colorpicker = (ColorPicker) findViewById(R.id.picker);
        SVBar sv = (SVBar) findViewById(R.id.svbar);
        TableLayout themeSeekbarTable = (TableLayout) findViewById(R.id.theme_seekBar_table);
        colorpicker.setVisibility(View.INVISIBLE);
        sv.setVisibility(View.INVISIBLE);
        themeSeekbarTable.setVisibility(View.VISIBLE);
    }

    public void swapToCustomThemeLight() {
        setTheme(R.style.CustomTheme_Light);
        FeelTripApplication.setThemeID(R.style.CustomTheme_Light);
        Intent intent = new Intent(this, loginActivity.class);
        Bundle bundle = new Bundle();
        EditText userField = (EditText) this.findViewById(R.id.user_text);
        EditText passField = (EditText) this.findViewById(R.id.pass_text);
        bundle.putString("user",userField.getText().toString());
        bundle.putString("pass",passField.getText().toString());
        bundle.putString("custom","LIGHT");
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    public void swapToCustomThemeDark() {
        setTheme(R.style.CustomTheme_Dark);
        FeelTripApplication.setThemeID(R.style.CustomTheme_Dark);
        Intent intent = new Intent(this, loginActivity.class);
        Bundle bundle = new Bundle();
        EditText userField = (EditText) this.findViewById(R.id.user_text);
        EditText passField = (EditText) this.findViewById(R.id.pass_text);
        bundle.putString("user",userField.getText().toString());
        bundle.putString("pass",passField.getText().toString());
        bundle.putString("custom","DARK");
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    public void swapToNaughtyPenguinsTheme(View v) {
        setTheme(R.style.NaughtyPenguins);
        FeelTripApplication.setThemeID(R.style.NaughtyPenguins);
        Intent intent = new Intent(this, loginActivity.class);
        Bundle bundle = new Bundle();
        EditText userField = (EditText) this.findViewById(R.id.user_text);
        EditText passField = (EditText) this.findViewById(R.id.pass_text);
        bundle.putString("user",userField.getText().toString());
        bundle.putString("pass",passField.getText().toString());
        bundle.putString("custom","NONE");
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    public void swapToGalaxyTheme(View v) {
        setTheme(R.style.DefaultTheme);
        FeelTripApplication.setThemeID(R.style.DefaultTheme);
        Intent intent = new Intent(this, loginActivity.class);
        Bundle bundle = new Bundle();
        EditText userField = (EditText) this.findViewById(R.id.user_text);
        EditText passField = (EditText) this.findViewById(R.id.pass_text);
        bundle.putString("user",userField.getText().toString());
        bundle.putString("pass",passField.getText().toString());
        bundle.putString("custom","NONE");
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    public void swapToOverwatchTheme(View v) {
        setTheme(R.style.DefaultTheme);
        FeelTripApplication.setThemeID(R.style.DefaultTheme);
        Intent intent = new Intent(this, loginActivity.class);
        Bundle bundle = new Bundle();
        EditText userField = (EditText) this.findViewById(R.id.user_text);
        EditText passField = (EditText) this.findViewById(R.id.pass_text);
        bundle.putString("user",userField.getText().toString());
        bundle.putString("pass",passField.getText().toString());
        bundle.putString("custom","NONE");
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }
}
