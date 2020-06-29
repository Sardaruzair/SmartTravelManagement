package com.uzair.smarttravelmanagement;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.Place;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rtchagas.pingplacepicker.PingPlacePicker;
import com.uzair.smarttravelmanagement.Interfaces.APIService;
import com.uzair.smarttravelmanagement.Models.HotelBookingModel;
import com.uzair.smarttravelmanagement.Models.User;
import com.uzair.smarttravelmanagement.Notifications.Client;
import com.uzair.smarttravelmanagement.Notifications.Data;
import com.uzair.smarttravelmanagement.Notifications.MyResponse;
import com.uzair.smarttravelmanagement.Notifications.Sender;
import com.uzair.smarttravelmanagement.Notifications.Token;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HotelBooking extends AppCompatActivity {

    //Data to get from intent
    private String HotelId, HotelName, SingleBedPrice, DoubleBedPrice, HotelDetails, City,
            OwnerName, OwnerPhone, OwnerCnic, FoodAvailable, hotelLatitude, hotelLongitude;
    private Boolean isSingleBed, isDoubleBed, isRoomsAvailable;
    private ArrayList<String> ImageURL;
    private HashMap<String, Object> hashMap;

    private EditText hbFirstName, hbLastName, hbEmail, hbPhone, hbSingleBeds, hbDoubleBeds,
            hbAdults, hbChildrens, hbCheckinDate, hbCheckoutDate, hbComments;
    private CheckBox hbSingleBedCheckBox, hbDoubleBedCheckBox, hbBookTransportCheckBox;
    private LinearLayout hbPickupLocationLinearLayout;
    private ImageView hbPickupLocationImage;
    private Spinner hbTranportTypeSpinner;
    private String[] transportTypeArray = {"Bus", "Car", "Carry Van", "Coaster", "Hiace"};
    private float[] transportTypePriceArray = {20, 7, 7, 15, 12};

    private String mhbFirstName, mhbLastName, mhbEmail, mhbPhone, mhbCheckinDate, mhbCheckoutDate,
            mhbComments = "", mhbSingleBeds, mhbDoubleBeds, mhbAdults, mhbChildrens;
    private Boolean mBookTransport = false;
    private String mPickupLatitude = "", mPickupLongitude = "", transportType = "";
    private float noOfDays, transportTypePrice, transportPrice, netPrice, calDistance, totalPrice;

    private DatabaseReference reference;

    private ProgressDialog progressDialog;

    private final String ADMIN_USER_ID = "v4Bs8oOG5kUwE9wzpMTK3rG7dsH2";
    private static final int PLACE_PICKER_REQUEST = 1;

    FirebaseUser firebaseUser;
    APIService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_booking);

        getSupportActionBar().setTitle("Hotel Booking");

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

        TextView hotelDetail = findViewById(R.id.hb_selected_hotel_detail);
        hotelDetail.setText(HotelName + " - " + City);

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        init();
    }

    private void init() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference();

        progressDialog = new ProgressDialog(this);

        hbFirstName = findViewById(R.id.hb_first_name);
        hbLastName = findViewById(R.id.hb_last_name);
        hbEmail = findViewById(R.id.hb_email);
        hbPhone = findViewById(R.id.hb_phone);
        hbSingleBeds = findViewById(R.id.hb_single_bed_rooms);
        hbDoubleBeds = findViewById(R.id.hb_double_bed_rooms);
        hbAdults = findViewById(R.id.hb_adults);
        hbChildrens = findViewById(R.id.hb_children);
        hbCheckinDate = findViewById(R.id.hb_check_in_date);
        hbCheckoutDate = findViewById(R.id.hb_check_out_date);
        hbComments = findViewById(R.id.hb_comments);
        hbSingleBedCheckBox = findViewById(R.id.hb_checkbox_single_bed);
        hbDoubleBedCheckBox = findViewById(R.id.hb_checkbox_double_bed);
        hbBookTransportCheckBox = findViewById(R.id.hb_checkbox_transport);
        hbPickupLocationLinearLayout = findViewById(R.id.hb_select_pickup_location_linear_layout);
        hbPickupLocationImage = findViewById(R.id.add_pickup_location);
        hbTranportTypeSpinner = findViewById(R.id.hb_transport_type_spinner);
        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, transportTypeArray);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        hbTranportTypeSpinner.setAdapter(aa);

        hbPickupLocationLinearLayout.setVisibility(View.GONE);

        hbFirstName.setText(drawer_main.getInstance().user.getFirstName());
        hbLastName.setText(drawer_main.getInstance().user.getLastName());
        hbEmail.setText(drawer_main.getInstance().user.getEmailAddress());
        hbPhone.setText(drawer_main.getInstance().user.getMobileNumber());

        hbCheckinDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDateDialog(hbCheckinDate);
            }
        });

        hbCheckoutDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDateDialog(hbCheckoutDate);
            }
        });

        hbPickupLocationImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PingPlacePicker.IntentBuilder builder = new PingPlacePicker.IntentBuilder();
                builder.setAndroidApiKey("YOUR_ANDROID_API_KEY")
                        .setMapsApiKey("AIzaSyCOt6PpFu8XzUe6Fzg4zENPy3blt_-bqeM");

                // If you want to set a initial location rather then the current device location.
                // NOTE: enable_nearby_search MUST be true.
                // builder.setLatLng(new LatLng(37.4219999, -122.0862462))

                try {
                    Intent placeIntent = builder.build(HotelBooking.this);
                    startActivityForResult(placeIntent, PLACE_PICKER_REQUEST);
                } catch (Exception ex) {
                    // Google Play services is not available...
                }

            }
        });

        hbTranportTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
                transportType = transportTypeArray[position];
                transportTypePrice = transportTypePriceArray[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    DatePickerDialog picker;

    private void selectDateDialog(final EditText et) {
        final Calendar cldr = Calendar.getInstance();
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        int month = cldr.get(Calendar.MONTH);
        int year = cldr.get(Calendar.YEAR);
        // date picker dialog
        picker = new DatePickerDialog(HotelBooking.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        et.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                    }
                }, year, month, day);
        picker.show();
    }

    public void onRoomTypeCheckboxClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch (view.getId()) {
            case R.id.hb_checkbox_single_bed:
                if (checked) {
                    isSingleBed = true;
                    hbSingleBeds.setEnabled(true);
                    hbSingleBeds.setText("");
                } else {
                    isSingleBed = false;
                    hbSingleBeds.setEnabled(false);
                    hbSingleBeds.setText("0");
                }
                break;
            case R.id.hb_checkbox_double_bed:
                if (checked) {
                    isDoubleBed = true;
                    hbDoubleBeds.setEnabled(true);
                    hbDoubleBeds.setText("");
                } else {
                    isDoubleBed = false;
                    hbDoubleBeds.setEnabled(false);
                    hbDoubleBeds.setText("0");
                }
                break;
            case R.id.hb_checkbox_transport:
                if (checked) {
                    mBookTransport = true;
                    hbPickupLocationLinearLayout.setVisibility(View.VISIBLE);
                } else {
                    mBookTransport = false;
                    hbPickupLocationLinearLayout.setVisibility(View.GONE);
                    mPickupLatitude = "";
                    mPickupLongitude = "";
                    hbPickupLocationImage.setImageResource(R.drawable.ic_add_location_gray_24dp);
                }
                break;
        }
    }

    public void BookHotelBtn(View view) {
        progressDialog.setMessage("Booking your hotel...");
        progressDialog.show();

        mhbFirstName = hbFirstName.getText().toString();
        mhbLastName = hbLastName.getText().toString();
        mhbEmail = hbEmail.getText().toString();
        mhbPhone = hbPhone.getText().toString();
        mhbCheckinDate = hbCheckinDate.getText().toString();
        mhbCheckoutDate = hbCheckoutDate.getText().toString();
        mhbComments = hbComments.getText().toString();
        mhbSingleBeds = hbSingleBeds.getText().toString();
        mhbDoubleBeds = hbDoubleBeds.getText().toString();
        mhbAdults = hbAdults.getText().toString();
        mhbChildrens = hbChildrens.getText().toString();

        if (hbBookTransportCheckBox.isChecked()) {
            mBookTransport = true;
        }

        if (mhbFirstName.isEmpty()) {
            hbFirstName.setError("Enter First Name");
            hbFirstName.requestFocus();
            progressDialog.dismiss();
            return;
        }
        if (mhbEmail.isEmpty()) {
            hbEmail.setError("Enter yout Email");
            hbEmail.requestFocus();
            progressDialog.dismiss();
            return;
        }
        if (mhbPhone.isEmpty()) {
            hbPhone.setError("Enter your phone number");
            hbPhone.requestFocus();
            progressDialog.dismiss();
            return;
        }
        if (mhbCheckinDate.isEmpty()) {
            hbCheckinDate.setError("Enter check-in date");
            hbCheckinDate.requestFocus();
            progressDialog.dismiss();
            return;
        }
        if (mhbCheckoutDate.isEmpty()) {
            hbCheckoutDate.setError("Enter check-out date");
            hbCheckoutDate.requestFocus();
            progressDialog.dismiss();
            return;
        }
        if (!hbSingleBedCheckBox.isChecked() && !hbDoubleBedCheckBox.isChecked()) {
            Toast.makeText(this, "You have to enter number for rooms.", Toast.LENGTH_LONG).show();
            hbSingleBedCheckBox.requestFocus();
            progressDialog.dismiss();
            return;
        }
        if (hbSingleBedCheckBox.isChecked() && mhbSingleBeds.isEmpty()) {
            hbSingleBeds.setError("Enter number of rooms");
            hbSingleBeds.requestFocus();
            progressDialog.dismiss();
            return;
        }
        if (hbSingleBeds.isEnabled() && (mhbSingleBeds.isEmpty() || mhbSingleBeds.equalsIgnoreCase("0"))) {
            hbSingleBeds.setError("Room number must not be empty of equal to 0...");
            hbSingleBeds.requestFocus();
            progressDialog.dismiss();
            return;
        }
        if (hbDoubleBeds.isEnabled() && (mhbDoubleBeds.isEmpty() || mhbDoubleBeds.equalsIgnoreCase("0"))) {
            hbDoubleBeds.setError("Room number must not be empty of equal to 0...");
            hbDoubleBeds.requestFocus();
            progressDialog.dismiss();
            return;
        }
        if (hbSingleBedCheckBox.isChecked() && mhbSingleBeds.isEmpty()) {
            hbSingleBeds.setError("Enter number of rooms");
            hbSingleBeds.requestFocus();
            progressDialog.dismiss();
            return;
        }
        if (mhbAdults.isEmpty() || mhbAdults.equalsIgnoreCase("0")) {
            hbAdults.setError("Enter number of adults.");
            hbAdults.requestFocus();
            progressDialog.dismiss();
            return;
        }
        if (mhbChildrens.isEmpty()) {
            hbChildrens.setError("Enter number of children. Enter 0 if you do not have any!");
            hbChildrens.requestFocus();
            progressDialog.dismiss();
            return;
        }
        if (hbBookTransportCheckBox.isChecked() && mPickupLatitude.isEmpty() && mPickupLongitude.isEmpty()) {
            Toast.makeText(this, "Select Pickup Location or Uncheck the Book Transport Checkbox.", Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
            return;
        }
        if (hbBookTransportCheckBox.isChecked() && transportType.isEmpty()) {
            Toast.makeText(this, "Select Pickup Transport Type or Uncheck the Book Transport Checkbox.", Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
            return;
        }

        showPaymentDialog();
//        sendBookingDetailsToDatabase();
    }

    private void showPaymentDialog() {
        final Dialog dialog = new Dialog(HotelBooking.this, android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);
        dialog.setContentView(R.layout.dialog_billing);

        TextView dialogHotelName = dialog.findViewById(R.id.dialog_hotel_name);
        TextView dialogHotelCity = dialog.findViewById(R.id.dialog_hotel_city);
        TextView dialogSingleRoom = dialog.findViewById(R.id.dialog_single_room_price);
        TextView dialogDoubleRoom = dialog.findViewById(R.id.dialog_double_room_price);
        TextView dialogNoOfDays = dialog.findViewById(R.id.dialog_no_of_days);
        TextView dialogNetPrice = dialog.findViewById(R.id.dialog_net_price);
        TextView dialogTransport = dialog.findViewById(R.id.dialog_transport);
        TextView dialogTransportType = dialog.findViewById(R.id.dialog_transport_type);
        TextView dialogDistance = dialog.findViewById(R.id.dialog_distance);
        TextView dialogTransportCharges = dialog.findViewById(R.id.dialog_transport_price);
        TextView dialogTotalPrice = dialog.findViewById(R.id.dialog_total_price);

        dialogHotelName.setText(dialogHotelName.getText().toString() + HotelName);
        dialogHotelCity.setText(dialogHotelCity.getText().toString() + City);
        dialogSingleRoom.setText(dialogSingleRoom.getText().toString() + mhbSingleBeds + " x Rs " + SingleBedPrice);
        dialogDoubleRoom.setText(dialogDoubleRoom.getText().toString() + mhbDoubleBeds + " x Rs " + DoubleBedPrice);
        dialogNoOfDays.setText(dialogNoOfDays.getText().toString() + getNoOfDays());
        dialogNetPrice.setText(dialogNetPrice.getText().toString() + getNetPrice());

        dialogDistance.setVisibility(View.GONE);

        if (!mBookTransport) {
            dialogTransport.setText(dialogTransport.getText().toString() + "No Transport!");
            dialogTransportCharges.setVisibility(View.GONE);
            dialogDistance.setVisibility(View.GONE);
        } else {
            dialogTransport.setText(dialogTransport.getText().toString() + "Yes.");
            dialogTransportType.setText(dialogTransportType.getText().toString() + transportType);
            String price = calculateTransportPrice();
            dialogTransportCharges.setText(dialogTransportCharges.getText().toString() + price);
            if (calDistance > 0.0) {
                dialogDistance.setText(dialogDistance.getText().toString() + calDistance);
            } else {
                dialogDistance.setText(dialogDistance.getText().toString() + "0.0");
            }
        }

        dialogTotalPrice.setText(dialogTotalPrice.getText().toString() + getTotalPrice());

        Button dialogCancel = dialog.findViewById(R.id.dialog_cancel);
        Button dialogPayAndSubmit = dialog.findViewById(R.id.dialog_pay_and_submit);

        dialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.dismiss();
                dialog.dismiss();
            }
        });

        dialogPayAndSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                sendBookingDetailsToDatabase();
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

    private String getNoOfDays() {
        String days;
        try {
            Date date1 = new SimpleDateFormat("dd/MM/yyyy").parse(mhbCheckinDate);
            Date date2 = new SimpleDateFormat("dd/MM/yyyy").parse(mhbCheckoutDate);
            long diff = date2.getTime() - date1.getTime();
            noOfDays = ((diff / 1000 / 60 / 60 / 24) + 1);
            days = Float.toString(noOfDays);
        } catch (ParseException e) {
            days = "Unable to calculate days.";
        }

        return days;
    }

    private String getNetPrice() {
        String price;
        float priceSingle = Float.valueOf(mhbSingleBeds) * Float.valueOf(SingleBedPrice);
        float priceDouble = Float.valueOf(mhbDoubleBeds) * Float.valueOf(DoubleBedPrice);
        netPrice = (noOfDays * (priceSingle + priceDouble));
        price = "Rs " + netPrice;
        return price;
    }

    private String calculateTransportPrice() {
        String price;
        LatLng source = new LatLng(Double.valueOf(mPickupLatitude), Double.valueOf(mPickupLongitude));
        LatLng dest = new LatLng(Double.valueOf(hotelLatitude), Double.valueOf(hotelLongitude));

        float[] distance = new float[1];
        try {
            Location.distanceBetween(source.latitude, source.longitude, dest.latitude, dest.longitude, distance);
        } catch (Exception e) {

        }

        calDistance = distance[0] / 1000;

        transportPrice = Float.valueOf(Double.toString(((calDistance * transportTypePrice) + 500)));

        price = "Rs " + transportPrice;

        return price;
    }

    private String getTotalPrice() {
        totalPrice = netPrice + transportPrice;
        String price = "Rs " + totalPrice;
        return price;
    }

    private void sendBookingDetailsToDatabase() {
        final String key = reference.child("booking-details").push().getKey();
        if (mBookTransport) {
            reference.child("booking-details").child(key)
                    .setValue(new HotelBookingModel(
                            FirebaseAuth.getInstance().getUid(),
                            null,
                            HotelId,
                            HotelName,
                            City,
                            mhbFirstName,
                            mhbLastName,
                            mhbEmail,
                            mhbPhone,
                            mhbCheckinDate,
                            mhbCheckoutDate,
                            mhbComments,
                            mhbSingleBeds,
                            mhbDoubleBeds,
                            mhbAdults,
                            mhbChildrens,
                            mBookTransport,
                            System.currentTimeMillis(),
                            "new",
                            Double.toString(netPrice),
                            Double.toString(totalPrice),
                            mPickupLatitude,
                            mPickupLongitude,
                            transportType))
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(HotelBooking.this, "Hotel Booked Successfully", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            String HOTEL_BOOKING_MESSAGE = "Hey, I have booked a room in hotel: " + HotelName + " located in City: " + City + ". Kindly check booking requests";
                            sendMessage(FirebaseAuth.getInstance().getUid(), ADMIN_USER_ID, HOTEL_BOOKING_MESSAGE);
                            startActivity(new Intent(HotelBooking.this, drawer_main.class));
                        }
                    });
        } else {
            reference.child("booking-details").child(key)
                    .setValue(new HotelBookingModel(
                            FirebaseAuth.getInstance().getUid(),
                            null,
                            HotelId,
                            HotelName,
                            City,
                            mhbFirstName,
                            mhbLastName,
                            mhbEmail,
                            mhbPhone,
                            mhbCheckinDate,
                            mhbCheckoutDate,
                            mhbComments,
                            mhbSingleBeds,
                            mhbDoubleBeds,
                            mhbAdults,
                            mhbChildrens,
                            mBookTransport,
                            System.currentTimeMillis(),
                            "new",
                            Double.toString(netPrice),
                            Double.toString(totalPrice),
                            null,
                            null,
                            null))
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(HotelBooking.this, "Hotel Booked Successfully", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            String HOTEL_BOOKING_MESSAGE = "Hey, I have booked a room in hotel: " + HotelName + " located in City: " + City + ". Kindly check booking requests";
                            sendMessage(FirebaseAuth.getInstance().getUid(), ADMIN_USER_ID, HOTEL_BOOKING_MESSAGE);
                            startActivity(new Intent(HotelBooking.this, drawer_main.class));
                        }
                    });
        }
    }

    private void sendMessage(String sender, final String receiver, String message) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);

        reference.child("Chats").push().setValue(hashMap);

        final String msg = message;
        reference = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                sendNotification(receiver, user.getFirstName() + " " + user.getLastName(), msg);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendNotification(String receiver, final String username, final String msg) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(firebaseUser.getUid(), R.mipmap.ic_launcher, username + ": " + msg, "New Message", ADMIN_USER_ID);

                    Sender sender = new Sender(data, token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200) {
                                        if (response.body().success != 1) {
                                            Toast.makeText(HotelBooking.this, "Failed", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == PLACE_PICKER_REQUEST) && (resultCode == RESULT_OK)) {
            Place place = PingPlacePicker.getPlace(data);
            if (place != null) {
                mPickupLatitude = String.valueOf(place.getLatLng().latitude);
                mPickupLongitude = String.valueOf(place.getLatLng().longitude);

                hbPickupLocationImage.setImageResource(R.drawable.ic_location_on_green_24dp);

            }
        }
    }
}
