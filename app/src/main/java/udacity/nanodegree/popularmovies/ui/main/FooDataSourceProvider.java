package udacity.nanodegree.popularmovies.ui.main;

import android.arch.paging.LivePagedListProvider;
import android.support.annotation.NonNull;

import udacity.nanodegree.popularmovies.api.TMDbApi;
import udacity.nanodegree.popularmovies.model.Movie;

public class FooDataSourceProvider extends LivePagedListProvider<Integer, Movie> {

    private final FooDataSource dataSource;

    public FooDataSourceProvider(@NonNull final TMDbApi api) {
        dataSource = new FooDataSource(api);
    }

    @Override
    protected FooDataSource createDataSource() {
        return dataSource;
    }
}
