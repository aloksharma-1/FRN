package com.alokdev21.foodapk;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.alokdev21.foodapk.models.UserRole;
import com.alokdev21.foodapk.utils.SnackbarHelper;

public class SignIn extends AppCompatActivity {

    private static final String TAG = "SignIn";
    private static final String PREFS_NAME = "UserPrefs";
    private static final String KEY_USER_ROLE = "userRole";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USER_EMAIL = "userEmail";

    private TextView tvSignup, txtForgot;
    private EditText emailInput, passwordInput;
    private Button btnSignIn;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Customize ActionBar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#AC7A4A"))); // Example color
        }

        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize UI components
        tvSignup = findViewById(R.id.tvSignup);
        txtForgot = findViewById(R.id.forgottxt);
        emailInput = findViewById(R.id.editTextTextEmailAddress);
        passwordInput = findViewById(R.id.editTextTextPassword);
        btnSignIn = findViewById(R.id.buttonsignin);
        progressBar = findViewById(R.id.progressBar);

        // Check user session and redirect if needed
        if (auth.getCurrentUser() != null) {
            checkCachedRole();
        }

        // Set up listeners
        tvSignup.setOnClickListener(v -> startActivity(new Intent(SignIn.this, SignUp.class)));
        txtForgot.setOnClickListener(v -> startActivity(new Intent(SignIn.this, Forgot.class)));
        btnSignIn.setOnClickListener(v -> performLogin());
        setUpPasswordVisibilityToggle();
    }

    private void performLogin() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            SnackbarHelper.showSnackbar(this, findViewById(android.R.id.content),
                    "Please fill in all fields", ContextCompat.getColor(this, R.color.snackbar_error));
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnSignIn.setEnabled(false);

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    progressBar.setVisibility(View.GONE);
                    btnSignIn.setEnabled(true);

                    if (task.isSuccessful()) {
                        FirebaseUser currentUser = auth.getCurrentUser();
                        if (currentUser != null) {
                            saveUserSession(currentUser);
                            // Fetch and cache the user role in the background
                            fetchAndCacheUserRole(currentUser.getUid());
                        }
                    } else {
                        String errorMessage = task.getException() != null ? task.getException().getMessage() : "Sign-in failed.";
                        Log.e(TAG, "Sign-in failed: " + errorMessage);
                        SnackbarHelper.showSnackbar(this, findViewById(android.R.id.content), errorMessage,
                                ContextCompat.getColor(this, R.color.snackbar_error));
                    }
                });
    }

    private void checkCachedRole() {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String cachedRole = preferences.getString(KEY_USER_ROLE, null);
        String userId = preferences.getString(KEY_USER_ID, null);

        if (cachedRole != null) {
            Log.d(TAG, "Cached role found: " + cachedRole);
            if ("admin".equalsIgnoreCase(cachedRole)) {
                redirectToDashboard();
            } else if ("user".equalsIgnoreCase(cachedRole)) {
                redirectToHome();
            }
        } else {
            Log.d(TAG, "No cached role found.");
            // Optionally handle redirection if the session has expired or not found
            if (auth.getCurrentUser() == null) {
                Log.d(TAG, "User not logged in. Redirecting to sign-in.");
                // Redirect to the sign-in screen if needed
            }
        }
    }

    private void fetchAndCacheUserRole(String userId) {
        db.collection("users").document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            UserRole userRole = document.toObject(UserRole.class);

                            if (userRole != null) {
                                saveRoleToPreferences(userRole.getRole());

                                if ("admin".equalsIgnoreCase(userRole.getRole())) {
                                    redirectToDashboard();
                                } else if ("user".equalsIgnoreCase(userRole.getRole())) {
                                    redirectToHome();
                                }
                            } else {
                                Log.d(TAG, "User role is null. Defaulting to home.");
                                redirectToHome();
                            }
                        } else {
                            Log.d(TAG, "No role information found. Defaulting to home.");
                            redirectToHome();
                        }
                    } else {
                        Log.e(TAG, "Error fetching user role: " + task.getException().getMessage());
                        SnackbarHelper.showSnackbar(this, findViewById(android.R.id.content),
                                "Error retrieving user role.", ContextCompat.getColor(this, R.color.snackbar_error));
                        redirectToHome();
                    }
                });
    }

    private void saveRoleToPreferences(String role) {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_USER_ROLE, role);
        editor.apply();
        Log.d(TAG, "Role saved to preferences: " + role);
    }

    private void saveUserSession(FirebaseUser currentUser) {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_USER_ID, currentUser.getUid());
        editor.putString(KEY_USER_EMAIL, currentUser.getEmail());
        editor.apply();
        Log.d(TAG, "User session saved: " + currentUser.getEmail());
    }

    private void redirectToDashboard() {
        Intent intent = new Intent(SignIn.this, DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void redirectToHome() {
        Intent intent = new Intent(SignIn.this, Home.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setUpPasswordVisibilityToggle() {
        passwordInput.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (passwordInput.getCompoundDrawablesRelative()[2] != null) {
                    int drawableWidth = passwordInput.getCompoundDrawablesRelative()[2].getBounds().width();
                    if (event.getRawX() >= (passwordInput.getRight() - drawableWidth)) {
                        int selection = passwordInput.getSelectionEnd();
                        if (passwordInput.getTransformationMethod() instanceof android.text.method.PasswordTransformationMethod) {
                            passwordInput.setTransformationMethod(null);
                            passwordInput.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.baseline_visibility_24, 0);
                        } else {
                            passwordInput.setTransformationMethod(android.text.method.PasswordTransformationMethod.getInstance());
                            passwordInput.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.baseline_visibility_off_24, 0);
                        }
                        passwordInput.setSelection(selection);
                        return true;
                    }
                }
            }
            return false;
        });
    }
}
