package com.inhatc.startupproject2;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        findViewById(R.id.btnSignup).setOnClickListener(onClickListener);
    }

    /*
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }
    */

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btnSignup:
                    signUp();
                    break;
            }
        }
    };

    private void signUp() {
        String email = ((EditText)findViewById(R.id.etEmail)).getText().toString();
        String password = ((EditText)findViewById(R.id.etPw)).getText().toString();
        String passwordCheck = ((EditText)findViewById(R.id.etCfPw)).getText().toString();

        if(email.length() > 0 && password.length() > 0 && passwordCheck.length() > 0){
            if(password.equals(passwordCheck)){
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    databaseReference.child("Users").child(mAuth.getCurrentUser().getUid()).child("email").setValue(mAuth.getCurrentUser().getEmail());
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Toast.makeText(SignupActivity.this, "Successful register.",Toast.LENGTH_SHORT).show();
                                    mAuth.signOut();
                                    myStartActivity(MainActivity.class);
                                } else {
                                    try {
                                        throw task.getException();
                                    } catch(FirebaseAuthWeakPasswordException e) {
                                        Toast.makeText(SignupActivity.this,"Please enter a password of at least 6 digits" ,Toast.LENGTH_SHORT).show();
                                    } catch(FirebaseAuthInvalidCredentialsException e) {
                                        Toast.makeText(SignupActivity.this,"Please enter in email format" ,Toast.LENGTH_SHORT).show();
                                    } catch(FirebaseAuthUserCollisionException e) {
                                        Toast.makeText(SignupActivity.this,"This email already exists" ,Toast.LENGTH_SHORT).show();
                                    } catch(Exception e) {
                                        Toast.makeText(SignupActivity.this, "Please check again" ,Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
            }else{
                Toast.makeText(SignupActivity.this, "passwords do not match.",Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(SignupActivity.this, "Please enter your email or password.",Toast.LENGTH_SHORT).show();
        }
    }

    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}