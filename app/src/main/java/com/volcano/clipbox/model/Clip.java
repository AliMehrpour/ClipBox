package com.volcano.clipbox.model;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Clip entity.
 */
public class Clip {

    private static final String TAG = Clip.class.getName();

    public int id;
    public String value;
    public Date createDate;

    public Clip(int id, String value, Date createDate) {
        this.id = id;
        this.value = value;
        this.createDate = createDate;
    }

    public static String DateToString(Date date) {
        final DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.US);
        final Date today = Calendar.getInstance().getTime();
        return format.format(today);
    }

    public static Date StringToDate(String str) {
        Date date = null;
        final DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.US);
        try {
            date = format.parse(str);
        }
        catch (ParseException e) {
            Log.e(TAG, "Error in parse string to date.");
        }

        return date;
    }
}
