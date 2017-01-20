package udacity.nanodegree.popularmovies.di;

import android.app.Application;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;
import dagger.android.support.DaggerApplication;
import udacity.nanodegree.popularmovies.ui.main.MainActivityViewModel;
import udacity.nanodegree.popularmovies.utils.glide.MovieCoverUrlLoader;
import udacity.nanodegree.popularmovies.utils.glide.MyGlideModule;

@Singleton
@Component(modules = {
    AppModule.class,
    AndroidSupportInjectionModule.class,
    InjectorModule.class,
    NetworkModule.class
})
public interface AppComponent extends AndroidInjector<DaggerApplication> {

    @Override
    void inject(DaggerApplication instance);

    void inject(MyGlideModule myGlideModule);
    void inject(MovieCoverUrlLoader movieCoverUrlLoader);
    void inject(MainActivityViewModel mainActivityViewModel);

    @Component.Builder
    interface Builder {

        @BindsInstance
        AppComponent.Builder application(Application application);

        AppComponent build();
    }
}
