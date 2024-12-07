package util;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Utils {
    static public <T> T selectRandomlyFrom(List<T> list) {
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("List cannot be null or empty");
        }
        return list.get(ThreadLocalRandom.current().nextInt(list.size()));
    }
}
