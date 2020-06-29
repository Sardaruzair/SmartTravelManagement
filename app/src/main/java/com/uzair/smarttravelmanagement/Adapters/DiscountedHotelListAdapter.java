package com.uzair.smarttravelmanagement.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.uzair.smarttravelmanagement.Models.Hotel;
import com.uzair.smarttravelmanagement.R;
import com.uzair.smarttravelmanagement.SingleHotelDetail;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class DiscountedHotelListAdapter extends RecyclerView.Adapter<DiscountedHotelListAdapter.MyViewHolder> {

    Context context;
    ArrayList<Hotel> hotels;

    public DiscountedHotelListAdapter(Context c, ArrayList<Hotel> h) {
        context = c;
        hotels = h;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder((LayoutInflater.from(context).inflate(R.layout.discounted_hotel_list_row, parent, false)));
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int i) {
        holder.hotelName.setText(hotels.get(i).getHotelName());
        holder.hotelHeading.setText("Promotion # " + (i + 1));
        holder.cityName.setText(hotels.get(i).getCity());

        if (hotels.get(i).getSingleBed() && hotels.get(i).getDoubleBed()) {
            holder.roomType.setText("Single Bed, Double Bed");
            holder.price.setText("Rs." + hotels.get(i).getSingleBedPrice() + " - " + "Rs. " + hotels.get(i).getDoubleBedPrice());
        } else if (hotels.get(i).getSingleBed() && !hotels.get(i).getDoubleBed()) {
            holder.roomType.setText("Single Bed");
            holder.price.setText("Rs." + hotels.get(i).getSingleBedPrice());
        } else if (!hotels.get(i).getSingleBed() && hotels.get(i).getDoubleBed()) {
            holder.roomType.setText("Double Bed");
            holder.price.setText("Rs. " + hotels.get(i).getDoubleBedPrice());
        } else if (!hotels.get(i).getSingleBed() && !hotels.get(i).getDoubleBed()) {
            holder.roomType.setText("No Bed Available");
            holder.price.setText(" ---- ");
        }

        if (hotels.get(i).getDiscountAvailable()) {
            holder.roomType.setText("Single Bed, Double Bed");
            holder.price.setPaintFlags(holder.price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.discountPrice.setText("Rs." + hotels.get(i).getDiscountedSingleBedPrice() + " - " + "Rs. " + hotels.get(i).getDiscountedDoubleBedPrice());
        }

        holder.roomType.setVisibility(View.GONE);

        if (hotels.get(i).getImageURL() != null) {
            Picasso.get().load(hotels.get(i).getImageURL().get(0)).into(holder.hotelImage);
        }

        final HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("HotelId", hotels.get(i).getHotelId());
        hashMap.put("HotelName", hotels.get(i).getHotelName());
        hashMap.put("SingleBedPrice", hotels.get(i).getSingleBedPrice());
        hashMap.put("DoubleBedPrice", hotels.get(i).getDoubleBedPrice());
        hashMap.put("DiscountSingleBedPrice", hotels.get(i).getDiscountedSingleBedPrice());
        hashMap.put("DiscountDoubleBedPrice", hotels.get(i).getDiscountedDoubleBedPrice());
        hashMap.put("HotelDetails", hotels.get(i).getHotelDetails());
        hashMap.put("City", hotels.get(i).getCity());
        hashMap.put("ImageURL", hotels.get(i).getImageURL());
        hashMap.put("OwnerName", hotels.get(i).getOwnerName());
        hashMap.put("OwnerPhone", hotels.get(i).getOwnerPhone());
        hashMap.put("OwnerCnic", hotels.get(i).getOwnerCnic());
        hashMap.put("FoodAvailable", hotels.get(i).getFoodAvailable());
        hashMap.put("isSingleBed", hotels.get(i).getSingleBed());
        hashMap.put("isDoubleBed", hotels.get(i).getDoubleBed());
        hashMap.put("isDiscountAvailable", hotels.get(i).getDiscountAvailable());
        hashMap.put("hotelLatitude", hotels.get(i).getHotelLatitude());
        hashMap.put("hotelLongitude", hotels.get(i).getHotelLongitude());
        hashMap.put("isRoomsAvailable", hotels.get(i).getRoomsAvailable());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, SingleHotelDetail.class);
                i.putExtra("data", hashMap);
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return hotels.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView hotelHeading, hotelName, cityName, roomType, price, discountPrice;
        ImageView hotelImage;
        RatingBar ratingBar;
        CardView hotelDetailCard;

        public MyViewHolder(View itemView) {
            super(itemView);
            hotelHeading = itemView.findViewById(R.id.row_hotel_heading);
            hotelName = itemView.findViewById(R.id.row_hotel_name);
            cityName = itemView.findViewById(R.id.row_city_name);
            roomType = itemView.findViewById(R.id.row_room_type);
            price = itemView.findViewById(R.id.row_room_price);
            discountPrice = itemView.findViewById(R.id.row_discount_room_price);

            hotelImage = itemView.findViewById(R.id.row_hotel_image);

            ratingBar = itemView.findViewById(R.id.row_rating_bar);

            hotelDetailCard = itemView.findViewById(R.id.row_hotels_card_view);
        }
    }
}
