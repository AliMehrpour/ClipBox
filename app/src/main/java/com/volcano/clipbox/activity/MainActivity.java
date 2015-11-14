package com.volcano.clipbox.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.volcano.clipbox.ClipBoxApplication;
import com.volcano.clipbox.Intents;
import com.volcano.clipbox.R;
import com.volcano.clipbox.analytics.MixpanelManager;
import com.volcano.clipbox.fragment.ClipListFragment;
import com.volcano.clipbox.service.ClipboardListenerService;
import com.volcano.clipbox.view.VlSearchView;

import org.json.JSONObject;

import ir.adad.client.AdListener;
import ir.adad.client.AdView;
import ir.adad.client.Adad;

/**
 * Main Activity
 */
public class MainActivity extends ActionBarActivity {

    private ClipListFragment mFragment;
    private VlSearchView mSearchView;
    private MenuItem mFavoriteMenu;

    private boolean mFavoriteLoaded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize ads
        Adad.initialize(ClipBoxApplication.getInstance());

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFragment = (ClipListFragment) getFragmentManager().findFragmentById(R.id.fragment_clipboard);

        // Start service
        startService(new Intent(getBaseContext(), ClipboardListenerService.class));

        if (savedInstanceState != null) {
            mFavoriteLoaded = savedInstanceState.getBoolean(Intents.KEY_FAVORITE);
        }

        // Setup ads
        final AdView adView = (AdView) findViewById(R.id.banner_ad_view);
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                MixpanelManager.getInstance().track(MixpanelManager.EVENT_AD_LOADED);
            }

            @Override
            public void onAdFailedToLoad() {
                MixpanelManager.getInstance().track(MixpanelManager.EVENT_AD_FAILED_TO_LOAD);
            }

            @Override
            public void onMessageReceive(JSONObject jsonObject) {

            }

            @Override
            public void onRemoveAdsRequested() {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mFavoriteLoaded) {
            mFragment.loadFavoriteClips();
            if (mFavoriteMenu != null) {
                mFavoriteMenu.setIcon(R.drawable.ic_action_star);
            }
        }
        else {
            mFragment.loadClips();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(Intents.KEY_FAVORITE, mFavoriteLoaded);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        MixpanelManager.getInstance().flush();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem searchMenu = menu.findItem(R.id.menu_search);
        mFavoriteMenu = menu.findItem(R.id.menu_favorite);

        mSearchView = (VlSearchView) searchMenu.getActionView();
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
        mSearchView.setSearchViewListener(new VlSearchView.SearchViewListener() {
            @Override
            public void onExpandSearch() {
                if (mFavoriteLoaded) {
                    mFavoriteMenu.setIcon(R.drawable.ic_action_star_empty);
                    mFavoriteLoaded = false;
                    mFragment.loadClips();
                }

                MixpanelManager.getInstance().track(MixpanelManager.EVENT_SEARCH_CLIP);
            }

            @Override
            public void onCollapseSearch() {

            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();

        if(id == R.id.menu_settings) {
            startActivity(new Intent(ClipBoxApplication.getInstance(), SettingActivity.class));
        }
        else if (id == R.id.menu_refresh) {
            mFragment.loadClips();
        }
        else if (id == R.id.menu_favorite) {
            if (mFavoriteLoaded) {
                mFragment.loadClips();
                mFavoriteMenu.setIcon(R.drawable.ic_action_star_empty);
            }
            else {
                mFragment.loadFavoriteClips();
                mFavoriteMenu.setIcon(R.drawable.ic_action_star);
            }

            mFavoriteLoaded = !mFavoriteLoaded;
        }

        return true;
    }
}
