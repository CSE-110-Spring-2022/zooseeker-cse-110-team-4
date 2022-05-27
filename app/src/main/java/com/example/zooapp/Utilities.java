package com.example.zooapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.Layout;

import java.util.Optional;

public class Utilities {
    public static AlertDialog showAlert(Activity activity, String message){
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity);

        alertBuilder
                .setTitle("Alert!")
                .setMessage(message)
                .setPositiveButton("Ok", (dialog, id) -> {
                    dialog.cancel();
                })
                .setCancelable(true);

        AlertDialog alertDialog = alertBuilder.create();
        return alertDialog;

    }

    public static AlertDialog optionalAlert(Activity activity, String message){
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity);
        alertBuilder
                .setTitle("Alert!")
                .setMessage(message)
                .setCancelable(true);

        alertBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // Do nothing but close the dialog
                DirectionsActivity.check = true;
                dialog.dismiss();
                DirectionsActivity.canCheckReplan = true;
                DirectionsActivity.replanAlertShown = false;
            }
        });

        alertBuilder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // Do nothing but close the dialog
                DirectionsActivity.check = false;
                dialog.dismiss();
                DirectionsActivity.canCheckReplan = false;
                DirectionsActivity.replanAlertShown = false;
                DirectionsActivity.recentlyNoReplan = true;
            }
        });
        AlertDialog alertDialog = alertBuilder.create();
        return alertDialog;

    }


    public static Optional<Integer> parseCount(String str){
        try{
            int maxCount = Integer.parseInt(str);
            return Optional.of(maxCount);
        } catch (NumberFormatException e){
            return Optional.empty();
        }
    }
}

