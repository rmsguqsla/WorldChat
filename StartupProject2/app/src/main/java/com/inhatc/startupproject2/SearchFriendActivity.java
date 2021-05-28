package com.inhatc.startupproject2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SearchFriendActivity extends AppCompatActivity implements View.OnClickListener{

    EditText etSearch;
    Button btnSearch;
    ListView lstSearch;
    static final String[] LIST_MENU = {"aa", "LIST2", "LIST3"} ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friend);

        etSearch = findViewById(R.id.etSearch);
        btnSearch = findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(this);
        lstSearch = findViewById(R.id.lstSearch);

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, LIST_MENU) ;
        lstSearch.setAdapter(adapter);
        lstSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(SearchFriendActivity.this,"OK",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v){
        if(v == btnSearch) {
            String Search = etSearch.getText().toString();
        }
    }
}