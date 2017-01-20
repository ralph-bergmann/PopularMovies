/**
 * Copyright (C) 2015 Wasabeef
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p>
 * with small changes for Glide v4
 * <p>
 * https://github.com/wasabeef/glide-transformations/blob/master/transformations/src/main/java/jp/wasabeef/glide/transformations/BlurTransformation.java
 */

package udacity.nanodegree.popularmovies.utils.glide;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.renderscript.RSRuntimeException;
import android.support.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.nio.ByteBuffer;
import java.security.MessageDigest;

import jp.wasabeef.glide.transformations.internal.FastBlur;
import jp.wasabeef.glide.transformations.internal.RSBlur;

public class BackdropImageTransformation extends BitmapTransformation {

    private static int    VERSION  = 1;
    private static String ID       = BackdropImageTransformation.class.getCanonicalName() + VERSION;
    private static byte[] ID_BYTES = ID.getBytes(CHARSET);

    private final Context context;
    private final int     radius;


    BackdropImageTransformation(Context context, int radius) {
        this.context = context.getApplicationContext();
        this.radius = radius;
    }

    @Override
    protected Bitmap transform(@NonNull final BitmapPool pool,
                               @NonNull final Bitmap toTransform,
                               final int outWidth,
                               final int outHeight) {

        int width = toTransform.getWidth();
        int height = toTransform.getHeight();
        int canvasHeight = width / 2 * 3;

        Bitmap bitmap = pool.get(width, canvasHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(toTransform,
                          new Rect(0, 0, width, height),
                          new Rect(0, 0, width, canvasHeight),
                          paint);

        try {
            bitmap = RSBlur.blur(context, bitmap, radius);
        } catch (RSRuntimeException e) {
            bitmap = FastBlur.blur(bitmap, radius, true);
        }

        canvas.drawBitmap(toTransform, 0f, canvasHeight - height, paint);

        return bitmap;
    }

    @Override
    public void updateDiskCacheKey(final MessageDigest messageDigest) {

        messageDigest.update(ID_BYTES);

        final byte[] radiusData = ByteBuffer.allocate(4).putInt(radius).array();
        messageDigest.update(radiusData);
    }
}
