package com.example.medica;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.medica.Prevalent.Prevalent;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import io.paperdb.Paper;

public class HostForAds extends AppCompatActivity {


    private RecyclerView recyclerView;
    private DatabaseReference myRef;
    private ArrayList<ImageInfoAdvartise> cardListAds;
    double pharmacyLat;
    double pharmacyLong;
    double UserLat;
    double UserLong;
    double Distance;
    AdvartiseRecyclerAdapter  advartiseRecyclerAdapter;
    String pharmacyEmail;
    double maxDistance= 2000;
    double xPharmacyLat;
    double yPharmacyLong;
    double xUserLat=0;
    double yUserLong=0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.host_of_ads);

        Button Log_out=findViewById(R.id.Log_out_Pharmacy);



        Log_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });


        recyclerView =(RecyclerView) findViewById(R.id.recyclerViewAds);
        LinearLayoutManager layoutManager= new LinearLayoutManager(mContextNotInScope);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        //Firebase
        myRef= FirebaseDatabase.getInstance().getReference();

        //ArrayList
        cardListAds = new ArrayList<>();

        //Clear ArrayList
        ClearAll();
        //GetMethod
        GetDataFromFirebase();
    }
    private double XConvert(double lat,double longt){

        return 6371 * Math.cos(lat) * Math.cos(longt);
    }
    private double YConvert(double lat,double longt){

        return 6371 * Math.cos(lat) * Math.sin(longt);
    }
    private double Distance(double x , double y){

        return  Math.sqrt(Math.pow(x,2)+Math.pow(y,2));
    }


    private void GetDataFromFirebase() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            pharmacyEmail = extras.getString("encodeUserEmail");
        }

        Query queryAccept=myRef.child("PharmacyUsers");
        Query query=myRef.child("Ads");

        queryAccept.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                pharmacyLat=Double.parseDouble(snapshot
                        .child(pharmacyEmail).child("LocationLat").getValue().toString());
                pharmacyLong=Double.parseDouble(snapshot
                        .child(pharmacyEmail).child("LocationLong").getValue().toString());
                xPharmacyLat=XConvert(pharmacyLat,pharmacyLong);
                yPharmacyLong=YConvert(pharmacyLat,pharmacyLong);


                Log.d("lat",String.valueOf(pharmacyLat));
                Log.d("lat",String.valueOf(pharmacyLong));
                Log.d("lat",String.valueOf(xPharmacyLat));
                Log.d("lat",String.valueOf(yPharmacyLong));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ClearAll();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    for(DataSnapshot queryUser : dataSnapshot.getChildren()){


                        ImageInfoAdvartise imageInfoAdvartise = new ImageInfoAdvartise();
                        imageInfoAdvartise.setImageUrl(queryUser.child("AdsdownloadImageUrl").getValue().toString());
                        imageInfoAdvartise.setTitleText(queryUser.child("TitleText").getValue().toString());
                        imageInfoAdvartise.setLocationDescription(queryUser.child("Description").getValue().toString());
                        imageInfoAdvartise.setEmail_Or_Phone_Id(queryUser.getKey());

                        Log.d("User",String.valueOf(imageInfoAdvartise.getEmail_Or_Phone_Id()));

                        UserLat= Double.parseDouble( queryUser.child("LocationLat").getValue().toString());
                        UserLong = Double.parseDouble(queryUser.child("LocationLong").getValue().toString());

                        xUserLat=XConvert(UserLat,UserLong);
                        yUserLong=YConvert(UserLat,UserLong);
                        double x=xUserLat-xPharmacyLat;
                        double y=yUserLong-yPharmacyLong;
                        Distance = Distance(x,y);
                        if(Distance < maxDistance ) {
                            cardListAds.add(imageInfoAdvartise);

                        }



                    }
                }
                Log.d("UserUser",String.valueOf(UserLat));
                Log.d("User",String.valueOf(UserLong));
                Log.d("User",String.valueOf(xUserLat));
                Log.d("User",String.valueOf(yUserLong));
                Log.d("Distance",String.valueOf(Distance));






                advartiseRecyclerAdapter= new AdvartiseRecyclerAdapter(
                        HostForAds.this
                        , cardListAds
                        , new OnItemClickListenerAds() {
                    @Override
                    public void OnAdsImageClick(Uri uri) {
                        Intent editIntent = new Intent(Intent.ACTION_EDIT);
                        editIntent.setDataAndType(uri, "image/*");
                        editIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivity(Intent.createChooser(editIntent, null));
                    }

                    @Override
                    public void OnCheckBoxClick(Boolean checked, String requestID) {
                        pharmacyReply(requestID,pharmacyEmail);
                    }








                });

                recyclerView.setAdapter(advartiseRecyclerAdapter);
                advartiseRecyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });



    }
    private void ClearAll(){
        if(cardListAds !=null){
            cardListAds.clear();

            if(advartiseRecyclerAdapter!=null){
                advartiseRecyclerAdapter.notifyDataSetChanged();
            }
        }
        cardListAds = new ArrayList<>();
    }


    Context mContextNotInScope = this;
    private void signOut(){
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
        Paper.init(this);

        Paper.book().write(Prevalent.UserPhoneKey,"");
        Paper.book().write(Prevalent.UserPasswordKey,"");

        Intent intent = new Intent(mContextNotInScope,PharmacyLogin.class);
        startActivity(intent);
    }



    public void pharmacyReply(String replyID , String pharmacyEmail){

        final DatabaseReference RootRef=FirebaseDatabase.getInstance().getReference();
        //todo============================ Upload to firebase==================================
        //
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                HashMap<String, Object> pharmacyDataMap = new HashMap<>();
                pharmacyDataMap.put("replyID",replyID);

                //todo============================ HashMap==================================
                RootRef.child("PharmacyUsers").child(pharmacyEmail).child("replyIDs").updateChildren(pharmacyDataMap)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if(task.isSuccessful()){

                                    Toast.makeText(getApplicationContext(),"Congratulations, your pharmacy has been added.",Toast.LENGTH_SHORT).show();

                                    Log.d("exist","Congratulations, your pharmacy has been added");
                                }else{

                                    Toast.makeText(getApplicationContext(), "Network Error:Please try again", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }) ;
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //todo============================ Upload to firebase==================================


    }


    public void pharmacyReplyRemove(String replyID , String pharmacyEmail){

        final DatabaseReference RootRef=FirebaseDatabase.getInstance().getReference();
        //todo============================ Upload to firebase==================================
        //
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {



                //todo============================ HashMap==================================
                RootRef.child("PharmacyUsers").child(pharmacyEmail).child("replyIDs").child(replyID).removeValue()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if(task.isSuccessful()){

                                    Toast.makeText(getApplicationContext(),"Congratulations, your pharmacy has been added.",Toast.LENGTH_SHORT).show();

                                    Log.d("exist","Congratulations, your pharmacy has been added");
                                }else{

                                    Toast.makeText(getApplicationContext(), "Network Error:Please try again", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }) ;
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //todo============================ Upload to firebase==================================


    }



}