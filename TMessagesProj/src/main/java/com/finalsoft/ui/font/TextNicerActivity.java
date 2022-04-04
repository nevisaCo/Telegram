package com.finalsoft.ui.font;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.webrtc.VideoDecoder;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.finalsoft.SharedStorage;

import static com.finalsoft.Config.TAG;


public class TextNicerActivity extends BaseFragment {

    private ListAdapter listAdapter;
    private RecyclerListView listView;
    MessagesStorage.IntCallback callback;

    //    private int enablePopupRow;
    private int type1Row;
    private int type2Row;
    private int type3Row;
    private int type4Row;
    private int type5Row;
    private int type6Row;
    private int type7Row;
    private int type8Row;
    private int nicerDesRow;

    private int rowCount = 0;

    private int basicFont;

    public TextNicerActivity(MessagesStorage.IntCallback callback) {
        this.callback = callback;
    }


    @Override
    public boolean onFragmentCreate() {
        super.onFragmentCreate();

//        enablePopupRow = rowCount++;
        type1Row = rowCount++;
        type2Row = rowCount++;
        type3Row = rowCount++;
        type4Row = rowCount++;
        type5Row = rowCount++;
        type6Row = rowCount++;
        type7Row = rowCount++;
        type8Row = rowCount++;
        nicerDesRow = rowCount++;

        return true;
    }

    @Override
    public View createView(Context context) {
        basicFont = SharedStorage.basicFont();
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setAllowOverlayTitle(true);
        actionBar.setTitle(LocaleController.getString("TextNicer", R.string.TextNicer));
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                }
            }
        });

        fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));

        listAdapter = new ListAdapter(context);

        listView = new RecyclerListView(context);
        listView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        listView.setVerticalScrollBarEnabled(false);
        frameLayout.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        listView.setAdapter(listAdapter);

        listView.setOnItemClickListener((view, position) -> {
            Log.i(TAG, "createView: niceId:" + position);
            if (view instanceof FontSettingsCell) {
                SharedStorage.basicFont(position);
                finishFragment();
                callback.run(position);
            }
        });
        return fragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    //Adapter
    private class ListAdapter extends RecyclerListView.SelectionAdapter {

        private Context mContext;
        String[] items = {
                LocaleController.getString("Normal", R.string.Normal),
                LocaleController.getString("nicer1", R.string.nicer1),
                LocaleController.getString("nicer2", R.string.nicer2),
                LocaleController.getString("nicer3", R.string.nicer3),
                LocaleController.getString("nicer4", R.string.nicer4),
                LocaleController.getString("nicer5", R.string.nicer5),
                LocaleController.getString("nicer6", R.string.nicer6),
                LocaleController.getString("nicer7", R.string.nicer7),
        };

        public ListAdapter(Context context) {
            mContext = context;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int position = holder.getAdapterPosition();
            if (position == nicerDesRow) {
                return false;
            }
            return true;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            switch (viewType) {
                case 0:
                 /*   view = new TextCheckCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 1:
                    view = new ShadowSectionCell(mContext);
                    break;
                case 2:*/
                    view = new FontSettingsCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 1:
                    view = new TextInfoPrivacyCell(mContext);
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 0:
                   /* TextCheckCell checkCell = (TextCheckCell) holder.itemView;
                    checkCell.setTextAndCheck(LocaleController.getString("TextNicerPopup", R.string.TextNicerPopup), true, false);
                    break;
                case 2:*/
                    FontSettingsCell fontSettingsCell = (FontSettingsCell) holder.itemView;
                    fontSettingsCell.setFont("nicer");
                    fontSettingsCell.setText(items[position], true);
                    if (position == basicFont) {
                        fontSettingsCell.setBackgroundColor(Theme.getColor(Theme.key_chat_selectedBackground));
                    }
                    break;
                case 1:
                    TextInfoPrivacyCell privacyCell = (TextInfoPrivacyCell) holder.itemView;
                    if (position == nicerDesRow) {
                        privacyCell.setText(LocaleController.getString("TextNicerDes", R.string.TextNicerDes));
                        privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(mContext, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                    }
                    break;
            }
        }

        @Override
        public int getItemCount() {
            return rowCount;
        }

        @Override
        public int getItemViewType(int position) {
            /*if (position == enablePopupRow) {
                return 0;
            } else*/
            /*if (position == shadowRow) {
                return 1;
            } else*/
            if (position == nicerDesRow) {
                return 1;
            }
            return 0;
        }
    }
}
