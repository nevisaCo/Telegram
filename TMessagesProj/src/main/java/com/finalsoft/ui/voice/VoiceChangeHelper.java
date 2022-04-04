package com.finalsoft.ui.voice;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.View;

import com.finalsoft.SharedStorage;

import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.SlideChooseView;

import java.util.concurrent.atomic.AtomicInteger;

public class VoiceChangeHelper {
    final static int[] bitRates = {9000, 11000, 14500, 16000, 19000, 22000, 25000};
    final static int[] icons = new int[]{
            R.drawable.ic_mic_3,
            R.drawable.ic_mic_2,
            R.drawable.ic_mic_1,
            R.drawable.input_mic,
            R.drawable.ic_mic1,
            R.drawable.ic_mic2,
            R.drawable.ic_mic3
    };

    private static int[] getBitRates() {
        return bitRates;
    }

    public static void save(int index) {
        SharedStorage.voiceBitRate(bitRates[index]);
        MediaController.getInstance().setBitRate(bitRates[index]);
    }

    public static void show(Context context, SlideChooseView.Callback callback) {
        AlertDialog.Builder builder = AlertsCreator.createSimpleAlert(context,
                LocaleController.getString("VoiceChanger", R.string.VoiceChanger),
                "");

        AtomicInteger position = new AtomicInteger(2);

        View v = getView(context, index -> position.set(index));
        builder.setView(v);
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (dialogInterface, i) -> callback.onOptionSelected(0));
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), (dialog, which) -> {
            save(position.get());
            callback.onOptionSelected(1);
        });
        builder.create().show();
    }

    public static View getView(Context context, SlideChooseView.Callback callback) {
        SlideChooseView slideChooseView = new SlideChooseView(context);
        slideChooseView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        slideChooseView.setCallback(callback);
        int stored = SharedStorage.voiceBitRate();
        int index = 3;
        for (int i = 0; i < bitRates.length; i++) {
            if (bitRates[i] == stored) {
                index = i;
                break;
            }
        }
        slideChooseView.setOptions(index,
                "-3",
                "-2",
                "-1",
                "Normal",
                "+1",
                "+2",
                "+3"
        );
        return slideChooseView;
    }

    public static int getCurrentIcon() {
        int stored = SharedStorage.voiceBitRate();
        for (int i = 0; i < getBitRates().length; i++) {
            if (getBitRates()[i] == stored) {
                return icons[i];
            }
        }
        return R.drawable.input_mic;
    }
}
