package com.udacity.zomato.ui.detail;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import com.udacity.zomato.R;
import com.udacity.zomato.data.database.Restaurant;
import com.udacity.zomato.data.database.RestaurantLocation;
import com.udacity.zomato.data.network.ServiceGenerator;
import com.udacity.zomato.utils.InjectorUtils;
import com.udacity.zomato.widget.FavoriteRestaurantIntentService;


public class DetailFragment extends Fragment {
    private boolean isFavouriteMovie;
    private ImageView restaurantPosterIV;
    private FloatingActionButton favouriteButton;

    private DetailActivityViewModel mViewModel;
    private Context context;

    private String restaurantAddressString;
    private String sort_by;

    private String restaurantLatLng;
    private String restaurantAddress;
    private String restaurantRating;
    private String restaurantVotes;



    public static final String LOG_TAG = DetailFragment.class.getSimpleName();

    public DetailFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        this.context = getActivity();
        MobileAds.initialize(context, getResources().getString(R.string.admob_Id));
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();

        if (bundle == null)
            return null;

        boolean isTwoPane = bundle.getBoolean(ServiceGenerator.EXTRA_TWO_PANE);
        sort_by = bundle.getString(ServiceGenerator.EXTRA_SORT_BY);
        Restaurant restaurant = (Restaurant) bundle.getParcelableArrayList(ServiceGenerator.EXTRA_DATA).get(0);
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        //Toolbar
        final Toolbar toolbar = rootView.findViewById(R.id.toolbar);
        CollapsingToolbarLayout collapsingToolbar = rootView.findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(restaurant.getName());
        if(!isTwoPane) {
            ((DetailActivity) context).setSupportActionBar(toolbar);
            ((DetailActivity) context).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Loading Ads
        AdView mAdView = rootView.findViewById(R.id.restaurant_adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        favouriteButton = rootView.findViewById(R.id.iv_favourite_button);
        restaurantPosterIV = rootView.findViewById(R.id.iv_restaurant_detail_thumbnail);
        DetailViewModelFactory detailViewModelFactory = InjectorUtils.provideDetailViewModelFactory(getActivity().getApplicationContext(), restaurant);
        mViewModel = ViewModelProviders.of(this, detailViewModelFactory).get(DetailActivityViewModel.class);
        MutableLiveData<Restaurant> favouriteMovie = mViewModel.getFavouriteMovie();

        favouriteMovie.observe(getViewLifecycleOwner(), new Observer<Restaurant>() {
            @Override
            public void onChanged(@Nullable Restaurant restaurant) {
                // If entry removed or added, update the UI

//                isFavouriteMovie  !isFavouriteMovie
                if (restaurant != null) {
                    if (ServiceGenerator.LOCAL_LOGD)
                        Log.d(LOG_TAG, "su: mViewModel.isFavouriteMovie(true)");
                    mViewModel.setFavouriteFlag(true);
                    favouriteButton.setImageResource(R.drawable.ic_favorite_red_34dp);
                } else {
                    if (ServiceGenerator.LOCAL_LOGD)
                        Log.d(LOG_TAG, "su: NOT mViewModel.isFavouriteMovie(false)");
                    mViewModel.setFavouriteFlag(false);
                    favouriteButton.setImageResource(R.drawable.ic_favorite_border_black_34dp);
                }
                isFavouriteMovie = mViewModel.isFavouriteFlag();
            }
        });

        favouriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFavouriteMovie) {
                    //Remove from favourite_movie Table
                    mViewModel.deleteMovieFromFavouriteTable(mViewModel.getRestaurant());
                    if (ServiceGenerator.LOCAL_LOGD)
                        Log.d(LOG_TAG, "su: deleteRestaurantFromFavourite()");
                } else if (!isFavouriteMovie) {
                    //Add to favourite_movie Table
                    Restaurant restaurantDB = new Restaurant(mViewModel.getRestaurant().getId(), mViewModel.getRestaurant().getName(), mViewModel.getRestaurant().getUrl(), mViewModel.getRestaurant().getCuisines(),
                            mViewModel.getRestaurant().getAverage_cost_for_two(), mViewModel.getRestaurant().getThumb(), mViewModel.getRestaurant().getFeatured_image(), restaurantAddress,
                            restaurantRating, restaurantVotes, restaurantLatLng);
                    mViewModel.insertMovieToFavouriteTable(restaurantDB);
                    if (ServiceGenerator.LOCAL_LOGD)
                        Log.d(LOG_TAG, "su: insertRestaurantToFavourite()");
                }
                if (ServiceGenerator.LOCAL_LOGD)
                    Log.d(LOG_TAG, "su: Widget startActionUpdateFavoriteRestaurant()");
                FavoriteRestaurantIntentService.startActionUpdateFavoriteRestaurant(context);
            }
        });
        populateUI(rootView, mViewModel.getRestaurant());
        Button detailButton=rootView.findViewById(R.id.btn_detail);
        Button directionButton=rootView.findViewById(R.id.btn_direction);
        detailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = mViewModel.getRestaurant().getUrl();
                if(url.isEmpty()) {
                    Toast.makeText(context, context.getResources().getString(R.string.noMoreDetailsToast), Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });

        directionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(context.getResources().getString(R.string.googleMapsIntentURL) + restaurantLatLng));
                startActivity(intent);
            }
        });
        return rootView;
    }

    //Populate UI with Restaurant details
    private void populateUI(View rootView, final Restaurant restaurant) {
        String posterUrl = restaurant.getFeatured_image();
        Glide.with(context)
                .load(posterUrl)
                .into(restaurantPosterIV);
        /*Picasso.get()
                .load(posterUrl)
                .placeholder(R.mipmap.ic_launcher)
                .into(restaurantPosterIV);*/


        TextView restaurant_address_tv = rootView.findViewById(R.id.tv_restaurant_detail_address);
        TextView restaurant_cuisine_tv = rootView.findViewById(R.id.tv_restaurant_detail_cuisine);
        TextView restaurant_avg_cost_tv = rootView.findViewById(R.id.tv_restaurant_detail_average_cost);

        TextView restaurant_rating_tv = rootView.findViewById(R.id.tv_restaurant_detail_rating);
        TextView restaurant_votes_tv = rootView.findViewById(R.id.tv_restaurant_detail_votes);

        if (sort_by.equals(ServiceGenerator.ORDER_FAVOURITE)) {

            if (TextUtils.isEmpty(restaurant.getRestaurant_full_address())) {
                restaurant_address_tv.setText(getString(R.string.unknown_text));
                restaurantAddress = getString(R.string.unknown_text);
            }else {
                restaurant_address_tv.setText(context.getResources().getString(R.string.addressLabel) + restaurant.getRestaurant_full_address());
                restaurantAddress = restaurant.getRestaurant_full_address();
            }

            if (TextUtils.isEmpty(restaurant.getCuisines()))
                restaurant_cuisine_tv.setText(getString(R.string.unknown_text));
            else
                restaurant_cuisine_tv.setText(context.getResources().getString(R.string.cuisinesLabel) + restaurant.getCuisines());

            if (TextUtils.isEmpty(restaurant.getAverage_cost_for_two()))
                restaurant_avg_cost_tv.setText(getString(R.string.unknown_text));
            else
                restaurant_avg_cost_tv.setText(context.getResources().getString(R.string.averageCostForTwoLabel) + restaurant.getAverage_cost_for_two());


            if (TextUtils.isEmpty(restaurant.getRestaurant_aggregate_rating())) {
                restaurant_rating_tv.setText(getString(R.string.unknown_text));
                restaurantRating = getString(R.string.unknown_text);
            } else {
                restaurant_rating_tv.setText(context.getResources().getString(R.string.userRatingLabel) + restaurant.getRestaurant_aggregate_rating());
                restaurantRating = restaurant.getRestaurant_aggregate_rating();
            }

            if (TextUtils.isEmpty(restaurant.getRestaurant_votes())) {
                restaurant_votes_tv.setText(getString(R.string.unknown_text));
                restaurantVotes = getString(R.string.unknown_text);
            }else {
                restaurant_votes_tv.setText(context.getResources().getString(R.string.votesLabel1) + restaurant.getRestaurant_votes());
                restaurantVotes = restaurant.getRestaurant_votes();
            }
            restaurantLatLng = restaurant.getRestaurant_latLng();

            return;
        }

        RestaurantLocation location = restaurant.getLocation();
        restaurantAddressString = "";

        if (location != null) {
            if (!location.getAddress().isEmpty()) {
                restaurantAddressString += location.getAddress();
            }
            if (!location.getLocality().isEmpty()) {
                if (!restaurantAddressString.isEmpty()) {
                    restaurantAddressString += context.getResources().getString(R.string.comma);
                }
                restaurantAddressString += location.getLocality();
            }
            if (!location.getCity().isEmpty()) {
                if (!restaurantAddressString.isEmpty()) {
                    restaurantAddressString += context.getResources().getString(R.string.comma);
                }
                restaurantAddressString += location.getCity();
            }
            if (!location.getZipcode().isEmpty()) {
                if (!restaurantAddressString.isEmpty()) {
                    restaurantAddressString += context.getResources().getString(R.string.hyphen);
                }
                restaurantAddressString += location.getZipcode();
            }
            if (!location.getLatitude().isEmpty() && !location.getLongitude().isEmpty()) {
                restaurantLatLng = location.getLatitude() + context.getResources().getString(R.string.comma1) + location.getLongitude();
            }
        }

        if (restaurant.getLocation() != null) {
            if (TextUtils.isEmpty(restaurant.getLocation().getAddress())) {
                restaurant_address_tv.setText(getString(R.string.unknown_text));
                restaurantAddress = getString(R.string.unknown_text);
            }else {
                restaurant_address_tv.setText(context.getResources().getString(R.string.addressLabel) + restaurantAddressString);
                restaurantAddress = restaurantAddressString;
            }
        }

        if (TextUtils.isEmpty(restaurant.getCuisines()))
            restaurant_cuisine_tv.setText(getString(R.string.unknown_text));
        else
            restaurant_cuisine_tv.setText(context.getResources().getString(R.string.cuisinesLabel) + restaurant.getCuisines());
        if (TextUtils.isEmpty(restaurant.getAverage_cost_for_two()))
            restaurant_avg_cost_tv.setText(getString(R.string.unknown_text));
        else
            restaurant_avg_cost_tv.setText(context.getResources().getString(R.string.averageCostForTwoLabel) + restaurant.getAverage_cost_for_two());

        if (restaurant.getUser_rating() != null) {
            if (TextUtils.isEmpty(restaurant.getUser_rating().getAggregate_rating())) {
                restaurant_rating_tv.setText(getString(R.string.unknown_text));
                restaurantRating = getString(R.string.unknown_text);
            }else {
                restaurant_rating_tv.setText(context.getResources().getString(R.string.userRatingLabel) + restaurant.getUser_rating().getAggregate_rating());
                restaurantRating =restaurant.getUser_rating().getAggregate_rating();
            }
            if (TextUtils.isEmpty(restaurant.getUser_rating().getVotes())) {
                restaurant_votes_tv.setText(getString(R.string.unknown_text));
                restaurantVotes = getString(R.string.unknown_text);
            } else {
                restaurant_votes_tv.setText(context.getResources().getString(R.string.votesLabel1) + restaurant.getUser_rating().getVotes());
                restaurantVotes = restaurant.getUser_rating().getVotes();
            }
        }
    }
}
