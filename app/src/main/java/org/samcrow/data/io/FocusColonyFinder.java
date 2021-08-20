package org.samcrow.data.io;

import org.samcrow.colonynavigator.data4.Colony;
import org.samcrow.colonynavigator.data4.ColonySet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Reads colony numbers, one per line, from the a file and marks
 * the identified colonies as focus colonies
 *
 * @author samcrow
 */
public class FocusColonyFinder {

    private final ColonySet colonies;
    private final InputStream focusFile;

    public FocusColonyFinder(InputStream focusFile, ColonySet colonies) {
        this.focusFile = focusFile;
        this.colonies = colonies;
    }

    /**
     * Marks required colonies from the colony set as focused
     */
    public void updateColonies() throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(focusFile))) {
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                if (line.isEmpty()) {
                    continue;
                }

                String colonyId = line.trim();
                final Colony colony = colonies.get(colonyId);
                if (colony != null) {
                    colony.setAttribute("census.focus", true);
                }
            }
        }

    }

}
