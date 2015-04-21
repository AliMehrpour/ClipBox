package com.volcano.clipbox.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Start service after system boot
 */
public class BootCompletedIntentReceiver extends BroadcastReceiver {
    private static final String TAG = BootCompletedIntentReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Log.v(TAG, "Boot complete intent received.");

            final Intent pushIntent = new Intent(context, ClipboardListenerService.class);
            context.startService(pushIntent);
        }
    }
}
