package com.example.medica;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserHostForPharmacy extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DatabaseReference myRef;
    private ArrayList<ImageInfo> cardList;
    private PharmacyRecyclerAdapter recyclerAdapter;
    private Button insertMedicine;


    /**
     * permission code
     */
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;

    /**
     * requestPermissions and do something
     *
     */
    public void requestRead() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        } else {
            readFile();
        }
    }

    /**
     * do you want to do
     */
    public void readFile() {
        // do something
        Intent intent = new Intent(mContextNotInScope,PublishMedicine.class);
        startActivity(intent);

    }

    /**
     * onRequestPermissionsResult
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                readFile();
            } else {
                // Permission Denied
                Toast.makeText(UserHostForPharmacy.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }







    private void printEmail(FirebaseUser account){
        for (UserInfo userInfo : account.getProviderData()) {
            Log.i(userInfo.getUid(),userInfo.getProviderId()+" "+
                    userInfo.getEmail()+" "+userInfo.isEmailVerified() );
            Toast.makeText(mContextInScope, userInfo.getEmail(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_host_for_pharmacy);
        Button Log_out=findViewById(R.id.Log_out);

     /*   FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                printEmail(user);
            }*/


        Log_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
        insertMedicine=findViewById(R.id.publishMedicine);
        insertMedicine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readFile();
            }
        });


        recyclerView = findViewById(R.id.recyclerViewAds);
        LinearLayoutManager layoutManager= new LinearLayoutManager(mContextNotInScope);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        //Firebase
        myRef= FirebaseDatabase.getInstance().getReference();

        //ArrayList
        cardList = new ArrayList<>();

        //Clear ArrayList
        ClearAll();
        //GetMethod
        GetDataFromFirebase();
    }

    private void GetDataFromFirebase() {
        Query query = myRef.child("PharmacyUsers");
        Query queryAds = myRef.child("Ads");
        ArrayList<String> fromAds=new ArrayList<>();

        queryAds.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){

                    for (DataSnapshot dataSnap : dataSnapshot.getChildren()) {
                        fromAds.add(dataSnap.getKey().toString());

                        for (int i = 0; i < fromAds.size(); i++) {
                            Log.d("fromAds", fromAds.get(i));
                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });











        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ClearAll();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){




                    ImageInfo imageInfo = new ImageInfo();
                    imageInfo.setImageUrl(snapshot.child("downloadImageUrl").getValue().toString());
                    imageInfo.setPharmacyName(snapshot.child("pharmacyName").getValue().toString());
                    imageInfo.setLocationDescription(snapshot.child("Description").getValue().toString());
                    imageInfo.setLocationUrl(snapshot.child("LocationUrl").getValue().toString());
                    imageInfo.setWhatsapp(snapshot.child("Whatsapp").getValue().toString());
                    imageInfo.setPhone(snapshot.child("Phone").getValue().toString());
                    imageInfo.setLat(Double.parseDouble( snapshot.child("LocationLat").getValue().toString()));
                    imageInfo.setLongt(Double.parseDouble(snapshot.child("LocationLong").getValue().toString()));
                    imageInfo.setDelivary((Boolean) snapshot.child("Delivary").getValue());






                    for (DataSnapshot replyIDs : snapshot.child("replyIDs").getChildren()) {

                        String fromPharmacy=replyIDs.getValue().toString();
                        Log.d("fromPharmacy",fromPharmacy);
                        for(int i =0 ;i < fromAds.size();i++){
                            if(fromAds.get(i).equals(fromPharmacy)){
                                cardList.add(imageInfo);
                            }
                        }

                    }


                }
                recyclerAdapter= new PharmacyRecyclerAdapter(
                        mContextNotInScope.getApplicationContext()
                        , cardList
                        , new OnItemClickListener() {
                    @Override
                    public void OnItemClick(ImageView imageView) {


                    }

                    @Override
                    public void OnItemClick(double lat,double longt,String pharmacyName) {
                        Intent intent = new Intent(mContextNotInScope,PharmacyMap.class);
                        intent.putExtra("lat",lat);
                        intent.putExtra("longt",longt);
                        intent.putExtra("pharmacyName",pharmacyName);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void OnPhoneClick(String phone) {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:"+phone));
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void OnWhatsClick(String whats) {
                        String url = "https://api.whatsapp.com/send?phone="+whats;
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                        finish();
                    }
                });
                recyclerView.setAdapter(recyclerAdapter);
                recyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void ClearAll(){
        if(cardList !=null){
            cardList.clear();

            if(recyclerAdapter!=null){
                recyclerAdapter.notifyDataSetChanged();
            }
        }
        cardList = new ArrayList<>();
    }

    Context mContextInScope =getBaseContext();
    Context mContextNotInScope = this;
    private void signOut(){
        FirebaseAuth.getInstance().signOut();
        //LoginManager.getInstance().logOut();
        Intent intent = new Intent(mContextNotInScope,UserLogin.class);
        startActivity(intent);
    }
}