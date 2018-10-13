package com.example.hp.login;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EnterDeatils extends AppCompatActivity {

    private EditText input;
    private Button sub;
    private DatabaseReference rootRef,demoRef;
    private FirebaseAuth firebaseAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_deatils);


        input=(EditText)findViewById(R.id.et_detail);
        sub=(Button)findViewById(R.id.submit);


        firebaseAuth = FirebaseAuth.getInstance();

        String currentUser = firebaseAuth.getCurrentUser().getUid();


        rootRef = FirebaseDatabase.getInstance().getReference();

        demoRef = rootRef.child(currentUser);


        sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = input.getText().toString();

                if(haveNetwork()) {

                    if (!TextUtils.isEmpty(name)) {
                        demoRef.child("Name").setValue(name);
                        Toast.makeText(EnterDeatils.this, "Upload done", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(EnterDeatils.this, Login.class));

                    } else {

                        Toast.makeText(EnterDeatils.this, "Enter the Detail", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(EnterDeatils.this, "NO INTERNET", Toast.LENGTH_SHORT).show();
                }
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
