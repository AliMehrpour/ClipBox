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
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.volcano.clipbox.ClipBoxApplication;
import com.volcano.clipbox.R;
import com.volcano.clipbox.Util.Utils;
import com.volcano.clipbox.analytics.MixpanelManager;
import com.volcano.clipbox.model.Clip;
import com.volcano.clipbox.provider.DatabaseHelper;

import java.util.ArrayList;

/**
 * Clipboard Fragment
 */
public class ClipListFragment extends Fragment {

    private ArrayList<Clip> mClips = new ArrayList<>();
    private ListView mClipList;
    private final ClipAdapter mClipAdapter = new ClipAdapter();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_clipboard,container);
        mClipList = (ListView) view.findViewById(R.id.list_Clip);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mClipList.setAdapter(mClipAdapter);
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
                                                    MixpanelManager.getIntance().track(MixpanelManager.EVENT_EDIT_CLIP_ITEM);

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

    @Override
    public void onResume() {
        super.onResume();
        loadClips();
    }

    public void loadClips() {
        mClips = DatabaseHelper.getInstance().getClips();
        mClipAdapter.notifyDataSetChanged();

        MixpanelManager.getIntance().trackLoadClipEvent(mClips.size());
    }

    public void loadClips(String query) {
        mClips = DatabaseHelper.getInstance().getClips(query);
        mClipAdapter.notifyDataSetChanged();
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
        private TextView mDiscardButton;
        private Clip mClip;

        public ClipItem(Context context) {
            super(context);
            View.inflate(context, R.layout.list_item_clip, this);

            mClipText = (TextView) findViewById(R.id.text_clip);
            mShareButton = (TextView) findViewById(R.id.button_share);
            mDiscardButton = (TextView) findViewById(R.id.button_discard);

            mShareButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.launchShareClient(getActivity(), "", mClipText.getText().toString());
                }
            });

            mDiscardButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialogWrapper.Builder(getActivity())
                            .setMessage(R.string.alert_delete_clip)
                            .setButtonColor(getResources().getColor(R.color.primary_dark))
                            .setNegativeButton(android.R.string.cancel, null)
                            .setPositiveButton(R.string.button_delete_uppercase, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mClips.remove(mClip);
                                    DatabaseHelper.getInstance().deleteClip(mClip);
                                    mClipAdapter.notifyDataSetChanged();
                                }
                            }).show();
                }
            });
        }

        public void setClip(Clip clip) {
            mClipText.setText(clip.value);
            mClip = clip;
        }
    }
}
