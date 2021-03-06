package com.alc.app.expressnote.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.alc.app.expressnote.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Date;

public class EntryActivity extends AppCompatActivity {

    private ImageButton mSelectImage;
    private EditText mEntryTitle;
    private EditText mEntryContent;
        private Button mAddEntryBtn;

    private Uri mImageUri = null;

    private static final int GALLERY_REQUEST = 1;

    private StorageReference mStorage;
    private DatabaseReference mDatabase;

    private ProgressDialog mProgress;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();


        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Entries");


        mSelectImage = (ImageButton) findViewById(R.id.imgBtn);
        mEntryTitle = (EditText) findViewById(R.id.titleField);
        mEntryContent = (EditText) findViewById(R.id.contentField);
        mAddEntryBtn = (Button) findViewById(R.id.addBtn);

        mProgress = new ProgressDialog(this);

        if (isNetworkConnected() || isWifiConnected()) {
            Toast.makeText(this, "Yes you can post", Toast.LENGTH_SHORT).show();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("No Internet Connection")
                    .setCancelable(false)
                    .setMessage("It looks like your internet connection is off. Please turn it " +
                            "on and try again to be able to write your note")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).setIcon(R.drawable.warning).show();
        }

        mSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);

            }
        });

        mAddEntryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                createEntry();
            }
        });
    }

    private void createEntry() {

        mProgress.setMessage("Adding to Diary....");
        final String title_val = mEntryTitle.getText().toString().trim();
        final String content_val = mEntryContent.getText().toString().trim();

        if(!TextUtils.isEmpty(title_val) && !TextUtils.isEmpty(content_val) && mImageUri != null){  //Check if all content is provided
            mProgress.show();

            StorageReference filepath = mStorage.child("hoto").child(mImageUri.getLastPathSegment()); // Provide Firebase filepath

            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() { //Add file to Firebase
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Uri dowloadUri = taskSnapshot.getDownloadUrl(); //Get file url from firebase

                    DatabaseReference newEntry = mDatabase.push();

                    newEntry.child("title").setValue(title_val);
                    newEntry.child("content").setValue(content_val);
                    newEntry.child("image").setValue(dowloadUri.toString());
                    newEntry.child("uid").setValue(mCurrentUser.getUid());
                    newEntry.child("date").setValue(new Date().toString().substring(0, 10) + new Date().toString().substring(23, 28));

                    mProgress.dismiss();

                    startActivity(new Intent(EntryActivity.this, DisplayActivity.class));

                }
            });
            
            
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK){

            mImageUri = data.getData();

            CropImage.activity(mImageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(16,9)
                    .start(this);

        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();

                mSelectImage.setImageURI(resultUri);
                mImageUri = resultUri;

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private boolean isWifiConnected() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE); // 1
        assert connMgr != null;
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo(); // 2
        return networkInfo != null && networkInfo.isConnected(); // 3
    }

    private boolean isNetworkConnected() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connMgr != null;
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && (ConnectivityManager.TYPE_WIFI == networkInfo.getType()) && networkInfo.isConnected();
    }




}
