package com.volcano.clipbox;

import android.app.Application;

import com.volcano.clipbox.Util.PrefUtils;
import com.volcano.clipbox.analytics.MixpanelManager;

/**
 * return an instance of ClipBoxApplication.
 */
public class ClipBoxApplication extends Application {

    private static ClipBoxApplication sInstance;

    public ClipBoxApplication() {
        super();
        sInstance= this;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        final boolean trackedAppInstalled = PrefUtils.getPref(PrefUtils.PREF_TRACK_APP_INSTALLED, true);
        if (trackedAppInstalled) {
            PrefUtils.setPref(PrefUtils.PREF_TRACK_APP_INSTALLED, false);
            MixpanelManager.getInstance().trackAppInstalledEvent();
        }
    }

    public static ClipBoxApplication getInstance() {
        return sInstance;
    }
}
