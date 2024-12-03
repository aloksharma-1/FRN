package com.alokdev21.foodapk;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignIn extends AppCompatActivity {
    private TextView tvsignup, txtforgot;
    private EditText outputemail, outputpassword;
    private Button btnsignin;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    private SharedPreferencesManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Initialize views
        tvsignup = findViewById(R.id.tvSignup);
        txtforgot = findViewById(R.id.forgottxt); // Initialize forgot password text
        outputemail = findViewById(R.id.editTextTextEmailAddress);
        outputpassword = findViewById(R.id.editTextTextPassword);
        btnsignin = findViewById(R.id.buttonsignin);

        // Initialize FirebaseAuth and SharedPreferencesManager
        mAuth = FirebaseAuth.getInstance();
        sessionManager = new SharedPreferencesManager(this);

        // Initialize progress dialog
        progressDialog = new ProgressDialog(this);

        // Check if user is already logged in, redirect to Home if so
        if (mAuth.getCurrentUser() != null) {
            redirectToHomeScreen();
        }

        // Set up button click listeners
        setUpButtonListeners();
    }

    private void setUpButtonListeners() {
        // Sign Up button click listener
        tvsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignIn.this, SignUp.class);
                startActivity(intent);
            }
        });

        // Forgot Password button click listener
        txtforgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignIn.this, Forgot.class);
                startActivity(intent);
            }
        });

        // Sign In button click listener
        btnsignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLogin();
            }
        });
    }

    private void performLogin() {
        String email = outputemail.getText().toString().trim();
        String password = outputpassword.getText().toString().trim();

        // Input validation
        if (!email.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")) {
            outputemail.setError("Enter a valid email");
            outputemail.requestFocus();
        } else if (password.isEmpty() || password.length() < 6) {
            outputpassword.setError("Password must be at least 6 characters");
            outputpassword.requestFocus();
        } else {
            // Show progress dialog
            progressDialog.setMessage("Logging in...");
            progressDialog.setTitle("Please wait");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            // Sign in with Firebase
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialog.dismiss();
                            if (task.isSuccessful()) {
                                Toast.makeText(SignIn.this, "Login successful!", Toast.LENGTH_SHORT).show();

                                // Save login state and redirect
                                sessionManager.setLogin(true);
                                redirectToHomeScreen();
                            } else {
                                String errorMessage = task.getException() != null ? task.getException().getMessage() : "Login failed";
                                Toast.makeText(SignIn.this, errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void redirectToHomeScreen() {
        Intent intent = new Intent(SignIn.this, Home.class); // Replace with your actual home screen activity
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish(); // Finish the current activity so the user can't navigate back to the login screen
    }
}
