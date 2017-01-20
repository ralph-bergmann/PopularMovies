package udacity.nanodegree.popularmovies.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.content.Context;
import android.databinding.Observable;
import android.databinding.ObservableInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;

import com.squareup.moshi.Json;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import udacity.nanodegree.popularmovies.R;

import static udacity.nanodegree.popularmovies.utils.Utils.colorWithAlpha;
import static udacity.nanodegree.popularmovies.utils.Utils.tintColor;

@Entity(tableName = "Movie")
public class Movie {

    @Json(name = "id")
    @PrimaryKey
    @ColumnInfo(name = "id")
    public int id;

    @Json(name = "poster_path")
    @ColumnInfo(name = "poster_path")
    public String posterPath;

    @Json(name = "adult")
    @ColumnInfo(name = "adult")
    public boolean adult;

    @Json(name = "overview")
    @ColumnInfo(name = "overview")
    public String overview;

//    @Json(name = "release_date")
//    @ColumnInfo(name = "release_date")
//    public LocalDate releaseDate;

    @Json(name = "original_title")
    @ColumnInfo(name = "original_title")
    public String originalTitle;

    @Json(name = "original_language")
    @ColumnInfo(name = "original_language")
    public String originalLanguage;

    @Json(name = "title")
    @ColumnInfo(name = "title")
    public String title;

    @Json(name = "backdrop_path")
    @ColumnInfo(name = "backdrop_path")
    public String backdropPath;

    @Json(name = "popularity")
    @ColumnInfo(name = "popularity")
    public double popularity;

    @Json(name = "vote_count")
    @ColumnInfo(name = "vote_count")
    public int voteCount;

    @Json(name = "video")
    @ColumnInfo(name = "video")
    public boolean video;

    @Json(name = "vote_average")
    @ColumnInfo(name = "vote_average")
    public float voteAverage;

//    @Json(name = "genre_ids")
//    @ColumnInfo(name = "genre_ids")
//    public List<Integer> genreIds;

    @Ignore
    public transient ObservableInt colorTitle;

    @Ignore
    public transient ObservableInt colorBackground;

    @Ignore
    public transient ObservableInt colorTint;

    @Ignore
    public transient ObservableInt colorTransparentBackground;

    public Movie() {

        colorTitle = new ObservableInt();
        colorBackground = new ObservableInt();
        colorTint = new ObservableInt();
        colorTransparentBackground = new ObservableInt();

        colorTitle.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {

            @Override
            public void onPropertyChanged(final Observable sender, final int propertyId) {
                colorTint.set(tintColor(colorTitle.get()));
            }
        });

        colorBackground.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {

            @Override
            public void onPropertyChanged(final Observable sender, final int propertyId) {
                colorTransparentBackground.set(colorWithAlpha(colorBackground.get(), 64));
            }
        });
    }

    public CharSequence fullTitle(@NonNull final Context ctx) {

        final TextAppearanceSpan titleAppearance = new TextAppearanceSpan(ctx,
                                                                          R.style.AppTheme_TextAppearance_White_16sp_Bold);
        final TextAppearanceSpan originalTitleAppearance = new TextAppearanceSpan(ctx,
                                                                                  R.style.AppTheme_TextAppearance_White70_14sp);
        SpannableString spannable;

        if (title.equals(originalTitle)) {
            spannable = new SpannableString(String.format(Locale.ENGLISH,
                                                          "%s (%d)",
                                                          title,
                                                          releaseDate()));
        } else {
            spannable = new SpannableString(String.format(Locale.ENGLISH, "%s\n%s (%d)",
                                                          title,
                                                          originalTitle,
                                                          releaseDate()));
        }
        spannable.setSpan(titleAppearance, 0, title.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(originalTitleAppearance,
                          title.length(),
                          spannable.length(),
                          Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return spannable;
    }

    public int releaseDate() {
        return 0; // releaseDate.getYear();
    }

    @Nullable
    public String genres(@NonNull final Context ctx) {
//            final List<Genre> genres = ((MovieApp) ctx.getApplicationContext()).genres;
//            if (genres == null || genres.isEmpty()) {
//                return null;
//            }
        final List<String> genreNames = new ArrayList<>();
//            for (Genre g : genres) {
//                if (genreIds.contains(g.id)) {
//                    genreNames.add(g.name);
//                }
//            }
        return TextUtils.join(", ", genreNames);
    }

    public float rating() {
        return voteAverage;
    }
}
