package com.volcano.clipbox.Util;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.volcano.clipbox.ClipBoxApplication;
import com.volcano.clipbox.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Application utilities
 */
public class Utils {
    private static final String TAG = Utils.class.getName();

    /**
     * Convert {@link Date} to String
     * @param date The date
     */
    public static String DateToString(Date date) {
        final DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        return format.format(date);
    }

    /**
     * Convert {@link String} to Date
     * @param str The string date
     */
    public static Date StringToDate(String str) {
        Date date = null;
        final DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        try {
            date = format.parse(str);
        }
        catch (ParseException e) {
            Log.e(TAG, "Error in parse string to date.");
        }

        return date;
    }

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

    /**
     * @param num The number
     * @return True if num is greater than 0, false otherwise
     */
    public static boolean getBoolean(int num) {
        return num == 1;
    }

    /**
     * @return 1 if value is true, 0 otherwise
     */
    public static int getInt(boolean value) {
        return value ? 1 : 0;
    }

    /**
     * @return Return now time as Date
     */
    public static Date getDate() {
        Calendar cal = Calendar.getInstance();
        return cal.getTime();
    }

    /**
     * Return suitable format of Date
     * @param date The date
     * @return Formatted Date
     */
    public static String getTimeSpan(Date date) {
        final Date now = new Date();
        SimpleDateFormat sdfDate;
        final String dateFormatted;
        sdfDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        if (sdfDate.format(date).equals(sdfDate.format(now))) {
            dateFormatted = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(date);
        }
        else {
            sdfDate = new SimpleDateFormat("yyyy", Locale.getDefault());
            if (sdfDate.format(date).equals(sdfDate.format(now))) {
                dateFormatted = new SimpleDateFormat("EEE, MMM dd", Locale.getDefault()).format(date);
            }
            else {
                dateFormatted = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(date);
            }
        }

        return dateFormatted;
    }

    /**
     * Launch an email client
     * @param activity An Activity context for launching the email client
     * @param recipients A {@link java.util.List} of recipients
     * @param subject The subject
     */
    public static void launchEmailClient(Activity activity, List<String> recipients, String subject) {
        launchEmailClient(activity, recipients, subject, null);
    }

    /**
     * Launch an email client
     * @param activity An Activity context for launching the email client
     * @param recipients A {@link List} of recipients
     * @param subject The subject
     * @param body The body
     */
    public static void launchEmailClient(Activity activity, List<String> recipients, String subject, String body) {
        final Intent intent = new Intent(Intent.ACTION_SENDTO)
                .setData(Uri.parse("mailto:"))
                .putExtra(Intent.EXTRA_EMAIL, recipients.toArray(new String[recipients.size()]))
                .putExtra(Intent.EXTRA_SUBJECT, subject)
                .putExtra(Intent.EXTRA_TEXT, body);

        try {
            activity.startActivity(Intent.createChooser(intent, activity.getString(R.string.email_choose_app_title)));
        }
        catch (ActivityNotFoundException e) {
            Log.e(TAG, "Not found any email app on your device");
            showToast(R.string.toast_load_email_client_failed);
        }
    }

    /**
     * Launch play store client. If play store client isn't installed on device or disabled, open
     * play store in web browser
     */
    public static void launchPlayStore() {
        Intent intent = new Intent(Intent.ACTION_VIEW)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .setData(Uri.parse(String.format("market://details?id=%s", ClipBoxApplication.getInstance().getPackageName())));

        if (intent.resolveActivity(ClipBoxApplication.getInstance().getPackageManager()) != null) {
            ClipBoxApplication.getInstance().startActivity(intent);
        }
        else {
            intent.setData(Uri.parse(String.format("https://play.google.com/store/apps/details?id=%s", ClipBoxApplication.getInstance().getPackageName())));
            ClipBoxApplication.getInstance().startActivity(intent);
        }
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
