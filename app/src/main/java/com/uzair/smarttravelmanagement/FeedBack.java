package com.uzair.smarttravelmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uzair.smarttravelmanagement.Adapters.FeedbackMessageAdapter;
import com.uzair.smarttravelmanagement.Models.FeedbackMessage;
import com.uzair.smarttravelmanagement.Models.HotelBookingModel;

import java.security.Timestamp;
import java.util.ArrayList;
import java.util.Date;

public class FeedBack extends AppCompatActivity {

    private String userId, userType;
    private RelativeLayout adminRelativeLayout;
    private RecyclerView adminFeedbackRecyclerView;
    private ArrayList<FeedbackMessage> feedbackMessagesList;
    private FeedbackMessageAdapter feedbackMessageAdapter;
    private TextView adminNoFeedbackText;
    private LinearLayout userLinearLayout;
    private EditText userFullname, userMobileNumber, userMessage;
    private String mFullname, mMobile, mMessage;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("FeedBack");

        Intent i = getIntent();
        if (i != null) {
            userId = i.getStringExtra("user_id");
            userType = i.getStringExtra("user_type");
        } else {
            finish();
        }

        init();
    }

    private void init() {
        reference = FirebaseDatabase.getInstance().getReference().child("feedback");
        adminRelativeLayout = findViewById(R.id.fb_admin_linear_layout);
        userLinearLayout = findViewById(R.id.fb_user_linear_layout);
        if (userType.equalsIgnoreCase("admin")) {
            adminRelativeLayout.setVisibility(View.VISIBLE);
            userLinearLayout.setVisibility(View.GONE);
        } else if (userType.equalsIgnoreCase("user")) {
            adminRelativeLayout.setVisibility(View.GONE);
            userLinearLayout.setVisibility(View.VISIBLE);
        }

        userFullname = findViewById(R.id.fb_full_name);
        userMobileNumber = findViewById(R.id.fb_mobile_number);
        userMessage = findViewById(R.id.fb_feedback_message);

        adminNoFeedbackText = findViewById(R.id.fb_no_feedback_found);
        adminFeedbackRecyclerView = findViewById(R.id.fb_feedback_recycler_view);
        adminFeedbackRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        feedbackMessagesList = new ArrayList<>();
        feedbackMessageAdapter = new FeedbackMessageAdapter(this, feedbackMessagesList);
        reteriveFeedback();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void SendFeedbackBtn(View view) {
        mFullname = userFullname.getText().toString().trim();
        mMobile = userMobileNumber.getText().toString().trim();
        mMessage = userMessage.getText().toString().trim();

        if (mFullname.isEmpty()) {
            userFullname.setError("Enter Full Name");
            userFullname.requestFocus();
            return;
        }
        if (mMobile.isEmpty()) {
            userMobileNumber.setError("Enter Mobile Number");
            userMobileNumber.requestFocus();
            return;
        }
        if (mMessage.isEmpty()) {
            userMessage.setError("Enter Feedback Message");
            userMessage.requestFocus();
            return;
        }

        reference.child(String.valueOf(System.currentTimeMillis())).setValue(new FeedbackMessage(
                FirebaseAuth.getInstance().getCurrentUser().getUid(), mFullname, mMobile, mMessage, String.valueOf(System.currentTimeMillis())
        )).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(FeedBack.this, "Feedback Sent Successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(FeedBack.this, drawer_main.class));
                finish();
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(FeedBack.this, "Unable to send feedback. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void reteriveFeedback() {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                feedbackMessagesList.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    FeedbackMessage message = dataSnapshot1.getValue(FeedbackMessage.class);
                    Log.e("feedback", "onDataChange: " + message);
                    feedbackMessagesList.add(message);
                }

                adminFeedbackRecyclerView.setAdapter(feedbackMessageAdapter);
                feedbackMessageAdapter.notifyDataSetChanged();

                if (feedbackMessagesList.size() == 0) {
                    adminNoFeedbackText.setVisibility(View.VISIBLE);
                } else {
                    adminNoFeedbackText.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(FeedBack.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
