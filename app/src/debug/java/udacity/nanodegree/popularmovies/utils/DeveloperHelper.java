package udacity.nanodegree.popularmovies.utils;

import android.support.annotation.NonNull;

import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import udacity.nanodegree.popularmovies.MovieApp;

public class DeveloperHelper {

    public static void enableLogging(@NonNull final OkHttpClient.Builder builder) {
        final HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
        builder.addNetworkInterceptor(logging);
    }

    public static void enableStetho(@NonNull final OkHttpClient.Builder builder) {
        builder.addNetworkInterceptor(new StethoInterceptor());
    }

    public static void enableStetho(@NonNull final MovieApp app) {
        Stetho.initializeWithDefaults(app);
    }
}
