package udacity.nanodegree.popularmovies.di;

import android.support.annotation.NonNull;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import udacity.nanodegree.popularmovies.MovieApp;

@Module(includes = {NetworkModule.class})
public class MainModule {

    @NonNull private final MovieApp app;

    public MainModule(@NonNull final MovieApp app) {
        this.app = app;
    }

    @Provides
    @Singleton
    MovieApp provideApplication() {
        return app;
    }
}
