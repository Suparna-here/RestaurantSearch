package com.udacity.zomato.ui.list;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.udacity.zomato.R;
import com.udacity.zomato.data.database.Restaurant;
import com.udacity.zomato.data.network.ServiceGenerator;
import com.udacity.zomato.ui.detail.DetailActivity;
import com.udacity.zomato.ui.detail.DetailFragment;
import com.udacity.zomato.utils.gps.GPSUtils;

import java.util.ArrayList;



public class MainActivity extends AppCompatActivity implements MasterListFragment.OnResturantClickListener, GPSUtils.onGpsListener {
    private boolean isGPS = false;
    private GPSUtils mGpsUtils;
    // A single-pane display refers to phone screens, and two-pane to larger tablet screens
    private boolean isTwoPane = false;

    public static final int GPS_REQUEST = 1001;
    public static final String TAG = MainActivity.class.getSimpleName();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurants);
        mGpsUtils = new GPSUtils(this);

        if (savedInstanceState == null) {
            if (ServiceGenerator.LOCAL_LOGD)
                Log.d(TAG, "su: savedInstanceState == null create Fragment");
            FragmentManager fragmentManager = getSupportFragmentManager();
            MasterListFragment masterListFragment = (MasterListFragment) fragmentManager.findFragmentById(R.id.master_list_fragment);
        }

        // Also, for the two-pane display
        if (findViewById(R.id.fragment_detail_container) != null) {
            isTwoPane = true;
        }
    }

    public void turnOnGPS() {
        Log.d(TAG, "su: turnOnGPS mGpsUtils" + mGpsUtils);
        if (mGpsUtils != null)
            mGpsUtils.turnGPSOn(this);
    }

    @Override
    protected void onResume() {
        turnOnGPS();
        super.onResume();
    }

    @Override
    public void onResturantCardSelected(Restaurant resturant, String sort_by) {
        ArrayList<Restaurant> resturantList = new ArrayList<Restaurant>();
        resturantList.add(resturant);
        if (ServiceGenerator.LOCAL_LOGD)
            Log.d(TAG, "su: itemClickListener " + resturant.getId());
        Bundle bundle = new Bundle();
        bundle.putBoolean(ServiceGenerator.EXTRA_TWO_PANE, isTwoPane);
        bundle.putString(ServiceGenerator.EXTRA_SORT_BY, sort_by);
        bundle.putParcelableArrayList(ServiceGenerator.EXTRA_DATA, resturantList);
        if (!isTwoPane) {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra(ServiceGenerator.EXTRA_BUNDLE, bundle);
            startActivity(intent);
        } else {
            // Retrieve list index values that were sent through an intent; use them to display the desired Resturant details
            // Use setResturant(Resturant resturant) to set the Resturant
            // Create a new detailFragment
            DetailFragment detailFragment = new DetailFragment();
            detailFragment.setArguments(bundle);
            // Add the fragment to its container using a FragmentManager and a Transaction
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_detail_container, detailFragment, ServiceGenerator.FRAGMENT_TAG)
                    .commit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GPS_REQUEST) {
                isGPS = true; // flag maintain before get location
            }
        }
    }

    @Override
    public void gpsStatus(boolean isGPSEnable) {
        // turn on GPS
        isGPS = isGPSEnable;
    }
}
