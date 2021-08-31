package com.example.medica;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.medica.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.widget.CheckBox;

import io.paperdb.Paper;

import static com.example.medica.Prevalent.Prevalent.UserPhoneKey;

public class PharmacyLogin extends AppCompatActivity {


    String encodeUserEmail;
    Button loginButton ;
    TextView signUpText;
    private EditText textViewEmail,textViewPassword;
    private static final String PharmacyUsers = "PharmacyUsers";
    CheckBox checkBox;
    String decryptedPassword=null;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pharmacy_login);


        Paper.init(this);
        //TODO:identifiers
        loginButton =findViewById(R.id.loginButtonPharmcay);
        Button switchButtonToUser =findViewById(R.id.switchButtonToUser);
        signUpText = findViewById(R.id.signUpText);
        textViewPassword = findViewById(R.id.textViewPassword);
        textViewEmail = findViewById(R.id.textViewEmail);


        //TODO: checkBox
        checkBox =findViewById(R.id.delivaryService);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Email = textViewEmail.getText().toString();
                String password = textViewPassword.getText().toString();

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getBaseContext(), "Please Enter your password...", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(Email)) {
                    Toast.makeText(getBaseContext(), "Please Enter your phone number or email...", Toast.LENGTH_SHORT).show();
                } else {

                    AllowAccessToAccount(Email, password);

                }


            }
        });
        signUpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PharmacyLogin.this, PharmacyRegister.class);
                startActivity(intent);
            }
        });

        String UserEmail = Paper.book().read(Prevalent.Email);
        String UserPassword = Paper.book().read(Prevalent.UserPasswordKey);

        if (UserEmail != null && UserPassword != null) {

            if (!TextUtils.isEmpty(UserEmail) && !TextUtils.isEmpty(UserPassword)) {

                AllowAccessWithRemember(UserEmail, UserPassword);

            }

        }


        switchButtonToUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PharmacyLogin.this,UserLogin.class);
                startActivity(intent);
            }
        });

    }



    private void AllowAccessWithRemember(String userEmail, String userPassword) {

        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        String encodeUserEmail= encodeUserEmail(userEmail);



        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.child(PharmacyUsers).child(encodeUserEmail).exists()) {
                    try {
                        decryptedPassword=AESCrypt.decrypt(snapshot.child(PharmacyUsers).child(encodeUserEmail).child("Password").getValue().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Log.d("pharmacyUser",encodeUserEmail);
                    Log.d("pharmacyUser",decryptedPassword);
                    Log.d("pharmacyUser",userPassword);

                    if (decryptedPassword.equals(userPassword)) {

                        Toast.makeText(getBaseContext(), "You have login successfully...", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(getBaseContext(), HostForAds.class);
                        intent.putExtra("encodeUserEmail",encodeUserEmail);
                        startActivity(intent);

                    }else{
                        Toast.makeText(getBaseContext(), "Your password is not correct", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(getBaseContext(), "Account with PharmcayFragmentLogin" + userEmail + "number is not exists.", Toast.LENGTH_SHORT).show();
                    Toast.makeText(getBaseContext(), "you need to create a new account", Toast.LENGTH_SHORT).show();
                }

            }


            @Override
            public void onCancelled (@NonNull DatabaseError error){

            }

        });
    }

    static String encodeUserEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }

    private void AllowAccessToAccount(String Email, String password) {
        Paper.init(PharmacyLogin.this);


        if(checkBox.isChecked()){
            Paper.book().write(UserPhoneKey,Email);
            Paper.book().write(Prevalent.UserPasswordKey,password);
        }

        encodeUserEmail= encodeUserEmail(Email);


        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                if (snapshot.child(PharmacyUsers).child(encodeUserEmail).exists()) {


                    try {
                        decryptedPassword=AESCrypt.decrypt(snapshot.child(PharmacyUsers).child(encodeUserEmail).child("Password").getValue().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Log.d("phone",Email);

                    Log.d("password",password);
                    Log.d("decryptedPassword",decryptedPassword);
                    Log.d("getphone",encodeUserEmail);
                    if (decryptedPassword.equals(password)) {
                        Toast.makeText(getBaseContext(), "You have login successfully...", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(PharmacyLogin.this, HostForAds.class);
                        intent.putExtra("encodeUserEmail",encodeUserEmail);
                        startActivity(intent);

                    }else{
                        Toast.makeText(getBaseContext(), "Your password is not correct", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(getBaseContext(), "Account with PharmcayFragmentLogin  " + Email + "  number is not exists.", Toast.LENGTH_SHORT).show();
                    Toast.makeText(getBaseContext(), "you need to create a new account", Toast.LENGTH_SHORT).show();
                }

            }


            @Override
            public void onCancelled (@NonNull DatabaseError error){

            }

        });


    }






}