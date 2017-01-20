package udacity.nanodegree.popularmovies.api.models;

import com.squareup.moshi.FromJson;
import com.squareup.moshi.ToJson;

import org.threeten.bp.LocalDate;

public class LocalDateAdapter {

    @ToJson
    String toJson(LocalDate localDate) {
        return localDate.toString();
    }

    @FromJson
    LocalDate fromJson(String value) {
        return LocalDate.parse(value);
    }
}
