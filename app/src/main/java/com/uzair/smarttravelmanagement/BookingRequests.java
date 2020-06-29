package com.uzair.smarttravelmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uzair.smarttravelmanagement.Adapters.BookingRequestsAdapter;
import com.uzair.smarttravelmanagement.Models.HotelBookingModel;

import java.util.ArrayList;

public class BookingRequests extends AppCompatActivity {

    private RecyclerView BookingRequestsRecyclerView;
    private ArrayList<HotelBookingModel> BookingRequestsList;
    private BookingRequestsAdapter bookingRequestsAdapter;
    private TextView NotFound;

    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_requests);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("New Booking Requests");

        NotFound = findViewById(R.id.booking_requests_not_found);
        BookingRequestsRecyclerView = findViewById(R.id.booking_requests_recycler_view);
        BookingRequestsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        BookingRequestsList = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference();
        bookingRequestsAdapter = new BookingRequestsAdapter(this, BookingRequestsList);

        reteriveBookingRequests("new");

    }

    private void reteriveBookingRequests(final String filterString) {
        reference.child("booking-details").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                BookingRequestsList.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    HotelBookingModel booking = dataSnapshot1.getValue(HotelBookingModel.class);
                    if (drawer_main.getInstance().user.getUserType().equals("admin")) {
                        if (booking.getBookingStatus().equalsIgnoreCase(filterString)) {
                            booking.setBookingId(dataSnapshot1.getKey());
                            BookingRequestsList.add(booking);
                        }
                    } else if (drawer_main.getInstance().user.getUserType().equals("user")) {
                        if (booking.getBookingStatus().equalsIgnoreCase(filterString) && booking.getUserId().equalsIgnoreCase(FirebaseAuth.getInstance().getUid())) {
                            booking.setBookingId(dataSnapshot1.getKey());
                            BookingRequestsList.add(booking);
                        }
                    }
                }

                BookingRequestsRecyclerView.setAdapter(bookingRequestsAdapter);
                bookingRequestsAdapter.notifyDataSetChanged();

                if(BookingRequestsList.size() == 0){
                    NotFound.setVisibility(View.VISIBLE);
                } else {
                    NotFound.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BookingRequests.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu_booking_request, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_new:
                getSupportActionBar().setTitle("New Booking Requests");
                reteriveBookingRequests("new");
                break;
            case R.id.action_history_confirmed:
                getSupportActionBar().setTitle("Confirmed Booking Requests");
                reteriveBookingRequests("confirmed");
                break;
            case R.id.action_history_declined:
                getSupportActionBar().setTitle("Declined Booking Requests");
                reteriveBookingRequests("declined");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
