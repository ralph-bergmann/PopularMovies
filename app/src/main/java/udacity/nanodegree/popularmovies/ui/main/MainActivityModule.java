package udacity.nanodegree.popularmovies.ui.main;

import android.arch.lifecycle.ViewModelProviders;

import dagger.Module;
import dagger.Provides;

@Module
public class MainActivityModule {

    @Provides
    static MainActivityViewModel provideViewModel(MainActivity activity) {
        return ViewModelProviders.of(activity).get(MainActivityViewModel.class);
    }
}
