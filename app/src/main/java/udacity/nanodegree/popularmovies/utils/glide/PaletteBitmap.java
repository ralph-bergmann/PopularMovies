package udacity.nanodegree.popularmovies.utils.glide;

import android.graphics.Bitmap;
import android.support.v7.graphics.Palette;

public class PaletteBitmap {

    public final Bitmap  bitmap;
    public final Palette paletteFull;
    public final Palette paletteBottom;

    public PaletteBitmap(final Bitmap bitmap,
                         final Palette paletteFull,
                         final Palette paletteBottom) {
        this.bitmap = bitmap;
        this.paletteFull = paletteFull;
        this.paletteBottom = paletteBottom;
    }
}
