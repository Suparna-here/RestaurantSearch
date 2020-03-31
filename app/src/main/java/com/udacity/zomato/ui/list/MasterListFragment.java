package com.udacity.zomato.ui.list;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.udacity.zomato.R;
import com.udacity.zomato.data.database.Restaurant;
import com.udacity.zomato.data.network.ServiceGenerator;
import com.udacity.zomato.ui.detail.DetailFragment;
import com.udacity.zomato.utils.InjectorUtils;

import java.util.List;

import static androidx.constraintlayout.widget.Constraints.TAG;


public class MasterListFragment extends Fragment implements RestaurantDBAdapter.RestaurantDBAdapterOnClickHandler {
    private RecyclerView mRecyclerView;
    private RestaurantDBAdapter mRestaurantDBAdapter;
    private MainActivityViewModel mViewModel;
    private TextView mErrorMessageDisplay;
    private TextView mMovieFavouriteErrorMessageDisplay;
    private Context context;


    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private boolean mTrackingLocation;

    public static final int REQUEST_LOCATION_PERMISSION = 1;//>0
    private static final String TRACKING_LOCATION_KEY = "tracking_location";
    public static final String LOG_TAG = MasterListFragment.class.getSimpleName();

    // Define a new interface OnResturantClickListener that triggers a callback in the host activity
    private OnResturantClickListener mCallback;

    // OnResturantClickListener interface, calls a method in the host activity named onResturantCardSelected
    public interface OnResturantClickListener {
        void onResturantCardSelected(Restaurant restaurant, String sort_by);
    }

    // Override onAttach to make sure that the container activity has implemented the callback
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        // This makes sure that the host activity has implemented the callback interface
        // If not, it throws an exception
        try {
            mCallback = (OnResturantClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnResturantClickListener");
        }
    }


    // Mandatory empty constructor
    public MasterListFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        if (savedInstanceState != null) {
            mTrackingLocation = savedInstanceState.getBoolean(TRACKING_LOCATION_KEY);
        }

        final View rootView = inflater.inflate(R.layout.fragment_master_list, container, false);
        //Toolbar
        final Toolbar toolbar = rootView.findViewById(R.id.toolbar);
        ((MainActivity) context).setSupportActionBar(toolbar);

        setHasOptionsMenu(true);

        mRecyclerView =rootView.findViewById(R.id.recyclerview_restaurants);
        //This TextView is used to display errors and will be hidden if there are no errors
        mErrorMessageDisplay =rootView.findViewById(R.id.tv_restaurant_error_message_display);
        mMovieFavouriteErrorMessageDisplay =rootView.findViewById(R.id.tv_restaurant_favourite_error_message_display);
        final int SPAN_COUNT = getResources().getInteger(R.integer.gallery_columns);
        /*
         * GridLayoutManager for GridView.
         * SPAN_COUNT parameter defines number of columns.
         */
        GridLayoutManager layoutManager
                = new GridLayoutManager(getActivity(), SPAN_COUNT);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        // Pass in 'this' as the RestaurantDBAdapterOnClickHandler
        /*
         * The RestaurantDBAdapter is responsible for linking restaurant data with the Views that
         * will end up displaying our restaurant data.
         */
        mRestaurantDBAdapter = new RestaurantDBAdapter(context,this);

        /* Setting the adapter attaches it to the RecyclerView in our layout. */
        mRecyclerView.setAdapter(mRestaurantDBAdapter);

        MainViewModelFactory mainViewModelFactory = InjectorUtils.provideMainActivityViewModelFactory(getActivity().getApplicationContext());
        mViewModel = ViewModelProviders.of(this, mainViewModelFactory).get(MainActivityViewModel.class);

        if (ServiceGenerator.LOCAL_LOGD)
            Log.d(LOG_TAG, "su: sort By " + mViewModel.getSort_by() + " Initialize mLocationCallback");
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (ServiceGenerator.LOCAL_LOGD)
                    Log.d(LOG_TAG, "su: mTrackingLocation " + mTrackingLocation + ", mViewModel.getSort_by() " + mViewModel.getSort_by());

                // If tracking is turned on, reverse geocode into an address
                if (mTrackingLocation && mViewModel.getSort_by().equals(ServiceGenerator.ORDER_CURRENT_LOCATION)) {
                    Location location = locationResult.getLastLocation();
                    mViewModel.setMoviesBasedOnLocationOrderInViewModel(Double.toString(location.getLatitude()), Double.toString(location.getLongitude()), mViewModel.getSort_by());
                    if (ServiceGenerator.LOCAL_LOGD)
                        Log.d(LOG_TAG, "su: mViewModel.getSort_by() " + mViewModel.getSort_by() + " loadResturantDataInViewModel()");
                }
            }
        };
        if (savedInstanceState == null) {
//            sort_by = ServiceGenerator.ORDER_POPULARITY;
            if (ServiceGenerator.LOCAL_LOGD)
                Log.d(LOG_TAG, "su: savedInstanceState == null & sort By " + mViewModel.getSort_by() + " mViewModel.setMoviesBasedOnSortOrderInViewModel(sort_by)");
//          Once all of our views are setup, we can load the restaurant data.
            setMoviesBasedOnSortOrderInFragment(mViewModel.getSort_by());
        }
        if (ServiceGenerator.LOCAL_LOGD)
            Log.d(LOG_TAG, "su: mViewModel.getSort_by() " + mViewModel.getSort_by() + " loadResturantDataInViewModel()");
        loadResturantDataInViewModel();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mTrackingLocation && (mViewModel.getSort_by().equals(ServiceGenerator.ORDER_CURRENT_LOCATION))) {
            if (ServiceGenerator.LOCAL_LOGD)
                Log.d(LOG_TAG, "su: onResume" + " startTrackingLocation");
            startTrackingLocation();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mTrackingLocation) {
            if (ServiceGenerator.LOCAL_LOGD)
                Log.d(LOG_TAG, "su: onPause" + " stopTrackingLocation");
            stopTrackingLocation();
        }
        mTrackingLocation = true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(TRACKING_LOCATION_KEY, mTrackingLocation);
        super.onSaveInstanceState(outState);
    }

    public void setMoviesBasedOnSortOrderInFragment(final String sort_by) {
        if (ServiceGenerator.LOCAL_LOGD)
            Log.d(LOG_TAG, "su: sort By " + sort_by + " setMoviesBasedOnSortOrderInFragment");

        if (sort_by.equals(ServiceGenerator.ORDER_CURRENT_LOCATION)) {
            if (ServiceGenerator.LOCAL_LOGD)
                Log.d(LOG_TAG, "su: sort By " + sort_by + " startTrackingLocation " +"setMoviesBasedOnSortOrderInFragment");
            mViewModel.setSort_by(sort_by);
            startTrackingLocation();
        } else if (sort_by.equals(ServiceGenerator.ORDER_FAVOURITE)) {
            if (ServiceGenerator.LOCAL_LOGD)
                Log.d(LOG_TAG, "su: sort By " + sort_by + " stopTrackingLocation");
            mViewModel.setSort_by(sort_by);
            stopTrackingLocation();
            mViewModel.setMoviesBasedOnFavouriteOrderInViewModel(sort_by);

        }
    }

    /**
     * Method that stops tracking the device. It removes the location
     * updates, stops the animation and reset the UI.
     */
    private void stopTrackingLocation() {
        if (mTrackingLocation) {
            mTrackingLocation = false;
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }

    public void startTrackingLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {
            Log.d(TAG, "su: getLocationpermissions granted");
            mFusedLocationClient.requestLocationUpdates
                    (getLocationRequest(), mLocationCallback,
                            null /* Looper */);
        }
        mTrackingLocation = true;
    }

    private LocationRequest getLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(ServiceGenerator.LOCATION_UPDATE_INTERVAL);
        locationRequest.setFastestInterval(ServiceGenerator.FASTEST_LOCATION_UPDATE_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    private void loadResturantDataInViewModel() {
        LiveData<List<Restaurant>> restaurantList = mViewModel.getMoviesList();
        restaurantList.observe(this, new Observer<List<Restaurant>>() {
            @Override
            public void onChanged(@Nullable List<Restaurant> resturantList) {
                if (ServiceGenerator.LOCAL_LOGD)
                    Log.d(LOG_TAG, "su: swap Movies sort_by " + mViewModel.getSort_by());
                mRestaurantDBAdapter.setResturantData(resturantList, mViewModel.getSort_by());
                // Show the restaurant list or the loading screen based on whether the restaurant data exists
                // and is loaded
                if (resturantList != null && resturantList.size() != 0) {
                    if (ServiceGenerator.LOCAL_LOGD)
                        Log.d(LOG_TAG, "mRecyclerView mPosition" + " size=" + resturantList.size());
                    showResturantDataView();
                } else {
                    if (ServiceGenerator.LOCAL_LOGD)
                        Log.d(LOG_TAG, "showErrorMessage");
                    showErrorMessage();
                }
            }
        });
    }

    /**
     * This method will make the View for the restaurant data visible and
     * hide the error message.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    private void showResturantDataView() {
        // First, make sure the error is invisible
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mMovieFavouriteErrorMessageDisplay.setVisibility(View.INVISIBLE);
        // Then, make sure the restaurant data is visible
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * This method will make the internet connection error message visible and hide the ResturantResponse
     * View.
     */
    private void showErrorMessage() {
        if (mViewModel.getSort_by().equals(ServiceGenerator.ORDER_CURRENT_LOCATION) || mViewModel.getSort_by().equals(ServiceGenerator.ORDER_TOPRATED)) {
            //First, hide the currently visible data
            if (ServiceGenerator.LOCAL_LOGD)
                Log.d(LOG_TAG, "su: mErrorMessageDisplay " + mViewModel.getSort_by());
            mRecyclerView.setVisibility(View.INVISIBLE);
            mMovieFavouriteErrorMessageDisplay.setVisibility(View.INVISIBLE);
            // Then, show the internet error
            mErrorMessageDisplay.setVisibility(View.VISIBLE);
        } else if (mViewModel.getSort_by().equals(ServiceGenerator.ORDER_FAVOURITE)) {
            if (ServiceGenerator.LOCAL_LOGD)
                Log.d(LOG_TAG, "su: mMovieFavouriteErrorMessageDisplay " + mViewModel.getSort_by());
            // First, hide the currently visible data and internet connection error message
            mRecyclerView.setVisibility(View.INVISIBLE);
            mErrorMessageDisplay.setVisibility(View.INVISIBLE);
            // Then, show the Favourite restaurant data error
            mMovieFavouriteErrorMessageDisplay.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:
                // If the permission is granted, get the location,
                // otherwise, show a Toast
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startTrackingLocation();
                } else {
                    Toast.makeText(getActivity(),
                            R.string.location_permission_denied,
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        String sort_by;

        if (id == R.id.action_current_location) {//Show near by Restaurants
            Boolean isTablet = getResources().getBoolean(R.bool.tablet);
            if(isTablet){
                // Add the fragment to its container using a FragmentManager and a Transaction
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                DetailFragment detailFragment=(DetailFragment) fragmentManager.findFragmentByTag(ServiceGenerator.FRAGMENT_TAG);
                if(detailFragment!=null)
                fragmentManager.beginTransaction()
                        .remove(detailFragment)
                        .commit();
            }
            sort_by = ServiceGenerator.ORDER_CURRENT_LOCATION;
            this.setMoviesBasedOnSortOrderInFragment(sort_by);
            return true;
        } else if (id == R.id.action_favourite) {//Show Top Favourite Movies
            boolean isTablet = getResources().getBoolean(R.bool.tablet);
            if(isTablet){
                // Add the fragment to its container using a FragmentManager and a Transaction
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                DetailFragment detailFragment=(DetailFragment) fragmentManager.findFragmentByTag(ServiceGenerator.FRAGMENT_TAG);
                if(detailFragment!=null)
                fragmentManager.beginTransaction()
                        .remove(detailFragment)
                        .commit();
            }
            sort_by = ServiceGenerator.ORDER_FAVOURITE;
            this.setMoviesBasedOnSortOrderInFragment(sort_by);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void itemClickListener(Restaurant restaurant) {
        mCallback.onResturantCardSelected(restaurant, mViewModel.getSort_by());
    }
}
