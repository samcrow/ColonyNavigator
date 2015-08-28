package org.samcrow.data.io;

import org.samcrow.colonynavigator.data4.Colony;
import org.samcrow.colonynavigator.data4.ColonySet;

import java.io.File;
import java.io.IOException;
import java.util.Set;

/**
 * Interface for a class that can parse a data source into a set of colonies
 * and write a set of colonies to a file.
 * It is suggested that classes implementing this interface
 * have a constructor that takes a {@link File} as an argument
 * to specify the file that should be read from and written to.
 * @author Sam Crow
 */
public interface FileParser extends Parser<Colony> {

	/**
	 * Parse the data source into a set of objects.
	 * Implementations of this method should try to
	 * ignore any syntax errors and continue parsing
	 * the valid parts of the source. If parsing
	 * fails entirely, this method should return an
	 * empty {@link Set}.
	 * @return The colonies
	 */
	ColonySet parse() throws IOException;

	/**
	 * Write all of a set of objects to the data source
	 * @param values The colonies to write
	 */
	void write(Iterable<? extends Colony> values) throws IOException;

}
