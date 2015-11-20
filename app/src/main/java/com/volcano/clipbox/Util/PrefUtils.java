package com.volcano.clipbox.Util;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.volcano.clipbox.ClipBoxApplication;

/**
 * Utilities for SharedPreferences
 */
public final class PrefUtils {

    public static final String PREF_TRACK_APP_INSTALLED = "track_app_installed";
    public static final String PREF_DISPLAY_NEW_FEATURE_1_1_0 = "display_new_feature_1_1_0";
    public static final String PREF_DISPLAY_NEW_FEATURE_1_2_0 = "display_new_feature_1_2_0";

    public static SharedPreferences getPrefs() {
        return PreferenceManager.getDefaultSharedPreferences(ClipBoxApplication.getInstance());
    }

    public static boolean exists(String key) {
        return getPrefs().contains(key);
    }

    public static void remove(String key) {
        final SharedPreferences.Editor editor = getPrefs().edit();
        editor.remove(key);
        editor.commit();
    }

    public static String getPref(String key, String defaultValue) {
        return getPrefs().getString(key, defaultValue);
    }

    public static void setPref(String key, String value) {
        final SharedPreferences.Editor editor = getPrefs().edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static boolean getPref(String key, boolean defaultValue) {
        return getPrefs().getBoolean(key, defaultValue);
    }

    public static void setPref(String key, boolean value) {
        final SharedPreferences.Editor editor = getPrefs().edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static int getPref(String key, int defaultValue) {
        return getPrefs().getInt(key, defaultValue);
    }

    public static void setPref(String key, int value) {
        final SharedPreferences.Editor editor = getPrefs().edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static long getPref(String key, long defaultValue) {
        return getPrefs().getLong(key, defaultValue);
    }

    public static void setPref(String key, long value) {
        final SharedPreferences.Editor editor = getPrefs().edit();
        editor.putLong(key, value);
        editor.commit();
    }
}
