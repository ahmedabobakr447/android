package com.example.medica;


import android.net.Uri;
import android.widget.ImageView;


public interface OnItemClickListener {
    void OnItemClick(ImageView imageView);
    void OnItemClick(double lat,double longt,String pharmacyName);
    void OnPhoneClick(String phone);
    void OnWhatsClick(String whats);


}
