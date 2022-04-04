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
import com.finalsoft.ui.voice.VoiceChangeHelper;

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

public class TranslateSettingActivity extends BaseFragment {

    //region vars
    private static final String TAG = Config.TAG + "csa";

    private static final int CHECK_CELL = 0;
    private static final int HEADER = 1;
    private static final int INFO_ROW = 2;
    private static final int RADIO_CELL = 3;
    private static final int SETTING_CELL = 4;
    private static final int TEXT_CELL = 5;
    private static final int DIVIDER = 6;
    private static final int NOT_CHECK_CELL = 7;
    private static final int SEEK_BAR = 8;

    private ListAdapter listAdapter;
    private RecyclerListView listView;
    @SuppressWarnings("FieldCanBeLocal")

    private int translateHeaderRow0 = -1;
    private int translateHeaderRow = -1;
    private int translationProviderRow = -1;
    private int activeTranslateTarget = -1;
    private int translateTargetLanguageRow = -1;
    private int activeTranslateToMeLanguage = -1;
    private int targetLanguageInChatRow = -1;
    private int translatePreviewInChat = -1;


    private int rowCount;
    //endregion

    @Override
    public boolean onFragmentCreate() {
        super.onFragmentCreate();

        rowCount = 0;


        if (BuildVars.TRANSLATE_FEATURE) {
            translateHeaderRow0 = rowCount++;
            translateHeaderRow = rowCount++;
            translationProviderRow = rowCount++;
            activeTranslateTarget = rowCount++;
            translateTargetLanguageRow = rowCount++;
            activeTranslateToMeLanguage = rowCount++;
            targetLanguageInChatRow = rowCount++;
            translatePreviewInChat = rowCount++;
        }

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
        actionBar.setTitle(LocaleController.getString("ChatSettings", R.string.ChatSettings));
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

            if (position == targetLanguageInChatRow) {
                boolean xx = SharedStorage.chatSettings(SharedStorage.keys.TARGET_TRANSLATE);
                SharedStorage.chatSettings(SharedStorage.keys.TARGET_TRANSLATE, !xx);
            } else if (position == translatePreviewInChat) {
                boolean xx = SharedStorage.chatSettings(SharedStorage.keys.TRANSLATE_PREVIEW);
                SharedStorage.chatSettings(SharedStorage.keys.TRANSLATE_PREVIEW, !xx);
            } else if (position == activeTranslateTarget) {
                boolean xx = SharedStorage.activeTranslateTarget();
                SharedStorage.activeTranslateTarget(!xx);
            } else if (position == translateTargetLanguageRow) {
                presentFragment(new LanguageSelectActivity(LanguageSelectActivity.Type.TRANSLATE));
            } else if (position == activeTranslateToMeLanguage) {
                presentFragment(new LanguageSelectActivity(LanguageSelectActivity.Type.TRANSLATE_2_ME));
            } else if (position == translationProviderRow) {
                showTranslateList(context);
            }

            listAdapter.notifyDataSetChanged();
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.updateFragmentSettings);

        });


        return fragmentView;
    }

    private void showTranslateList(Context context) {
        ArrayList<String> arrayList = new ArrayList<>();
        ArrayList<Integer> types = new ArrayList<>();
        arrayList.add(LocaleController.getString("ProviderGoogleTranslate", R.string.ProviderGoogleTranslate));
        types.add(1);
        arrayList.add(LocaleController.getString("ProviderGoogleTranslateCN", R.string.ProviderGoogleTranslateCN));
        types.add(2);
        arrayList.add(LocaleController.getString("ProviderLingocloud", R.string.ProviderLingocloud));
        types.add(3);
        arrayList.add(LocaleController.getString("ProviderGoogleTranslateWeb", R.string.ProviderGoogleTranslateWeb));
        types.add(-1);
        arrayList.add(LocaleController.getString("ProviderGoogleTranslateCNWeb", R.string.ProviderGoogleTranslateCNWeb));
        types.add(-2);
        arrayList.add(LocaleController.getString("ProviderBaiduFanyiWeb", R.string.ProviderBaiduFanyiWeb));
        types.add(-3);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(LocaleController.getString("TranslationProvider", R.string.TranslationProvider));
        final LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        builder.setView(linearLayout);

        for (int a = 0; a < arrayList.size(); a++) {
            RadioColorCell cell = new RadioColorCell(context);
            cell.setPadding(AndroidUtilities.dp(4), 0, AndroidUtilities.dp(4), 0);
            cell.setTag(a);
            cell.setCheckColor(Theme.getColor(Theme.key_radioBackground), Theme.getColor(Theme.key_dialogRadioBackgroundChecked));
            cell.setTextAndValue(arrayList.get(a), SharedStorage.translationProvider() == types.get(a));
            linearLayout.addView(cell);
            cell.setOnClickListener(v -> {
                Integer which = (Integer) v.getTag();
                SharedStorage.translationProvider(types.get(which));
                listAdapter.notifyItemChanged(translationProviderRow);
                builder.getDismissRunnable().run();
            });
        }
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        showDialog(builder.create());
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
            return position == targetLanguageInChatRow
                    || position == translatePreviewInChat
                    || position == translationProviderRow
                    || position == activeTranslateTarget
                    || position == activeTranslateToMeLanguage
                    || position == translateTargetLanguageRow;
        }

        @Override
        public int getItemCount() {
            return rowCount;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case NOT_CHECK_CELL: {
                    view = new NotificationsCheckCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                }
                case CHECK_CELL: {
                    view = new TextCheckCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                }
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
                case TEXT_CELL: {
                    view = new TextCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                }
                case DIVIDER: {
                    view = new ShadowSectionCell(mContext);
                    view.setBackgroundDrawable(Theme.getThemedDrawable(mContext, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                    break;
                }
                case SEEK_BAR: {
                    view = VoiceChangeHelper.getView(mContext, index -> VoiceChangeHelper.save(index));
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                }
                case SETTING_CELL:
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
                case NOT_CHECK_CELL: {
                    NotificationsCheckCell ncc = (NotificationsCheckCell) holder.itemView;
                    String text = "";
                    String value = "";
                    break;
                }
                case CHECK_CELL: {
                    TextCheckCell checkBoxCell = (TextCheckCell) holder.itemView;
              if (position == targetLanguageInChatRow) {
                        checkBoxCell.setTextAndValueAndCheck(
                                LocaleController.getString("ShowTargetTranslateInChat", R.string.ShowTargetTranslateInChat),
                                LocaleController.getString("ShowTargetTranslateInChatInfo", R.string.ShowTargetTranslateInChatInfo),
                                SharedStorage.chatSettings(SharedStorage.keys.TARGET_TRANSLATE),
                                true, true);
                    } else if (position == translatePreviewInChat) {
                        checkBoxCell.setTextAndValueAndCheck(
                                LocaleController.getString("TranslatePreviewInChat", R.string.TranslatePreviewInChat),
                                LocaleController.getString("TranslatePreviewInChatInfo", R.string.TranslatePreviewInChatInfo),
                                SharedStorage.chatSettings(SharedStorage.keys.TRANSLATE_PREVIEW),
                                true, true);
                    } else if (position == activeTranslateTarget) {
                        checkBoxCell.setTextAndValueAndCheck(
                                LocaleController.getString("TranslateTargetActive", R.string.TranslateTargetActive),
                                LocaleController.getString("TranslateTargetActiveInfo", R.string.TranslateTargetActiveInfo),
                                SharedStorage.activeTranslateTarget(), true, true);
                    }
                    break;
                }
                case HEADER: {
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                   if (position == translateHeaderRow) {
                        headerCell.setText(LocaleController.getString("Translate", R.string.Translate));
                    }
                    break;
                }
                case INFO_ROW: {
                    TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) holder.itemView;

                    break;
                }
                case RADIO_CELL: {
                    RadioCell radioCell = (RadioCell) holder.itemView;

                    break;
                }
                case SETTING_CELL: {
                    TextSettingsCell textCell = (TextSettingsCell) holder.itemView;
                    if (position == translationProviderRow) {
                        String value;
                        switch (SharedStorage.translationProvider()) {
                            case 1:
                                value = LocaleController.getString("ProviderGoogleTranslate", R.string.ProviderGoogleTranslate);
                                break;
                            case -1:
                                value = LocaleController.getString("ProviderGoogleTranslateWeb", R.string.ProviderGoogleTranslateWeb);
                                break;
                            case 2:
                                value = LocaleController.getString("ProviderGoogleTranslateCN", R.string.ProviderGoogleTranslateCN);
                                break;
                            case -2:
                                value = LocaleController.getString("ProviderGoogleTranslateCNWeb", R.string.ProviderGoogleTranslateCNWeb);
                                break;
                            case -3:
                                value = LocaleController.getString("ProviderBaiduFanyiWeb", R.string.ProviderBaiduFanyiWeb);
                                break;
                            case 3:
                            default:
                                value = LocaleController.getString("ProviderLingocloud", R.string.ProviderLingocloud);
                                break;
                        }
                        textCell.setTextAndValue(LocaleController.getString("TranslationProvider", R.string.TranslationProvider), value, false);
                    } else if (position == translateTargetLanguageRow) {
                        textCell.setTextAndValue(LocaleController.getString("TranslateTargetSelectLanguage", R.string.TranslateTargetSelectLanguage),
                                SharedStorage.translateShortName(), true);
                    } else if (position == activeTranslateToMeLanguage) {
                        textCell.setTextAndValue(LocaleController.getString("TranslateTargetToMeSelectLanguage", R.string.TranslateTargetToMeSelectLanguage),
                                SharedStorage.translateToMeShortName(), true);
                    }
                    break;
                }
            }
        }

        @Override
        public int getItemViewType(int i) {
            if ( i == translateHeaderRow0) {
                return DIVIDER;
            }
            if (i == translateHeaderRow                    ) {
                return HEADER;
            }


            if (i == translationProviderRow || i == translateTargetLanguageRow || i == activeTranslateToMeLanguage ) {
                return SETTING_CELL;
            }
            return CHECK_CELL;
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
