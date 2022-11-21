package com.finalsoft.contactsChanges;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.finalsoft.SharedStorage;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.VideoEditedInfo;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Cells.RadioColorCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.PhotoViewer.PhotoViewerProvider;
import org.telegram.ui.PhotoViewer.PlaceProviderObject;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class ContactChangesActivity extends BaseFragment implements NotificationCenterDelegate, PhotoViewerProvider {
    private static final int delete = 2;
    private static final int filter = 3;
    private int currentFilterType;
    private UpdateCursorAdapter updateCursorAdapter;
    private DBHelper dbHelper;
    private ListView listView;
    private boolean paused;
    private User selectedUser;
    protected BackupImageView selectedUserAvatar;


    public ContactChangesActivity(Bundle bundle) {
        super(bundle);
        this.currentFilterType = 0;
         SharedStorage.contactChangeCount(0);
    }

    private void forceReload() {
        this.updateCursorAdapter = new UpdateCursorAdapter(getParentActivity(), new DBHelper().getData(this.currentFilterType, 500));
        this.listView.setAdapter((ListAdapter) this.updateCursorAdapter);
    }

    private void openChatActivity() {
        Bundle bundle = new Bundle();
        bundle.putLong("user_id", this.selectedUser.id);
        presentFragment(new ChatActivity(bundle), false);
    }

    private void showDeleteHistoryConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setMessage(LocaleController.getString("AreYouSureDeleteChanges", R.string.AreYouSureDeleteChanges));
        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ContactChangesActivity.this.dbHelper.b();
                ContactChangesActivity.this.forceReload();
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        showDialog(builder.create());
    }

    public boolean cancelButtonPressed() {
        return false;
    }

    @Override
    public void needAddMorePhotos() {

    }


    @SuppressLint("ClickableViewAccessibility")
    public View createView(Context context) {
        this.fragmentView = new FrameLayout(context);
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("ContactsChanges", R.string.ContactsChanges));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i) {
                if (i == -1) {
                    ContactChangesActivity.this.finishFragment();
                } else if (i == ContactChangesActivity.delete) {
                    ContactChangesActivity.this.showDeleteHistoryConfirmation();
//                    NotificationCenter.getInstance(UserConfig.selectedAccount).postNotificationName(NotificationCenter.refreshMenu, 0);
                } else if (i == ContactChangesActivity.filter) {
                    ContactChangesActivity.this.showFilterDialog();
                }
            }
        });
        ActionBarMenu createMenu = this.actionBar.createMenu();
        createMenu.addItem(delete, R.drawable.msg_delete);
        ActionBarMenuItem filterItem = createMenu.addItem(filter, R.drawable.msg_settings);
        this.dbHelper = new DBHelper();
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setVisibility(View.INVISIBLE);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        ((FrameLayout) this.fragmentView).addView(linearLayout);
        LayoutParams layoutParams = (LayoutParams) linearLayout.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        layoutParams.gravity = 48;
        linearLayout.setLayoutParams(layoutParams);
        linearLayout.setOnTouchListener((view, motionEvent) -> true);
        TextView emptyTextView = new TextView(context);
        emptyTextView.setTextColor(0xff808080);
        emptyTextView.setTextSize(1, 20.0f);
        emptyTextView.setGravity(17);
        emptyTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        emptyTextView.setText(LocaleController.getString("NoContactChanges", R.string.NoContactChanges));
        linearLayout.addView(emptyTextView);
        LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) emptyTextView.getLayoutParams();
        layoutParams2.width = -1;
        layoutParams2.height = -1;
        layoutParams2.weight = 0.5f;
        emptyTextView.setLayoutParams(layoutParams2);
        View frameLayout = new FrameLayout(context);
        linearLayout.addView(frameLayout);
        layoutParams2 = (LinearLayout.LayoutParams) frameLayout.getLayoutParams();
        layoutParams2.width = -1;
        layoutParams2.height = -1;
        layoutParams2.weight = 0.5f;
        frameLayout.setLayoutParams(layoutParams2);

        Cursor cursor = new DBHelper().getData(currentFilterType, 500);
        this.updateCursorAdapter = new UpdateCursorAdapter(context, cursor);
        this.listView = new ListView(context);
        this.listView.setEmptyView(linearLayout);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setDivider(null);
        this.listView.setDividerHeight(0);
        this.listView.setFastScrollEnabled(true);
        listView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        this.listView.setCacheColorHint(0);
        this.listView.setScrollingCacheEnabled(false);
        this.listView.setAdapter((ListAdapter) this.updateCursorAdapter);
        ((FrameLayout) this.fragmentView).addView(this.listView);
        layoutParams = (LayoutParams) this.listView.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        this.listView.setLayoutParams(layoutParams);
        this.listView.setOnItemClickListener((adapterView, view, i, j) -> {
            ContactChangesActivity.this.selectedUser = MessagesController.getInstance(UserConfig.selectedAccount).getUser((long) ContactChangesActivity.this.dbHelper.updateModel((Cursor) ContactChangesActivity.this.updateCursorAdapter.getItem(i)).getUserId());
            if (ContactChangesActivity.this.selectedUser != null) {
                ContactChangesActivity.this.selectedUserAvatar = ((UpdateCell) view).getAvatarImageView();
                ContactChangesActivity.this.showUserActionsDialog();
            }
        });

        return this.fragmentView;
    }

    public void didReceivedNotification(int i, Object... objArr) {
        if (!this.paused) {
            UpdateNotificationUtil.dismissNotification();
            this.dbHelper.a();
            NotificationCenter.getInstance(UserConfig.selectedAccount).postNotificationName(NotificationCenter.mainUserInfoChanged);
        }
        forceReload();
    }

    public PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, FileLocation fileLocation, int i) {
        if (fileLocation == null) {
            return null;
        }
        FileLocation fileLocation2;
        if (!(this.selectedUser == null || this.selectedUser.id == 0)) {
            User user = MessagesController.getInstance(UserConfig.selectedAccount).getUser(this.selectedUser.id);
            if (!(user == null || user.photo == null || user.photo.photo_big == null)) {
                fileLocation2 = user.photo.photo_big;
                if (fileLocation2 != null || fileLocation2.local_id != fileLocation.local_id || fileLocation2.volume_id != fileLocation.volume_id || fileLocation2.dc_id != fileLocation.dc_id) {
                    return null;
                }
                int[] iArr = new int[delete];
                this.selectedUserAvatar.getLocationInWindow(iArr);
                PlaceProviderObject placeProviderObject = new PlaceProviderObject();
                placeProviderObject.viewX = iArr[0];
                placeProviderObject.viewY = iArr[1] - AndroidUtilities.statusBarHeight;
                placeProviderObject.parentView = this.selectedUserAvatar;
                placeProviderObject.imageReceiver = this.selectedUserAvatar.getImageReceiver();
                //  placeProviderObject.user_id = this.selectedUser.id;
                placeProviderObject.thumb = placeProviderObject.imageReceiver.getThumbBitmapSafe();
                placeProviderObject.size = -1;
                placeProviderObject.radius = this.selectedUserAvatar.getImageReceiver().getRoundRadius();
                return placeProviderObject;
            }
        }
        fileLocation2 = null;
        return fileLocation2 != null ? null : null;
    }

    public int getSelectedCount() {
        return 0;
    }

    public ImageReceiver.BitmapHolder getThumbForPhoto(MessageObject messageObject, FileLocation fileLocation, int i) {
        return null;
    }

    public boolean isPhotoChecked(int i) {
        return false;
    }

    @Override
    public int setPhotoChecked(int index, VideoEditedInfo videoEditedInfo) {
        return 0;
    }

    @Override
    public int setPhotoUnchecked(Object photoEntry) {
        return 0;
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
//        NotificationCenter.getInstance(UserConfig.selectedAccount).postNotificationName(NotificationCenter.refreshMenu, 0);
    }

    public void onPause() {
        super.onPause();
        this.paused = true;
    }

    public void onResume() {
        super.onResume();
        this.paused = false;
        UpdateNotificationUtil.dismissNotification();
        this.dbHelper.a();
        NotificationCenter.getInstance(UserConfig.selectedAccount).postNotificationName(NotificationCenter.mainUserInfoChanged);

    }

    public void sendButtonPressed(int i) {
    }

    public void setPhotoChecked(int i) {
    }


    protected void showFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        final LinearLayout linearLayout = new LinearLayout(getParentActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        String descriptions[] = new String[]{
                LocaleController.getString("Disable", R.string.Disable),
                LocaleController.getString("AllChanges", R.string.AllChanges),
                LocaleController.getString("change_name", R.string.change_name),
                LocaleController.getString("change_photo", R.string.change_photo),
                LocaleController.getString("change_phone", R.string.change_phone),
        };


/*        int i = 0;
        if (this.currentFilterType > 0) {
            i = this.currentFilterType - 1;
        }*/

        for (int a = 0; a < descriptions.length; a++) {
            RadioColorCell cell = new RadioColorCell(getParentActivity());
            cell.setPadding(AndroidUtilities.dp(4), 0, AndroidUtilities.dp(4), 0);
            cell.setTag(a);
//      cell.setCheckColor(TextColorCell.color[a], TextColorCell.color[a]);
            cell.setTextAndValue(descriptions[a], currentFilterType == a);
            linearLayout.addView(cell);
            cell.setOnClickListener(v -> {
//                int count = linearLayout.getChildCount();
                currentFilterType= (Integer) v.getTag();
/*                for (int a1 = 0; a1 < count; a1++) {
                    RadioColorCell cell1 = (RadioColorCell) linearLayout.getChildAt(a1);
                    if (cell == v) {
                        selected =
                    }
                }*/
//        Log.w("sina-msg","selected == " + selected);
//                currentFilterType = selected;

/*            if (selected == 0) {
                    currentFilterType = 0;
                } else if (selected == 1) {
                    currentFilterType = 2;
                } else if (selected == 2) {
                    currentFilterType = 3;
                } else if (selected == 3) {
                    currentFilterType = 4;
                }*/
//        Log.w("sina-msg","ContactChangesActivity.this.currentFilterType == " + ContactChangesActivity.this.currentFilterType);


                ContactChangesActivity.this.forceReload();
                builder.create().dismiss();
            });
        }
        builder.setTitle(LocaleController.getString("filter_title", R.string.filter_title));
        builder.setView(linearLayout);
        builder.setPositiveButton(null, null);
        builder.setNeutralButton(null, null);
        showDialog(builder.create());
    }


//   protected void showFilterDialog() {
//       int i = 0;
//
//
//       AlertDialog.Builder builder = new  AlertDialog.Builder(getParentActivity());
//       builder.setTitle(R.string.filter_title);
//
//
//
//
//       CharSequence[] charSequenceArr = new CharSequence[]{
//               getParentActivity().getString(R.string.AllChanges),
//               getParentActivity().getString(R.string.change_name),
//               getParentActivity().getString(R.string.change_photo),
//               getParentActivity().getString(R.string.change_phone)};
//       if (this.currentFilterType != 0) {
//           i = this.currentFilterType - 1;
//       }
//
//       builder.setSingleChoiceItems(charSequenceArr, i, new OnClickListener() {
//           @Override
//           public void onClick(DialogInterface dialogInterface, int i) {
//               if (i == 0) {
//                   ContactChangesActivity.this.filterItem.setIcon(R.drawable.ic_smiles_settings);
//               } else {
//                   ContactChangesActivity.this.filterItem.setIcon(R.drawable.ic_smiles_settings);
//               }
//               if (i == 0) {
//                   ContactChangesActivity.this.currentFilterType = 0;
//               } else if (i == 1) {
//                   ContactChangesActivity.this.currentFilterType = ContactChangesActivity.delete;
//               } else if (i == ContactChangesActivity.delete) {
//                   ContactChangesActivity.this.currentFilterType = ContactChangesActivity.filter;
//               } else if (i == ContactChangesActivity.filter) {
//                   ContactChangesActivity.this.currentFilterType = 4;
//               }
//               ContactChangesActivity.this.forceReload();
//               dialogInterface.dismiss();
//           }
//       });
//
//     showDialog(builder.create());
//
//   }

    protected void showUserActionsDialog() {
        ContactChangesActivity.this.openChatActivity();

      /* if (this.selectedUser.photo == null || this.selectedUser.photo.photo_big == null) {
           openChatActivity();
           return;
       }

       Builder builder = new Builder(getParentActivity());
       builder.setTitle(ContactsController.formatName(this.selectedUser.first_name, this.selectedUser.last_name));
       CharSequence[] charSequenceArr = new CharSequence[delete];
       charSequenceArr[0] = getParentActivity().getString(R.string.send_message_in_telegram);
       charSequenceArr[1] = getParentActivity().getString(R.string.show_user_photos);
       builder.setItems(charSequenceArr, new OnClickListener() {
           @Override
           public void onClick(DialogInterface dialogInterface, int i) {
               if (i == 0) {
                   ContactChangesActivity.this.openChatActivity();
               } else if (i == 1) {
                   PhotoViewer.getInstance().setParentActivity(getParentActivity());
                   PhotoViewer.getInstance().openPhoto(selectedUser.photo.photo_big, ContactChangesActivity.this);
               }
               dialogInterface.dismiss();
           }
       });
       showDialog(builder.create());*/
    }

    public void updatePhotoAtIndex(int i) {
    }

    @Override


    public boolean allowCaption() {
        return false;
    }

    @Override
    public boolean scaleToFill() {
        return false;
    }

    @Override
    public ArrayList<Object> getSelectedPhotosOrder() {
        return null;
    }

    @Override
    public HashMap<Object, Object> getSelectedPhotos() {
        return null;
    }

    @Override
    public boolean canScrollAway() {
        return false;
    }

    @Override
    public int getPhotoIndex(int index) {
        return 0;
    }

    @Override
    public void deleteImageAtIndex(int index) {

    }

    @Override
    public String getDeleteMessageString() {
        return null;
    }

    @Override
    public boolean canCaptureMorePhotos() {
        return false;
    }

    public void willHidePhotoViewer() {
        if (this.selectedUserAvatar != null) {
            this.selectedUserAvatar.getImageReceiver().setVisible(true, true);
        }
    }

    public void willSwitchFromPhoto(MessageObject messageObject, FileLocation fileLocation, int i) {
    }

    @Override
    public void didReceivedNotification(int id, int account, Object... args) {

    }
}
