package udacity.nanodegree.popularmovies.utils.paperparcel;

import android.databinding.ObservableInt;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import paperparcel.RegisterAdapter;
import paperparcel.TypeAdapter;

@RegisterAdapter
public class ObservableIntAdapter implements TypeAdapter<ObservableInt> {

    /**
     * Creates a new instance of the desired Type by reading values from the Parcel {@code inParcel}
     *
     * @param source
     *     The {@link Parcel} which contains the values of {@code T}
     * @return A new object based on the values in {@code inParcel}.
     */
    @Override
    public ObservableInt readFromParcel(@NonNull final Parcel source) {
        return new ObservableInt(source.readInt());
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
    public void writeToParcel(@NonNull final ObservableInt value,
                              @NonNull final Parcel dest,
                              final int flags) {
        dest.writeInt(value.get());
    }
}
