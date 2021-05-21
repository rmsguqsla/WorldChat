package com.inhatc.startupproject2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    FirebaseDatabase myFirebase;
    DatabaseReference myDB_Reference = null;

    EditText etId, etPw;
    Button btnLogin;

    String dbId;
    String dbPw;

    TextView aa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etId = findViewById(R.id.etId);
        etPw = findViewById(R.id.etPw);
        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);

        aa = findViewById(R.id.aa);

        myFirebase = FirebaseDatabase.getInstance();
        myDB_Reference = myFirebase.getReference();
    }

    public void onClick(View v) {
        if(v == btnLogin) {
            //final ProgressDialog mDialog = new ProgressDialog(this);
            //mDialog.setMessage("로그인중입니다...");
            //mDialog.show();
            if(etId.getText().toString().length() == 0){
                //mDialog.dismiss();
                Toast.makeText(this,"Please write your id",Toast.LENGTH_SHORT).show();
            }
            else if(etPw.getText().toString().length() == 0){
                //mDialog.dismiss();
                Toast.makeText(this,"Please write your password",Toast.LENGTH_SHORT).show();
            }
            else {
                String id = etId.getText().toString().trim();
                String pw = etPw.getText().toString().trim();
                //mGet_FirebaseDatabase();
                myDB_Reference.child("Member").child(id).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e("firebase", "Error getting data", task.getException());
                        }
                        else {
                            try {
                                String result = task.getResult().getValue().toString();
                                dbId = result.substring(18, 26);
                                dbPw = result.substring(55, 59);
                                if (id.equals(dbId) && pw.equals(dbPw)) {
                                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            } catch (Exception e) {
                                Toast.makeText(LoginActivity.this,"ID or password is wrong",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        }
    }

    /*public void mGet_FirebaseDatabase(){
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                aa.setText("Firebase Value : ");
                for(DataSnapshot postSnapshot: snapshot.getChildren()) {
                    String strKey = postSnapshot.getKey();
                    aa.append(strKey);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("TAG: ", "Failed to read value.", error.toException());
            }
        };
        //Query sortbyName = FirebaseDatabase.getInstance().getReference().child("Member").orderByChild(etId.getText().toString().trim());
        //sortbyName.addListenerForSingleValueEvent(postListener);
    }*/
}