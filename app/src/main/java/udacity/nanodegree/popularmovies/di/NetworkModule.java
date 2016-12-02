package udacity.nanodegree.popularmovies.di;

import com.google.gson.Gson;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.io.IOException;
import java.util.Locale;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import udacity.nanodegree.popularmovies.BuildConfig;
import udacity.nanodegree.popularmovies.api.TMDbApi;

@Module(includes = {GsonModule.class, OkHttpModule.class})
public class NetworkModule {

    private static final String ENDPOINT_THEMOVIEDB_API = "https://api.themoviedb.org/3/";

    @Provides
    @Singleton
    static TMDbApi provideApi(final Gson gson, final OkHttpClient client) {

        final OkHttpClient apiClient = client
            .newBuilder()
            .addInterceptor(new TMDbApiInterceptor())
            .build();

        return new Retrofit.Builder()
            .baseUrl(ENDPOINT_THEMOVIEDB_API)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(apiClient)
            .build()
            .create(TMDbApi.class);
    }

    private static class TMDbApiInterceptor implements Interceptor {

        TMDbApiInterceptor() {}

        @Override
        public Response intercept(final Chain chain) throws IOException {

            final HttpUrl httpUrl = chain
                .request()
                .url()
                .newBuilder()
                .addQueryParameter("language", apiLocaleString())
                .addQueryParameter("api_key", BuildConfig.TMDB_API_KEY)
                .build();

            return chain.proceed(chain
                                     .request()
                                     .newBuilder()
                                     .addHeader("Accept", "application/json")
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
