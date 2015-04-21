package com.volcano.clipbox;

import android.app.Application;

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

        MixpanelManager.getIntance().track(MixpanelManager.EVENT_APP_LAUNCHED);
    }

    public static ClipBoxApplication getInstance() {
        return sInstance;
    }
}
