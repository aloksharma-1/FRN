package com.alokdev21.foodapk.utils;

import android.content.Context;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

public class SnackbarHelper {
    public static void showSnackbar(Context context, View view, String message, int backgroundColor) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(backgroundColor);
        snackbar.show();
    }
}
