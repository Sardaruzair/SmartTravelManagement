package com.uzair.smarttravelmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.uzair.smarttravelmanagement.Interfaces.APIService;
import com.uzair.smarttravelmanagement.Models.Hotel;
import com.uzair.smarttravelmanagement.Models.HotelBookingModel;
import com.uzair.smarttravelmanagement.Models.User;
import com.uzair.smarttravelmanagement.Notifications.Client;
import com.uzair.smarttravelmanagement.Notifications.Data;
import com.uzair.smarttravelmanagement.Notifications.MyResponse;
import com.uzair.smarttravelmanagement.Notifications.Sender;
import com.uzair.smarttravelmanagement.Notifications.Token;

import java.util.Calendar;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SingleBookingRequest extends AppCompatActivity {

    private DatabaseReference reference;
    private HotelBookingModel booking;
    private Hotel hotel;
    private String getBookingId = null;

    private TextView PersonName, PersonPhone, PersonEmail, CheckInOutDate, NoOfPersons, RoomType,
            BookingDate, Transport, Comments, HotelName, PricePerBed, HotelDetails, RoomDetails,
            FoodAvailable, RoomsAvailable, OwnerName, OwnerCnic, TransportPrice, TransportType, TotalPrice;

    private APIService apiService;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_booking_request);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("New Request");

        if (drawer_main.getInstance().user.getUserType().equalsIgnoreCase("user")) {
            findViewById(R.id.confirmBookingFAB).setVisibility(View.GONE);
            findViewById(R.id.declineBookingFAB).setVisibility(View.GONE);
        }


        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        Intent i = getIntent();

        reference = FirebaseDatabase.getInstance().getReference();
        getBookingId = i.getStringExtra("bookingId");
        getBookingRequest(getBookingId);
    }

    private void getBookingRequest(final String bookingId) {
        reference.child("booking-details").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    if (dataSnapshot1.getKey().equals(bookingId)) {
                        booking = dataSnapshot1.getValue(HotelBookingModel.class);
                        if (booking.getBookingStatus().equals("confirmed") ||
                                booking.getBookingStatus().equals("declined")) {
                            findViewById(R.id.declineBookingFAB).setVisibility(View.GONE);
                            findViewById(R.id.confirmBookingFAB).setVisibility(View.GONE);
                        }
                        getHotelDetails(booking.getHotelId());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(SingleBookingRequest.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getHotelDetails(final String hotelId) {
        reference.child("hotel-details").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    if (dataSnapshot1.getKey().equals(hotelId)) {
                        hotel = dataSnapshot1.getValue(Hotel.class);
                        init();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(SingleBookingRequest.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void init() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        PersonName = findViewById(R.id.single_person_name);
        PersonPhone = findViewById(R.id.single_person_phone);
        PersonEmail = findViewById(R.id.single_person_email);
        CheckInOutDate = findViewById(R.id.single_check_in_out_date);
        NoOfPersons = findViewById(R.id.single_no_of_people);
        RoomType = findViewById(R.id.single_room_type);
        BookingDate = findViewById(R.id.single_timestamp);
        TransportPrice = findViewById(R.id.single_transport_price);
        TransportType = findViewById(R.id.single_transport_type);
        TotalPrice = findViewById(R.id.total_price);
        Transport = findViewById(R.id.single_transport);
        Comments = findViewById(R.id.single_comments);

        PersonName.setText(booking.getFirstName() + " " + booking.getLastName());
        PersonPhone.setText(booking.getPhone());
        PersonEmail.setText(booking.getEmail());
        CheckInOutDate.setText("Check-in: " + booking.getCheckinDate() + " - " + "Check-out: " + booking.getCheckoutDate());
        NoOfPersons.setText("Adult(s): " + booking.getAdults() + ", " + "Children: " + booking.getChildrens());
        RoomType.setText("Single Bed Room(s): " + booking.getSingleBeds() + ", Double Bed Room(s): " + booking.getDoubleBeds());
        if(booking.getBookTransport()){
            float transportPrice = Float.valueOf(booking.getTotalPrice()) - Float.valueOf(booking.getNetPrice());
            TransportPrice.setText("Transport Price: Rs " + transportPrice);
            TransportType.setText("Transport Type: " + booking.getTransportType());
        } else {
            TransportPrice.setVisibility(View.GONE);
            TransportType.setVisibility(View.GONE);
        }
        TotalPrice.setText("Total Price: Rs " + booking.getTotalPrice());

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(booking.getTimestamp());
        String date = DateFormat.format("dd-MM-yyyy", calendar).toString();
        String time = DateFormat.format("hh:mm a", calendar).toString();
        BookingDate.setText("Booked on: " + date + " - " + time);
        Transport.setText("Need Transport: " + ((booking.getBookTransport()) ? "yes" : "no"));
        Comments.setText("Comments:\n" + booking.getComments());

        HotelName = findViewById(R.id.single_hotel_name);
        PricePerBed = findViewById(R.id.single_hotel_price);
        RoomDetails = findViewById(R.id.single_rooms_details);
        HotelDetails = findViewById(R.id.single_hotel_details);
        FoodAvailable = findViewById(R.id.single_food_available);
        RoomsAvailable = findViewById(R.id.single_rooms_available);
        OwnerName = findViewById(R.id.single_owner_name);
        OwnerCnic = findViewById(R.id.single_owner_cnic);

        HotelName.setText(hotel.getHotelName() + " - " + hotel.getCity());
        if (hotel.getSingleBed() && hotel.getDoubleBed()) {
            RoomDetails.setText("Rooms: Single Bed, Double Bed");
            PricePerBed.setText("Rs." + hotel.getSingleBedPrice() + " - " + "Rs. " + hotel.getDoubleBedPrice());
        } else if (hotel.getSingleBed() && !hotel.getDoubleBed()) {
            RoomDetails.setText("Rooms: Single Bed");
            PricePerBed.setText("Rs." + hotel.getSingleBedPrice());
        } else if (!hotel.getSingleBed() && hotel.getDoubleBed()) {
            RoomDetails.setText("Rooms: Double Bed");
            PricePerBed.setText("Rs. " + hotel.getDoubleBedPrice());
        } else if (!hotel.getSingleBed() && !hotel.getDoubleBed()) {
            RoomDetails.setText("No Bed Available");
            PricePerBed.setText(" ---- ");
        }
        HotelDetails.setText("Hotel Details:\n" + hotel.getHotelDetails());
        FoodAvailable.setText("Food Available:\n" + hotel.getFoodAvailable());
        RoomsAvailable.setText("Room Available: " + ((hotel.getRoomsAvailable()) ? "yes" : "no"));
        OwnerName.setText("Owner Name: " + hotel.getOwnerName() + "\nOwner Phone: " + hotel.getOwnerPhone());
        OwnerCnic.setText("Owner Cnic: " + hotel.getOwnerCnic());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void confirmBookingBtn(View view) {
        getSupportActionBar().setTitle("Confirmed Request");
        changeBookingStatus("confirmed");
    }

    public void declineBookingBtn(View view) {
        getSupportActionBar().setTitle("Declined Request");
        changeBookingStatus("declined");
    }

    private void changeBookingStatus(final String status) {
        reference.child("booking-details").child(getBookingId).child("bookingStatus")
                .setValue(status)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(SingleBookingRequest.this, "Action Completed", Toast.LENGTH_LONG).show();
                        String HOTEL_BOOKING_MESSAGE = "";
                        if (status.equals("confirmed")) {
                            HOTEL_BOOKING_MESSAGE = "Your booking request is confirmed.";
                        } else if (status.equals("declined")) {
                            HOTEL_BOOKING_MESSAGE = "Your booking request is declined.";
                        }
                        sendMessage(FirebaseAuth.getInstance().getUid(), booking.getUserId(), HOTEL_BOOKING_MESSAGE);
                        finish();
                    }
                });
    }

    private void sendMessage(String sender, final String receiver, String message) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        reference.child("Chats").push().setValue(hashMap);

        final String msg = message;
        reference = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getUid());
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

    private void sendNotification(final String receiver, final String username, final String msg) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(firebaseUser.getUid(), R.mipmap.ic_launcher, username + ": " + msg, "New Message", receiver);

                    Sender sender = new Sender(data, token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200) {
                                        if (response.body().success != 1) {
                                            Toast.makeText(SingleBookingRequest.this, "Failed", Toast.LENGTH_SHORT).show();
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
}
