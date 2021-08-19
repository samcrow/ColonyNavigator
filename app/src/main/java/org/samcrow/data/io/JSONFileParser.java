package org.samcrow.data.io;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.samcrow.colonynavigator.data4.Colony;
import org.samcrow.colonynavigator.data4.ColonySet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Reads/writes JSON data to/from files
 *
 * @author Sam Crow
 */
public class JSONFileParser extends JSONParser implements FileParser {

    protected File file;

    /**
     * Constructor
     *
     * @param file The file to read from and write to
     */
    public JSONFileParser(File file) {
        this.file = file;
    }

    public static ColonySet parseFromStream(InputStream in) throws IOException {
        try {
            ColonySet colonies = new ColonySet();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            //Read the whole text of the file into a string
            StringBuilder jsonText = new StringBuilder();
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }

                jsonText.append(line);
            }
            reader.close();

            JSONObject jsonRoot = new JSONObject(jsonText.toString());

            JSONArray colonyArray = jsonRoot.getJSONArray("colonies");

            for (int i = 0, max = colonyArray.length(); i < max; i++) {
                try {
                    JSONObject colonyObject = colonyArray.getJSONObject(i);

                    colonies.put(fromJSON(colonyObject));
                } catch (JSONException e) {
                    //If an error with this colony was encountered, move on to the next one
                    e.printStackTrace();
                }
            }
            return colonies;
        } catch (JSONException e) {
            throw new IOException("Could not write JSON", e);
        }
    }

    public static void writeToStream(OutputStream out, Iterable<? extends Colony> values) throws IOException {
        JSONObject jsonRoot = new JSONObject();
        JSONArray colonyArray = new JSONArray();

        for (Colony colony : values) {
            try {
                colonyArray.put(toJSON(colony));
            } catch (JSONException e) {
                throw new IOException("Could not write colony", e);
            }
        }

        try {
            jsonRoot.put("colonies", colonyArray);
            //Add a comment with some information for humans
            jsonRoot.put("comment",
                    "Serialized into JSON by JSONFileParser at " + DateTime.now()
                            .toString(ISODateTimeFormat.basicDateTime()) + ".");
        } catch (JSONException e) {
            throw new IOException("Could not create top-level JSON", e);
        }
        PrintStream stream = new PrintStream(out);
        stream.println(jsonRoot.toString());
    }

    @Override
    public ColonySet parse() throws IOException {
        try (final InputStream stream = new FileInputStream(file)) {
            return parseFromStream(stream);
        }
    }

    @Override
    public void write(Iterable<? extends Colony> values) throws IOException {

        boolean deleteResult = file.delete();
        if (!deleteResult) {
            System.err.println("Could not delete file!");
        }

        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (FileOutputStream stream = new FileOutputStream(file)) {
            writeToStream(stream, values);
        }
    }

}
