package com.finalsoft.ui.tab;

import com.finalsoft.SharedStorage;

import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

public class InternalFilters {
    public static final int SCHEDULED = MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_ARCHIVED |
            MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_MUTED;
    ;

    public static final int MEGA_GROUP = MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_ARCHIVED;

    public static final int MINE = MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_MUTED;

    public static final int UNREAD = MessagesController.DIALOG_FILTER_FLAG_CONTACTS |
            MessagesController.DIALOG_FILTER_FLAG_NON_CONTACTS |
            MessagesController.DIALOG_FILTER_FLAG_GROUPS |
            MessagesController.DIALOG_FILTER_FLAG_CHANNELS |
            MessagesController.DIALOG_FILTER_FLAG_BOTS |
            MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_READ |
            MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_ARCHIVED;


    public static int FAV = MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_ARCHIVED |
            MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_READ |
            MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_MUTED;

    public static int ONLINE = MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_ARCHIVED |
            MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_READ;

    public static LinkedList<TLRPC.TL_dialogFilterSuggested> internalFilters = new LinkedList<>();

    static {

        /*usersFilter =*/
        mkFilter(LocaleController.getString("NotificationsUsers", R.string.FilterNameUsers),
                LocaleController.getString("FilterNameUsersDescription", R.string.FilterNameUsersDescription),
                MessagesController.DIALOG_FILTER_FLAG_CONTACTS |
                        MessagesController.DIALOG_FILTER_FLAG_NON_CONTACTS |
                        MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_ARCHIVED,
                (it) -> {

                    it.contacts = true;
                    it.non_contacts = true;
                    it.exclude_archived = true;
                    it.emoticon = String.valueOf( R.drawable.menu_contacts);
                });

        /*contactsFilter = */
        mkFilter(LocaleController.getString("FilterNameContacts", R.string.FilterNameContacts),
                LocaleController.getString("FilterNameContactsDescription", R.string.FilterNameContactsDescription),
                MessagesController.DIALOG_FILTER_FLAG_CONTACTS |
                        MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_ARCHIVED,
                (it) -> {

                    it.contacts = true;
                    it.exclude_archived = true;
                    it.emoticon = String.valueOf( R.drawable.menu_chats);

                });


        /*nonContactsFilter =*/
        mkFilter(LocaleController.getString("FilterNameNonContact", R.string.FilterNameNonContact),
                LocaleController.getString("FilterNameNoneContactsDescription", R.string.FilterNameNoneContactsDescription),
                MessagesController.DIALOG_FILTER_FLAG_NON_CONTACTS,
                (it) -> {
                    it.non_contacts = true;
                    it.emoticon = String.valueOf( R.drawable.menu_secret_14);

                });

        /* groupsFilter =*/
        mkFilter(LocaleController.getString("FilterNameGroups", R.string.FilterNameGroups),
                LocaleController.getString("FilterNameContactsDescription", R.string.FilterNameGroupsDescription),
                MessagesController.DIALOG_FILTER_FLAG_GROUPS |
                        MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_ARCHIVED,
                (it) -> {

                    it.groups = true;
                    it.exclude_archived = true;
                    it.emoticon = String.valueOf( R.drawable.menu_groups);

                });


        /*favFilter = */
        mkFilter(LocaleController.getString("Favorites", R.string.Favorites),
                LocaleController.getString("FilterNameGroupsDescription", R.string.FilterNameGroupsDescription),
                FAV,
                (it) -> {
                    it.exclude_archived = true;
                    it.exclude_muted = true;
                    it.exclude_read = true;
                    it.emoticon = String.valueOf( R.drawable.msg_fave);
                });


        /* onlineFilter =*/
        mkFilter(LocaleController.getString("Online", R.string.Online),
                LocaleController.getString("FilterNameGroupsDescription", R.string.FilterNameGroupsDescription),
                ONLINE,
                (it) -> {
                    it.exclude_archived = true;
                    it.exclude_read = true;
                    it.emoticon = String.valueOf( R.drawable.ic_masks_recent1);
                });


        /*channelsFilter =*/
        mkFilter(LocaleController.getString("FilterNameChannels", R.string.FilterNameChannels),
                LocaleController.getString("FilterNameChannelsDescription", R.string.FilterNameChannelsDescription),
                MessagesController.DIALOG_FILTER_FLAG_CHANNELS |
                        MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_ARCHIVED,
                (it) -> {

                    it.broadcasts = true;
                    it.exclude_archived = true;
                    it.emoticon = String.valueOf( R.drawable.menu_broadcast);

                });

        /*      botsFilter =*/
        mkFilter(LocaleController.getString("FilterNameBots", R.string.FilterNameBots),
                LocaleController.getString("FilterNameBotsDescription", R.string.FilterNameBotsDescription),
                MessagesController.DIALOG_FILTER_FLAG_BOTS |
                        MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_ARCHIVED,
                (it) -> {

                    it.bots = true;
                    it.exclude_archived = true;
                    it.emoticon = String.valueOf( R.drawable.input_bot1);

                });

        /*        unmutedFilter = */
        mkFilter(LocaleController.getString("FilterNameUnmuted", R.string.FilterNameUnmuted),
                LocaleController.getString("FilterNameUnmutedDescription", R.string.FilterNameUnmutedDescription),
                MessagesController.DIALOG_FILTER_FLAG_CONTACTS |
                        MessagesController.DIALOG_FILTER_FLAG_NON_CONTACTS |
                        MessagesController.DIALOG_FILTER_FLAG_GROUPS |
                        MessagesController.DIALOG_FILTER_FLAG_CHANNELS |
                        MessagesController.DIALOG_FILTER_FLAG_BOTS |
                        MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_MUTED |
                        MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_ARCHIVED,
                (it) -> {

                    it.contacts = true;
                    it.non_contacts = true;
                    it.groups = true;
                    it.broadcasts = true;
                    it.bots = true;
                    it.exclude_muted = true;
                    it.exclude_archived = true;
                    it.emoticon = String.valueOf( R.drawable.msg_unmute);

                });

        /*  unreadFilter =*/
        mkFilter(LocaleController.getString("FilterNameUnread2", R.string.FilterNameUnread2),
                LocaleController.getString("FilterNameUnreadDescription", R.string.FilterNameUnreadDescription),
                UNREAD,
                (it) -> {

                    it.contacts = true;
                    it.non_contacts = true;
                    it.groups = true;
                    it.broadcasts = true;
                    it.bots = true;
                    it.exclude_read = true;
                    it.exclude_archived = true;
                    it.emoticon = String.valueOf( R.drawable.msg_markunread);

                });

        /*  unmutedAndUnreadFilter =*/
        mkFilter(LocaleController.getString("FilterNameUnmutedAndUnread", R.string.FilterNameUnmutedAndUnread),
                LocaleController.getString("FilterNameUnmutedAndUnreadDescription", R.string.FilterNameUnmutedAndUnreadDescription),
                MessagesController.DIALOG_FILTER_FLAG_CONTACTS |
                        MessagesController.DIALOG_FILTER_FLAG_NON_CONTACTS |
                        MessagesController.DIALOG_FILTER_FLAG_GROUPS |
                        MessagesController.DIALOG_FILTER_FLAG_CHANNELS |
                        MessagesController.DIALOG_FILTER_FLAG_BOTS |
                        MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_MUTED |
                        MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_READ |
                        MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_ARCHIVED,
                (it) -> {

                    it.contacts = true;
                    it.non_contacts = true;
                    it.groups = true;
                    it.broadcasts = true;
                    it.bots = true;
                    it.exclude_muted = true;
                    it.exclude_read = true;
                    it.exclude_archived = true;
                    it.emoticon = String.valueOf( R.drawable.msg_markunread);

                });


         mkFilter(LocaleController.getString("MegaGroups", R.string.MegaGroups),
                LocaleController.getString("MegaGroups", R.string.MegaGroups),
                MEGA_GROUP,
                (it) -> {
                    it.exclude_archived = true;
                    it.emoticon = String.valueOf( R.drawable.tab_sgroups_new);
                });

         mkFilter(LocaleController.getString("ScheduledMessages", R.string.ScheduledMessages),
                LocaleController.getString("ScheduledMessages", R.string.ScheduledMessages),
                SCHEDULED,
                (it) -> {
                    it.exclude_archived = true;
                    it.exclude_muted = true;
                    it.emoticon = String.valueOf( R.drawable.msg_schedule);
                });


         mkFilter(LocaleController.getString("Mine", R.string.Mine),
                LocaleController.getString("Mine", R.string.Mine),
                MINE,
                (it) -> {
                    it.exclude_muted = true;
                    it.emoticon = String.valueOf( R.drawable.msg_edit);
                });

    }

    public static Collection<Long> getAlwaysShow() {
        Collection<Long> col = new ArrayList<>();
        col.add(UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser().id);
        return col;
    }

    @FunctionalInterface
    interface FilterBuilder {

        void apply(TLRPC.TL_dialogFilter filter);

    }

    private static int currId = 10;

    private static void mkFilter(String name, String description, int flag, FilterBuilder builder) {

        TLRPC.TL_dialogFilterSuggested suggestedFilter = new TLRPC.TL_dialogFilterSuggested();

        suggestedFilter.description = description != null ? description : "Nya ~";

        suggestedFilter.filter = new TLRPC.TL_dialogFilter();

        suggestedFilter.filter.id = currId;

        suggestedFilter.filter.title = name;
        suggestedFilter.filter.flags = flag;
        
        builder.apply(suggestedFilter.filter);

        internalFilters.add(suggestedFilter);

        currId++;

//        return suggestedFilter.filter;

    }

    public static boolean isActive(int flag) {
        for (MessagesController.DialogFilter df : MessagesController.getInstance(UserConfig.selectedAccount).dialogFilters) {
            if (df.flags == flag) {
                return true;
            }
        }
        return false;
    }
}
