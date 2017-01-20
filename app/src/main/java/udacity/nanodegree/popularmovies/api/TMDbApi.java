package udacity.nanodegree.popularmovies.api;

import android.support.annotation.IntRange;

import io.reactivex.Flowable;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;
import udacity.nanodegree.popularmovies.api.models.ConfigResponse;
import udacity.nanodegree.popularmovies.api.models.GenresResponse;
import udacity.nanodegree.popularmovies.api.models.MoviesResponse;

public interface TMDbApi {

    @GET("configuration")
    @Headers("Accept: application/json")
    Call<ConfigResponse> configuration();

    @GET("genre/movie/list")
    @Headers("Accept: application/json")
    Call<GenresResponse> genres();

    @GET("movie/popular?page=1")
    @Headers("Accept: application/json")
    Call<MoviesResponse> popular();

    @GET("movie/popular")
    @Headers("Accept: application/json")
    Flowable<Response<MoviesResponse>> popular(@IntRange(from = 1, to = 1000) @Query("page") int page);

    @GET("movie/popular")
    @Headers("Accept: application/json")
    Call<MoviesResponse> popular2(@IntRange(from = 1, to = 1000) @Query("page") int page);

    @GET("movie/top_rated")
    @Headers("Accept: application/json")
    Flowable<Response<MoviesResponse>> toprated(@IntRange(from = 1, to = 1000) @Query("page") int page);
}
