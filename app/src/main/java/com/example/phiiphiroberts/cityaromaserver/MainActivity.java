package com.example.phiiphiroberts.cityaromaserver;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.phiiphiroberts.cityaromaserver.Common.Common;
import com.example.phiiphiroberts.cityaromaserver.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shashank.sony.fancytoastlib.FancyToast;

public class MainActivity extends AppCompatActivity {
    EditText userPhone,userPass;
    Button myLogin;

    FirebaseDatabase db;
    DatabaseReference users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userPhone= (EditText) findViewById(R.id.userPhone);
        userPass = (EditText) findViewById(R.id.userPass);
        myLogin = (Button) findViewById(R.id.myLogin);

        db = FirebaseDatabase.getInstance();
        users = db.getReference("User");

        myLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignInUser(userPhone.getText().toString(),userPass.getText().toString());

            }
        });
    }

    public void SignInUser(String phone, String password)
    {
        //setting a progress dialog
        final ProgressDialog mDialog = new ProgressDialog(MainActivity.this);
        mDialog.setMessage("Please Waiting...");
        mDialog.show();

        final String localPhone = phone;
        final String localPassword = password;
        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(localPhone).exists())
                {
                    User user = dataSnapshot.child(localPhone).getValue(User.class);
                    user.setPhone(localPhone);
                    if (Boolean.parseBoolean(user.getIsStaff()))//if isStaff is true
                    {
                        if (user.getPassword().equals(localPassword))
                        {
                            //Login Ok
                            Intent Home = new Intent(MainActivity.this, Home.class);
                            Common.currentUser = user;
                            startActivity(Home);

                            //giving feedback after user click sign in button
                            mDialog.dismiss();
                            FancyToast.makeText(MainActivity.this, "Login Successful", FancyToast.LENGTH_LONG, FancyToast.SUCCESS, false).show();
                            finish();
                        }else
                            mDialog.dismiss();
                            FancyToast.makeText(MainActivity.this, "Wrong Password", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                    }else
                        mDialog.dismiss();
                        FancyToast.makeText(MainActivity.this, "Please Login with staff Account", FancyToast.LENGTH_LONG, FancyToast.INFO, false).show();
                }else
                    {
                        mDialog.dismiss();
                        FancyToast.makeText(MainActivity.this, "User not found in database", FancyToast.LENGTH_LONG, FancyToast.INFO, false).show();
                    }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
