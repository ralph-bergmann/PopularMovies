package udacity.nanodegree.popularmovies.di;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import org.threeten.bp.LocalDate;

import java.io.IOException;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class GsonModule {

    @Provides
    @Singleton
    static Gson provideGson() {

        return new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter())
            .create();
    }

    private static class LocalDateTypeAdapter extends TypeAdapter<LocalDate> {

        LocalDateTypeAdapter() {}

        @Override
        public LocalDate read(final JsonReader in) throws IOException {
            return LocalDate.parse(in.nextString());
        }

        @Override
        public void write(final JsonWriter out,
                          final LocalDate value) throws IOException {

        }
    }
}
