package com.example.zooapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Set up the title of the menu bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Settings");
        actionBar.setDisplayHomeAsUpEnabled(true);

        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        Switch switchButton = findViewById(R.id.directions_switch);
        boolean toggled = preferences.getBoolean("toggled", false); //default false

        if(toggled == true){
            switchButton.setChecked(true);
        }else{
            switchButton.setChecked(false);
        }
    }

    public void onDirectionsSwitchClick(View view) {
        //change variable, need to get it from location branch
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        Switch switchButton = findViewById(R.id.directions_switch);

        if(switchButton.isChecked()){
            editor.putBoolean("toggled", true);
            editor.commit();
        }else{
            editor.putBoolean("toggled", false);
            editor.commit();
        }
    }

    /**
     * When the options are created
     *
     * @param item Item that is created
     * @return Not used
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}