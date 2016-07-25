package io.leifu.ribbit2;

import android.content.Context;
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
}
