package org.samcrow.data.io;

import org.samcrow.colonynavigator.data4.Colony;
import org.samcrow.colonynavigator.data4.ColonySet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Parses CSV files
 *
 * @author Sam Crow
 */
public class CSVFileParser extends CSVParser implements FileParser {

    protected File file;

    /**
     * Constructor
     *
     * @param file The file to read from and write to
     */
    public CSVFileParser(File file) {
        this.file = file;
    }

    public static ColonySet parseFromStream(InputStream in) throws IOException {
        ColonySet colonies = new ColonySet();

        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(in));

            //Parse each line
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }

                Colony colony = parseOneStatic(line);
                //colony might be null if the line couldn't be parsed.
                //Add it only if it was parsed successfully.
                if (colony != null) {
                    colonies.put(colony);
                }

            }

            reader.close();
        } catch (FileNotFoundException e) {
            throw new IOException(e);
        }

        return colonies;
    }

    public static void writeToStream(OutputStream out, Iterable<? extends Colony> values) throws IOException {
        PrintStream stream = new PrintStream(out);

        for (Colony colony : values) {
            stream.println(encodeOneStatic(colony));
        }
    }

    @Override
    public ColonySet parse() throws IOException {
        return parseFromStream(new FileInputStream(this.file));
    }

    @Override
    public void write(Iterable<? extends Colony> values) throws IOException {
        //Delete the file, if it exists, so that it can be rewritten from the beginning
        final boolean deleteSuccess = file.delete();
        if (!deleteSuccess) {
            throw new IOException("Could not delete file");
        }
        file.createNewFile();

        try {
            PrintStream stream = new PrintStream(file);
            try {
                writeToStream(stream, values);
            } finally {
                stream.close();
            }

        } catch (FileNotFoundException e) {
            throw new IOException(e);
        }

    }

}
