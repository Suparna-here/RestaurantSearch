package com.udacity.zomato.data.database;

import android.os.Parcel;
import android.os.Parcelable;

public class User_Rating implements Parcelable {
    /* "user_rating":{
                    "aggregate_rating":"4.9",
                    "rating_text":"Excellent",
                    "rating_color":"3F7E00",
                    "rating_obj":{
                        "title":{
                                 "text":"4.9"
                                },
                       "bg_color":{
                                "type":"lime",
                                "tint":"800"
                               }
                    },
                   "votes":"1442"
        },*/
    private String aggregate_rating;
    private String rating_text;
    private String votes;

    public String getAggregate_rating() {
        return aggregate_rating;
    }

    public String getRating_text() {
        return rating_text;
    }

    public String getVotes() {
        return votes;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public User_Rating(Parcel in) {
        aggregate_rating = in.readString();
        rating_text = in.readString();
        votes = in.readString();

//        List<Integer> genre_ids
//        genre_ids = (ArrayList<Integer>) in.readSerializable();
    }

    @Override
    public void writeToParcel(Parcel destParcel, int flags) {
        destParcel.writeString(aggregate_rating);
        destParcel.writeString(rating_text);
        destParcel.writeString(votes);

       /* destParcel.writeSerializable(genre_ids);*/
    }

    public static final Creator<User_Rating> CREATOR = new Creator<User_Rating>() {
        @Override
        public User_Rating createFromParcel(Parcel in) {
            return new User_Rating(in);
        }

        @Override
        public User_Rating[] newArray(int size) {
            return new User_Rating[size];
        }
    };
}
