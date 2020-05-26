package com.example.rfiddetector;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.google.android.material.snackbar.Snackbar;

public abstract class Notification {

    public static void showPopUpWindow(Context context, String text) {
        try {
            Toast.makeText((Activity) context, text, Toast.LENGTH_SHORT).show();
        }
        catch (Exception e) {
            Log.d("Exception", e.getMessage());
        }
    }

    public static void showBottomPanel(Context context, String text) {
        try {
            View view = ((Activity) context).findViewById(R.id.fab);

            Snackbar.make(view, (CharSequence) text, Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        }
        catch (Exception e) {
            Log.d("Exception", e.getMessage());
        }
    }
}
