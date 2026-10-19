import java.util.List;

public class ListTools {
    public static <T> T lastOrDefault(List<T> items, T fallback) {
        if (items.isEmpty()) {
            return fallback;
        }
        return items.get(items.size() - 1);
    }

    public static <T extends Comparable<T>> T max(List<T> items) {
        if (items.isEmpty()) {
            return null;
        }
        T best = items.get(0);
        for (T item : items) {
            if (item.compareTo(best) > 0) {
                best = item;
            }
        }
        return best;
    }
}
