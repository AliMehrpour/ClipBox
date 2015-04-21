package com.volcano.clipbox.Util;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import com.volcano.clipbox.ClipBoxApplication;
import com.volcano.clipbox.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Application utilities
 */
public class Utils {
    private static final String TAG = Utils.class.getName();

    /**
     * @return Application version name
     */
    public static String getAppVersionName() {
        try {
            final ClipBoxApplication app = ClipBoxApplication.getInstance();
            return app.getPackageManager().getPackageInfo(app.getPackageName(), 0).versionName;
        }
        catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Can't get package info");
            throw new RuntimeException(e);
        }
    }

    public static Date getDate() {
        Calendar cal = Calendar.getInstance();
        return cal.getTime();
    }

    /**
     * Launch an share client
     * @param activity An Activity context for launching the email client
     * @param subject The subject
     * @param body The body
     */
    public static void launchShareClient(Activity activity, String subject, String body) {
        final Intent intent = new Intent(Intent.ACTION_SEND)
                .setType("text/plain")
                .putExtra(Intent.EXTRA_SUBJECT, subject)
                .putExtra(Intent.EXTRA_TEXT, body);

        try {
            activity.startActivity(Intent.createChooser(intent, activity.getString(R.string.label_share_choose_app)));
        }
        catch (ActivityNotFoundException e) {
            Log.e(TAG, "Not found any share app on your device");
            showToast(R.string.toast_load_share_client_failed);
        }
    }

    /**
     * Show toast message
     * @param id The if of string asset
     */
    public static void showToast(int id) {
        showToast(ClipBoxApplication.getInstance().getString(id));
    }

    /**
     * Show toast message
     * @param text The message
     */
    public static void showToast(CharSequence text) {
        Toast.makeText(ClipBoxApplication.getInstance(), text, Toast.LENGTH_SHORT).show();
    }
}
