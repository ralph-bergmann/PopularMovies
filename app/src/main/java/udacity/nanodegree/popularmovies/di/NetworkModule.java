package udacity.nanodegree.popularmovies.di;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.squareup.moshi.Moshi;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;
import udacity.nanodegree.popularmovies.api.TMDbApi;
import udacity.nanodegree.popularmovies.api.models.LocalDateAdapter;

@Module(includes = {OkHttpModule.class})
class NetworkModule {

    private static final String ENDPOINT_THEMOVIEDB_API = "https://api.themoviedb.org/3/";

    @Provides
    @Singleton
    static Moshi provideMoshi() {
        return new Moshi.Builder()
            .add(new LocalDateAdapter())
            .build();
    }

    @Provides
    @Singleton
    static TMDbApi provideApi(final Moshi moshi, final OkHttpClient client) {

        return new Retrofit.Builder()
            .baseUrl(ENDPOINT_THEMOVIEDB_API)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(client)
            .build()
            .create(TMDbApi.class);
    }
}
