package com.inhatc.startupproject2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.inhatc.startupproject2.dummy.DummyContent.DummyItem;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    String myname;
    String mylanguage;
    String yourlanguage;
    private final List<ChatMsgVo> mValues;

    public ChatAdapter(List<ChatMsgVo> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_chat_msg, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("users").document(user.getUid());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        if (document.exists()) {
                            myname = document.getData().get("name").toString();
                            mylanguage = document.getData().get("language").toString();
                            ChatMsgVo vo = mValues.get(position);
                            if (mValues.get(position).getUserName().equals(myname)) {
                                holder.other_cl.setVisibility(View.GONE);
                                holder.my_cl.setVisibility(View.VISIBLE);
                                holder.date_tv2.setText(vo.getCrt_dt());
                                holder.content_tv2.setText(vo.getContent());
                            } else {
                                holder.other_cl.setVisibility(View.VISIBLE);
                                holder.my_cl.setVisibility(View.GONE);
                                holder.translate_tv.setVisibility(View.GONE);
                                holder.userName_tv.setText(vo.getUserName());
                                holder.date_tv.setText(vo.getCrt_dt());
                                holder.content_tv.setText(vo.getContent());
                                yourlanguage = vo.getLanguage();
                                if(!mylanguage.equals(yourlanguage)) {
                                    BackgroundTask backgroundTask = new BackgroundTask();
                                    backgroundTask.execute(vo.getContent());
                                    holder.translate_tv.setVisibility(View.VISIBLE);
                                }
                            }
                        } else {
                            Log.d("TAG : ", "No such document");
                        }
                    }
                } else {
                    Log.d("TAG : ", "get failed with ", task.getException());
                }
            }

            class BackgroundTask extends AsyncTask<String,Void,String> {
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

                        if(yourlanguage.equals("English")) {
                            postParams += "source=en&";
                        } else if(yourlanguage.equals("한국어")) {
                            postParams += "source=ko&";
                        } else if(yourlanguage.equals("中国人")) {
                            postParams += "source=zh-cn&";
                        } else if(yourlanguage.equals("日本語")) {
                            postParams += "source=ja&";
                        }
                        if(mylanguage.equals("English")) {
                            postParams += "target=en&";
                        } else if(mylanguage.equals("한국어")) {
                            postParams += "target=ko&";
                        } else if(mylanguage.equals("中国人")) {
                            postParams += "target=zh-cn&";
                        } else if(mylanguage.equals("日本語")) {
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
                    holder.translate_tv.setText(tmp);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ConstraintLayout my_cl, other_cl;
        public TextView userName_tv, date_tv, content_tv, date_tv2, content_tv2;
        public TextView translate_tv;

        public ViewHolder(View view) {
            super(view);
            my_cl = view.findViewById(R.id.my_cl);
            other_cl = view.findViewById(R.id.other_cl);
            userName_tv = view.findViewById(R.id.userName_tv);
            date_tv = view.findViewById(R.id.date_tv);
            content_tv = view.findViewById(R.id.content_tv);
            translate_tv = view.findViewById(R.id.translate_tv);
            date_tv2 = view.findViewById(R.id.date_tv2);
            content_tv2 = view.findViewById(R.id.content_tv2);
        }
    }
}