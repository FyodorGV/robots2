package gui;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Фильтр для словаря, который добавляет или убирает префикс у ключей
 */
public class PrefixedMap extends AbstractMap<String, String> {

    private final Map<String, String> source; /** исходный */
    private final String prefix; /** префикс . на конце */

    /**
     * Создает новый фильтр для словаря
     */
    public PrefixedMap(Map<String, String> source, String prefix) {
        this.source = source;
        this.prefix = prefix + ".";
    }

    @Override
    public Set<Entry<String, String>> entrySet() {
        return new AbstractSet<Entry<String, String>>() {
            @Override
            public Iterator<Entry<String, String>> iterator() {
                Iterator<Entry<String, String>> it = source.entrySet().iterator();

                return new Iterator<Entry<String, String>>() {
                    private Entry<String, String> next;

                    @Override
                    public boolean hasNext() {
                        while (it.hasNext()) {
                            Entry<String, String> entry = it.next();
                            if (entry.getKey().startsWith(prefix)) {
                                String key = entry.getKey().substring(prefix.length());
                                next = new SimpleEntry<>(key, entry.getValue());
                                return true;
                            }
                        }
                        return false;
                    }

                    @Override
                    public Entry<String, String> next() {
                        return next;
                    }
                };
            }

            @Override
            public int size() {
                int count = 0;
                for (String key : source.keySet()) {
                    if (key.startsWith(prefix)) count++;
                }
                return count;
            }
        };
    }

    @Override
    public String put(String key, String value) {
        return source.put(prefix + key, value);
    }
}