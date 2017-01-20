package udacity.nanodegree.popularmovies.utils.glide;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LifecycleRegistry;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.ModelCache;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;
import com.bumptech.glide.load.model.stream.BaseGlideUrlLoader;

import java.io.InputStream;
import java.util.List;

import javax.inject.Inject;

import udacity.nanodegree.popularmovies.MovieApp;
import udacity.nanodegree.popularmovies.model.ImageConfig;
import udacity.nanodegree.popularmovies.model.Movie;
import udacity.nanodegree.popularmovies.model.PosterSize;
import udacity.nanodegree.popularmovies.room.AppDatabase;
import udacity.nanodegree.popularmovies.utils.Utils;

import static android.arch.lifecycle.Lifecycle.Event.ON_DESTROY;
import static android.arch.lifecycle.Lifecycle.Event.ON_START;

public class MovieCoverUrlLoader extends BaseGlideUrlLoader<Movie> implements LifecycleOwner {

    @Inject           AppDatabase       db;
    @NonNull private  LifecycleRegistry lifecycle;
    @Nullable private ImageConfig       imageConfig;
    @Nullable private List<PosterSize>  posterSizes;

    MovieCoverUrlLoader(@NonNull final ModelLoader<GlideUrl, InputStream> concreteLoader,
                        @NonNull final ModelCache<Movie, GlideUrl> modelCache) {

        super(concreteLoader, modelCache);
        MovieApp.graph().inject(this);

        lifecycle = new LifecycleRegistry(this);
        lifecycle.handleLifecycleEvent(ON_START);

        db.imageConfigurationDao()
          .get()
          .observe(this, imageConfig -> this.imageConfig = imageConfig);

        db.imageConfigurationDao()
          .getAllPosterSize()
          .observe(this, posterSizes -> this.posterSizes = posterSizes);
    }

    @Override
    protected String getUrl(final Movie movie,
                            final int width,
                            final int height,
                            final Options options) {

        if (imageConfig == null || posterSizes == null) {
            throw new IllegalStateException("imageConfig or posterSizes are null");
        }

        return Utils.urlFor(movie, width, imageConfig.secureBaseUrl, posterSizes);
    }

    @Override
    public boolean handles(final Movie movie) {
        return true;
    }

    @Override
    public Lifecycle getLifecycle() {
        return lifecycle;
    }

    void teardown() {
        lifecycle.handleLifecycleEvent(ON_DESTROY);
    }

    public static class Factory implements ModelLoaderFactory<Movie, InputStream> {

        @Nullable private MovieCoverUrlLoader loader;

        @Override
        public ModelLoader<Movie, InputStream> build(final MultiModelLoaderFactory multiFactory) {

            loader = new MovieCoverUrlLoader(multiFactory.build(GlideUrl.class, InputStream.class),
                                             new ModelCache<>());
            return loader;
        }

        @Override
        public void teardown() {
            if (loader != null) {
                loader.teardown();
            }
        }
    }
}
