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

public class SignUp extends AppCompatActivity {
    TextView textviewlogin;
    EditText inputuser_name, inputpassword, inputemail, inputphone;
    Button btnregister;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    ProgressDialog progressDialog;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    SharedPreferencesManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize views
        textviewlogin = findViewById(R.id.tv8);
        inputuser_name = findViewById(R.id.edituser);
        inputpassword = findViewById(R.id.editTextpassword);
        inputemail = findViewById(R.id.editTextemail);
        inputphone = findViewById(R.id.editTextPhone);
        btnregister = findViewById(R.id.registerbtn);

        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        sessionManager = new SharedPreferencesManager(this);

        // Check if the user is already logged in
        if (sessionManager.isLoggedIn()) {
            redirectToHomeScreen();
        }

        textviewlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUp.this, SignIn.class);
                startActivity(intent);
            }
        });

        btnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performAuth();
            }
        });
    }

    private void performAuth() {
        String email = inputemail.getText().toString();
        String password = inputpassword.getText().toString();
        String phone = inputphone.getText().toString();
        String username = inputuser_name.getText().toString();

        if (!email.matches(emailPattern)) {
            inputemail.setError("Enter correct email");
        } else if (password.isEmpty() || password.length() < 6) {
            inputpassword.setError("Please enter a valid password");
        } else if (phone.isEmpty() || phone.length() < 10) {
            inputphone.setError("Please enter a valid phone number");
        } else if (username.isEmpty() || username.length() < 10 || username.matches(email)) {
            inputuser_name.setError("Please enter a valid username");
        } else {
            progressDialog.setMessage("Please wait while registration...");
            progressDialog.setTitle("Registration");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();
                        Toast.makeText(SignUp.this, "Registration is successful", Toast.LENGTH_SHORT).show();
                        sessionManager.setLogin(true); // Save login state
                        redirectToHomeScreen();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(SignUp.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void redirectToHomeScreen() {
        Intent intent = new Intent(SignUp.this, Home.class); // Replace with your home screen activity
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
