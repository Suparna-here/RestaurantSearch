package com.udacity.zomato.ui.list;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.udacity.zomato.R;
import com.udacity.zomato.data.database.Restaurant;
import com.udacity.zomato.data.database.RestaurantLocation;
import com.udacity.zomato.data.network.ServiceGenerator;

import java.util.List;


/**
 * {@link RestaurantDBAdapter} exposes a list of restaurant details to a
 * {@link RecyclerView}
 */
public class RestaurantDBAdapter extends RecyclerView.Adapter<RestaurantDBAdapter.ResturantDBAdapterViewHolder> {
    private static final int VIEW_TYPE_REGULAR = 0;
    private static final int VIEW_TYPE_FAVOURITE = 1;
    private List<Restaurant> mRestaurantData;
    private String sort_by;
    private Context context;

    private final RestaurantDBAdapterOnClickHandler mClickHandler;

    public interface RestaurantDBAdapterOnClickHandler {
        void itemClickListener(Restaurant restaurant);
    }

    public RestaurantDBAdapter(Context context, RestaurantDBAdapterOnClickHandler mClickHandler) {
        this.context = context;
        this.mClickHandler = mClickHandler;
    }

    /**
     * Cache of the children views for a restaurant grid item.
     */
    public class ResturantDBAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final ImageView posterThumbnailIV;
        public final ImageView favourite_markedIV;
        public final TextView restaurantTV;
        public final TextView restaurant_ratingTV;
        public final TextView restaurant_votesTV;
        public final TextView restaurant_addressTV;

        public ResturantDBAdapterViewHolder(View view) {
            super(view);
            posterThumbnailIV = view.findViewById(R.id.iv_restaurant_thumbnail);
            favourite_markedIV = view.findViewById(R.id.iv_favourite_marked);
            restaurantTV = view.findViewById(R.id.tv_restaurant_name);
            restaurant_ratingTV = view.findViewById(R.id.tv_restaurant_rating);
            restaurant_votesTV = view.findViewById(R.id.tv_restaurant_votes);
            restaurant_addressTV = view.findViewById(R.id.tv_restaurant_address);
            // Call setOnClickListener on the view passed into the constructor (use 'this' as the OnClickListener)
            view.setOnClickListener(this);
        }

        // Override onClick, passing the clicked Resturant's data to mClickHandler via its onClick method
        @Override
        public void onClick(View view) {
            mClickHandler.itemClickListener(mRestaurantData.get(getAdapterPosition()));
        }
    }

    @NonNull
    @Override
    public ResturantDBAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId = getLayoutIdByType(viewType);
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        view.setFocusable(true);
        return new ResturantDBAdapterViewHolder(view);
    }

    /**
     * Returns the the layout id depending on whether the restaurant item is a normal item or the favourite restaurant item.
     *
     * @param viewType
     * @return int
     */
    private int getLayoutIdByType(int viewType) {
        switch (viewType) {

            case VIEW_TYPE_REGULAR: {
                return R.layout.restaurant_list_item;
            }

            case VIEW_TYPE_FAVOURITE: {
                return R.layout.favourite_restaurant_list_item;
            }

            default:
                throw new IllegalArgumentException("Invalid view type, value of " + viewType);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ResturantDBAdapterViewHolder holder, int position) {
        Restaurant restaurant = mRestaurantData.get(position);
        String posteUrl = restaurant.getThumb();
        Glide.with(context)
                .load(posteUrl)
                .into(holder.posterThumbnailIV);

       /* Picasso.get()
                .load(posteUrl)
                .error(R.mipmap.ic_launcher)
                .placeholder(R.mipmap.ic_launcher)
                .into(holder.posterThumbnailIV);*/
        holder.restaurantTV.setText(restaurant.getName());

        if (sort_by.equals(ServiceGenerator.ORDER_FAVOURITE)) {

            if (TextUtils.isEmpty(restaurant.getRestaurant_full_address()))
                holder.restaurant_addressTV.setText(context.getResources().getString(R.string.unknown_text));
            else
                holder.restaurant_addressTV.setText(context.getResources().getString(R.string.addressLabel) + restaurant.getRestaurant_full_address());

            if (TextUtils.isEmpty(restaurant.getRestaurant_aggregate_rating()))
                holder.restaurant_ratingTV.setText(context.getResources().getString(R.string.unknown_text));
            else
                holder.restaurant_ratingTV.setText(context.getResources().getString(R.string.ratingLabel) + restaurant.getRestaurant_aggregate_rating());

            if (TextUtils.isEmpty(restaurant.getRestaurant_votes()))
                holder.restaurant_votesTV.setText(context.getResources().getString(R.string.unknown_text));
            else holder.restaurant_votesTV.setText("Votes: " + restaurant.getRestaurant_votes());

            return;
        }

        RestaurantLocation location = restaurant.getLocation();
        String address = "";
        if (location != null && !location.getAddress().isEmpty()) {
            address += location.getAddress();
        }
        if (location != null && !location.getLocality().isEmpty()) {
            if (!address.isEmpty()) {
                address += context.getResources().getString(R.string.comma);
            }
            address += location.getLocality();
        }
        if (location != null && !location.getCity().isEmpty()) {
            if (!address.isEmpty()) {
                address += context.getResources().getString(R.string.comma);
            }
            address += location.getCity();
        }
        if (location != null && !location.getZipcode().isEmpty()) {
            if (!address.isEmpty()) {
                address += context.getResources().getString(R.string.hyphen);
            }
            address += location.getZipcode();
        }

        if (restaurant.getLocation() != null) {
            if (TextUtils.isEmpty(restaurant.getLocation().getAddress()))
                holder.restaurant_addressTV.setText(context.getResources().getString(R.string.unknown_text));
            else
                holder.restaurant_addressTV.setText(context.getResources().getString(R.string.addressLabel) + address);
        }
        if (restaurant.getUser_rating() != null) {
            if (TextUtils.isEmpty(restaurant.getUser_rating().getAggregate_rating()))
                holder.restaurant_ratingTV.setText(context.getResources().getString(R.string.unknown_text));
            else
                holder.restaurant_ratingTV.setText(context.getResources().getString(R.string.ratingLabel) + restaurant.getUser_rating().getAggregate_rating());

            if (TextUtils.isEmpty(restaurant.getUser_rating().getVotes()))
                holder.restaurant_votesTV.setText(context.getResources().getString(R.string.unknown_text));
            else
                holder.restaurant_votesTV.setText("Votes: " + restaurant.getUser_rating().getVotes());
        }
    }

    @Override
    public int getItemCount() {
        if (null == mRestaurantData) return 0;
        return mRestaurantData.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (sort_by.equals(ServiceGenerator.ORDER_CURRENT_LOCATION) || sort_by.equals(ServiceGenerator.ORDER_TOPRATED))
            return VIEW_TYPE_REGULAR;
        else
            return VIEW_TYPE_FAVOURITE;
    }

    /*
     * This method is used to set the restaurant data on a RestaurantDBAdapter
     * @param resturantData The new restaurant data to be displayed.
     */
    public void setResturantData(List<Restaurant> restaurantData, String sort_by) {
        if (mRestaurantData != null)
            mRestaurantData.clear();
        mRestaurantData = restaurantData;
        this.sort_by = sort_by;
        notifyDataSetChanged();
    }
}
