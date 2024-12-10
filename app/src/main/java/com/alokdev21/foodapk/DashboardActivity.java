package com.alokdev21.foodapk;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.alokdev21.foodapk.SignIn;
import com.google.firebase.auth.FirebaseAuth;

public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Find the ImageView by its ID
        ImageView optionsMenu = findViewById(R.id.options_menu);

        // Set the OnClickListener to make it clickable
        optionsMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("DashboardActivity", "ImageView clicked!");  // Log click event

                // Create PopupMenu
                PopupMenu popupMenu = new PopupMenu(DashboardActivity.this, v);
                getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());

                // Force show icons on Android 10 and above
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    popupMenu.setForceShowIcon(true);
                }

                // Handle menu item click
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.menu_sign_out) {
                            signOut();
                            return true;
                        }
                        return false;
                    }
                });

                // Show the PopupMenu
                popupMenu.show();
            }
        });
    }

    // Method to handle sign out
    private void signOut() {
        // Perform Firebase sign-out
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(this, "Logging out Successfully", Toast.LENGTH_SHORT).show();

        // Redirect to SignIn activity after logout is complete
        finish();  // Close current activity

        // Start SignIn activity
        Intent intent = new Intent(DashboardActivity.this, SignIn.class);  // Replace with your SignIn activity class name
        startActivity(intent);
    }
}
