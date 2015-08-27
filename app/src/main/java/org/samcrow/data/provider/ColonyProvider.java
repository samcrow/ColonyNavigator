package org.samcrow.data.provider;

import org.samcrow.colonynavigator.data4.Colony;
import org.samcrow.colonynavigator.data4.ColonySet;

/**
 * An interface for a class that can get colonies and update their information.
 * @author Sam Crow
 */
public interface ColonyProvider {

	/**
	 * Get the colonies.
	 * This method should not block.
	 * @return The colonies, or null if the colonies are not currently available
	 */
	ColonySet getColonies();

	/**
	 * Take all the colonies (the same reference as returned by {@link #getColonies()})
	 * and write them to this provider's persistence mechanism.
	 * This method should not block.
	 * @throws UnsupportedOperationException if this provider does not support
	 * persistent storage
	 */
	void updateColonies() throws UnsupportedOperationException;

	/**
	 * Write the information on a selected colony to the persistence mechanism.
	 * This is similar to {@link #updateColonies()}, but this method may allow
	 * implementations to optimize their handling of partial updates.
	 * This method should not block.
	 * @param colony The colony to update
	 * @throws UnsupportedOperationException if this provider does not support
	 * persistent storage
	 */
	void updateColony(Colony colony) throws UnsupportedOperationException;
}
