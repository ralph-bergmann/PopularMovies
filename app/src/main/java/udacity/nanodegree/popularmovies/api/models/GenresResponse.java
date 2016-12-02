package udacity.nanodegree.popularmovies.api.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GenresResponse {


    @SerializedName("genres") public List<Genre> genres;


    public static class Genre {

        @SerializedName("id") public   int    id;
        @SerializedName("name") public String name;
    }
}
