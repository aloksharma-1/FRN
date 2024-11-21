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
    TextView tvsignup;
    EditText outputemail, outputpassword;
    Button btnsignin;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    ProgressDialog progressDialog;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    SharedPreferencesManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Initialize views
        tvsignup = findViewById(R.id.tvSignup);
        outputemail = findViewById(R.id.editTextTextEmailAddress);
        outputpassword = findViewById(R.id.editTextTextPassword);
        btnsignin = findViewById(R.id.buttonsignin);

        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        sessionManager = new SharedPreferencesManager(this);

        // Check session and redirect to Home if logged in
        if (sessionManager.isLoggedIn()) {
            redirectToHomeScreen();
        }

        // Handle Sign Up button click
        tvsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignIn.this, SignUp.class);
                startActivity(intent);
            }
        });

        // Handle Sign In button click
        btnsignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLogin();
            }
        });
    }

    private void performLogin() {
        String email = outputemail.getText().toString();
        String password = outputpassword.getText().toString();

        if (!email.matches(emailPattern)) {
            outputemail.setError("Enter a valid email");
        } else if (password.isEmpty() || password.length() < 6) {
            outputpassword.setError("Please enter a valid password");
        } else {
            progressDialog.setMessage("Please wait while Logging in...");
            progressDialog.setTitle("Login");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();
                        Toast.makeText(SignIn.this, "Login successful", Toast.LENGTH_SHORT).show();

                        // Save login state
                        sessionManager.setLogin(true);

                        // Redirect to Home screen
                        redirectToHomeScreen();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(SignIn.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void redirectToHomeScreen() {
        Intent intent = new Intent(SignIn.this, Home.class); // Replace with your home screen activity
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
