package com.finalsoft.ui;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.finalsoft.SharedStorage;
import com.finalsoft.helper.DownloadHelper;
import com.finalsoft.ui.settings.DownloadSettingsActivity;

import org.telegram.SQLite.SQLiteCursor;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildConfig;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.R;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.VideoEditedInfo;
import org.telegram.messenger.WebFile;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Adapters.StickersAdapter;
import org.telegram.ui.ArticleViewer;
import org.telegram.ui.Cells.BotHelpCell;
import org.telegram.ui.Cells.ChatActionCell;
import org.telegram.ui.Cells.ChatLoadingCell;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Cells.ChatUnreadCell;
import org.telegram.ui.Cells.ContextLinkCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.ChatAttachAlert;
import org.telegram.ui.Components.ChatAvatarContainer;
import org.telegram.ui.Components.ChatBigEmptyView;
import org.telegram.ui.Components.ContextProgressView;
import org.telegram.ui.Components.EmbedBottomSheet;
import org.telegram.ui.Components.ExtendedGridLayoutManager;
import org.telegram.ui.Components.FragmentContextView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.NumberTextView;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ShareAlert;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.StickersAlert;
import org.telegram.ui.ContentPreviewViewer;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.LocationActivity;
import org.telegram.ui.PhotoViewer;
import org.telegram.ui.ProfileActivity;
import org.telegram.ui.TopicsFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;


@SuppressWarnings("ALL")
public class DownloadManagerActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate, DialogsActivity.DialogsActivityDelegate,
        PhotoViewer.PhotoViewerProvider, DownloadController.FileDownloadProgressListener {

    private boolean userBlocked = false;

    private ArrayList<ChatMessageCell> chatMessageCellsCache = new ArrayList<>();

    private Dialog closeChatDialog;
    private FrameLayout progressView;
    private View progressView2;
    private RadialProgressView progressBar;
    private FrameLayout bottomOverlay;
    private ImageView stickersPanelArrow;
    private View timeItem2;
    private ActionBarMenuItem menuItem;
    private ActionBarMenuItem attachItem;
    private ActionBarMenuItem headerItem;
    private ContextProgressView editDoneItemProgress;
    private AnimatorSet editDoneItemAnimation;
    private TextView addContactItem;
    private RecyclerListView chatListView;
    private LinearLayoutManager chatLayoutManager;
    private ChatActivityAdapter chatAdapter;
    private TextView bottomOverlayChatText;
    private FrameLayout bottomOverlayChat;
    private FrameLayout emptyViewContainer;
    private ChatBigEmptyView bigEmptyView;
    private ArrayList<View> actionModeViews = new ArrayList<>();
    private ChatAvatarContainer avatarContainer;
    private TextView bottomOverlayText;
    private NumberTextView selectedMessagesCountTextView;
    private FrameLayout actionModeTitleContainer;
    private SimpleTextView actionModeTextView;
    private SimpleTextView actionModeSubTextView;
    private RecyclerListView stickersListView;
    private RecyclerListView.OnItemClickListener stickersOnItemClickListener;
    private RecyclerListView.OnItemClickListener mentionsOnItemClickListener;
    private StickersAdapter stickersAdapter;
    private FrameLayout stickersPanel;
    private TextView muteItem;
    private FrameLayout pagedownButton;
    private boolean pagedownButtonShowedByScroll;
    private TextView pagedownButtonCounter;
    private BackupImageView replyImageView;
    private SimpleTextView replyNameTextView;
    private SimpleTextView replyObjectTextView;
    private ImageView replyIconImageView;
    private ImageView replyCloseImageView;
    private LinearLayoutManager mentionLayoutManager;
    private ExtendedGridLayoutManager mentionGridLayoutManager;
    private AnimatorSet mentionListAnimation;
    private ChatAttachAlert chatAttachAlert;
    private LinearLayout reportSpamView;
    private AnimatorSet reportSpamViewAnimator;
    private TextView addToContactsButton;
    private TextView reportSpamButton;
    private FrameLayout reportSpamContainer;
    private TextView gifHintTextView;
    private View emojiButtonRed;
    private FrameLayout pinnedMessageView;
    private AnimatorSet pinnedMessageViewAnimator;
    private BackupImageView pinnedMessageImageView;
    private SimpleTextView pinnedMessageNameTextView;
    private SimpleTextView pinnedMessageTextView;
    private FrameLayout alertView;
    private Runnable hideAlertViewRunnable;
    private TextView alertNameTextView;
    private TextView alertTextView;
    private AnimatorSet alertViewAnimator;
    private FrameLayout searchContainer;
    private ImageView searchUpButton;
    private ImageView searchDownButton;
    private SimpleTextView searchCountText;
    private boolean mentionListViewIgnoreLayout;
    private int mentionListViewScrollOffsetY;
    private int mentionListViewLastViewTop;
    private int mentionListViewLastViewPosition;
    private boolean mentionListViewIsScrolling;
    private MessageObject pinnedMessageObject;
    private int loadingPinnedMessage;
    private ObjectAnimator pagedownButtonAnimation;
    private AnimatorSet replyButtonAnimation;
    private boolean openSearchKeyboard;
    private boolean waitingForReplyMessageLoad;
    private boolean allowStickersPanel;
    private boolean allowContextBotPanel;
    private boolean allowContextBotPanelSecond = true;
    private AnimatorSet runningAnimation;
    private MessageObject selectedObject;
    private ArrayList<MessageObject> forwardingMessages;
    private MessageObject forwaringMessage;
    private MessageObject replyingMessageObject;
    private int editingMessageObjectReqId;
    private boolean paused = true;
    private boolean wasPaused = false;
    private boolean readWhenResume = false;
    private TLRPC.FileLocation replyImageLocation;
    private TLRPC.FileLocation pinnedImageLocation;
    private int linkSearchRequestId;
    private TLRPC.WebPage foundWebPage;
    private ArrayList<CharSequence> foundUrls;
    private String pendingLinkSearchString;
    private Runnable pendingWebPageTimeoutRunnable;
    private Runnable waitingForCharaterEnterRunnable;
    private boolean openAnimationEnded;
    private int readWithDate;
    private int readWithMid;
    private boolean scrollToTopOnResume;
    private boolean forceScrollToTop;
    private boolean scrollToTopUnReadOnResume;
    private long dialog_id;
    private int lastLoadIndex;
    private FragmentContextView fragmentContextView;
    private HashMap<Integer, MessageObject>[] selectedMessagesIds = new HashMap[]{new HashMap<>(), new HashMap<>()}; //*
    private HashMap<Integer, MessageObject>[] selectedMessagesCanCopyIds = new HashMap[]{new HashMap<>(), new HashMap<>()}; //*
    private int cantDeleteMessagesCount; //*
    private ArrayList<Integer> waitingForLoad = new ArrayList<>(); //*
    private int newUnreadMessageCount;
    private HashMap<Integer, MessageObject>[] messagesDict = new HashMap[]{new HashMap<>(), new HashMap<>()};
    private HashMap<String, ArrayList<MessageObject>> messagesByDays = new HashMap<>(); //*
    protected ArrayList<MessageObject> messages = new ArrayList<>();

    //MyT
    HashMap<MessageObject, Integer> e = new HashMap<MessageObject, Integer>();
    HashMap<Long, List<MessageObject>> f = new HashMap<Long, List<MessageObject>>();
    HashMap<Long, MessageObject> g = new HashMap<Long, MessageObject>();
    HashSet<Long> h = new HashSet<Long>();
    HashSet<Long> i = new HashSet<Long>();
    //End MyT

    private int maxMessageId[] = new int[]{Integer.MAX_VALUE, Integer.MAX_VALUE};
    private int minMessageId[] = new int[]{Integer.MIN_VALUE, Integer.MIN_VALUE};
    private int maxDate[] = new int[]{Integer.MIN_VALUE, Integer.MIN_VALUE};
    private int minDate[] = new int[2];
    private boolean endReached[] = new boolean[2];
    private boolean cacheEndReached[] = new boolean[2];
    private boolean forwardEndReached[] = new boolean[]{true, true};
    private boolean loading;
    private boolean firstLoading = true;
    private int loadsCount;
    private int last_message_id = 0;
    private long mergeDialogId;
    private int startLoadFromMessageId;
    private boolean needSelectFromMessageId;
    private int returnToMessageId;
    private int returnToLoadIndex;
    private boolean first = true;
    private int unread_to_load;
    private int first_unread_id;
    private boolean loadingForward;
    private MessageObject unreadMessageObject;
    private MessageObject scrollToMessage;
    private int highlightMessageId = Integer.MAX_VALUE;
    private int scrollToMessagePosition = -10000;
    private String currentPicturePath;
    protected TLRPC.ChatFull info = null;
    private HashMap<Integer, TLRPC.BotInfo> botInfo = new HashMap<>();
    private String botUser;
    private long inlineReturn;
    private MessageObject botButtons;
    private MessageObject botReplyButtons;
    private int botsCount;
    private boolean hasBotsCommands;
    private long chatEnterTime = 0;
    private long chatLeaveTime = 0;
    private String startVideoEdit = null;
    private float startX = 0;
    private float startY = 0;
    private ArrayList<Object> botContextResults;
    private PhotoViewer.PhotoViewerProvider botContextProvider = new PhotoViewer.PhotoViewerProvider() {

        @Override
        public PhotoViewer.PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int index, boolean needPreview) {
            if (index < 0 || index >= botContextResults.size()) {
                return null;
            }
//            int count = mentionListView.getChildCount();
//            Object result = botContextResults.get(index);
//
//            for (int a = 0; a < count; a++) {
//                ImageReceiver imageReceiver = null;
//                View view = mentionListView.getChildAt(a);
//                if (view instanceof ContextLinkCell) {
//                    ContextLinkCell cell = (ContextLinkCell) view;
//                    if (cell.getResult() == result) {
//                        imageReceiver = cell.getPhotoImage();
//                    }
//                }
//
//                if (imageReceiver != null) {
//                    int coords[] = new int[2];
//                    view.getLocationInWindow(coords);
//                    PhotoViewer.PlaceProviderObject object = new PhotoViewer.PlaceProviderObject();
//                    object.viewX = coords[0];
//                    object.viewY = coords[1] - (Build.VERSION.SDK_INT >= 21 ? 0 : AndroidUtilities.statusBarHeight);
//                    object.parentView = mentionListView;
//                    object.imageReceiver = imageReceiver;
//                    object.thumb = imageReceiver.getBitmap();
//                    object.radius = imageReceiver.getRoundRadius();
//                    return object;
//                }
//            }
            return null;
        }

        @Override
        public ImageReceiver.BitmapHolder getThumbForPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int index) {
            return null;
        }

        @Override
        public void willSwitchFromPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int index) {

        }

        @Override
        public void willHidePhotoViewer() {

        }

        @Override
        public boolean isPhotoChecked(int index) {
            return false;
        }

        @Override
        public int setPhotoChecked(int index, VideoEditedInfo videoEditedInfo) {

            return index;
        }

        @Override
        public int setPhotoUnchecked(Object photoEntry) {
            return 0;
        }

        @Override
        public boolean cancelButtonPressed() {
            return false;
        }

        @Override
        public void needAddMorePhotos() {

        }

        @Override
        public void sendButtonPressed(int index, VideoEditedInfo videoEditedInfo, boolean notify, int scheduleDate, boolean forceDocument) {

        }

        @Override
        public void sendButtonPressed(int index, VideoEditedInfo videoEditedInfo, boolean notify, int scheduleDate) {
            if (index < 0 || index >= botContextResults.size()) {
                return;
            }
        }

        @Override
        public void replaceButtonPressed(int index, VideoEditedInfo videoEditedInfo) {

        }

        @Override
        public boolean canReplace(int index) {
            return false;
        }

        @Override
        public int getSelectedCount() {
            return 0;
        }

        @Override
        public void updatePhotoAtIndex(int index) {

        }

        @Override
        public boolean allowSendingSubmenu() {
            return false;
        }

        @Override
        public boolean scaleToFill() {
            return false;
        }

//        @Override
//        public void toggleGroupPhotosEnabled() {
//
//        }

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

//        @Override
//        public boolean allowGroupPhotos() {
//            return false;
//        }

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

        @Override
        public void openPhotoForEdit(String file, String thumb, boolean isVideo) {

        }

        @Override
        public int getTotalImageCount() {
            return 0;
        }

        @Override
        public boolean loadMore() {
            return false;
        }

        @Override
        public CharSequence getTitleFor(int index) {
            return null;
        }

        @Override
        public CharSequence getSubtitleFor(int index) {
            return null;
        }

        @Override
        public MessageObject getEditingMessageObject() {
            return null;
        }

        @Override
        public void onCaptionChanged(CharSequence caption) {

        }

        @Override
        public boolean closeKeyboard() {
            return false;
        }

        @Override
        public boolean validateGroupId(long groupId) {
            return false;
        }

        @Override
        public void onApplyCaption(CharSequence caption) {

        }

        @Override
        public void onOpen() {

        }

        @Override
        public void onClose() {

        }

        @Override
        public boolean allowCaption() {
            return true;
        }
    };

    boolean downloaderRunning;
    private final static int delete_downloaded = 1;
    private final static int delete_all = 2;
    private final static int settings = 3;
    private final static int copy = 10;
    private final static int quoteforward = 11;
    private final static int forward = 12;
    private final static int delete = 13;
    private final static int delete_chat = 14;
    private final static int id_chat_compose_panel = 1000;

    RecyclerListView.OnItemLongClickListener onItemLongClickListener = new RecyclerListView.OnItemLongClickListener() {
        @Override
        public boolean onItemClick(View view, int position) {
            if (!actionBar.isActionModeShowed()) {
                createMenu(view, false);
                return true;
            }
            return false;
        }
    };

    RecyclerListView.OnItemClickListener onItemClickListener = new RecyclerListView.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            if (actionBar.isActionModeShowed()) {
                processRowSelect(view);
                return;
            }
            createMenu(view, true);
        }
    };

    public DownloadManagerActivity(Bundle args) {
        super(args);
    }

    @Override
    public boolean onFragmentCreate() {
        downloaderRunning = SharedStorage.downloadModule(DownloadHelper.Modules.RUNNING)
        ;

        startLoadFromMessageId = 0;
        NotificationCenter.getInstance(currentAccount).addObserver(this, NotificationCenter.messagesDidLoad);
        NotificationCenter.getInstance(currentAccount).addObserver(this, NotificationCenter.closeChats);
        NotificationCenter.getInstance(currentAccount).addObserver(this, NotificationCenter.removeAllMessagesFromDialog);
        NotificationCenter.getInstance(currentAccount).addObserver(this, NotificationCenter.messagePlayingProgressDidChanged);
        NotificationCenter.getInstance(currentAccount).addObserver(this, NotificationCenter.messagePlayingDidReset);
        NotificationCenter.getInstance(currentAccount).addObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
        NotificationCenter.getInstance(currentAccount).addObserver(this, NotificationCenter.fileNewChunkAvailable);
        NotificationCenter.getInstance(currentAccount).addObserver(this, NotificationCenter.didCreatedNewDeleteTask);
        NotificationCenter.getInstance(currentAccount).addObserver(this, NotificationCenter.messagePlayingDidStart);
        NotificationCenter.getInstance(currentAccount).addObserver(this, NotificationCenter.updateMessageMedia);
        NotificationCenter.getInstance(currentAccount).addObserver(this, NotificationCenter.didUpdateMessagesViews);
        NotificationCenter.getInstance(currentAccount).addObserver(this, NotificationCenter.chatInfoCantLoad);
        super.onFragmentCreate();

        loading = true;
        if (startLoadFromMessageId != 0) {
            needSelectFromMessageId = true;
            waitingForLoad.add(lastLoadIndex);
        } else {
            waitingForLoad.add(lastLoadIndex);
        }
        DM_LoadMessagesByClassGuid(classGuid);

        return true;
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(currentAccount).removeObserver(this, NotificationCenter.messagesDidLoad);
        NotificationCenter.getInstance(currentAccount).removeObserver(this, NotificationCenter.closeChats);
        NotificationCenter.getInstance(currentAccount).removeObserver(this, NotificationCenter.messagesDeleted);
        NotificationCenter.getInstance(currentAccount).removeObserver(this, NotificationCenter.removeAllMessagesFromDialog);
        NotificationCenter.getInstance(currentAccount).removeObserver(this, NotificationCenter.messagePlayingProgressDidChanged);
        NotificationCenter.getInstance(currentAccount).removeObserver(this, NotificationCenter.messagePlayingDidReset);
        NotificationCenter.getInstance(currentAccount).removeObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
        NotificationCenter.getInstance(currentAccount).removeObserver(this, NotificationCenter.fileNewChunkAvailable);
        NotificationCenter.getInstance(currentAccount).removeObserver(this, NotificationCenter.didCreatedNewDeleteTask);
        NotificationCenter.getInstance(currentAccount).removeObserver(this, NotificationCenter.messagePlayingDidStart);
        NotificationCenter.getInstance(currentAccount).removeObserver(this, NotificationCenter.updateMessageMedia);
        NotificationCenter.getInstance(currentAccount).removeObserver(this, NotificationCenter.didUpdateMessagesViews);
        NotificationCenter.getInstance(currentAccount).removeObserver(this, NotificationCenter.chatInfoCantLoad);

        if (AndroidUtilities.isTablet()) {
            NotificationCenter.getInstance(currentAccount).postNotificationName(NotificationCenter.openedChatChanged, dialog_id, true);
        }
        AndroidUtilities.removeAdjustResize(getParentActivity(), classGuid);
        if (stickersAdapter != null) {
            stickersAdapter.onDestroy();
        }
        if (chatAttachAlert != null) {
            chatAttachAlert.onDestroy();
        }
        AndroidUtilities.unlockOrientation(getParentActivity());
    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getParentActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public View createView(final Context context) {

        if (chatMessageCellsCache.isEmpty()) {
            for (int a = 0; a < 8; a++) {
                chatMessageCellsCache.add(new ChatMessageCell(context));
            }
        }
        for (int a = 1; a >= 0; a--) {
            selectedMessagesIds[a].clear();
            selectedMessagesCanCopyIds[a].clear();
        }
        cantDeleteMessagesCount = 0;

        hasOwnBackground = true;
        if (chatAttachAlert != null) {
            chatAttachAlert.onDestroy();
            chatAttachAlert = null;
        }

        Theme.createChatResources(context, false);

        actionBar.setTitle(LocaleController.getString("DownloadManager", R.string.DownloadManager));
        actionBar.setAddToContainer(false);
        actionBar.setBackButtonDrawable(new BackDrawable(false));
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(final int id) {
                if (id == -1) {
                    if (actionBar.isActionModeShowed()) {
                        for (int a = 1; a >= 0; a--) {
                            selectedMessagesIds[a].clear();
                            selectedMessagesCanCopyIds[a].clear();
                        }
                        actionBar.hideActionMode();
                        cantDeleteMessagesCount = 0;
                        updateVisibleRows();
                    } else {
                        finishFragment();
                    }
                } else if (id == delete_downloaded) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                    builder.setMessage(LocaleController.getString("AreYouSureToContinue", R.string.AreYouSureToContinue));
                    builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                    builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ArrayList<TLRPC.Message> msgs = new ArrayList<>();
                            for (int a = 0; a < messages.size(); a++) {
                                MessageObject messageObject = messages.get(a);
                                msgs.add(messageObject.messageOwner);
                            }
                            clearChatData();
                            DM_DeleteDownloaded(msgs);
                            DM_LoadMessagesByClassGuid(classGuid);
                            if (chatAdapter != null) {
                                chatAdapter.notifyDataSetChanged();
                            }
                            if (messages.isEmpty()) {
                                progressView.setVisibility(View.INVISIBLE);
                                chatListView.setEmptyView(emptyViewContainer);
                            }
                        }
                    });
                    builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                    showDialog(builder.create());
                } else if (id == delete_all) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                    builder.setMessage(LocaleController.getString("AreYouSureToContinue", R.string.AreYouSureToContinue));
                    builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                    builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            DM_DeleteAll();
                            messages.clear();
                            if (chatAdapter != null) {
                                chatAdapter.notifyDataSetChanged();
                            }
                            if (messages.isEmpty()) {
                                progressView.setVisibility(View.INVISIBLE);
                                chatListView.setEmptyView(emptyViewContainer);
                            }
                        }
                    });
                    builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                    showDialog(builder.create());
                } else if (id == settings) {
                    presentFragment(new DownloadSettingsActivity());
                } else if (id == copy) {
                    String str = "";
                    long previousUid = 0;
                    for (int a = 1; a >= 0; a--) {
                        ArrayList<Integer> ids = new ArrayList<>(selectedMessagesCanCopyIds[a].keySet());
                        Collections.sort(ids);
                        for (int b = 0; b < ids.size(); b++) {
                            Integer messageId = ids.get(b);
                            MessageObject messageObject = selectedMessagesCanCopyIds[a].get(messageId);
                            if (str.length() != 0) {
                                str += "\n\n";
                            }
                            str += getMessageContent(messageObject, previousUid, true);
                            previousUid = messageObject.messageOwner.from_id.user_id;
                        }
                    }
                    if (str.length() != 0) {
                        AndroidUtilities.addToClipboard(str);
                    }
                    for (int a = 1; a >= 0; a--) {
                        selectedMessagesIds[a].clear();
                        selectedMessagesCanCopyIds[a].clear();
                    }
                    cantDeleteMessagesCount = 0;
                    actionBar.hideActionMode();
                    updateVisibleRows();
                } else if (id == delete) {
                    if (getParentActivity() == null) {
                        return;
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                    builder.setMessage(LocaleController.getString("AreYouSureToContinue", R.string.AreYouSureToContinue));
                    builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                    builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ArrayList<TLRPC.Message> msgs = new ArrayList<>();
                            for (int a = 1; a >= 0; a--) {
                                for (HashMap.Entry<Integer, MessageObject> entry : selectedMessagesIds[a].entrySet()) {
                                    MessageObject messageObject = entry.getValue();
                                    msgs.add(messageObject.messageOwner);
                                }
                            }
                            clearChatData();
                            DM_DeleteMessage(msgs);
                            DM_LoadMessagesByClassGuid(classGuid);
                            if (chatAdapter != null) {
                                chatAdapter.notifyDataSetChanged();
                            }
                            if (messages.isEmpty()) {
                                progressView.setVisibility(View.INVISIBLE);
                                chatListView.setEmptyView(emptyViewContainer);
                            }
                            actionBar.hideActionMode();
                        }
                    });
                    builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                    showDialog(builder.create());
                } else if (id == quoteforward) {
                    if (getParentActivity() == null) {
                        return;
                    }
                    ArrayList<MessageObject> msgObj = new ArrayList<MessageObject>();
                    for (int a = 1; a >= 0; a--) {
                        ArrayList<Integer> ids = new ArrayList<>(selectedMessagesIds[a].keySet());
                        Collections.sort(ids);
                        for (int b = 0; b < ids.size(); b++) {
                            Integer messageId = ids.get(b);
                            MessageObject messageObject = selectedMessagesIds[a].get(messageId);
                            msgObj.add(messageObject);
                        }
                    }
                    showDialog(new ShareAlert(context, msgObj, null, false, null, false));

                    for (int a = 1; a >= 0; a--) {
                        selectedMessagesIds[a].clear();
                    }
                    actionBar.hideActionMode();
                    updateVisibleRows();
                } else if (id == forward) {
                    if (getParentActivity() == null) {
                        return;
                    }
                    ArrayList<MessageObject> msgObj = new ArrayList<MessageObject>();
                    for (int a = 1; a >= 0; a--) {
                        ArrayList<Integer> ids = new ArrayList<>(selectedMessagesIds[a].keySet());
                        Collections.sort(ids);
                        for (int b = 0; b < ids.size(); b++) {
                            Integer messageId = ids.get(b);
                            MessageObject messageObject = selectedMessagesIds[a].get(messageId);
                            msgObj.add(messageObject);
                        }
                    }
                    showDialog(new ShareAlert(context, msgObj, null, false, null, false));

                    for (int a = 1; a >= 0; a--) {
                        selectedMessagesIds[a].clear();
                    }
                    actionBar.hideActionMode();
                    updateVisibleRows();
                }
            }
        });

        ActionBarMenu menu = actionBar.createMenu();

        ActionBarMenuItem setting = menu.addItem(1, R.drawable.msg_settings);
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presentFragment(new DownloadSettingsActivity());
            }
        });

        headerItem = menu.addItem(0, R.drawable.ic_ab_other);
        headerItem.addSubItem(delete_downloaded, LocaleController.getString("DownloaderDelDownloaded", R.string.DownloaderDelDownloaded));
        headerItem.addSubItem(delete_all, LocaleController.getString("DownloaderDelAll", R.string.DownloaderDelAll));
        headerItem.addSubItem(settings, LocaleController.getString("DownloaderSettings", R.string.DownloaderSettings));

        actionModeViews.clear();

        final ActionBarMenu actionMode = actionBar.createActionMode();

        selectedMessagesCountTextView = new NumberTextView(actionMode.getContext());
        selectedMessagesCountTextView.setTextSize(18);
        selectedMessagesCountTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        selectedMessagesCountTextView.setTextColor(Theme.getColor(Theme.key_actionBarActionModeDefaultIcon));
        actionMode.addView(selectedMessagesCountTextView, LayoutHelper.createLinear(0, LayoutHelper.MATCH_PARENT, 1.0f, 65, 0, 0, 0));
        selectedMessagesCountTextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        actionModeTitleContainer = new FrameLayout(context) {

            @Override
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int width = MeasureSpec.getSize(widthMeasureSpec);
                int height = MeasureSpec.getSize(heightMeasureSpec);

                setMeasuredDimension(width, height);

                actionModeTextView.setTextSize(!AndroidUtilities.isTablet() && getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? 18 : 20);
                actionModeTextView.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(24), MeasureSpec.AT_MOST));

                if (actionModeSubTextView.getVisibility() != GONE) {
                    actionModeSubTextView.setTextSize(!AndroidUtilities.isTablet() && getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? 14 : 16);
                    actionModeSubTextView.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(20), MeasureSpec.AT_MOST));
                }
            }

            @Override
            protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                int height = bottom - top;

                int textTop;
                if (actionModeSubTextView.getVisibility() != GONE) {
                    textTop = (height / 2 - actionModeTextView.getTextHeight()) / 2 + AndroidUtilities.dp(!AndroidUtilities.isTablet() && getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? 2 : 3);
                } else {
                    textTop = (height - actionModeTextView.getTextHeight()) / 2;
                }
                actionModeTextView.layout(0, textTop, actionModeTextView.getMeasuredWidth(), textTop + actionModeTextView.getTextHeight());

                if (actionModeSubTextView.getVisibility() != GONE) {
                    textTop = height / 2 + (height / 2 - actionModeSubTextView.getTextHeight()) / 2 - AndroidUtilities.dp(!AndroidUtilities.isTablet() && getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? 1 : 1);
                    actionModeSubTextView.layout(0, textTop, actionModeSubTextView.getMeasuredWidth(), textTop + actionModeSubTextView.getTextHeight());
                }
            }
        };
        actionMode.addView(actionModeTitleContainer, LayoutHelper.createLinear(0, LayoutHelper.MATCH_PARENT, 1.0f, 65, 0, 0, 0));
        actionModeTitleContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        actionModeTitleContainer.setVisibility(View.GONE);

        actionModeTextView = new SimpleTextView(context);
        actionModeTextView.setTextSize(18);
        actionModeTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        actionModeTextView.setTextColor(Theme.getColor(Theme.key_actionBarActionModeDefaultIcon));
        actionModeTextView.setText(LocaleController.getString("Edit", R.string.Edit));
        actionModeTitleContainer.addView(actionModeTextView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        actionModeSubTextView = new SimpleTextView(context);
        actionModeSubTextView.setGravity(Gravity.LEFT);
        actionModeSubTextView.setTextColor(Theme.getColor(Theme.key_actionBarActionModeDefaultIcon));
        actionModeTitleContainer.addView(actionModeSubTextView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        actionModeViews.add(actionMode.addItem(copy, R.drawable.msg_copy, AndroidUtilities.dp(54)));
        actionModeViews.add(actionMode.addItem(forward, R.drawable.msg_forward, AndroidUtilities.dp(54)));
        actionModeViews.add(actionMode.addItem(delete, R.drawable.msg_delete, AndroidUtilities.dp(54)));

        actionMode.getItem(copy).setVisibility(selectedMessagesCanCopyIds[0].size() + selectedMessagesCanCopyIds[1].size() != 0 ? View.VISIBLE : View.GONE);
        actionMode.getItem(delete).setVisibility(cantDeleteMessagesCount == 0 ? View.VISIBLE : View.GONE);
        checkActionBarMenu();


        fragmentView = new SizeNotifierFrameLayout(context) {

            int inputFieldHeight = AndroidUtilities.dp(56);

            @Override
            protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
                boolean result = super.drawChild(canvas, child, drawingTime);
                if (child == actionBar) {
                    parentLayout.drawHeaderShadow(canvas, actionBar.getMeasuredHeight());
                }
                return result;
            }

            @Override
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int widthSize = MeasureSpec.getSize(widthMeasureSpec);
                int heightSize = MeasureSpec.getSize(heightMeasureSpec);

                setMeasuredDimension(widthSize, heightSize);
                heightSize -= getPaddingTop();

                measureChildWithMargins(actionBar, widthMeasureSpec, 0, heightMeasureSpec, 0);
                int actionBarHeight = actionBar.getMeasuredHeight();
                heightSize -= actionBarHeight;

                int keyboardSize = getKeyboardHeight();
                int childCount = getChildCount();

                for (int i = 0; i < childCount; i++) {
                    View child = getChildAt(i);
                    if (child == null || child.getVisibility() == GONE || child == actionBar) {
                        continue;
                    }
                    if (child == chatListView || child == progressView) {
                        int contentWidthSpec = MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY);
                        int contentHeightSpec = MeasureSpec.makeMeasureSpec(Math.max(AndroidUtilities.dp(10), heightSize - inputFieldHeight + AndroidUtilities.dp(2)), MeasureSpec.EXACTLY);
                        child.measure(contentWidthSpec, contentHeightSpec);
                    } else if (child == emptyViewContainer) {
                        int contentWidthSpec = MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY);
                        int contentHeightSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY);
                        child.measure(contentWidthSpec, contentHeightSpec);
                    } else {
                        measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
                    }
                }
            }

            @Override
            protected void onLayout(boolean changed, int l, int t, int r, int b) {
                final int count = getChildCount();

                int paddingBottom = 0;
                setBottomClip(paddingBottom);

                for (int i = 0; i < count; i++) {
                    final View child = getChildAt(i);
                    if (child.getVisibility() == GONE) {
                        continue;
                    }
                    final LayoutParams lp = (LayoutParams) child.getLayoutParams();

                    final int width = child.getMeasuredWidth();
                    final int height = child.getMeasuredHeight();

                    int childLeft;
                    int childTop;

                    int gravity = lp.gravity;
                    if (gravity == -1) {
                        gravity = Gravity.TOP | Gravity.LEFT;
                    }

                    final int absoluteGravity = gravity & Gravity.HORIZONTAL_GRAVITY_MASK;
                    final int verticalGravity = gravity & Gravity.VERTICAL_GRAVITY_MASK;

                    switch (absoluteGravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
                        case Gravity.CENTER_HORIZONTAL:
                            childLeft = (r - l - width) / 2 + lp.leftMargin - lp.rightMargin;
                            break;
                        case Gravity.RIGHT:
                            childLeft = r - width - lp.rightMargin;
                            break;
                        case Gravity.LEFT:
                        default:
                            childLeft = lp.leftMargin;
                    }

                    switch (verticalGravity) {
                        case Gravity.TOP:
                            childTop = lp.topMargin + getPaddingTop();
                            if (child != actionBar) {
                                childTop += actionBar.getMeasuredHeight();
                            }
                            break;
                        case Gravity.CENTER_VERTICAL:
                            childTop = ((b - paddingBottom) - t - height) / 2 + lp.topMargin - lp.bottomMargin;
                            break;
                        case Gravity.BOTTOM:
                            childTop = ((b - paddingBottom) - t) - height - lp.bottomMargin;
                            break;
                        default:
                            childTop = lp.topMargin;
                    }

                    if (child == emptyViewContainer) {
                        childTop -= inputFieldHeight / 2 - actionBar.getMeasuredHeight() / 2;
                    } else if (child == gifHintTextView) {
                        childTop -= inputFieldHeight;
                    } else if (child == chatListView || child == progressView) {

                    } else if (child == actionBar) {
                        childTop -= getPaddingTop();
                    }
                    child.layout(childLeft, childTop, childLeft + width, childTop + height);
                }

                updateMessagesVisisblePart();
                notifyHeightChanged();
            }
        };

        SizeNotifierFrameLayout contentView = (SizeNotifierFrameLayout) fragmentView;

        contentView.setBackgroundImage(Theme.getCachedWallpaper(), false);

        emptyViewContainer = new FrameLayout(context);
        emptyViewContainer.setVisibility(View.INVISIBLE);
        contentView.addView(emptyViewContainer, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER));
        emptyViewContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        TextView emptyView = new TextView(context);
        emptyView.setText(LocaleController.getString("DownloadQueueIsEmpty", R.string.DownloadQueueIsEmpty));
        emptyView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        emptyView.setGravity(Gravity.CENTER);
        emptyView.setTextColor(Theme.getColor(Theme.key_chat_serviceText));
        emptyView.setBackgroundResource(R.drawable.system);
//        emptyView.getBackground().setColorFilter(Theme.colorFilter);
        emptyView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        emptyView.setPadding(AndroidUtilities.dp(10), AndroidUtilities.dp(2), AndroidUtilities.dp(10), AndroidUtilities.dp(3));
        emptyViewContainer.addView(emptyView, new FrameLayout.LayoutParams(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER));

        chatListView = new RecyclerListView(context) {
            @Override
            protected void onLayout(boolean changed, int l, int t, int r, int b) {
                super.onLayout(changed, l, t, r, b);
                forceScrollToTop = false;
            }
        };
        chatListView.setTag(1);
        chatListView.setVerticalScrollBarEnabled(true);
        chatListView.setAdapter(chatAdapter = new ChatActivityAdapter(context));
        chatListView.setClipToPadding(false);
        chatListView.setPadding(0, AndroidUtilities.dp(4), 0, AndroidUtilities.dp(3));
        chatListView.setItemAnimator(null);
        chatListView.setLayoutAnimation(null);
        chatLayoutManager = new LinearLayoutManager(context) {
            @Override
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        chatLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        chatLayoutManager.setStackFromEnd(true);
        chatListView.setLayoutManager(chatLayoutManager);
        contentView.addView(chatListView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        chatListView.setOnItemLongClickListener(onItemLongClickListener);
        chatListView.setOnItemClickListener(onItemClickListener);
        chatListView.setOnScrollListener(new RecyclerView.OnScrollListener() {

            private float totalDy = 0;
            private final int scrollValue = AndroidUtilities.dp(100);

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING && highlightMessageId != Integer.MAX_VALUE) {
                    highlightMessageId = Integer.MAX_VALUE;
                    updateVisibleRows();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                checkScrollForLoad(true);
                int firstVisibleItem = chatLayoutManager.findFirstVisibleItemPosition();
                int visibleItemCount = firstVisibleItem == RecyclerView.NO_POSITION ? 0 : Math.abs(chatLayoutManager.findLastVisibleItemPosition() - firstVisibleItem) + 1;
                if (visibleItemCount > 0) {
                    int totalItemCount = chatAdapter.getItemCount();
                    if (firstVisibleItem + visibleItemCount == totalItemCount && forwardEndReached[0]) {
                    }
                }
                updateMessagesVisisblePart();
            }
        });
        chatListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });

        progressView = new FrameLayout(context);
        progressView.setVisibility(View.INVISIBLE);
        contentView.addView(progressView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.TOP | Gravity.LEFT));

        progressView2 = new View(context);
        progressView2.setBackgroundResource(R.drawable.loading_animation);
//        progressView2.getBackground().setColorFilter(Theme.colorFilter);
        progressView.addView(progressView2, LayoutHelper.createFrame(36, 36, Gravity.CENTER));

        progressBar = new RadialProgressView(context);
        progressBar.setSize(AndroidUtilities.dp(28));
        progressBar.setProgressColor(Theme.getColor(Theme.key_chat_serviceText));
        progressView.addView(progressBar, LayoutHelper.createFrame(32, 32, Gravity.CENTER));

        alertView = new FrameLayout(context);
        alertView.setTag(1);
        alertView.setTranslationY(-AndroidUtilities.dp(50));
        alertView.setVisibility(View.GONE);
        alertView.setBackgroundResource(R.drawable.blockpanel);
        contentView.addView(alertView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 50, Gravity.TOP | Gravity.LEFT));

        alertNameTextView = new TextView(context);
        alertNameTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        alertNameTextView.setTextColor(Theme.getColor(Theme.key_chat_topPanelTitle));
        alertNameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        alertNameTextView.setSingleLine(true);
        alertNameTextView.setEllipsize(TextUtils.TruncateAt.END);
        alertNameTextView.setMaxLines(1);
        alertView.addView(alertNameTextView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.TOP | Gravity.LEFT, 8, 5, 8, 0));

        alertTextView = new TextView(context);
        alertTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        alertTextView.setTextColor(Theme.getColor(Theme.key_chat_topPanelMessage));
        alertTextView.setSingleLine(true);
        alertTextView.setEllipsize(TextUtils.TruncateAt.END);
        alertTextView.setMaxLines(1);
        alertView.addView(alertTextView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.TOP | Gravity.LEFT, 8, 23, 8, 0));

        FrameLayout replyLayout = new FrameLayout(context) {
            @Override
            public void setTranslationY(float translationY) {
                super.setTranslationY(translationY);
                if (getVisibility() != GONE) {
                    int height = getLayoutParams().height;
                    if (chatListView != null) {
                        chatListView.setTranslationY(translationY);
                    }
                    if (progressView != null) {
                        progressView.setTranslationY(translationY);
                    }
                    if (pagedownButton != null) {
                        pagedownButton.setTranslationY(translationY);
                    }
                }
            }

            @Override
            public boolean hasOverlappingRendering() {
                return false;
            }

            @Override
            public void setVisibility(int visibility) {
                super.setVisibility(visibility);
                if (visibility == GONE) {
                    if (chatListView != null) {
                        chatListView.setTranslationY(0);
                    }
                    if (progressView != null) {
                        progressView.setTranslationY(0);
                    }
                    if (pagedownButton != null) {
                        pagedownButton.setTranslationY(pagedownButton.getTag() == null ? AndroidUtilities.dp(100) : 0);
                    }
                }
            }
        };
        replyLayout.setClickable(true);

        View lineView = new View(context);
        lineView.setBackgroundColor(0xffe8e8e8);
        replyLayout.addView(lineView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 1, Gravity.BOTTOM | Gravity.LEFT));

        replyIconImageView = new ImageView(context);
        replyIconImageView.setScaleType(ImageView.ScaleType.CENTER);
        replyLayout.addView(replyIconImageView, LayoutHelper.createFrame(52, 46, Gravity.TOP | Gravity.LEFT));

        replyCloseImageView = new ImageView(context);
        replyCloseImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_replyPanelClose), PorterDuff.Mode.MULTIPLY));
        replyCloseImageView.setImageResource(R.drawable.msg_panel_clear);
        replyCloseImageView.setScaleType(ImageView.ScaleType.CENTER);
        replyLayout.addView(replyCloseImageView, LayoutHelper.createFrame(52, 46, Gravity.RIGHT | Gravity.TOP, 0, 0.5f, 0, 0));
        replyCloseImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (forwardingMessages != null) {
                    forwardingMessages.clear();
                }
            }
        });

        replyNameTextView = new SimpleTextView(context);
        replyNameTextView.setTextSize(14);
        replyNameTextView.setTextColor(Theme.getColor(Theme.key_chat_replyPanelName));
        replyNameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        replyLayout.addView(replyNameTextView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 18, Gravity.TOP | Gravity.LEFT, 52, 6, 52, 0));

        replyObjectTextView = new SimpleTextView(context);
        replyObjectTextView.setTextSize(14);
        replyObjectTextView.setTextColor(Theme.getColor(Theme.key_chat_replyPanelMessage));
        replyLayout.addView(replyObjectTextView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 18, Gravity.TOP | Gravity.LEFT, 52, 24, 52, 0));

        replyImageView = new BackupImageView(context);
        replyLayout.addView(replyImageView, LayoutHelper.createFrame(34, 34, Gravity.TOP | Gravity.LEFT, 52, 6, 0, 0));

        stickersPanel = new FrameLayout(context);
        stickersPanel.setVisibility(View.GONE);
        contentView.addView(stickersPanel, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, 81.5f, Gravity.LEFT | Gravity.BOTTOM, 0, 0, 0, 38));

        stickersListView = new RecyclerListView(context) {
            @Override
            public boolean onInterceptTouchEvent(MotionEvent event) {
                boolean result = ContentPreviewViewer.getInstance().onInterceptTouchEvent(event, stickersListView, 0, null,null);
                return super.onInterceptTouchEvent(event) || result;
            }
        };
        stickersListView.setTag(3);
        stickersListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return ContentPreviewViewer.getInstance().onTouch(event, stickersListView, 0, stickersOnItemClickListener, null,null);
            }
        });
        stickersListView.setDisallowInterceptTouchEvents(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        stickersListView.setLayoutManager(layoutManager);
        stickersListView.setClipToPadding(false);
        stickersListView.setOverScrollMode(RecyclerListView.OVER_SCROLL_NEVER);
        stickersPanel.addView(stickersListView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 78));

        stickersPanelArrow = new ImageView(context);
        stickersPanelArrow.setImageResource(R.drawable.stickers_back_arrow);
        stickersPanelArrow.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_stickersHintPanel), PorterDuff.Mode.MULTIPLY));
        stickersPanel.addView(stickersPanelArrow, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.BOTTOM | Gravity.LEFT, 53, 0, 0, 0));

        bottomOverlay = new FrameLayout(context) {
            @Override
            public void onDraw(Canvas canvas) {
                int bottom = Theme.chat_composeShadowDrawable.getIntrinsicHeight();
                Theme.chat_composeShadowDrawable.setBounds(0, 0, getMeasuredWidth(), bottom);
                Theme.chat_composeShadowDrawable.draw(canvas);
                canvas.drawRect(0, bottom, getMeasuredWidth(), getMeasuredHeight(), Theme.chat_composeBackgroundPaint);
            }
        };
        bottomOverlay.setWillNotDraw(false);
        bottomOverlay.setFocusable(true);
        bottomOverlay.setFocusableInTouchMode(true);
        bottomOverlay.setClickable(true);
        bottomOverlay.setPadding(0, AndroidUtilities.dp(3), 0, 0);
        contentView.addView(bottomOverlay, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 56, Gravity.BOTTOM));

        bottomOverlayText = new TextView(context);
        if (downloaderRunning) {
            bottomOverlayText.setText(LocaleController.getString("StopDownloader", R.string.StopDownloader));
        } else {
            bottomOverlayText.setText(LocaleController.getString("StartDownloader", R.string.StartDownloader));
        }
        bottomOverlayText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
        bottomOverlayText.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        bottomOverlayText.setTextColor(Theme.getColor(Theme.key_chat_secretChatStatusText));
        bottomOverlay.addView(bottomOverlayText, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER));
        bottomOverlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloaderRunning = !downloaderRunning;
                if (downloaderRunning) {
                    startDownloading(messages);
                    bottomOverlayText.setText(LocaleController.getString("StopDownloader", R.string.StopDownloader));
                } else {
                    stopDownloading();
                    bottomOverlayText.setText(LocaleController.getString("StartDownloader", R.string.StartDownloader));
                }
            }
        });

        chatAdapter.updateRows();
        if (loading && messages.isEmpty()) {
            progressView.setVisibility(chatAdapter.botInfoRow == -1 ? View.VISIBLE : View.INVISIBLE);
            chatListView.setEmptyView(null);
        } else {
            progressView.setVisibility(View.INVISIBLE);
            chatListView.setEmptyView(emptyViewContainer);
        }

        if (!AndroidUtilities.isTablet() || AndroidUtilities.isSmallTablet()) {
            contentView.addView(fragmentContextView = new FragmentContextView(context, this, false), LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 39, Gravity.TOP | Gravity.LEFT, 0, -36, 0, 0));
        }

        try {
            if (Build.VERSION.SDK_INT >= 23) {
                getParentActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
            }
        } catch (Throwable e) {
            FileLog.e("tmessages", e);
        }
        fixLayoutInternal();

        contentView.addView(actionBar);

        return fragmentView;
    }

    public long getDialogId() {
        return dialog_id;
    }

    @Override
    public boolean dismissDialogOnPause(Dialog dialog) {
        return dialog != chatAttachAlert && super.dismissDialogOnPause(dialog);
    }

    private void forwardMessages(ArrayList<MessageObject> arrayList, boolean fromMyName) {
        if (arrayList == null || arrayList.isEmpty()) {
            return;
        }
        if (!fromMyName) {
            SendMessagesHelper.getInstance(currentAccount).sendMessage(arrayList, dialog_id, true,false,false, 0);
        } else {
            for (MessageObject object : arrayList) {
                SendMessagesHelper.getInstance(currentAccount).processForwardFromMyName(object, dialog_id);
            }
        }
    }

    private void moveScrollToLastMessage() {
        if (chatListView != null && !messages.isEmpty()) {
            chatLayoutManager.scrollToPositionWithOffset(messages.size() - 1, -100000 - chatListView.getPaddingTop());
        }
    }

    private void clearChatData() {
        messages.clear();
        messagesByDays.clear();
        waitingForLoad.clear();

        progressView.setVisibility(chatAdapter.botInfoRow == -1 ? View.VISIBLE : View.INVISIBLE);
        chatListView.setEmptyView(null);
        for (int a = 0; a < 2; a++) {
            messagesDict[a].clear();
            maxMessageId[a] = Integer.MAX_VALUE;
            minMessageId[a] = Integer.MIN_VALUE;
            maxDate[a] = Integer.MIN_VALUE;
            minDate[a] = 0;
            endReached[a] = false;
            cacheEndReached[a] = false;
            forwardEndReached[a] = true;
        }
        first = true;
        firstLoading = true;
        loading = true;
        loadingForward = false;
        waitingForReplyMessageLoad = false;
        startLoadFromMessageId = 0;
        last_message_id = 0;
        needSelectFromMessageId = false;
        chatAdapter.notifyDataSetChanged();
    }

    private void updateMessagesVisisblePart() {
        if (chatListView == null) {
            return;
        }
        int count = chatListView.getChildCount();
        int additionalTop = 0;
        int height = chatListView.getMeasuredHeight();
        for (int a = 0; a < count; a++) {
            View view = chatListView.getChildAt(a);
            if (view instanceof ChatMessageCell) {
                ChatMessageCell messageCell = (ChatMessageCell) view;
                int top = messageCell.getTop();
                int bottom = messageCell.getBottom();
                int viewTop = top >= 0 ? 0 : -top;
                int viewBottom = messageCell.getMeasuredHeight();
                if (viewBottom > height) {
                    viewBottom = viewTop + height;
                }
                messageCell.setVisiblePart(viewTop, viewBottom - viewTop, 0, 0,top,0,height,0,0);
            }
        }
    }

    private void scrollToMessageId(int id, int fromMessageId, boolean select, int loadIndex) {
        MessageObject object = messagesDict[loadIndex].get(id);
        boolean query = false;
        if (object != null) {
            int index = messages.indexOf(object);
            if (index != -1) {
                if (select) {
                    highlightMessageId = id;
                } else {
                    highlightMessageId = Integer.MAX_VALUE;
                }
                final int yOffset = Math.max(0, (chatListView.getHeight() - object.getApproximateHeight()) / 2);
                if (messages.get(messages.size() - 1) == object) {
                    chatLayoutManager.scrollToPositionWithOffset(0, -chatListView.getPaddingTop() - AndroidUtilities.dp(7) + yOffset);
                } else {
                    chatLayoutManager.scrollToPositionWithOffset(chatAdapter.messagesStartRow + messages.size() - messages.indexOf(object) - 1, -chatListView.getPaddingTop() - AndroidUtilities.dp(7) + yOffset);
                }
                updateVisibleRows();
                boolean found = false;
                int count = chatListView.getChildCount();
                for (int a = 0; a < count; a++) {
                    View view = chatListView.getChildAt(a);
                    if (view instanceof ChatMessageCell) {
                        ChatMessageCell cell = (ChatMessageCell) view;
                        MessageObject messageObject = cell.getMessageObject();
                        if (messageObject != null && messageObject.getId() == object.getId()) {
                            found = true;
                            break;
                        }
                    } else if (view instanceof ChatActionCell) {
                        ChatActionCell cell = (ChatActionCell) view;
                        MessageObject messageObject = cell.getMessageObject();
                        if (messageObject != null && messageObject.getId() == object.getId()) {
                            found = true;
                            break;
                        }
                    }
                }
            } else {
                query = true;
            }
        } else {
            query = true;
        }

        if (query) {
            /*clearChatData();
            loadsCount = 0;
            unread_to_load = 0;
            first_unread_id = 0;
            loadingForward = false;
            unreadMessageObject = null;
            scrollToMessage = null;*/

            waitingForLoad.clear();
            waitingForReplyMessageLoad = true;
            highlightMessageId = Integer.MAX_VALUE;
            scrollToMessagePosition = -10000;
            startLoadFromMessageId = id;
            waitingForLoad.add(lastLoadIndex);
            MessagesController.getInstance(currentAccount).loadMessages(
                     dialog_id ,
                    mergeDialogId,
                    false,
                    startLoadFromMessageId,
                    0,
                    0,
                    true,
                    0,
                    classGuid,
                    3,
                    0,
                    0,
                    0,
                    0,
                    lastLoadIndex++,
                    false);

            //emptyViewContainer.setVisibility(View.INVISIBLE);
        }
        returnToMessageId = fromMessageId;
        returnToLoadIndex = loadIndex;
        needSelectFromMessageId = select;
    }

    @Override
    public void onRequestPermissionsResultFragment(int requestCode, String[] permissions, int[] grantResults) {

    }

    private void checkActionBarMenu() {
        if (menuItem != null) {
            menuItem.setVisibility(View.GONE);
        }
        if (timeItem2 != null) {
            timeItem2.setVisibility(View.GONE);
        }
        if (avatarContainer != null) {
            avatarContainer.hideTimeItem(false);
        }
    }

    private int getMessageType(MessageObject messageObject) {
        if (messageObject == null) {
            return -1;
        }
        boolean isBroadcastError = messageObject.getId() <= 0 && messageObject.isSendError();
        if (messageObject.getId() <= 0 && messageObject.isOut() || isBroadcastError) {
            if (messageObject.isSendError()) {
                if (!messageObject.isMediaEmpty()) {
                    return 0;
                } else {
                    return 20;
                }
            } else {
                return -1;
            }
        } else {
            if (messageObject.type == 6) {
                return -1;
            } else if (messageObject.type == 10 || messageObject.type == 11) {
                if (messageObject.getId() == 0) {
                    return -1;
                }
                return 1;
            } else {
                if (messageObject.isVoice()) {
                    return 2;
                } else if (messageObject.isSticker()) {
                    TLRPC.InputStickerSet inputStickerSet = messageObject.getInputStickerSet();
                    if (inputStickerSet instanceof TLRPC.TL_inputStickerSetID) {
                        if (!MediaDataController.getInstance(currentAccount).isStickerPackInstalled(inputStickerSet.id)) {
                            return 7;
                        }
                    } else if (inputStickerSet instanceof TLRPC.TL_inputStickerSetShortName) {
                        if (!MediaDataController.getInstance(currentAccount).isStickerPackInstalled(inputStickerSet.short_name)) {
                            return 7;
                        }
                    }
                } else if (messageObject.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto || messageObject.getDocument() != null || messageObject.isMusic() || messageObject.isVideo()) {
                    boolean canSave = false;
                    if (messageObject.messageOwner.attachPath != null && messageObject.messageOwner.attachPath.length() != 0) {
                        File f = new File(messageObject.messageOwner.attachPath);
                        if (f.exists()) {
                            canSave = true;
                        }
                    }
                    if (!canSave) {
                        File f = FileLoader.getInstance(currentAccount).getPathToMessage(messageObject.messageOwner);
                        if (f.exists()) {
                            canSave = true;
                        }
                    }
                    if (canSave) {
                        if (messageObject.getDocument() != null) {
                            String mime = messageObject.getDocument().mime_type;
                            if (mime != null) {
                                if (mime.endsWith("/xml")) {
                                    return 5;
                                } else if (mime.endsWith("/png") || mime.endsWith("/jpg") || mime.endsWith("/jpeg")) {
                                    return 6;
                                }
                            }
                        }
                        return 4;
                    }
                } else if (messageObject.type == 12) {
                    return 8;
                } else if (messageObject.isMediaEmpty()) {
                    return 3;
                }
                return 2;
            }
        }
    }

    private void addToSelectedMessages(MessageObject messageObject) {
        int index = messageObject.getDialogId() == dialog_id ? 0 : 1;
        if (selectedMessagesIds[index].containsKey(messageObject.getId())) {
            selectedMessagesIds[index].remove(messageObject.getId());
            if (messageObject.type == 0 || messageObject.caption != null) {
                selectedMessagesCanCopyIds[index].remove(messageObject.getId());
            }
        } else {
            selectedMessagesIds[index].put(messageObject.getId(), messageObject);
            if (messageObject.type == 0 || messageObject.caption != null) {
                selectedMessagesCanCopyIds[index].put(messageObject.getId(), messageObject);
            }
        }
        if (actionBar.isActionModeShowed()) {
            if (selectedMessagesIds[0].isEmpty() && selectedMessagesIds[1].isEmpty()) {
                actionBar.hideActionMode();
            } else {
                int copyVisible = actionBar.createActionMode().getItem(copy).getVisibility();
                actionBar.createActionMode().getItem(copy).setVisibility(selectedMessagesCanCopyIds[0].size() + selectedMessagesCanCopyIds[1].size() != 0 ? View.VISIBLE : View.GONE);
                int newCopyVisible = actionBar.createActionMode().getItem(copy).getVisibility();
                actionBar.createActionMode().getItem(delete).setVisibility(cantDeleteMessagesCount == 0 ? View.VISIBLE : View.GONE);
            }
        }
    }

    private void processRowSelect(View view) {
        MessageObject message = null;
        if (view instanceof ChatMessageCell) {
            message = ((ChatMessageCell) view).getMessageObject();
        } else if (view instanceof ChatActionCell) {
            message = ((ChatActionCell) view).getMessageObject();
        }

        int type = getMessageType(message);

        if (type < 2 || type == 20) {
            return;
        }
        addToSelectedMessages(message);
        updateActionModeTitle();
        updateVisibleRows();
    }

    private void updateActionModeTitle() {
        if (!actionBar.isActionModeShowed()) {
            return;
        }
        if (!selectedMessagesIds[0].isEmpty() || !selectedMessagesIds[1].isEmpty()) {
            selectedMessagesCountTextView.setNumber(selectedMessagesIds[0].size() + selectedMessagesIds[1].size(), true);
        }
    }

    @Override
    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void saveSelfArgs(Bundle args) {
        if (currentPicturePath != null) {
            args.putString("path", currentPicturePath);
        }
    }

    @Override
    public void restoreSelfArgs(Bundle args) {
        currentPicturePath = args.getString("path");
    }

    private void removeUnreadPlane() {
        if (unreadMessageObject != null) {
            forwardEndReached[0] = forwardEndReached[1] = true;
            first_unread_id = 0;
            last_message_id = 0;
            unread_to_load = 0;
            removeMessageObject(unreadMessageObject);
            unreadMessageObject = null;
        }
    }

    @Override
    public void didReceivedNotification(int id, int account, final Object... args) {
        if (id == NotificationCenter.messagesDidLoad) {
            if ((int) args[10] == classGuid) {
                final HashMap<Long, ArrayList<MessageObject>> hashMap = new HashMap<Long, ArrayList<MessageObject>>();
                endReached[0] = true;
                endReached[1] = true;

                final ArrayList<MessageObject> messArr = (ArrayList) args[2];
                int fnid = (int) args[4];
                if (fnid != 0) {
                    first_unread_id = fnid;
                    last_message_id = (int) args[5];
                } else {
                    if (messArr.isEmpty()) {
                        first_unread_id = 0;
                    }
                    last_message_id = (int) args[5];
                }
                first_unread_id = 0;
                last_message_id = 0;

                final int load_type = (int) args[8];
                if (firstLoading) {
                    e.clear();
                    g.clear();
                    f.clear();
                    messages.clear();
                    messagesByDays.clear();
                    for (int i = 0; i < 2; ++i) {
                        maxMessageId[i] = Integer.MAX_VALUE;
                        minMessageId[i] = Integer.MIN_VALUE;
                        maxDate[i] = Integer.MIN_VALUE;
                        minDate[i] = 0;
                    }
                    firstLoading = false;
                }

                int newRowsCount = 0;

                MessageObject messageObject;
                ArrayList<MessageObject> dayArray;
                TLRPC.Message message;

                for (int j = 0; j < messArr.size(); ++j) {
                    messageObject = messArr.get(j);
                    if (!hashMap.containsKey(messageObject.getDialogId())) {
                        hashMap.put(messageObject.getDialogId(), new ArrayList<MessageObject>());
                    }
                    hashMap.get(messageObject.getDialogId()).add(messageObject);
                    e.put(messageObject, j);
                    if (messageObject.getId() > 0) {
                        maxMessageId[1] = Math.min(messageObject.getId(), maxMessageId[1]);
                        minMessageId[1] = Math.max(messageObject.getId(), minMessageId[1]);
                    }
                    if (messageObject.messageOwner.date != 0) {
                        maxDate[1] = Math.max(maxDate[1], messageObject.messageOwner.date);
                        if (minDate[1] == 0 || messageObject.messageOwner.date < minDate[1]) {
                            minDate[1] = messageObject.messageOwner.date;
                        }
                    }
                    dayArray = messagesByDays.get(messageObject.dateKey);
                    if (dayArray == null) {
                        dayArray = new ArrayList<MessageObject>();
                        messagesByDays.put(messageObject.dateKey, dayArray);
                        TLRPC.Message dateMsg = new TLRPC.TL_message();
                        dateMsg.message = LocaleController.formatDateChat(messageObject.messageOwner.date);
                        dateMsg.date = messageObject.messageOwner.date - messageObject.messageOwner.date % 86400;
                        dateMsg.id = 0;
                        MessageObject dateObj = new MessageObject(currentAccount, dateMsg, false, false);
                        dateObj.type = 10;
                        dateObj.contentType = 1;
                        if (load_type == 1) {
                            messages.add(0, dateObj);
                        } else {
                            messages.add(dateObj);
                        }
                        newRowsCount++;
                    }
                    dayArray.add(messageObject);
                    messages.add(messages.size() - 1, messageObject);
                }
                for (final long longValue : hashMap.keySet()) {
                    MediaDataController.getInstance(currentAccount).loadReplyMessagesForMessages(messages, longValue, false, 0, null);
                }
                if (first) {
                    if (chatListView != null) {

                    }
                    first = false;
                }


                if (h.size() == f.size()) {
                    if (progressView != null) {
                        progressView.setVisibility(View.INVISIBLE);
                    }
                    if (!messages.isEmpty()) {
                        endReached[0] = true;
                        endReached[1] = true;
                        Collections.sort(messages, new Comparator<MessageObject>() {
                            public int compare(final MessageObject messageObject, final MessageObject messageObject2) {
                                if (messageObject.messageOwner.date > messageObject2.messageOwner.date) {
                                    return -1;
                                }
                                if (messageObject.messageOwner.date < messageObject2.messageOwner.date) {
                                    return 1;
                                }
                                return 0;
                            }
                        });
                        chatAdapter.notifyDataSetChanged();
                        chatListView.setEmptyView(emptyViewContainer);
                        chatListView.setEmptyView(null);
                        moveScrollToLastMessage();
                        return;
                    }
                    endReached[0] = true;
                    endReached[1] = true;
                    chatListView.setEmptyView(emptyViewContainer);
                    updateVisibleRows();
                } else {
                    chatListView.setEmptyView(emptyViewContainer);
                    if (progressView != null) {
                        progressView.setVisibility(View.INVISIBLE);
                    }
                }
            }
        } else if (id == NotificationCenter.emojiLoaded) {
            if (chatListView != null) {
                chatListView.invalidateViews();
            }
            if (replyObjectTextView != null) {
                replyObjectTextView.invalidate();
            }
            if (alertTextView != null) {
                alertTextView.invalidate();
            }
            if (pinnedMessageTextView != null) {
                pinnedMessageTextView.invalidate();
            }
        } else if (id == NotificationCenter.messagePlayingDidReset || id == NotificationCenter.messagePlayingPlayStateChanged) {
            if (chatListView != null) {
                int count = chatListView.getChildCount();
                for (int a = 0; a < count; a++) {
                    View view = chatListView.getChildAt(a);
                    if (view instanceof ContextLinkCell) {
                        ContextLinkCell cell = (ContextLinkCell) view;
                        MessageObject messageObject = cell.getMessageObject();
                        if (messageObject != null && (messageObject.isVoice() || messageObject.isMusic())) {
                            cell.updateButtonState(false, false);
                        }
                    }
                }
            }
        } else if (id == NotificationCenter.messagePlayingProgressDidChanged) {
            Integer mid = (Integer) args[0];
            if (chatListView != null) {
                int count = chatListView.getChildCount();
                for (int a = 0; a < count; a++) {
                    View view = chatListView.getChildAt(a);
                    if (view instanceof ChatMessageCell) {
                        ChatMessageCell cell = (ChatMessageCell) view;
                        if (cell.getMessageObject() != null && cell.getMessageObject().getId() == mid) {
                            MessageObject playing = cell.getMessageObject();
                            MessageObject player = MediaController.getInstance().getPlayingMessageObject();
                            if (player != null) {
                                playing.audioProgress = player.audioProgress;
                                playing.audioProgressSec = player.audioProgressSec;
                                cell.updatePlayingMessageProgress();
                            }
                            break;
                        }
                    }
                }
            }
        } else if (id == NotificationCenter.removeAllMessagesFromDialog) {
            long did = (Long) args[0];
            if (dialog_id == did) {
                messages.clear();
                waitingForLoad.clear();
                messagesByDays.clear();
                for (int a = 1; a >= 0; a--) {
                    messagesDict[a].clear();
                    maxMessageId[a] = Integer.MAX_VALUE;
                    minMessageId[a] = Integer.MIN_VALUE;
                    maxDate[a] = Integer.MIN_VALUE;
                    minDate[a] = 0;
                    selectedMessagesIds[a].clear();
                    selectedMessagesCanCopyIds[a].clear();
                }
                cantDeleteMessagesCount = 0;
                actionBar.hideActionMode();

                if (botButtons != null) {
                    botButtons = null;
                }

                if ((Boolean) args[1]) {
                    if (chatAdapter != null) {
                        progressView.setVisibility(chatAdapter.botInfoRow == -1 ? View.VISIBLE : View.INVISIBLE);
                        chatListView.setEmptyView(null);
                    }
                    for (int a = 0; a < 2; a++) {
                        endReached[a] = false;
                        cacheEndReached[a] = false;
                        forwardEndReached[a] = true;
                    }
                    first = true;
                    firstLoading = true;
                    loading = true;
                    startLoadFromMessageId = 0;
                    needSelectFromMessageId = false;
                    waitingForLoad.add(lastLoadIndex);
                    //MessagesController.getInstance().loadMessages(dialog_id, AndroidUtilities.isTablet() ? 30 : 20, 0, true, 0, classGuid, 2, 0, false, lastLoadIndex++);
                } else {
                    if (progressView != null) {
                        progressView.setVisibility(View.INVISIBLE);
                        chatListView.setEmptyView(emptyViewContainer);
                    }
                }

                if (chatAdapter != null) {
                    chatAdapter.notifyDataSetChanged();
                }
            }
        } else if (id == NotificationCenter.didCreatedNewDeleteTask) {
            SparseArray<ArrayList<Integer>> mids = (SparseArray<ArrayList<Integer>>) args[0];
            boolean changed = false;
            for (int i = 0; i < mids.size(); i++) {
                int key = mids.keyAt(i);
                ArrayList<Integer> arr = mids.get(key);
                for (Integer mid : arr) {
                    MessageObject messageObject = messagesDict[0].get(mid);
                    if (messageObject != null) {
                        messageObject.messageOwner.destroyTime = key;
                        changed = true;
                    }
                }
            }
            if (changed) {
                updateVisibleRows();
            }
        } else if (id == NotificationCenter.updateMessageMedia) {
            MessageObject messageObject = (MessageObject) args[0];
            MessageObject existMessageObject = messagesDict[0].get(messageObject.getId());
            if (existMessageObject != null) {
                existMessageObject.messageOwner.media = messageObject.messageOwner.media;
                existMessageObject.messageOwner.attachPath = messageObject.messageOwner.attachPath;
                existMessageObject.generateThumbs(false);
            }
            updateVisibleRows();
        } else if (id == NotificationCenter.replaceMessagesObjects) {
            long did = (long) args[0];
            if (did != dialog_id && did != mergeDialogId) {
                return;
            }
            int loadIndex = did == dialog_id ? 0 : 1;
            boolean changed = false;
            boolean mediaUpdated = false;
            ArrayList<MessageObject> messageObjects = (ArrayList<MessageObject>) args[1];
            for (int a = 0; a < messageObjects.size(); a++) {
                MessageObject messageObject = messageObjects.get(a);
                MessageObject old = messagesDict[loadIndex].get(messageObject.getId());
                if (pinnedMessageObject != null && pinnedMessageObject.getId() == messageObject.getId()) {
                    pinnedMessageObject = messageObject;
                }
                if (old != null) {
                    if (messageObject.type >= 0) {
                        if (!mediaUpdated && messageObject.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage) {
                            mediaUpdated = true;
                        }
                        if (old.replyMessageObject != null) {
                            messageObject.replyMessageObject = old.replyMessageObject;
                            if (messageObject.messageOwner.action instanceof TLRPC.TL_messageActionGameScore) {
                                messageObject.generateGameMessageText(null);
                            }
                        }
                        messageObject.messageOwner.attachPath = old.messageOwner.attachPath;
                        messageObject.attachPathExists = old.attachPathExists;
                        messageObject.mediaExists = old.mediaExists;
                        messagesDict[loadIndex].put(old.getId(), messageObject);
                    } else {
                        messagesDict[loadIndex].remove(old.getId());
                    }
                    int index = messages.indexOf(old);
                    if (index >= 0) {
                        ArrayList<MessageObject> dayArr = messagesByDays.get(old.dateKey);
                        int index2 = -1;
                        if (dayArr != null) {
                            index2 = dayArr.indexOf(old);
                        }
                        if (messageObject.type >= 0) {
                            messages.set(index, messageObject);
                            if (chatAdapter != null) {
                                chatAdapter.notifyItemChanged(chatAdapter.messagesStartRow + messages.size() - index - 1);
                            }
                            if (index2 >= 0) {
                                dayArr.set(index2, messageObject);
                            }
                        } else {
                            messages.remove(index);
                            if (chatAdapter != null) {
                                chatAdapter.notifyItemRemoved(chatAdapter.messagesStartRow + messages.size() - index - 1);
                            }
                            if (index2 >= 0) {
                                dayArr.remove(index2);
                                if (dayArr.isEmpty()) {
                                    messagesByDays.remove(old.dateKey);
                                    messages.remove(index);
                                    chatAdapter.notifyItemRemoved(chatAdapter.messagesStartRow + messages.size());
                                }
                            }
                        }
                        changed = true;
                    }
                }
            }
            if (changed && chatLayoutManager != null) {
                if (mediaUpdated && chatLayoutManager.findLastVisibleItemPosition() >= messages.size() - (1)) {
                    moveScrollToLastMessage();
                }
            }
        } else if (id == NotificationCenter.didUpdateMessagesViews) {
            SparseArray<SparseIntArray> channelViews = (SparseArray<SparseIntArray>) args[0];
            SparseIntArray array = channelViews.get((int) dialog_id);
            if (array != null) {
                boolean updated = false;
                for (int a = 0; a < array.size(); a++) {
                    int messageId = array.keyAt(a);
                    MessageObject messageObject = messagesDict[0].get(messageId);
                    if (messageObject != null) {
                        int newValue = array.get(messageId);
                        if (newValue > messageObject.messageOwner.views) {
                            messageObject.messageOwner.views = newValue;
                            updated = true;
                        }
                    }
                }
                if (updated) {
                    updateVisibleRows();
                }
            }
        }
    }

    @Override
    public boolean needDelayOpenAnimation() {
        return firstLoading;
    }

    int[] allowedNotifications;

    @Override
    public void onTransitionAnimationStart(boolean isOpen, boolean backward) {
        allowedNotifications = new int[]{NotificationCenter.chatInfoDidLoad, NotificationCenter.dialogsNeedReload,
                NotificationCenter.closeChats, NotificationCenter.messagesDidLoad, NotificationCenter.botKeyboardDidLoad/*, NotificationCenter.botInfoDidLoaded*/};
        NotificationCenter.getInstance(currentAccount).setAnimationInProgress(0, allowedNotifications);
        if (isOpen) {
            openAnimationEnded = false;
        }
    }

    @Override
    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        NotificationCenter.getInstance(currentAccount).setAnimationInProgress(0, allowedNotifications);
        if (isOpen) {
            openAnimationEnded = true;
        }
    }

    @Override
    protected void onDialogDismiss(Dialog dialog) {
        if (closeChatDialog != null && dialog == closeChatDialog) {
            MessagesController.getInstance(currentAccount).deleteDialog(dialog_id, 0);
            if (parentLayout != null && !this.parentLayout.getFragmentStack().isEmpty() && parentLayout.getFragmentStack().get(parentLayout.getFragmentStack().size() - 1) != this) {
                BaseFragment fragment = parentLayout.getFragmentStack().get(parentLayout.getFragmentStack().size() - 1);
                removeSelfFromStack();
                fragment.finishFragment();
            } else {
                finishFragment();
            }
        }
    }

    private void checkListViewPaddings() {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                try {
                    int firstVisPos = chatLayoutManager.findLastVisibleItemPosition();
                    int top = 0;
                    if (firstVisPos != RecyclerView.NO_POSITION) {
                        View firstVisView = chatLayoutManager.findViewByPosition(firstVisPos);
                        top = ((firstVisView == null) ? 0 : firstVisView.getTop()) - chatListView.getPaddingTop();
                    }
                    if (chatListView.getPaddingTop() != AndroidUtilities.dp(52) && (pinnedMessageView != null && pinnedMessageView.getTag() == null || reportSpamView != null && reportSpamView.getTag() == null)) {
                        chatListView.setPadding(0, AndroidUtilities.dp(52), 0, AndroidUtilities.dp(3));
                        chatListView.setTopGlowOffset(AndroidUtilities.dp(48));
                        top -= AndroidUtilities.dp(48);
                    } else if (chatListView.getPaddingTop() != AndroidUtilities.dp(4) && (pinnedMessageView == null || pinnedMessageView.getTag() != null) && (reportSpamView == null || reportSpamView.getTag() != null)) {
                        chatListView.setPadding(0, AndroidUtilities.dp(4), 0, AndroidUtilities.dp(3));
                        chatListView.setTopGlowOffset(0);
                        top += AndroidUtilities.dp(48);
                    } else {
                        firstVisPos = RecyclerView.NO_POSITION;
                    }
                    if (firstVisPos != RecyclerView.NO_POSITION) {
                        chatLayoutManager.scrollToPositionWithOffset(firstVisPos, top);
                    }
                } catch (Exception e) {
                    FileLog.e("tmessages", e);
                }
            }
        });
    }

/*    @Override
    public void dismissCurrentDialig() {
        if (chatAttachAlert != null && visibleDialog == chatAttachAlert) {
//            chatAttachAlert.closeCamera(false);
            chatAttachAlert.dismissInternal();
//            chatAttachAlert.hideCamera(true);
            return;
        }
        super.dismissCurrentDialig();
    }*/

    @Override
    public void onResume() {
        super.onResume();

        AndroidUtilities.requestAdjustResize(getParentActivity(), classGuid);

        checkActionBarMenu();
//        if (replyImageLocation != null && replyImageView != null) {
//            replyImageView.setImage(replyImageLocation, "50_50", (Drawable) null, replyingMessageObject);
//        }
//        if (pinnedImageLocation != null && pinnedMessageImageView != null) {
//            pinnedMessageImageView.setImage(pinnedImageLocation, "50_50", (Drawable) null, pinnedMessageObject);
//        }

        NotificationsController.getInstance(currentAccount).setOpenedDialogId(dialog_id,0);
        if (scrollToTopOnResume) {
            if (scrollToTopUnReadOnResume && scrollToMessage != null) {
                if (chatListView != null) {
                    int yOffset;
                    if (scrollToMessagePosition == -9000) {
                        yOffset = Math.max(0, (chatListView.getHeight() - scrollToMessage.getApproximateHeight()) / 2);
                    } else if (scrollToMessagePosition == -10000) {
                        yOffset = 0;
                    } else {
                        yOffset = scrollToMessagePosition;
                    }
                    chatLayoutManager.scrollToPositionWithOffset(messages.size() - messages.indexOf(scrollToMessage), -chatListView.getPaddingTop() - AndroidUtilities.dp(7) + yOffset);
                }
            } else {
                moveScrollToLastMessage();
            }
            scrollToTopUnReadOnResume = false;
            scrollToTopOnResume = false;
            scrollToMessage = null;
        }
        paused = false;
        if (readWhenResume && !messages.isEmpty()) {
            for (MessageObject messageObject : messages) {
                if (!messageObject.isUnread() && !messageObject.isOut()) {
                    break;
                }
                if (!messageObject.isOut()) {
                    messageObject.setIsRead();
                }
            }
            readWhenResume = false;
        }
        if (wasPaused) {
            wasPaused = false;
            if (chatAdapter != null) {
                chatAdapter.notifyDataSetChanged();
            }
        }

        fixLayout();

        if (bottomOverlayChat != null && bottomOverlayChat.getVisibility() != View.VISIBLE) {

        }

        if (chatListView != null) {
            chatListView.setOnItemLongClickListener(onItemLongClickListener);
            chatListView.setOnItemClickListener(onItemClickListener);
            chatListView.setLongClickable(true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (menuItem != null) {
            menuItem.closeSubMenu();
        }
        if (chatAttachAlert != null) {
            chatAttachAlert.onPause();
        }
        paused = true;
        wasPaused = true;
        NotificationsController.getInstance(currentAccount).setOpenedDialogId(0,0);
        CharSequence draftMessage = null;
        boolean searchWebpage = true;

        CharSequence[] message = new CharSequence[]{draftMessage};
        ArrayList<TLRPC.MessageEntity> entities = MediaDataController.getInstance(currentAccount).getEntities(message, false);
        MediaDataController.getInstance(currentAccount).saveDraft(dialog_id, 0, message[0], entities, replyingMessageObject != null ? replyingMessageObject.messageOwner : null, !searchWebpage);

        MessagesController.getInstance(currentAccount).cancelTyping(0, dialog_id, 0);
    }

    private boolean fixLayoutInternal() {
        if (!AndroidUtilities.isTablet() && ApplicationLoader.applicationContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            selectedMessagesCountTextView.setTextSize(18);
        } else {
            selectedMessagesCountTextView.setTextSize(20);
        }

        if (AndroidUtilities.isTablet()) {
            if (AndroidUtilities.isSmallTablet() && ApplicationLoader.applicationContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                actionBar.setBackButtonDrawable(new BackDrawable(false));
                if (fragmentContextView != null && fragmentContextView.getParent() == null) {
                    ((ViewGroup) fragmentView).addView(fragmentContextView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 39, Gravity.TOP | Gravity.LEFT, 0, -36, 0, 0));
                }
            } else {
                actionBar.setBackButtonDrawable(new BackDrawable(parentLayout == null || parentLayout.getFragmentStack().isEmpty() || parentLayout.getFragmentStack().get(0) == DownloadManagerActivity.this || parentLayout.getFragmentStack().size() == 1));
                if (fragmentContextView != null && fragmentContextView.getParent() != null) {
                    fragmentView.setPadding(0, 0, 0, 0);
                    ((ViewGroup) fragmentView).removeView(fragmentContextView);
                }
            }
            return false;
        }
        return true;
    }

    private void fixLayout() {
        if (avatarContainer != null) {
            avatarContainer.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    if (avatarContainer != null) {
                        avatarContainer.getViewTreeObserver().removeOnPreDrawListener(this);
                    }
                    return fixLayoutInternal();
                }
            });
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        fixLayout();
    }

    private void createMenu(View v, boolean single) {
        if (actionBar.isActionModeShowed()) {
            return;
        }

        MessageObject message = null;
        if (v instanceof ChatMessageCell) {
            message = ((ChatMessageCell) v).getMessageObject();
        } else if (v instanceof ChatActionCell) {
            message = ((ChatActionCell) v).getMessageObject();
        }
        if (message == null) {
            return;
        }
        final int type = getMessageType(message);
        if (single && message.messageOwner.action instanceof TLRPC.TL_messageActionPinMessage) {
            scrollToMessageId(message.getId(), 0, true, 0);
            return;
        }

        selectedObject = null;
        forwaringMessage = null;
        for (int a = 1; a >= 0; a--) {
            selectedMessagesCanCopyIds[a].clear();
            selectedMessagesIds[a].clear();
        }
        cantDeleteMessagesCount = 0;
        actionBar.hideActionMode();

        boolean allowChatActions = true;

        if (SharedStorage.downloadModule(DownloadHelper.Modules.MESSAGE_MENU_ICON)) {

            if (single || type < 2 || type == 20) {
                if (type >= 0) {
                    selectedObject = message;
                    if (getParentActivity() == null) {
                        return;
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());

                    ArrayList<CharSequence> items = new ArrayList<>();
                    ArrayList<Integer> icons = new ArrayList<>();
                    final ArrayList<Integer> options = new ArrayList<>();

                    if (type == 0) {
                        items.add(LocaleController.getString("Delete", R.string.Delete));
                        icons.add(R.drawable.msg_delete);
                        options.add(1);
                    } else if (type == 20) {
                        items.add(LocaleController.getString("Copy", R.string.Copy));
                        icons.add(R.drawable.msg_copy);
                        options.add(3);
                        items.add(LocaleController.getString("Delete", R.string.Delete));
                        icons.add(R.drawable.msg_delete);
                        options.add(1);
                    } else {
                        if (selectedObject.type == 0 || selectedObject.caption != null) {
                            items.add(LocaleController.getString("Copy", R.string.Copy));
                            icons.add(R.drawable.msg_copy);
                            options.add(3);
                        }
                        if (type == 3) {
                            if (selectedObject.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage && MessageObject.isNewGifDocument(selectedObject.messageOwner.media.webpage.document)) {
                                items.add(LocaleController.getString("SaveToGIFs", R.string.SaveToGIFs));
                                icons.add(R.drawable.msg_gif);
                                options.add(11);
                            }
                        } else if (type == 4) {
                            if (selectedObject.isVideo()) {
                                items.add(LocaleController.getString("SaveToGallery", R.string.SaveToGallery));
                                icons.add(R.drawable.msg_gallery);
                                options.add(4);
                                items.add(LocaleController.getString("ShareFile", R.string.ShareFile));
                                icons.add(R.drawable.msg_shareout);
                                options.add(6);
                            } else if (selectedObject.isMusic()) {
                                items.add(LocaleController.getString("SaveToMusic", R.string.SaveToMusic));
                                icons.add(R.drawable.msg_download);
                                options.add(10);
                                items.add(LocaleController.getString("ShareFile", R.string.ShareFile));
                                icons.add(R.drawable.msg_shareout);
                                options.add(6);
                            } else if (selectedObject.getDocument() != null) {
                                if (MessageObject.isNewGifDocument(selectedObject.getDocument())) {
                                    items.add(LocaleController.getString("SaveToGIFs", R.string.SaveToGIFs));
                                    icons.add(R.drawable.msg_gif);
                                    options.add(11);
                                }
                                items.add(LocaleController.getString("SaveToDownloads", R.string.SaveToDownloads));
                                icons.add(R.drawable.msg_download);
                                options.add(10);
                                items.add(LocaleController.getString("ShareFile", R.string.ShareFile));
                                icons.add(R.drawable.msg_shareout);
                                options.add(6);
                            } else {
                                items.add(LocaleController.getString("SaveToGallery", R.string.SaveToGallery));
                                icons.add(R.drawable.msg_gallery);
                                options.add(4);
                            }
                        } else if (type == 5) {
                            items.add(LocaleController.getString("ShareFile", R.string.ShareFile));
                            icons.add(R.drawable.msg_share);
                            options.add(6);
                        } else if (type == 6) {
                            items.add(LocaleController.getString("SaveToGallery", R.string.SaveToGallery));
                            icons.add(R.drawable.msg_gallery);
                            options.add(7);
                            items.add(LocaleController.getString("SaveToDownloads", R.string.SaveToDownloads));
                            icons.add(R.drawable.msg_download);
                            options.add(10);
                            items.add(LocaleController.getString("ShareFile", R.string.ShareFile));
                            icons.add(R.drawable.msg_shareout);
                            options.add(6);
                        }

                    }
                    items.add(LocaleController.getString("Delete", R.string.Delete));
                    icons.add(R.drawable.msg_delete);
                    options.add(1);
                    items.add(LocaleController.getString("Forward1", R.string.Forward));
                    icons.add(R.drawable.msg_forward);
                    options.add(2);

                    if (options.isEmpty()) {
                        return;
                    }
                    final CharSequence[] finalItems = items.toArray(new CharSequence[items.size()]);
                    builder.setItems(finalItems, AndroidUtilities.toIntArray(icons), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (selectedObject == null || i < 0 || i >= options.size()) {
                                return;
                            }
                            processSelectedOption(options.get(i));
                        }
                    });

                    builder.setTitle(LocaleController.getString("Message", R.string.Message));
                    if (!selectedObject.getDocumentName().equals("") && !selectedObject.isVideo() && !selectedObject.isGif()) {
                        builder.setSubtitle(selectedObject.getDocumentName());
                    }
                    showDialog(builder.create());
                }
                return;
            }
        } else {

            if (single || type < 2 || type == 20) {
                if (type >= 0) {
                    selectedObject = message;
                    if (getParentActivity() == null) {
                        return;
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());

                    ArrayList<CharSequence> items = new ArrayList<>();
                    final ArrayList<Integer> options = new ArrayList<>();

                    if (type == 0) {
                        items.add(LocaleController.getString("Delete", R.string.Delete));
                        options.add(1);
                    } else if (type == 20) {
                        items.add(LocaleController.getString("Copy", R.string.Copy));
                        options.add(3);
                        items.add(LocaleController.getString("Delete", R.string.Delete));
                        options.add(1);
                    } else {
                        if (selectedObject.type == 0 || selectedObject.caption != null) {
                            items.add(LocaleController.getString("Copy", R.string.Copy));
                            options.add(3);
                        }
                        if (type == 3) {
                            if (selectedObject.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage && MessageObject.isNewGifDocument(selectedObject.messageOwner.media.webpage.document)) {
                                items.add(LocaleController.getString("SaveToGIFs", R.string.SaveToGIFs));
                                options.add(11);
                            }
                        } else if (type == 4) {
                            if (selectedObject.isVideo()) {
                                items.add(LocaleController.getString("SaveToGallery", R.string.SaveToGallery));
                                options.add(4);
                                items.add(LocaleController.getString("ShareFile", R.string.ShareFile));
                                options.add(6);
                            } else if (selectedObject.isMusic()) {
                                items.add(LocaleController.getString("SaveToMusic", R.string.SaveToMusic));
                                options.add(10);
                                items.add(LocaleController.getString("ShareFile", R.string.ShareFile));
                                options.add(6);
                            } else if (selectedObject.getDocument() != null) {
                                if (MessageObject.isNewGifDocument(selectedObject.getDocument())) {
                                    items.add(LocaleController.getString("SaveToGIFs", R.string.SaveToGIFs));
                                    options.add(11);
                                }
                                items.add(LocaleController.getString("SaveToDownloads", R.string.SaveToDownloads));
                                options.add(10);
                                items.add(LocaleController.getString("ShareFile", R.string.ShareFile));
                                options.add(6);
                            } else {
                                items.add(LocaleController.getString("SaveToGallery", R.string.SaveToGallery));
                                options.add(4);
                            }
                        } else if (type == 5) {
                            items.add(LocaleController.getString("ShareFile", R.string.ShareFile));
                            options.add(6);
                        } else if (type == 6) {
                            items.add(LocaleController.getString("SaveToGallery", R.string.SaveToGallery));
                            options.add(7);
                            items.add(LocaleController.getString("SaveToDownloads", R.string.SaveToDownloads));
                            options.add(10);
                            items.add(LocaleController.getString("ShareFile", R.string.ShareFile));
                            options.add(6);
                        }

                    }
                    items.add(LocaleController.getString("Delete", R.string.Delete));
                    options.add(1);
                    items.add(LocaleController.getString("Forward", R.string.Forward));
                    options.add(2);

                    if (options.isEmpty()) {
                        return;
                    }
                    final CharSequence[] finalItems = items.toArray(new CharSequence[items.size()]);
                    builder.setItems(finalItems, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (selectedObject == null || i < 0 || i >= options.size()) {
                                return;
                            }
                            processSelectedOption(options.get(i));
                        }
                    });

                    builder.setTitle(LocaleController.getString("Message", R.string.Message));
                    if (!selectedObject.getDocumentName().equals("") && !selectedObject.isVideo() && !selectedObject.isGif() && !selectedObject.isSticker()) {
                        builder.setSubtitle(selectedObject.getDocumentName());
                    }
                    showDialog(builder.create());
                }
                return;
            }

        }

        final ActionBarMenu actionMode = actionBar.createActionMode();
        View item = actionMode.getItem(forward);
        if (item != null) {
            item.setVisibility(View.VISIBLE);
        }
        item = actionMode.getItem(delete);
        if (item != null) {
            item.setVisibility(View.VISIBLE);
        }

        actionBar.showActionMode();

        AnimatorSet animatorSet = new AnimatorSet();
        ArrayList<Animator> animators = new ArrayList<>();
        for (int a = 0; a < actionModeViews.size(); a++) {
            View view = actionModeViews.get(a);
            AndroidUtilities.clearDrawableAnimation(view);
            animators.add(ObjectAnimator.ofFloat(view, "scaleY", 0.1f, 1.0f));
        }
        animatorSet.playTogether(animators);
        animatorSet.setDuration(250);
        animatorSet.start();

        addToSelectedMessages(message);
        selectedMessagesCountTextView.setNumber(1, false);
        updateVisibleRows();
    }

    private String getMessageContent(MessageObject messageObject, long previousUid, boolean name) {
        String str = "";
        if (name) {
            if (previousUid != messageObject.messageOwner.from_id.user_id) {
                if (messageObject.messageOwner.from_id.user_id > 0) {
                    TLRPC.User user = MessagesController.getInstance(currentAccount).getUser(messageObject.messageOwner.from_id.user_id);
                    if (user != null) {
                        str = ContactsController.formatName(user.first_name, user.last_name) + ":\n";
                    }
                } else if (messageObject.messageOwner.from_id.user_id < 0) {
                    TLRPC.Chat chat = MessagesController.getInstance(currentAccount).getChat(-messageObject.messageOwner.from_id.user_id);
                    if (chat != null) {
                        str = chat.title + ":\n";
                    }
                }
            }
        }
        if (messageObject.type == 0 && messageObject.messageOwner.message != null) {
            str += messageObject.messageOwner.message;
        } else if (messageObject.messageOwner.media != null && messageObject.messageOwner.message != null) {
            str += messageObject.messageOwner.message;
        } else {
            str += messageObject.messageText;
        }
        return str;
    }

    private void processSelectedOption(int option) {
        if (selectedObject == null) {
            return;
        }
        switch (option) {
            case 1: {
                if (getParentActivity() == null) {
                    selectedObject = null;
                    return;
                }
                final MessageObject messageObject = selectedObject;
                AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                builder.setMessage(LocaleController.getString("AreYouSureToContinue", R.string.AreYouSureToContinue));
                builder.setTitle(LocaleController.getString("Message", R.string.Message));
                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ArrayList<TLRPC.Message> msgs = new ArrayList<TLRPC.Message>();
                        msgs.add(messageObject.messageOwner);
                        DM_DeleteMessage(msgs);
                        messages.remove(messageObject);
                        if (chatAdapter != null) {
                            chatAdapter.notifyDataSetChanged();
                        }
                    }
                });
                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                showDialog(builder.create());

                break;
            }
            case 2: {
                forwaringMessage = selectedObject;
                if (getParentActivity() == null) {
                    return;
                }
                ArrayList<MessageObject> msgObj = new ArrayList<MessageObject>();
                msgObj.add(forwaringMessage);
                showDialog(new ShareAlert(getParentActivity(), msgObj, null, false, null, false));
                break;
            }
            case 3: {
                AndroidUtilities.addToClipboard(getMessageContent(selectedObject, 0, false));
                break;
            }
            case 4: {
                String path = selectedObject.messageOwner.attachPath;
                if (path != null && path.length() > 0) {
                    File temp = new File(path);
                    if (!temp.exists()) {
                        path = null;
                    }
                }
                if (path == null || path.length() == 0) {
                    path = FileLoader.getInstance(currentAccount).getPathToMessage(selectedObject.messageOwner).toString();
                }
                if (selectedObject.type == 3 || selectedObject.type == 1) {
                    if (Build.VERSION.SDK_INT >= 23 && getParentActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        getParentActivity().requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 4);
                        selectedObject = null;
                        return;
                    }
                    MediaController.saveFile(path, getParentActivity(), selectedObject.type == 3 ? 1 : 0, null, null);
                }
                break;
            }
            case 6: {
                String path = selectedObject.messageOwner.attachPath;
                if (path != null && path.length() > 0) {
                    File temp = new File(path);
                    if (!temp.exists()) {
                        path = null;
                    }
                }
                if (path == null || path.length() == 0) {
                    path = FileLoader.getInstance(currentAccount).getPathToMessage(selectedObject.messageOwner).toString();
                }
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType(selectedObject.getDocument().mime_type);
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(path)));
                getParentActivity().startActivityForResult(Intent.createChooser(intent, LocaleController.getString("ShareFile", R.string.ShareFile)), 500);
                break;
            }
            case 7: {
                String path = selectedObject.messageOwner.attachPath;
                if (path != null && path.length() > 0) {
                    File temp = new File(path);
                    if (!temp.exists()) {
                        path = null;
                    }
                }
                if (path == null || path.length() == 0) {
                    path = FileLoader.getInstance(currentAccount).getPathToMessage(selectedObject.messageOwner).toString();
                }
                if (Build.VERSION.SDK_INT >= 23 && getParentActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    getParentActivity().requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 4);
                    selectedObject = null;
                    return;
                }
                MediaController.saveFile(path, getParentActivity(), 0, null, null);
                break;
            }
            case 10: {
                if (Build.VERSION.SDK_INT >= 23 && getParentActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    getParentActivity().requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 4);
                    selectedObject = null;
                    return;
                }
                String fileName = FileLoader.getDocumentFileName(selectedObject.getDocument());
                if (fileName == null || fileName.length() == 0) {
                    fileName = selectedObject.getFileName();
                }
                String path = selectedObject.messageOwner.attachPath;
                if (path != null && path.length() > 0) {
                    File temp = new File(path);
                    if (!temp.exists()) {
                        path = null;
                    }
                }
                if (path == null || path.length() == 0) {
                    path = FileLoader.getInstance(currentAccount).getPathToMessage(selectedObject.messageOwner).toString();
                }
                MediaController.saveFile(path, getParentActivity(), selectedObject.isMusic() ? 3 : 2, fileName, selectedObject.getDocument() != null ? selectedObject.getDocument().mime_type : "");
                break;
            }
            case 11: {
                //Temp x
//                TLRPC.Document document = selectedObject.getDocument();
//                MessagesController.getInstance(currentAccount).saveGif(document);
                break;
            }
        }
        selectedObject = null;
    }
    @Override
    public boolean didSelectDialogs(DialogsActivity fragment, ArrayList<MessagesStorage.TopicKey> dids, CharSequence message, boolean param, TopicsFragment topicsFragment) {
        if (dialog_id != 0 && (forwaringMessage != null || !selectedMessagesIds[0].isEmpty() || !selectedMessagesIds[1].isEmpty())) {
            ArrayList<MessageObject> fmessages = new ArrayList<>();
            if (forwaringMessage != null) {
                fmessages.add(forwaringMessage);
                forwaringMessage = null;
            } else {
                for (int a = 1; a >= 0; a--) {
                    ArrayList<Integer> ids = new ArrayList<>(selectedMessagesIds[a].keySet());
                    Collections.sort(ids);
                    for (int b = 0; b < ids.size(); b++) {
                        Integer id = ids.get(b);
                        MessageObject messageObject = selectedMessagesIds[a].get(id);
                        if (messageObject != null && id > 0) {
                            fmessages.add(messageObject);
                        }
                    }
                    selectedMessagesCanCopyIds[a].clear();
                    selectedMessagesIds[a].clear();
                }
                cantDeleteMessagesCount = 0;
                actionBar.hideActionMode();
            }

            long did = dids.get(0).dialogId;
            if (did != dialog_id) {
                int lower_part = (int) did;
                if (lower_part != 0) {
                    Bundle args = new Bundle();
                    args.putBoolean("scrollToTopOnResume", scrollToTopOnResume);
                    if (lower_part > 0) {
                        args.putInt("user_id", lower_part);
                    } else if (lower_part < 0) {
                        args.putInt("chat_id", -lower_part);
                    }
                    if (!MessagesController.getInstance(currentAccount).checkCanOpenChat(args, fragment)) {
                        return param;
                    }

                    ChatActivity chatActivity = new ChatActivity(args);
                    if (presentFragment(chatActivity, true)) {
                        chatActivity.showFieldPanelForForward(true, fmessages);
                        if (!AndroidUtilities.isTablet()) {
                            removeSelfFromStack();
                        }
                    } else {
                        fragment.finishFragment();
                    }
                } else {
                    fragment.finishFragment();
                }
            } else {
                fragment.finishFragment();
                moveScrollToLastMessage();
                if (AndroidUtilities.isTablet()) {
                    actionBar.hideActionMode();
                }
                updateVisibleRows();
            }
        }
        return false;
    }

    @Override
    public boolean onBackPressed() {
        if (actionBar != null && actionBar.isActionModeShowed()) {
            for (int a = 1; a >= 0; a--) {
                selectedMessagesIds[a].clear();
                selectedMessagesCanCopyIds[a].clear();
            }
            actionBar.hideActionMode();
            cantDeleteMessagesCount = 0;
            updateVisibleRows();
            return false;
        }
        return true;
    }

    private void updateVisibleRows() {
        if (chatListView == null) {
            return;
        }
        int count = chatListView.getChildCount();
        for (int a = 0; a < count; a++) {
            View view = chatListView.getChildAt(a);
            if (view instanceof ChatMessageCell) {
                ChatMessageCell cell = (ChatMessageCell) view;

                boolean disableSelection = false;
                boolean selected = false;
                if (actionBar.isActionModeShowed()) {
                    MessageObject messageObject = cell.getMessageObject();
                    if (selectedMessagesIds[messageObject.getDialogId() == dialog_id ? 0 : 1].containsKey(messageObject.getId())) {
                        view.setBackgroundColor(Theme.getColor(Theme.key_chat_selectedBackground));
                        selected = true;
                    } else {
                        view.setBackgroundDrawable(null);
                    }
                    disableSelection = true;
                } else {
                    view.setBackgroundDrawable(null);
                }

                cell.setMessageObject(cell.getMessageObject(), cell.getCurrentMessagesGroup(), cell.isPinnedBottom(), cell.isPinnedTop());
                cell.setCheckPressed(!disableSelection, disableSelection && selected);
                cell.setHighlighted(highlightMessageId != Integer.MAX_VALUE && cell.getMessageObject() != null && cell.getMessageObject().getId() == highlightMessageId);
                if (searchContainer != null && searchContainer.getVisibility() == View.VISIBLE && MediaDataController.getInstance(currentAccount).getLastSearchQuery() != null) {
                    cell.setHighlightedText(MediaDataController.getInstance(currentAccount).getLastSearchQuery());
                } else {
                    cell.setHighlightedText(null);
                }
            } else if (view instanceof ChatActionCell) {
                ChatActionCell cell = (ChatActionCell) view;
                cell.setMessageObject(cell.getMessageObject());
            }
        }
    }

    private ArrayList<MessageObject> createVoiceMessagesPlaylist(MessageObject startMessageObject, boolean playingUnreadMedia) {
        ArrayList<MessageObject> messageObjects = new ArrayList<>();
        messageObjects.add(startMessageObject);
        int messageId = startMessageObject.getId();
        if (messageId != 0) {
            boolean started = false;
            for (int a = messages.size() - 1; a >= 0; a--) {
                MessageObject messageObject = messages.get(a);
                if (messageObject.getId() > messageId && messageObject.isVoice() && (!playingUnreadMedia || messageObject.isContentUnread() && !messageObject.isOut())) {
                    messageObjects.add(messageObject);
                }
            }
        }
        return messageObjects;
    }

    private void alertUserOpenError(MessageObject message) {
        if (getParentActivity() == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
        if (message.type == 3) {
            builder.setMessage(LocaleController.getString("NoPlayerInstalled", R.string.NoPlayerInstalled));
        } else {
            builder.setMessage(LocaleController.formatString("NoHandleAppInstalled", R.string.NoHandleAppInstalled, message.getDocument().mime_type));
        }
        showDialog(builder.create());
    }

    @Override
    public void updatePhotoAtIndex(int index) {

    }

    @Override
    public boolean allowSendingSubmenu() {
        return false;
    }

    @Override
    public boolean allowCaption() {
        return true;
    }

    @Override
    public boolean scaleToFill() {
        return false;
    }

//    @Override
//    public void toggleGroupPhotosEnabled() {
//
//    }

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

//    @Override
//    public boolean allowGroupPhotos() {
//        return false;
//    }

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

    @Override
    public void openPhotoForEdit(String file, String thumb, boolean isVideo) {

    }

    @Override
    public int getTotalImageCount() {
        return 0;
    }

    @Override
    public boolean loadMore() {
        return false;
    }

    @Override
    public CharSequence getTitleFor(int index) {
        return null;
    }

    @Override
    public CharSequence getSubtitleFor(int index) {
        return null;
    }

    @Override
    public MessageObject getEditingMessageObject() {
        return null;
    }

    @Override
    public void onCaptionChanged(CharSequence caption) {

    }

    @Override
    public boolean closeKeyboard() {
        return false;
    }

    @Override
    public boolean validateGroupId(long groupId) {
        return false;
    }

    @Override
    public void onApplyCaption(CharSequence caption) {

    }

    @Override
    public void onOpen() {

    }

    @Override
    public void onClose() {

    }

    @Override
    public PhotoViewer.PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int index, boolean needPreview) {
        int count = chatListView.getChildCount();

        for (int a = 0; a < count; a++) {
            ImageReceiver imageReceiver = null;
            View view = chatListView.getChildAt(a);
            if (view instanceof ChatMessageCell) {
                if (messageObject != null) {
                    ChatMessageCell cell = (ChatMessageCell) view;
                    MessageObject message = cell.getMessageObject();
                    if (message != null && message.getId() == messageObject.getId()) {
                        imageReceiver = cell.getPhotoImage();
                    }
                }
            } else if (view instanceof ChatActionCell) {
                ChatActionCell cell = (ChatActionCell) view;
                MessageObject message = cell.getMessageObject();
                if (message != null) {
                    if (messageObject != null) {
                        if (message.getId() == messageObject.getId()) {
                            imageReceiver = cell.getPhotoImage();
                        }
                    } else if (fileLocation != null && message.photoThumbs != null) {
                        for (int b = 0; b < message.photoThumbs.size(); b++) {
                            TLRPC.PhotoSize photoSize = message.photoThumbs.get(b);
                            if (photoSize.location.volume_id == fileLocation.volume_id && photoSize.location.local_id == fileLocation.local_id) {
                                imageReceiver = cell.getPhotoImage();
                                break;
                            }
                        }
                    }
                }
            }

            if (imageReceiver != null) {
                int coords[] = new int[2];
                view.getLocationInWindow(coords);
                PhotoViewer.PlaceProviderObject object = new PhotoViewer.PlaceProviderObject();
                object.viewX = coords[0];
                object.viewY = coords[1] - (Build.VERSION.SDK_INT >= 21 ? 0 : AndroidUtilities.statusBarHeight);
                object.parentView = chatListView;
                object.imageReceiver = imageReceiver;
                object.thumb = imageReceiver.getBitmapSafe();
                object.radius = imageReceiver.getRoundRadius();
                if (pinnedMessageView != null && pinnedMessageView.getTag() == null || reportSpamView != null && reportSpamView.getTag() == null) {
                    object.clipTopAddition = AndroidUtilities.dp(48);
                }
                return object;
            }
        }
        return null;
    }

    @Override
    public ImageReceiver.BitmapHolder getThumbForPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int index) {
        return null;
    }

    @Override
    public void willSwitchFromPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int index) {
    }

    @Override
    public void willHidePhotoViewer() {
    }

    @Override
    public boolean isPhotoChecked(int index) {
        return false;
    }

    @Override
    public int setPhotoChecked(int index, VideoEditedInfo videoEditedInfo) {

        return index;
    }

    @Override
    public int setPhotoUnchecked(Object photoEntry) {
        return 0;
    }

    @Override
    public boolean cancelButtonPressed() {
        return true;
    }

    @Override
    public void needAddMorePhotos() {

    }

    @Override
    public void sendButtonPressed(int index, VideoEditedInfo videoEditedInfo, boolean notify, int scheduleDate, boolean forceDocument) {

    }

    @Override
    public void sendButtonPressed(int index, VideoEditedInfo videoEditedInfo, boolean notify, int scheduleDate) {

    }

    @Override
    public void replaceButtonPressed(int index, VideoEditedInfo videoEditedInfo) {

    }

    @Override
    public boolean canReplace(int index) {
        return false;
    }

    @Override
    public int getSelectedCount() {
        return 0;
    }

    public void showOpenUrlAlert(final String url, boolean ask) {
        if (Browser.isInternalUrl(url, null) || !ask) {
            Browser.openUrl(getParentActivity(), url, inlineReturn == 0);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
            builder.setMessage(LocaleController.formatString("OpenUrlAlert2", R.string.OpenUrlAlert2, url));
            builder.setPositiveButton(LocaleController.getString("Open", R.string.Open), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Browser.openUrl(getParentActivity(), url, inlineReturn == 0);
                }
            });
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
            showDialog(builder.create());
        }
    }

    private void removeMessageObject(MessageObject messageObject) {
        int index = messages.indexOf(messageObject);
        if (index == -1) {
            return;
        }
        messages.remove(index);
        if (chatAdapter != null) {
            chatAdapter.notifyItemRemoved(chatAdapter.messagesStartRow + messages.size() - index - 1);
        }
    }



    public class ChatActivityAdapter extends RecyclerView.Adapter {

        private Context mContext;
        private int rowCount;
        private int botInfoRow = -1;
        private int loadingUpRow;
        private int loadingDownRow;
        private int messagesStartRow;
        private int messagesEndRow;

        public ChatActivityAdapter(Context context) {
            mContext = context;
        }

        public void updateRows() {
            rowCount = 0;
            botInfoRow = -1;
            if (!messages.isEmpty()) {
                if (!endReached[0] || !endReached[1]) {
                    loadingUpRow = rowCount++;
                } else {
                    loadingUpRow = -1;
                }
                messagesStartRow = rowCount;
                rowCount += messages.size();
                messagesEndRow = rowCount;
                if (!forwardEndReached[0] || !forwardEndReached[1]) {
                    loadingDownRow = rowCount++;
                } else {
                    loadingDownRow = -1;
                }
            } else {
                loadingUpRow = -1;
                loadingDownRow = -1;
                messagesStartRow = -1;
                messagesEndRow = -1;
            }
        }

        private class Holder extends RecyclerView.ViewHolder {

            public Holder(View itemView) {
                super(itemView);
            }
        }

        @Override
        public int getItemCount() {
            return rowCount;
        }

        @Override
        public long getItemId(int i) {
            return RecyclerListView.NO_ID;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            if (viewType == 0) {
                if (!chatMessageCellsCache.isEmpty()) {
                    view = chatMessageCellsCache.get(0);
                    chatMessageCellsCache.remove(0);
                } else {
                    view = new ChatMessageCell(mContext);
                }
                ChatMessageCell chatMessageCell = (ChatMessageCell) view;
                chatMessageCell.setDelegate(new ChatMessageCell.ChatMessageCellDelegate() {
                    @Override
                    public void didPressSideButton(ChatMessageCell cell) {
                        if (getParentActivity() == null) {
                            return;
                        }
                        ArrayList<MessageObject> msgObj = new ArrayList<MessageObject>();
                        msgObj.add(cell.getMessageObject());
                        showDialog(new ShareAlert(mContext, msgObj, null, false, null, false));
                    }

                    @Override
                    public boolean needPlayMessage(MessageObject messageObject, boolean mute) {
                        if (messageObject.isVoice() || messageObject.isRoundVideo()) {
                            boolean result = MediaController.getInstance().playMessage(messageObject);
                            MediaController.getInstance().setVoiceMessagesPlaylist(result ? createVoiceMessagesPlaylist(messageObject, false) : null, false);
                            return result;
                        } else if (messageObject.isMusic()) {
                            return MediaController.getInstance().setPlaylist(messages, messageObject, 0);
                        }
                        return false;
                    }

                    @Override
                    public void didPressChannelAvatar(ChatMessageCell cell, TLRPC.Chat chat, int postId, float touchX, float touchY) {
//                        if (actionBar.isActionModeShowed()) {
//                            processRowSelect(cell);
//                            return;
//                        }
//                        if (chat != null && chat != currentChat) {
//                            Bundle args = new Bundle();
//                            args.putInt("chat_id", chat.id);
//                            if (postId != 0) {
//                                args.putInt("message_id", postId);
//                            }
//                            if (MessagesController.checkCanOpenChat(args, DownloadActivity.this)) {
//                                presentFragment(new ChatActivity(args), true);
//                            }
//                        }
                    }

                    @Override
                    public void didPressOther(ChatMessageCell cell, float otherX, float otherY) {
                        createMenu(cell, true);
                    }

                    @Override
                    public void didPressUserAvatar(ChatMessageCell cell, TLRPC.User user, float touchX, float touchY) {

                    }

                    @Override
                    public void didPressBotButton(ChatMessageCell cell, TLRPC.KeyboardButton button) {
                        if (getParentActivity() == null || bottomOverlayChat.getVisibility() == View.VISIBLE &&
                                !(button instanceof TLRPC.TL_keyboardButtonSwitchInline) && !(button instanceof TLRPC.TL_keyboardButtonCallback) &&
                                !(button instanceof TLRPC.TL_keyboardButtonGame) && !(button instanceof TLRPC.TL_keyboardButtonUrl)) {
                            return;
                        }
                    }

                    @Override
                    public void didPressVoteButtons(ChatMessageCell cell, ArrayList<TLRPC.TL_pollAnswer> button, int showCount, int x, int y) {

                    }

                    @Override
                    public void didPressInstantButton(ChatMessageCell cell, int type) {
                        MessageObject messageObject = cell.getMessageObject();
                        if (messageObject.messageOwner.media != null && messageObject.messageOwner.media.webpage != null && messageObject.messageOwner.media.webpage.cached_page != null) {
                            ArticleViewer.getInstance().setParentActivity(getParentActivity(), DownloadManagerActivity.this);
                            ArticleViewer.getInstance().open(messageObject);
                        }
                    }

/*                    @Override
                    public String getAdminRank(int uid) {
                        return null;
                    }*/

                    @Override
                    public void didPressCancelSendButton(ChatMessageCell cell) {
                        MessageObject message = cell.getMessageObject();
                        if (message.messageOwner.send_state != 0) {
                            SendMessagesHelper.getInstance(currentAccount).cancelSendingMessage(message);
                        }
                    }

                    @Override
                    public void didLongPress(ChatMessageCell cell, float x, float y) {
                        createMenu(cell, false);
                    }

                    @Override
                    public boolean canPerformActions() {
                        return actionBar != null && !actionBar.isActionModeShowed();
                    }

//                    @Override
//                    public void didPressUrl(MessageObject messageObject, final CharacterStyle url, boolean longPress) {
//                        if (url == null) {
//                            return;
//                        }
//                        if (url instanceof URLSpanMono) {
//                            ((URLSpanMono) url).copyToClipboard();
//                            MyUtils.showToast(getParentActivity(), LocaleController.getString("TextCopied", R.string.TextCopied), Toast.LENGTH_SHORT);
//                        } else if (url instanceof URLSpanUserMention) {
//                            TLRPC.User user = MessagesController.getInstance(currentAccount).getUser(Utilities.parseInt(((URLSpanUserMention) url).getURL()));
//                            if (user != null) {
//                                MessagesController.openChatOrProfileWith(user, null, DownloadManagerActivity.this, 0, false);
//                            }
//                        } else if (url instanceof URLSpanNoUnderline) {
//                            String str = ((URLSpanNoUnderline) url).getURL();
//                            if (str.startsWith("@")) {
//                                MessagesController.getInstance(currentAccount).openByUserName(str.substring(1), DownloadManagerActivity.this, 0);
//                            } else if (str.startsWith("#")) {
//
//                            } else if (str.startsWith("/")) {
//                                if (URLSpanBotCommand.enabled) {
//
//                                }
//                            }
//                        } else {
//                            final String urlFinal = ((URLSpan) url).getURL();
//                            if (longPress) {
//                                BottomSheet.Builder builder = new BottomSheet.Builder(getParentActivity());
//                                builder.setTitle(urlFinal);
//                                builder.setItems(new CharSequence[]{LocaleController.getString("Open", R.string.Open), LocaleController.getString("Copy", R.string.Copy)}, new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, final int which) {
//                                        if (which == 0) {
//                                            Browser.openUrl(getParentActivity(), urlFinal, inlineReturn == 0);
//                                        } else if (which == 1) {
//                                            String url = urlFinal;
//                                            if (url.startsWith("mailto:")) {
//                                                url = url.substring(7);
//                                            } else if (url.startsWith("tel:")) {
//                                                url = url.substring(4);
//                                            }
//                                            AndroidUtilities.addToClipboard(url);
//                                        }
//                                    }
//                                });
//                                showDialog(builder.create());
//                            } else {
//                                if (url instanceof URLSpanReplacement) {
//                                    showOpenUrlAlert(((URLSpanReplacement) url).getURL(), true);
//                                } else if (url instanceof URLSpan) {
//                                    if (messageObject.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage && messageObject.messageOwner.media.webpage != null && messageObject.messageOwner.media.webpage.cached_page != null) {
//                                        String lowerUrl = urlFinal.toLowerCase();
//                                        String lowerUrl2 = messageObject.messageOwner.media.webpage.url.toLowerCase();
//                                        if (lowerUrl.contains("telegra.ph") && (lowerUrl.contains(lowerUrl2) || lowerUrl2.contains(lowerUrl))) {
//                                            ArticleViewer.getInstance().setParentActivity(getParentActivity(), DownloadManagerActivity.this);
//                                            ArticleViewer.getInstance().open(messageObject);
//                                            return;
//                                        }
//                                    }
//                                    Browser.openUrl(getParentActivity(), urlFinal, inlineReturn == 0);
//                                } else if (url instanceof ClickableSpan) {
//                                    ((ClickableSpan) url).onClick(fragmentView);
//                                }
//                            }
//                        }
//                    }

/*
                    @Override
                    public void needOpenWebView(String url, String title, String description, String originalUrl, int w, int h) {
                        EmbedBottomSheet.show(mContext, title, description, originalUrl, url, w, h, true);
                    }
*/

                    @Override
                    public void didPressReplyMessage(ChatMessageCell cell, int id) {
                        MessageObject messageObject = cell.getMessageObject();
                        scrollToMessageId(id, messageObject.getId(), true, messageObject.getDialogId() == mergeDialogId ? 1 : 0);
                    }

                    @Override
                    public void didPressViaBot(ChatMessageCell cell, String username) {
                        if (bottomOverlayChat != null && bottomOverlayChat.getVisibility() == View.VISIBLE || bottomOverlay != null && bottomOverlay.getVisibility() == View.VISIBLE) {
                            return;
                        }
                    }

                    @Override
                    public void didPressImage(ChatMessageCell cell, float x, float y) {
                        MessageObject message = cell.getMessageObject();
                        if (message.isSendError()) {
                            createMenu(cell, false);
                            return;
                        } else if (message.isSending()) {
                            return;
                        }
                        if (message.type == 13) {
                            showDialog(new StickersAlert(getParentActivity(), DownloadManagerActivity.this, message.getInputStickerSet(), null, null));
                        } else if (Build.VERSION.SDK_INT >= 16 && message.isVideo() || message.type == 1 || message.type == 0 && !message.isWebpageDocument() || message.isGif()) {
                            PhotoViewer.getInstance().setParentActivity(getParentActivity());
                            if (PhotoViewer.getInstance().openPhoto(
                                    message,
                                    message.type != 0 ? dialog_id : 0,
                                    message.type != 0 ? mergeDialogId : 0,
                                    0,
                                    DownloadManagerActivity.this,
                                    false)
                            ) {
                                //PhotoViewer.getInstance().setParentChatActivity(ChatActivity.this);
                            }
                        } else if (message.type == 3) {
                            try {
                                File f = null;
                                if (message.messageOwner.attachPath != null && message.messageOwner.attachPath.length() != 0) {
                                    f = new File(message.messageOwner.attachPath);
                                }
                                if (f == null || !f.exists()) {
                                    f = FileLoader.getInstance(currentAccount).getPathToMessage(message.messageOwner);
                                }
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                if (Build.VERSION.SDK_INT >= 24) {
                                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                    intent.setDataAndType(FileProvider.getUriForFile(getParentActivity(), BuildConfig.APPLICATION_ID + ".provider", f), "video/mp4");
                                } else {
                                    intent.setDataAndType(Uri.fromFile(f), "video/mp4");
                                }
                                getParentActivity().startActivityForResult(intent, 500);
                            } catch (Exception e) {
                                alertUserOpenError(message);
                            }
                        } else if (message.type == 4) {
                           /* if (!AndroidUtilities.isGoogleMapsInstalled(DownloadManagerActivity.this)) {
                                return;
                            }*/
                            LocationActivity fragment = new LocationActivity(2);
                            fragment.setMessageObject(message);
                            presentFragment(fragment);
                        } else if (message.type == 9 || message.type == 0) {
                            try {
//                                AndroidUtilities.openForView(message, getParentActivity());
                            } catch (Exception e) {
                                alertUserOpenError(message);
                            }
                        }
                    }
                });
                chatMessageCell.setAllowAssistant(true);
            } else if (viewType == 1) {
                view = new ChatActionCell(mContext);
                ((ChatActionCell) view).setDelegate(new ChatActionCell.ChatActionCellDelegate() {
                    @Override
                    public void didClickImage(ChatActionCell cell) {
                        MessageObject message = cell.getMessageObject();
                        PhotoViewer.getInstance().setParentActivity(getParentActivity());
                        TLRPC.PhotoSize photoSize = FileLoader.getClosestPhotoSizeWithSize(message.photoThumbs, 640);
                        if (photoSize != null) {
                            PhotoViewer.getInstance().openPhoto(photoSize.location, DownloadManagerActivity.this);
                        } else {
                            PhotoViewer.getInstance().openPhoto(message, 0, 0,0, DownloadManagerActivity.this, false);
                        }
                    }

//                    @Override
                    public void needOpenUserProfile(int uid) {
                        if (uid < 0) {
                            Bundle args = new Bundle();
                            args.putInt("chat_id", -uid);
                            if (MessagesController.getInstance(currentAccount).checkCanOpenChat(args, DownloadManagerActivity.this)) {
                                presentFragment(new ChatActivity(args), true);
                            }
                        } else if (uid != UserConfig.getInstance(currentAccount).getClientUserId()) {
                            Bundle args = new Bundle();
                            args.putInt("user_id", uid);
                            ProfileActivity fragment = new ProfileActivity(args);
                            //fragment.setPlayProfileAnimation(currentUser != null && currentUser.id == uid);
                            presentFragment(fragment);
                        }
                    }

                    @Override
                    public void didPressReplyMessage(ChatActionCell cell, int id) {
                        MessageObject messageObject = cell.getMessageObject();
                        scrollToMessageId(id, messageObject.getId(), true, messageObject.getDialogId() == mergeDialogId ? 1 : 0);
                    }

                    @Override
                    public void didPressBotButton(MessageObject messageObject, TLRPC.KeyboardButton button) {
                        if (getParentActivity() == null || bottomOverlayChat.getVisibility() == View.VISIBLE &&
                                !(button instanceof TLRPC.TL_keyboardButtonSwitchInline) && !(button instanceof TLRPC.TL_keyboardButtonCallback) &&
                                !(button instanceof TLRPC.TL_keyboardButtonGame) && !(button instanceof TLRPC.TL_keyboardButtonUrl)) {
                            return;
                        }
                    }
                });
            } else if (viewType == 2) {
                view = new ChatUnreadCell(mContext,null);
            } else if (viewType == 3) {
                view = new BotHelpCell(mContext, null);
                ((BotHelpCell) view).setDelegate(new BotHelpCell.BotHelpCellDelegate() {
                    @Override
                    public void didPressUrl(String url) {
                        if (url.startsWith("@")) {
                            MessagesController.getInstance(currentAccount).openByUserName(url.substring(1), DownloadManagerActivity.this, 0);
                        } else if (url.startsWith("#")) {
                            DialogsActivity fragment = new DialogsActivity(null);
                            fragment.setSearchString(url);
                            presentFragment(fragment);
                        }
                    }
                });
            } else if (viewType == 4) {
                view = new ChatLoadingCell(mContext, parent, null);
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (position == botInfoRow) {

            } else if (position == loadingDownRow || position == loadingUpRow) {
                ChatLoadingCell loadingCell = (ChatLoadingCell) holder.itemView;
                loadingCell.setProgressVisible(loadsCount > 1);
            } else if (position >= messagesStartRow && position < messagesEndRow) {
                MessageObject message = messages.get(messages.size() - (position - messagesStartRow) - 1);
                View view = holder.itemView;

                boolean selected = false;
                boolean disableSelection = false;
                if (actionBar.isActionModeShowed()) {
                    MessageObject messageObject = null;
                    if (messageObject == message || selectedMessagesIds[message.getDialogId() == dialog_id ? 0 : 1].containsKey(message.getId())) {
                        //view.setBackgroundColor(Theme.MSG_SELECTED_BACKGROUND_COLOR);
                        selected = true;
                    } else {
                        view.setBackgroundColor(0);
                    }
                    disableSelection = true;
                } else {
                    view.setBackgroundColor(0);
                }

                if (view instanceof ChatMessageCell) {
                    ChatMessageCell messageCell = (ChatMessageCell) view;
                    messageCell.isChat = false;
                    messageCell.setMessageObject(message, null, false, false);
                    messageCell.setCheckPressed(!disableSelection, disableSelection && selected);
                    messageCell.setHighlighted(highlightMessageId != Integer.MAX_VALUE && message.getId() == highlightMessageId);
                    if (searchContainer != null && searchContainer.getVisibility() == View.VISIBLE && MediaDataController.getInstance(currentAccount).getLastSearchQuery() != null) {
                        messageCell.setHighlightedText(MediaDataController.getInstance(currentAccount).getLastSearchQuery());
                    } else {
                        messageCell.setHighlightedText(null);
                    }
                } else if (view instanceof ChatActionCell) {
                    ChatActionCell actionCell = (ChatActionCell) view;
                    actionCell.setMessageObject(message);
                } else if (view instanceof ChatUnreadCell) {
                    ChatUnreadCell unreadCell = (ChatUnreadCell) view;
                    unreadCell.setText(LocaleController.formatPluralString("NewMessages", unread_to_load));
                }
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (position >= messagesStartRow && position < messagesEndRow) {
                return messages.get(messages.size() - (position - messagesStartRow) - 1).contentType;
            } else if (position == botInfoRow) {
                return 3;
            }
            return 4;
        }

        @Override
        public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
            if (holder.itemView instanceof ChatMessageCell) {
                final ChatMessageCell messageCell = (ChatMessageCell) holder.itemView;
                messageCell.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        messageCell.getViewTreeObserver().removeOnPreDrawListener(this);

                        int height = chatListView.getMeasuredHeight();
                        int top = messageCell.getTop();
                        int bottom = messageCell.getBottom();
                        int viewTop = top >= 0 ? 0 : -top;
                        int viewBottom = messageCell.getMeasuredHeight();
                        if (viewBottom > height) {
                            viewBottom = viewTop + height;
                        }
                        messageCell.setVisiblePart(viewTop, viewBottom - viewTop, 0, 0,0,0,0,0,0);

                        return true;
                    }
                });
                messageCell.setHighlighted(highlightMessageId != Integer.MAX_VALUE && messageCell.getMessageObject().getId() == highlightMessageId);
            }
        }

        public void updateRowWithMessageObject(MessageObject messageObject) {
            int index = messages.indexOf(messageObject);
            if (index == -1) {
                return;
            }
            notifyItemChanged(messagesStartRow + messages.size() - index - 1);
        }

        @Override
        public void notifyDataSetChanged() {
            updateRows();
            try {
                super.notifyDataSetChanged();
            } catch (Exception e) {
                FileLog.e("tmessages", e);
            }
        }

        @Override
        public void notifyItemChanged(int position) {
            updateRows();
            try {
                super.notifyItemChanged(position);
            } catch (Exception e) {
                FileLog.e("tmessages", e);
            }
        }

        @Override
        public void notifyItemRangeChanged(int positionStart, int itemCount) {
            updateRows();
            try {
                super.notifyItemRangeChanged(positionStart, itemCount);
            } catch (Exception e) {
                FileLog.e("tmessages", e);
            }
        }

        @Override
        public void notifyItemInserted(int position) {
            updateRows();
            try {
                super.notifyItemInserted(position);
            } catch (Exception e) {
                FileLog.e("tmessages", e);
            }
        }

        @Override
        public void notifyItemMoved(int fromPosition, int toPosition) {
            updateRows();
            try {
                super.notifyItemMoved(fromPosition, toPosition);
            } catch (Exception e) {
                FileLog.e("tmessages", e);
            }
        }

        @Override
        public void notifyItemRangeInserted(int positionStart, int itemCount) {
            updateRows();
            try {
                super.notifyItemRangeInserted(positionStart, itemCount);
            } catch (Exception e) {
                FileLog.e("tmessages", e);
            }
        }

        @Override
        public void notifyItemRemoved(int position) {
            updateRows();
            try {
                super.notifyItemRemoved(position);
            } catch (Exception e) {
                FileLog.e("tmessages", e);
            }
        }

        @Override
        public void notifyItemRangeRemoved(int positionStart, int itemCount) {
            updateRows();
            try {
                super.notifyItemRangeRemoved(positionStart, itemCount);
            } catch (Exception e) {
                FileLog.e("tmessages", e);
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onFailedDownload(String fileName, boolean canceled) {

    }

    @Override
    public void onSuccessDownload(String fileName) {
        startDownloading(messages);
    }

    @Override
    public void onProgressDownload(String fileName, long downloadedSize, long totalSize) {

    }

    @Override
    public void onProgressUpload(String fileName, long uploadedSize, long totalSize, boolean isEncrypted) {

    }

    @Override
    public int getObserverTag() {
        return 0;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public TLObject getDownloadObject(MessageObject messageObject) {
        TLRPC.MessageMedia media = messageObject.messageOwner.media;
        if (media != null) {
            if (media.document != null) {
                return media.document;
            }
            if (media.webpage != null && media.webpage.document != null) {
                return media.webpage.document;
            }
            if (media.webpage != null && media.webpage.photo != null) {
                return FileLoader.getClosestPhotoSizeWithSize(media.webpage.photo.sizes, AndroidUtilities.getPhotoSize());
            }
            if (media.photo != null) {
                return FileLoader.getClosestPhotoSizeWithSize(media.photo.sizes, AndroidUtilities.getPhotoSize());
            }
        }
        return new TLRPC.TL_messageMediaEmpty();
    }

    //Temp x
    private void loadFile(TLObject attach, MessageObject messageObject) {
        if (attach instanceof TLRPC.PhotoSize) {
            FileLoader.getInstance(currentAccount).loadFile(ImageLocation.getForPhoto((TLRPC.PhotoSize) attach, messageObject.messageOwner.media.photo), messageObject, null, 0, 0);
        } else if (attach instanceof TLRPC.Document) {
            FileLoader.getInstance(currentAccount).loadFile((TLRPC.Document) attach, messageObject, 0, 0);
        } else if (attach instanceof WebFile) {
            FileLoader.getInstance(currentAccount).loadFile((WebFile) attach, 0, 0);
        }
    }

    private void startDownloading(ArrayList<MessageObject> messageObjects) {
        SharedStorage.downloadModule(DownloadHelper.Modules.RUNNING, true);
        for (MessageObject messageObject : messageObjects) {
            TLObject attach = getDownloadObject(messageObject);
            loadFile(attach, messageObject);
            File pathToMessage = FileLoader.getInstance(currentAccount).getPathToMessage(messageObject.messageOwner);
            if (pathToMessage != null && !pathToMessage.exists()) {
                DownloadController.getInstance(currentAccount).addLoadingFileObserver(FileLoader.getAttachFileName(attach), DownloadManagerActivity.this);
                return;
            }
        }
    }

    private void stopDownloading() {
        SharedStorage.downloadModule(DownloadHelper.Modules.RUNNING, false);
        for (int i = 0; i < messages.size(); i++) {
            MessageObject messageObject = messages.get(i);
            if (messageObject != null) {
                TLObject attach = getDownloadObject(messageObject);
                if (attach instanceof TLRPC.PhotoSize) {
                    FileLoader.getInstance(currentAccount).cancelLoadFile((TLRPC.PhotoSize) attach);
                } else if (attach instanceof TLRPC.Document) {
                    FileLoader.getInstance(currentAccount).cancelLoadFile((TLRPC.Document) attach);
                }
            }
        }
        ;

    }

    private boolean downloaded(MessageObject messageObject) {
        boolean downloaded = false;
        if (messageObject.messageOwner.attachPath != null && messageObject.messageOwner.attachPath.length() != 0) {
            File f = new File(messageObject.messageOwner.attachPath);
            if (f.exists()) {
                downloaded = true;
            }
        }
        if (!downloaded) {
            File f = FileLoader.getInstance(currentAccount).getPathToMessage(messageObject.messageOwner);
            if (f.exists()) {
                downloaded = true;
            }
        }
        return downloaded;
    }

    //////// Query ////////
    public void DM_DeleteMessage(final ArrayList<TLRPC.Message> messages) {
        MessagesStorage.getInstance(currentAccount).getStorageQueue().postRunnable(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int a = 0; a < messages.size(); a++) {
                        TLRPC.Message message = messages.get(a);
                        MessagesStorage.getInstance(currentAccount).getDatabase().executeFast(String.format(Locale.US, "DELETE FROM my_idm WHERE mid = %d", message.id)).stepThis().dispose();
                    }
                } catch (Exception e) {
                    FileLog.e("tmessages", e);
                }
            }
        });
    }

    public void DM_DeleteDownloaded(final ArrayList<TLRPC.Message> messages) {
        MessagesStorage.getInstance(currentAccount).getStorageQueue().postRunnable(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int a = 0; a < messages.size(); a++) {
                        TLRPC.Message message = messages.get(a);
                        boolean downloaded = false;
                        if (message.attachPath != null && message.attachPath.length() != 0) {
                            File f = new File(message.attachPath);
                            if (f.exists()) {
                                downloaded = true;
                            }
                        }
                        if (!downloaded) {
                            File f = FileLoader.getInstance(currentAccount).getPathToMessage(message);
                            if (f.exists()) {
                                downloaded = true;
                            }
                        }
                        if (downloaded) {
                            MessagesStorage.getInstance(currentAccount).getDatabase().executeFast(String.format(Locale.US, "DELETE FROM my_idm WHERE mid = %d", message.id)).stepThis().dispose();
                        }
                    }
                } catch (Exception e) {
                    FileLog.e("tmessages", e);
                }
            }
        });
    }

    public void DM_DeleteAll() {
        MessagesStorage.getInstance(currentAccount).getStorageQueue().postRunnable(new Runnable() {
            @Override
            public void run() {
                try {
                    MessagesStorage.getInstance(currentAccount).getDatabase().executeFast(String.format(Locale.US, "DELETE FROM my_idm")).stepThis().dispose();
                } catch (Exception e) {
                    FileLog.e("tmessages", e);
                }
            }
        });
    }

    public void DM_LoadMessagesByClassGuid(final int classGuid) {
        MessagesStorage.getInstance(currentAccount).getStorageQueue().postRunnable(new Runnable() {
            @Override
            public void run() {
                final TLRPC.TL_messages_messages res = new TLRPC.TL_messages_messages();
                SQLiteCursor cursor = null;
                try {
                    cursor = MessagesStorage.getInstance(currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT * FROM my_idm ORDER BY date ASC"));
                    while (cursor.next()) {
                        NativeByteBuffer data = cursor.byteBufferValue(3);
                        if (data != null) {
                            TLRPC.Message message = TLRPC.Message.TLdeserialize(data, data.readInt32(false), false);
                            data.reuse();
                            message.id = cursor.intValue(0);
                            message.dialog_id = cursor.intValue(1);
                            message.date = cursor.intValue(2);

                            res.messages.add(message);
                        }
                    }
                } catch (Exception e) {
                    FileLog.e("tmessages", e);
                } finally {
                    if (cursor != null) {
                        cursor.dispose();
                    }
                }

                final ArrayList<MessageObject> objects = new ArrayList<>();
                final HashMap<Long, TLRPC.User> usersDict = new HashMap<>();
                final HashMap<Long, TLRPC.Chat> chatsDict = new HashMap<>();
                for (int a = 0; a < res.users.size(); a++) {
                    TLRPC.User u = res.users.get(a);
                    usersDict.put(u.id, u);
                }
                for (int a = 0; a < res.chats.size(); a++) {
                    TLRPC.Chat c = res.chats.get(a);
                    chatsDict.put(c.id, c);
                }
                for (int a = 0; a < res.messages.size(); a++) {
                    TLRPC.Message message = res.messages.get(a);
                    MessageObject messageObject = new MessageObject(currentAccount, message, usersDict, chatsDict, true, false);
                    objects.add(messageObject);
                }
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        NotificationCenter.getInstance(currentAccount).postNotificationName(NotificationCenter.messagesDidLoad, 0, objects.size(), objects, true, 0, 0, 0, 0, 0, true, classGuid, 0);
                    }
                });
            }
        });
    }
}
