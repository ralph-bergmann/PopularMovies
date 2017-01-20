package udacity.nanodegree.popularmovies.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import com.squareup.moshi.Json;

import java.util.List;

@Entity(tableName = "ImageConfig")
public class ImageConfig {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @Json(name = "base_url")
    @ColumnInfo(name = "base_url")
    public String baseUrl;

    @Json(name = "secure_base_url")
    @ColumnInfo(name = "secure_base_url")
    public String secureBaseUrl;

    @Json(name = "backdrop_sizes")
    @Ignore
    public List<String> backdropSizes;

    @Json(name = "logo_sizes")
    @Ignore
    public List<String> logoSizes;

    @Json(name = "poster_sizes")
    @Ignore
    public List<String> posterSizes;

    @Json(name = "profile_sizes")
    @Ignore
    public List<String> profileSizes;

    @Json(name = "still_sizes")
    @Ignore
    public List<String> stillSizes;
}
