package com.sharkozp.testproject.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.sharkozp.testproject.R;
import com.sharkozp.testproject.system.Constants;
import com.sharkozp.testproject.system.NonSwipeableViewPager;
import com.sharkozp.testproject.system.ScreenSlidePagerAdapter;
import com.sharkozp.testproject.system.database.Person;
import com.sharkozp.testproject.system.database.UserChoice;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by oleksandr on 1/24/16.
 */
public class ImageFragment extends MainFragment implements View.OnClickListener {
    private ImageView imageView;
    private int personId;
    private View personWrapper;
    private ProgressBar progressBar;
    private Person person;
    private NonSwipeableViewPager mPager;

    public static Fragment newInstance(int personId) {
        ImageFragment imageFragment = new ImageFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.PERSON_ID, personId);
        imageFragment.setArguments(bundle);
        return imageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            personId = getArguments().getInt(Constants.PERSON_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_image, container, false);
        person = getPersonById(personId);
        if (person != null) {
            UserChoice userChoice = getUserChoice(person);

            if (person.getStatus().equals(userChoice.getUserChoice())) {
                Log.e(Constants.TAG, person.getLocation());
            }
            mPager = (NonSwipeableViewPager) getActivity().findViewById(R.id.imagePager);

            rootView.findViewById(R.id.likeButton).setOnClickListener(this);
            rootView.findViewById(R.id.dislikeButton).setOnClickListener(this);
            imageView = (ImageView) rootView.findViewById(R.id.imageView);
            personWrapper = rootView.findViewById(R.id.personWrapper);
            progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
            if (person.getStatus().equals(Constants.LIKE_STATUS)) {
                rootView.findViewById(R.id.heartImage).setVisibility(View.VISIBLE);
            }
            new ImageTask().execute(person.getPhoto());
        }
        return rootView;
    }

    @Override
    public void onClick(View v) {
        UserChoice userChoice = new UserChoice();
        userChoice.setPerson(person);
        switch (v.getId()) {
            case R.id.likeButton:
                userChoice.setUserChoice(Constants.LIKE_STATUS);
                break;
            case R.id.dislikeButton:
                userChoice.setUserChoice(Constants.DISLIKE_STATUS);
                break;
        }
        addUserChoice(userChoice);

        if (userChoice.getUserChoice().equals(person.getStatus()) && person.getStatus().equals(Constants.LIKE_STATUS)) {
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.item_detail_container, MatchFragment.newInstance(person.getId()), Constants.CURRENT_FRAGMENT).commit();
        } else {
            removePerson(person);
            //update list count
            saveListCount(getPersonCount());

            int currentItem = mPager.getCurrentItem();
            if (currentItem < mPager.getAdapter().getCount() - 1) {
                mPager.setCurrentItem(mPager.getCurrentItem() + 1);
                mPager.getAdapter().notifyDataSetChanged();
            } else {
                ScreenSlidePagerAdapter adapter = new ScreenSlidePagerAdapter(getActivity(),  getActivity().getSupportFragmentManager(), getPersonList());
                mPager.setAdapter(adapter);
            }
        }
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
