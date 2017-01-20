package udacity.nanodegree.popularmovies.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bumptech.glide.ListPreloader;
import com.bumptech.glide.RequestBuilder;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;

import timber.log.Timber;
import udacity.nanodegree.popularmovies.databinding.ItemMovieBinding;
import udacity.nanodegree.popularmovies.model.Movie;
import udacity.nanodegree.popularmovies.utils.glide.PaletteBitmap;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MovieViewHolder>
    implements ListPreloader.PreloadModelProvider<Movie> {

    private final     Activity                      activity;
    private final     ClickHandler                  clickHandler;
    private final     RequestBuilder<PaletteBitmap> preloadRequest;
    @Nullable private List<Movie>                   data;

    public MainAdapter(final Activity activity,
                       final OnMovieInteractionListener listener,
                       final RequestBuilder<PaletteBitmap> preloadRequest) {

        this.activity = activity;
        this.clickHandler = new ClickHandler(listener);
        this.preloadRequest = preloadRequest;
        setHasStableIds(true);
    }

    public void setMovies(@NonNull final List<Movie> movies) {
        Timber.i("setMovies movies: %,d",movies.size());
//        if (data != null) {
//
//            final List<Movie> newData = new ArrayList<>(data);
//            newData.addAll(movies);
//
//            final DiffUtil.DiffResult diffResult =
//                DiffUtil.calculateDiff(new MovieDiffUtilCallback(data, newData));
//
//            data = newData;
//            diffResult.dispatchUpdatesTo(this);
//            diffResult.dispatchUpdatesTo(new ListUpdateCallback() {
//
//                @Override
//                public void onInserted(final int i, final int i1) {
//
//                }
//
//                @Override
//                public void onRemoved(final int i, final int i1) {
//
//                }
//
//                @Override
//                public void onMoved(final int i, final int i1) {
//
//                }
//
//                @Override
//                public void onChanged(final int i, final int i1, final Object o) {
//
//                }
//            });
//
//        } else {
            data = movies;
            notifyDataSetChanged();
//        }
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
        return data == null ? 0 : data.size();
    }

    @Override
    public long getItemId(final int position) {
        return data == null ? -1 : data.get(position).id;
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


    private static class MovieDiffUtilCallback extends DiffUtil.Callback {

        @NonNull private final List<Movie> oldList;
        @NonNull private final List<Movie> newList;

        MovieDiffUtilCallback(@NonNull final List<Movie> oldList,
                              @NonNull final List<Movie> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(final int oldItemPosition, final int newItemPosition) {
            return areItemsTheSame(oldList.get(oldItemPosition), newList.get(newItemPosition));
        }

        private static boolean areItemsTheSame(final Movie oldItem,
                                               final Movie newItem) {
            return oldItem.id == newItem.id;
        }

        @Override
        public boolean areContentsTheSame(final int oldItemPosition, final int newItemPosition) {
            return areContentsTheSame(oldList.get(oldItemPosition), newList.get(newItemPosition));
        }

        private static boolean areContentsTheSame(final Movie oldItem,
                                                  final Movie newItem) {
            return oldItem.id == newItem.id;
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
