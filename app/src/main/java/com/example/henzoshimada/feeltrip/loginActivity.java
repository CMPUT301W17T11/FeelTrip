package com.example.henzoshimada.feeltrip;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class loginActivity extends AppCompatActivity {

    EditText userField = (EditText) findViewById(R.id.user_text);
    EditText passField = (EditText) findViewById(R.id.pass_text);
    Button loginButton = (Button) findViewById(R.id.login_button);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }
    public void checkuser(){
        userField.getText();
        passField.getText();

    }
}
