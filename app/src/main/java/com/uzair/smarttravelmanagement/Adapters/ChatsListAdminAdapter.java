package com.uzair.smarttravelmanagement.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.uzair.smarttravelmanagement.MessageActivity;
import com.uzair.smarttravelmanagement.Models.User;
import com.uzair.smarttravelmanagement.R;

import java.util.List;

public class ChatsListAdminAdapter extends RecyclerView.Adapter<ChatsListAdminAdapter.ViewHolder> {

    private Context mContext;
    private List<User> mUser;

    public ChatsListAdminAdapter(Context mContext, List<User> mUser) {
        this.mContext = mContext;
        this.mUser = mUser;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.chats_list_admin_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.Name.setText(mUser.get(position).getFirstName() + " " + mUser.get(position).getLastName());
        holder.Contact.setText(mUser.get(position).getMobileNumber() + " - " + mUser.get(position).getEmailAddress());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, MessageActivity.class);
                i.putExtra("adminId", mUser.get(position).getUserId());
                mContext.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUser.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView Name;
        public TextView Contact;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            Name = itemView.findViewById(R.id.cla_name);
            Contact = itemView.findViewById(R.id.cla_contact);
        }
    }
}
