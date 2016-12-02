package udacity.nanodegree.popularmovies.api.models;

import android.content.Context;
import android.databinding.Observable;
import android.databinding.ObservableInt;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;

import com.google.gson.annotations.SerializedName;

import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import paperparcel.PaperParcel;
import udacity.nanodegree.popularmovies.MovieApp;
import udacity.nanodegree.popularmovies.R;
import udacity.nanodegree.popularmovies.api.models.GenresResponse.Genre;

import static udacity.nanodegree.popularmovies.utils.Utils.colorWithAlpha;
import static udacity.nanodegree.popularmovies.utils.Utils.tintColor;

/**
 * {
 * .."page": 1,
 * .."results": [
 * ....{
 * ......"poster_path": "/z6BP8yLwck8mN9dtdYKkZ4XGa3D.jpg",
 * ......"adult": false,
 * ......"overview": "A big screen remake of John ... thieves.",
 * ......"release_date": "2016-09-14",
 * ......"genre_ids": [
 * ........28,
 * ........12,
 * ........37
 * ......],
 * ......"id": 333484,
 * ......"original_title": "The Magnificent Seven",
 * ......"original_language": "en",
 * ......"title": "The Magnificent Seven",
 * ......"backdrop_path": "/g54J9MnNLe7WJYVIvdWTeTIygAH.jpg",
 * ......"popularity": 38.19583,
 * ......"vote_count": 298,
 * ......"video": false,
 * ......"vote_average": 4.67
 * ....}
 * ..],
 * .."total_results": 19648,
 * .."total_pages": 983
 * }
 */
public class MoviesResponse {

    @SerializedName("page") public          int         page;
    @SerializedName("total_results") public int         totalResults;
    @SerializedName("total_pages") public   int         totalPages;
    @SerializedName("results") public       List<Movie> results;


    @PaperParcel
    public static class Movie implements Parcelable {

        @SerializedName("poster_path") public       String        posterPath;
        @SerializedName("adult") public             boolean       adult;
        @SerializedName("overview") public          String        overview;
        @SerializedName("release_date") public      LocalDate     releaseDate;
        @SerializedName("id") public                int           id;
        @SerializedName("original_title") public    String        originalTitle;
        @SerializedName("original_language") public String        originalLanguage;
        @SerializedName("title") public             String        title;
        @SerializedName("backdrop_path") public     String        backdropPath;
        @SerializedName("popularity") public        double        popularity;
        @SerializedName("vote_count") public        int           voteCount;
        @SerializedName("video") public             boolean       video;
        @SerializedName("vote_average") public      float         voteAverage;
        @SerializedName("genre_ids") public         List<Integer> genreIds;

        public ObservableInt colorTitle;
        public ObservableInt colorBackground;
        public ObservableInt colorTint;
        public ObservableInt colorTransparentBackground;

        public static final Creator<Movie> CREATOR = PaperParcelMoviesResponse_Movie.CREATOR;

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
            return releaseDate.getYear();
        }

        @Nullable
        public String genres(@NonNull final Context ctx) {
            final List<Genre> genres = ((MovieApp) ctx.getApplicationContext()).genres;
            if (genres == null || genres.isEmpty()) {
                return null;
            }
            final List<String> genreNames = new ArrayList<>();
            for (Genre g : genres) {
                if (genreIds.contains(g.id)) {
                    genreNames.add(g.name);
                }
            }
            return TextUtils.join(", ", genreNames);
        }

        public float rating() {
            return voteAverage;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(final Parcel dest, final int flags) {
            PaperParcelMoviesResponse_Movie.writeToParcel(this, dest, flags);
        }
    }
}
