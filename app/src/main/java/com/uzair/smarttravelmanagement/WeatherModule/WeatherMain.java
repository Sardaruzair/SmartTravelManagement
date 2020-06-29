package com.uzair.smarttravelmanagement.WeatherModule;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uzair.smarttravelmanagement.Adapters.ViewPagerAdapter;
import com.uzair.smarttravelmanagement.R;
import com.uzair.smarttravelmanagement.WeatherModule.Common.Common;
import com.uzair.smarttravelmanagement.WeatherModule.Fragments.ForecastFragment;
import com.uzair.smarttravelmanagement.WeatherModule.Fragments.TodayWeatherFragment;

import java.util.ArrayList;
import java.util.Collections;

public class WeatherMain extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private CoordinatorLayout coordinatorLayout;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;

    private ValueEventListener listener;
    private DatabaseReference reference;

    private ArrayAdapter<String> adapter;
    private ArrayList<String> spinnerDataList;
    private String mCity = "";

    private static final int MY_PERMISSION_REQUEST_CODE = 7171;
    private static final int REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_main);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        init();
    }

    private void init() {
        reference = FirebaseDatabase.getInstance().getReference();
        coordinatorLayout = findViewById(R.id.weather_root_view);
        toolbar = findViewById(R.id.toolbar);
        Drawable overFlowIcon = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_filter_list_white_24dp);
        toolbar.setOverflowIcon(overFlowIcon);
        toolbar.setTitle("Weather Updates");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Log.d("Location", "onPermissionsChecked: called......");

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE);
            return;
        }
        buildLocalRequest();
        buildLocationCallBack();

        Log.d("Location", "onPermissionsChecked: calledddd");
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(WeatherMain.this);
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

        spinnerDataList = new ArrayList<>();
        adapter = new ArrayAdapter<String>(WeatherMain.this,
                android.R.layout.simple_spinner_dropdown_item, spinnerDataList);

        reteriveCityData();
    }

    private void buildLocationCallBack() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                Common.current_location = locationResult.getLastLocation();
                viewPager = findViewById(R.id.view_pager);
                setupViewPager(viewPager);
                tabLayout = findViewById(R.id.tabs);
                tabLayout.setupWithViewPager(viewPager);

                Log.d("Location", locationResult.getLastLocation().getLatitude() + "/" + locationResult.getLastLocation().getLongitude());
            }
        };
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(TodayWeatherFragment.getInstance(), "Today");
        adapter.addFragment(ForecastFragment.getInstance(), "5 DAYS");
        viewPager.setAdapter(adapter);
    }

    private void buildLocalRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setSmallestDisplacement(10.0f);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

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

    public void appbar_filter_button(View view) {
        final Dialog dialog = new Dialog(WeatherMain.this);
        dialog.setContentView(R.layout.city_list_dialog);
        dialog.setTitle(R.string.select_city);

        final Spinner cityListSpinner = dialog.findViewById(R.id.city_list_spinner);
        cityListSpinner.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        Button selectCityBtn = dialog.findViewById(R.id.select_city_btn);
        selectCityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(WeatherMain.this, "City Selected: " + mCity, Toast.LENGTH_SHORT).show();
                Common.cityName = mCity;
                TodayWeatherFragment.getWeatherInformation();
                ForecastFragment.getForecastWeatherInformation();
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
}
