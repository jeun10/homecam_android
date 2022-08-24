package com.example.myapplicationn;

import android.app.FragmentManager;
import android.os.Bundle;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class MultiPageActivity extends Activity {

    private static final String TAG = "";
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    FirebaseUser mCurrentUser;
    TextView msg_token_fmt;


    private void signInAnonymously() {
        mAuth.signInAnonymously().addOnSuccessListener(this, new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Log.e("TAG", "success sign");
                // do your stuff
            }
        })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.e("TAG", "failed sign");
                    }
                });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        msg_token_fmt = (TextView) findViewById(R.id.msg_token_fmt);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();
                        mDatabase.child("Token").child("value").setValue(token);
                        String fff= msg_token_fmt.getText().toString();
                        // Log and toast
                        String msg = fff+token;
                        Log.d(TAG, msg);
                        Toast.makeText(MultiPageActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        //FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
        sign();
    }

    private void sign() {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            Log.e("TAG", "already sign");
        } else {
            signInAnonymously();
            Log.e("TAG", "sign null");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void myListener(View target) {
        Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
        startActivity(intent);
    }
    public void myListener1(View target){
        Intent intent = new Intent(getApplicationContext(), ShowLive.class);
        startActivity(intent);

    }
    public void myListener2(View target) {
        Intent intent = new Intent(getApplicationContext(), LogActivity.class);
        startActivity(intent);
    }


    public void myListener3(View target) {
        Intent intent = new Intent(getApplicationContext(), SendMessageActivity.class);
        startActivity(intent);

    }




}
