package com.udacity.zomato.data.database;

import android.os.Parcel;
import android.os.Parcelable;

public class RestaurantLocation implements Parcelable {
    /* "address":"32 Spring Street, New York 10012",
       "locality":"Spring Street, Lower East Side",
       "city":"New York City",
       "city_id":280,
       "latitude":"40.7215730000",
       "longitude":"-73.9956350000",
       "zipcode":"10012",
       "country_id":216,
       "locality_verbose":"Spring Street, Lower East Side, New York City"*/
    private String address;
    private String locality;
    private String city;
    private String city_id;
    private String latitude;
    private String longitude;
    private String zipcode;
    private String country_id;
    private String locality_verbose;


    public RestaurantLocation(Parcel in) {
        address = in.readString();
        locality = in.readString();
        city = in.readString();
        city_id = in.readString();
        latitude = in.readString();
        longitude = in.readString();
        zipcode = in.readString();
        country_id = in.readString();
        locality_verbose = in.readString();

//        List<Integer> genre_ids
//        genre_ids = (ArrayList<Integer>) in.readSerializable();
    }

    @Override
    public void writeToParcel(Parcel destParcel, int flags) {
        destParcel.writeString(address);
        destParcel.writeString(locality);
        destParcel.writeString(city);
        destParcel.writeString(city_id);
        destParcel.writeString(latitude);
        destParcel.writeString(longitude);

        destParcel.writeString(zipcode);
        destParcel.writeString(country_id);
        destParcel.writeString(locality_verbose);

       /* destParcel.writeSerializable(genre_ids);*/
    }

    @Override
    public int describeContents() {
        return 0;
    }
    public static final Creator<RestaurantLocation> CREATOR = new Creator<RestaurantLocation>() {
        @Override
        public RestaurantLocation createFromParcel(Parcel in) {
            return new RestaurantLocation(in);
        }

        @Override
        public RestaurantLocation[] newArray(int size) {
            return new RestaurantLocation[size];
        }
    };

    public String getLocality() {
        return locality;
    }

    public String getCity() {
        return city;
    }

    public String getCity_id() {
        return city_id;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getZipcode() {
        return zipcode;
    }

    public String getCountry_id() {
        return country_id;
    }

    public String getLocality_verbose() {
        return locality_verbose;
    }

    public String getAddress() {
        return address;
    }
}
