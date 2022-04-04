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

public class CalendarSettingActivity extends BaseFragment {

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


    private int calendarHeaderRow0 = -1;
    private int calendarHeaderRow = -1;
    private int miladiCalendarRow = -1;
    private int shamsiCalendarRow = -1;
    private int calendarInfoRow = -1;


    private int rowCount;
    //endregion

    @Override
    public boolean onFragmentCreate() {
        super.onFragmentCreate();

        rowCount = 0;

        calendarHeaderRow0 = rowCount++;
        calendarHeaderRow = rowCount++;
        miladiCalendarRow = rowCount++;
        shamsiCalendarRow = rowCount++;
        calendarInfoRow = rowCount++;
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

            if (position == miladiCalendarRow) {
                SharedStorage.selectedCalendar(GREGORIAN);
                FastDateFormat.getInstance().updateCalendar();
                parentLayout.rebuildAllFragmentViews(false, false);
            } else if (position == shamsiCalendarRow) {
                SharedStorage.selectedCalendar(IRANIAN);
                FastDateFormat.getInstance().updateCalendar();
                parentLayout.rebuildAllFragmentViews(false, false);
            }

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
            return position == shamsiCalendarRow
                    || position == miladiCalendarRow
                    || position == calendarInfoRow;
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


                    break;
                }
                case CHECK_CELL: {

                    break;
                }
                case HEADER: {
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    if (position == calendarHeaderRow) {
                        headerCell.setText(LocaleController.getString("SelectCalendar", R.string.SelectCalendar));
                    }
                    break;
                }
                case INFO_ROW: {
                    TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) holder.itemView;
                    if (position == calendarInfoRow) {
                        textInfoPrivacyCell.setText(LocaleController.getString("SelectCalendarInfo", R.string.SelectCalendarInfo));
                    }
                    break;
                }
                case RADIO_CELL: {
                    RadioCell radioCell = (RadioCell) holder.itemView;
                    if (position == miladiCalendarRow) {
                        radioCell.setText(LocaleController.getString("GregorianCalendar", R.string.GregorianCalendar
                                ),
                                SharedStorage.selectedCalendar() == GREGORIAN, true);
                    } else if (position == shamsiCalendarRow) {
                        radioCell.setText(LocaleController.getString("IranianCalendar", R.string.IranianCalendar),
                                SharedStorage.selectedCalendar() == IRANIAN, true);
                    }
                    break;
                }
                case SETTING_CELL: {
                    TextSettingsCell textCell = (TextSettingsCell) holder.itemView;

                    break;
                }
            }
        }

        @Override
        public int getItemViewType(int i) {
            if (i == calendarHeaderRow0) {
                return DIVIDER;
            }
            if (i == calendarHeaderRow
            ) {
                return HEADER;
            }
            if (i == calendarInfoRow) {
                return INFO_ROW;
            }
            if (i == shamsiCalendarRow ||
                    i == miladiCalendarRow) {
                return RADIO_CELL;
            }
            return CHECK_CELL;
        }

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
                    CalendarSettingActivity.StickerSizeCell.this.invalidate();
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
            messagesCell.invalidate();
            sizeBar.invalidate();
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
