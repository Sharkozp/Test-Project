package com.sharkozp.testproject.system;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.gson.Gson;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.sharkozp.testproject.R;
import com.sharkozp.testproject.system.database.DatabaseHelper;
import com.sharkozp.testproject.system.database.Person;
import com.sharkozp.testproject.system.database.UserChoice;

import org.testpackage.test_sdk.android.testlib.API;
import org.testpackage.test_sdk.android.testlib.services.UpdateService;

import java.sql.SQLException;

/**
 * Created by oleksandr on 1/25/16.
 */
public class UpdaterService extends Service implements UpdateService.UpdateServiceListener {
    private final static int NOTIFICATION_ID = 8955;
    public final static String UPDATE_NOTIFICATION = "com.sharkozp.testproject.system.UpdaterService";
    private DatabaseHelper databaseHelper = null;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        API.INSTANCE.subscribeUpdates(this);
        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onChanges(String jsonPerson) {
        Person person = new Gson().fromJson(jsonPerson, Person.class);
        UserChoice userChoice = getUserChoice(person);
        if (userChoice.getUserChoice().equals(person.getStatus()) && person.getStatus().equals(Constants.LIKE_STATUS)) {
            createNotification(true);
        } else if (person.getStatus().equals(Constants.REMOVED_STATUS)) {
            createNotification(false);
        }
        try {
            getHelper().getPersonDao().createOrUpdate(person);
            publishResults();
        } catch (SQLException e) {
            Log.e(Constants.TAG, e.getMessage());
        }
    }

    public UserChoice getUserChoice(Person person) {
        UserChoice userChoice = new UserChoice();
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
        return userChoice;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        API.INSTANCE.unSubscribeUpdates();
        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
    }

    protected DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return databaseHelper;
    }

    protected void createNotification(boolean like) {
        int messageTextId = (like) ? R.string.notification_like_text : R.string.notification_removed_text;

        String message = getString(messageTextId);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_heart)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(message);

        if (like) {
            mBuilder.setDefaults(Notification.DEFAULT_SOUND)
                    .setDefaults(Notification.DEFAULT_VIBRATE);
        }
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    private void publishResults() {
        Intent intent = new Intent(UPDATE_NOTIFICATION);
        sendBroadcast(intent);
    }
}
