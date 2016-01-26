package com.sharkozp.testproject.fragment;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sharkozp.testproject.R;
import com.sharkozp.testproject.system.Constants;
import com.sharkozp.testproject.system.Task;
import com.sharkozp.testproject.system.database.Person;

import org.testpackage.test_sdk.android.testlib.API;
import org.testpackage.test_sdk.android.testlib.interfaces.PersonsExtendedCallback;
import org.testpackage.test_sdk.android.testlib.interfaces.SuccessCallback;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by oleksandr on 1/24/16.
 */
public class GenerateFragment extends MainFragment implements PersonsExtendedCallback, View.OnClickListener, SuccessCallback {
    private static final String PAGE_NUMBER = "page_number";
    private Button generateButton;
    private ProgressBar progressBar;

    public static GenerateFragment newInstance() {
        return new GenerateFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_generate, container, false);
        generateButton = (Button) rootView.findViewById(R.id.generateButton);
        generateButton.setOnClickListener(this);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        return rootView;
    }

    @Override
    public void onClick(View v) {
        SharedPreferences settings = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);
        int page = settings.getInt(PAGE_NUMBER, 0);
        progressBar.setVisibility(View.VISIBLE);
        generateButton.setVisibility(View.GONE);
        new BackgroundTask(page).execute(Task.RESULT);

        //save next page for use
        settings.edit().putInt(PAGE_NUMBER, ++page).apply();
    }

    @Override
    public void onResult(String persons) {
        Type listType = new TypeToken<List<Person>>() {
        }.getType();
        List<Person> personList = new Gson().fromJson(persons, listType);
        if (personList.size() > 0) {
            //update count list
            saveListCount(personList.size());
            savePersons(personList);

            //only after all operations change screen
            getFragmentManager().beginTransaction().replace(R.id.item_detail_container, MainTabbedFragment.newInstance(),Constants.CURRENT_FRAGMENT).commit();
        } else {
            //regenerate list
            new BackgroundTask().execute(Task.GENERATE);
        }
    }

    @Override
    public void onFail(String reason) {
        Log.e(Constants.TAG, reason);
    }

    @Override
    public void onSuccess() {
        int page = 0;
        SharedPreferences settings = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);
        new BackgroundTask(page).execute(Task.RESULT);

        //save next page for use
        settings.edit().putInt(PAGE_NUMBER, ++page).apply();
    }

    private class BackgroundTask extends AsyncTask<Task, Void, Void> {
        private int page;

        public BackgroundTask() {
        }

        public BackgroundTask(int page) {
            this.page = page;
        }

        @Override
        protected Void doInBackground(Task... tasks) {
            switch (tasks[0]) {
                case GENERATE:
                    API.INSTANCE.refreshPersons(GenerateFragment.this);
                    break;
                case RESULT:
                    API.INSTANCE.getPersons(page, GenerateFragment.this);
            }
            return null;
        }
    }
}
