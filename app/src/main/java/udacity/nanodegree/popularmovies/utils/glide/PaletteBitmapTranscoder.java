package udacity.nanodegree.popularmovies.utils.glide;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.graphics.Palette;
import android.util.TypedValue;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.transcode.ResourceTranscoder;

public class PaletteBitmapTranscoder implements ResourceTranscoder<Bitmap, PaletteBitmap> {

    @NonNull private final Context    ctx;
    @NonNull private final BitmapPool bitmapPool;

    public PaletteBitmapTranscoder(@NonNull final Context context) {
        this.ctx = context;
        this.bitmapPool = Glide.get(context).getBitmapPool();
    }

    @Override
    public Resource<PaletteBitmap> transcode(final Resource<Bitmap> toTranscode) {
        final Bitmap bitmap = toTranscode.get();
        final Palette paletteFull = Palette.from(bitmap)
                                           .maximumColorCount(16)
                                           .clearFilters()
                                           .generate();
        final Palette paletteBottom =
            Palette.from(bitmap)
                   .maximumColorCount(6)
                   .clearFilters()
                   .setRegion(0,
                              bitmap.getHeight() - twelveDip(ctx),
                              bitmap.getWidth(),
                              bitmap.getHeight())
                   .generate();
        final PaletteBitmap result = new PaletteBitmap(bitmap, paletteFull, paletteBottom);
        return new PaletteBitmapResource(result, bitmapPool);
    }

    private static int twelveDip(final Context ctx) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                                               12f,
                                               ctx.getResources().getDisplayMetrics());
    }
}
