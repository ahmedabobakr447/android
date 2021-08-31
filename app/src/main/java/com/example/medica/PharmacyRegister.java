package com.example.medica;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rey.material.widget.CheckBox;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PharmacyRegister extends AppCompatActivity {

    Uri imageUri;
    private Button signUpButton;
    private EditText pharmacyName, phoneNumber, editTextPassword, confirmPassword, locationAddress, whatsappNumber;
    private static final String PharmacyUsers = "PharmacyUsers";
    private CardView cardView;
    private static final int GalleryPick = 1000;
    private static final int GalleryPick_PERMISSION = 1000;
    private ImageView imageCard;
    private EditText email;
    private Button defineLocation;
    private String Url;
    private double latitude;
    private double longtide;
    private boolean flag = false;
    private boolean imageFlag;
    private CheckBox delivaryService;
    private static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z]).{8,20}$";
    //(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>])
    private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
    private String DownloadLogoUri = "Default";
    private StorageReference pharmacyImageRef;
    SharedPreferences preferences;
    private boolean delivary = false;
    private boolean pictureSet = false;
    private String ImagePath = "";
    private Bitmap bitmap = null;
    private Uri uploadUri = null;
    Uri uploadUriTwo = null;
    String downloadImageUrl;
    private String DownloadLogoUrl;
    String saveCurrentDate, saveCurrentTime, signUpDate, encryptedPassword;
    CardView MedicineCard;
    boolean imageeeeeFlag;

    String locationUrl ;
    double locationLat ;
    double LocationLong ;
    boolean CheangeMaps ;

    @Override
    public void onResume() {
        super.onResume();
        gettingTheLatLong();
        Log.i("lat", String.valueOf(locationLat));
        Log.i("picture", String.valueOf(pictureSet));
        if(CheangeMaps) {
            defineLocation.setText("Change");
        }
        DataGetter();
        Log.i("pictureSet",String.valueOf(pictureSet));
    }


    public void gettingTheLatLong(){
        locationUrl = getIntent().getStringExtra("LocationUrl");
        locationLat = getIntent().getDoubleExtra("LocationLat",0);
        LocationLong = getIntent().getDoubleExtra("LocationLong",0);
        CheangeMaps = getIntent().getBooleanExtra("Changed",false);
    }


    public static boolean isValid(final String password) {
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                           if (result.getResultCode() == Activity.RESULT_OK) {
                                // There are no request codes
                                Intent data = result.getData();
                                if (data != null ) {
                                    MedicineCard.setVisibility(View.INVISIBLE);
                                    imageUri = data.getData();

                                    Log.i("URIiiiii",String.valueOf(imageUri));
                                    try {
                                        if(  imageUri.getPath()!=null ){
                                            bitmap = MediaStore.Images.Media.getBitmap(getBaseContext().getContentResolver() , imageUri);
                                            Log.i("bitmap"," exist");
                                        }else{
                                            Log.i("problem"," in path");
                                        }
                                    }
                                    catch (Exception e) {
                                        Log.i("problem"," in pathhhhhhhhhhhhh");
                                    }
                                    uploadUri= getImageUri(getBaseContext(),bitmap);
                                    imageCard.setImageURI(uploadUri);
                                    imageeeeeFlag = true;
                                    DataGetter();

                        }
                    }
                }
            });

    public void openGallaryForResult() {
        if (ContextCompat.checkSelfPermission(getBaseContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(PharmacyRegister.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }else {
            goToGallary();
        }
    }

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
                goToGallary();
            } else {
                // Permission Denied
                Toast.makeText(PharmacyRegister.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    public void goToGallary(){
        Intent galleryintent = new Intent(Intent.ACTION_GET_CONTENT, null);
        galleryintent.setType("image/*");

        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        Intent chooser = new Intent(Intent.ACTION_CHOOSER);
        chooser.putExtra(Intent.EXTRA_INTENT, galleryintent);
        chooser.putExtra(Intent.EXTRA_TITLE, "title");
        Intent[] intentArray = {cameraIntent};
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
        someActivityResultLauncher.launch(chooser);
    }

    public  Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 1, bytes);
        byte[] imageInByte = bytes.toByteArray();
        long lengthbmp = imageInByte.length/1024;
        Log.i("imageLength", String.valueOf(lengthbmp));
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pharmacy_register);

        signUpButton = findViewById(R.id.signUpButton);
        pharmacyName = findViewById(R.id.MedicineName);
        phoneNumber = findViewById(R.id.phoneOrEmail);
        editTextPassword = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmPassword);
        MedicineCard = findViewById(R.id.MedicineCard);
        imageCard = findViewById(R.id.imageCard);
        email = findViewById(R.id.email);
        defineLocation = findViewById(R.id.defineLocationPublish);
        delivaryService = findViewById(R.id.delivaryService);
        pharmacyImageRef = FirebaseStorage.getInstance().getReference().child("Pharmacy_Logo");
        locationAddress = findViewById(R.id.locationAddress);
        whatsappNumber = findViewById(R.id.whatsapp);





        delivaryService.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Toast.makeText(getBaseContext(), String.valueOf(delivaryService.isChecked()), Toast.LENGTH_LONG).show();
                delivary = delivaryService.isChecked();
            }
        });

/*
        if (flag) {
            defineLocation.setText("Change Location");
        }*/

        imageCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imageFlag== true){
                    DataSaver();
                    openGallaryForResult();}
            }
        });

        if(imageFlag== false){
            MedicineCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DataSaver();
                    openGallaryForResult();
                }
            });}

        //todo==============================     save    ================================

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAccount();
                Clear();
            }
        });
        //todo==============================     save    ================================
        defineLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DataSaver();
                Intent intent = new Intent(getBaseContext(), MapsActivity.class);
                startActivity(intent);
            }
        });
        //todo==============================     save    ================================




    }



    private void CreateAccount() {
        String pharmacyName = this.pharmacyName.getText().toString();
        String phone = phoneNumber.getText().toString();
        String password = editTextPassword.getText().toString();
        String confirmPassword = this.confirmPassword.getText().toString();
        String emailStr = email.getText().toString();
        String locationAddress = this.locationAddress.getText().toString();
        String whatsappNumber = this.whatsappNumber.getText().toString();
/*
        if (TextUtils.isEmpty(pharmacyName) || pharmacyName.length() > 50) {
            Toast.makeText(getBaseContext(), "Please Enter your Pharmacy Name...", Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(locationAddress) || pharmacyName.length() > 50) {
            Toast.makeText(getBaseContext(), "Please Enter your Location Address...", Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(phone)) {
            Toast.makeText(getBaseContext(), "Please Enter your phone number...", Toast.LENGTH_LONG).show();
        } else if (!(phone.length() == 11)) {
            Toast.makeText(getBaseContext(), "Please Correct Phone Number...", Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(emailStr) || emailStr.length() > 50) {
            Toast.makeText(getBaseContext(), "Please Enter your email...", Toast.LENGTH_LONG).show();
        } else if (!emailStr.contains("@")) {
            Toast.makeText(getBaseContext(), "Please Enter correct email...", Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(getBaseContext(), "Please Enter your password...", Toast.LENGTH_LONG).show();
        } else if (!isValid(password)) {
            Toast.makeText(getBaseContext(), "You should enter numbers and letters...", Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(getBaseContext(), "Please confirm your password...", Toast.LENGTH_LONG).show();
        } else if (!confirmPassword.equals(password)) {
            Toast.makeText(getBaseContext(), "Password isn't correct...", Toast.LENGTH_LONG).show();
        } else if (!flag) {
            Toast.makeText(getBaseContext(), "Please select pharmacy Location...", Toast.LENGTH_LONG).show();
        }*/
        // else {

        uploadImage(pharmacyName, phone, password, emailStr, whatsappNumber, locationAddress);
        // }

    }

    public void uploadImage(String pharmacyName, String phone, String password, String emailStr, String whatsappNumber, String locationAddress) {
        //todo============================upload pharmacy Logo==================================
        getDate();
        Uri finalUri;
        if (!(uploadUri == null)) {
            finalUri = uploadUri;
        } else {
            finalUri = uploadUriTwo;
        }
        StorageReference filePath = pharmacyImageRef.child(finalUri.getLastPathSegment() + signUpDate + ".jpg");
        final UploadTask uploadTask = filePath.putFile(finalUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message = e.toString();
                Toast.makeText(getBaseContext(), "Error: " + message, Toast.LENGTH_SHORT).show();

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getBaseContext(), "Product Image upload Successfully...", Toast.LENGTH_LONG).show();

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        downloadImageUrl = filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {

                        if (task.isSuccessful()) {

                            downloadImageUrl = task.getResult().toString();
                            Toast.makeText(getBaseContext(), "got product image Url Successfully...", Toast.LENGTH_LONG).show();
                            Log.d("messagessss", downloadImageUrl);

                            uploadDataToFirebase(pharmacyName, phone, password, emailStr, whatsappNumber, locationAddress);
                        }
                    }
                });

            }

        });

        //todo============================upload pharmacy Logo==================================
    }
    private void uploadDataToFirebase(String pharmacyName, String phone, String password, String emailStr, String whatsappNumber, String locationAddress) {


        final DatabaseReference RootRef = FirebaseDatabase.getInstance().getReference();
        //todo============================ Upload to firebase==================================
        //
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!(snapshot.child(PharmacyUsers).child(phone).exists()) && !(snapshot.child(PharmacyUsers).child(encodeUserEmail(emailStr)).exists())) {


                    //todo============================ HashMap==================================
                    try {
                        encryptedPassword = AESCrypt.encrypt(password);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    HashMap<String, Object> pharmacyDataMap = new HashMap<>();
                    pharmacyDataMap.put("Phone", phone);
                    pharmacyDataMap.put("Password", encryptedPassword);
                    pharmacyDataMap.put("pharmacyName", pharmacyName);
                    pharmacyDataMap.put("Email", encodeUserEmail(emailStr));
                    pharmacyDataMap.put("LocationLat", locationLat);
                    pharmacyDataMap.put("LocationLong", LocationLong);
                    pharmacyDataMap.put("LocationUrl", locationUrl);
                    pharmacyDataMap.put("CompleteSignUpDate", signUpDate);
                    pharmacyDataMap.put("SignUpDate", saveCurrentDate);
                    pharmacyDataMap.put("SignUpTime", saveCurrentTime);
                    Log.d("url", downloadImageUrl);
                    pharmacyDataMap.put("downloadImageUrl", downloadImageUrl);
                    pharmacyDataMap.put("Delivary", delivary);
                    pharmacyDataMap.put("Description", locationAddress);
                    pharmacyDataMap.put("Whatsapp", whatsappNumber);
                    //todo============================ HashMap==================================

                    try {

                        String decryptedPassword = AESCrypt.decrypt(encryptedPassword);
                        Log.d("decryptedPassword", decryptedPassword);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    RootRef.child(PharmacyUsers).child(encodeUserEmail(emailStr)).updateChildren(pharmacyDataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {

                                        Toast.makeText(getBaseContext(), "Congratulations, your pharmacy has been added.", Toast.LENGTH_SHORT).show();
                                        Log.d("exist", "Congratulations, your pharmacy has been added");
                                        Intent intent = new Intent(getBaseContext(), PharmacyLogin.class);
                                        startActivity(intent);

                                    } else {

                                        Toast.makeText(getBaseContext(), "Network Error:Please try again", Toast.LENGTH_SHORT).show();

                                    }


                                }
                            });


                } else {
                    Toast.makeText(getBaseContext(), "Phone number is already exists ", Toast.LENGTH_SHORT).show();

                    Toast.makeText(getBaseContext(), "Please enter another phone number ", Toast.LENGTH_SHORT).show();
                    Log.d("exist", "exit");

                }
            }

            @Override
            public void onCancelled(@NonNull @org.jetbrains.annotations.NotNull DatabaseError error) {

            }


        });
        //todo============================ Upload to firebase==================================
    }


    public void getDate() {
        //todo============================pharmacy Date==================================

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        signUpDate = saveCurrentDate + " " + saveCurrentTime;
        Log.d("date", signUpDate);
        //todo============================pharmacy Date==================================
    }




    //todo===========================================Compress the Image============================================



    //todo===========================================Compress the Image============================================



    static String encodeUserEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }

    @Override
    public void onBackPressed(){
        Clear();
        Intent toLogin = new Intent(this,PharmacyLogin.class);
        startActivity(toLogin);

    }


    public void Clear(){
        if(preferences != null){
            imageeeeeFlag=false;
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.apply();
        }

    }


    //todo==============================Savers======================================

    public void DataSaver(){
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("PharmacyName", pharmacyName.getText().toString());
        editor.putString("PhoneNumber", phoneNumber.getText().toString());
        editor.putString("Password", editTextPassword.getText().toString());
        editor.putString("ConfirmPassword", confirmPassword.getText().toString());
        editor.putString("Email", email.getText().toString());
        editor.putBoolean("delivary", delivary);
        editor.putString("locationAddress", locationAddress.getText().toString());
        editor.putString("whatsappNumber", whatsappNumber.getText().toString());
        editor.putBoolean("pictureSet", imageeeeeFlag);

        if (imageeeeeFlag) {
            editor.putString("imageURISingUp", uploadUri.toString());
            Toast.makeText(this, "Image path picked", Toast.LENGTH_LONG).show();
        }else{
            editor.putString("imageURISingUp", null);
        }
        editor.apply();

    }
    public void DataGetter(){
        preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        pharmacyName.setText(preferences.getString("PharmacyName", ""));
        phoneNumber.setText(preferences.getString("PhoneNumber", ""));
        editTextPassword.setText(preferences.getString("Password", ""));
        confirmPassword.setText(preferences.getString("ConfirmPassword", ""));
        email.setText(preferences.getString("Email", ""));
        pictureSet=preferences.getBoolean("pictureSet", false);
        if(pictureSet){
            String imageUriString = preferences.getString("imageURISingUp", null);
            uploadUriTwo = Uri.parse(imageUriString);
            MedicineCard.setVisibility(View.INVISIBLE);
            imageCard.setImageURI(uploadUriTwo);}
        if(preferences.getBoolean("delivary", false)){
            Log.i("delivary",String.valueOf(preferences.getBoolean("delivary", false)) );
            delivaryService.setChecked(preferences.getBoolean("delivary", false));}
        locationAddress.setText(preferences.getString("locationAddress", ""));
        whatsappNumber.setText(preferences.getString("whatsappNumber", ""));

    }




    //todo==============================Savers======================================
}