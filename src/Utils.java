import java.util.Collections;
import java.util.List;

public class Utils {
    static public <T> T selectRandomlyFrom(List<T> list) {
        Collections.shuffle(list);
        return list.get(0);
    }
}
