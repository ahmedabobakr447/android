package com.example.medica;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.rey.material.widget.CheckBox;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;

public class PharmacyRecyclerAdapter extends  RecyclerView.Adapter<PharmacyRecyclerAdapter.ViewHolder> {
    private static final String Tag = "RecyclerView";
    private Context mContext;
    private ArrayList<ImageInfo> ImageList;
    OnItemClickListener listener;
    private String locaitonOnGoogleMaps;
    private Uri uri;
    private double lat;
    private double longt;
    private String PharmacyName;
    private String phone;
    private String whatsphone;


    public PharmacyRecyclerAdapter(Context mContext, ArrayList<ImageInfo> imageInfoArraylist , OnItemClickListener listener) {
        this.mContext = mContext;
        this.ImageList = imageInfoArraylist;
        this.listener =listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_pharmacy,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.bind(position,listener);
        holder.pharmacyName.setText(ImageList.get(position).getPharmacyName());
        holder.locationDescription.setText(ImageList.get(position).getLocationDescription());
        holder.locationUrl.setText("Location on Google Maps");
        holder.phoneNumber.setText(ImageList.get(position).getPhone());
        holder.whatsApp.setText(ImageList.get(position).getWhatsapp());
       if(ImageList.get(position).getDelivary())
        holder.delivary.setChecked(true);

       //ImageView: Glide Library

        Glide.with(mContext)
                .asBitmap()
                .load(ImageList.get(position).getImageUrl())
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        holder.imageView.setImageBitmap(resource);

                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });

    }



    @Override
    public int getItemCount() {
        return  ImageList.size();
    }

    public  class ViewHolder extends  RecyclerView.ViewHolder {

        ImageView imageView;
        TextView pharmacyName;
        TextView locationDescription;
        TextView phoneNumber;
        TextView whatsApp;
        TextView locationUrl;
        CheckBox delivary;

       public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            pharmacyName = itemView.findViewById(R.id.MedicineName);
            locationDescription = itemView.findViewById(R.id.locationDescription);
            phoneNumber = itemView.findViewById(R.id.phoneNumber);
            whatsApp = itemView.findViewById(R.id.phoneOrEmail);
            delivary= itemView.findViewById(R.id.delivary);
            locationUrl= itemView.findViewById(R.id.locationUrl);

        }

        public void bind( int position, final OnItemClickListener listener) {




            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.OnItemClick(imageView);
                }
            });
            locationUrl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.OnItemClick(ImageList.get(position).getLat(),ImageList.get(position).getLongt(),ImageList.get(position).getPharmacyName());
                }
            });
            phoneNumber.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.OnPhoneClick(ImageList.get(position).getPhone());

                }
            });
            whatsApp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.OnWhatsClick(ImageList.get(position).getWhatsapp());
                 }
            });




        }

    }


}
