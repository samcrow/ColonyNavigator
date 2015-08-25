package org.samcrow.data4;

import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * A set of colonies
 */
public class ColonySet implements Iterable<Colony> {

    private final LinkedHashMap<Integer, Colony> map;

    /**
     * Creates a new empty set of colonies
     */
    public ColonySet() {
        map = new LinkedHashMap<Integer, Colony>();
    }

    /**
     * Returns an iterator over the colonies in this set
     * @return an iterator
     */
    @Override
    public Iterator<Colony> iterator() {
        return map.values().iterator();
    }

    /**
     * Locates and returns a colony with the specified identifier
     * @param id the ID of the colony to find
     * @return the matching colony, or null if no colony with that ID is available
     */
    public Colony get(int id) {
        return map.get(id);
    }

    /**
     * Adds a colony to this set
     *
     * If a colony with the same ID is already in this set, it is replaced with the provided colony
     *
     * @param colony the colony to store. Must not be null.
     * @return the colony that the provided colony replaced, or null if no colony was replaced
     */
    public Colony put(Colony colony) {
        if(colony == null) {
            throw new NullPointerException("Colony must not be null");
        }
        return map.put(colony.getID(), colony);
    }
}
