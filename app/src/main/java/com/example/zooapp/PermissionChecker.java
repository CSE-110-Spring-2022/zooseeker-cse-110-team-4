package com.example.zooapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;

import java.util.Arrays;

/**
 * This class is used to handle location permissions with the user
 */
public class PermissionChecker {

    final ActivityResultLauncher<String[]> requestPermissionLauncher;

    //Private fields
    private ComponentActivity activity;

    /**
     * Constructor
     *
     * @param activity the activity where permissions are required
     */
    public PermissionChecker(ComponentActivity activity) {
        this.activity = activity;
        requestPermissionLauncher = activity.registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), perms -> {
            perms.forEach((perm, isGranted) -> {
                Log.i("LAB7", String.format("Permission %s granted: %s", perm, isGranted));
            });
        });
    }

    /**
     * Checks if location permissions have been allowed
     *
     * @return returns if permissions are granted
     */
    boolean ensurePermissions() {
        var requiredPermissions = new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };

        var hasNoLocationPerms = Arrays.stream(requiredPermissions)
                .map(perm -> ContextCompat.checkSelfPermission(activity, perm))
                .allMatch(status -> status == PackageManager.PERMISSION_DENIED);

        if (hasNoLocationPerms) {
            requestPermissionLauncher.launch(requiredPermissions);
            return true;
        }
        return false;
    }
}
