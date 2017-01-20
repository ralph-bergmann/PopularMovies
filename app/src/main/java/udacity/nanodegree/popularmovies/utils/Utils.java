package udacity.nanodegree.popularmovies.utils;

import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.ColorInt;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.graphics.Palette;

import java.util.List;
import java.util.regex.Pattern;

import udacity.nanodegree.popularmovies.model.Movie;
import udacity.nanodegree.popularmovies.model.PosterSize;

public class Utils {

    private static final Pattern PATTERN = Pattern.compile("^w\\d+");

    @ColorInt
    public static int tintColor(@ColorInt int color) {
        final float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        if (hsv[2] > 0.8f) { hsv[2] = 0.8f; }
        if (hsv[2] < 0.25f) { hsv[2] = 0.25f; }
        return Color.HSVToColor(hsv);
    }

    @ColorInt
    public static int colorWithAlpha(@ColorInt int color, @IntRange(from = 0x0, to = 0xFF) int alpha) {
        return ColorUtils.setAlphaComponent(color, alpha);
    }

    @ColorInt
    public static int colorForeground(@NonNull final Palette palette) {

        int UNSET = 0;
        int color;

        color = palette.getLightVibrantColor(UNSET);
        if (color == UNSET) {
            color = palette.getVibrantColor(UNSET);
        }
        if (color == UNSET) {
            color = palette.getLightMutedColor(UNSET);
        }
        if (color == UNSET) {
            color = palette.getMutedColor(UNSET);
        }
        if (color == UNSET) {
            color = Color.WHITE;
        }

        final float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        if (hsv[1] > 0.8f) { hsv[1] = 0.8f; }
        if (hsv[2] < 0.9f) { hsv[2] = 0.9f; }
        if (hsv[2] > 0.97f) { hsv[2] = 0.97f; }
        return Color.HSVToColor(hsv);
    }

    @ColorInt
    public static int colorBackground(@NonNull final Palette palette) {

        int UNSET = 0;
        int color;

        color = palette.getDarkMutedColor(UNSET);
        if (color == UNSET) {
            color = palette.getMutedColor(UNSET);
        }
        if (color == UNSET) {
            color = palette.getDarkVibrantColor(UNSET);
        }
        if (color == UNSET) {
            color = palette.getVibrantColor(UNSET);
        }
        if (color == UNSET) {
            color = Color.BLACK;
        }

        final float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        if (hsv[1] > 0.6f) { hsv[1] = 0.6f; }
        if (hsv[2] > 0.5f) { hsv[2] = 0.5f; }
        if (hsv[2] < 0.05f) { hsv[2] = 0.05f; }
        return Color.HSVToColor(hsv);
    }

    public static String urlFor(@NonNull final Movie movie,
                                final int width,
                                @NonNull final String secureBaseUrl,
                                @NonNull final List<PosterSize> posterSizes) {

        return Uri.parse(secureBaseUrl).buildUpon()
                  .appendPath(fileSize(width, posterSizes))
                  .appendPath(movie.posterPath.substring(1)) // remove leading /
                  .build()
                  .toString();
    }

//    public static String urlFor(@NonNull final BackdropImage image,
//                                final int width,
//                                @NonNull final ImageConfig imageConfig) {
//
//        return Uri.parse(imageConfig.secureBaseUrl).buildUpon()
//                  .appendPath(fileSize(width, imageConfig.posterSizes))
//                  .appendPath(image.path.substring(1)) // remove leading /
//                  .build()
//                  .toString();
//    }

    @VisibleForTesting
    static String fileSize(final int width, final List<PosterSize> sizes) {

        int min = Integer.MAX_VALUE;
        for (PosterSize size : sizes) {
            final String tmp = size.size;
            if (!PATTERN.matcher(tmp).matches()) {
                continue;
            }
            final int value = Integer.valueOf(tmp.substring(1));
            if (Math.abs(value - width) < Math.abs(min - width)) {
                min = value;
            }
        }

        return "w" + min;
    }
}
