package com.uzair.smarttravelmanagement.Adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.uzair.smarttravelmanagement.Models.FeedbackMessage;
import com.uzair.smarttravelmanagement.R;

import java.util.ArrayList;
import java.util.Calendar;

public class FeedbackMessageAdapter extends RecyclerView.Adapter<FeedbackMessageAdapter.MyViewHolder> {

    Context context;
    ArrayList<FeedbackMessage> messages;

    public FeedbackMessageAdapter(Context context, ArrayList<FeedbackMessage> messages) {
        this.context = context;
        this.messages = messages;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder((LayoutInflater.from(context).inflate(R.layout.admin_feedback_row, parent, false)));
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        holder.Fullname.setText(messages.get(position).getName());
        holder.MobileNumber.setText(messages.get(position).getMobile());
        holder.Message.setText("Message: " + messages.get(position).getMessage());

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(messages.get(position).getTimestamp()));
        String date = DateFormat.format("dd-MM-yyyy", calendar).toString();
        String time = DateFormat.format("hh:mm a", calendar).toString();

        holder.Time.setText(date + " - " + time);

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView Fullname, MobileNumber, Message, Time;

        public MyViewHolder(View itemView) {
            super(itemView);
            Fullname = itemView.findViewById(R.id.row_fullname);
            MobileNumber = itemView.findViewById(R.id.row_mobilenumber);
            Message = itemView.findViewById(R.id.row_feedbackmessage);
            Time = itemView.findViewById(R.id.row_feedbacktime);
        }
    }
}
