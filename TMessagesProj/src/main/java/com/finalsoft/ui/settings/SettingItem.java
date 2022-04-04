package com.finalsoft.ui.settings;

import android.content.Context;
import android.view.View;

import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Cells.EmptyCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.NotificationsCheckCell;
import org.telegram.ui.Cells.RadioCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.SeekBarView;

public class SettingItem {
    public int id;
    public String name;
    public int icon;
    public String info;
    public boolean divider;
    public Type type;
    public BaseFragment fragment;

    public SettingItem(int id, String name, int icon) {
        this(id, name, icon, "");
    }

    public SettingItem(int id, String name, int icon, String info) {
        this(id, name, icon, info, true, Type.TEXT_CELL, null);
    }

    public SettingItem(int id, String name, int icon, Type type) {
        this(id, name, icon, "", true, type, null);
    }

    public SettingItem(int id, String name, int icon, boolean divider) {
        this(id, name, icon, "", divider, Type.TEXT_CELL, null);
    }

    public SettingItem(int id, String name, int icon, boolean divider, BaseFragment fragment) {
        this(id, name, icon, "", divider, Type.TEXT_CELL, fragment);
    }

    public SettingItem(int id, String name, int icon, String info, boolean divider, Type type, BaseFragment fragment) {
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.info = info;
        this.divider = divider;
        this.type = type;
        this.fragment = fragment;
    }

    public int getId() {
        return id;
    }

    public BaseFragment getFragment() {
        return fragment;
    }

    public String getName() {
        return name;
    }

    public int getIcon() {
        return icon;
    }

    public String getInfo() {
        return info;
    }

    public boolean isDivider() {
        return divider;
    }

    public int getType() {
        return type.ordinal();
    }

    public enum Type {
        EMPTY_CELL,
        DIVIDER,
        TEXT_CELL,
        HEADER_CELL,
        TEXT_INFO_PRIVACY_CELL,
        SEEK_BAR,
        TEXT_CHECK_CELL,
        TEXT_SETTING_CELL,
        NOT_CHECK_CELL,
        RADIO_CELL
    }

    public static View getView(Context mContext,Type type) {
        switch (type) {
            case EMPTY_CELL:
                return new EmptyCell(mContext);
            case DIVIDER:
                return new ShadowSectionCell(mContext);
            case TEXT_CELL:
                return new TextCell(mContext);
            case HEADER_CELL:
                return new HeaderCell(mContext);
            case TEXT_INFO_PRIVACY_CELL:
                return new TextInfoPrivacyCell(mContext);
            case SEEK_BAR:
                return new SeekBarView(mContext);
            case TEXT_CHECK_CELL:
                return new TextCheckCell(mContext);
            case TEXT_SETTING_CELL:
                return new TextSettingsCell(mContext);
            case NOT_CHECK_CELL:
                return new NotificationsCheckCell(mContext);
            case RADIO_CELL:
                return new RadioCell(mContext);
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }

    }
}