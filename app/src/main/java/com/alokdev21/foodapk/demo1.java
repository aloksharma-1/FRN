package com.alokdev21.foodapk;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class demo1 extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Button btnSignout,demobutton;
//    private SharedPreferencesManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo1);

        // Initialize FirebaseAuth and SharedPreferencesManager
        mAuth = FirebaseAuth.getInstance();
//        sessionManager = new SharedPreferencesManager(this);

        // Check if user is not logged in, redirect to SignIn activity
        if (mAuth.getCurrentUser() == null) {
            redirectToSignInScreen();
        }

        // Initialize the sign-out button and set its listener
        btnSignout = findViewById(R.id.ButtonSignout);
        demobutton = findViewById(R.id.demo);
        demobutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(demo1.this, Home.class);
                startActivity(intent);
            }
        });
        btnSignout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Sign out the user
                mAuth.signOut();
                /*sessionManager.logout(); // Clear session data*/
                Toast.makeText(demo1.this, "Signed out successfully", Toast.LENGTH_SHORT).show();

                // Redirect to SignIn activity
                redirectToSignInScreen();
            }
        });
    }

    private void redirectToSignInScreen() {
        Intent intent = new Intent(demo1.this, SignIn.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish(); // Close the current activity
    }
}
