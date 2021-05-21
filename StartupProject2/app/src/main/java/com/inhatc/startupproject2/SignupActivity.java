package com.inhatc.startupproject2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener{

    FirebaseDatabase myFirebase;
    DatabaseReference myDB_Reference = null;

    HashMap<String, Object> Member = null;

    EditText etId, etPw, etCfPw, etName;
    Spinner spinLan;
    Button btnSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etId = (EditText)findViewById(R.id.etId);
        etPw = (EditText)findViewById(R.id.etPw);
        etCfPw = (EditText)findViewById(R.id.etCfPw);
        etName = (EditText)findViewById(R.id.etName);
        spinLan = (Spinner)findViewById(R.id.spinLan);
        String[] language = {"English", "한국어", "中国人", "日本語"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, language);
        spinLan.setAdapter(adapter);
        btnSignup = (Button)findViewById(R.id.btnSignup);
        btnSignup.setOnClickListener(this);

        myFirebase = FirebaseDatabase.getInstance();
        myDB_Reference = myFirebase.getReference();
        Member = new HashMap<>();
    }

    public void onClick(View v) {
        if(v == btnSignup) {
            if(!etId.getText().toString().equals("") &&
                    !etPw.getText().toString().equals("") &&
                    !etCfPw.getText().toString().equals("") &&
                    !etName.getText().toString().equals("")) {

                String id = etId.getText().toString().trim();
                String pw = etPw.getText().toString().trim();
                String cfpw = etCfPw.getText().toString().trim();
                String name = etName.getText().toString().trim();
                String language = spinLan.getSelectedItem().toString().trim();

                if(pw.equals(cfpw)) {
                    final ProgressDialog mDialog = new ProgressDialog(SignupActivity.this);
                    mDialog.setMessage("가입중입니다...");
                    mDialog.show();

                    Member.put("Id", id);
                    Member.put("Password", pw);
                    Member.put("Name", name);
                    Member.put("Language", language);
                    myDB_Reference.child("Member").child(id).setValue(Member);

                    mDialog.dismiss();

                    Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    Toast.makeText(SignupActivity.this, "회원가입에 성공하셨습니다.", Toast.LENGTH_SHORT).show();

                    //비밀번호 오류시
                }else{

                    Toast.makeText(SignupActivity.this, "비밀번호가 틀렸습니다. 다시 입력해 주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        } else {
            Toast.makeText(SignupActivity.this, "빈칸 없이 입력해주세요.", Toast.LENGTH_SHORT).show();
        }
    }
}