package udacity.nanodegree.popularmovies.di;

import android.support.annotation.NonNull;

import javax.inject.Singleton;

import dagger.Component;
import udacity.nanodegree.popularmovies.ui.MainActivity;
import udacity.nanodegree.popularmovies.utils.glide.MyGlideModule;

@Singleton
@Component(modules = {MainModule.class})
public interface MainComponent {

    void inject(MainActivity mainActivity);
    void inject(MyGlideModule myGlideModule);

    final class Initializer {

        private Initializer() { }

        public static MainComponent init(@NonNull final MainModule mainModule) {
            return DaggerMainComponent.builder().mainModule(mainModule).build();
        }
    }
}
