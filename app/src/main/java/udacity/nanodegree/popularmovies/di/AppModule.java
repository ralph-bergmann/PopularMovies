package udacity.nanodegree.popularmovies.di;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import udacity.nanodegree.popularmovies.room.AppDatabase;

@Module
abstract class AppModule {

    @Binds
    abstract Context bindContext(Application application);

    @Provides
    @Singleton
    static AppDatabase provideDb(Context context) {
        return Room.databaseBuilder(context, AppDatabase.class, "movie").build();
    }
}
