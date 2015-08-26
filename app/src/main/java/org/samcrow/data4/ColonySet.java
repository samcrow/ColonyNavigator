package org.samcrow.data4;

import java.util.Collection;
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
        map = new LinkedHashMap<>();
    }

    /**
     * Creates a new set of colonies with colonies from the provided collection
     * @param collection a collection containing the colonies to copy
     * @throws NullPointerException if any colony in the collection is null
     */
    public ColonySet(Collection<? extends Colony> collection) {
        this();
        for(Colony colony : collection) {
            if(colony == null) {
                throw new NullPointerException("No colony may be null");
            }
            map.put(colony.getID(), colony);
        }
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

    /**
     * Adds all colonies from a collection to this set
     * @param collection the collection of colonies to add
     * @throws NullPointerException if any colony in the collection is null
     */
    public void putAll(Collection<? extends Colony> collection) {
        for(Colony colony : collection) {
            put(colony);
        }
    }
    /**
     * Adds all colonies from another set to this set
     * @param other the collection of colonies to add
     */
    public void putAll(ColonySet other) {
        for(Colony colony : other) {
            put(colony);
        }
    }

    /**
     * Removes all colonies from this set
     */
    public void clear() {
        map.clear();
    }
}
