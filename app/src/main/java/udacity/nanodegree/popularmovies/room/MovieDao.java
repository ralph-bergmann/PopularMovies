package udacity.nanodegree.popularmovies.room;

import android.arch.lifecycle.LiveData;
import android.arch.paging.LivePagedListProvider;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import udacity.nanodegree.popularmovies.model.Movie;


@Dao
public interface MovieDao {

    @Query("SELECT * FROM Movie")
    LiveData<List<Movie>> getAll();

    @Query("SELECT * FROM Movie")
    LivePagedListProvider<Integer, Movie> usersByLastName();

    @Insert
    void insertAll(List<Movie> movies);

    @Delete
    void delete(Movie movie);

    @Update
    int update(Movie movie);

    @Delete
    void delete(List<Movie> movies);

    @Query("DELETE FROM Movie")
    void deleteAll();
}
