package org.samcrow.data.provider;

import android.Manifest.permission;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import androidx.core.content.ContextCompat;
import android.widget.Toast;

import org.samcrow.colonynavigator.R;
import org.samcrow.colonynavigator.data4.Colony;
import org.samcrow.colonynavigator.data4.ColonySet;
import org.samcrow.data.io.CSVFileParser;
import org.samcrow.data.io.FileParser;
import org.samcrow.data.io.FocusColonyFinder;
import org.samcrow.data.io.JSONFileParser;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * Provides colonies from data stored on the memory card.
 * This class first looks for a CSV file named colonies.csv in the directory specified by {@link #cardPath}.
 * It parses that data.
 * Then it looks for a JSON file named colonies.json in the same directory and parses that data.
 * In the event of any conflict between the two files, the version in colonies.json takes precedence.
 * <p/>
 * When writing colony data, this implementation writes it to colonies.json. It does not modify colonies.csv.
 *
 * @author Sam Crow
 */
public class MemoryCardDataProvider implements ColonyProvider {

    /**
     * The name, including the file extension, of the CSV file to use
     */
    private static final String kCsvFileName = "/colonies.csv";
    /**
     * The name, including the file extension, of the JSON file to use
     */
    private static final String kJsonFileName = "/colonies.json";
    private final File cardPath;
    private final Context context;
    private ColonySet colonies = new ColonySet();

    public MemoryCardDataProvider(Context context, File cardPath) throws IOException {
        this.context = context;
        this.cardPath = cardPath;
        //Create the directory if it doesn't already exist
        cardPath.mkdirs();

        File csvFile = new File(cardPath.getAbsolutePath() + kCsvFileName);
        File jsonFile = new File(cardPath.getAbsolutePath() + kJsonFileName);

        //Case 1: Application hasn't been run before
        //colonies.csv exists, colonies.json does not
        if (csvFile.exists() && !jsonFile.exists()) {

            //Read the CSV and get the colonies into memory
            FileParser csvParser = new CSVFileParser(csvFile);
            colonies = csvParser.parse();

            //Write the JSON file from memory
            FileParser jsonParser = new JSONFileParser(jsonFile);
            jsonParser.write(colonies);
        }

        //Case 2: both files exist
        else if (csvFile.exists() && jsonFile.exists()) {


            FileParser csvParser = new CSVFileParser(csvFile);
            ColonySet csvColonies = csvParser.parse();

            //Write the JSON file from memory
            FileParser jsonParser = new JSONFileParser(jsonFile);
            ColonySet jsonColonies = jsonParser.parse();

            //Put into memory the colonies from the CSV updated with colonies from the JSON file
            colonies = extend(csvColonies, jsonColonies);

            //Write the JSON file from memory
            jsonParser.write(colonies);
        }

        //Cases 3: CSV doesn't exist, JSON does
        else if (!csvFile.exists() && jsonFile.exists()) {
            //Use the JSON file
            FileParser jsonParser = new JSONFileParser(jsonFile);
            colonies.clear();
            colonies.putAll(jsonParser.parse());
        } else {
            String message = "Neither " + csvFile.getAbsolutePath() + " or " + jsonFile.getAbsolutePath() + " exists! Failed to get colonies from the memory card.";
            System.err.println(message);
        }


        //Look for focus_colonies.txt
        File focusFile = new File(cardPath, "focus_colonies.txt");
        if (focusFile.exists() && focusFile.canRead()) {
            try {
                new FocusColonyFinder(focusFile, colonies).updateColonies();
            } catch (IOException e) {
                System.err.println("Could not read focus colonies file");
                e.printStackTrace();
            }
        }
    }

    private static Throwable ultimateCause(Throwable ex) {
        while (ex.getCause() != null) {
            ex = ex.getCause();
        }
        return ex;
    }

    /* (non-Javadoc)
     * @see org.samcrow.data.provider.ColonyProvider#getColonies()
     */
    @Override
    public ColonySet getColonies() {
        return colonies;
    }

    /* (non-Javadoc)
     * @see org.samcrow.data.provider.ColonyProvider#updateColonies()
     */
    @Override
    public void updateColonies() throws UnsupportedOperationException {
        new FileWriteTask().execute();
    }

    /* (non-Javadoc)
     * @see org.samcrow.data.provider.ColonyProvider#updateColony(org.samcrow.data.Colony)
     */
    @Override
    public void updateColony(Colony colony)
            throws UnsupportedOperationException {
        new FileWriteTask().execute();
    }

    /**
     * Extend a set of colonies to reflect changes from a supplemental set
     * This is based on jQuery's <code>jQuery.extend()</code> function.
     * <p/>
     * This method will create and return a new set with the following:
     * <ul>
     * <li>Every colony in supplement but not base included as-is</li>
     * <li>Every colony in base but not supplement included as-is</li>
     * <li>For every colony in both sets, the copy from base will be ignored
     * and the copy from supplement will be used</li>
     * </ul>
     * Colonies are considered equal if their IDs as returned by {@link Colony#getID()}
     * are the same.
     *
     * @param base       The base set of colonies
     * @param supplement The supplement set of colonies
     * @return A new set of colonies reflecting the changes to base made by supplement.
     * This set will contain references to the same colony objects referred to by
     * the input sets.
     */
    private ColonySet extend(ColonySet base, ColonySet supplement) {
        //Note: base and supplement contain references to different colony objects with the same IDs

        ColonySet finalSet = new ColonySet();

        for (Colony colony : base) {
            String id = colony.getID();

            Colony supplementColony = supplement.get(id);
            if (supplementColony != null) {
                //It's in the supplement set, so just use the version from the supplement
                finalSet.put(supplementColony);
            } else {
                //This colony isn't in the supplement. Use the version from the base.
                finalSet.put(colony);
            }
        }
        //Add every colony that's in the supplement
        for (Colony supplementColony : supplement) {
            String id = supplementColony.getID();

            Colony baseColony = base.get(id);
            if (baseColony == null) {
                //If this colony is in the base set, it's already been added.
                //Here, it isn't in the base set, so it's added to the final set.
                finalSet.put(supplementColony);
            }
        }

        return finalSet;
    }

    /**
     * A task that writes the colonies to the JSON file
     *
     * @author Sam Crow
     */
    private class FileWriteTask extends AsyncTask<Void, Void, Void> {

        @Override
        public Void doInBackground(Void... args) {
            File file = new File(cardPath + kJsonFileName);

            // Check permissions
            if (ContextCompat.checkSelfPermission(context, permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                throw new IllegalStateException("No permission to write external storage");
            }

            FileParser parser = new JSONFileParser(file);
            try {
                parser.write(colonies);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            try {
                get();
                Toast.makeText(context, "Saved colonies", Toast.LENGTH_SHORT).show();
            } catch (InterruptedException e) {
                // Nothing
            } catch (ExecutionException e) {
                new AlertDialog.Builder(context)
                        .setTitle("Failed to save colonies")
                        .setMessage(ultimateCause(e).getMessage())
                        .setNeutralButton(R.string.ok, new OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
            }
        }
    }
}
