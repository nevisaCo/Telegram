package com.finalsoft.ui.profile;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.StyleRes;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;

import java.util.ArrayList;


public class ProfileSelector extends BaseFragment {
    private static int menu_done=1;
    public String inputname;
    private Context context;
    private RecyclerView.Adapter listAdapter;
    public ArrayList<String> ProfilePatterns=new ArrayList<>();

    public ProfileSelector(Bundle bundle, ProfileMakerFragment profilemaker) {
        super(bundle);
        inputname=bundle.getString("name",null);
    }

    @Override
    public View createView(final Context context) {
        this.context=context;
        this.actionBar.createMenu();
        ActionBarLayout actionBarLayout = new ActionBarLayout(context);
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setTitle(LocaleController.getString("ProfileMaker", R.string.ProfileMaker));
        this.actionBar.setAllowOverlayTitle(true);

        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick(){
            @Override
            public void onItemClick(int id) {
                if(id==-1) {
                    finishFragment();
                }
            }
        });
        FrameLayout frameLayout = new FrameLayout(context);

        RecyclerListView listView = new RecyclerListView(context);
        listView.setVerticalScrollBarEnabled(false);
        listView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
//        listView.setGlowColor(Theme.getColor(Theme.key_avatar_backgroundActionBarBlue));
        frameLayout.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.TOP ));
        listView.setAdapter(listAdapter=new ListAdapter(context));
        LoadPatterns();
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        this.fragmentView = frameLayout;
        return frameLayout;
    }

    public void SetProfile(String profile){
        String newName = profile;
        TLRPC.User currentUser = UserConfig.getInstance(currentAccount).getCurrentUser();
        if (currentUser.first_name == null || !currentUser.first_name.equals(newName) || currentUser.last_name == null || !currentUser.last_name.equals("")) {
            TLRPC.TL_account_updateProfile tL_account_updateProfile = new TLRPC.TL_account_updateProfile();
            tL_account_updateProfile.flags = 3;
            String str2 = newName;
            tL_account_updateProfile.first_name = str2;
            currentUser.first_name = str2;
            tL_account_updateProfile.last_name = "";
            currentUser.last_name = "";
            TLRPC.User user = MessagesController.getInstance(UserConfig.selectedAccount).getUser(UserConfig.getInstance(currentAccount).getClientUserId());
            if (user != null) {
                user.first_name = tL_account_updateProfile.first_name;
                user.last_name = tL_account_updateProfile.last_name;
            }
            UserConfig.getInstance(currentAccount).saveConfig(true);
            NotificationCenter.getInstance(UserConfig.selectedAccount).postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
            NotificationCenter.getInstance(UserConfig.selectedAccount).postNotificationName(NotificationCenter.updateInterfaces, new Object[]{Integer.valueOf(1)});
            ConnectionsManager.getInstance(UserConfig.selectedAccount).sendRequest(tL_account_updateProfile, new RequestDelegate() {
                public void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    if(tL_error==null){
                        Toast.makeText(context, LocaleController.getString("ChangedSuccessfully", R.string.ChangedSuccessfully), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
    private void LoadPatterns() {
        try {
            JSONArray jsonarray=new JSONArray(JsonFileHelper.loadJSONFromAsset("patterns.json"));
            for(int i=0;i<jsonarray.length();i++){
                this.ProfilePatterns.add(jsonarray.getJSONObject(i).getString("pattern"));
            }
            listAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private class ListAdapter extends RecyclerListView.SelectionAdapter {

        private Context mContext;

        public ListAdapter(Context context) {
            mContext = context;
        }

        @Override
        public int getItemCount() {
            return ProfilePatterns.size();
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((ProfilePatternCell)holder.itemView).setPattern(ProfilePatterns.get(position).replace("@@@@NAME@@@@",inputname));
        }
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ProfilePatternCell view=new ProfilePatternCell(mContext);

            return new RecyclerListView.Holder(view);
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return true;
        }
    }

    private class ProfilePatternCell extends FrameLayout {
        private String pattern="";
        public void setPattern(String pattern){
            this.pattern=pattern;
            init();
        }
        public ProfilePatternCell(@NonNull Context context) {
            super(context);
            init();
        }

        public ProfilePatternCell(@NonNull Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        public ProfilePatternCell(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            init();
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public ProfilePatternCell(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
            init();
        }

        public void init(){
//            RelativeLayout relativeLayout=new RelativeLayout(getContext());
            TextView txtpattern=new TextView(getContext());
            txtpattern.setTextSize(14);
            txtpattern.setTypeface(AndroidUtilities.getTypeface(""));
            txtpattern.setText(pattern);
            txtpattern.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            txtpattern.setGravity(Gravity.CENTER_HORIZONTAL);
//            relativeLayout.addView(txtpattern, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 40, Gravity.CENTER));
            addView(txtpattern, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 60, Gravity.CENTER));
            setLayoutParams(LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
            setOnClickListener(v -> AndroidUtilities.runOnUIThread(() -> {
                SetProfile(pattern);
                finishFragment();
            }));
        }
    }
}
