package com.volcano.clipbox.service;

import android.app.Service;
import android.content.ClipboardManager;
import android.content.ClipboardManager.OnPrimaryClipChangedListener;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.volcano.clipbox.R;
import com.volcano.clipbox.Util.Utils;
import com.volcano.clipbox.model.Clip;
import com.volcano.clipbox.provider.DatabaseHelper;

/**
 * ClipBoard Listener Service.
 */
public class ClipboardListenerService extends Service {
    private static final String TAG = ClipboardListenerService.class.getSimpleName();

    private static final Object mLock = new Object();
    private ClipboardManager mClipboard;
    private OnPrimaryClipChangedListener mPrimaryChangeListener;
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v(TAG, "ClipBox service started.");

        mClipboard = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
        mPrimaryChangeListener =
                new ClipboardManager.OnPrimaryClipChangedListener() {
                    public void onPrimaryClipChanged() {
                        if (!mClipboard.hasPrimaryClip()) {
                            return;
                        }

                        final Handler handler = new Handler();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                synchronized (mLock) {
                                    String clipString;
                                    try {
                                        clipString = String.valueOf(mClipboard.getPrimaryClip().getItemAt(0).getText());
                                        if (TextUtils.isEmpty(clipString) || clipString.equals("null")) {
                                            return;
                                        }

                                        // Check if last clip in database is equal to clipboard, Don't in database
                                        // Save in database
                                        final Clip lastClip = DatabaseHelper.getInstance().getLastClip();
                                        if (lastClip == null || !lastClip.value.equals(clipString)) {
                                            DatabaseHelper.getInstance().addClip(new Clip(0, clipString, Utils.getDate()));

                                            // Show toast
                                            handler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Utils.showToast(getString(R.string.toast_copied_clipbox));
                                                }
                                            });
                                        }
                                    } catch (Exception e) {
                                        Log.e(TAG, "Error in inserting clipboard item.");
                                    }
                                }
                            }
                        }).start();
                    }
                };
        mClipboard.addPrimaryClipChangedListener(mPrimaryChangeListener);

        return START_STICKY;
    }
    @Override
    public void onDestroy() {
        mClipboard.removePrimaryClipChangedListener(mPrimaryChangeListener);
        Log.v(TAG, "ClipBox service destroyed.");
        super.onDestroy();
    }
}
