package com.volcano.clipbox.service;

import android.app.Dialog;
import android.app.Service;
import android.content.ClipboardManager;
import android.content.ClipboardManager.OnPrimaryClipChangedListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.volcano.clipbox.ClipBoxApplication;
import com.volcano.clipbox.R;
import com.volcano.clipbox.Util.PrefUtils;
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
    private static OnPrimaryClipChangedListener mPrimaryChangeListener;
    private String mPreviousText = "";

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v(TAG, "ClipBox service started.");

        mClipboard = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
        if (mPrimaryChangeListener == null) {
            mPrimaryChangeListener =
                    new ClipboardManager.OnPrimaryClipChangedListener() {
                        public void onPrimaryClipChanged() {
                            final String clipString = String.valueOf(mClipboard.getPrimaryClip().getItemAt(0).getText());
                            if (!mClipboard.hasPrimaryClip() || TextUtils.isEmpty(clipString) ||
                                    clipString.equals("null") || mPreviousText.equals(clipString)) {
                                return;
                            }

                            mPreviousText = clipString;
                            // Handle confirm copy setup
                            final boolean isConfirmCopy = PrefUtils.getPref(getString(R.string.preference_app_setting_confirm_copy_key), false);
                            if (isConfirmCopy) {
                                AlertDialogWrapper.Builder builder = new AlertDialogWrapper.Builder(ClipBoxApplication.getInstance())
                                        .setMessage(getString(R.string.alert_copy_clip))
                                        .setButtonColor(getResources().getColor(R.color.primary_dark))
                                        .setPositiveButton(R.string.button_copy_uppercase, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                copyToClipBox(clipString);
                                            }
                                        })
                                        .setNegativeButton(R.string.button_close_uppercase, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.dismiss();
                                                mPreviousText = "";
                                            }
                                        });

                                final Dialog dialog = builder.create();
                                dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                                dialog.show();
                            } else {
                                copyToClipBox(clipString);
                            }
                        }
                    };
            mClipboard.addPrimaryClipChangedListener(mPrimaryChangeListener);
        }
        return START_STICKY;
    }

    private void copyToClipBox(final String clipString) {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (mLock) {
                    try {
                        // Check if the last clip in database is equal to clipboard, Don't save
                        Clip lastClip = Clip.getLastClip();
                        if (lastClip == null) {
                            lastClip = DatabaseHelper.getInstance().getLastClip();
                            Clip.setLastCLip(lastClip);
                        }

                        if (lastClip == null || !lastClip.value.equals(clipString)) {
                            final Clip clip = new Clip(0, clipString, Utils.getDate(), false);
                            Clip.setLastCLip(clip);
                            DatabaseHelper.getInstance().addClip(clip);

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

    @Override
    public void onDestroy() {
        mClipboard.removePrimaryClipChangedListener(mPrimaryChangeListener);
        Log.v(TAG, "ClipBox service destroyed.");
        super.onDestroy();
    }
}