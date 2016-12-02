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
import udacity.nanodegree.popularmovies.utils.Utils;

public class BackdropImageUrlLoader extends BaseGlideUrlLoader<BackdropImage> {

    @NonNull private final Context ctx;

    protected BackdropImageUrlLoader(@NonNull final ModelLoader<GlideUrl, InputStream> concreteLoader,
                                     @NonNull final ModelCache<BackdropImage, GlideUrl> modelCache,
                                     @NonNull final Context context) {
        super(concreteLoader, modelCache);
        this.ctx = context;
    }

    @Override
    protected String getUrl(final BackdropImage backdropImage,
                            final int width,
                            final int height,
                            final Options options) {

        final ConfigurationResponse.ImageConfiguration imageConfiguration =
            ((MovieApp) ctx.getApplicationContext()).imageConfiguration;
        return Utils.urlFor(backdropImage, width, imageConfiguration);
    }

    @Override
    public boolean handles(final BackdropImage backdropImage) {
        return true;
    }

    public static class Factory implements ModelLoaderFactory<BackdropImage, InputStream> {

        @NonNull private final Context ctx;

        public Factory(@NonNull final Context context) {
            this.ctx = context;
        }

        @Override
        public ModelLoader<BackdropImage, InputStream> build(final MultiModelLoaderFactory multiFactory) {
            return new BackdropImageUrlLoader(multiFactory.build(GlideUrl.class, InputStream.class),
                                              new ModelCache<>(),
                                              ctx);
        }

        @Override
        public void teardown() {

        }
    }
}
