package com.sharkozp.testproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.sharkozp.testproject.fragment.GenerateFragment;
import com.sharkozp.testproject.fragment.MainTabbedFragment;
import com.sharkozp.testproject.fragment.SplashFragment;
import com.sharkozp.testproject.system.Constants;
import com.sharkozp.testproject.system.UpdaterService;

import org.testpackage.test_sdk.android.testlib.API;
import org.testpackage.test_sdk.android.testlib.interfaces.SuccessCallback;

public class MainActivity extends AppCompatActivity implements SuccessCallback {
    private static final String FIRST_LAUNCH = "first_launch";
    private static final int SPLASH_INTERVAL = 1000;
    private Intent serviceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        serviceIntent = new Intent(this, UpdaterService.class);
        setContentView(R.layout.activity_main);
        API.INSTANCE.init(getApplicationContext());
        if (savedInstanceState != null) {
            //Restore the fragment's instance
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(Constants.CURRENT_FRAGMENT);
            getSupportFragmentManager().beginTransaction().replace(R.id.item_detail_container, fragment, Constants.CURRENT_FRAGMENT).commit();
        } else {
            initApp();
        }
    }

    private void showSplashScreen(boolean isFirstLaunch) {
        getSupportFragmentManager().beginTransaction().replace(R.id.item_detail_container, SplashFragment.newInstance(), Constants.CURRENT_FRAGMENT).commit();

        if (!isFirstLaunch) {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Fragment fragment = (getListCount() > 0) ? MainTabbedFragment.newInstance() : GenerateFragment.newInstance();
                    showContent(fragment);
                }
            }, SPLASH_INTERVAL);
        }
    }

    private void showContent(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.item_detail_container, fragment,Constants.CURRENT_FRAGMENT).commit();
    }

    private int getListCount() {
        SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME, 0);
        return settings.getInt(Constants.LIST_COUNT, 0);
    }

    private void initApp() {
        SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME, 0);
        boolean isFirstLaunch = settings.getBoolean(FIRST_LAUNCH, true);
        showSplashScreen(isFirstLaunch);
        if (isFirstLaunch) {
            new BackgroundTask().execute();
        }
    }

    @Override
    public void onSuccess() {
        SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME, 0);
        settings.edit().putBoolean(FIRST_LAUNCH, false).apply();
        showContent(GenerateFragment.newInstance());
    }


    private class BackgroundTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... tasks) {
            API.INSTANCE.refreshPersons(MainActivity.this);
            return null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startService(serviceIntent);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        API.INSTANCE.unSubscribeUpdates();
        stopService(serviceIntent);
    }
}
