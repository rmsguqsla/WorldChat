package com.inhatc.startupproject2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class UserInfoActivity extends AppCompatActivity implements View.OnClickListener{

    DatabaseReference databaseReference;
    EditText etName;
    Button btnSet;
    Spinner spinLan;
    String[] language = {"English", "한국어", "中国人", "日本語"};
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        etName = findViewById(R.id.etName);
        spinLan = findViewById(R.id.spinLan);
        btnSet = findViewById(R.id.btnSet);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, language);
        spinLan.setAdapter(adapter);

        btnSet.setOnClickListener(this);
    }
    @Override
    public void onClick(View v){
        if(v == btnSet){
            storeUploader();
        }
    }

    private void storeUploader() {
        final String name = etName.getText().toString();
        final String language = spinLan.getSelectedItem().toString();
        UserInfo userInfo;

        if (name.length() > 0) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            userInfo = new UserInfo(name, language);
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            if(user != null) {
                db.collection("users").document(user.getUid()).set(userInfo)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                databaseReference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("userName").setValue(name);
                                databaseReference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("userLanguage").setValue(language);
                                Toast.makeText(UserInfoActivity.this, "Success",Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(UserInfoActivity.this, HomeActivity.class);
                                startActivity(intent);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(UserInfoActivity.this, "Failure",Toast.LENGTH_LONG).show();
                                Log.w("TAG : ", "Error writing document", e);
                            }
                        });
            }
        } else {
            Toast.makeText(UserInfoActivity.this, "회원정보를 입력해주세요.",Toast.LENGTH_SHORT).show();
        }
    }
}