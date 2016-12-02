package udacity.nanodegree.popularmovies.utils.paperparcel;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import org.threeten.bp.LocalDate;

import paperparcel.RegisterAdapter;
import paperparcel.TypeAdapter;

@RegisterAdapter
public class LocalDateAdapter implements TypeAdapter<LocalDate> {

    /**
     * Creates a new instance of the desired Type by reading values from the Parcel {@code inParcel}
     *
     * @param source
     *     The {@link Parcel} which contains the values of {@code T}
     * @return A new object based on the values in {@code inParcel}.
     */
    @Override
    public LocalDate readFromParcel(@NonNull final Parcel source) {
        return LocalDate.ofEpochDay(source.readLong());
    }

    /**
     * Writes {@code value} to the Parcel {@code outParcel}.
     *
     * @param value
     *     The object to be written to the {@link Parcel}
     * @param dest
     *     The {@link Parcel} which will contain the value of {@code T}
     * @param flags
     *     Additional flags about how the object should be written. May be 0 or
     *     {@link Parcelable#PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(final LocalDate value, @NonNull final Parcel dest, final int flags) {
        dest.writeLong(value.toEpochDay());
    }
}
