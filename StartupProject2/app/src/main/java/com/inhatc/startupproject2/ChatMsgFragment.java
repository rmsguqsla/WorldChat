package com.inhatc.startupproject2;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatMsgFragment extends Fragment implements View.OnClickListener {

    // 로그용 TAG
    private final String TAG = getClass().getSimpleName();

    // 채팅을 입력할 입력창과 전송 버튼
    EditText content_et;
    ImageView send_iv;

    // 채팅 내용을 뿌려줄 RecyclerView 와 Adapter
    RecyclerView rv;
    ChatAdapter mAdapter;

    // 유저 이름
    String userName;

    // 채팅 내용을 담을 배열
    List<ChatMsgVo> msgList = new ArrayList<>();

    // FirebaseDatabase 연결용 객체들
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef;

    String chatRoomName;

    //유저 언어
    String language;

    public ChatMsgFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ChatMsgFragment newInstance() {
        ChatMsgFragment fragment = new ChatMsgFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("users").document(user.getUid());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        if (document.exists()) {
                            userName = document.getData().get("name").toString();
                            language = document.getData().get("language").toString();
                        } else {
                            Log.d("TAG : ", "No such document");
                        }
                    }
                } else {
                    Log.d("TAG : ", "get failed with ", task.getException());
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_msg, container, false);

        content_et = view.findViewById(R.id.content_et);
        send_iv = view.findViewById(R.id.send_iv);

        rv = view.findViewById(R.id.rv);
        send_iv.setOnClickListener(this);

// ChatRoomFragment 에서 받는 채팅방 이름
        mAdapter = new ChatAdapter(msgList);

        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv.setAdapter(mAdapter);

// Firebase Database 초기
        myRef = database.getReference();
// Firebase Database Listener 붙이기
        myRef.child("ChatRooms").child(chatRoomName).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
// Firebase 의 해당 DB 에 값이 추가될 경우 호출, 생성 후 최초 1번은 실행됨
                Log.d(TAG, "onChild added");
                Log.d(TAG, "onChild = "+dataSnapshot.getValue(ChatMsgVo.class).toString());

// Database 의 정보를 ChatMsgVO 객체에 담음
                ChatMsgVo chatMsgVO = dataSnapshot.getValue(ChatMsgVo.class);
                msgList.add(chatMsgVO);

// 채팅 메시지 배열에 담고 RecyclerView 다시 그리기
                mAdapter = new ChatAdapter(msgList);
                rv.setAdapter(mAdapter);
                rv.scrollToPosition(msgList.size()-1);
                Log.d(TAG, msgList.size()+"");
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) { }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) { }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) { }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(getActivity() != null && getActivity() instanceof ChatMsgActivity) {
            chatRoomName = ((ChatMsgActivity)getActivity()).getData().toString();
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.send_iv:
                if(content_et.getText().toString().trim().length() >= 1){
                    Log.d(TAG, "입력처리");

                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                    // Database 에 저장할 객체 만들기
                    ChatMsgVo msgVO = new ChatMsgVo(userName, df.format(new Date()).toString(), content_et.getText().toString().trim(), language);

// 해당 DB 에 값 저장시키기
                    myRef.child("ChatRooms").child(chatRoomName).push().setValue(msgVO);

// 입력 필드 초기화
                    content_et.setText("");
                }else
                {
                    Toast.makeText(getActivity(), "메시지를 입력하세요.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}