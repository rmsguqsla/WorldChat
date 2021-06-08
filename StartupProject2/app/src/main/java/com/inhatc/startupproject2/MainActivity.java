package com.inhatc.startupproject2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnLogin, btnSign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLogin = (Button)findViewById(R.id.btnLogin);
        btnSign = (Button)findViewById(R.id.btnSignup);
        btnLogin.setOnClickListener(this);
        btnSign.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view == btnLogin) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        else if(view == btnSign) {
            Intent signIntent = new Intent(this, SignupActivity.class);
            startActivity(signIntent);
        }
    }

    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}