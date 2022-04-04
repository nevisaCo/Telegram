package com.finalsoft.controller;

import android.util.Log;

import com.finalsoft.Config;
import com.finalsoft.SharedStorage;
import com.finalsoft.firebase.channel.RequestManager;
import com.finalsoft.firebase.channel.TLRPCResponseHandler;
import com.finalsoft.models.PromoItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class PromoController {
    private static final String TAG = Config.TAG + "pc";
    public static PromoController promoController;
    ArrayList<PromoItem> promoItems = new ArrayList<>();
    int counter = 0;
    int counter_success = 0;
    boolean isReady = false;

    public static PromoController getInstance() {
        if (promoController == null) {
            promoController = new PromoController();
        }
        return promoController;
    }

    private boolean getShowPromo() {
        return isReady && Config.PROMO_FEATURE;
    }

    public void init() {
        if (!BuildVars.PROMO_FEATURE) {
            return;
        }

        counter = 0;
        counter_success = 0;

        if (BuildVars.DEBUG_VERSION) {
            promoItems.clear();
            promoItems.add(new PromoItem("fereidoon99", "test", "aaaaa", new int[]{0, 1, 4}));
        } else {
            try {
                String json = SharedStorage.promo();
                if (json == null || json.isEmpty()) {
                    Log.i(TAG, "PromoController > init: json is empty");
                    return;
                }
                JSONArray jsonArray = new JSONArray(json);
                Gson gson = new Gson();
                Type typeOfList = new TypeToken<ArrayList<PromoItem>>() {
                }.getType();
                promoItems.clear();
                promoItems.addAll(gson.fromJson(jsonArray.toString(), typeOfList));
            } catch (JSONException e) {
                Log.e(TAG, "PromoController > init > error: ", e);
                return;
            }
        }

        int selectedAccount = UserConfig.selectedAccount;
        AndroidUtilities.runOnUIThread(() -> {
            try {
                for (PromoItem promoItem : promoItems) {
                    if (promoItem.getDialog() == null) {
                        RequestManager.findChannelInfo(selectedAccount, promoItem.getLink(), new TLRPCResponseHandler<TLRPC.Chat>() {
                            @Override
                            public void onSuccess(TLRPC.Chat chat) {
                                if (chat != null) {
//                                    Log.i(TAG, "addPromo: " + new Gson().toJson(chat));
                                    TLRPC.TL_dialog dialog = new TLRPC.TL_dialog();
                                    dialog.id = -chat.id;
                                    dialog.pinned = false;
                                    dialog.pinnedNum = 0;
                                    dialog.unread_count = 0;
//                                    dialog.unread_mark = true;
           /*                     TLRPC.DraftMessage draftMessage = new TLRPC.DraftMessage() {
                                };
                                draftMessage.message = promoItem.getLast_message();
                                dialog.draft = draftMessage;*/
                                    dialog.unread_mentions_count = 2;
                                    dialog.last_message_date = Integer.MAX_VALUE;
                                    TLRPC.Peer peer = new TLRPC.Peer() {
                                    };
                                    peer.chat_id = chat.id;
                                    dialog.peer = peer;

                                    promoItem.setDialog(dialog);
        /*                            MessagesController.getInstance(selectedAccount).dialogs_dict.put(dialog.id, dialog);
                                ArrayList<Integer> aa = new ArrayList<>();
                                aa.add(Integer.MAX_VALUE);
                                MessagesController.getInstance(selectedAccount).reloadMessages(aa, dialog.id, false);*/
                                    MessagesController.getInstance(selectedAccount).putChat(chat, false);
                                    MessagesController.getInstance(selectedAccount).addDialogToItsFolder(0, dialog);
                                    counter_success++;
                                }
                                reload();

                            }

                            @Override
                            public void onFailed(TLRPC.TL_error tL_error) {
                                Log.e(TAG, "addPromo > error : " + new Gson().toJson(tL_error));
                                reload();
                            }

                            @Override
                            public void onStart() {

                            }


                        });
                    } else {
                        Log.i(TAG, "PromoController > init: dialog exist:" + promoItem.getDialog().id);
                        counter_success++;
                        reload();
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "PromoController > init > error: ", e);
            }

        });
    }

    public void setPromos(String json) {
        String old = SharedStorage.promo();
        if (json == null || json.isEmpty()) {
            if (old != null && !old.isEmpty()) {
                promoItems.clear();
                SharedStorage.promo(null);
                removeDialogs();
            }
            Log.e(TAG, "PromoController > setPromos: json is empty!");
            return;
        }

        if (!old.equals(json)) {
            SharedStorage.promo(json);
            init();
        }
    }

    private void reload() {
        counter++;
        Log.i(TAG, "reload > counter :" + counter + " , counter_success:" + counter_success);
        if (counter >= promoItems.size()) {
            Log.i(TAG, "reload exec! ");
            if (counter_success > 0) {
                Log.i(TAG, "reload succeeded");
                isReady = true;
                MessagesController.getInstance(UserConfig.selectedAccount).sortDialogs(null);
                MessagesController.getInstance(UserConfig.selectedAccount).loadDialogs(0, 0, 20, true);
            } else {
                Log.e(TAG, "reload failed!");
            }
        }
    }

    public PromoItem getMessageInfo(long currentDialogId) {
        if (getShowPromo()) {
            for (PromoItem promoItem : promoItems) {
                if (promoItem.getDialog() != null && promoItem.getDialog().id == currentDialogId) {
                    return promoItem;
                }
            }
        }
        return null;
    }

    public void addPromoToDialogs() {
        if (!getShowPromo()) {
            return;
        }
        ArrayList<TLRPC.Dialog> dialogs = null;
        for (PromoItem promoItem : promoItems) {
            if (promoItem.getDialog() == null) {
                continue;
            }
            for (Integer item : promoItem.getTabs()) {

                if (item == 0) {
                    dialogs = MessagesController.getInstance(UserConfig.selectedAccount).getAllDialogs();
                } else {
                    if (MessagesController.getInstance(UserConfig.selectedAccount).dialogFilters.size() >= item) {
                        dialogs = MessagesController.getInstance(UserConfig.selectedAccount).dialogFilters.get(item - 1).dialogs;
                    }
                }


                if (dialogs == null /*|| dialogs.size() == 0*/) {
                    continue;
                }
                if (!dialogs.contains(promoItem.getDialog()) /*|| !(dialogs.get(0) instanceof DialogAddCell)*/) {
                    if (MessagesController.getInstance(UserConfig.selectedAccount).getAllDialogs() != null) {
                        int index = dialogs.size() == 0 ? 0 : 1;
                        dialogs.add(index, promoItem.getDialog());
                        if (BuildVars.DEBUG_VERSION) {
                            Log.i(TAG, "addPromoToDialogs :  dialog.add(0, dialogAddCell)");
                        }
                    } else {
                        if (BuildVars.DEBUG_VERSION) {
                            Log.i(TAG, "addPromoToDialogs: getAllDialogs is Null!");
                        }
                    }
                } else {
                    if (BuildVars.DEBUG_VERSION) {
                        Log.i(TAG, "addPromoToDialogs: dialogs contain ad!");
                    }
                }
            }
        }


    }

    private void removeDialogs() {
        if (getShowPromo()) {
            for (PromoItem promoItem : promoItems) {
                if (promoItem.getDialog() != null) {
                    try {
                        MessagesController.getInstance(UserConfig.selectedAccount).dialogsUsersOnly.remove(promoItem.getDialog());
                        MessagesController.getInstance(UserConfig.selectedAccount).dialogsChannelsOnly.remove(promoItem.getDialog());
                        MessagesController.getInstance(UserConfig.selectedAccount).dialogsBotOnly.remove(promoItem.getDialog());
                        MessagesController.getInstance(UserConfig.selectedAccount).dialogsUnreadOnly.remove(promoItem.getDialog());
                        MessagesController.getInstance(UserConfig.selectedAccount).dialogsScheduledOnly.remove(promoItem.getDialog());
                        MessagesController.getInstance(UserConfig.selectedAccount).dialogsFavoriteOnly.remove(promoItem.getDialog());
                        MessagesController.getInstance(UserConfig.selectedAccount).dialogsGroupsOnly.remove(promoItem.getDialog());
                        MessagesController.getInstance(UserConfig.selectedAccount).dialogsSuperGroupsOnly.remove(promoItem.getDialog());
                        MessagesController.getInstance(UserConfig.selectedAccount).dialogsOnLineOnly.remove(promoItem.getDialog());
                        MessagesController.getInstance(UserConfig.selectedAccount).dialogsMineOnly.remove(promoItem.getDialog());
                        MessagesController.getInstance(UserConfig.selectedAccount).dialogsByFolder.get(0).remove(promoItem.getDialog());
                    } catch (Exception e) {
                        Log.e(TAG, "PromoController > removeDialogs: ", e);
                    }
                }
            }
        }
    }
}
