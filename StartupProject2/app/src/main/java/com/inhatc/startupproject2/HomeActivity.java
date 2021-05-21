package com.inhatc.startupproject2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.widget.Toast;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        if (Build.VERSION.SDK_INT >= 23) {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.INTERNET,
                            Manifest.permission.RECORD_AUDIO}, PERMISSION);
        }

        yourText = (TextView)findViewById(R.id.yourText);
        myText = (TextView)findViewById(R.id.myText);
        yourSpinner = (Spinner)findViewById(R.id.yourSpinner);
        mySpinner = (Spinner)findViewById(R.id.mySpinner);
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

        myTabHost.setCurrentTab(0);

        yourText.setOnClickListener(this);
        myText.setOnClickListener(this);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, language);

        yourSpinner.setAdapter(adapter);
        mySpinner.setAdapter(adapter);

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

    }

    private RecognitionListener yourlistener = new RecognitionListener() {
        @Override public void onReadyForSpeech(Bundle params) {
            Toast.makeText(HomeActivity.this, "음성인식을 시작합니" +
                    "다.", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(HomeActivity.this, "음성인식을 시작합니" +
                    "다.", Toast.LENGTH_SHORT).show();
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
            yourText.setText(tmp);
        }
    }
}

