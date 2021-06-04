package com.inhatc.startupproject2;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class Papago {
    public String getTranslation(String word, String source, String target) {

        String clientId ="tVgKm0zaEMKwd8O2dLXR";
        String clientSecret="fY4_JkE9lm";
        String realsource = "";
        String realtarget = "";

        if(source.equals("한국어")){
            realsource = "ko";
        }else if(source.equals("English")){
            realsource = "en";
        }else if(source.equals("中国人")){
            realsource = "zh-cn";
        }else if(source.equals("日本語")){
            realsource = "ja";
        }
        if(target.equals("한국어")){
            realtarget = "ko";
        }else if(target.equals("English")){
            realtarget = "en";
        }else if(target.equals("中国人")){
            realtarget = "zh-cn";
        }else if(target.equals("日本語")){
            realtarget = "ja";
        }

        Log.d("tag : ", word + " " +realsource + " " + realtarget);

        try {
            String wordSource, wordTarget;
            String text = URLEncoder.encode(word, "UTF-8");
            wordSource = URLEncoder.encode(realsource, "UTF-8");
            wordTarget = URLEncoder.encode(realtarget, "UTF-8");

            String apiURL = "https://openapi.naver.com/v1/papago/n2mt";
            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("X-Naver-Client-Id", clientId);
            con.setRequestProperty("X-Naver-Client-Secret", clientSecret);
            // post request
            String postParams = "source="+wordSource+"&target="+wordTarget+"&text=" + text;
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(postParams);
            wr.flush();
            wr.close();
            int responseCode = con.getResponseCode();
            BufferedReader br;
            if (responseCode == 200) { // 정상 호출
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
            String s = response.toString();
            s = s.split("\"")[27];
            return s;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "번역 실패";
    }
}