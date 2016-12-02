package udacity.nanodegree.popularmovies;

import android.app.Application;

import com.jakewharton.threetenabp.AndroidThreeTen;
import com.squareup.leakcanary.LeakCanary;

import java.util.List;

import timber.log.Timber;
import udacity.nanodegree.popularmovies.api.models.ConfigurationResponse;
import udacity.nanodegree.popularmovies.api.models.GenresResponse;
import udacity.nanodegree.popularmovies.di.MainComponent;
import udacity.nanodegree.popularmovies.di.MainModule;
import udacity.nanodegree.popularmovies.utils.DebugTree;


public class MovieApp extends Application {

    private static MainComponent                            graph;
    public         ConfigurationResponse.ImageConfiguration imageConfiguration;
    public         List<GenresResponse.Genre>               genres;

    @Override
    public void onCreate() {
        super.onCreate();

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);

        graph = MainComponent.Initializer.init(new MainModule(this));

        if (BuildConfig.DEBUG) {
            Timber.plant(new DebugTree("movies"));
        }
        AndroidThreeTen.init(this);
    }

    public static MainComponent graph() {
        return graph;
    }
}
