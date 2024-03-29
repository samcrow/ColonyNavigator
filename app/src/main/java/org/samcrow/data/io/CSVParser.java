package org.samcrow.data.io;

import org.samcrow.colonynavigator.data4.Colony;

/**
 * Parses/encodes comma-delimited CSV representations of colonies
 *
 * @author Sam Crow
 */
public class CSVParser implements Parser<Colony> {

    /**
     * The character that separates data fields
     */
    protected static final char separator = ',';

    /**
     * Parse a line of CSV into a colony object.
     * The line must contain the fields id, x, y
     */
    @Override
    public Colony parseOne(String line) {
        return parseOneStatic(line);
    }


    protected static Colony parseOneStatic(String line) {
        line = line.trim();
        //Split the line into parts separated by commas
        String[] parts = line.split("\\s*,\\s*");
        try {

            String colonyId = parts[0];
            int x = Integer.parseInt(parts[1]);
            int y = Integer.parseInt(parts[2]);

            //Ignore active, assume each colony is inactive for the census
            //			//Active if part 3 is A (case insensitive), otherwise false
            //			boolean active = parts[3].compareToIgnoreCase("A") == 0;

            final Colony colony = new Colony(colonyId);
            colony.setX(x);
            colony.setY(y);
            colony.setAttribute("census.active", false);
            return colony;
        } catch (Throwable e) {
            //If anything went wrong, return null
            return null;
        }
    }

    /* (non-Javadoc)
     * @see org.samcrow.data.io.Parser#encodeOne(org.samcrow.data.Colony)
     */
    @Override
    public String encodeOne(Colony colony) {
        return encodeOneStatic(colony);
    }


    protected static String encodeOneStatic(Colony colony) {

        int xInt = (int) Math.round(colony.getX());
        int yInt = (int) Math.round(colony.getY());

        char activeChar = ((boolean) colony.getAttribute("census.active")) ? 'A' : ' ';

        //Format: id,x,y,active,,
        return String.valueOf(
                colony.getID()) + separator + xInt + separator + yInt + separator + activeChar + separator + separator;
    }

}
