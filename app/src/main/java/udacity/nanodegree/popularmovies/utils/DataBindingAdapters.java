package udacity.nanodegree.popularmovies.utils;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;

import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.target.ViewTarget;
import com.bumptech.glide.request.transition.Transition;

import udacity.nanodegree.popularmovies.model.Movie;
import udacity.nanodegree.popularmovies.utils.glide.BackdropImage;
import udacity.nanodegree.popularmovies.utils.glide.GlideRequestBuilder;
import udacity.nanodegree.popularmovies.utils.glide.PaletteBitmap;

import static udacity.nanodegree.popularmovies.utils.Utils.colorBackground;
import static udacity.nanodegree.popularmovies.utils.Utils.colorForeground;

public class DataBindingAdapters {

    @BindingAdapter({"imageUrl", "activity"})
    public static void setImageUrl(ImageView imageView,
                                   Movie movie,
                                   Activity activity) {
        GlideRequestBuilder.fullRequest(activity)
                           .load(movie)
                           .thumbnail(GlideRequestBuilder.thumbnailRequest(activity).load(movie))
                           .into(new ImageViewTarget<Bitmap>(imageView) {

                               @Override
                               protected void setResource(@Nullable final Bitmap resource) {
                                   view.setImageBitmap(resource);
                               }
                           });
    }

    @BindingAdapter({"imageUrlPalette", "activity"})
    public static void setImageUrlPalette(ImageView imageView,
                                          Movie movie,
                                          Activity activity) {
        GlideRequestBuilder.fullRequestPalette(activity)
                           .load(movie)
                           .thumbnail(GlideRequestBuilder.thumbnailRequestPalette(activity).load(movie))
                           .into(new ImageViewTarget<PaletteBitmap>(imageView) {

                               @Override
                               protected void setResource(@Nullable final PaletteBitmap resource) {
                                   if (resource == null) {
                                       return;
                                   }
                                   movie.colorTitle.set(colorForeground(resource.paletteBottom));
                                   movie.colorBackground.set(colorBackground(resource.paletteBottom));
                                   view.setImageBitmap(resource.bitmap);
                               }
                           });
    }

    @BindingAdapter({"backgroundUrl", "activity"})
    public static void setBackgroundUrl(View view,
                                        Movie movie,
                                        Activity activity) {
        view.setBackgroundColor(movie.colorBackground.get());
        GlideRequestBuilder.blurRequest(activity)
                           .load(movie)
                           .into(new ViewTarget<View, Bitmap>(view) {

                               @Override
                               public void onResourceReady(final Bitmap resource,
                                                           final Transition<? super Bitmap> transition) {

                                   view.setBackground(new BitmapDrawable(view.getContext().getResources(), resource));
                               }
                           });
    }

    @BindingAdapter({"contentScrim", "activity"})
    public static void setContentScrim(CollapsingToolbarLayout view,
                                       String path,
                                       Activity activity) {
        final BackdropImage image = new BackdropImage(path); // convert it to BackdropImage that Glide knows which url to load
        GlideRequestBuilder.backdropImageRequest(activity)
                           .load(image)
                           .into(new ViewTarget<CollapsingToolbarLayout, Bitmap>(view) {

                               @Override
                               public void onResourceReady(final Bitmap resource,
                                                           final Transition<? super Bitmap> transition) {
                                   view.setContentScrim(new BitmapDrawable(resource));
                               }
                           });
    }

    @BindingAdapter("fabTint")
    public static void setFabTint(FloatingActionButton fab, int color) {
        fab.setBackgroundTintList(ColorStateList.valueOf(color));
    }

    @BindingAdapter("ratingStarTint")
    public static void setRatingStarTint(RatingBar bar, int color) {
        DrawableCompat.setTint(bar.getProgressDrawable(), color);
    }
}
