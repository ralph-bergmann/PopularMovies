package udacity.nanodegree.popularmovies.ui.main;

import android.app.Activity;
import android.arch.paging.PagedListAdapter;
import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.DiffCallback;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bumptech.glide.ListPreloader;
import com.bumptech.glide.RequestBuilder;

import java.util.Collections;
import java.util.List;

import udacity.nanodegree.popularmovies.adapter.MainAdapter;
import udacity.nanodegree.popularmovies.databinding.ItemMovieBinding;
import udacity.nanodegree.popularmovies.model.Movie;
import udacity.nanodegree.popularmovies.utils.glide.PaletteBitmap;

public class FooAdapter extends PagedListAdapter<Movie, FooAdapter.MovieViewHolder>
    implements ListPreloader.PreloadModelProvider<Movie> {

    private final Activity                      activity;
    private final MainAdapter.ClickHandler      clickHandler;
    private final RequestBuilder<PaletteBitmap> preloadRequest;

    private static final DiffCallback<Movie> DIFF_CALLBACK = new DiffCallback<Movie>() {

        @Override
        public boolean areItemsTheSame(@NonNull Movie oldItem, @NonNull Movie newItem) {
            // User properties may have changed if reloaded from the DB, but ID is fixed
            return theSame(oldItem, newItem);
        }

        @Override
        public boolean areContentsTheSame(@NonNull Movie oldItem, @NonNull Movie newItem) {
            // NOTE: if you use equals, your object must properly override Object#equals()
            // Incorrectly returning false here will result in too many animations.
            return theSame(oldItem, newItem);
        }

        private boolean theSame(final @NonNull Movie oldItem, final @NonNull Movie newItem) {
            return oldItem.id == newItem.id;
        }
    };

    public FooAdapter(final Activity activity,
                      final MainAdapter.OnMovieInteractionListener listener,
                      final RequestBuilder<PaletteBitmap> preloadRequest) {
        super(DIFF_CALLBACK);
        this.activity = activity;
        this.clickHandler = new MainAdapter.ClickHandler(listener);
        this.preloadRequest = preloadRequest;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        return new FooAdapter.MovieViewHolder(ItemMovieBinding.inflate(LayoutInflater.from(parent.getContext()),
                                                                       parent,
                                                                       false));
    }

    @Override
    public void onBindViewHolder(final MovieViewHolder holder, final int position) {

        Movie movie = getItem(position);
        if (movie != null) {
            holder.binder.setMovie(movie);
            holder.binder.setActivity(activity);
            holder.binder.setClickHandler(clickHandler);
        } else {
            // Null defines a placeholder item - PagedListAdapter will automatically invalidate
            // this row when the actual object is loaded from the database
//            holder.clear();
        }
    }

    @Override
    public List<Movie> getPreloadItems(final int position) {
        return Collections.singletonList(getItem(position));
    }

    @Override
    public RequestBuilder getPreloadRequestBuilder(final Movie item) {
        return preloadRequest.load(item);
    }

    static class MovieViewHolder extends RecyclerView.ViewHolder {

        final ItemMovieBinding binder;

        MovieViewHolder(final ItemMovieBinding binder) {
            super(binder.getRoot());
            this.binder = binder;
        }
    }
}
