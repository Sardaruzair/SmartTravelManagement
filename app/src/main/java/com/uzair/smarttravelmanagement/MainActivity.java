package com.uzair.smarttravelmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uzair.smarttravelmanagement.Adapters.HotelListAdapter;
import com.uzair.smarttravelmanagement.Models.Hotel;
import com.uzair.smarttravelmanagement.Models.User;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    RecyclerView hotelsListRecyclerView;
    ArrayList<Hotel> hotelsList;
    HotelListAdapter hotelListAdapter;
    TextView adminPanelHeading;
    Button addHotelsBtn;

    DatabaseReference reference;

    public User user;
    private String UserId;

    private static MainActivity instance;

    public static MainActivity getInstance() {
        if (instance == null) {
            android.util.Log.i("workflow", "getInstance: new");
            instance = new MainActivity();
        }
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        instance = this;

        reference = FirebaseDatabase.getInstance().getReference().child("hotel-details");

//        getUser();
    }

    private void getUser() {
        final FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    if(dataSnapshot1.getKey().equals(currentFirebaseUser.getUid())) {
                        setUserId(currentFirebaseUser.getUid());
                        user = dataSnapshot1.getValue(User.class);
                        init();
                    } else {
                        startActivity(new Intent(MainActivity.this, Login.class));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public static final boolean isInstanciated() {
        return instance != null;
    }

    private void init() {
//        adminPanelHeading = findViewById(R.id.admin_panel_heading);
//        addHotelsBtn = findViewById(R.id.add_hotels_btn);
//        if(user.getUserType().equalsIgnoreCase("user")){
//            adminPanelHeading.setVisibility(View.GONE);
//            addHotelsBtn.setVisibility(View.GONE);
//        }
//        hotelsListRecyclerView = findViewById(R.id.hotels_list_recycler_view);
//        hotelsListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        hotelsList = new ArrayList<Hotel>();
//        hotelListAdapter = new HotelListAdapter(MainActivity.this, hotelsList);
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                hotelsList.clear();
//                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
//                    Hotel hotel = dataSnapshot1.getValue(Hotel.class);
//                    if (hotel.getRoomsAvailable()) {
//                        hotel.setHotelId(dataSnapshot1.getKey());
//                        hotelsList.add(hotel);
//                    }
//                }
//
//                hotelListAdapter.notifyDataSetChanged();
//                hotelsListRecyclerView.setAdapter(hotelListAdapter);
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Toast.makeText(MainActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
//            }
//        });


    }

    public void LogoutBtn(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(MainActivity.this, Login.class));
    }

    public void addHotelBtn(View view) {
        startActivity(new Intent(MainActivity.this, AddHotelDetail.class));
    }
}
