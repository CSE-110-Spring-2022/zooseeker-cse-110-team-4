package com.example.zooapp.Viewer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;

import com.example.zooapp.R;

/**
 * The activity is used to change the type of directions displayed on screen
 */
public class SettingsActivity extends AppCompatActivity {

    /**
     * Sets up the information needed for this activity when it is created
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Set up the title of the menu bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Settings");
        actionBar.setDisplayHomeAsUpEnabled(true);

        //Set the switch to the state it was in the last time the app was run
        SharedPreferences preferences = getSharedPreferences("DIRECTIONS", MODE_PRIVATE);
        Switch switchButton = findViewById(R.id.directions_switch);
        boolean toggled = preferences.getBoolean("toggled", false);

        if(toggled == true){
            switchButton.setChecked(true);
        }else{
            switchButton.setChecked(false);
        }
    }

    /**
     * When the switch button is clicked, change the directions from detailed to brief or vice versa
     *
     * @param view
     */
    public void onDirectionsSwitchClick(View view) {

        SharedPreferences preferences = getSharedPreferences("DIRECTIONS", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        Switch switchButton = findViewById(R.id.directions_switch);

        //if the button is on detailed directions, clicking it will switch it to brief directions and vice versa
        if(switchButton.isChecked()){
            editor.putBoolean("toggled", true);
            editor.commit();
            DirectionsActivity.directionsDetailedText = true;
        }else{
            editor.putBoolean("toggled", false);
            editor.commit();
            DirectionsActivity.directionsDetailedText = false;
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