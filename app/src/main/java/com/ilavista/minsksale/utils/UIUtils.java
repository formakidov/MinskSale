package com.ilavista.minsksale.utils;

import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

import com.ilavista.minsksale.R;

public class UIUtils {
    public static void showSnackBar(View parentView, String text) {
        showSnackBar(parentView, text, Snackbar.LENGTH_LONG);
    }

    public static void showSnackBar(View parentView, String text, int length) {
        Snackbar snack = Snackbar.make(parentView, text, length);
        View view = snack.getView();
        view.setBackgroundResource(R.color.colorPrimary);
        TextView snackTextView = view.findViewById(android.support.design.R.id.snackbar_text);
        snackTextView.setTextColor(Color.parseColor("#6c440b"));
        snack.show();
    }
}
