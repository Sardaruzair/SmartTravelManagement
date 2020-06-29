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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import com.uzair.smarttravelmanagement.Models.NearbyLocationsModel;
import com.rtchagas.pingplacepicker.PingPlacePicker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class NearbyLocations extends AppCompatActivity {

    private ArrayAdapter<String> adapter;
    private ArrayList<String> spinnerDataList;

    private ArrayAdapter<String> hotelsAdapter;
    private ArrayList<String> hotelsSpinnerDataList;
    private ArrayList<String> hotelsKeySpinnerDataList;

    private FloatingActionButton addNearbyLocationFAB;

    private String mCity = "";
    private String mHotelKey = "";
    private String mLocationName = "";

    private ValueEventListener listener;
    private DatabaseReference reference;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    private ImageView hotelLocation;
    private String mHotelLatitude = "", mHotelLongitude = "";

    private ImageUploadListAdatper imageUploadListAdatper;
    private List<Uri> imagePathList;
    private RecyclerView uploadImageRV;
    private Uri filePath = null;
    private Boolean isImageToUpload = false;
    private ArrayList<String> mImageUrl = new ArrayList<>();
    private boolean isEditable = false;

    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference ref = database.getReference("");

    private static final int PICK_IMAGE_REQUEST = 71;
    private static final int PLACE_PICKER_REQUEST = 1;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_locations);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(R.string.nearby_locations);

        init();
    }

    private void init() {
        mProgressDialog = new ProgressDialog(this);
        reference = FirebaseDatabase.getInstance().getReference();
        //Firebase
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference().child("location-images");

        addNearbyLocationFAB = findViewById(R.id.add_nearby_location_FAB);
        addNearbyLocationFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddNearbyLocationDialog();
            }
        });

        spinnerDataList = new ArrayList<>();
        adapter = new ArrayAdapter<String>(NearbyLocations.this,
                android.R.layout.simple_spinner_dropdown_item, spinnerDataList);

        hotelsSpinnerDataList = new ArrayList<>();
        hotelsKeySpinnerDataList = new ArrayList<>();
        hotelsAdapter = new ArrayAdapter<String>(NearbyLocations.this,
                android.R.layout.simple_spinner_dropdown_item, hotelsSpinnerDataList);

        reteriveCityData();
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

    private void getHotelList(final String city) {
        reference.child("hotel-details").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                hotelsSpinnerDataList.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Hotel hotel = dataSnapshot1.getValue(Hotel.class);
                    if (hotel.getCity().equalsIgnoreCase(city)) {
                        hotel.setHotelId(dataSnapshot1.getKey());
                        hotelsSpinnerDataList.add(hotel.getHotelName());
                        hotelsKeySpinnerDataList.add(hotel.getHotelId());
                    }

                    Collections.sort(hotelsSpinnerDataList);
                }

                hotelsAdapter.notifyDataSetChanged();
//                hotelListAdapter.notifyDataSetChanged();
//                hotelsListRecyclerView.setAdapter(hotelListAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(NearbyLocations.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddNearbyLocationDialog() {
        final Dialog dialog = new Dialog(NearbyLocations.this);
        dialog.setContentView(R.layout.add_nearby_locations_dialog);
        dialog.setTitle(R.string.nearby_locations);

        final Spinner cityListSpinner = dialog.findViewById(R.id.city_list_spinner);
        cityListSpinner.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        final Spinner hotelsListSpinner = dialog.findViewById(R.id.hotels_list_spinner);
        hotelsListSpinner.setAdapter(hotelsAdapter);
        hotelsAdapter.notifyDataSetChanged();

        final EditText locationName = dialog.findViewById(R.id.location_name);

        hotelLocation = dialog.findViewById(R.id.add_location);
        hotelLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PingPlacePicker.IntentBuilder builder = new PingPlacePicker.IntentBuilder();
                builder.setAndroidApiKey("YOUR_ANDROID_API_KEY")
                        .setMapsApiKey("AIzaSyCOt6PpFu8XzUe6Fzg4zENPy3blt_-bqeM");

                try {
                    Intent placeIntent = builder.build(NearbyLocations.this);
                    startActivityForResult(placeIntent, PLACE_PICKER_REQUEST);
                } catch (Exception ex) {
                    // Google Play services is not available...
                }

            }
        });

        uploadImageRV = dialog.findViewById(R.id.add_location_image_recycler_view);
        imagePathList = new ArrayList<>();
        imageUploadListAdatper = new ImageUploadListAdatper(this, imagePathList);
        uploadImageRV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        uploadImageRV.setHasFixedSize(true);
        uploadImageRV.setAdapter(imageUploadListAdatper);

        final Button hotelImagebtn = dialog.findViewById(R.id.add_location_image);
        hotelImagebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        TextView addLocation = dialog.findViewById(R.id.select_city_btn);
        addLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressDialog.setMessage("Uploading location details to database.");
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();

                mLocationName = locationName.getText().toString();
                if (mCity.isEmpty()) {
                    Toast.makeText(NearbyLocations.this, "Select City from the list.", Toast.LENGTH_SHORT).show();
                    cityListSpinner.requestFocus();
                    mProgressDialog.dismiss();
                    return;
                }
                if (mHotelKey.isEmpty()) {
                    Toast.makeText(NearbyLocations.this, "Select Hotel Name from the list", Toast.LENGTH_LONG).show();
                    hotelsListSpinner.requestFocus();
                    mProgressDialog.dismiss();
                    return;
                }
                if (mLocationName.isEmpty()) {
                    locationName.setError("Enter location name");
                    locationName.requestFocus();
                    mProgressDialog.dismiss();
                    return;
                }
                if (imagePathList.size() == 0) {
                    Toast.makeText(NearbyLocations.this, "Select Images for this location.", Toast.LENGTH_SHORT).show();
                    hotelImagebtn.requestFocus();
                    mProgressDialog.dismiss();
                    return;
                }
                if (mHotelLatitude.isEmpty()) {
                    Toast.makeText(NearbyLocations.this, "Select Location for this place.", Toast.LENGTH_SHORT).show();
                    hotelLocation.requestFocus();
                    mProgressDialog.dismiss();
                    return;
                }

                if (isImageToUpload) {
                    uploadImage();
                } else {
                    addLocationToDatabase();
                }
            }
        });

        cityListSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mCity = cityListSpinner.getSelectedItem().toString();
                getHotelList(mCity);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        hotelsListSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mHotelKey = hotelsKeySpinnerDataList.get(hotelsListSpinner.getSelectedItemPosition());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        TextView Cancel = dialog.findViewById(R.id.cancel_btn);
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

    private void addLocationToDatabase() {
        if (!isEditable) {
            ref.child("nearby-location-details").push()
                    .setValue(new NearbyLocationsModel(mHotelKey, mCity, mLocationName, mHotelLatitude, mHotelLongitude, mImageUrl))
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(NearbyLocations.this, "Location Added Successfully", Toast.LENGTH_SHORT).show();
                            mProgressDialog.dismiss();
                        }
                    });
        } else if (isEditable) {
//            progressDialog.dismiss();
//            ref.child("nearby-location-details").child(hotelIdWhileEditing)
//                    .setValue(new Hotel(null, mHotelName, mSingleBedPrice, mDoubleBedPrice, mHotelDetails, mCity, mImageUrl, mOwnerName, mOwnerPhone, mOwnerCnic, mFoodAvailable, isSingleBed, isDoubleBed, mHotelLatitude, mHotelLongitude, true))
//                    .addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                            Toast.makeText(NearbyLocations.this, "Hotel Edited Successfully", Toast.LENGTH_SHORT).show();
//                            progressDialog.dismiss();
//                        }
//                    });
            // This is the key of hotel           hotelIdWhileEditing
        }
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
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
                                            addLocationToDatabase();
                                        }

                                    }
                                });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                mProgressDialog.dismiss();
                                Toast.makeText(NearbyLocations.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }

        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
