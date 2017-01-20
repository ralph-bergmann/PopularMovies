package udacity.nanodegree.popularmovies.ui.main;

import android.arch.paging.TiledDataSource;
import android.support.annotation.NonNull;

import java.io.IOException;
import java.util.List;

import retrofit2.Response;
import timber.log.Timber;
import udacity.nanodegree.popularmovies.api.TMDbApi;
import udacity.nanodegree.popularmovies.api.models.MoviesResponse;
import udacity.nanodegree.popularmovies.model.Movie;

public class FooDataSource extends TiledDataSource<Movie> {

    public static final int PAGE_SIZE = 20;
    private final TMDbApi api;

    public FooDataSource(@NonNull final TMDbApi api) {
        this.api = api;
    }

    @Override
    public int countItems() {
        return COUNT_UNDEFINED;
    }

    @Override
    public List<Movie> loadRange(final int startPosition, final int count) {
        final int page = startPosition / PAGE_SIZE + 1; // +1 because the TMDbApi starts with page 1
        try {
            final Response<MoviesResponse> response = api.popular2(page).execute();
            if (response.isSuccessful()) {
                final MoviesResponse body = response.body();
                if (body != null) {
                    return body.results;
                }
            }
        } catch (IOException e) {
            Timber.e(e, "failed to load page: %,d", startPosition);
        }
        return null;
    }
}
