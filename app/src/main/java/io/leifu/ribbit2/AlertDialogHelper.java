package io.leifu.ribbit2;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

public class AlertDialogHelper {

    public static void builder(Context context, String message, String title) {
        new AlertDialog.Builder(context)
                .setMessage(message)
                .setTitle(title)
                .setPositiveButton(android.R.string.ok, null)
                .create()
                .show();
    }

    public static void builder(Context context, int itemId, DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(context)
                .setItems(itemId, listener)
                .create()
                .show();
    }
}
