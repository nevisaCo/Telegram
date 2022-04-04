package com.finalsoft.ui.profile;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.EditTextCaption;
import org.telegram.ui.Components.LayoutHelper;


public class ProfileMakerFragment extends BaseFragment {
    private static int menu_done = 1;
    private boolean fristtime = true;
    private EditTextCaption txtname;

    @Override
    public View createView(final Context context) {
        this.actionBar.createMenu();
        ActionBarLayout actionBarLayout = new ActionBarLayout(context);
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setTitle(LocaleController.getString("ProfileMaker", R.string.ProfileMaker));
        this.actionBar.setAllowOverlayTitle(true);
        ActionBarMenu actionbarmenu = this.actionBar.createMenu();
        actionbarmenu.addItem(menu_done, R.drawable.ic_ab_done);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                } else if (id == menu_done) {
                    Bundle bundle = new Bundle();
                    bundle.putString("name", txtname.getText().toString());
                    presentFragment(new ProfileSelector(bundle, ProfileMakerFragment.this), true);
                }
            }
        });
        FrameLayout frameLayout = new FrameLayout(context);
        TextView txttitle = new TextView(context);
        txttitle.setTextSize(20);
        txttitle.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        txttitle.setTypeface(AndroidUtilities.getTypeface("fonts/IRANSansLight.ttf"));
        txttitle.setText(LocaleController.getString("EnterNameForProfileMaker", R.string.EnterNameForProfileMaker));
        frameLayout.addView(txttitle, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.TOP | Gravity.CENTER_HORIZONTAL, 10, 10, 10, 10));

        txtname = new EditTextCaption(context, null);
        txtname.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        String currentname = "";
        TLRPC.User currentUser = UserConfig.getInstance(currentAccount).getCurrentUser();
        if (currentUser.first_name != null && currentUser.first_name.length() > 0) {
            currentname += currentUser.first_name;
        }
        if (currentUser.last_name != null && currentUser.last_name.length() > 0) {
            currentname += currentUser.last_name;
        }

        txtname.setText(currentname);
        txtname.setTextSize(20);
        fristtime = true;
        txtname.setOnClickListener(v -> {
            if (fristtime) {
                fristtime = false;
                txtname.setText("");
            }
        });
        txtname.setTypeface(AndroidUtilities.getTypeface("fonts/IRANSansLight.ttf"));
        frameLayout.addView(txtname, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.TOP | Gravity.LEFT, 5, 40, 5, 0));


        this.fragmentView = frameLayout;
        return frameLayout;
    }

}
