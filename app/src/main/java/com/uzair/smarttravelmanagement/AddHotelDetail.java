package com.uzair.smarttravelmanagement;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.Place;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.uzair.smarttravelmanagement.Adapters.ImageUploadListAdatper;
import com.uzair.smarttravelmanagement.Models.Hotel;
import com.rtchagas.pingplacepicker.PingPlacePicker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class AddHotelDetail extends AppCompatActivity {

    EditText HotelName, singleBedPrice, doubleBedPrice, discountSingleBedPrice, discountDoubleBedPrice, hotelDetails, ownerName, ownerPhone, ownerCnic, foodAvailable;
    Spinner CityList;
    CheckBox cbSingleBed, cbDoubleBed, cbDiscountAvailable;
    ImageView hotelLocation;
    Button hotelImagebtn;

    private ImageUploadListAdatper imageUploadListAdatper;
    private List<Uri> imagePathList;
    private RecyclerView uploadImageRV;
    private Uri filePath = null;

    private static final int PICK_IMAGE_REQUEST = 71;
    private static final int PLACE_PICKER_REQUEST = 1;

    String mHotelName, mSingleBedPrice = "", mDoubleBedPrice = "", mDiscountedSingleBedPrice = "", mDiscountedDoubleBedPrice = "", mHotelDetails, mCity, mOwnerName, mOwnerPhone, mOwnerCnic, mFoodAvailable, mHotelLatitude = "", mHotelLongitude = "";
    ArrayList<String> mImageUrl = new ArrayList<>();
    Boolean isSingleBed = false;
    Boolean isDoubleBed = false;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference("");

    //Firebase
    FirebaseStorage storage;
    StorageReference storageReference;

    ValueEventListener listener;
    ArrayAdapter<String> adapter;
    ArrayList<String> spinnerDataList;

    private ProgressDialog progressDialog;

    private boolean isEditable = false;
    private boolean isImageToUpload = true;
    private String hotelIdWhileEditing = "";

    private boolean isDiscountAvailable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_hotel_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(R.string.add_hotel_detils);

        init();

        Intent intent = getIntent();

        if (intent.hasExtra("data")) {
            isEditable = true;
            getSupportActionBar().setTitle("Edit Hotel");

            HashMap<String, Object> hashMap = (HashMap<String, Object>) intent.getSerializableExtra("data");
            hotelIdWhileEditing = hashMap.get("HotelId").toString();
            String intentHotelName = hashMap.get("HotelName").toString();
            String intentSingleBedPrice = hashMap.get("SingleBedPrice").toString();
            String intentDoubleBedPrice = hashMap.get("DoubleBedPrice").toString();
            String intentHotelDetails = hashMap.get("HotelDetails").toString();
            String intentCity = hashMap.get("City").toString();
            ArrayList<String> intentImageURL = (ArrayList<String>) hashMap.get("ImageURL");
            String intentOwnerName = hashMap.get("OwnerName").toString();
            String intentOwnerPhone = hashMap.get("OwnerPhone").toString();
            String intentOwnerCnic = hashMap.get("OwnerCnic").toString();
            String intentFoodAvailable = hashMap.get("FoodAvailable").toString();
            Boolean intentIsSingleBed = (Boolean) hashMap.get("isSingleBed");
            Boolean intentIsDoubleBed = (Boolean) hashMap.get("isDoubleBed");
            String intenthotelLatitude = hashMap.get("hotelLatitude").toString();
            String intenthotelLongitude = hashMap.get("hotelLongitude").toString();
            Boolean intentIsDiscountAvailable;
            try {
                intentIsDiscountAvailable = (Boolean) hashMap.get("isDiscountAvailable");
            } catch (Exception e) {
                intentIsDiscountAvailable = false;
                isDiscountAvailable = false;
            }

            HotelName.setText(intentHotelName);

            if (intentImageURL != null) {
                for (int i = 0; i < intentImageURL.size(); i++) {
                    imagePathList.add(Uri.parse(intentImageURL.get(i)));
                    imageUploadListAdatper.notifyDataSetChanged();
                }
            }

            mImageUrl = intentImageURL;
            hotelDetails.setText(intentHotelDetails);
            ownerName.setText(intentOwnerName);
            ownerPhone.setText(intentOwnerPhone);
            ownerCnic.setText(intentOwnerCnic);
            foodAvailable.setText(intentFoodAvailable);

            isImageToUpload = false;
            if (intentIsSingleBed) {
                cbSingleBed.setChecked(true);
                singleBedPrice.setEnabled(true);
                isSingleBed = true;
                singleBedPrice.setText(intentSingleBedPrice);
            }
            if (intentIsDoubleBed) {
                cbDoubleBed.setChecked(true);
                isDoubleBed = true;
                doubleBedPrice.setEnabled(true);
                doubleBedPrice.setText(intentDoubleBedPrice);
            }
            if (intentIsDiscountAvailable != null && intentIsDiscountAvailable) {
                isDiscountAvailable = true;
                String intentDiscountSingleBedPrice = hashMap.get("DiscountSingleBedPrice").toString();
                String intentDiscountDoubleBedPrice = hashMap.get("DiscountDoubleBedPrice").toString();
                cbDiscountAvailable = findViewById(R.id.checkbox_discount_available);
                cbDiscountAvailable.setChecked(true);
                discountSingleBedPrice.setText(intentDiscountSingleBedPrice);
                discountDoubleBedPrice.setText(intentDiscountDoubleBedPrice);
                findViewById(R.id.discount_single_bed_prices_linear_layout).setVisibility(View.VISIBLE);
                findViewById(R.id.discount_double_bed_prices_linear_layout).setVisibility(View.VISIBLE);
            }
            if (!intenthotelLatitude.isEmpty() && !intenthotelLongitude.isEmpty()) {
                mHotelLatitude = intenthotelLatitude;
                mHotelLongitude = intenthotelLongitude;
                hotelLocation.setImageResource(R.drawable.ic_location_on_green_24dp);
            }
        }

    }

    private void init() {
        HotelName = findViewById(R.id.hotel_name);
        singleBedPrice = findViewById(R.id.single_bed_price);
        doubleBedPrice = findViewById(R.id.double_bed_price);
        discountSingleBedPrice = findViewById(R.id.discount_single_bed_price);
        discountDoubleBedPrice = findViewById(R.id.discount_double_bed_price);
        hotelDetails = findViewById(R.id.hotel_details);
        CityList = findViewById(R.id.city_list);
        cbSingleBed = findViewById(R.id.checkbox_single_bed);
        cbDoubleBed = findViewById(R.id.checkbox_double_bed);
        ownerName = findViewById(R.id.owner_name);
        ownerPhone = findViewById(R.id.owner_phone);
        ownerCnic = findViewById(R.id.owner_cnic);
        foodAvailable = findViewById(R.id.food_details);
        hotelImagebtn = findViewById(R.id.add_image);
        hotelLocation = findViewById(R.id.add_location);

        uploadImageRV = findViewById(R.id.add_hotel_image_recycler_view);
        imagePathList = new ArrayList<>();
        imageUploadListAdatper = new ImageUploadListAdatper(this, imagePathList);
        uploadImageRV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        uploadImageRV.setHasFixedSize(true);
        uploadImageRV.setAdapter(imageUploadListAdatper);

        //Progress Dialog
        progressDialog = new ProgressDialog(this);

        //Firebase
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference().child("hotel-images");

        spinnerDataList = new ArrayList<>();
        adapter = new ArrayAdapter<String>(AddHotelDetail.this,
                android.R.layout.simple_spinner_dropdown_item, spinnerDataList);

        reteriveCityData();

        CityList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mCity = CityList.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mCity = "";
            }
        });

        hotelImagebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        hotelLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PingPlacePicker.IntentBuilder builder = new PingPlacePicker.IntentBuilder();
                builder.setAndroidApiKey("YOUR_ANDROID_API_KEY")
                        .setMapsApiKey("AIzaSyCOt6PpFu8XzUe6Fzg4zENPy3blt_-bqeM");

                // If you want to set a initial location rather then the current device location.
                // NOTE: enable_nearby_search MUST be true.
                // builder.setLatLng(new LatLng(37.4219999, -122.0862462))

                try {
                    Intent placeIntent = builder.build(AddHotelDetail.this);
                    startActivityForResult(placeIntent, PLACE_PICKER_REQUEST);
                } catch (Exception ex) {
                    // Google Play services is not available...
                }

            }
        });
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void uploadImage() {
        if (imagePathList.size() > 0) {

            mImageUrl.clear();
            for (int i = 0; i < imagePathList.size(); i++) {

                Random r = new Random();
                long x = r.nextLong();

                final StorageReference ref = storageReference.child("image" + x);
                ref.putFile(imagePathList.get(i))
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {

                                        mImageUrl.add(String.valueOf(uri));
                                        if (mImageUrl.size() == imagePathList.size()) {
                                            sendHotelDataToDatabase();
                                        }

                                    }
                                });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(AddHotelDetail.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            isImageToUpload = true;
            imagePathList.clear();
            if (data.getClipData() != null) {

                for (int i = 0; i < data.getClipData().getItemCount(); i++) {

                    Uri fileUri = data.getClipData().getItemAt(i).getUri();

                    imagePathList.add(fileUri);

                    imageUploadListAdatper.notifyDataSetChanged();

                }

            } else if (data.getData() != null) {

                imagePathList.add(data.getData());
                imageUploadListAdatper.notifyDataSetChanged();
            }


        }
        if ((requestCode == PLACE_PICKER_REQUEST) && (resultCode == RESULT_OK)) {
            Place place = PingPlacePicker.getPlace(data);
            if (place != null) {
                mHotelLatitude = String.valueOf(place.getLatLng().latitude);
                mHotelLongitude = String.valueOf(place.getLatLng().longitude);

                hotelLocation.setImageResource(R.drawable.ic_location_on_green_24dp);

            }
        }
    }

    public void onRoomTypeCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch (view.getId()) {
            case R.id.checkbox_single_bed:
                if (checked) {
                    isSingleBed = true;
                    singleBedPrice.setEnabled(true);
                } else {
                    isSingleBed = false;
                    singleBedPrice.setEnabled(false);
                    singleBedPrice.setText("");
                }
                break;
            case R.id.checkbox_double_bed:
                if (checked) {
                    isDoubleBed = true;
                    doubleBedPrice.setEnabled(true);
                } else {
                    isDoubleBed = false;
                    doubleBedPrice.setEnabled(false);
                    doubleBedPrice.setText("");
                }
                break;
            case R.id.checkbox_discount_available:
                if (isDiscountAvailable) {
                    findViewById(R.id.discount_single_bed_prices_linear_layout).setVisibility(View.GONE);
                    discountSingleBedPrice.setText("");
                    discountDoubleBedPrice.setText("");
                    findViewById(R.id.discount_double_bed_prices_linear_layout).setVisibility(View.GONE);
                    isDiscountAvailable = false;
                } else {
                    findViewById(R.id.discount_single_bed_prices_linear_layout).setVisibility(View.VISIBLE);
                    findViewById(R.id.discount_double_bed_prices_linear_layout).setVisibility(View.VISIBLE);
                    isDiscountAvailable = true;
                }
                break;

        }
    }

    public void AddHotelBtn(View view) {
        progressDialog.setMessage("Adding hotel to database...");
        progressDialog.show();

        mHotelName = HotelName.getText().toString();
        mSingleBedPrice = singleBedPrice.getText().toString();
        mDoubleBedPrice = doubleBedPrice.getText().toString();
        mHotelDetails = hotelDetails.getText().toString();
        mOwnerName = ownerName.getText().toString();
        mOwnerPhone = ownerPhone.getText().toString();
        mOwnerCnic = ownerCnic.getText().toString();
        mFoodAvailable = foodAvailable.getText().toString();

        if (mHotelName.isEmpty()) {
            HotelName.setError("Enter Hotel Name");
            HotelName.requestFocus();
            progressDialog.dismiss();
            return;
        }
        if (singleBedPrice.isEnabled()) {
            if (mSingleBedPrice.isEmpty()) {
                singleBedPrice.setError("Enter Price for Single Bed Room");
                singleBedPrice.requestFocus();
                progressDialog.dismiss();
                return;
            }
        }
        if (doubleBedPrice.isEnabled()) {
            if (mDoubleBedPrice.isEmpty()) {
                doubleBedPrice.setError("Enter price for Double bed room");
                doubleBedPrice.requestFocus();
                progressDialog.dismiss();
                return;
            }
        }
        if (isDiscountAvailable) {
            mDiscountedSingleBedPrice = discountSingleBedPrice.getText().toString();
            mDiscountedDoubleBedPrice = discountDoubleBedPrice.getText().toString();
            if (mDiscountedSingleBedPrice.isEmpty()) {
                discountSingleBedPrice.setError("Enter discounted Price for Single Bed Room");
                discountSingleBedPrice.requestFocus();
                progressDialog.dismiss();
                return;
            }
            if (mDiscountedDoubleBedPrice.isEmpty()) {
                discountDoubleBedPrice.setError("Enter discounted price for Double bed room");
                discountDoubleBedPrice.requestFocus();
                progressDialog.dismiss();
                return;
            }
            if (Double.valueOf(mSingleBedPrice) <= Double.valueOf(mDiscountedSingleBedPrice)) {
                discountSingleBedPrice.setError("Enter discounted Price Less then original price");
                discountSingleBedPrice.requestFocus();
                progressDialog.dismiss();
                return;
            }
            if (Double.valueOf(mDoubleBedPrice) <= Double.valueOf(mDiscountedDoubleBedPrice)) {
                discountDoubleBedPrice.setError("Enter discounted Price Less then original price");
                discountDoubleBedPrice.requestFocus();
                progressDialog.dismiss();
                return;
            }
        }
        if (mHotelDetails.isEmpty()) {
            hotelDetails.setError("Enter Hotel Details");
            hotelDetails.requestFocus();
            progressDialog.dismiss();
            return;
        }
        if (mCity.isEmpty()) {
            Toast.makeText(this, "Select City from dropdown", Toast.LENGTH_SHORT).show();
            CityList.requestFocus();
            progressDialog.dismiss();
            return;
        }
        if (mOwnerName.isEmpty()) {
            ownerName.setError("Enter Owner Name");
            ownerName.requestFocus();
            progressDialog.dismiss();
            return;
        }
        if (mOwnerPhone.isEmpty()) {
            ownerPhone.setError("Enter Owner Phone");
            ownerPhone.requestFocus();
            progressDialog.dismiss();
            return;
        }
        if (mOwnerCnic.isEmpty()) {
            ownerCnic.setError("Enter Owner Cnic");
            ownerCnic.requestFocus();
            progressDialog.dismiss();
            return;
        }
        if (mFoodAvailable.isEmpty()) {
            foodAvailable.setError("Enter available food");
            foodAvailable.requestFocus();
            progressDialog.dismiss();
            return;
        }
        if (imagePathList.size() == 0) {
            Toast.makeText(this, "Select Images for hotel.", Toast.LENGTH_SHORT).show();
            hotelImagebtn.requestFocus();
            progressDialog.dismiss();
            return;
        }
        if (mHotelLatitude.isEmpty()) {
            Toast.makeText(this, "Select Location for hotel.", Toast.LENGTH_SHORT).show();
            hotelLocation.requestFocus();
            progressDialog.dismiss();
            return;
        }

        if (isImageToUpload) {
            uploadImage();
        } else {
            sendHotelDataToDatabase();
        }

    }

    private void sendHotelDataToDatabase() {

        if (!isEditable) {
            ref.child("hotel-details").push()
                    .setValue(new Hotel(null, mHotelName, mSingleBedPrice, mDoubleBedPrice, mDiscountedSingleBedPrice, mDiscountedDoubleBedPrice, mHotelDetails, mCity, mImageUrl, mOwnerName, mOwnerPhone, mOwnerCnic, mFoodAvailable, isSingleBed, isDoubleBed, isDiscountAvailable, mHotelLatitude, mHotelLongitude, true))
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(AddHotelDetail.this, "Hotel Added Successfully", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            startActivity(new Intent(AddHotelDetail.this, drawer_main.class));
                        }
                    });
        } else if (isEditable) {
            progressDialog.dismiss();
            ref.child("hotel-details").child(hotelIdWhileEditing)
                    .setValue(new Hotel(null, mHotelName, mSingleBedPrice, mDoubleBedPrice, mDiscountedSingleBedPrice, mDiscountedDoubleBedPrice, mHotelDetails, mCity, mImageUrl, mOwnerName, mOwnerPhone, mOwnerCnic, mFoodAvailable, isSingleBed, isDoubleBed, isDiscountAvailable, mHotelLatitude, mHotelLongitude, true))
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(AddHotelDetail.this, "Hotel Edited Successfully", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            startActivity(new Intent(AddHotelDetail.this, drawer_main.class));
                        }
                    });
            // This is the key of hotel           hotelIdWhileEditing
        }
    }

    //    Reterive City Data from firebase database into spinner
    public void reteriveCityData() {
        listener = ref.child("city-list").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                spinnerDataList.clear();
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    spinnerDataList.add(item.getValue().toString());
                }
                Collections.sort(spinnerDataList);
                CityList.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //  Add city button
    public void AddCityBtn(View view) {
        final Dialog dialog = new Dialog(AddHotelDetail.this);
        dialog.setContentView(R.layout.add_city_dialog);
        dialog.setTitle(R.string.add_City);

        Spinner cityListSpinner = dialog.findViewById(R.id.city_list_spinner);
        cityListSpinner.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        final EditText cityName = dialog.findViewById(R.id.city_name);

        Button addCityBtn = dialog.findViewById(R.id.add_city_btn);
        addCityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String city = cityName.getText().toString();
                if (city.isEmpty()) {
                    cityName.setError("Enter City Name");
                    cityName.requestFocus();
                    return;

                } else {
                    for (int i = 0; i < spinnerDataList.size(); i++) {
                        if (spinnerDataList.get(i).equalsIgnoreCase(city)) {
                            cityName.setError("City Alraedy exist");
                            cityName.requestFocus();
                            return;
                        }
                    }
                    ref.child("city-list").push().setValue(city).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(AddHotelDetail.this, "City Added Successfully", Toast.LENGTH_SHORT).show();
                            spinnerDataList.clear();
                            recreate();
                            adapter.notifyDataSetChanged();
                            dialog.dismiss();
                        }
                    });
                }


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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
