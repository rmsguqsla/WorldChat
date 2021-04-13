package com.inhatc.startupproject;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth firebaseAuth;
    EditText etEmail, etPw, etCfPw, etFname, etLname;
    Spinner spLanguage;
    Button btnSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        etEmail = (EditText)findViewById(R.id.etEmail);
        etPw = (EditText)findViewById(R.id.etPw);
        etCfPw = (EditText)findViewById(R.id.etCfPw);
        etFname = (EditText)findViewById(R.id.etFname);
        etLname = (EditText)findViewById(R.id.etLname);
        spLanguage = (Spinner)findViewById(R.id.spLanguage);
        
        btnSignup = (Button)findViewById(R.id.btnSignup);
        btnSignup.setOnClickListener(this);

        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void onClick(View v) {
        if(v == btnSignup) {
            if(!etEmail.getText().toString().equals("") &&
            !etPw.getText().toString().equals("") &&
            !etCfPw.getText().toString().equals("") &&
            !etFname.getText().toString().equals("") &&
            !etLname.getText().toString().equals("")) {

                String email = etEmail.getText().toString().trim();
                String pw = etPw.getText().toString().trim();
                String cfpw = etCfPw.getText().toString().trim();

                if(pw.equals(cfpw)) {
                    final ProgressDialog mDialog = new ProgressDialog(SignupActivity.this);
                    mDialog.setMessage("가입중입니다...");
                    mDialog.show();

                    firebaseAuth.createUserWithEmailAndPassword(email, pw).addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            //가입 성공시
                            if (task.isSuccessful()) {
                                mDialog.dismiss();

                                //가입이 이루어져을시 가입 화면을 빠져나감.
                                Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                                Toast.makeText(SignupActivity.this, "회원가입에 성공하셨습니다.", Toast.LENGTH_SHORT).show();
                            } else {
                                mDialog.dismiss();
                                Toast.makeText(SignupActivity.this, "이미 존재하는 아이디 입니다."+task.getException().toString(), Toast.LENGTH_LONG).show();
                                return;  //해당 메소드 진행을 멈추고 빠져나감.
                            }
                        }
                    });
                //비밀번호 오류시
                }else{

                    Toast.makeText(SignupActivity.this, "비밀번호가 틀렸습니다. 다시 입력해 주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }
    }
}