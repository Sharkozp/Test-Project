package com.sharkozp.testproject.system;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.ViewGroup;

import com.sharkozp.testproject.R;
import com.sharkozp.testproject.fragment.GenerateFragment;
import com.sharkozp.testproject.fragment.ImageFragment;
import com.sharkozp.testproject.system.database.Person;

import java.util.List;

/**
 * Created by oleksandr on 1/26/16.
 */
public class ScreenSlidePagerAdapter extends FragmentPagerAdapter {
    private int count;
    private FragmentActivity activity;
    private List<Person> personList;

    public ScreenSlidePagerAdapter(FragmentActivity activity, FragmentManager fm, List<Person> personList) {
        super(fm);
        this.activity = activity;
        this.personList = personList;
        this.count = personList.size();
    }

    @Override
    public Fragment getItem(int position) {
        int personId = personList.get(position).getId();
        return ImageFragment.newInstance(personId);
    }

    @Override
    public int getCount() {
        int count = personList.size();
        if (count == 0) {
            activity.getSupportFragmentManager().beginTransaction().replace(R.id.item_detail_container, GenerateFragment.newInstance(), Constants.CURRENT_FRAGMENT).commit();
        }
        return count;
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
        Fragment fragment = (Fragment) object;
        FragmentManager manager = fragment.getFragmentManager();
        if (position >= getCount()) {
            manager.beginTransaction().remove(fragment).commit();
        }
    }

    public void swapList(List<Person> personList) {
        this.personList = personList;
        notifyDataSetChanged();
    }
}