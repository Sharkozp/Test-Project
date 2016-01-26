package com.sharkozp.testproject.fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.sharkozp.testproject.system.Constants;
import com.sharkozp.testproject.system.database.DatabaseHelper;
import com.sharkozp.testproject.system.database.Person;
import com.sharkozp.testproject.system.database.UserChoice;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by oleksandr on 1/24/16.
 */
public class MainFragment extends Fragment {
    private DatabaseHelper databaseHelper = null;
    private Context context;

    public int getPersonCount() {
        int count = 0;
        try {
            count = (int) getHelper().getPersonDao().countOf();
        } catch (SQLException e) {
            Log.e(Constants.TAG, e.getMessage());
        }
        return count;
    }

    public void saveListCount(int count) {
        if (context != null) {
            SharedPreferences settings = context.getSharedPreferences(Constants.PREFS_NAME, 0);
            int previousCount = settings.getInt(Constants.LIST_COUNT, 0);
            settings.edit().putInt(Constants.LIST_COUNT, previousCount + count).apply();
        }
    }

    public UserChoice getUserChoice(Person person) {
        UserChoice userChoice = new UserChoice();
        if (person != null) {
            try {
                QueryBuilder<UserChoice, Integer> userChoiceQb = getHelper().getUserChoicesDao().queryBuilder();
                userChoiceQb.where().eq("person_id", person.getId());
                userChoice = userChoiceQb.queryForFirst();
            } catch (SQLException e) {
                Log.e(Constants.TAG, e.getMessage());
            }
            if (userChoice == null) {
                userChoice = new UserChoice();
            }
        }
        return userChoice;
    }

    public void removePerson(Person person) {
        try {
            getHelper().getPersonDao().delete(person);
        } catch (SQLException e) {
            Log.e(Constants.TAG, e.getMessage());
        }
    }

    public void addUserChoice(UserChoice userChoice) {
        try {
            getHelper().getUserChoicesDao().createOrUpdate(userChoice);
        } catch (SQLException e) {
            Log.e(Constants.TAG, e.getMessage());
        }
    }

    public void savePersons(List<Person> personList) {
        try {
            Dao<Person, Integer> personDao = getHelper().getPersonDao();
            for (Person person : personList) {
                personDao.createOrUpdate(person);
            }
        } catch (SQLException e) {
            Log.e(Constants.TAG, e.getMessage());
        }
    }

    public List<Person> getPersonList() {
        List<Person> personList = new ArrayList<>();
        try {
            personList = getHelper().getPersonDao().queryForAll();
        } catch (SQLException e) {
            Log.e(Constants.TAG, e.getMessage());
        }
        return personList;
    }

    public Person getPersonById(int id) {
        Person person = null;
        try {
            person = getHelper().getPersonDao().queryForId(id);
        } catch (SQLException e) {
            Log.e(Constants.TAG, e.getMessage());
        }
        return person;
    }

    protected DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(getActivity(), DatabaseHelper.class);
        }
        return databaseHelper;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
    }
}
