package udacity.nanodegree.popularmovies.di;

import android.content.Context;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import udacity.nanodegree.popularmovies.BuildConfig;

import static udacity.nanodegree.popularmovies.utils.DeveloperHelper.enableLogging;
import static udacity.nanodegree.popularmovies.utils.DeveloperHelper.enableStetho;

@Module
class OkHttpModule {

    private static final long OKHTTP_CLIENT_CACHE_SIZE = 100L * 1024L * 1024L; // 100 MiB for images

    @Provides
    @Singleton
    static OkHttpClient provideOkHttpClient(final Context context) {

        final File cacheDirectory = new File(context.getCacheDir().getAbsoluteFile(), "http");
        final Cache cache = new Cache(cacheDirectory, OKHTTP_CLIENT_CACHE_SIZE);
        final OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.cache(cache);
        builder.addInterceptor(new TMDbApiInterceptor());

        if (BuildConfig.DEBUG) {
            enableLogging(builder);
            enableStetho(builder);
        }

        return builder.build();
    }

    private static class TMDbApiInterceptor implements Interceptor {

        TMDbApiInterceptor() {}

        @Override
        public Response intercept(final Chain chain) throws IOException {
            final HttpUrl httpUrl = chain.request()
                                         .url()
                                         .newBuilder()
                                         .addQueryParameter("language", apiLocaleString())
                                         .addQueryParameter("api_key", BuildConfig.TMDB_API_KEY)
                                         .build();
            return chain.proceed(chain.request()
                                      .newBuilder()
                                      .url(httpUrl)
                                      .build());
        }

        private static String apiLocaleString() {
            Locale locale = Locale.getDefault();
            return String.format(Locale.ENGLISH,
                                 "%s-%s",
                                 locale.getLanguage(),
                                 locale.getCountry());
        }
    }
}
