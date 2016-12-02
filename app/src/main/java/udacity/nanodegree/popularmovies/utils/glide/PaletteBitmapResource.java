package udacity.nanodegree.popularmovies.utils.glide;

import android.support.annotation.NonNull;

import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.util.Util;

public class PaletteBitmapResource implements Resource<PaletteBitmap> {

    @NonNull private final PaletteBitmap paletteBitmap;
    @NonNull private final BitmapPool    bitmapPool;

    public PaletteBitmapResource(@NonNull final PaletteBitmap paletteBitmap,
                                 @NonNull final BitmapPool bitmapPool) {
        this.paletteBitmap = paletteBitmap;
        this.bitmapPool = bitmapPool;
    }

    @Override
    public Class<PaletteBitmap> getResourceClass() {
        return PaletteBitmap.class;
    }

    @Override
    public PaletteBitmap get() {
        return paletteBitmap;
    }

    @Override
    public int getSize() {
        return Util.getBitmapByteSize(paletteBitmap.bitmap);
    }

    @Override
    public void recycle() {
        bitmapPool.put(paletteBitmap.bitmap);
    }
}
