package udacity.nanodegree.popularmovies.api.models;

import com.squareup.moshi.Json;

import udacity.nanodegree.popularmovies.model.ImageConfig;

public class ConfigResponse {


    @Json(name = "images") public ImageConfig imageConfig;
}
