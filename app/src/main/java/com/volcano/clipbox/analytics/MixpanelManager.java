package com.volcano.clipbox.analytics;

import android.provider.Settings;
import android.util.Log;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.volcano.clipbox.ClipBoxApplication;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Mixpanel Event Tracker
 */
public class MixpanelManager {
    private static final String TAG = MixpanelManager.class.getSimpleName();

    private static final MixpanelManager sInstance = new MixpanelManager();

    public static final String EVENT_APP_INSTALLED      = "App Installed";
    public static final String EVENT_DATABASE_UPGRADED  = "Database Upgraded";
    public static final String EVENT_EDIT_CLIP_ITEM     = "Edit Clip Item";
    public static final String EVENT_LOAD_CLIP_LIST     = "Load Clip List";
    public static final String EVENT_SHARE_APP          = "Share App";
    public static final String EVENT_SEARCH_CLIP        = "Search Clip";
    public static final String EVENT_AD_LOADED          = "Ad Loaded";
    public static final String EVENT_AD_FAILED_TO_LOAD  = "Ad Failed To Load";

    private static final String PARAM_CLIP_COUNT     = "Clip Count";
    private static final String PARAM_DEVICE_ID      = "Device Id";
    private static final String PARAM_NEW_VERSION    = "New Version";
    private static final String PARAM_OLD_VERSION    = "Old Version";

    private static final String TOKEN = "47756b211e509eb050195d4ad5d2e980";
    private MixpanelAPI mMixpanelAPI;
    private boolean mEnabled = true;

    private MixpanelManager() {
        mMixpanelAPI = MixpanelAPI.getInstance(ClipBoxApplication.getInstance(), TOKEN);
        mMixpanelAPI.identify(Settings.Secure.getString(ClipBoxApplication.getInstance().getContentResolver(),
                Settings.Secure.ANDROID_ID));
    }

    /**
     * @return Return Singleton Instance
     */
    public static MixpanelManager getInstance() {
        return sInstance;
    }

    /**
     * Push all queued Mixpanel events and People Analytics to Mixpanel servers
     */
    public void flush() {
        mMixpanelAPI.flush();
    }

    /**
     * Track an event without parameter
     * @param eventName The event name in order to track
     */
    public void track(String eventName) {
        if (mEnabled) {
            mMixpanelAPI.track(eventName, null);
        }
    }

    /**
     * Track an event with a set of name/value pairs that describe the properties of event
     * @param eventName The event name in order to track
     * @param properties A JSONObject containing the key value pairs of the properties to include in this event.
     */
    private void track(String eventName, JSONObject properties) {
        if (mEnabled) {
            mMixpanelAPI.track(eventName, properties);
        }
    }

    public void trackAppInstalledEvent() {
        final JSONObject params = new JSONObject();

        try {
            params.put(PARAM_DEVICE_ID, Settings.Secure.getString(ClipBoxApplication.getInstance().getContentResolver(),
                    Settings.Secure.ANDROID_ID));

            track(EVENT_APP_INSTALLED, params);
        }
        catch (JSONException e) {
            Log.e(TAG, "Error in creating json object");
        }

    }

    public void trackDatabaseUpgradedEvent(int oldVersion, int newVersion) {
        final JSONObject params = new JSONObject();

        try {
            params.put(PARAM_OLD_VERSION, oldVersion);
            params.put(PARAM_NEW_VERSION, newVersion);

            track(EVENT_DATABASE_UPGRADED, params);
        }
        catch (JSONException e) {
            Log.e(TAG, "Error in creating json object");
        }
    }

    public void trackLoadClipEvent(int clipCount) {
        final JSONObject params = new JSONObject();

        try {
            params.put(PARAM_CLIP_COUNT, clipCount);
            params.put(PARAM_DEVICE_ID, Settings.Secure.getString(ClipBoxApplication.getInstance().getContentResolver(),
                    Settings.Secure.ANDROID_ID));

            track(EVENT_LOAD_CLIP_LIST, params);
        }
        catch (JSONException e) {
            Log.e(TAG, "Error in creating json object");
        }
    }
}
