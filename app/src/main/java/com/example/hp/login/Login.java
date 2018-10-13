package com.example.hp.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.security.PrivateKey;

public class Login extends AppCompatActivity {
    private Button Reset,Sign,upload,detalis;
    private FirebaseAuth auth;
    private StorageReference mStorage;
    private ProgressBar progressBar;
    private ProgressDialog mprogressDialog;

    private ImageView img;
    private EditText name;
    private Button save;
    private Uri uriProfileImage;

    private static final int GALLERY_INTENT= 101;
    private String profileImageurl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        detalis=(Button)findViewById(R.id.details);
        Reset=(Button)findViewById(R.id.button_res);
        upload=(Button)findViewById(R.id.upload);
        Sign=(Button)findViewById(R.id.button_signout);
        save=(Button)findViewById(R.id.bt_save);
        img=(ImageView)findViewById(R.id.iv_photo);
        name=(EditText)findViewById(R.id.et_name);
        progressBar=(ProgressBar)findViewById(R.id.progressBar5);
        mprogressDialog= new ProgressDialog(this);

        auth=FirebaseAuth.getInstance();
        mStorage= FirebaseStorage.getInstance().getReference();



        detalis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this,Details.class));
            }
        });


        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //intent.setType("Image/*");
                //intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(intent,GALLERY_INTENT);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String displayName = name.getText().toString();

                if(displayName.isEmpty()){
                    name.setError("Name Required");
                    name.requestFocus();
                    return;
                }

                FirebaseUser User = auth.getCurrentUser();
                if(User!=null && profileImageurl!=null){
                    UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                            .setDisplayName(displayName)
                            .setPhotoUri(Uri.parse(profileImageurl)).build();
                    User.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(Login.this,"Profile Updated",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        Reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this,ResetPassword.class));
            }
        });

        Sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // progressBar.setVisibility(View.VISIBLE);

                auth.signOut();
                finish();
                startActivity(new Intent(Login.this,MainActivity.class));
                }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode== GALLERY_INTENT && requestCode== RESULT_OK && data !=null && data.getData()!=null){
            mprogressDialog.setMessage("Uploading...");
            mprogressDialog.show();
            uriProfileImage=data.getData();

           // StorageReference filepath = mStorage.child("Photos").child(uriProfileImage.getLastPathSegment());
            try {
                Bitmap bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),uriProfileImage);
                img.setImageBitmap(bitmap);


                final StorageReference profileImageRef = FirebaseStorage.getInstance().getReference("profilepics/"+System.currentTimeMillis()+".jpg");
                if(uriProfileImage!=null){
                    progressBar.setVisibility(View.VISIBLE);
                    profileImageRef.putFile(uriProfileImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressBar.setVisibility(View.GONE);
                            profileImageurl=taskSnapshot.getDownloadUrl().toString();
                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(Login.this,e.getMessage(),Toast.LENGTH_SHORT).show();

                                }
                            });{
                    }
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
