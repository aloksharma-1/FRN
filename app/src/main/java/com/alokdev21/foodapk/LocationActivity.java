package com.alokdev21.foodapk;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.alokdev21.foodapk.utils.SnackbarHelper;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class LocationActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private GoogleMap mMap;
    private EditText editCustomLocation;
    private Button btnGetAddress;
    private FloatingActionButton btnNext;
    private FirebaseFirestore db;
    private LatLng currentLocation;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        editCustomLocation = findViewById(R.id.editCustomLocation);
        btnNext = findViewById(R.id.btnNext);
        btnGetAddress = findViewById(R.id.btnGetAddress);
        db = FirebaseFirestore.getInstance();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Initialize Map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        btnGetAddress.setOnClickListener(v -> {
            String customLocation = editCustomLocation.getText().toString().trim();
            if (!customLocation.isEmpty()) {
                LatLng latLng = getLocationFromAddress(customLocation);
                if (latLng != null) {
                    mMap.clear(); // Clear previous markers
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                    mMap.addMarker(new MarkerOptions().position(latLng).title("Custom Location"));
                } else {
                    SnackbarHelper.showSnackbar(this, findViewById(android.R.id.content),
                            "Location not found", ContextCompat.getColor(this, R.color.snackbar_error));
                }
            } else {
                SnackbarHelper.showSnackbar(this, findViewById(android.R.id.content),
                        "Please enter a location", ContextCompat.getColor(this, R.color.snackbar_error));
            }
        });

        btnNext.setOnClickListener(v -> {
            String address = null;
            if (!editCustomLocation.getText().toString().trim().isEmpty()) {
                address = getAddressFromEditText(editCustomLocation.getText().toString().trim());
            } else if (currentLocation != null) {
                address = getAddressFromLatLng(currentLocation);
            }

            if (address != null) {
                saveAddressToFirestore(address);
            } else {
                SnackbarHelper.showSnackbar(this, findViewById(android.R.id.content),
                        "Unable to retrieve address", ContextCompat.getColor(this, R.color.snackbar_error));
            }
        });

        requestUserLocation();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        enableUserLocation();
    }

    private void enableUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }
    }

    private void requestUserLocation() {
        // Create LocationRequest using the new LocationRequest.Builder
        LocationRequest locationRequest = new LocationRequest.Builder(10000) // interval: 10 seconds
                .setMinUpdateIntervalMillis(5000)  // fastest interval: 5 seconds
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)  // high accuracy priority
                .build();

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult != null && locationResult.getLastLocation() != null) {
                        currentLocation = new LatLng(locationResult.getLastLocation().getLatitude(),
                                locationResult.getLastLocation().getLongitude());
                        if (mMap != null) {
                            mMap.clear(); // Clear previous markers
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
                            mMap.addMarker(new MarkerOptions().position(currentLocation).title("Your Location"));
                        }
                    }
                }
            }, getMainLooper());
        } else {
            SnackbarHelper.showSnackbar(this, findViewById(android.R.id.content),
                    "Location permission is required for accurate tracking", ContextCompat.getColor(this, R.color.snackbar_error));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableUserLocation();
                requestUserLocation(); // Start location updates once permission is granted
            } else {
                SnackbarHelper.showSnackbar(this, findViewById(android.R.id.content),
                        "Location permission denied", ContextCompat.getColor(this, R.color.snackbar_error));
            }
        }
    }

    private LatLng getLocationFromAddress(String addressString) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(addressString, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                return new LatLng(address.getLatitude(), address.getLongitude());
            }
        } catch (IOException e) {
            e.printStackTrace();
            SnackbarHelper.showSnackbar(this, findViewById(android.R.id.content),
                    "Error fetching location", ContextCompat.getColor(this, R.color.snackbar_error));
        }
        return null;
    }

    private String getAddressFromEditText(String addressString) {
        return getLocationFromAddress(addressString) != null ? addressString : null;
    }

    private String getAddressFromLatLng(LatLng latLng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                StringBuilder fullAddress = new StringBuilder();
                for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                    fullAddress.append(address.getAddressLine(i));
                    if (i < address.getMaxAddressLineIndex()) {
                        fullAddress.append(", ");
                    }
                }
                return fullAddress.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
            SnackbarHelper.showSnackbar(this, findViewById(android.R.id.content),
                    "Error fetching address", ContextCompat.getColor(this, R.color.snackbar_error));
        }
        return null;
    }

    private void saveAddressToFirestore(String address) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            SnackbarHelper.showSnackbar(this, findViewById(android.R.id.content),
                    "User not logged in", ContextCompat.getColor(this, R.color.snackbar_error));
            return;
        }

        String userId = user.getUid();
        Map<String, Object> addressMap = new HashMap<>();
        addressMap.put("address", address);

        db.collection("users").document(userId)
                .update(addressMap)
                .addOnSuccessListener(aVoid -> {
                    SnackbarHelper.showSnackbar(this, findViewById(android.R.id.content),
                            "Address saved successfully", ContextCompat.getColor(this, R.color.snackbar_success));
                    // Redirect to the Home activity and clear the activity stack
                    Intent homeIntent = new Intent(LocationActivity.this, Home.class);
                    homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(homeIntent);
                    finish(); // Close LocationActivity
                })
                .addOnFailureListener(e -> {
                    SnackbarHelper.showSnackbar(this, findViewById(android.R.id.content),
                            "Error saving address", ContextCompat.getColor(this, R.color.snackbar_error));
                });
    }
}
