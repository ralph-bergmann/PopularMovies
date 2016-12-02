package udacity.nanodegree.popularmovies.adapter;

import android.app.Activity;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.util.SortedListAdapterCallback;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bumptech.glide.ListPreloader;
import com.bumptech.glide.RequestBuilder;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;

import udacity.nanodegree.popularmovies.api.models.MoviesResponse.Movie;
import udacity.nanodegree.popularmovies.databinding.ItemMovieBinding;
import udacity.nanodegree.popularmovies.ui.MainActivity;
import udacity.nanodegree.popularmovies.utils.glide.PaletteBitmap;

import static udacity.nanodegree.popularmovies.ui.MainActivity.SORTORDER_MOST_POPULAR;
import static udacity.nanodegree.popularmovies.ui.MainActivity.SORTORDER_TOP_RATED;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MovieViewHolder>
    implements ListPreloader.PreloadModelProvider<Movie> {

    private final MainAdapterCallback           adapterCallback;
    private final SortedList<Movie>             data;
    private final Activity                      activity;
    private final ClickHandler                  clickHandler;
    private final RequestBuilder<PaletteBitmap> preloadRequest;

    public MainAdapter(final Activity activity,
                       final OnMovieInteractionListener listener,
                       final RequestBuilder<PaletteBitmap> preloadRequest) {

        this.adapterCallback = new MainAdapterCallback(this);
        this.data = new SortedList<>(Movie.class, adapterCallback);
        this.activity = activity;
        this.clickHandler = new ClickHandler(listener);
        this.preloadRequest = preloadRequest;
        setHasStableIds(true);
    }

    public void addMovies(final List<Movie> movies) {
        data.addAll(movies);
    }

    public void clear() {
        data.clear();
    }

    public void setSortOrder(@MainActivity.SortOrder final int sortOrder) {
        adapterCallback.setSortOrder(sortOrder);
    }

    @Override
    public MovieViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        return new MovieViewHolder(ItemMovieBinding.inflate(LayoutInflater.from(parent.getContext()),
                                                            parent,
                                                            false));
    }

    @Override
    public void onBindViewHolder(final MovieViewHolder holder, final int position) {
        holder.binder.setMovie(data.get(position));
        holder.binder.setActivity(activity);
        holder.binder.setClickHandler(clickHandler);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public long getItemId(final int position) {
        return data.get(position).id;
    }

    //
    // ListPreloader.PreloadModelProvider<Movie>
    //

    @Override
    public List<Movie> getPreloadItems(final int position) {
        return Collections.singletonList(data.get(position));
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


    private static class MainAdapterCallback extends SortedListAdapterCallback<Movie> {

        private int sortOrder;

        MainAdapterCallback(final RecyclerView.Adapter adapter) {
            super(adapter);
        }

        @Override
        public int compare(final Movie o1, final Movie o2) {
            switch (sortOrder) {
                case SORTORDER_TOP_RATED:
                    return Float.compare(o2.voteAverage, o1.voteAverage);
                case SORTORDER_MOST_POPULAR:
                default:
                    return Double.compare(o2.popularity, o1.popularity);
            }
        }

        @Override
        public boolean areContentsTheSame(final Movie oldItem, final Movie newItem) {
            return oldItem.id == newItem.id;
        }

        @Override
        public boolean areItemsTheSame(final Movie item1, final Movie item2) {
            return item1.id == item2.id;
        }

        public void setSortOrder(@MainActivity.SortOrder final int sortOrder) {
            this.sortOrder = sortOrder;
        }
    }


    public static class ClickHandler {

        private final WeakReference<OnMovieInteractionListener> ref;

        public ClickHandler(final OnMovieInteractionListener listener) {
            ref = new WeakReference<>(listener);
        }

        public void onClick(final Movie movie) {
            final OnMovieInteractionListener listener = ref.get();
            if (listener != null) {
                listener.onClick(movie);
            }
        }
    }


    public interface OnMovieInteractionListener {

        void onClick(Movie movie);
    }
}
