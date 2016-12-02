package udacity.nanodegree.popularmovies.utils.glide;

import android.content.Context;
import android.support.annotation.NonNull;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.ModelCache;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;
import com.bumptech.glide.load.model.stream.BaseGlideUrlLoader;

import java.io.InputStream;

import udacity.nanodegree.popularmovies.MovieApp;
import udacity.nanodegree.popularmovies.api.models.ConfigurationResponse;
import udacity.nanodegree.popularmovies.api.models.MoviesResponse;
import udacity.nanodegree.popularmovies.utils.Utils;

public class MovieCoverUrlLoader extends BaseGlideUrlLoader<MoviesResponse.Movie> {

    @NonNull private final Context ctx;

    protected MovieCoverUrlLoader(@NonNull final ModelLoader<GlideUrl, InputStream> concreteLoader,
                                  @NonNull final ModelCache<MoviesResponse.Movie, GlideUrl> modelCache,
                                  @NonNull final Context context) {
        super(concreteLoader, modelCache);
        this.ctx = context;
    }

    @Override
    protected String getUrl(final MoviesResponse.Movie movie,
                            final int width,
                            final int height,
                            final Options options) {

        final ConfigurationResponse.ImageConfiguration imageConfiguration =
            ((MovieApp) ctx.getApplicationContext()).imageConfiguration;
        return Utils.urlFor(movie, width, imageConfiguration);
    }

    @Override
    public boolean handles(final MoviesResponse.Movie movie) {
        return true;
    }

    public static class Factory implements ModelLoaderFactory<MoviesResponse.Movie, InputStream> {

        @NonNull private final Context ctx;

        public Factory(@NonNull final Context context) {
            this.ctx = context;
        }

        @Override
        public ModelLoader<MoviesResponse.Movie, InputStream> build(final MultiModelLoaderFactory multiFactory) {
            return new MovieCoverUrlLoader(multiFactory.build(GlideUrl.class, InputStream.class),
                                           new ModelCache<>(),
                                           ctx);
        }

        @Override
        public void teardown() {

        }
    }
}
