package udacity.nanodegree.popularmovies.room;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import udacity.nanodegree.popularmovies.model.BackdropSize;
import udacity.nanodegree.popularmovies.model.Genre;
import udacity.nanodegree.popularmovies.model.ImageConfig;
import udacity.nanodegree.popularmovies.model.Movie;
import udacity.nanodegree.popularmovies.model.PosterSize;


@Database(entities = {
                         ImageConfig.class,
                         PosterSize.class,
                         BackdropSize.class,
                         Genre.class,
                         Movie.class
                     },
          version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract ImageConfigurationDao imageConfigurationDao();
    public abstract GenreDao genreDao();
    public abstract MovieDao movieDao();
}
