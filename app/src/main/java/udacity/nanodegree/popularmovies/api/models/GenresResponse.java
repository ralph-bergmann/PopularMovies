package udacity.nanodegree.popularmovies.api.models;

import com.squareup.moshi.Json;

import java.util.List;

import udacity.nanodegree.popularmovies.model.Genre;

public class GenresResponse {


    @Json(name = "genres") public List<Genre> genres;
}
