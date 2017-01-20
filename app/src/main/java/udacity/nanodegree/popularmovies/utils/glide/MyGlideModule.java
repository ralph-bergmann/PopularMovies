package udacity.nanodegree.popularmovies.utils.glide;

import android.content.Context;
import android.graphics.Bitmap;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;

import java.io.InputStream;

import javax.inject.Inject;

import okhttp3.OkHttpClient;
import udacity.nanodegree.popularmovies.MovieApp;
import udacity.nanodegree.popularmovies.model.Movie;

@GlideModule
public class MyGlideModule extends AppGlideModule {

    @Inject OkHttpClient client;

    public MyGlideModule() {
        MovieApp.graph().inject(this);
    }

    @Override
    public void applyOptions(final Context context, final GlideBuilder builder) {}

    @Override
    public void registerComponents(Context context, Glide glide, Registry registry) {
        registry
            .replace(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(client))
            .register(Bitmap.class, PaletteBitmap.class, new PaletteBitmapTranscoder(context, glide))
            .append(Movie.class, InputStream.class, new MovieCoverUrlLoader.Factory())
            .append(BackdropImage.class, InputStream.class, new BackdropImageUrlLoader.Factory(context));
    }

    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }
}
