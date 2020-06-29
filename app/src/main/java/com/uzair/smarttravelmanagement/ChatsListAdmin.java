package com.uzair.smarttravelmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uzair.smarttravelmanagement.Adapters.ChatsListAdminAdapter;
import com.uzair.smarttravelmanagement.Models.Chat;
import com.uzair.smarttravelmanagement.Models.User;
import com.uzair.smarttravelmanagement.Notifications.Token;

import java.util.ArrayList;

public class ChatsListAdmin extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private RecyclerView chatsListRecyclerView;
    private ArrayList<User> mUser;
    private ArrayList<String> userIds;
    private ChatsListAdminAdapter chatsListAdminAdapter;

    private DatabaseReference reference;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        setContentView(R.layout.activity_chats_list_admin);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Chats History");

        init();
    }

    private boolean isSame = false;

    private void init() {

        chatsListRecyclerView = findViewById(R.id.chats_list_admin_recycler_view);
        chatsListRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatsListAdmin.this);
        chatsListRecyclerView.setLayoutManager(linearLayoutManager);
        userIds = new ArrayList<>();
        mUser = new ArrayList<>();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference();

        reference.child("Chats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userIds.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Chat chat = dataSnapshot1.getValue(Chat.class);
                    if (!chat.getSender().equals(firebaseUser.getUid())) {
                        if (userIds.size() == 0) {
                            userIds.add(chat.getSender());
                        } else {
                            for (String user : userIds) {
                                if (chat.getSender().equals(user)) {
                                    isSame = true;
                                }
                            }
                            if (!isSame) {
                                userIds.add(chat.getSender());
                            } else {
                                isSame = false;
                            }
                        }
                    }

                }

                getAvailableUsers();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getAvailableUsers() {
        if (userIds.size() > 0) {

            reference.child("users").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mUser.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user1 = snapshot.getValue(User.class);
                        for (String user : userIds) {
                            if (user.equals(snapshot.getKey())) {
                                user1.setUserId(snapshot.getKey());
                                mUser.add(user1);
                            }
                        }
                    }
                    chatsListAdminAdapter = new ChatsListAdminAdapter(ChatsListAdmin.this, mUser);
                    chatsListRecyclerView.setAdapter(chatsListAdminAdapter);
                    chatsListAdminAdapter.notifyDataSetChanged();
                    progressDialog.hide();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

//            updateToken(FirebaseInstanceId.getInstance().getToken());

        }
    }

    private void updateToken(String token){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(firebaseUser.getUid()).setValue(token1);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
