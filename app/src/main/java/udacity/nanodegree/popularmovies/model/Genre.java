package udacity.nanodegree.popularmovies.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.squareup.moshi.Json;

@Entity(tableName = "Genre")
public class Genre {

    @Json(name = "id")
    @PrimaryKey
    @ColumnInfo(name = "id")
    public int id;

    @Json(name = "name")
    @ColumnInfo(name = "name")
    public String name;
}
