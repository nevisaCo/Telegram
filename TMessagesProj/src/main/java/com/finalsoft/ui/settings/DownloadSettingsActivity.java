package com.finalsoft.ui.settings;

import android.app.TimePickerDialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TimePicker;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.finalsoft.SharedStorage;
import com.finalsoft.dm.DownloadReceiver;
import com.finalsoft.helper.DownloadHelper;
//import com.mohamadamin.persianmaterialdatetimepicker.time.RadialPickerLayout;
//import com.mohamadamin.persianmaterialdatetimepicker.time.TimePickerDialog;

import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextDetailSettingsCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;

import java.util.Calendar;


public class DownloadSettingsActivity extends BaseFragment implements TimePickerDialog.OnTimeSetListener {

    private ListAdapter listAdapter;
//    boolean[] days = new boolean[]{true, true, true, true, true, true, true};

    private int rowCount = 0;

    private int enableDMRow;
    private int justTodayRow;
    private int activeDaysRow;
    private int startTimeRow;
    private int endTimeRow;
    private int enableWifiRow;
    private int disableWifiRow;
    private String[] dayNames = {
            LocaleController.getString("Saturday", R.string.Saturday),
            LocaleController.getString("Sunday", R.string.Sunday),
            LocaleController.getString("Monday", R.string.Monday),
            LocaleController.getString("Tuesday", R.string.Tuesday),
            LocaleController.getString("Wednesday", R.string.Wednesday),
            LocaleController.getString("Thursday", R.string.Thursday),
            LocaleController.getString("Friday", R.string.Friday)
    };

    @Override
    public boolean onFragmentCreate() {
        super.onFragmentCreate();

        enableDMRow = rowCount++;
        justTodayRow = rowCount++;
        activeDaysRow = rowCount++;
        startTimeRow = rowCount++;
        endTimeRow = rowCount++;
        enableWifiRow = rowCount++;
        disableWifiRow = rowCount++;

        return true;
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
    }

    @Override
    public View createView(final Context context) {
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setAllowOverlayTitle(true);
        actionBar.setTitle(LocaleController.getString("DownloadManager", R.string.DownloadManager));
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

        RecyclerListView listView = new RecyclerListView(context);
        listView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        listView.setVerticalScrollBarEnabled(false);
        frameLayout.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        listView.setAdapter(listAdapter);

        listView.setOnItemClickListener((view, position) -> {
            if (position == enableDMRow) {
                boolean downloadReceiver = SharedStorage.downloadModule(DownloadHelper.Modules.RECEIVER);
                if (downloadReceiver) {
                    new DownloadReceiver().cancelAlarm(ApplicationLoader.applicationContext);
                }
                SharedStorage.downloadModule(DownloadHelper.Modules.RECEIVER, !downloadReceiver);
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(!downloadReceiver);
                }
            } else if (position == justTodayRow) {
                boolean jt = SharedStorage.downloadModule(DownloadHelper.Modules.JUST_TODAY);
                SharedStorage.downloadModule(DownloadHelper.Modules.JUST_TODAY, !jt);
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(!jt);
                }
            } else if (position == activeDaysRow) {
                if (getParentActivity() == null) {
                    return;
                }
                final boolean[] maskValues = new boolean[7];
                BottomSheet.Builder builder = new BottomSheet.Builder(getParentActivity());
                builder.setApplyTopPadding(false);
                builder.setApplyBottomPadding(false);
                LinearLayout linearLayout = new LinearLayout(getParentActivity());
                linearLayout.setOrientation(LinearLayout.VERTICAL);

                for (int a = 0; a < 7; a++) {
                    String name = dayNames[a];
                    maskValues[a] = SharedStorage.downloadDay(a);

                    CheckBoxCell checkBoxCell = new CheckBoxCell(getParentActivity(), 1);
                    checkBoxCell.setTag(a);
                    checkBoxCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                    linearLayout.addView(checkBoxCell, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 48));
                    checkBoxCell.setText(name, "", maskValues[a], true);
                    checkBoxCell.setOnClickListener(v -> {
                        CheckBoxCell cell = (CheckBoxCell) v;
                        int num = (Integer) cell.getTag();
                        maskValues[num] = !maskValues[num];
                        cell.setChecked(maskValues[num], true);
                    });
                }

                BottomSheet.BottomSheetCell cell = new BottomSheet.BottomSheetCell(getParentActivity(), 1);
                cell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                cell.setTextAndIcon(LocaleController.getString("Save", R.string.Save).toUpperCase(), 0);
                cell.setTextColor(Theme.getColor(Theme.key_dialogTextBlue2));
                cell.setOnClickListener(v -> {
                    try {
                        if (visibleDialog != null) {
                            visibleDialog.dismiss();
                        }
                    } catch (Exception e) {
                        FileLog.e("tmessages", e);
                    }
                    for (int a = 0; a < 7; a++) {
                        SharedStorage.downloadDay(a, maskValues[a]);
                    }
                    if (listAdapter != null) {
                        listAdapter.notifyItemChanged(position);
                    }
                });
                linearLayout.addView(cell, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 48));
                builder.setCustomView(linearLayout);
                showDialog(builder.create());
            } else if (position == startTimeRow) {
/*
                TimePickerDialog.newInstance((view1, hourOfDay, minute) -> {
                    SharedStorage.downloadTime(DownloadHelper.TimeKeys.START_HOURS, hourOfDay);
                    SharedStorage.downloadTime(DownloadHelper.TimeKeys.START_MINUTES, minute);

                    saveReminder();
                    if (listAdapter != null) {
                        listAdapter.notifyItemChanged(position);
                    }
                }, SharedStorage.downloadTime(DownloadHelper.TimeKeys.START_HOURS), SharedStorage.downloadTime(DownloadHelper.TimeKeys.START_MINUTES), false).show(getParentActivity().getFragmentManager(), "Timepickerdialog");
*/
            } else if (position == endTimeRow) {
/*
                TimePickerDialog.newInstance((view12, hourOfDay, minute) -> {
                    SharedStorage.downloadTime(DownloadHelper.TimeKeys.END_HOURS, hourOfDay);
                    SharedStorage.downloadTime(DownloadHelper.TimeKeys.END_MINUTES, minute);

                    saveReminder();
                    if (listAdapter != null) {
                        listAdapter.notifyItemChanged(position);
                    }
                },  SharedStorage.downloadTime(DownloadHelper.TimeKeys.END_HOURS),  SharedStorage.downloadTime(DownloadHelper.TimeKeys.END_MINUTES), false).show(getParentActivity().getFragmentManager(), "Timepickerdialog_end");
*/
            } else if (position == enableWifiRow) {
                boolean ew =  SharedStorage.downloadModule(DownloadHelper.Modules.ENABLED_WIFI);
                SharedStorage.downloadModule(DownloadHelper.Modules.ENABLED_WIFI, !ew);
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(!ew);
                }
            } else if (position == disableWifiRow) {
                boolean dw = SharedStorage.downloadModule(DownloadHelper.Modules.DISABLED_WIFI);
                SharedStorage.downloadModule(DownloadHelper.Modules.DISABLED_WIFI, !dw);
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(!dw);
                }
            }
        });


        return fragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

/*    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {

    }*/

    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {

    }


    //Adapter
    private class ListAdapter extends RecyclerListView.SelectionAdapter {

        private Context mContext;

        public ListAdapter(Context context) {
            mContext = context;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int position = holder.getAdapterPosition();

            return position == enableDMRow || position == startTimeRow || (position == activeDaysRow && !SharedStorage.downloadModule(DownloadHelper.Modules.DISABLED_WIFI)) || position == endTimeRow || position == enableWifiRow || position == disableWifiRow || position == justTodayRow;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = null;
            switch (viewType) {
                case 0:
                    view = new TextCheckCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 1:
                    view = new TextDetailSettingsCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 2:
                    view = new TextSettingsCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 0:
                    TextCheckCell checkCell = (TextCheckCell) holder.itemView;
                    if (position == enableDMRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("DownloaderEnableScheduler", R.string.DownloaderEnableScheduler),
                                SharedStorage.downloadModule(DownloadHelper.Modules.RECEIVER), true);
                    } else if (position == enableWifiRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("DownloaderEnableWifi", R.string.DownloaderEnableWifi),
                                SharedStorage.downloadModule(DownloadHelper.Modules.ENABLED_WIFI), true);
                    } else if (position == disableWifiRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("DownloaderDisableWifi", R.string.DownloaderDisableWifi),
                                SharedStorage.downloadModule(DownloadHelper.Modules.DISABLED_WIFI), true);
                    } else if (position == justTodayRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("DownloaderJustToday", R.string.DownloaderJustToday),
                                SharedStorage.downloadModule(DownloadHelper.Modules.JUST_TODAY), false);
                    }
                    break;
                case 1:
                    TextDetailSettingsCell detailSettingsCell = (TextDetailSettingsCell) holder.itemView;
                    if (position == activeDaysRow) {
                        StringBuilder text = new StringBuilder();

                        int i = 0;
                        for (String s : dayNames) {
                            if (SharedStorage.downloadDay(i)) {
                                text.append(s).append(", ");
                            }
                            i++;
                        }

                        StringBuilder textSB = new StringBuilder(text.toString());
                        if (textSB.length() != 0) {
                            textSB.setCharAt(textSB.length() - 2, ' ');
                        }
                        detailSettingsCell.setTextAndValue(LocaleController.getString("DownloaderDays", R.string.DownloaderDays), String.valueOf(textSB), true);
                        detailSettingsCell.setMultilineDetail(false);
                    }
                    break;
                case 2:
                    TextSettingsCell settingsCell = (TextSettingsCell) holder.itemView;
                    if (position == startTimeRow) {
                        String time;
                        int hour = SharedStorage.downloadTime(DownloadHelper.TimeKeys.START_HOURS);
                        int minute = SharedStorage.downloadTime(DownloadHelper.TimeKeys.START_MINUTES);
                        if (minute < 10) {
                            time = String.format("%s", hour) + ":" + "0" + String.format("%s", minute);
                        } else {
                            time = String.format("%s", hour) + ":" + String.format("%s", minute);
                        }
                        settingsCell.setTextAndValue(LocaleController.getString("DownloaderStartTime", R.string.DownloaderStartTime), time, true);
                    } else if (position == endTimeRow) {
                        String time;
                        int hour = SharedStorage.downloadTime(DownloadHelper.TimeKeys.END_HOURS);

                        int minute = SharedStorage.downloadTime(DownloadHelper.TimeKeys.END_MINUTES);

                        if (minute < 10) {
                            time = String.format("%s", hour) + ":" + "0" + String.format("%s", minute);
                        } else {
                            time = String.format("%s", hour) + ":" + String.format("%s", minute);
                        }
                        settingsCell.setTextAndValue(LocaleController.getString("DownloaderEndTime", R.string.DownloaderEndTime), time, true);
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
            if (position == enableDMRow || position == enableWifiRow || position == disableWifiRow || position == justTodayRow) { //TextCheckCell
                return 0;
            } else if (position == activeDaysRow) { //TextDetailSettingsCell
                return 1;
            } else if (position == startTimeRow || position == endTimeRow) { //TextSettingsCell
                return 2;
            }
            return 0;
        }
    }

    public void saveReminder() {
        int sHour = SharedStorage.downloadTime(DownloadHelper.TimeKeys.START_HOURS);
        int sMinute = SharedStorage.downloadTime(DownloadHelper.TimeKeys.START_MINUTES);
        int eHour = SharedStorage.downloadTime(DownloadHelper.TimeKeys.END_HOURS);
        int eMinute = SharedStorage.downloadTime(DownloadHelper.TimeKeys.END_MINUTES);
        Calendar mCalendar;
        Calendar mCalendarEnd;

        new DownloadReceiver().cancelAlarm(ApplicationLoader.applicationContext);

        if (SharedStorage.downloadModule(DownloadHelper.Modules.JUST_TODAY)) {
            mCalendar = Calendar.getInstance();
            mCalendarEnd = Calendar.getInstance();

            mCalendar.set(Calendar.HOUR_OF_DAY, sHour);
            mCalendar.set(Calendar.MINUTE, sMinute);
            mCalendar.set(Calendar.SECOND, 0);

            mCalendarEnd.set(Calendar.HOUR_OF_DAY, eHour);
            mCalendarEnd.set(Calendar.MINUTE, eMinute);
            mCalendarEnd.set(Calendar.SECOND, 0);
            new DownloadReceiver().setAlarm(ApplicationLoader.applicationContext, mCalendar, mCalendarEnd);
        } else {
            if (SharedStorage.downloadDay(0)) {
                setRepeatAlarm(1, sHour, sMinute, eHour, eMinute);
            }
            if (SharedStorage.downloadDay(1)) {
                setRepeatAlarm(2, sHour, sMinute, eHour, eMinute);
            }
            if (SharedStorage.downloadDay(2)) {
                setRepeatAlarm(3, sHour, sMinute, eHour, eMinute);
            }
            if (SharedStorage.downloadDay(3)) {
                setRepeatAlarm(4, sHour, sMinute, eHour, eMinute);
            }
            if (SharedStorage.downloadDay(4)) {
                setRepeatAlarm(5, sHour, sMinute, eHour, eMinute);
            }
            if (SharedStorage.downloadDay(5)) {
                setRepeatAlarm(6, sHour, sMinute, eHour, eMinute);
            }
            if (SharedStorage.downloadDay(6)) {
                setRepeatAlarm(7, sHour, sMinute, eHour, eMinute);
            }
        }
    }

    private void setRepeatAlarm(int day, int sHour, int sMinute, int eHour, int eMinute) {
        Calendar mCalendar_r = Calendar.getInstance();
        Calendar mCalendarEnd_r = Calendar.getInstance();
        mCalendar_r.set(Calendar.DAY_OF_WEEK, day);
        mCalendar_r.set(Calendar.HOUR_OF_DAY, sHour);
        mCalendar_r.set(Calendar.MINUTE, sMinute);
        mCalendar_r.set(Calendar.SECOND, 0);
        mCalendar_r.set(Calendar.MILLISECOND, 0);
        mCalendarEnd_r.set(Calendar.DAY_OF_WEEK, day);
        mCalendarEnd_r.set(Calendar.HOUR_OF_DAY, eHour);
        mCalendarEnd_r.set(Calendar.MINUTE, eMinute);
        mCalendarEnd_r.set(Calendar.SECOND, 0);
        mCalendarEnd_r.set(Calendar.MILLISECOND, 0);
        new DownloadReceiver().setRepeatAlarm(ApplicationLoader.applicationContext, mCalendar_r, mCalendarEnd_r, (day) + 300);
    }
}
