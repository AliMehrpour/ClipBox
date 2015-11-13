package com.volcano.clipbox.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.volcano.clipbox.ClipBoxApplication;
import com.volcano.clipbox.R;
import com.volcano.clipbox.Util.Utils;
import com.volcano.clipbox.analytics.MixpanelManager;

import java.util.Collections;

/**
 * Application Settings Fragment.
 */
public class SettingFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        final Preference aboutPref = findPreference(getString(R.string.preference_other_about_key));
        aboutPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                final LinearLayout layout = (LinearLayout) View.inflate(getActivity(), R.layout.dialog_about, null);
                final TextView textVersion = (TextView) layout.findViewById(R.id.text_app_version);
                textVersion.setText(Utils.getAppVersionName());

                new AlertDialogWrapper.Builder(getActivity())
                        .setTitle(getString(R.string.label_about))
                        .setView(layout)
                        .setButtonColor(getResources().getColor(R.color.primary_dark))
                        .setNegativeButton(R.string.button_cancel_uppercase, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        })
                        .show();

                return false;
            }
        });

        final Preference feedbackPref = findPreference(getString(R.string.preference_other_feedback_key));
        feedbackPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Utils.launchEmailClient(getActivity(),
                        Collections.singletonList(getString(R.string.email_address_feedback)),
                        String.format(getString(R.string.email_subject_bug_report), getString(R.string.app_name), Utils.getAppVersionName()));
                return false;
            }
        });

        final Preference ratePref = findPreference(getString(R.string.preference_other_rate_key));
        ratePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Utils.launchPlayStore();
                return false;
            }
        });

        final Preference sharePref = findPreference(getString(R.string.preference_other_share_key));
        sharePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                MixpanelManager.getInstance().track(MixpanelManager.EVENT_SHARE_APP);

                final String googlePlayLink = String.format("https://play.google.com/store/apps/details?id=%s", ClipBoxApplication.getInstance().getPackageName());
                final String cafeBazarLink = String.format("http://cafebazaar.ir/app/%s/", ClipBoxApplication.getInstance().getPackageName());

                Utils.launchShareClient(getActivity(),
                        getString(R.string.share_subject, getString(R.string.app_name), Utils.getAppVersionName()),
                        getString(R.string.share_body, googlePlayLink, cafeBazarLink));
                return false;
            }
        });
    }

}
