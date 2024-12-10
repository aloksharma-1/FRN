package com.alokdev21.foodapk;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Home extends AppCompatActivity {
    private ViewPager2 ViewPager;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize ViewPager2 and BottomNavigationView
        ViewPager = findViewById(R.id.viewpager);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set up the ViewPager with a custom adapter
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        ViewPager.setAdapter(adapter);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_menu) {
                ViewPager.setCurrentItem(0);
            } else if (id == R.id.nav_cart) {
                ViewPager.setCurrentItem(1);
            } else if (id == R.id.nav_profile) {
                ViewPager.setCurrentItem(2);
            }

            return true;
        });
        // Sync ViewPager2 with BottomNavigationView
        ViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0:
                        bottomNavigationView.setSelectedItemId(R.id.nav_menu);
                        break;
                    case 1:
                        bottomNavigationView.setSelectedItemId(R.id.nav_cart);
                        break;
                    case 2:
                        bottomNavigationView.setSelectedItemId(R.id.nav_profile);
                        break;
                }
            }
        });
    }
}

