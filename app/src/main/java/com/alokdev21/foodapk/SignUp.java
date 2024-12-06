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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {
    TextView textviewlogin;
    EditText inputuser_name, inputpassword, inputemail, inputphone;
    Button btnregister;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    ProgressDialog progressDialog;
    FirebaseAuth mAuth;
    FirebaseFirestore firestore;

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
        firestore = FirebaseFirestore.getInstance();

        // Check if the user is already logged in
        if (mAuth.getCurrentUser() != null) {
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
        String email = inputemail.getText().toString().trim();
        String password = inputpassword.getText().toString().trim();
        String phone = inputphone.getText().toString().trim();
        String username = inputuser_name.getText().toString().trim();

        if (!email.matches(emailPattern)) {
            inputemail.setError("Enter a valid email");
        } else if (password.isEmpty() || password.length() < 6) {
            inputpassword.setError("Password must be at least 6 characters");
        } else if (phone.isEmpty() || phone.length() < 10) {
            inputphone.setError("Enter a valid phone number");
        } else if (username.isEmpty() || username.length() < 3) {
            inputuser_name.setError("Enter a valid username (at least 3 characters)");
        } else {
            progressDialog.setMessage("Registering...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialog.dismiss();
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    saveUserData(user.getUid(), username, email, phone);
                                }
                            } else {
                                Toast.makeText(SignUp.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void saveUserData(String userId, String username, String email, String phone) {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("name", username);
        userMap.put("email", email);
        userMap.put("phone", phone);

        firestore.collection("users").document(userId)
                .set(userMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SignUp.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                            redirectToHomeScreen();
                        } else {
                            Toast.makeText(SignUp.this, "Failed to save user data: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void redirectToHomeScreen() {
        Intent intent = new Intent(SignUp.this, Home.class); // Replace with your home screen activity
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
