package com.example.medica;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import static com.example.medica.UserLogin.GloblencodedEmailOrPhone;


public class PublishMedicine extends AppCompatActivity {

    CardView MedicineCard;
    static final int GalleryPick=1;
    Bitmap bitmap;
    Uri uploadUri;
    ImageView imageCard;
    String signUpDate;
    String saveCurrentDate ;
    String saveCurrentTime;
    SharedPreferences preferences;
    boolean pictureSet=false;
    boolean flag= false;
    String Url;
    double latitude;
    double longtide;
    EditText Title;
    EditText Description;
    public Uri uploadUriTwo;
    private StorageReference pharmacyImageRef;
    private String downloadImageUrl;
   private final static String Users= "Users";
    private final static String EmailOrPhone= "EmailOrPhone";
    private final static String Published_Ads= "Published_Ads";
    private final static String Ads= "Ads";
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


    static String encodeUserEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }

    static String decodeUserEmail(String userEmail) {
        return userEmail.replace(",", ".");
    }
    private String printPhoneOrEmailFromFireBase(FirebaseUser account, DataSnapshot dataSnapshot){


        String emailString=null;
        String phoneString=null;
        String phoneOrEmail;
        for (UserInfo userInfo : account.getProviderData()) {
            emailString=userInfo.getEmail();
            phoneString=userInfo.getPhoneNumber();
            Toast.makeText(getApplicationContext(), emailString, Toast.LENGTH_LONG).show();
            Toast.makeText(getApplicationContext(), phoneString, Toast.LENGTH_LONG).show();
       }
        if(!(emailString==null)){
            phoneOrEmail=emailString;
        }else if(!(phoneString==null)){
            phoneOrEmail=phoneString;
        }else{
            phoneOrEmail= GloblencodedEmailOrPhone;
            Toast.makeText(getApplicationContext(), dataSnapshot.child(Users).child(EmailOrPhone).toString(), Toast.LENGTH_LONG).show();
            Toast.makeText(getApplicationContext(), phoneOrEmail, Toast.LENGTH_LONG).show();

        }

        return  encodeUserEmail(phoneOrEmail);

    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_medicine);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarPublish);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Button defineLocation= findViewById(R.id.defineLocationPublish);
        imageCard=findViewById(R.id.imageView2);
        MedicineCard=findViewById(R.id.MedicineCard);
        Button publishYourMedicine= (Button) findViewById(R.id.PublishYourMedicine);
        Title=findViewById(R.id.Title);
        Description=findViewById(R.id.Description);
        pharmacyImageRef = FirebaseStorage.getInstance().getReference().child("Ads_Images");
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Url=extras.getString("LocationUrl");
            latitude=extras.getDouble("LocationLat");
            longtide=extras.getDouble("LocationLong");
            Log.d("valus",Url+String.valueOf(latitude)+String.valueOf(longtide));
            Toast.makeText(PublishMedicine.this, Url+String.valueOf(latitude)+String.valueOf(longtide), Toast.LENGTH_SHORT).show();
        }

       Toast.makeText(PublishMedicine.this, String.valueOf(Url), Toast.LENGTH_SHORT).show();
        if(Url != null) {
            preferences = PreferenceManager.getDefaultSharedPreferences(this);
            pictureSet = preferences.getBoolean("pictureSet", false);
            Title.setText(preferences.getString("Title", ""));
            Description.setText(preferences.getString("Description", ""));
            Toast.makeText(PublishMedicine.this, String.valueOf(pictureSet), Toast.LENGTH_SHORT).show();

            if (pictureSet) {
                String imageUriString = preferences.getString("imageURI", null);
                uploadUriTwo = Uri.parse(imageUriString);
                MedicineCard.setVisibility(View.INVISIBLE);
                imageCard.setImageURI(uploadUriTwo);
                Log.d("uploadUriTwo",uploadUriTwo.toString());
            }
            defineLocation.setText("Change Location");
        }


        MedicineCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallaryForResult();
            }
        });

        imageCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallaryForResult();
            }
        });

        defineLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("Title", Title.getText().toString());
                editor.putString("Description", Description.getText().toString());
                if (pictureSet) {
                    editor.putString("imageURI", uploadUri.toString());
                    editor.putBoolean("pictureSet", true);
                   Toast.makeText(getApplicationContext(), "Image path picked", Toast.LENGTH_LONG).show();
                }else{
                    editor.putString("imageURI", null);
                }
                editor.apply();
                statusCheck();

                Intent intent = new Intent(PublishMedicine.this, MapsPublish.class);
                startActivity(intent);
            }
        });

        publishYourMedicine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadData();
            }
        });








   }



    private void UploadData() {
        String TitleText= Title.getText().toString();
        String Description= this.Description.getText().toString();

       if (TextUtils.isEmpty(TitleText) || TitleText.length() > 50) {
            Toast.makeText(this, "Please Enter your Title...", Toast.LENGTH_LONG).show();
        }else if (TextUtils.isEmpty(Description) || Description.length() > 50) {
            Toast.makeText(this, "Please Enter your Description...", Toast.LENGTH_LONG).show();
        }else{


            uploadImage(TitleText,Description);
        }
    }


    public void uploadImage(String TitleText, String Description) {
        //todo============================upload pharmacy Logo==================================
        getDate();
        Uri finalUri;
        if (!(uploadUri == null)) {
            finalUri = uploadUri;
        } else {
            finalUri = uploadUriTwo;
        }
        Toast.makeText(this, finalUri.toString(), Toast.LENGTH_SHORT).show();
        Log.d("this tag", finalUri.toString());

        StorageReference filePath = pharmacyImageRef.child(finalUri.getLastPathSegment() + signUpDate + ".jpg");
        final UploadTask uploadTask = filePath.putFile(finalUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message = e.toString();
                Toast.makeText(getApplicationContext(), "Error: " + message, Toast.LENGTH_SHORT).show();
               // loadingBar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getApplicationContext(), "Product Image upload Successfully...", Toast.LENGTH_LONG).show();

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
                            Toast.makeText(getApplicationContext(), "got product image Url Successfully...", Toast.LENGTH_LONG).show();
                            Log.d("messagessss", downloadImageUrl);

                            uploadDataToFirebase(TitleText , Description);
                        }
                    }
                });

            }

        });

        //todo============================upload pharmacy Logo==================================
    }

    private void uploadDataToFirebase(String TitleText, String Description) {


        final DatabaseReference RootRef = FirebaseDatabase.getInstance().getReference();
        //todo============================ Upload to firebase==================================
        //
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                    String Email_Or_Phone_Id=printPhoneOrEmailFromFireBase(user,snapshot)+"_"+signUpDate;
                    String Email_Or_Phone=printPhoneOrEmailFromFireBase(user,snapshot);



                    //todo============================ HashMap==================================

                    HashMap<String, Object> UsersDataMap = new HashMap<>();
                    UsersDataMap.put("LocationLat", latitude);
                    UsersDataMap.put("LocationLong", longtide);
                    UsersDataMap.put("LocationUrl", Url);
                    UsersDataMap.put("CompleteAdsDate", signUpDate);
                    UsersDataMap.put("AdsDate", saveCurrentDate);
                    UsersDataMap.put("AdsTime", saveCurrentTime);
                    Log.d("url", downloadImageUrl);
                    UsersDataMap.put("AdsdownloadImageUrl", downloadImageUrl);
                    UsersDataMap.put("TitleText", TitleText);
                    UsersDataMap.put("Description", Description);
                    UsersDataMap.put("EmailOrPhone", Email_Or_Phone);
                    UsersDataMap.put("EmailOrPhone_Id", Email_Or_Phone_Id);

                    //todo============================ HashMap==================================

                    RootRef.child(Ads).child(Email_Or_Phone).child(Email_Or_Phone_Id).updateChildren(UsersDataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {

                                        Toast.makeText(getApplicationContext(), "Congratulations, your pharmacy has been added.", Toast.LENGTH_SHORT).show();

                                        Log.d("exist", "Congratulations, your pharmacy has been added");
                                        Intent intent = new Intent(getApplicationContext(), UserHostForPharmacy.class);
                                        startActivity(intent);

                                    } else {

                                        Toast.makeText(getApplicationContext(), "Network Error:Please try again", Toast.LENGTH_SHORT).show();

                                    }


                                }
                            });
           }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //todo============================ Upload to firebase==================================
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }





//todo====================================================================================
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
                        Uri imageUri = data.getData();

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
                        pictureSet=true;

                    }
                }
            }
        });

    public void openGallaryForResult() {
        if (ContextCompat.checkSelfPermission(getBaseContext(),
        Manifest.permission.READ_EXTERNAL_STORAGE)
        != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(PublishMedicine.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }else {
            goToGallary();
        }
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



       /*     MedicineCard.setVisibility(View.INVISIBLE);
            Uri imageUri = data.getData();

            try {
                if(  Uri.parse(imageUri.getPath())!=null   ){
                    bitmap = MediaStore.Images.Media.getBitmap(getBaseContext().getContentResolver() , Uri.parse(imageUri.getPath()));
                    if(bitmap == null){Log.i("bitmap","exist");}


                }
            }
            catch (Exception e) {
                //handle exception
            }

            Uri imageCompressed= getImageUri(getBaseContext(),bitmap);
            imageCard.setImageURI(imageCompressed);
*/


    public  Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 1, bytes);
        byte[] imageInByte = bytes.toByteArray();
        long lengthbmp = imageInByte.length/1024;
        Log.i("imageLength", String.valueOf(lengthbmp));
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

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
                Toast.makeText(PublishMedicine.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }












 //todo========================================================================================
    public void getDate() {
        //todo============================pharmacy Date==================================

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM_dd_ yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        signUpDate = saveCurrentDate + " " + saveCurrentTime;
        Log.d("date", signUpDate);
        //todo============================pharmacy Date==================================
    }
    public void statusCheck() {
        final LocationManager manager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        }
    }
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }








}