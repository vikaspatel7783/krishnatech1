package com.krishnatech.mobile.ui;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;

import com.krishnatech.mobile.R;

public class ParentActivity extends Activity {

    private static final String TAG_PROGRESS_DIALOG = "ProgressDialog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        playInAnimation();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        playOutAnimation();
    }

    protected void showProgressbar(String message) {
        showDialog(new ProgressBarDialogFragment().withMessage(message), TAG_PROGRESS_DIALOG);
    }

    protected void dismissProgressbar() {
        ProgressBarDialogFragment progressBarDialogFragment = (ProgressBarDialogFragment) getFragmentManager().findFragmentByTag(TAG_PROGRESS_DIALOG);
        if (progressBarDialogFragment != null) {
            progressBarDialogFragment.dismiss();
        }
    }

    private boolean showDialog(DialogFragment dialogFragment, String tag) {
        FragmentManager fragmentManager = getFragmentManager();
        Fragment fragmentByTag = fragmentManager.findFragmentByTag(tag);
        if (fragmentByTag == null) {
            dialogFragment.show(fragmentManager, tag);
            return true;
        }
        return false;
    }

    private void playInAnimation() {
        overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
    }

    private void playOutAnimation(){
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

}
