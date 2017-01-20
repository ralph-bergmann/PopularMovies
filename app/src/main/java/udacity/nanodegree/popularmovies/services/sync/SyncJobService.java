package udacity.nanodegree.popularmovies.services.sync;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;

import java.io.IOException;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import retrofit2.Response;
import timber.log.Timber;
import udacity.nanodegree.popularmovies.api.TMDbApi;
import udacity.nanodegree.popularmovies.api.models.ConfigResponse;
import udacity.nanodegree.popularmovies.api.models.GenresResponse;
import udacity.nanodegree.popularmovies.api.models.MoviesResponse;
import udacity.nanodegree.popularmovies.model.BackdropSize;
import udacity.nanodegree.popularmovies.model.ImageConfig;
import udacity.nanodegree.popularmovies.model.PosterSize;
import udacity.nanodegree.popularmovies.room.AppDatabase;

import static android.Manifest.permission.RECEIVE_BOOT_COMPLETED;
import static android.app.job.JobScheduler.RESULT_SUCCESS;
import static java.util.concurrent.TimeUnit.HOURS;

public class SyncJobService extends JobService {

    private static final int JOB_ID = 1;

    @Inject TMDbApi     api;
    @Inject AppDatabase db;


    @Override
    public void onCreate() {
        AndroidInjection.inject(this);
        super.onCreate();
    }

    @Override
    public boolean onStartJob(final JobParameters job) {
        Timber.i("onStartJob id: %s", job.getJobId());

        new Thread(() -> {

            try {
                save(loadImageConfig());
                save(loadGenres());
                save(loadPopularMovies());
            } catch (IOException e) {
                Timber.e(e, "onStartJob id: %s", job.getJobId());
            } finally {
                jobFinished(job, false);
            }
        }).start();

        return false;
    }

    @Override
    public boolean onStopJob(final JobParameters job) {
        return true;
    }

    private ImageConfig loadImageConfig() throws IOException {

        final Response<ConfigResponse> response = api.configuration().execute();
        if (!response.isSuccessful() || response.body() == null) {
            throw new IOException("failed to load image config");
        }

        return response.body().imageConfig;
    }

    private GenresResponse loadGenres() throws IOException {

        final Response<GenresResponse> genres = api.genres().execute();
        if (!genres.isSuccessful() || genres.body() == null) {
            throw new IOException("failed to load image config");
        }

        return genres.body();
    }

    private MoviesResponse loadPopularMovies() throws IOException {

        final Response<MoviesResponse> movies = api.popular().execute();
        if (!movies.isSuccessful() || movies.body() == null) {
            throw new IOException("failed to load image config");
        }

        return movies.body();
    }

    private void save(final ImageConfig imageConfig) {

        // delete old config
        db.imageConfigurationDao().deleteAll();

        // save new config
        final long id = db.imageConfigurationDao().insert(imageConfig);

        // save poster sizes
        final PosterSize posterSize = new PosterSize();
        posterSize.configId = id;

        for (String size : imageConfig.posterSizes) {
            posterSize.size = size;
            db.imageConfigurationDao().insert(posterSize);
        }

        // save backdrop sizes
        final BackdropSize backdropSize = new BackdropSize();
        backdropSize.configId = id;

        for (String size : imageConfig.backdropSizes) {
            backdropSize.size = size;
            db.imageConfigurationDao().insert(backdropSize);
        }
    }

    private void save(final MoviesResponse movies) {

        // delete old genres
        db.movieDao().deleteAll();

        // save new genres
        db.movieDao().insertAll(movies.results);
    }

    private void save(final GenresResponse genres) {

        // delete old genres
        db.genreDao().deleteAll();

        // save new genres
        db.genreDao().insertAll(genres.genres);
    }

    @RequiresPermission(RECEIVE_BOOT_COMPLETED)
    public static boolean createAndScheduleSyncJob(@NonNull final Context context) {

        final ComponentName componentName = new ComponentName(context, SyncJobService.class);
        final JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        final int result = jobScheduler.schedule(new JobInfo.Builder(JOB_ID, componentName)
                                                     .setPersisted(true)
                                                     .setPeriodic(HOURS.toMillis(12))
                                                     .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                                                     .build());
        return result == RESULT_SUCCESS;
    }
}
