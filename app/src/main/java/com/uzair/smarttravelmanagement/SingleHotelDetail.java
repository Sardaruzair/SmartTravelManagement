package com.uzair.smarttravelmanagement;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.uzair.smarttravelmanagement.Adapters.ImageUploadListAdatper;
import com.squareup.picasso.Picasso;
import com.uzair.smarttravelmanagement.Models.City;

import java.util.ArrayList;
import java.util.HashMap;

public class SingleHotelDetail extends AppCompatActivity implements OnMapReadyCallback {

    //Data to get from intent
    private String HotelId, HotelName, SingleBedPrice, DoubleBedPrice, DiscountSingleBedPrice, DiscountDoubleBedPrice, HotelDetails, City, OwnerName, OwnerPhone, OwnerCnic, FoodAvailable, hotelLatitude, hotelLongitude;
    private Boolean isSingleBed, isDoubleBed, isRoomsAvailable, isDiscountAvailable;
    private ArrayList<String> ImageURL;

    //TextView fields in the view
    private ImageView ivHotelImage;
    private TextView tvHotelName, tvHotelPrice, tvDiscountHotelPrice, tvRoomsAvailable, tvHotelDetail, tvFoodDetails, tvOwnerName, tvOwnerPhone, tvOwnerCnic;

    private HashMap<String, Object> hashMap;

    private ProgressDialog progressDialog;
    private ProgressDialog progressDialogStart;

    private ImageUploadListAdatper imageUploadListAdatper;
    private RecyclerView hotelImagesRecyclerView;

    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialogStart = new ProgressDialog(this);
        progressDialogStart.setMessage("Loading...");
        progressDialogStart.setCancelable(false);
        progressDialogStart.show();
        setContentView(R.layout.activity_single_hotel_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent intent = getIntent();

        hashMap = (HashMap<String, Object>) intent.getSerializableExtra("data");
        HotelId = hashMap.get("HotelId").toString();
        HotelName = hashMap.get("HotelName").toString();
        SingleBedPrice = hashMap.get("SingleBedPrice").toString();
        DoubleBedPrice = hashMap.get("DoubleBedPrice").toString();
        HotelDetails = hashMap.get("HotelDetails").toString();
        City = hashMap.get("City").toString();
        ImageURL = (ArrayList<String>) hashMap.get("ImageURL");
        OwnerName = hashMap.get("OwnerName").toString();
        OwnerPhone = hashMap.get("OwnerPhone").toString();
        OwnerCnic = hashMap.get("OwnerCnic").toString();
        FoodAvailable = hashMap.get("FoodAvailable").toString();
        isSingleBed = (Boolean) hashMap.get("isSingleBed");
        isDoubleBed = (Boolean) hashMap.get("isDoubleBed");
        hotelLatitude = hashMap.get("hotelLatitude").toString();
        hotelLongitude = hashMap.get("hotelLongitude").toString();
        isRoomsAvailable = (Boolean) hashMap.get("isRoomsAvailable");

        try{
            isDiscountAvailable = (Boolean) hashMap.get("isDiscountAvailable");
        } catch (Exception e){
            isDiscountAvailable = false;
        }

        if(isDiscountAvailable != null && isDiscountAvailable){
            DiscountSingleBedPrice = hashMap.get("DiscountSingleBedPrice").toString();
            DiscountDoubleBedPrice = hashMap.get("DiscountDoubleBedPrice").toString();
        }

        getSupportActionBar().setTitle(HotelName);

        init();
        progressDialogStart.dismiss();

    }

    private void init() {
        reference = FirebaseDatabase.getInstance().getReference();

        //Progress Dialog
        progressDialog = new ProgressDialog(this);

        ivHotelImage = findViewById(R.id.hotel_image);

        if (ImageURL != null) {
            Picasso.get().load(ImageURL.get(0)).into(ivHotelImage);
        }

        ArrayList<Uri> imageUri = new ArrayList<>();
        for (int i = 0; i < ImageURL.size(); i++) {
            imageUri.add(Uri.parse(ImageURL.get(i)));
        }

        hotelImagesRecyclerView = findViewById(R.id.hotel_images_recyclerview);
        imageUploadListAdatper = new ImageUploadListAdatper(this, imageUri);
        hotelImagesRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        hotelImagesRecyclerView.setHasFixedSize(true);
        hotelImagesRecyclerView.setAdapter(imageUploadListAdatper);

        tvHotelName = findViewById(R.id.hotel_name);
        tvHotelName.setText(HotelName + "\nCity: " + City);

        tvHotelPrice = findViewById(R.id.hotel_price);
        tvDiscountHotelPrice = findViewById(R.id.hotel_discount_price);
        tvRoomsAvailable = findViewById(R.id.rooms_available);

        if (isSingleBed && isDoubleBed) {
            tvRoomsAvailable.setText("Single Bed, Double Bed");
            tvHotelPrice.setText("Rs." + SingleBedPrice + " - " + "Rs. " + DoubleBedPrice);
        } else if (isSingleBed && !isDoubleBed) {
            tvRoomsAvailable.setText("Single Bed");
            tvHotelPrice.setText("Rs." + SingleBedPrice);
        } else if (!isSingleBed && isDoubleBed) {
            tvRoomsAvailable.setText("Double Bed");
            tvHotelPrice.setText("Rs. " + DoubleBedPrice);
        } else if (!isSingleBed && !isDoubleBed) {
            tvRoomsAvailable.setText("No Bed Available");
            tvHotelPrice.setText(" ---- ");
        }
        if (isDiscountAvailable != null && isDiscountAvailable) {
            tvHotelPrice.setPaintFlags(tvHotelPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            tvDiscountHotelPrice.setText("Rs." + DiscountSingleBedPrice + " - " + "Rs. " + DiscountDoubleBedPrice);
        } else {
            tvDiscountHotelPrice.setVisibility(View.GONE);
        }

        tvHotelDetail = findViewById(R.id.hotel_details);
        tvHotelDetail.setText(HotelDetails);

        tvFoodDetails = findViewById(R.id.food_details);
        tvFoodDetails.setText(FoodAvailable);

        tvOwnerName = findViewById(R.id.owner_name);
        tvOwnerName.setText(OwnerName);

        tvOwnerPhone = findViewById(R.id.owner_phone);
        tvOwnerPhone.setText(OwnerPhone);

        tvOwnerCnic = findViewById(R.id.owner_cnic);
        tvOwnerCnic.setText(OwnerCnic);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.hotel_map);
        mapFragment.getMapAsync(this);

        if (drawer_main.isInstanciated()) {
            if (drawer_main.getInstance().user.getUserType().equalsIgnoreCase("user")) {
                findViewById(R.id.owner_phone_heading).setVisibility(View.GONE);
                findViewById(R.id.owner_cnic_heading).setVisibility(View.GONE);
                tvOwnerPhone.setVisibility(View.GONE);
                tvOwnerCnic.setVisibility(View.GONE);
                findViewById(R.id.editFAB).setVisibility(View.GONE);
                findViewById(R.id.deleteFAB).setVisibility(View.GONE);
                findViewById(R.id.book_hotel).setVisibility(View.VISIBLE);
            } else if (drawer_main.getInstance().user.getUserType().equalsIgnoreCase("admin")) {
                findViewById(R.id.owner_phone_heading).setVisibility(View.VISIBLE);
                findViewById(R.id.owner_cnic_heading).setVisibility(View.VISIBLE);
                tvOwnerPhone.setVisibility(View.VISIBLE);
                tvOwnerCnic.setVisibility(View.VISIBLE);
                findViewById(R.id.editFAB).setVisibility(View.VISIBLE);
                findViewById(R.id.deleteFAB).setVisibility(View.VISIBLE);
                findViewById(R.id.book_hotel).setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng hotelLatLng = new LatLng(Float.valueOf(hotelLatitude), Float.valueOf(hotelLongitude));
        googleMap.addMarker(new MarkerOptions().position(hotelLatLng)
                .title(HotelName));

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(hotelLatLng, 15);
        googleMap.animateCamera(cameraUpdate);
    }

    public void editHotelDetailsBtn(View view) {
        Intent i = new Intent(SingleHotelDetail.this, AddHotelDetail.class);
        i.putExtra("data", hashMap);
        startActivity(i);
    }

    public void deleteHotelDetailsBtn(View view) {

        final AlertDialog.Builder dialog = new AlertDialog.Builder(SingleHotelDetail.this);
        dialog.setTitle("Are you sure to delete?");
        dialog.setMessage(HotelName);
        dialog.setCancelable(false);
        dialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressDialog.setMessage("Deleting Hotel from Database....");
                progressDialog.show();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("hotel-details").child(HotelId);
                ref.removeValue();
                Toast.makeText(SingleHotelDetail.this, "Hotel Removed!!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SingleHotelDetail.this, drawer_main.class));
                progressDialog.dismiss();
            }
        });
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void BookHotelBtn(View view) {
        Intent i = new Intent(SingleHotelDetail.this, HotelBooking.class);
        i.putExtra("data", hashMap);
        startActivity(i);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
