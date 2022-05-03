package com.example.zooapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class DirectionsActivity extends AppCompatActivity {
    int currIndex = 0;
    String[] sample = {"Polar Bear Street", "Alligator Street", "Tiger Street"};
    String[] sample2 = {"Polar Bears", "Alligators", "Tigers"};
    public final String head = "Directions to ";
    public final String proceed = "Proceed to ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directions);

        TextView header = findViewById(R.id.directions_header);
        TextView directions = findViewById(R.id.directions_text);
        header.setText(head + sample2[currIndex]);
        directions.setText(proceed + sample[currIndex]);
    }

    public void onNextButtonClicked(View view) {
        if(currIndex == sample.length -1){
            runOnUiThread(() -> {
                Utilities.showAlert(this,"The Route is Completed");
            });
        }
        if (currIndex < sample.length - 1){
            currIndex++;
        }
        Button previous = findViewById(R.id.previous_button);
        previous.setVisibility(View.VISIBLE);

        TextView directions = findViewById(R.id.directions_text);
        TextView header = findViewById(R.id.directions_header);
        header.setText(head + sample2[currIndex]);
        directions.setText(proceed +sample[currIndex]);
    }

    public void onPreviousButtonClicked(View view) {
        Button previous = findViewById(R.id.previous_button);
        if(currIndex == 0){
            runOnUiThread(() -> {
                Utilities.showAlert(this,"Can't go back!");
            });

        }
        if (currIndex == 1){
            previous.setVisibility(View.INVISIBLE);
        }
        if (currIndex > 0){
            currIndex--;
        }

        TextView directions = findViewById(R.id.directions_text);
        TextView header = findViewById(R.id.directions_header);
        header.setText(head + sample2[currIndex]);
        directions.setText(proceed +sample[currIndex]);
    }
}