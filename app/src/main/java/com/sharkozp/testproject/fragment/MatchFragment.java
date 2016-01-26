package com.sharkozp.testproject.fragment;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.sharkozp.testproject.R;
import com.sharkozp.testproject.system.Constants;
import com.sharkozp.testproject.system.database.Person;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MatchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MatchFragment extends MainFragment implements View.OnClickListener {
    private int personId;
    private ImageView imageView;
    private View personWrapper;
    private ProgressBar progressBar;


    public MatchFragment() {
        // Required empty public constructor
    }

    public static MatchFragment newInstance(int personId) {
        MatchFragment fragment = new MatchFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.PERSON_ID, personId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            personId = getArguments().getInt(Constants.PERSON_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_match, container, false);
        imageView = (ImageView) rootView.findViewById(R.id.imageView);
        personWrapper = rootView.findViewById(R.id.personWrapper);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        rootView.findViewById(R.id.backButton).setOnClickListener(this);
        Person person = getPersonById(personId);
        new ImageTask().execute(person.getPhoto());

        //remove person from the list
        removePerson(person);
        //update list count
        saveListCount(getPersonCount());
        return rootView;
    }

    @Override
    public void onClick(View v) {
        getFragmentManager().beginTransaction().replace(R.id.item_detail_container, MainTabbedFragment.newInstance(),Constants.CURRENT_FRAGMENT).commit();
    }

    private class ImageTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... photos) {
            InputStream inputStream = null;
            Bitmap bitmap = null;
            try {
                URL url = new URL(photos[0]);
                inputStream = url.openConnection().getInputStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
            } catch (IOException e) {
                Log.e(Constants.TAG, e.getMessage());
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        Log.e(Constants.TAG, e.getMessage());
                    }
                }
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            imageView.setImageBitmap(bitmap);
            progressBar.setVisibility(View.GONE);
            personWrapper.setVisibility(View.VISIBLE);
        }
    }
}
