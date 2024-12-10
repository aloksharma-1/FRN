package com.alokdev21.foodapk;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.alokdev21.foodapk.utils.SnackbarHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {

    private static final int LOCATION_REQUEST_CODE = 101;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private ProgressBar progressBar;
    private EditText inputUserName, inputPassword, inputEmail, inputPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Enable Firestore offline persistence
        firestore.setFirestoreSettings(new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build());

        // Bind views
        progressBar = findViewById(R.id.progressBar);
        inputUserName = findViewById(R.id.edituser);
        inputPassword = findViewById(R.id.editTextpassword);
        inputEmail = findViewById(R.id.editTextemail);
        inputPhone = findViewById(R.id.editTextPhone);

        // Set up event listeners
        findViewById(R.id.registerbtn).setOnClickListener(v -> onSubmitButtonClick());
        findViewById(R.id.tv8).setOnClickListener(v -> navigateToSignIn());
        setUpPasswordVisibilityToggle();
    }

    private void navigateToSignIn() {
        startActivity(new Intent(SignUp.this, SignIn.class));
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setUpPasswordVisibilityToggle() {
        inputPassword.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (inputPassword.getCompoundDrawablesRelative()[2] != null) {
                    int drawableWidth = inputPassword.getCompoundDrawablesRelative()[2].getBounds().width();
                    if (event.getRawX() >= (inputPassword.getRight() - drawableWidth)) {
                        int selection = inputPassword.getSelectionEnd();
                        if (inputPassword.getTransformationMethod() instanceof android.text.method.PasswordTransformationMethod) {
                            inputPassword.setTransformationMethod(null);
                            inputPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.baseline_visibility_24, 0);
                        } else {
                            inputPassword.setTransformationMethod(android.text.method.PasswordTransformationMethod.getInstance());
                            inputPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.baseline_visibility_off_24, 0);
                        }
                        inputPassword.setSelection(selection);
                        return true;
                    }
                }
            }
            return false;
        });
    }

    private void onSubmitButtonClick() {
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();
        String phone = inputPhone.getText().toString().trim();
        String username = inputUserName.getText().toString().trim();

        if (isValidInput(email, password, phone, username)) {
            progressBar.setVisibility(View.VISIBLE);
            registerUser(email, password, username, phone); // Fast redirection
        }
    }

    private boolean isValidInput(String email, String password, String phone, String username) {
        String passwordPattern = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";

        if (!email.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")) {
            inputEmail.setError("Enter a valid email");
            return false;
        } else if (password.isEmpty() || !password.matches(passwordPattern)) {
            inputPassword.setError("Password must include uppercase, lowercase, number, special character, and be at least 8 characters long.");
            return false;
        } else if (phone.isEmpty() || phone.length() < 10) {
            inputPhone.setError("Enter a valid phone number");
            return false;
        } else if (username.isEmpty() || username.length() < 3) {
            inputUserName.setError("Enter a valid username (at least 3 characters)");
            return false;
        }
        return true;
    }

    private void registerUser(String email, String password, String username, String phone) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE); // Hide progress bar after operation
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // Save data in the background and return immediately
                            saveUserDataInBackground(user.getUid(), username, email, phone);
                            // Navigate quickly to LocationActivity
                            startLocationActivity();
                        }
                    } else {
                        SnackbarHelper.showSnackbar(this, findViewById(android.R.id.content),
                                "Registration failed: " + task.getException().getMessage(), ContextCompat.getColor(this, R.color.snackbar_error));
                    }
                });
    }

    // Save data asynchronously in a background thread (to improve responsiveness)
    private void saveUserDataInBackground(String userId, String username, String email, String phone) {
        new Thread(() -> {
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("name", username);
            userMap.put("email", email);
            userMap.put("phone", phone);
            userMap.put("role", "user"); // Default role is user

            // Save user data in Firestore without blocking UI
            firestore.collection("users").document(userId)
                    .set(userMap)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Optional: Inform user if needed
                            runOnUiThread(() -> SnackbarHelper.showSnackbar(SignUp.this, findViewById(android.R.id.content),
                                    "User data saved successfully!", ContextCompat.getColor(SignUp.this, R.color.snackbar_success)));
                        } else {
                            // Log error for debugging
                            runOnUiThread(() -> SnackbarHelper.showSnackbar(SignUp.this, findViewById(android.R.id.content),
                                    "Failed to save user data: " + task.getException().getMessage(), ContextCompat.getColor(SignUp.this, R.color.snackbar_error)));
                        }
                    });
        }).start();
    }

    private void startLocationActivity() {
        Intent intent = new Intent(SignUp.this, LocationActivity.class);
        startActivityForResult(intent, LOCATION_REQUEST_CODE); // Use startActivityForResult if needed
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOCATION_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            String address = data.getStringExtra("address");
            if (address != null) {
                SnackbarHelper.showSnackbar(this, findViewById(android.R.id.content),
                        "Address recorded: " + address, ContextCompat.getColor(this, R.color.snackbar_success));
                saveAddressToFirestore(address);
            } else {
                SnackbarHelper.showSnackbar(this, findViewById(android.R.id.content),
                        "Failed to get address", ContextCompat.getColor(this, R.color.snackbar_error));
            }
        }
    }

    private void saveAddressToFirestore(String address) {
        String userId = mAuth.getCurrentUser().getUid();
        Map<String, Object> addressMap = new HashMap<>();
        addressMap.put("address", address);

        firestore.collection("users").document(userId)
                .update(addressMap)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        SnackbarHelper.showSnackbar(this, findViewById(android.R.id.content),
                                "Address saved successfully!", ContextCompat.getColor(this, R.color.snackbar_success));
                    } else {
                        SnackbarHelper.showSnackbar(this, findViewById(android.R.id.content),
                                "Failed to save address: " + task.getException().getMessage(), ContextCompat.getColor(this, R.color.snackbar_error));
                    }
                });
    }
}
