package udacity.nanodegree.popularmovies.utils;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class UtilsTest {

    @Test
    public void fileSize() throws Exception {

        final List<String> sizes = new ArrayList<>();
        sizes.add("w500");
        sizes.add("w154");
        sizes.add("w92");
        sizes.add("w185");
        sizes.add("w342");
        sizes.add("w780");
        sizes.add("original");

        assertEquals("w92", Utils.fileSize(50, sizes));
        assertEquals("w92", Utils.fileSize(100, sizes));
        assertEquals("w154", Utils.fileSize(150, sizes));
        assertEquals("w185", Utils.fileSize(250, sizes));
        assertEquals("w342", Utils.fileSize(350, sizes));
        assertEquals("w500", Utils.fileSize(450, sizes));
        assertEquals("w500", Utils.fileSize(500, sizes));
        assertEquals("w780", Utils.fileSize(750, sizes));
        assertEquals("w780", Utils.fileSize(900, sizes));
        assertEquals("w780", Utils.fileSize(1280, sizes));
    }
}
