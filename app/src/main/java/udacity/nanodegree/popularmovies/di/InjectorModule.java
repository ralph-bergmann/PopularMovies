package udacity.nanodegree.popularmovies.di;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import udacity.nanodegree.popularmovies.di.scopes.ActivityScope;
import udacity.nanodegree.popularmovies.di.scopes.ServiceScope;
import udacity.nanodegree.popularmovies.services.sync.SyncJobService;
import udacity.nanodegree.popularmovies.services.sync.SyncJobServiceModule;
import udacity.nanodegree.popularmovies.ui.main.MainActivity;
import udacity.nanodegree.popularmovies.ui.main.MainActivityModule;

@Module
abstract class InjectorModule {

    @ActivityScope
    @ContributesAndroidInjector(modules = {MainActivityModule.class})
    abstract MainActivity contributeMainActivityInjector();

    @ServiceScope
    @ContributesAndroidInjector(modules = {SyncJobServiceModule.class})
    abstract SyncJobService contributeSyncJobServiceInjector();
}
