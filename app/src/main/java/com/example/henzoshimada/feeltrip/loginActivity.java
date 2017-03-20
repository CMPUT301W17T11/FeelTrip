package com.example.henzoshimada.feeltrip;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
        ArrayList<User> users = new ArrayList<>();
        ElasticSearchController.GetUserTask get = new ElasticSearchController.GetUserTask(userField.getText().toString(),
        passField.getText().toString());
        get.execute();
        try {
            users.addAll(get.get());
        }
        catch (InterruptedException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();
        }
        catch (ExecutionException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();
        }
        try{
            if(users.get(0)!=null) {
                Intent intent = new Intent(this, MainScreen.class);
                startActivity(intent);
            }
        }
        catch (Exception e){
            Toast.makeText(getApplicationContext(),"Error username/pass does not exist.",Toast.LENGTH_SHORT).show();
        }
    }
    public void regUser(View v){
        EditText userField = (EditText) this.findViewById(R.id.user_text);
        EditText passField = (EditText) this.findViewById(R.id.pass_text);
        ElasticSearchController.AddUserTask addUserTask = new ElasticSearchController.AddUserTask();
        User user = new User(userField.getText().toString(), passField.getText().toString());
        addUserTask.execute(user);
        Toast.makeText(getApplicationContext(),"User Creation sucessful!",Toast.LENGTH_SHORT).show();
    }
}
