package com.alokdev21.foodapk;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;
import com.squareup.picasso.Picasso;

public class Profile_fragment extends Fragment {
    private TextView profileName, profileEmail, profilePhone, profileAddress;
    private ImageView profilePicture, editAddressIcon;
    private Button logoutButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    public Profile_fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.profile_fragment, container, false);

        // Initialize Firebase instances
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize UI elements
        profileName = view.findViewById(R.id.profileName);
        profileEmail = view.findViewById(R.id.profileEmail);
        profilePhone = view.findViewById(R.id.profilePhone);
        profileAddress = view.findViewById(R.id.profileAddress);
        profilePicture = view.findViewById(R.id.profile_image);
        logoutButton = view.findViewById(R.id.logoutButton);
        editAddressIcon = view.findViewById(R.id.editAddressIcon);

        // Check if the user is authenticated
        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            DocumentReference userRef = db.collection("users").document(userId);

            // Fetch user data from Firestore
            userRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Extract data from the document
                        String name = document.getString("name");
                        String email = document.getString("email");
                        String phone = document.getString("phone");
                        String address = document.getString("address");
                        String profileImageUrl = document.getString("profileImageUrl");

                        // Set data to the UI elements
                        profileName.setText(name);
                        profileEmail.setText(email);
                        profilePhone.setText("+91" + " " +phone);
                        profileAddress.setText((address != null ? address : "Not provided"));

                        // Load profile picture if available
                        if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                            Picasso.get().load(profileImageUrl).into(profilePicture);
                        } else {
                            profilePicture.setImageResource(R.drawable.profile); // Placeholder image
                        }
                    } else {
                        Toast.makeText(getContext(), "No such document found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Failed to fetch data: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            // Optionally redirect the user to a login activity
        }

        // Set up button click listeners
        setUpButtonListeners();

        return view;
    }

    private void setUpButtonListeners() {
        logoutButton.setOnClickListener(v -> {
            // Log out the user and show a toast message
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(getContext(), "Logging out Successfully", Toast.LENGTH_SHORT).show();

            // Redirect to SignIn activity after logout is complete
            getActivity().finish(); // Close the current activity
            Intent intent = new Intent(getActivity(), SignIn.class); // Replace with your SignIn activity class name
            startActivity(intent);
        });

        // Set up the edit icon click listener for the address
        editAddressIcon.setOnClickListener(v -> {
            showEditAddressDialog();
        });
    }

    private void showEditAddressDialog() {
        // Create a dialog for address editing
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Edit Address");

        // Create an EditText for manual input
        EditText input = new EditText(getContext());
        input.setText(profileAddress.getText().toString());
        builder.setView(input);

        // Set up dialog buttons
        builder.setPositiveButton("Save", (dialog, which) -> {
            String newAddress = input.getText().toString().trim();
            if (!newAddress.isEmpty()) {
                // Update the UI and Firestore database with the new address
                profileAddress.setText(newAddress);
                updateAddressInFirestore(newAddress);
            } else {
                Toast.makeText(getContext(), "Address cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void updateAddressInFirestore(String newAddress) {
        String userId = mAuth.getCurrentUser().getUid();
        DocumentReference userRef = db.collection("users").document(userId);

        // Update the address field in Firestore
        userRef.update("address", newAddress)
                .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Address updated successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to update address: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
