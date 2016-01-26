package com.sharkozp.testproject.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sharkozp.testproject.R;
import com.sharkozp.testproject.system.NonSwipeableViewPager;
import com.sharkozp.testproject.system.ScreenSlidePagerAdapter;
import com.sharkozp.testproject.system.UpdaterService;

/**
 * Created by oleksandr on 1/24/16.
 */
public class SingleFragment extends MainFragment {
    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // mPagerAdapter.notifyDataSetChanged();
        }
    };
    private ScreenSlidePagerAdapter mPagerAdapter;
    private NonSwipeableViewPager mPager;

    public static SingleFragment newInstance() {
        return new SingleFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_single, container, false);
        initPager(rootView);
        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(receiver, new IntentFilter(UpdaterService.UPDATE_NOTIFICATION));
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(receiver);
    }

    /**
     * Instantiate a ViewPager and a PagerAdapter.
     *
     * @param rootView
     */
    private void initPager(View rootView) {
        mPager = (NonSwipeableViewPager) rootView.findViewById(R.id.imagePager);
        //The pager adapter, which provides the pages to the view pager widget.
        mPagerAdapter = new ScreenSlidePagerAdapter(getActivity(), getChildFragmentManager(), getPersonList());
        mPager.setAdapter(mPagerAdapter);
        mPagerAdapter.notifyDataSetChanged();
    }
}
