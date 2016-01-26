package com.sharkozp.testproject.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.sharkozp.testproject.system.Constants;
import com.sharkozp.testproject.system.CurrentLocation;
import com.sharkozp.testproject.system.UpdaterService;
import com.sharkozp.testproject.system.database.DatabaseHelper;
import com.sharkozp.testproject.system.database.Person;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends SupportMapFragment implements OnMapReadyCallback {
    private final static int IMAGE_SIZE = 100;
    private DatabaseHelper databaseHelper = null;
    private GoogleMap googleMap;
    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            googleMap.clear();
            new MarkersTask().execute();
        }
    };

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        getMapAsync(this);
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


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        Location currentLocation = new CurrentLocation(getActivity()).getLastKnownLocation();
        if (currentLocation != null) {
            double latitude = currentLocation.getLatitude();
            double longitude = currentLocation.getLongitude();
            CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(longitude, latitude));
            this.googleMap.moveCamera(center);
        }
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
        this.googleMap.animateCamera(zoom);

        new MarkersTask().execute();
    }

    private List<Person> getPersonList() {
        List<Person> personList = new ArrayList<>();
        try {
            personList = getHelper().getPersonDao().queryForAll();
        } catch (SQLException e) {
            Log.e(Constants.TAG, e.getMessage());
        }
        return personList;
    }

    protected DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(getActivity(), DatabaseHelper.class);
        }
        return databaseHelper;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
    }

    private class MarkersTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            List<Person> personList = getPersonList();
            for (Person person : personList) {
                String[] locations = person.getLocation().split(",");
                double latitude = Double.parseDouble(locations[0]);
                double longitude = Double.parseDouble(locations[1]);

                InputStream inputStream = null;
                Bitmap bitmap = null;
                try {
                    URL url = new URL(person.getPhoto());
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

                final MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(new LatLng(latitude, longitude));
                if (bitmap != null) {
                    Bitmap resizedImage = scaleDown(bitmap, IMAGE_SIZE, true);
                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(resizedImage));
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        googleMap.addMarker(markerOptions);
                    }
                });
            }
            return null;
        }
    }

    public static Bitmap scaleDown(Bitmap realImage, float maxImageSize, boolean filter) {
        float ratio = Math.min(maxImageSize / realImage.getWidth(), maxImageSize / realImage.getHeight());
        int width = Math.round(ratio * realImage.getWidth());
        int height = Math.round(ratio * realImage.getHeight());
        return Bitmap.createScaledBitmap(realImage, width, height, filter);
    }
}
