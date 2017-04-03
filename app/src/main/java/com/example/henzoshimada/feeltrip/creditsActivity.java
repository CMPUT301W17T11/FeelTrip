package com.example.henzoshimada.feeltrip;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import org.w3c.dom.Text;

public class creditsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);
        LinearLayout creditLayout = (LinearLayout)findViewById(R.id.loginActivity);
        TextView thanksText = (TextView)findViewById(R.id.thanksText);
        ImageView gabbyCard = (ImageView)findViewById(R.id.gabbyCard);
        TextView gabbyDesc = (TextView)findViewById(R.id.gabbyText);
        YoYo.with(Techniques.Landing).duration(1000).delay(0).playOn(creditLayout);
        YoYo.with(Techniques.Tada).duration(1000).delay(500).playOn(gabbyCard);
    }
}