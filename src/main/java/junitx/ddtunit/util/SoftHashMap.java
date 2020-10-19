/**
 * This class was taken from<br/>
 * <a href="http://www.gruntz.ch/papers/references/SoftHashMap.java">SoftHashMap (Java Code)</a><br/>
 * <a href="http://www.gruntz.ch/papers/references/">References API (PDF Artikel)</a><br/>
 * by Dominik Gruntz, Fachhochschule Aargau/Nordwestschweiz<br/>
 * @author <a href="mailto:d.gruntz@fh-aargau.ch">Dominik Gruntz</a>
 */
package junitx.ddtunit.util;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public class SoftHashMap<K, V> extends AbstractMap<K, V> {
    private Map<K, Reference> innerMap = null;

    private ReferenceQueue refQueue = new ReferenceQueue();

    /** The number of "hard" references to hold internally. */
    private final int HARD_SIZE;

    /** The FIFO list of hard references, order of last access. */
    private final LinkedList hardCache = new LinkedList();

    public SoftHashMap() {
        innerMap = new HashMap<K, Reference>();
        this.HARD_SIZE = 100;
    }

    public SoftHashMap(int initialCapacity) {
        innerMap = new HashMap<K, Reference>(initialCapacity);
        this.HARD_SIZE = initialCapacity;
    }

    public SoftHashMap(int initialCapacity, float loadFactor) {
        innerMap = new HashMap<K, Reference>(initialCapacity, loadFactor);
        this.HARD_SIZE = initialCapacity;
    }

    public Object get(String key) {
        Reference res = innerMap.get(key);
        Object result = null;
        // We get the SoftReference represented by that key
        if (res != null) {
            // From the SoftReference we get the value, which can be
            // null if it was not in the map, or it was removed in
            // the processQueue() method defined below
            result = ((Reference) res).get();
            if (result == null) {
                // If the value has been garbage collected, remove the
                // entry from the HashMap.
                innerMap.remove(key);
            } else {
                // We now add this object to the beginning of the hard
                // reference queue. One reference can occur more than
                // once, because lookups of the FIFO queue are slow, so
                // we don't want to search through it each time to remove
                // duplicates.
                hardCache.addFirst(result);
                if (hardCache.size() > HARD_SIZE) {
                    // Remove the last entry if list longer than HARD_SIZE
                    hardCache.removeLast();
                }
            }
        }
        return result;
    }

    public V put(K key, V value) {
        processQueue();
        Reference ref = (Reference) new SoftEntry<K>(key, value, refQueue);
        Reference res = innerMap.put(key, ref);
        return res == null ? null : (V) res.get();
    }

    private Set entrySet = null;

    public Set entrySet() {
        if (entrySet == null)
            entrySet = new EntrySet();
        return entrySet;
    }

    private void processQueue() {
        Reference ref;
        while ((ref = refQueue.poll()) != null) {
            SoftEntry sEntry = (SoftEntry) ref;
            innerMap.remove(sEntry.key);
        }
    }

    public int size() {
        processQueue();
        // return entrySet().size();
        return innerMap.size();
    }

    public V remove(Object key) {
        processQueue();
        Reference res = innerMap.remove(key);
        return res == null ? null : (V) res.get();
    }

    public void clear() {
        hardCache.clear();
        processQueue();
        innerMap.clear();
    }

    private static class SoftEntry<K> extends SoftReference {
        private K key; // neccessary so that freed objects can be removed

        private SoftEntry(K key, Object value, ReferenceQueue queue) {
            super(value, queue);
            this.key = key;
        }
    }

    private class EntrySet extends AbstractSet {
        private Set entrySet = innerMap.entrySet();

        public int size() {
            int s = 0;
            for (Iterator iter = iterator(); iter.hasNext(); iter.next())
                s++;
            return s;
        }

        public boolean isEmpty() {
            // default implementation computes size which is inefficient
            return !(iterator().hasNext());
        }

        public boolean remove(Object o) {
            processQueue();
            return super.remove(o);
        }

        public Iterator iterator() {

            return new Iterator() {
                Iterator it = entrySet.iterator();

                Entry next = null;

                Object value = null;

                /*
                 * Strong reference to key, so that the GC will leave it alone
                 * as long as this Entry exists
                 */

                public boolean hasNext() {
                    while (it.hasNext()) {
                        final Entry entry = (Entry) it.next();
                        SoftEntry se = (SoftEntry) entry.getValue();
                        value = null;
                        if ((se != null) && ((value = se.get()) == null)) {
                            /* Weak key has been cleared by GC */
                            continue;
                        }
                        next = new Map.Entry() {
                            public Object getKey() {
                                return entry.getKey();
                            }

                            public Object getValue() {
                                return value;
                            }

                            public Object setValue(Object v) {
                                Object res = value;
                                value = v;
                                entry.setValue(new SoftEntry((String) entry
                                    .getKey(), value, refQueue));
                                return res;
                            }

                            public boolean equals(Object x) {
                                if (!(x instanceof Map.Entry))
                                    return false;
                                Map.Entry e = (Map.Entry) x;
                                Object key = getKey();
                                return key == null ? e.getKey() == null : key
                                    .equals(e.getKey())
                                        && value == null ? e.getValue() == null
                                        : value.equals(e.getValue());
                            }

                            public int hashCode() {
                                Object key = getKey();
                                return (((key == null) ? 0 : key.hashCode()) ^ ((value == null) ? 0
                                        : value.hashCode()));
                            }

                        };
                        return true;
                    }
                    return false;
                }

                public Object next() {
                    if ((next == null) && !hasNext())
                        throw new NoSuchElementException();
                    Entry e = next;
                    next = null;
                    return e;
                }

                public void remove() {
                    it.remove();
                }

            };
        }
    }
}
