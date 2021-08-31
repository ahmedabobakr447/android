package com.example.medica;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserRegister extends AppCompatActivity {

    private Button signUpButton;
    private EditText editTextPassword,phoneOrEmail,confirmPassword;
    private ProgressDialog loadingBar;
    private static final String Users = "Users";
    private static final String PASSWORD_PATTERN ="^(?=.*[0-9])(?=.*[a-z]).{8,20}$";
    private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
    String saveCurrentDate,saveCurrentTime,signUpDate,encryptedPassword;
    FirebaseDatabase rootnode;
    DatabaseReference reference;


    public static boolean isValid(final String password) {
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);



        signUpButton = findViewById(R.id.signUpButton);
        editTextPassword = findViewById(R.id.password);
        loadingBar = new ProgressDialog(getBaseContext());
        phoneOrEmail=findViewById(R.id.phoneOrEmail);
        confirmPassword=findViewById(R.id.confirmPassword);

        //todo==============================================================
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("SignUp","Button work");

                CreateAccount();
            }
        });}

    private void CreateAccount() {
        String phoneOrEmail = this.phoneOrEmail.getText().toString();
        String password = this.editTextPassword.getText().toString();
        String confirmPassword = this.confirmPassword.getText().toString();

        if (password.equals(confirmPassword))
        {uploadDataToFirebase( phoneOrEmail,password );
        }else if(phoneOrEmail.isEmpty()){
            Toast.makeText(getBaseContext(), "please fill the email or username", Toast.LENGTH_LONG).show();
        }else if(!isValid(password)) {
            Toast.makeText(getBaseContext(), "password isn't valid", Toast.LENGTH_LONG).show();

        }else{
            Toast.makeText(getBaseContext(), "please check password", Toast.LENGTH_LONG).show();
        }

    }




    private void uploadDataToFirebase(String phoneOrEmail, String password) {
        Log.i("uploadDataToFirebase"," work");

        rootnode=FirebaseDatabase.getInstance();
        reference=rootnode.getReference(Users);


        //todo============================ Upload to firebase==================================

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!(snapshot.child(encodeUserEmail(phoneOrEmail)).exists())){


                    //todo============================ HashMap==================================
                    try {
                        encryptedPassword=AESCrypt.encrypt(password);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    getDate();
                    HashMap<String, Object> pharmacyDataMap = new HashMap<>();
                    pharmacyDataMap.put("phoneOrEmail",encodeUserEmail(phoneOrEmail));
                    pharmacyDataMap.put("Password",encryptedPassword);
                    pharmacyDataMap.put("CompleteSignUpDate",signUpDate);
                    pharmacyDataMap.put("SignUpDate",saveCurrentDate);
                    pharmacyDataMap.put("SignUpTime",saveCurrentTime);


                    //todo============================ HashMap==================================

                    try {

                        String decryptedPassword=AESCrypt.decrypt(encryptedPassword);
                        Log.d("decryptedPassword",decryptedPassword);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    reference.child(encodeUserEmail(phoneOrEmail)).updateChildren(pharmacyDataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){
                                        Log.i("time",signUpDate);
                                        Toast.makeText(getBaseContext(),"Congratulations, your account has been added.",Toast.LENGTH_SHORT).show();
                                        Log.d("exist","Congratulations, your account has been added");
                                        Intent intent = new Intent(getBaseContext(), UserLogin.class);
                                        startActivity(intent);

                                    }else{
                                        Toast.makeText(getBaseContext(), "Network Error:Please try again", Toast.LENGTH_SHORT).show();

                                    }


                                }
                            }) ;


                }else{
                    Toast.makeText(getBaseContext(), "Phone or email number is already exists ", Toast.LENGTH_SHORT).show();
                    Toast.makeText(getBaseContext(), "Please enter another phone number ", Toast.LENGTH_SHORT).show();
                    Log.d("exist","exit");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //todo============================ Upload to firebase==================================
    }


    public void getDate(){
        //todo============================pharmacy Date==================================

        Calendar calendar= Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate= currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime= currentTime.format(calendar.getTime());

        signUpDate= saveCurrentDate +" "+ saveCurrentTime;
        Log.d("date",signUpDate);
        //todo============================pharmacy Date==================================
    }
    static String encodeUserEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }

    static String decodeUserEmail(String userEmail) {
        return userEmail.replace(",", ".");
    }

}