package com.finalsoft.ui.settings;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.text.InputType;
import android.text.TextPaint;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.finalsoft.Config;
import com.finalsoft.SharedStorage;
import com.finalsoft.controller.MessageMenuController;
import com.finalsoft.ui.StickerSizePreviewMessagesCell;
import com.finalsoft.ui.profile.JsonFileHelper;
import com.finalsoft.ui.voice.VoiceChangeHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.time.FastDateFormat;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.DrawerActionCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.NotificationsCheckCell;
import org.telegram.ui.Cells.RadioCell;
import org.telegram.ui.Cells.RadioColorCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.EditTextCaption;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SeekBarView;
import org.telegram.ui.LanguageSelectActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

public class FontSettingActivity extends BaseFragment {

    //region vars

    private static final String TAG = Config.TAG + "fsa";

    private static final int HEADER = 0;
    private static final int INFO_ROW = 1;
    private static final int RADIO_CELL = 2;


    private ListAdapter listAdapter;
    private RecyclerListView listView;
    String selected_font = SharedStorage.selectedFont();

    @SuppressWarnings("FieldCanBeLocal")
    //endregion
    private class Font {
        public String name;
        public String nick_name;
        public String ln;

        public Font(String name, String nick_name, String ln) {
            this.name = name;
            this.nick_name = nick_name;
            this.ln = ln;
        }

        public Font(String name, String nick_name) {
            this(name, nick_name, "00");
        }

        public String getName() {
            return name;
        }

        public String getNick_name() {
            return nick_name;
        }

        public String getLn() {
            return ln;
        }
    }

    ArrayList<Font> fonts = new ArrayList<>();

    @Override
    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        fonts.add(new Font("header_name", "", "00"));
        try {
            JSONArray jsonarray = new JSONArray(JsonFileHelper.loadJSONFromAsset("fonts/fonts.json"));
            String local = LocaleController.getCurrentLanguageShortName();
            for (int i = 0; i < jsonarray.length(); i++) {
                String name = jsonarray.getJSONObject(i).getString("file");
                String nick_name = jsonarray.getJSONObject(i).getString("title");
                String ln = jsonarray.getJSONObject(i).getString("ln");

                if (ln.equals(local) || ln.equals("en")) {
                    fonts.add(new Font(name, nick_name, ln));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        fonts.add(new Font("info", "", "zz"));
        Collections.sort(fonts, (font, t1) -> font.getLn().compareTo(t1.getLn()));

        return true;
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
    }

    @Override
    public View createView(Context context) {
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setAllowOverlayTitle(true);
        actionBar.setTitle(LocaleController.getString("SelectFont", R.string.SelectFont));
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                }
            }
        });

        listAdapter = new ListAdapter(context);

        fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));

        listView = new RecyclerListView(context);
        listView.setVerticalScrollBarEnabled(false);
        listView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        frameLayout.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener((view, position, x, y) -> {
            if (getParentActivity() == null) {
                return;
            }
            Font font = fonts.get(position);
            selected_font = font.getName();
            SharedStorage.selectedFont(selected_font);
            AndroidUtilities.updateFont();
            parentLayout.rebuildAllFragmentViews(false, false);

            listAdapter.notifyDataSetChanged();

            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.updateFragmentSettings);

        });


        return fragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (listAdapter != null) {
//            checkSensitive();
            listAdapter.notifyDataSetChanged();
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {

        private Context mContext;

        ListAdapter(Context context) {
            mContext = context;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int position = holder.getAdapterPosition();
            return position > 1;
        }

        @Override
        public int getItemCount() {
            return fonts.size();
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case HEADER:
                    view = new HeaderCell(mContext, 16);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case INFO_ROW:
                    view = new TextInfoPrivacyCell(mContext);
                    view.setBackgroundDrawable(Theme.getThemedDrawable(mContext, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                    break;
                case RADIO_CELL:
                    view = new RadioCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;

                default:
                    view = new TextSettingsCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case HEADER: {
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    headerCell.setText(LocaleController.getString("SelectFont", R.string.SelectFont));

                    break;
                }
                case INFO_ROW: {
                    TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) holder.itemView;
                    textInfoPrivacyCell.setText(LocaleController.getString("SelectFontInfo", R.string.SelectFontInfo));

                    break;
                }
                case RADIO_CELL: {
                    RadioCell radioCell = (RadioCell) holder.itemView;
                    radioCell.setText(fonts.get(position).getNick_name(),
                            selected_font.equals(fonts.get(position).getName()), true);
                    break;
                }
            }
        }

        @Override
        public int getItemViewType(int i) {
/*            if (i == 0) {
                return DIVIDER;
            }*/
            if (i == 0) {
                return HEADER;
            }
            if (i == fonts.size() - 1) {
                return INFO_ROW;
            }
            return RADIO_CELL;
        }

    }

    @Override
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        return new ArrayList<>(Arrays.asList(
                new ThemeDescription(listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class}, null, null, null, Theme.key_windowBackgroundWhite),
                new ThemeDescription(fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray),

                new ThemeDescription(actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault),
                new ThemeDescription(listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault),
                new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon),
                new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle),
                new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector),

                new ThemeDescription(listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector),

                new ThemeDescription(listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText),
                new ThemeDescription(listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteValueText),

                new ThemeDescription(listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow),
                new ThemeDescription(listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText4)
        ));
    }
}
