package com.udacity.zomato.ui.detail;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.udacity.zomato.R;
import com.udacity.zomato.data.database.Restaurant;
import com.udacity.zomato.data.network.ServiceGenerator;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {

    public static final String LOG_TAG = DetailActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ServiceGenerator.LOCAL_LOGD)
            Log.d(LOG_TAG, "su: populateUI");
        Intent intent = getIntent();
        if (intent == null) {
            closeOnError();
        }
        Bundle bundle = intent.getBundleExtra(ServiceGenerator.EXTRA_BUNDLE);
        ArrayList<Restaurant> restaurantArrayList = bundle.getParcelableArrayList(ServiceGenerator.EXTRA_DATA);
        String sort_by=bundle.getString(ServiceGenerator.EXTRA_SORT_BY);
        boolean isTwoPane=bundle.getBoolean(ServiceGenerator.EXTRA_TWO_PANE);

        final Restaurant resturant = restaurantArrayList.get(0);
        if (restaurantArrayList == null) {
            // EXTRA_DATA not found in intent
            closeOnError();
            return;
        }
        if (resturant == null) {
            // Sandwich data unavailable
            closeOnError();
            return;
        }
        setContentView(R.layout.activity_detail);

        // Only create new fragments when there is no previously saved state
        if (savedInstanceState == null) {
            Bundle bundleFragment = new Bundle();
            bundleFragment.putBoolean(ServiceGenerator.EXTRA_TWO_PANE, isTwoPane);
            bundleFragment.putParcelableArrayList(ServiceGenerator.EXTRA_DATA, restaurantArrayList);
            bundleFragment.putString(ServiceGenerator.EXTRA_SORT_BY, sort_by);
            // Retrieve list index values that were sent through an intent; use them to display the desired Restaurant details
            // Use setRestaurant(Resturant restaurant) to set the Restaurant
            // Create a new detailFragment
            DetailFragment detailFragment = new DetailFragment();
            detailFragment.setArguments(bundleFragment);
            // Add the fragment to its container using a FragmentManager and a Transaction
            FragmentManager fragmentManager = getSupportFragmentManager();

            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_detail_container, detailFragment)
                    .commit();
        }
    }

    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.detail_error_message, Toast.LENGTH_SHORT).show();
    }



   /* @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }*/
}