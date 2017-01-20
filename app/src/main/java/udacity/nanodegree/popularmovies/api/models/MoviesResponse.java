package udacity.nanodegree.popularmovies.api.models;

import com.squareup.moshi.Json;

import java.util.List;

import udacity.nanodegree.popularmovies.model.Movie;

/**
 * {
 * .."page": 1,
 * .."results": [
 * ....{
 * ......"poster_path": "/z6BP8yLwck8mN9dtdYKkZ4XGa3D.jpg",
 * ......"adult": false,
 * ......"overview": "A big screen remake of John ... thieves.",
 * ......"release_date": "2016-09-14",
 * ......"genre_ids": [
 * ........28,
 * ........12,
 * ........37
 * ......],
 * ......"id": 333484,
 * ......"original_title": "The Magnificent Seven",
 * ......"original_language": "en",
 * ......"title": "The Magnificent Seven",
 * ......"backdrop_path": "/g54J9MnNLe7WJYVIvdWTeTIygAH.jpg",
 * ......"popularity": 38.19583,
 * ......"vote_count": 298,
 * ......"video": false,
 * ......"vote_average": 4.67
 * ....}
 * ..],
 * .."total_results": 19648,
 * .."total_pages": 983
 * }
 */
public class MoviesResponse {

    @Json(name = "page") public          int         page;
    @Json(name = "total_results") public int         totalResults;
    @Json(name = "total_pages") public   int         totalPages;
    @Json(name = "results") public       List<Movie> results;
}
