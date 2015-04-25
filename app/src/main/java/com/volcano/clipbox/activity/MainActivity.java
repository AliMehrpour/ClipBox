package com.volcano.clipbox.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.volcano.clipbox.ClipBoxApplication;
import com.volcano.clipbox.R;
import com.volcano.clipbox.Util.Utils;
import com.volcano.clipbox.analytics.MixpanelManager;
import com.volcano.clipbox.fragment.ClipListFragment;
import com.volcano.clipbox.service.ClipboardListenerService;

public class MainActivity extends ActionBarActivity {

    private ClipListFragment mFragment;
    private SearchView mSearchView;
    private MenuItem mSearchMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFragment = (ClipListFragment) getFragmentManager().findFragmentById(R.id.fragment_clipboard);

        // Start service
        startService(new Intent(getBaseContext(), ClipboardListenerService.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        MixpanelManager.getIntance().flush();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        mSearchMenu = menu.findItem(R.id.menu_search);

        mSearchView = (SearchView) mSearchMenu.getActionView();
        mSearchView.setQueryHint(getString(R.string.label_enter_word));
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (!mSearchView.isIconified()) {
                    mFragment.loadClips(s);
                }

                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();
        if(id == R.id.menu_about) {
            final LinearLayout layout = (LinearLayout) View.inflate(this, R.layout.dialog_about, null);
            final TextView textVersion = (TextView) layout.findViewById(R.id.text_app_version);
            textVersion.setText(Utils.getAppVersionName());

            new AlertDialogWrapper.Builder(this)
                    .setTitle(getString(R.string.label_about))
                    .setView(layout)
                    .setButtonColor(getResources().getColor(R.color.primary_dark))
                    .setNegativeButton(R.string.button_close_uppercase, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        }
        else if (id == R.id.menu_share) {
            MixpanelManager.getIntance().track(MixpanelManager.EVENT_SHARE_APP);

            final String googlePlayLink = String.format("https://play.google.com/store/apps/details?id=%s", ClipBoxApplication.getInstance().getPackageName());
            final String cafeBazarLink = String.format("http://cafebazaar.ir/app/%s/", ClipBoxApplication.getInstance().getPackageName());

            Utils.launchShareClient(this,
                    getString(R.string.share_subject, getString(R.string.app_name), Utils.getAppVersionName()),
                    getString(R.string.share_body, googlePlayLink, cafeBazarLink));
        }
        else if (id == R.id.menu_refresh) {
            mFragment.loadClips();
        }

        return true;
    }
}
