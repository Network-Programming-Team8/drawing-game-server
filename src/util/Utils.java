package util;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Utils {
    static public <T> T selectRandomlyFrom(Collection<T> collection) {
        if (collection == null || collection.isEmpty()) {
            throw new IllegalArgumentException("Collection cannot be null or empty");
        }
        List<T> list = new ArrayList<>(collection);
        return list.get(ThreadLocalRandom.current().nextInt(list.size()));
    }
}