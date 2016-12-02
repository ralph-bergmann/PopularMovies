package udacity.nanodegree.popularmovies.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;

import udacity.nanodegree.popularmovies.R;
import udacity.nanodegree.popularmovies.api.models.MoviesResponse;
import udacity.nanodegree.popularmovies.databinding.ActivityDetailBinding;

public class DetailActivity extends BaseActivity {

    public static final String ARG_MOVIE = "ARG_MOVIE";

    private MoviesResponse.Movie movie;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            movie = savedInstanceState.getParcelable(ARG_MOVIE);
        } else {
            movie = getIntent().getExtras().getParcelable(ARG_MOVIE);
        }

        final ActivityDetailBinding binding =
            DataBindingUtil.setContentView(this, R.layout.activity_detail);
        binding.setMovie(movie);
        binding.setActivity(this);

        setupFullscreen(null);
    }

    @Override
    void bindObservables() {}

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(ARG_MOVIE, movie);
    }
}
