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
import com.google.firebase.auth.FirebaseAuth;

public class Forgot extends AppCompatActivity {

    private EditText emailInput;
    private Button resetPasswordButton;
    private TextView  textBackToSignIn;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);

        // Initialize FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Initialize views
        emailInput = findViewById(R.id.editTextForgotEmail);
        resetPasswordButton = findViewById(R.id.buttonResetPassword);
        textBackToSignIn = findViewById(R.id.txtBackToSignIn);

        progressDialog = new ProgressDialog(this);

        // Handle Reset Password Button Click
        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });

        // Handle Back to Sign In Button Click
        textBackToSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Forgot.this, SignIn.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void resetPassword() {
        String email = emailInput.getText().toString().trim();

        if (email.isEmpty()) {
            emailInput.setError("Please enter your email");
            emailInput.requestFocus();
        } else {
            progressDialog.setMessage("Sending password reset link...");
            progressDialog.setTitle("Please wait");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            // Send password reset email
            mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        Toast.makeText(Forgot.this, "Password reset link sent to your email", Toast.LENGTH_SHORT).show();

                        // Redirect to Sign In
                        Intent intent = new Intent(Forgot.this, SignIn.class);
                        startActivity(intent);
                        finish();
                    } else {
                        String errorMessage = task.getException() != null ? task.getException().getMessage() : "Error occurred";
                        Toast.makeText(Forgot.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
