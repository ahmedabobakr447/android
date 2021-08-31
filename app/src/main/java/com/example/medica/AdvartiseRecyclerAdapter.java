package com.example.medica;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
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

public class AdvartiseRecyclerAdapter extends  RecyclerView.Adapter<AdvartiseRecyclerAdapter.ViewHolder> {
    private static final String Tag = "RecyclerView";
    private Context mContext;
    private ArrayList<ImageInfoAdvartise> ImageListAds;
    OnItemClickListenerAds listener;
    private Uri uri;
    private String requestID;



    public AdvartiseRecyclerAdapter(Context mContext, ArrayList<ImageInfoAdvartise> imageInfoArraylist , OnItemClickListenerAds listener) {
        this.mContext = mContext;
        this.ImageListAds = imageInfoArraylist;
        this.listener =listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_advertice,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.TitleMedicine.setText(ImageListAds.get(position).getTitleText());
        holder.Description.setText(ImageListAds.get(position).getLocationDescription());
        Glide.with(mContext)
                .asBitmap()
                .load(ImageListAds.get(position).getImageUrl())
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        holder.imageView.setImageBitmap(resource);
                        holder.bindimage(position,listener,resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });

        holder.bind(position,listener);
   }



    @Override
    public int getItemCount() {
        return  ImageListAds.size();
    }

    public  class ViewHolder extends  RecyclerView.ViewHolder {

        ImageView imageView;
        CheckBox checkBox;
        TextView TitleMedicine;
        TextView Description;

       public ViewHolder(@NonNull View itemView) {
            super(itemView);

           TitleMedicine = itemView.findViewById(R.id.MedicineName);
           Description = itemView.findViewById(R.id.locationDescription);
             imageView = itemView.findViewById(R.id.imageView);
            checkBox=itemView.findViewById(R.id.availability);

        }


        public void bind( int position, final OnItemClickListenerAds listener) {

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                {
                    listener.OnCheckBoxClick(isChecked , ImageListAds.get(position).getEmail_Or_Phone_Id());

                }
            });
            }

        private void bindimage(int position, final OnItemClickListenerAds listener,Bitmap bitmap)
        {
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.OnAdsImageClick(getImageUri(mContext,bitmap));
                }
            });
        }





        }



    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "IMG_" + Calendar.getInstance().getTime(), null);
        return Uri.parse(path);
    }


}
