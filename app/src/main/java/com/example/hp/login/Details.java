package com.example.hp.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Details extends AppCompatActivity {
    private DatabaseReference rootRef,demoRef;
    private FirebaseAuth firebaseAuth;
    private TextView data;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        firebaseAuth = FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference();

        String currentUser = firebaseAuth.getCurrentUser().getUid();
        demoRef = rootRef.child(currentUser);

        data=(TextView)findViewById(R.id.data);


        progressDialog= new ProgressDialog(Details.this);
        progressDialog.setTitle("Welcome");
        progressDialog.setMessage("Retrieving Information");
        progressDialog.show();


        demoRef.child("Name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                //Log.d(TAG,"Value is"+ value);
                //Toast.makeText(Details.this,"value is"+value,Toast.LENGTH_SHORT).show();
                if(haveNetwork()) {


                    if (value == null) {
                        startActivity(new Intent(Details.this, EnterDeatils.class));
                    } else {
                        data.setText(value);
                        progressDialog.dismiss();
                    }
                }
                else{
                    Toast.makeText(Details.this,"Check your Internet Connection",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Details.this,"Check your Internet Connection",Toast.LENGTH_SHORT).show();
            }
        });
    }
    private Boolean haveNetwork(){
        boolean have_WIFI=false;
        boolean have_MobileData=false;

        ConnectivityManager connectivityManager=(ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfos=connectivityManager.getAllNetworkInfo();

        for (NetworkInfo info:networkInfos){
            if(info.getTypeName().equalsIgnoreCase("WIFI"))
                if(info.isConnected())
                    have_WIFI=true;
            if (info.getTypeName().equalsIgnoreCase("MOBILE"))
                if(info.isConnected())
                    have_MobileData=true;
        }
        return have_MobileData||have_WIFI;

    }
}
