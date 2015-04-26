package com.volcano.clipbox.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.volcano.clipbox.ClipBoxApplication;
import com.volcano.clipbox.R;
import com.volcano.clipbox.Util.PrefUtils;
import com.volcano.clipbox.Util.Utils;
import com.volcano.clipbox.analytics.MixpanelManager;
import com.volcano.clipbox.model.Clip;
import com.volcano.clipbox.provider.DatabaseHelper;

import java.util.ArrayList;

/**
 * ClipList Fragment
 */
public class ClipListFragment extends Fragment {

    private ArrayList<Clip> mClips = new ArrayList<>();
    private ListView mClipList;
    private final ClipAdapter mClipAdapter = new ClipAdapter();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_clip_list,container);
        mClipList = (ListView) view.findViewById(R.id.list_Clip);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mClipList.setAdapter(mClipAdapter);
        mClipList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final Clip clip = mClips.get(position);
                new AlertDialogWrapper.Builder(getActivity())
                        .setMessage(R.string.alert_delete_clip)
                        .setButtonColor(getResources().getColor(R.color.primary_dark))
                        .setNegativeButton(android.R.string.cancel, null)
                        .setPositiveButton(R.string.button_delete_uppercase, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mClips.remove(clip);
                                DatabaseHelper.getInstance().deleteClip(clip);
                                mClipAdapter.notifyDataSetChanged();
                            }
                        }).show();

                return true;
            }
        });

        mClipList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final EditText clipEdit = (EditText) View.inflate(ClipBoxApplication.getInstance(),
                        R.layout.dialog_edit_clip, null);
                final Clip clip = mClips.get(position);
                clipEdit.setText(clip.value);
                clipEdit.setSelection(clip.value.length());

                new AlertDialogWrapper.Builder(getActivity())
                        .setTitle(getString(R.string.label_dialog_edit_clip))
                        .setView(clipEdit)
                        .setMessage(clip.value)
                        .setButtonColor(getResources().getColor(R.color.primary_dark))
                        .setNegativeButton(android.R.string.cancel, null)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final Handler handler = new Handler();
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        clip.createDate = Utils.getDate();
                                        clip.value = clipEdit.getText().toString();
                                        if (DatabaseHelper.getInstance().updateClip(clip)) {
                                            handler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    MixpanelManager.getInstance().track(MixpanelManager.EVENT_EDIT_CLIP_ITEM);

                                                    Utils.showToast(getString(R.string.label_clip_edited));
                                                    mClips.set(position, clip);
                                                    mClipAdapter.notifyDataSetChanged();
                                                }
                                            });
                                        }
                                    }
                                }).start();
                            }
                        })
                        .show();
            }
        });
    }

    public void loadClips() {
        loadClips(null);
    }

    public void loadClips(final String query) {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                mClips = DatabaseHelper.getInstance().getClips(query);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        mClipAdapter.notifyDataSetChanged();
                        if (PrefUtils.getPref(PrefUtils.PREF_DISPLAY_NEW_FEATURE_1_1_0, true)) {
                            if (mClips.size() > 0) {
                                mClipList.performItemClick(null, 0, 0);
                            }
                            PrefUtils.setPref(PrefUtils.PREF_DISPLAY_NEW_FEATURE_1_1_0, false);
                        }
                    }
                });

                MixpanelManager.getInstance().trackLoadClipEvent(mClips.size());
            }
        }).start();
    }

    public void loadFavoriteClips() {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                mClips = DatabaseHelper.getInstance().getFavoriteClips();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        mClipAdapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }

    private class ClipAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mClips.size();
        }

        @Override
        public Object getItem(int position) {
            return mClips.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ClipItem view;

            if ( convertView != null) {
                view = (ClipItem) convertView;
            }
            else {
                view = new ClipItem(getActivity());
            }
            view.setClip((Clip) getItem(position));

            return view;
        }
    }

    private class ClipItem extends RelativeLayout {
        private TextView mClipText;
        private TextView mShareButton;
        private TextView mDateText;
        private ImageButton mFavoriteButton;

        private Clip mClip;

        public ClipItem(Context context) {
            super(context);
            View.inflate(context, R.layout.list_item_clip, this);
            setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);

            mClipText = (TextView) findViewById(R.id.text_clip);
            mShareButton = (TextView) findViewById(R.id.button_share);
            mDateText = (TextView) findViewById(R.id.text_date);
            mFavoriteButton = (ImageButton) findViewById(R.id.button_favorite);

            mShareButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.launchShareClient(getActivity(), "", mClipText.getText().toString());
                }
            });

            mFavoriteButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (mClip.favorite) {
                        mFavoriteButton.setImageResource(R.drawable.ic_star_empty);
                    }
                    else {
                        mFavoriteButton.setImageResource(R.drawable.ic_star);
                    }

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            mClip.favorite = !mClip.favorite;
                            DatabaseHelper.getInstance().updateClip(mClip);

                            final int size = mClips.size();
                            for (int i = 0; i < size; i++) {
                                if (mClips.get(i).equals(mClip)) {
                                    mClips.set(i, mClip);
                                    break;
                                }
                            }
                        }
                    }).start();
                }
            });
        }

        public void setClip(Clip clip) {
            mClip = clip;
            mClipText.setText(clip.value);
            mDateText.setText(Utils.getTimeSpan(clip.createDate));
            mFavoriteButton.setImageResource(clip.favorite ? R.drawable.ic_star : R.drawable.ic_star_empty);
        }
    }
}
