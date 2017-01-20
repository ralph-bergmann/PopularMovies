package udacity.nanodegree.popularmovies.utils.glide;

import android.app.Activity;
import android.graphics.Bitmap;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import static com.bumptech.glide.GenericTransitionOptions.withNoTransition;
import static com.bumptech.glide.request.RequestOptions.priorityOf;

public class GlideRequestBuilder {

    public static RequestBuilder<Bitmap> fullRequest(final Activity activity) {
        return Glide.with(activity)
                    .asBitmap()
                    .transition(withNoTransition())
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL)
                                         .centerCrop());
    }

    public static RequestBuilder<Bitmap> thumbnailRequest(final Activity activity) {
        return fullRequest(activity)
            .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.DATA)
                                 .centerCrop()
                                 .override(50, 75));
    }

    public static RequestBuilder<PaletteBitmap> fullRequestPalette(final Activity activity) {
        return Glide.with(activity)
                    .as(PaletteBitmap.class)
                    .transition(withNoTransition())
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL)
                                         .centerCrop());
    }

    public static RequestBuilder<PaletteBitmap> thumbnailRequestPalette(final Activity activity) {
        return fullRequestPalette(activity)
            .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.DATA)
                                 .centerCrop()
                                 .override(50, 75));
    }

    public static RequestBuilder<PaletteBitmap> preloadRequest(final Activity activity) {
        return thumbnailRequestPalette(activity)
            .apply(priorityOf(Priority.HIGH));
    }

    public static RequestBuilder<Bitmap> blurRequest(final Activity activity) {
        return thumbnailRequest(activity)
            .apply(RequestOptions.bitmapTransform(new BlurTransformation(activity, 10)));
    }

    public static RequestBuilder<Bitmap> backdropImageRequest(final Activity activity) {
        return fullRequest(activity)
            .apply(RequestOptions.bitmapTransform(new BackdropImageTransformation(activity, 250))
                                 .sizeMultiplier(0.2f)); // load smaller image to prevent OOM
    }
}
