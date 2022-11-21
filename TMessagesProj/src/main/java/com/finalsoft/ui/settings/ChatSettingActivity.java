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
import org.telegram.messenger.UserConfig;
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

public class ChatSettingActivity extends BaseFragment {

    //region vars
    public static final int GREGORIAN = 0;
    public static final int IRANIAN = 1;
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

    private int bubbleMessageRow = -1;
    private int unlimitedPinRow = -1;
    private int openArchiveOnPullRow = -1;
    private int hideKeyboardOnChatScrollRow = -1;

    private int fullNumRow = -1;
    private int mutualContactRow = -1;

    private int currentAccountInChatRow = -1;

    private int bookmarkRow = -1;

    private int showGiftRow = -1;

    private int transparentStatusBarRow = -1;
    private int showCloudInChatRow = -1;

    private int stickerHeaderRow0 = -1;
    private int stickerHeaderRow = -1;
    private int stickerSizeRow = -1;
    private int unlimitedFavStickersRow = -1;

    private int privacyRow0 = -1;
    private int privacyRow = -1;
    private int showEditedMsgRow = -1;
    private int showDeletedMsgRow = -1;


    private int chatMenusHeaderRow0 = -1;
    private int chatMenusHeaderRow = -1;
    private int showMessageMenuAlertRow = -1;
    private int dialogBottomMenuItemSetting = -1;


    private int answeringHeaderRow0 = -1;
    private int answeringHeaderRow = -1;
    private int autoAnsweringRow = -1;
    private int autoAnswerIconRow = -1;


    private int rowCount;
    //endregion

    @Override
    public boolean onFragmentCreate() {
        super.onFragmentCreate();

        rowCount = 0;

        bubbleMessageRow = BuildVars.SHOW_BUBBLE_FEATURE ? rowCount++ : -1;
        unlimitedPinRow = BuildVars.UNLIMITED_PIN_FEATURE ? rowCount++ : -1;
        openArchiveOnPullRow = BuildVars.NEKO_FEATURE ? rowCount++ : -1;
        hideKeyboardOnChatScrollRow = BuildVars.NEKO_FEATURE ? rowCount++ : -1;
        mutualContactRow = BuildVars.MUTUAL_CONTACT_FEATURE ? rowCount++ : -1;
        bookmarkRow = rowCount++;
        fullNumRow = rowCount++;
        currentAccountInChatRow = BuildVars.INFO_IN_CHATS_FEATURE ? rowCount++ : -1;
        showGiftRow = BuildVars.DONATE_FEATURE ? rowCount++ : -1;
        transparentStatusBarRow = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? (BuildVars.TRANSPARENT_STATUSBAR_FEATURE ? rowCount++ : -1) : -1;
        showCloudInChatRow = rowCount++;

        if (BuildVars.STICKERS_FEATURE) {
            stickerHeaderRow0 = rowCount++;
            stickerHeaderRow = rowCount++;
            stickerSizeRow = rowCount++;
            unlimitedFavStickersRow = rowCount++;
        }
        if (BuildVars.CHAT_PRIVACY_FEATURE) {
            privacyRow0 = rowCount++;
            privacyRow = rowCount++;
            showEditedMsgRow = rowCount++;
            showDeletedMsgRow = rowCount++;
        }


        if (BuildVars.NEKO_FEATURE || BuildVars.DIALOG_BOTTOM_MENU_FEATURE) {
            chatMenusHeaderRow0 = rowCount++;
            chatMenusHeaderRow = rowCount++;
        }

        showMessageMenuAlertRow = BuildVars.NEKO_FEATURE ? rowCount++ : -1;
        dialogBottomMenuItemSetting = BuildVars.DIALOG_BOTTOM_MENU_FEATURE ? rowCount++ : -1;

        if (BuildVars.AUTO_ANSWER_FEATURE) {
            answeringHeaderRow0 = rowCount++;
            answeringHeaderRow = rowCount++;
            autoAnsweringRow = rowCount++;
            autoAnswerIconRow = rowCount++;
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

            if (position == fullNumRow) {
                boolean xx = SharedStorage.showFullNumber();
                SharedStorage.showFullNumber(!xx);
                LocaleController.getInstance().updateFullNumberStatus();
            } else if (position == currentAccountInChatRow) {
                boolean xx = SharedStorage.chatSettings(SharedStorage.keys.MESSAGE_HINT);
                SharedStorage.chatSettings(SharedStorage.keys.MESSAGE_HINT, !xx);
            } else if (position == showGiftRow) {
                boolean xx = SharedStorage.chatSettings(SharedStorage.keys.SHOW_GIFT);
                SharedStorage.chatSettings(SharedStorage.keys.SHOW_GIFT, !xx);
            }  else if (position == bookmarkRow) {
                boolean xx = SharedStorage.chatSettings(SharedStorage.keys.BOOKMARK);
                SharedStorage.chatSettings(SharedStorage.keys.BOOKMARK, !xx);
            } else if (position == mutualContactRow) {
                boolean xx = SharedStorage.chatSettings(SharedStorage.keys.MUTUAL_CONTACT);
                SharedStorage.chatSettings(SharedStorage.keys.MUTUAL_CONTACT, !xx);
            } else if (position == showEditedMsgRow) {
                boolean xx = SharedStorage.chatSettings(SharedStorage.keys.SHOW_EDITED);
                SharedStorage.chatSettings(SharedStorage.keys.SHOW_EDITED, !xx);
            } else if (position == showDeletedMsgRow) {
                boolean xx = SharedStorage.chatSettings(SharedStorage.keys.SHOW_DELETED);
                SharedStorage.chatSettings(SharedStorage.keys.SHOW_DELETED, !xx);
            } else if (position == bubbleMessageRow) {
                boolean xx = SharedStorage.showMessageBubble();
                SharedStorage.showMessageBubble(!xx);
            } else if (position == unlimitedPinRow) {
                boolean xx = SharedStorage.chatSettings(SharedStorage.keys.UNLIMITED_PIN);
                SharedStorage.chatSettings(SharedStorage.keys.UNLIMITED_PIN, !xx);
            } else if (position == openArchiveOnPullRow) {
                boolean xx = SharedStorage.chatSettings(SharedStorage.keys.OPEN_ARCHIVE);
                SharedStorage.chatSettings(SharedStorage.keys.OPEN_ARCHIVE, !xx);
            } else if (position == hideKeyboardOnChatScrollRow) {
                boolean xx = SharedStorage.chatSettings(SharedStorage.keys.HIDE_KEYBOARD);
                SharedStorage.chatSettings(SharedStorage.keys.HIDE_KEYBOARD, !xx);
            } else if (position == stickerSizeRow) {
                showStickerSizeAlert();
            } else if (position == unlimitedFavStickersRow) {
                boolean xx = SharedStorage.chatSettings(SharedStorage.keys.UNLIMITED_STICKER_FAV);
                SharedStorage.chatSettings(SharedStorage.keys.UNLIMITED_STICKER_FAV, !xx);
            } else if (position == transparentStatusBarRow) {
                boolean xx = SharedStorage.chatSettings(SharedStorage.keys.TRANSPARENT_STATUS_BAR);
                SharedStorage.chatSettings(SharedStorage.keys.TRANSPARENT_STATUS_BAR, !xx);
                AndroidUtilities.runOnUIThread(() -> NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didSetNewTheme, false));
            } else if (position == showCloudInChatRow) {
                boolean xx = SharedStorage.chatSettings(SharedStorage.keys.CLOUD_IN_CHAT);
                SharedStorage.chatSettings(SharedStorage.keys.CLOUD_IN_CHAT, !xx);
            } else if (position == showMessageMenuAlertRow) {
                showMessageMenuAlert();
            } else if (position == dialogBottomMenuItemSetting) {
                presentFragment(new DialogBottomMenuSettingsActivity());
            } else if (position == autoAnswerIconRow) {
                boolean xx = SharedStorage.chatSettings(SharedStorage.keys.SHOW_AUTO_ANSWER);
                SharedStorage.chatSettings(SharedStorage.keys.SHOW_AUTO_ANSWER, !xx);
            }

            if (view instanceof NotificationsCheckCell) {
                NotificationsCheckCell checkCell = ((NotificationsCheckCell) view);
                boolean area = LocaleController.isRTL && x <= AndroidUtilities.dp(76) || !LocaleController.isRTL && x >= view.getMeasuredWidth() - AndroidUtilities.dp(76);
                if (!area) {
                    //text clicked
                    if (position == autoAnsweringRow) {
                        getAnsweringText(checkCell, context);
                    }
                } else {
                    //checkbox clicked
                    if (position == autoAnsweringRow) {
                        if (SharedStorage.answeringMachineText().length() > 0) {
                            boolean xx = SharedStorage.chatSettings(SharedStorage.keys.AUTO_ANSWER);
                            SharedStorage.chatSettings(SharedStorage.keys.AUTO_ANSWER, !xx);
                            checkCell.setChecked(!xx);
                            listAdapter.notifyDataSetChanged();
                        } else {
                            getAnsweringText(checkCell, context);
                        }
                    }
                }
            }

            listAdapter.notifyDataSetChanged();

            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.updateFragmentSettings);

        });


        return fragmentView;
    }

    private void getAnsweringText(NotificationsCheckCell checkCell, Context context) {

        String msg = SharedStorage.answeringMachineText();
        AlertDialog.Builder builder = AlertsCreator.createSimpleAlert(context,
                LocaleController.getString("AutoAnsweringMessage", R.string.AutoAnsweringMessage),
                "");

        EditTextCaption textCaption = new EditTextCaption(context,null);
        textCaption.setText(msg);
        textCaption.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(textCaption);
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), (dialog, which) -> {
            SharedStorage.answeringMachineText(textCaption.getText().toString());
            checkCell.setChecked(textCaption.getText().toString().length() > 0);
            listAdapter.notifyDataSetChanged();
        });
        builder.create().show();


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
            return position == fullNumRow
                    || position == currentAccountInChatRow
                    || position == showGiftRow

                    || position == bookmarkRow
                    || position == mutualContactRow
                    || position == showEditedMsgRow
                    || position == showDeletedMsgRow
                    || position == bubbleMessageRow
                    || position == unlimitedPinRow
                    || position == openArchiveOnPullRow
                    || position == hideKeyboardOnChatScrollRow
                    || position == showMessageMenuAlertRow
                    || position == dialogBottomMenuItemSetting
                    || position == stickerSizeRow
                    || position == unlimitedFavStickersRow
                    || position == transparentStatusBarRow
                    || position == showCloudInChatRow
                    || position == autoAnsweringRow
                    || position == autoAnswerIconRow;
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
                    boolean check = false;
                    if (position == autoAnsweringRow) {
                        text = LocaleController.getString("AutoAnswer", R.string.AutoAnswer);
                        check = SharedStorage.chatSettings(SharedStorage.keys.AUTO_ANSWER);
                        value = SharedStorage.answeringMachineText();
                    }
                    ncc.setTextAndValueAndCheck(text, value, check, true);
                    break;
                }
                case CHECK_CELL: {
                    TextCheckCell checkBoxCell = (TextCheckCell) holder.itemView;
                    if (position == fullNumRow) {
                        checkBoxCell.setTextAndValueAndCheck(LocaleController.getString("ShowFullNumbers", R.string.ShowFullNumbers),
                                LocaleController.getString("ShowFullNumberInfo", R.string.ShowFullNumberInfo),
                                SharedStorage.showFullNumber(), true, true);
                    } else if (position == currentAccountInChatRow) {
                        checkBoxCell.setTextAndValueAndCheck(LocaleController.getString("ShowCurrentUserInfoInChat", R.string.ShowCurrentUserInfoInChat),
                                LocaleController.getString("ShowAccountInChatInfo", R.string.ShowAccountInChatInfo),
                                SharedStorage.chatSettings(SharedStorage.keys.MESSAGE_HINT), true, true);
                    } else if (position == showGiftRow) {
                        checkBoxCell.setTextAndValueAndCheck(LocaleController.getString("ShowGift", R.string.ShowGift),
                                LocaleController.getString("ShowGiftInfo", R.string.ShowGiftInfo),
                                SharedStorage.chatSettings(SharedStorage.keys.SHOW_GIFT), true, true);
                    }  else if (position == bookmarkRow) {
                        checkBoxCell.setTextAndValueAndCheck(LocaleController.getString("GoToBookmarked", R.string.GoToBookmarked),
                                LocaleController.getString("MessageBookmarkInfo", R.string.MessageBookmarkInfo),
                                SharedStorage.chatSettings(SharedStorage.keys.BOOKMARK), true, true);
                    } else if (position == mutualContactRow) {
                        checkBoxCell.setTextAndValueAndCheck(LocaleController.getString("ShowMutualContact", R.string.ShowMutualContact),
                                LocaleController.getString("ShowMutualContactInfo", R.string.ShowMutualContactInfo),
                                SharedStorage.chatSettings(SharedStorage.keys.BOOKMARK), true, true);
                    } else if (position == showEditedMsgRow) {
                        checkBoxCell.setTextAndValueAndCheck(LocaleController.getString("ShowEditedMessage", R.string.ShowEditedMessage),
                                LocaleController.getString("ShowEditedMessageInfo", R.string.ShowEditedMessageInfo),
                                SharedStorage.chatSettings(SharedStorage.keys.SHOW_EDITED), true, true);
                    } else if (position == showDeletedMsgRow) {
                        checkBoxCell.setTextAndValueAndCheck(LocaleController.getString("ShowDeletedMessage", R.string.ShowDeletedMessage),
                                LocaleController.getString("ShowDeletedMessageInfo", R.string.ShowDeletedMessageInfo),
                                SharedStorage.chatSettings(SharedStorage.keys.SHOW_DELETED), true, true);
                    } else if (position == bubbleMessageRow) {
                        checkBoxCell.setTextAndValueAndCheck(
                                LocaleController.getString("ShowMessageBubble", R.string.ShowMessageBubble),
                                LocaleController.getString("ShowMessageBubbleInfo", R.string.ShowMessageBubbleInfo),
                                SharedStorage.showMessageBubble(), true, true);
                    }  else if (position == unlimitedPinRow) {
                        checkBoxCell.setTextAndValueAndCheck(
                                LocaleController.getString("UnlimitedPinnedDialogs", R.string.UnlimitedPinnedDialogs),
                                LocaleController.getString("UnlimitedPinnedDialogsAbout", R.string.UnlimitedPinnedDialogsAbout),
                                SharedStorage.chatSettings(SharedStorage.keys.UNLIMITED_PIN), true, true);
                    } else if (position == openArchiveOnPullRow) {
                        checkBoxCell.setTextAndValueAndCheck(
                                LocaleController.getString("OpenArchiveOnPull", R.string.OpenArchiveOnPull),
                                LocaleController.getString("OpenArchiveOnPullInfo", R.string.OpenArchiveOnPullInfo),
                                SharedStorage.chatSettings(SharedStorage.keys.OPEN_ARCHIVE), true, true);
                    } else if (position == hideKeyboardOnChatScrollRow) {
                        checkBoxCell.setTextAndValueAndCheck(
                                LocaleController.getString("HideKeyboardOnChatScroll", R.string.HideKeyboardOnChatScroll),
                                LocaleController.getString("HideKeyboardOnChatScrollInfo", R.string.HideKeyboardOnChatScrollInfo),
                                SharedStorage.chatSettings(SharedStorage.keys.HIDE_KEYBOARD), true, true);
                    } else if (position == unlimitedFavStickersRow) {
                        checkBoxCell.setTextAndValueAndCheck(
                                LocaleController.getString("UnlimitedFavoredStickers", R.string.UnlimitedFavoredStickers),
                                LocaleController.getString("UnlimitedFavoredStickersAbout", R.string.UnlimitedFavoredStickersAbout),
                                SharedStorage.chatSettings(SharedStorage.keys.UNLIMITED_STICKER_FAV), true, true);
                    } else if (position == transparentStatusBarRow) {
                        checkBoxCell.setTextAndCheck(
                                LocaleController.getString("TransparentStatusBar", R.string.TransparentStatusBar),
                                SharedStorage.chatSettings(SharedStorage.keys.TRANSPARENT_STATUS_BAR), true);
                    } else if (position == showCloudInChatRow) {
                        checkBoxCell.setTextAndCheck(
                                LocaleController.getString("SavedMessagesInChat", R.string.SavedMessagesInChat),
                                SharedStorage.chatSettings(SharedStorage.keys.CLOUD_IN_CHAT), true);
                    } else if (position == autoAnswerIconRow) {
                        checkBoxCell.setTextAndCheck(
                                LocaleController.getString("AutoAnswerShowIcon", R.string.AutoAnswerShowIcon),
                                SharedStorage.chatSettings(SharedStorage.keys.SHOW_AUTO_ANSWER), true);
                    }
                    break;
                }
                case HEADER: {
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                     if (position == stickerHeaderRow) {
                        headerCell.setText(LocaleController.getString("StickerSettings", R.string.StickerSettings));
                    } else if (position == privacyRow) {
                        headerCell.setText(LocaleController.getString("ChatPrivacy", R.string.ChatPrivacy));
                    } else if (position == chatMenusHeaderRow) {
                        headerCell.setText(LocaleController.getString("ChatMenusSettings", R.string.ChatMenusSettings));
                    } else if (position == answeringHeaderRow) {
                        headerCell.setText(LocaleController.getString("AnsweringMachine", R.string.AnsweringMachine));
                    }
                    break;
                }
                case INFO_ROW: {
                    TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) holder.itemView;

                    break;
                }
                case RADIO_CELL: {

                    break;
                }
                case SETTING_CELL: {
                    TextSettingsCell textCell = (TextSettingsCell) holder.itemView;
                   if (position == stickerSizeRow) {
                        textCell.setTextAndValue(LocaleController.getString("StickerSize", R.string.StickerSize),
                                String.valueOf(Math.round(SharedStorage.stickerSize())), true);
                    } else if (position == showMessageMenuAlertRow) {
                        textCell.setText(LocaleController.getString("MessageMenu", R.string.MessageMenu), true);
                    } else if (position == dialogBottomMenuItemSetting) {
                        textCell.setText(
                                LocaleController.getString("DialogBottomMenuSetting",
                                        R.string.DialogBottomMenuSetting),
                                true);
                    }
                    break;
                }
            }
        }

        @Override
        public int getItemViewType(int i) {
            if ( i == chatMenusHeaderRow0
                    || i == privacyRow0
                    || i == stickerHeaderRow0
                    || i == answeringHeaderRow0
                    ) {
                return DIVIDER;
            }
            if ( i == privacyRow
                    || i == stickerHeaderRow
                    || i == answeringHeaderRow
                    || i == chatMenusHeaderRow) {
                return HEADER;
            }
            if (i == stickerSizeRow || i == showMessageMenuAlertRow || i == dialogBottomMenuItemSetting) {
                return SETTING_CELL;
            }
            if (i == autoAnsweringRow) {
                return NOT_CHECK_CELL;
            }
            return CHECK_CELL;
        }

    }

    private void showMessageMenuAlert() {
        if (getParentActivity() == null) {
            return;
        }
        Context context = getParentActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(LocaleController.getString("MessageMenu", R.string.MessageMenu));

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout linearLayoutInviteContainer = new LinearLayout(context);
        linearLayoutInviteContainer.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(linearLayoutInviteContainer, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        for (MessageMenuController.Type item : MessageMenuController.Type.values()) {
            int tag_val = item.ordinal();
            DrawerActionCell textCell = new DrawerActionCell(context);
            textCell.setTextAndIcon(item.ordinal(),MessageMenuController.labels[tag_val],
                    MessageMenuController.icons[tag_val],0);
            textCell.showCheckBox(true);
            textCell.setChecked(MessageMenuController.is(item));
            textCell.setTag(tag_val);
            textCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            linearLayoutInviteContainer.addView(textCell, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
            textCell.setOnClickListener(v2 -> {
                int tag = (Integer) v2.getTag();
                MessageMenuController.Type type = MessageMenuController.Type.values()[tag];
                boolean status = MessageMenuController.update(type);
                Log.i(TAG, "showMessageMenuAlert: key:" + type + " , status:" + status);
                textCell.setChecked(!status);
            });
        }
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
        builder.setView(linearLayout);
        showDialog(builder.create());
    }

    private void showStickerSizeAlert() {
        if (getParentActivity() == null) {
            return;
        }
        Context context = getParentActivity();
        BottomSheet.Builder builder = new BottomSheet.Builder(context);
        builder.setApplyTopPadding(false);
        builder.setApplyBottomPadding(false);

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        FrameLayout titleLayout = new FrameLayout(context);
        linearLayout.addView(titleLayout);

        HeaderCell headerCell = new HeaderCell(context, Theme.key_windowBackgroundWhiteBlueHeader, 23, 15, false);
        headerCell.setHeight(47);
        headerCell.setText(LocaleController.getString("StickerSize", R.string.StickerSize));
        titleLayout.addView(headerCell);

        ActionBarMenuItem optionsButton = new ActionBarMenuItem(context, null, 0, Theme.getColor(Theme.key_sheet_other));
        optionsButton.setLongClickEnabled(false);
        optionsButton.setSubMenuOpenSide(2);
        optionsButton.setIcon(R.drawable.ic_ab_other);
        optionsButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(Theme.key_player_actionBarSelector), 1));
        optionsButton.addSubItem(1, R.drawable.msg_reset, LocaleController.getString("Reset", R.string.Reset));
        optionsButton.setOnClickListener(v -> optionsButton.toggleSubMenu());
        optionsButton.setContentDescription(LocaleController.getString("AccDescrMoreOptions", R.string.AccDescrMoreOptions));
        titleLayout.addView(optionsButton, LayoutHelper.createFrame(40, 40, Gravity.TOP | Gravity.RIGHT, 0, 8, 5, 0));

        LinearLayout linearLayoutInviteContainer = new LinearLayout(context);
        linearLayoutInviteContainer.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(linearLayoutInviteContainer, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        StickerSizeCell stickerSizeCell = new StickerSizeCell(context);
        optionsButton.setDelegate(id -> {
            if (id == 1) {
                SharedStorage.stickerSize(14.0f);
                stickerSizeCell.invalidate();
            }
        });
        linearLayoutInviteContainer.addView(stickerSizeCell, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        builder.setCustomView(linearLayout);
        showDialog(builder.create());
    }

    private class StickerSizeCell extends FrameLayout {

        private StickerSizePreviewMessagesCell messagesCell;
        private SeekBarView sizeBar;
        private int startStickerSize = 2;
        private int endStickerSize = 20;

        private TextPaint textPaint;

        public StickerSizeCell(Context context) {
            super(context);

            setWillNotDraw(false);

            textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            textPaint.setTextSize(AndroidUtilities.dp(16));

            sizeBar = new SeekBarView(context);
            sizeBar.setReportChanges(true);
            sizeBar.setDelegate(new SeekBarView.SeekBarViewDelegate() {
                @Override
                public void onSeekBarDrag(boolean stop, float progress) {
                    SharedStorage.stickerSize(startStickerSize + (endStickerSize - startStickerSize) * progress);
                    StickerSizeCell.this.invalidate();
                }

                @Override
                public void onSeekBarPressed(boolean pressed) {

                }
            });
            addView(sizeBar, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 38, Gravity.LEFT | Gravity.TOP, 9, 5, 43, 11));

            messagesCell = new StickerSizePreviewMessagesCell(context, parentLayout);
            addView(messagesCell, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.LEFT | Gravity.TOP, 0, 53, 0, 0));
        }

        @Override
        protected void onDraw(Canvas canvas) {
            textPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteValueText));
            canvas.drawText("" + Math.round(SharedStorage.stickerSize()), getMeasuredWidth() - AndroidUtilities.dp(39), AndroidUtilities.dp(28), textPaint);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            sizeBar.setProgress((SharedStorage.stickerSize() - startStickerSize) / (float) (endStickerSize - startStickerSize));
        }

        @Override
        public void invalidate() {
            super.invalidate();
            listAdapter.notifyItemChanged(stickerSizeRow);
            messagesCell.invalidate();
            sizeBar.invalidate();
        }
    }

    /*
    private void checkSensitive() {
        TLRPC.TL_account_getContentSettings req = new TLRPC.TL_account_getContentSettings();
        getConnectionsManager().sendRequest(req, (response, error) -> AndroidUtilities.runOnUIThread(() -> {
            if (error == null) {
                TLRPC.TL_account_contentSettings settings = (TLRPC.TL_account_contentSettings) response;
                sensitiveEnabled = settings.sensitive_enabled;
                sensitiveCanChange = settings.sensitive_can_change;
                int count = listView.getChildCount();
                ArrayList<Animator> animators = new ArrayList<>();
                for (int a = 0; a < count; a++) {
                    View child = listView.getChildAt(a);
                    RecyclerListView.Holder holder = (RecyclerListView.Holder) listView.getChildViewHolder(child);
                    int position = holder.getAdapterPosition();
                    if (position == disableFilteringRow) {
                        TextCheckCell checkCell = (TextCheckCell) holder.itemView;
                        checkCell.setChecked(sensitiveEnabled);
                        checkCell.setEnabled(sensitiveCanChange, animators);
                        if (sensitiveCanChange) {
                            if (!animators.isEmpty()) {
                                if (animatorSet != null) {
                                    animatorSet.cancel();
                                }
                                animatorSet = new AnimatorSet();
                                animatorSet.playTogether(animators);
                                animatorSet.addListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animator) {
                                        if (animator.equals(animatorSet)) {
                                            animatorSet = null;
                                        }
                                    }
                                });
                                animatorSet.setDuration(150);
                                animatorSet.start();
                            }
                        }

                    }
                }
            } else {
                AndroidUtilities.runOnUIThread(() -> AlertsCreator.processError(currentAccount, error, this, req));
            }
        }));
    }
*/

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
