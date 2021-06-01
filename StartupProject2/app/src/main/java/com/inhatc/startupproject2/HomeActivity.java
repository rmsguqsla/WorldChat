package com.inhatc.startupproject2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener{
    final int PERMISSION = 1;
    Intent intent;
    SpeechRecognizer mRecognizer;
    TabHost myTabHost = null;
    TabHost.TabSpec myTabSpec;
    TextView yourText;
    TextView myText;
    Spinner mySpinner;
    Spinner yourSpinner;
    String[] language = {"English", "한국어", "中国人", "日本語"};
    TextView txtName;
    Button btnUserInfo;
    Button btnLogout;
    ArrayList<String> userList, chatList;
    ListView lstFriend, lstChat;
    DatabaseReference databaseReference;
    String roomName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        userList = new ArrayList<String>();
        chatList = new ArrayList<String>();

        init();

        if (Build.VERSION.SDK_INT >= 23) {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.INTERNET,
                            Manifest.permission.RECORD_AUDIO}, PERMISSION);
        }

        txtName = findViewById(R.id.txtName);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("users").document(user.getUid());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        if (document.exists()) {
                            txtName.setText(document.getData().get("name").toString());
                        } else {
                            Log.d("TAG : ", "No such document");
                        }
                    }
                } else {
                    Log.d("TAG : ", "get failed with ", task.getException());
                }
            }
        });

        btnLogout = (Button)findViewById(R.id.btnLogout);
        btnUserInfo = findViewById(R.id.btnUserInfo);
        yourText = (TextView)findViewById(R.id.yourText);
        myText = (TextView)findViewById(R.id.myText);
        yourSpinner = (Spinner)findViewById(R.id.yourSpinner);
        mySpinner = (Spinner)findViewById(R.id.mySpinner);
        lstFriend = findViewById(R.id.lstFriend);
        lstChat = findViewById(R.id.lstChat);

        CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("users");
        collectionReference.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            userList.clear();
                            for(QueryDocumentSnapshot document : task.getResult()) {
                                if(!txtName.getText().toString().trim().equals(document.getData().get("name").toString().trim())) {
                                    userList.add(document.getData().get("name").toString());
                                }
                            }
                        } else {
                            Log.d("TAG: ", "Error getting documents: ", task.getException());
                        }
                    }
                });

        databaseReference = FirebaseDatabase.getInstance().getReference();
        ArrayAdapter Fadapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, userList);
        lstFriend.setAdapter(Fadapter);
        lstFriend.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                roomName = txtName.getText().toString() + ", " + adapterView.getItemAtPosition(i);
                Intent intent = new Intent(HomeActivity.this, ChatMsgActivity.class);
                intent.putExtra("roomName",roomName);
                startActivity(intent);
            }
        });

        databaseReference.child("ChatRooms").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()) {
                    if(task.getResult().getKey() != null) {
                        for(DataSnapshot postTask: task.getResult().getChildren()) {
                            String[] lstUserName = postTask.getKey().split(", ");
                            if (lstUserName[0].equals(txtName.getText().toString()) || lstUserName[1].equals(txtName.getText().toString())) {
                                chatList.add(postTask.getKey());
                            }
                        }
                    }
                }
            }
        });
        ArrayAdapter Cadapter = new ArrayAdapter(HomeActivity.this, android.R.layout.simple_list_item_1, chatList);
        lstChat.setAdapter(Cadapter);

        lstChat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                roomName = adapterView.getItemAtPosition(i).toString();
                Intent intent = new Intent(HomeActivity.this, ChatMsgActivity.class);
                intent.putExtra("roomName",roomName);
                startActivity(intent);
            }
        });

        myTabHost = (TabHost)findViewById(R.id.tabhost);
        myTabHost.setup();

        myTabSpec = myTabHost.newTabSpec("Friend")
                .setIndicator("Friend")
                .setContent(R.id.tab1);
        myTabHost.addTab(myTabSpec);

        myTabSpec = myTabHost.newTabSpec("Chat")
                .setIndicator("Chat")
                .setContent(R.id.tab2);
        myTabHost.addTab(myTabSpec);

        myTabSpec = myTabHost.newTabSpec("Translator")
                .setIndicator("Translator")
                .setContent(R.id.tab3);
        myTabHost.addTab(myTabSpec);

        myTabSpec = myTabHost.newTabSpec("UserInfo")
                .setIndicator("UserInfo")
                .setContent(R.id.tab4);
        myTabHost.addTab(myTabSpec);

        myTabHost.setCurrentTab(0);
        yourText.setOnClickListener(this);
        myText.setOnClickListener(this);
        btnUserInfo.setOnClickListener(this);
        btnLogout.setOnClickListener(this);

        ArrayAdapter<String> Ladapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, language);

        yourSpinner.setAdapter(Ladapter);
        mySpinner.setAdapter(Ladapter);

        yourSpinner.setSelection(0);
        mySpinner.setSelection(1);

        /*yourSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(yourSpinner.getSelectedItem().toString().equals(mySpinner.getSelectedItem().toString())) {
                    if(yourSpinner.getSelectedItem().toString().equals("日本語"))
                        mySpinner.setSelection(0);
                    else
                        mySpinner.setSelection(i+1);
                }
            }
        });
        mySpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(mySpinner.getSelectedItem().toString().equals(yourSpinner.getSelectedItem().toString())) {
                    if(mySpinner.getSelectedItem().toString().equals("日本語"))
                        yourSpinner.setSelection(0);
                    else
                        yourSpinner.setSelection(i+1);
                }
            }
        });*/
    }

    private void init() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("users").document(firebaseUser.getUid());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        if (document.exists()) {
                            Log.d("TAG : ", "DocumentSnapshot data: " + document.getData());
                        } else {
                            Log.d("TAG : ", "No such document");
                            myStartActivity(UserInfoActivity.class);
                        }
                    }
                } else {
                    Log.d("TAG : ", "get failed with ", task.getException());
                }
            }
        });
    }

    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivityForResult(intent, 1);
    }
    
    /*@Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause(){
        super.onPause();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                break;
        }
    }*/

    /*@Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if(view == mySpinner) {
            if(mySpinner.getSelectedItem().toString().equals(yourSpinner.getSelectedItem().toString())) {
                if(mySpinner.getSelectedItemPosition() == 3)
                    yourSpinner.setSelection(0);
                else
                    yourSpinner.setSelection(i+1);
            }
        }

        if(view == yourSpinner) {
            if(yourSpinner.getSelectedItem().toString().equals(mySpinner.getSelectedItem().toString())) {
                if(yourSpinner.getSelectedItemPosition() == 3)
                    mySpinner.setSelection(0);
                else
                    mySpinner.setSelection(i+1);
            }
        }
    }*/

    @Override
    public void onClick(View view) {
        if(view == yourText) {
            intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,getPackageName());
            if(yourSpinner.getSelectedItem().toString().equals("English")) {
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"en-US");
            } else if(yourSpinner.getSelectedItem().toString().equals("한국어")) {
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ko");
            } else if(yourSpinner.getSelectedItem().toString().equals("中国人")) {
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"zh-CN");
            } else if(yourSpinner.getSelectedItem().toString().equals("日本語")) {
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ja");
            }
            mRecognizer=SpeechRecognizer.createSpeechRecognizer(this);
            mRecognizer.setRecognitionListener(yourlistener);
            mRecognizer.startListening(intent);

            yourBackgroundTask task = new yourBackgroundTask();
            String tmp = yourText.getText().toString();
            task.execute(tmp);
        }
        if(view == myText) {
            intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,getPackageName());
            if(mySpinner.getSelectedItem().toString().equals("English")) {
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"en-US");
            } else if(mySpinner.getSelectedItem().toString().equals("한국어")) {
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ko");
            } else if(mySpinner.getSelectedItem().toString().equals("中国人")) {
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"zh-CN");
            } else if(mySpinner.getSelectedItem().toString().equals("日本語")) {
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ja");
            }
            mRecognizer=SpeechRecognizer.createSpeechRecognizer(this);
            mRecognizer.setRecognitionListener(mylistener);
            mRecognizer.startListening(intent);

            myBackgroundTask task = new myBackgroundTask();
            String tmp = myText.getText().toString();
            task.execute(tmp);
        }
        if(view == btnUserInfo) {
            intent = new Intent(this, UserInfoActivity.class);
            startActivity(intent);
        }
        if(view == btnLogout) {
            FirebaseAuth.getInstance().signOut();
            intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    private RecognitionListener yourlistener = new RecognitionListener() {
        @Override public void onReadyForSpeech(Bundle params) {
            Toast.makeText(HomeActivity.this, "Speak", Toast.LENGTH_SHORT).show();
        }
        @Override public void onBeginningOfSpeech() {}
        @Override public void onRmsChanged(float rmsdB) {}
        @Override public void onBufferReceived(byte[] buffer) {}
        @Override public void onEndOfSpeech() {}
        @Override public void onError(int error) {
            String message;
            switch (error) {
                case SpeechRecognizer.ERROR_AUDIO:
                    message = "오디오 에러";
                    break;
                case SpeechRecognizer.ERROR_CLIENT:
                    message = "클라이언트 에러";
                    break;
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    message = "퍼미션 없음";
                    break;
                case SpeechRecognizer.ERROR_NETWORK:
                    message = "네트워크 에러";
                    break;
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    message = "네트웍 타임아웃";
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    message = "찾을 수 없음";
                    break;
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    message = "RECOGNIZER가 바쁨";
                    break;
                case SpeechRecognizer.ERROR_SERVER:
                    message = "서버가 이상함";
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    message = "말하는 시간초과";
                    break;
                default: message = "알 수 없는 오류임";
                    break;
            }
            Toast.makeText(getApplicationContext(),
                    "에러가 발생하였습니다. : " +
                            message + error,Toast.LENGTH_SHORT).show();
        }

        @Override public void onResults(Bundle results) {
            ArrayList<String> matches =
                    results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            for(int i = 0; i < matches.size() ; i++){
                yourText.setText(matches.get(i));
            }
        }
        @Override public void onPartialResults(Bundle partialResults) {}
        @Override public void onEvent(int eventType, Bundle params) {}
    };

    private RecognitionListener mylistener = new RecognitionListener() {
        @Override public void onReadyForSpeech(Bundle params) {
            Toast.makeText(HomeActivity.this, "Speak", Toast.LENGTH_SHORT).show();
        }
        @Override public void onBeginningOfSpeech() {}
        @Override public void onRmsChanged(float rmsdB) {}
        @Override public void onBufferReceived(byte[] buffer) {}
        @Override public void onEndOfSpeech() {}
        @Override public void onError(int error) {
            String message;
            switch (error) {
                case SpeechRecognizer.ERROR_AUDIO:
                    message = "오디오 에러";
                    break;
                case SpeechRecognizer.ERROR_CLIENT:
                    message = "클라이언트 에러";
                    break;
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    message = "퍼미션 없음";
                    break;
                case SpeechRecognizer.ERROR_NETWORK:
                    message = "네트워크 에러";
                    break;
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    message = "네트웍 타임아웃";
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    message = "찾을 수 없음";
                    break;
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    message = "RECOGNIZER가 바쁨";
                    break;
                case SpeechRecognizer.ERROR_SERVER:
                    message = "서버가 이상함";
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    message = "말하는 시간초과";
                    break;
                default: message = "알 수 없는 오류임";
                    break;
            }
            Toast.makeText(getApplicationContext(),
                    "에러가 발생하였습니다. : " +
                            message + error,Toast.LENGTH_SHORT).show();
        }

        @Override public void onResults(Bundle results) {
            ArrayList<String> matches =
                    results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            for(int i = 0; i < matches.size() ; i++){
                myText.setText(matches.get(i));
            }
        }
        @Override public void onPartialResults(Bundle partialResults) {}
        @Override public void onEvent(int eventType, Bundle params) {}
    };

    class yourBackgroundTask extends AsyncTask<String,Void,String>{
        @Override
        protected String doInBackground(String... str) {
            String inputTest = str[0];
            String clientId ="tVgKm0zaEMKwd8O2dLXR";
            String clientSecret="fY4_JkE9lm";
            String result ="";
            try {
                String text = URLEncoder.encode(inputTest, "UTF-8");
                String apiURL = "https://openapi.naver.com/v1/papago/n2mt";
                URL url = new URL(apiURL);
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("X-Naver-Client-Id", clientId);
                con.setRequestProperty("X-Naver-Client-Secret", clientSecret);
                // post request
                String postParams = "";
                if(yourSpinner.getSelectedItem().toString().equals("English")) {
                    postParams += "source=en-US&";
                } else if(yourSpinner.getSelectedItem().toString().equals("한국어")) {
                    postParams += "source=ko&";
                } else if(yourSpinner.getSelectedItem().toString().equals("中国人")) {
                    postParams += "source=zh-CN&";
                } else if(yourSpinner.getSelectedItem().toString().equals("日本語")) {
                    postParams += "source=ja&";
                }
                if(mySpinner.getSelectedItem().toString().equals("English")) {
                    postParams += "target=en-US&";
                } else if(mySpinner.getSelectedItem().toString().equals("한국어")) {
                    postParams += "target=ko&";
                } else if(mySpinner.getSelectedItem().toString().equals("中国人")) {
                    postParams += "target=zh-CN&";
                } else if(mySpinner.getSelectedItem().toString().equals("日本語")) {
                    postParams += "target=ja&";
                }
                postParams += "text=" + text;

                con.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(postParams);
                wr.flush();
                wr.close();
                //번역 결과 받아온다.
                int responseCode = con.getResponseCode();
                BufferedReader br;
                if(responseCode==200) { // 정상 호출
                    br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                } else {  // 에러 발생
                    br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                }
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = br.readLine()) != null) {
                    response.append(inputLine);
                }
                br.close();
                result = response.toString();
            } catch (Exception e) {
                result="번역 실패";
                System.out.println(e);
            }
            Log.d("papago",result);
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(s);
            String tmp = element.getAsJsonObject().get("message").getAsJsonObject().get("result").getAsJsonObject().get("translatedText").getAsString();
            myText.setText(tmp);
        }
    }

    class myBackgroundTask extends AsyncTask<String,Void,String>{
        @Override
        protected String doInBackground(String... str) {
            String inputTest = str[0];
            String clientId ="tVgKm0zaEMKwd8O2dLXR";
            String clientSecret="fY4_JkE9lm";
            String result ="";
            try {
                String text = URLEncoder.encode(inputTest, "UTF-8");
                String apiURL = "https://openapi.naver.com/v1/papago/n2mt";
                URL url = new URL(apiURL);
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("X-Naver-Client-Id", clientId);
                con.setRequestProperty("X-Naver-Client-Secret", clientSecret);
                // post request
                String postParams = "";
                if(mySpinner.getSelectedItem().toString().equals("English")) {
                    postParams += "source=en-US&";
                } else if(mySpinner.getSelectedItem().toString().equals("한국어")) {
                    postParams += "source=ko&";
                } else if(mySpinner.getSelectedItem().toString().equals("中国人")) {
                    postParams += "source=zh-CN&";
                } else if(mySpinner.getSelectedItem().toString().equals("日本語")) {
                    postParams += "source=ja&";
                }
                if(yourSpinner.getSelectedItem().toString().equals("English")) {
                    postParams += "target=en-US&";
                } else if(yourSpinner.getSelectedItem().toString().equals("한국어")) {
                    postParams += "target=ko&";
                } else if(yourSpinner.getSelectedItem().toString().equals("中国人")) {
                    postParams += "target=zh-CN&";
                } else if(yourSpinner.getSelectedItem().toString().equals("日本語")) {
                    postParams += "target=ja&";
                }
                postParams += "text=" + text;

                con.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(postParams);
                wr.flush();
                wr.close();
                //번역 결과 받아온다.
                int responseCode = con.getResponseCode();
                BufferedReader br;
                if(responseCode==200) { // 정상 호출
                    br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                } else {  // 에러 발생
                    br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                }
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = br.readLine()) != null) {
                    response.append(inputLine);
                }
                br.close();
                result = response.toString();
            } catch (Exception e) {
                result="Translation Failed";
                System.out.println(e);
            }
            Log.d("papago",result);
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(s);
            String tmp = element.getAsJsonObject().get("message").getAsJsonObject().get("result").getAsJsonObject().get("translatedText").getAsString();
            yourText.setText(tmp);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause(){
        super.onPause();
    }
}

