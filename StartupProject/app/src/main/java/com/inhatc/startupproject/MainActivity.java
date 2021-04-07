package com.inhatc.startupproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    protected Button btnLogin, btuSign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnLogin = (Button)findViewById(R.id.btnLogin);
        btuSign = (Button)findViewById(R.id.btnSignup);
        btnLogin.setOnClickListener(this);
        btuSign.setOnClickListener(this);
    }

    public void onClick(View v) {
        if (v == btnLogin) {
            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(loginIntent);
        }
        else if(v == btuSign) {
            Intent signIntent = new Intent(MainActivity.this, SignupActivity.class);
            startActivity(signIntent);
        }
    }
}