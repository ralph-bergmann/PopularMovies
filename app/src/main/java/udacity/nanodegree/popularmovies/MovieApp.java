package udacity.nanodegree.popularmovies;

import android.content.Intent;

import com.google.android.gms.security.ProviderInstaller;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.squareup.leakcanary.LeakCanary;

import dagger.android.AndroidInjector;
import dagger.android.support.DaggerApplication;
import timber.log.Timber;
import udacity.nanodegree.popularmovies.di.AppComponent;
import udacity.nanodegree.popularmovies.di.DaggerAppComponent;
import udacity.nanodegree.popularmovies.utils.DebugTree;

import static udacity.nanodegree.popularmovies.utils.DeveloperHelper.enableStetho;


public class MovieApp extends DaggerApplication implements ProviderInstaller.ProviderInstallListener {

    private static AppComponent graph;

    @Override
    public void onCreate() {
        super.onCreate();

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);

        if (BuildConfig.DEBUG) {
            Timber.plant(new DebugTree("movies"));
            enableStetho(this);
        }

        AndroidThreeTen.init(this);

        // Ensure an updated security provider is installed into the system when a new one is
        // available via Google Play services.
        try {
            ProviderInstaller.installIfNeededAsync(getApplicationContext(), this);
        } catch (Exception ignorable) {
            Timber.e("Unknown issue trying to install a new security provider.", ignorable);
        }
    }

    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        graph = DaggerAppComponent.builder().application(this).build();
        graph.inject(this);
        return graph;
    }

    @Override
    public void onProviderInstalled() {
        Timber.i("New security provider installed.");
    }

    @Override
    public void onProviderInstallFailed(int errorCode, Intent intent) {
        Timber.e("New security provider install failed.");
        // No notification shown there is no user intervention needed.
    }

    public static AppComponent graph() {
        return graph;
    }
}
