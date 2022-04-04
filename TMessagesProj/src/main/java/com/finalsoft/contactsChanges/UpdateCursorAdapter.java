package com.finalsoft.contactsChanges;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cursoradapter.widget.CursorAdapter;

import org.telegram.ui.Components.BackupImageView;

public class UpdateCursorAdapter extends CursorAdapter {
    private DBHelper dataBaseAccess;

    public class ViewHolder {
        BackupImageView avatarImageView;
        TextView tvNewValue;
        TextView tvOldValue;
    }

    public UpdateCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
        this.dataBaseAccess = new DBHelper();
    }

    public void bindView(View view, Context context, Cursor cursor) {
        ((UpdateCell) view).setData(dataBaseAccess.updateModel(cursor));
    }

    @SuppressLint("RestrictedApi")
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return new UpdateCell(this.mContext, 10);
    }
}
