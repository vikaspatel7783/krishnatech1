package com.krishnatech.mobile.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;

import com.krishnatech.mobile.R;

public class ParentActivity extends Activity {

    private static final String TAG_PROGRESS_DIALOG = "ProgressDialog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.actionbar)));
            actionBar.setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        playInAnimation();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        playOutAnimation();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
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
