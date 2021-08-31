package com.example.medica;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
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
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.widget.CheckBox;

import io.paperdb.Paper;

import static android.content.ContentValues.TAG;

public class UserLogin extends AppCompatActivity {
    TextView signUpText;
    Button loginButtonTrad ;
    private EditText textViewRegisterMethod,textViewPassword;
    private static final String PharmacyUsers = "PharmacyUsers";
    CheckBox checkBox;
    String decryptedPassword=null;
    private GoogleSignInClient mGoogleSignInClient;
    private final int RC_SIGN_IN=5;
    public static FirebaseAuth mAuth;
    private CallbackManager mCallbackManager;
    private static final String Users="Users";
    public static String GloblencodedEmailOrPhone;
    FirebaseUser currentUser;
    //todo==============================================google==================================

    private void printEmail(FirebaseUser account){
        for (UserInfo userInfo : account.getProviderData()) {
            Log.i(userInfo.getUid(),userInfo.getProviderId()+" "+
                    userInfo.getEmail()+" "+userInfo.isEmailVerified() );
            Toast.makeText(mContextInScope, userInfo.getEmail(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if(mAuth != null){
         currentUser = mAuth.getCurrentUser();}
        updateUI(currentUser);
    }
    private void ToHostTransition(){
        Intent intent = new Intent(UserLogin.this,UserHostForPharmacy.class);
        startActivity(intent);
    }
    public void updateUI(FirebaseUser account){

        account = FirebaseAuth.getInstance().getCurrentUser();
        if(account != null){

            printEmail(account);
            Toast.makeText(UserLogin.this,"U login successfully",Toast.LENGTH_LONG).show();
            ToHostTransition();
        }else {
            Toast.makeText(UserLogin.this,"U Didnt signed in",Toast.LENGTH_LONG).show();
        }
    }




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        Paper.init(this);
        //TODO:identifiers
        loginButtonTrad = findViewById(R.id.Login);
        signUpText= findViewById(R.id.signUpText);
        textViewPassword= findViewById(R.id.textViewPassword);
        textViewRegisterMethod=findViewById(R.id.textViewEmail);
        Button switchButtonToPharmacy = findViewById(R.id.switchButtonToPharmacy);
        //TODO: checkBox
        checkBox= (CheckBox) findViewById(R.id.delivaryService);
        loginButtonTrad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = textViewRegisterMethod.getText().toString();
                String password = textViewPassword.getText().toString();

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(UserLogin.this, "Please Enter your password...", Toast.LENGTH_SHORT).show();
                } else if(TextUtils.isEmpty(phone)) {
                    Toast.makeText(UserLogin.this, "Please Enter your phone number or email...", Toast.LENGTH_SHORT).show();
                }else{

                    AllowAccessToAccount(phone,password);

                }
            }
        });
        signUpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserLogin.this, UserRegister.class);
                intent.putExtra("UserComming", true);
                startActivity(intent);
            }
        });
        switchButtonToPharmacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserLogin.this,PharmacyLogin.class);
                startActivity(intent);
            }
        });

        String UserPhoneKey = Paper.book().read(Prevalent.UserPhoneKey);
        String UserPassword = Paper.book().read(Prevalent.UserPasswordKey);

        if(UserPhoneKey != null && UserPassword !=null){

            if(!TextUtils.isEmpty(UserPhoneKey) && !TextUtils.isEmpty(UserPassword)){

                AllowAccessWithRemember(UserPhoneKey,UserPassword);

            }

        }

    }



    private void AllowAccessWithRemember(String userPhoneKey, String userPassword) {
        String encodeUserEmail= encodeUserEmail(userPhoneKey);
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(Users).child(encodeUserEmail).exists()) {

                    String  phoneOrEmail = decodeUserEmail(snapshot.child(Users).child(encodeUserEmail).getKey());

                    try {
                        decryptedPassword=AESCrypt.decrypt(snapshot.child(Users).child(encodeUserEmail).child("Password").getValue().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (phoneOrEmail.equals(userPhoneKey) && decryptedPassword.equals(userPassword)) {
                        GloblencodedEmailOrPhone=encodeUserEmail;
                         textViewPassword.setText(decryptedPassword);
                         textViewRegisterMethod.setText(phoneOrEmail);
                        checkBox.setChecked(true);

                    }else{
                    }

                } else {
               }

            }
            @Override
            public void onCancelled (@NonNull DatabaseError error){

            }

        });
    }


    private void AllowAccessToAccount(String phone, String password) {

        Paper.init(this);
        if(checkBox.isChecked()){
            Paper.book().write(Prevalent.UserPhoneKey,phone);
            Paper.book().write(Prevalent.UserPasswordKey,password);
        }
        String encodeUserEmail= encodeUserEmail(phone);



        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(Users).child(encodeUserEmail).exists()) {
                    String  phoneOrEmail = decodeUserEmail(snapshot.child(Users).child(encodeUserEmail).getKey());

                    try {
                        decryptedPassword=AESCrypt.decrypt(snapshot.child(Users).child(encodeUserEmail).child("Password").getValue().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Log.d("phone",phone);

                    Log.d("password",password);
                    Log.d("decryptedPassword",decryptedPassword);
                    Log.d("getphone",phoneOrEmail);
                    if (phoneOrEmail.equals(phone) && decryptedPassword.equals(password)) {
                        Toast.makeText(mContextInScope, "You have login successfully...", Toast.LENGTH_SHORT).show();
                        GloblencodedEmailOrPhone=encodeUserEmail;
                        Intent intent = new Intent(mContextInScope, UserHostForPharmacy.class);
                        startActivity(intent);

                    }else{
                        Toast.makeText(mContextInScope, "Your password is not correct", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(mContextInScope, "Account with this" + phone + "number is not exists.", Toast.LENGTH_SHORT).show();
                    Toast.makeText(mContextInScope, "you need to create a new account", Toast.LENGTH_SHORT).show();
                }

            }


            @Override
            public void onCancelled (@NonNull DatabaseError error){

            }

        });


    }

    @Override
    protected void onStop() {
        super.onStop();
        Paper.init(this);
        if(!checkBox.isChecked()){
            Paper.book().write(Prevalent.UserPhoneKey,"");
            Paper.book().write(Prevalent.UserPasswordKey,"");
        }

    }

    static String encodeUserEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }

    static String decodeUserEmail(String userEmail) {
        return userEmail.replace(",", ".");
    }



    Context mContextInScope = UserLogin.this;
















}