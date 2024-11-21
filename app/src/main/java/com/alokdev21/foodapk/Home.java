package com.alokdev21.foodapk;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class Home extends AppCompatActivity {
    private FirebaseAuth mAuth;
    Button btnSignout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Initialize the button using findViewById
        btnSignout = findViewById(R.id.ButtonSignout); // Make sure this matches the button id in your layout

        // Set up the button click listener
        btnSignout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Sign out the user
                mAuth.signOut();

                // Redirect to SignIn activity
                Intent intent = new Intent(Home.this, SignIn.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish(); // Close the current activity
            }
        });
    }
}
