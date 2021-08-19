package org.samcrow.colonynavigator.data4;

import android.content.Context;

import org.samcrow.colonynavigator.Storage;
import org.samcrow.data.provider.ColonyProvider;
import org.samcrow.data.provider.MemoryCardDataProvider;

import java.io.IOException;
import java.util.List;

/**
 * A colony provider that uses a new colony database and a memory card provider
 */
public class NewColonyProvider implements ColonyProvider {

    /**
     * Database of new colonies
     */
    private final NewColonyDatabase mDatabase;

    /**
     * Memory card with new colonies
     */
    private final MemoryCardDataProvider mMemoryCard;

    public NewColonyProvider(Context context, Storage.FileUris uris) throws IOException {
        mDatabase = new NewColonyDatabase(context);
        mMemoryCard = new MemoryCardDataProvider(context, uris);
    }

    @Override
    public ColonySet getColonies() {
        final ColonySet colonies = new ColonySet();

        final List<NewColony> newColonies = mDatabase.getNewColonies();
        for (NewColony newColony : newColonies) {
            colonies.put(Colony.fromNewColony(newColony));
        }

        final ColonySet memoryCardColonies = mMemoryCard.getColonies();
        colonies.putAll(memoryCardColonies);

        return colonies;
    }

    @Override
    public void updateColonies() throws UnsupportedOperationException {
        mMemoryCard.updateColonies();
    }

    @Override
    public void updateColony(Colony colony) throws UnsupportedOperationException {
        mMemoryCard.updateColonies();
    }
}
