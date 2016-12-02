package udacity.nanodegree.popularmovies.api;

import android.support.annotation.IntRange;

import io.reactivex.Flowable;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;
import udacity.nanodegree.popularmovies.api.models.ConfigurationResponse;
import udacity.nanodegree.popularmovies.api.models.GenresResponse;
import udacity.nanodegree.popularmovies.api.models.MoviesResponse;

public interface TMDbApi {

    @GET("genre/movie/list")
    Flowable<Response<GenresResponse>> genres();

    @GET("movie/popular")
    Flowable<Response<MoviesResponse>> popular(@IntRange(from = 1, to = 1000) @Query("page") int page);

    @GET("movie/top_rated")
    Flowable<Response<MoviesResponse>> toprated(@IntRange(from = 1, to = 1000) @Query("page") int page);

    @GET("configuration")
    Flowable<Response<ConfigurationResponse>> configuration();
}
