package com.inhatc.startupproject;


import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;


public class SignupActivity extends AppCompatActivity implements View.OnClickListener{

    FirebaseDatabase firebase;
    DatabaseReference db_reference = null;
    EditText etId, etPw, etCfPw, etFname, etLname;
    Spinner spLanguage;
    Button btnSignup;
    HashMap<String, Object> user_value = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        etId = (EditText)findViewById(R.id.etId);
        etPw = (EditText)findViewById(R.id.etPw);
        etCfPw = (EditText)findViewById(R.id.etCfPw);
        etFname = (EditText)findViewById(R.id.etFname);
        etLname = (EditText)findViewById(R.id.etLname);
        spLanguage = (Spinner)findViewById(R.id.spLanguage);
        
        btnSignup = (Button)findViewById(R.id.btnSignup);
        btnSignup.setOnClickListener(this);

        firebase = FirebaseDatabase.getInstance();
        db_reference = firebase.getReference();
        user_value = new HashMap<>();
    }

    public void onClick(View v) {
        if(v == btnSignup) {
            if(!etId.getText().toString().equals("") &&
            !etPw.getText().toString().equals("") &&
            !etCfPw.getText().toString().equals("") &&
            !etFname.getText().toString().equals("") &&
            !etLname.getText().toString().equals("")) {
                user_value.put("id", etId.getText().toString());
                user_value.put("pw", etPw.getText().toString());
                user_value.put("name", etFname.getText().toString()
                + " " + etLname.getText().toString());
                user_value.put("language", spLanguage.getSelectedItem().toString());
                user_value.put("image", null);
                setFirebase(true);
            }

        }
    }

    public void setFirebase(boolean flag) {
        if(flag){
            db_reference.child("User").child(etId.getText().toString()).setValue(user_value);
        }
    }
}