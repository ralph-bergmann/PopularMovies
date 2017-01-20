package udacity.nanodegree.popularmovies.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(tableName = "BackdropSize",
        foreignKeys = @ForeignKey(entity = ImageConfig.class,
                                  parentColumns = "id",
                                  childColumns = "config_id",
                                  onDelete = CASCADE),
        indices = {@Index("config_id")})
public class BackdropSize {

    @PrimaryKey(autoGenerate = true) public long   id;
    @ColumnInfo(name = "config_id") public  long   configId;
    @ColumnInfo(name = "size") public       String size;
}
