package com.uzair.smarttravelmanagement.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ceylonlabs.imageviewpopup.ImagePopup;
import com.uzair.smarttravelmanagement.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageUploadListAdatper extends RecyclerView.Adapter<ImageUploadListAdatper.ViewHolder> {

    Context context;
    public List<Uri> imagePathLis;

    public ImageUploadListAdatper(Context context, List<Uri> imagePathLis) {
        this.context = context;
        this.imagePathLis = imagePathLis;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.upload_image_row, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Uri filePath = imagePathLis.get(position);

        Picasso.get().load(filePath).into(holder.fileUpload);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /** Initiate Popup view **/
                final ImagePopup imagePopup = new ImagePopup(context);
                imagePopup.setWindowHeight(800); // Optional
                imagePopup.setWindowWidth(800); // Optional
                imagePopup.setBackgroundColor(Color.BLACK);  // Optional
                imagePopup.setFullScreen(true); // Optional
                imagePopup.setHideCloseIcon(true);  // Optional
                imagePopup.setImageOnClickClose(true);  // Optional

                imagePopup.initiatePopup(holder.fileUpload.getDrawable());
                imagePopup.viewPopup();

            }
        });

    }

    @Override
    public int getItemCount() {
        return imagePathLis.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public ImageView fileUpload;
        public ImageView mainImage;

        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            fileUpload = (ImageView) mView.findViewById(R.id.uploaded_image);


        }

    }
}
