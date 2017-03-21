package com.example.henzoshimada.feeltrip;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class loginActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }
    public void checkUser(View v){
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
            Participant participant = FeelTripApplication.getParticipant();
            participant.setUserName(participants.get(0).getUserName());
            participant.setPassword(participants.get(0).getPassword());
            participant.addAllFollowing(participants.get(0).getFollowing());
            participant.addAllFollowRequest(participants.get(0).getFollowRequest());


            Intent intent = new Intent(this, MainScreen.class);
            startActivity(intent);
        }
        else {
            Toast.makeText(getApplicationContext(),"Invalid username or password!",Toast.LENGTH_SHORT).show();
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
}
