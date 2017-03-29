package com.example.henzoshimada.feeltrip;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class loginActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.NaughtyPenguins);
        setContentView(R.layout.activity_login);

        // set up a NetworkStateListener
        // This listener remains valid during the lifetime of this activity
        NetworkStateListener networkStateListener = new NetworkStateListener();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        intentFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");

        registerReceiver(networkStateListener, intentFilter);
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
}
