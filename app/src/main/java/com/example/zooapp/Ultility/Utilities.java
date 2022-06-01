package com.example.zooapp.Ultility;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.example.zooapp.Viewer.DirectionsActivity;

import java.util.Optional;

/**
 * This class is used to show alerts on the app screen
 */
public class Utilities {

    /**
     * Displays an alert on screen
     *
     * @param activity the activity the alert will appear in
     * @param message the message that will be displayed on the alert
     * @return AlertDialog
     */
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

    /**
     * Displays an alert on screen with a clickable yes or no option
     *
     * @param activity the activity the alert will appear in
     * @param message the message that will be displayed on the alert
     * @return AlertDialog
     */
    public static AlertDialog optionalAlert(Activity activity, String message){
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity);
        alertBuilder
                .setTitle("Alert!")
                .setMessage(message)
                .setCancelable(true);

        //Clicking the Yes option
        alertBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing but close the dialog
                DirectionsActivity.check = true;
                dialog.dismiss();
                DirectionsActivity.canCheckReplan = true;
                DirectionsActivity.replanAlertShown = false;
                DirectionsActivity.recentlyYesReplan = true;
            }
        });

        //Clicking the No option
        alertBuilder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // Do nothing but close the dialog
                DirectionsActivity.check = false;
                dialog.dismiss();
                DirectionsActivity.canCheckReplan = false;
                DirectionsActivity.replanAlertShown = false;
            }
        });
        AlertDialog alertDialog = alertBuilder.create();
        return alertDialog;

    }

    /**
     * Takes in a string and converts it to an Optional of type Integer
     *
     * @param str
     * @return Optional<Integer>
     */
    public static Optional<Integer> parseCount(String str){
        try{
            int maxCount = Integer.parseInt(str);
            return Optional.of(maxCount);
        } catch (NumberFormatException e){
            return Optional.empty();
        }
    }
}

