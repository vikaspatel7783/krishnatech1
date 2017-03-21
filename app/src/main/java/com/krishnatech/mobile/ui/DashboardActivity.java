package com.krishnatech.mobile.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.krishnatech.mobile.R;
import com.krishnatech.mobile.ServiceContext;

import java.util.HashMap;
import java.util.Map;

import static com.krishnatech.mobile.ui.LogoutService.requestId;

public class DashboardActivity extends ParentActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, LogoutService.LogoutCallback {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments

        switch (position) {
            case 0:
                break;

            case 1: // Alerts
                startActivity(new Intent(this, AlertsActivity.class));
                break;

            case 5: // Logout
                showProgressbar("Logging out...");

                Map<String, String> header = new HashMap<>();
                header.put(ServiceContext.KEY_AUTHORIZATION, ServiceContext.getInstance().getToken());

                new LogoutService(getApplicationContext(),
                        requestId,
                        Request.Method.GET,
                        UiUtil.BASE_URL +"/logout",
                        null,
                        header,
                        null,
                        this).logout();
                break;
        }


        /*FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();*/
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
            case 4:
                mTitle = getString(R.string.title_section4);
                break;
            case 5:
                mTitle = getString(R.string.title_section5);
                break;
            case 6:
                mTitle = getString(R.string.title_section6);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    /*protected void showProgressbar(String message) {
        showDialog(new ProgressBarDialogFragment().withMessage(message), TAG_PROGRESS_DIALOG);
    }

    protected void dismissProgressbar() {
        ProgressBarDialogFragment progressBarDialogFragment = (ProgressBarDialogFragment) getFragmentManager().findFragmentByTag(TAG_PROGRESS_DIALOG);
        if (progressBarDialogFragment != null) {
            progressBarDialogFragment.dismiss();
        }
    }

    public boolean showDialog(DialogFragment dialogFragment, String tag) {
        FragmentManager fragmentManager = getFragmentManager();
        Fragment fragmentByTag = fragmentManager.findFragmentByTag(tag);
        if (fragmentByTag == null) {
            dialogFragment.show(fragmentManager, tag);
            return true;
        }
        return false;
    }*/

    @Override
    public void onLoggedOut() {
        dismissProgressbar();
        finish();
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((DashboardActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}
