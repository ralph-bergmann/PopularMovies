package udacity.nanodegree.popularmovies.di;

import java.io.File;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import udacity.nanodegree.popularmovies.BuildConfig;
import udacity.nanodegree.popularmovies.MovieApp;

@Module
public class OkHttpModule {

    private static final long OKHTTP_CLIENT_CACHE_SIZE = 100L * 1024L * 1024L; // 100 MiB for images

    @Provides
    @Singleton
    static OkHttpClient provideOkHttpClient(final MovieApp app) {

        final File cacheDirectory = new File(app.getCacheDir().getAbsoluteFile(), "http");
        final Cache cache = new Cache(cacheDirectory, OKHTTP_CLIENT_CACHE_SIZE);
        final OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.cache(cache);

        if (BuildConfig.DEBUG) {
            final HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
            builder.addNetworkInterceptor(logging);
        }

        return builder.build();
    }
}
