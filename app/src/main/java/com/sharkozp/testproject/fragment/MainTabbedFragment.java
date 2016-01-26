package com.sharkozp.testproject.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sharkozp.testproject.R;

/**
 * Created by oleksandr on 1/24/16.
 */
public class MainTabbedFragment extends MainFragment {

    public MainTabbedFragment() {
        // Required empty public constructor
    }

    public static MainTabbedFragment newInstance() {
        return new MainTabbedFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main_tabbed, container, false);
        FragmentTabHost tabHost = (FragmentTabHost) rootView.findViewById(R.id.tabHost);
        tabHost.setup(getActivity(), getChildFragmentManager(), android.R.id.tabcontent);
        tabHost.addTab(tabHost.newTabSpec(getString(R.string.tab_title_0)).setIndicator(getString(R.string.tab_title_0)), SingleFragment.class, null);
        tabHost.addTab(tabHost.newTabSpec(getString(R.string.tab_title_1)).setIndicator(getString(R.string.tab_title_1)), MapFragment.class, null);
        tabHost.setCurrentTab(0);
        return rootView;
    }
}
