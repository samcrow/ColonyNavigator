package org.samcrow.data.io;

/**
 * An interface for a class that can convert between
 * string and in-memory representations of colonies
 *
 * @param <T> The class to parse
 * @author Sam Crow
 */
public interface Parser<T> {

    /**
     * Parse a string representation of one colony into a {@link Colony}.
     * If the string could not be parsed, this method should return <code>null</code>.
     *
     * @param oneString The string to parse
     * @return The parsed colony
     */
    T parseOne(String oneString);

    /**
     * Encode one colony into a string representation
     *
     * @param value The colony to encode
     * @return A string representation of the colony
     */
    String encodeOne(T value);

}
