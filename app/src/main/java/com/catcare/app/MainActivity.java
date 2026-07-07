package com.catcare.app;

import android.Manifest;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.catcare.app.fragments.BadgesFragment;
import com.catcare.app.fragments.FeedingFragment;
import com.catcare.app.fragments.HealthFragment;
import com.catcare.app.fragments.HomeFragment;
import com.catcare.app.fragments.RemindersFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;

    private final ActivityResultLauncher<String> notifPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                    granted -> { });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getIntent() != null && getIntent().hasExtra("from_notification")) {
            bottomNav.setSelectedItemId(R.id.nav_feeding);
        }

        bottomNav = findViewById(R.id.bottom_nav);

        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selected = null;
            int id = item.getItemId();

            if (id == R.id.nav_home)           selected = new HomeFragment();
            else if (id == R.id.nav_feeding)   selected = new FeedingFragment();
            else if (id == R.id.nav_health)    selected = new HealthFragment();
            else if (id == R.id.nav_reminders) selected = new RemindersFragment();
            else if (id == R.id.nav_badges)    selected = new BadgesFragment();

            if (selected != null) {
                loadFragment(selected);
                return true;
            }
            return false;
        });

        askNotificationPermission();
        askExactAlarmPermission();
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                notifPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    private void askExactAlarmPermission() {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.S ||
                Build.VERSION.SDK_INT == Build.VERSION_CODES.S_V2) {
            AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (am != null && !am.canScheduleExactAlarms()) {
                Intent intent = new Intent(
                        android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);
            }
        }
    }
}