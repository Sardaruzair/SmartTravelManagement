package com.uzair.smarttravelmanagement;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.uzair.smarttravelmanagement.Adapters.DiscountedHotelListAdapter;
import com.uzair.smarttravelmanagement.Adapters.HotelListAdapter;
import com.uzair.smarttravelmanagement.Models.Hotel;
import com.uzair.smarttravelmanagement.Models.User;
import com.uzair.smarttravelmanagement.Notifications.Token;
import com.uzair.smarttravelmanagement.WeatherModule.WeatherMain;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

public class drawer_main extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;
    private DrawerLayout drawer;
    private View headerView;
    private NavigationView navigationView;
    private RecyclerView hotelsListRecyclerView, discountedHotelsListRecyclerView;
    private ArrayList<Hotel> hotelsList, discountedHotelsList;
    private HotelListAdapter hotelListAdapter;
    private DiscountedHotelListAdapter discountedHotelListAdapter;
    private FloatingActionButton fabAddHotel;
    private DatabaseReference reference;
    private Spinner CityList;
    private String mCity = "";

    public User user;
    private String UserId;

    private static drawer_main instance;

    private ProgressDialog progressDialog;

    private ValueEventListener listener;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> spinnerDataList;
    FirebaseUser currentFirebaseUser;

    public static drawer_main getInstance() {
        if (instance == null) {
            android.util.Log.i("workflow", "getInstance: new");
            instance = new drawer_main();
        }
        return instance;
    }

    public static final boolean isInstanciated() {
        return instance != null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        setContentView(R.layout.activity_drawer_main);

//        if (ActivityCompat.checkSelfPermission(drawer_main.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
//                ActivityCompat.checkSelfPermission(drawer_main.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }

        instance = this;
        init();
        getUser();
    }

    private void init() {
        reference = FirebaseDatabase.getInstance().getReference();
        toolbar = findViewById(R.id.toolbar);
        Drawable overFlowIcon = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_filter_list_white_24dp);
        toolbar.setOverflowIcon(overFlowIcon);
        setSupportActionBar(toolbar);
        fabAddHotel = findViewById(R.id.fab_add_hotel);
        fabAddHotel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(drawer_main.this, AddHotelDetail.class));
            }
        });
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        headerView = navigationView.getHeaderView(0);

        spinnerDataList = new ArrayList<>();
        adapter = new ArrayAdapter<String>(drawer_main.this,
                android.R.layout.simple_spinner_dropdown_item, spinnerDataList);

        reteriveCityData();

        hotelsListRecyclerView = findViewById(R.id.hotels_list_recycler_view);
        hotelsListRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        hotelsList = new ArrayList<Hotel>();
        hotelListAdapter = new HotelListAdapter(drawer_main.this, hotelsList);

        discountedHotelsListRecyclerView = findViewById(R.id.discounted_hotels_list_recycler_view);
        discountedHotelsListRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        discountedHotelsList = new ArrayList<Hotel>();
        discountedHotelListAdapter = new DiscountedHotelListAdapter(drawer_main.this, discountedHotelsList);

        getHotelList(mCity);

    }

    private void getHotelList(final String city) {
        reference.child("hotel-details").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                hotelsList.clear();
                discountedHotelsList.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Hotel hotel = dataSnapshot1.getValue(Hotel.class);
                    if (hotel.getRoomsAvailable()) {
                        if (city.isEmpty()) {
                            hotel.setHotelId(dataSnapshot1.getKey());
                            if (hotel.getDiscountAvailable() != null && hotel.getDiscountAvailable()) {
                                discountedHotelsList.add(hotel);
                            } else {
                                hotelsList.add(hotel);
                            }
                        } else {
                            if (hotel.getCity().equalsIgnoreCase(city)) {
                                hotel.setHotelId(dataSnapshot1.getKey());
                                if (hotel.getDiscountAvailable() != null && hotel.getDiscountAvailable()) {
                                    discountedHotelsList.add(hotel);
                                } else {
                                    hotelsList.add(hotel);
                                }
                            }
                        }
                    }
                }

                hotelListAdapter.notifyDataSetChanged();
                hotelsListRecyclerView.setAdapter(hotelListAdapter);

                discountedHotelListAdapter.notifyDataSetChanged();
                discountedHotelsListRecyclerView.setAdapter(discountedHotelListAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(drawer_main.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getUser() {
        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    if (dataSnapshot1.getKey().equals(currentFirebaseUser.getUid())) {
                        setUserId(currentFirebaseUser.getUid());
                        user = dataSnapshot1.getValue(User.class);

                        TextView headerName = headerView.findViewById(R.id.nav_header_name);
                        headerName.setText(user.getFirstName() + " " + user.getLastName());
                        TextView headerUserType = headerView.findViewById(R.id.nav_header_city);
                        headerUserType.setText(user.getUserType());
                        if (user.getUserType().equalsIgnoreCase("user")) {
                            toolbar.setTitle("User Panel");
                            toolbar.setTitleTextColor(Color.WHITE);
                            fabAddHotel.setVisibility(View.GONE);
                            Menu nav_Menu = navigationView.getMenu();
                            nav_Menu.findItem(R.id.nav_chat).setVisible(false);
//                            nav_Menu.findItem(R.id.nav_nearby_locations).setVisible(false);
                        } else if (user.getUserType().equalsIgnoreCase("admin")) {
                            toolbar.setTitle("Admin Panel");
                            toolbar.setTitleTextColor(Color.WHITE);
                            Menu nav_Menu = navigationView.getMenu();
                            nav_Menu.findItem(R.id.nav_chat_with_admin).setVisible(false);
                        }
                        updateToken(FirebaseInstanceId.getInstance().getToken());
                        progressDialog.hide();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_booking_requests:
                closeDrawer();
                startActivity(new Intent(drawer_main.this, BookingRequests.class));
                break;
            case R.id.nav_chat_with_admin:
                closeDrawer();
                Intent i = new Intent(drawer_main.this, MessageActivity.class);
                i.putExtra("adminId", "v4Bs8oOG5kUwE9wzpMTK3rG7dsH2");
                startActivity(i);
                break;
            case R.id.nav_chat:
                closeDrawer();
                startActivity(new Intent(drawer_main.this, ChatsListAdmin.class));
                break;
            case R.id.nav_weather:
                closeDrawer();
                startActivity(new Intent(drawer_main.this, WeatherMain.class));
                break;
//            case R.id.nav_nearby_locations:
//                closeDrawer();
//                startActivity(new Intent(drawer_main.this, NearbyLocations.class));
//                break;
            case R.id.nav_feedback:
                closeDrawer();
                Intent intent = new Intent(drawer_main.this, FeedBack.class);
                intent.putExtra("user_id", user.getUserId());
                intent.putExtra("user_type", user.getUserType());
                startActivity(intent);
                break;
            case R.id.nav_logout:
                closeDrawer();
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(drawer_main.this, Login.class));
                break;
        }

        return true;
    }

    private void closeDrawer() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public void appbar_filter_button(View view) {
        final Dialog dialog = new Dialog(drawer_main.this);
        dialog.setContentView(R.layout.city_list_dialog);
        dialog.setTitle(R.string.select_city);

        final Spinner cityListSpinner = dialog.findViewById(R.id.city_list_spinner);
        cityListSpinner.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        Button selectCityBtn = dialog.findViewById(R.id.select_city_btn);
        selectCityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(drawer_main.this, "City Selected: " + mCity, Toast.LENGTH_SHORT).show();
                getHotelList(mCity);
                dialog.dismiss();
            }
        });

        cityListSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mCity = cityListSpinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Button Cancel = dialog.findViewById(R.id.cancel_btn);
        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.show();
        dialog.getWindow().setAttributes(lp);
        dialog.setCancelable(false);
    }

    //Reterive City Data from firebase database into spinner
    public void reteriveCityData() {
        listener = reference.child("city-list").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                spinnerDataList.clear();
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    spinnerDataList.add(item.getValue().toString());
                }
                Collections.sort(spinnerDataList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateToken(String token) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(currentFirebaseUser.getUid()).setValue(token1);
    }

}
