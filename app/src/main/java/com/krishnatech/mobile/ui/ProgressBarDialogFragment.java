
package com.krishnatech.mobile.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.TextView;

import com.krishnatech.mobile.R;

public class ProgressBarDialogFragment extends DialogFragment {

    public static final String KEY_ARG_MESSAGE = "KEY_ARG_MESSAGE";

    public ProgressBarDialogFragment() {
        if (getArguments() == null) {
            setArguments(new Bundle());
        }
        setCancelable(false);
    }

    public ProgressBarDialogFragment withMessage(String message) {
        setArgParam(KEY_ARG_MESSAGE, message);
        return this;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Activity activity = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        ViewGroup dialogView = (ViewGroup) activity.getLayoutInflater().inflate(
                R.layout.progress_bar_layout, null);

        setText(dialogView, getMessage());
        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);
        return alertDialog;
    }

    private void setArgParam(String keyParam, String param) {
        Bundle arge = getArguments();
        arge.putString(keyParam, param);
        setArguments(arge);
    }

    private void setText(ViewGroup dialogView, String text) {
        if (dialogView != null && !TextUtils.isEmpty(text)) {
            TextView titleTextView = (TextView) dialogView.findViewById(R.id.txt_progress_bar_msg);
            titleTextView.setText(text);
        }
    }

    public void setText(String text) {
        Dialog dialog = getDialog();
        if (dialog != null && !TextUtils.isEmpty(text)) {
            TextView titleTextView = (TextView) (dialog).findViewById(R.id.txt_progress_bar_msg);
            titleTextView.setText(text);
        }
    }

    private String getMessage() {
        return getArguments().getString(KEY_ARG_MESSAGE);
    }

    public boolean isShowing() {
        return getDialog() != null && getDialog().isShowing();
    }
}