package com.uzair.smarttravelmanagement.Adapters;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.uzair.smarttravelmanagement.Models.HotelBookingModel;
import com.uzair.smarttravelmanagement.R;
import com.uzair.smarttravelmanagement.SingleBookingRequest;

import java.util.ArrayList;
import java.util.Calendar;

public class BookingRequestsAdapter extends RecyclerView.Adapter<BookingRequestsAdapter.MyViewHolder> {

    Context context;
    ArrayList<HotelBookingModel> bookings;

    public BookingRequestsAdapter(Context context, ArrayList<HotelBookingModel> bookings) {
        this.context = context;
        this.bookings = bookings;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder((LayoutInflater.from(context).inflate(R.layout.booking_requests_row, parent, false)));
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        holder.HotelName.setText(bookings.get(position).getHotelName());
        holder.HotelCity.setText(bookings.get(position).getHotelCity());
        holder.RoomType.setText("Single Beds: " + bookings.get(position).getSingleBeds() + ", Double Beds: " + bookings.get(position).getDoubleBeds());
        holder.CheckinDate.setText(bookings.get(position).getCheckinDate());
        holder.CheckoutDate.setText(bookings.get(position).getCheckoutDate());

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(bookings.get(position).getTimestamp());
        String date = DateFormat.format("dd-MM-yyyy", calendar).toString();
        String time = DateFormat.format("hh:mm a", calendar).toString();

        holder.RequestTime.setText(date + " - " + time);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, SingleBookingRequest.class);
                i.putExtra("bookingId", bookings.get(position).getBookingId());
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView HotelName, HotelCity, RoomType, CheckinDate, CheckoutDate, RequestTime;

        public MyViewHolder(View itemView) {
            super(itemView);
            HotelName = itemView.findViewById(R.id.row_booking_requests_hotel_name);
            HotelCity = itemView.findViewById(R.id.row_booking_requests_city_name);
            RoomType = itemView.findViewById(R.id.row_booking_requests_room_type);
            CheckinDate = itemView.findViewById(R.id.row_booking_requests_check_in_date);
            CheckoutDate = itemView.findViewById(R.id.row_booking_requests_check_out_date);
            RequestTime = itemView.findViewById(R.id.row_booking_requests_request_time);
        }
    }
}
