package udacity.nanodegree.popularmovies.ui.main;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.PagedList;

import javax.inject.Inject;

import udacity.nanodegree.popularmovies.MovieApp;
import udacity.nanodegree.popularmovies.api.TMDbApi;
import udacity.nanodegree.popularmovies.model.Movie;
import udacity.nanodegree.popularmovies.room.AppDatabase;

import static udacity.nanodegree.popularmovies.ui.main.FooDataSource.PAGE_SIZE;

public class MainActivityViewModel extends ViewModel {

    @Inject AppDatabase                db;
    @Inject TMDbApi                    api;
    private LiveData<PagedList<Movie>> data;

    public MainActivityViewModel() {
        MovieApp.graph().inject(this);
//        data = db.movieDao().getAll();

        final FooDataSourceProvider dataSourceProvider = new FooDataSourceProvider(api);
        data = dataSourceProvider.create(0,
                                         new PagedList.Config.Builder()
                                             .setPageSize(PAGE_SIZE)
                                             .setPrefetchDistance(PAGE_SIZE)
                                             .setEnablePlaceholders(false)
                                             .build()
                                        );
    }

    LiveData<PagedList<Movie>> getData() {
        return data;
    }
}
