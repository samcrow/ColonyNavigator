package org.samcrow.data.io;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.samcrow.colonynavigator.data4.Colony;
import org.samcrow.colonynavigator.data4.ColonySet;

/**
 * Parses and encodes JSON
 *
 * @author samcrow
 */
public class JSONParser implements Parser<Colony> {

    protected static Colony fromJSON(JSONObject json) throws JSONException {
        Colony colony = new Colony(json.getString("id"));
        colony.setX(json.getDouble("x"));
        colony.setY(json.getDouble("y"));

        colony.setAttribute("census.visited", json.optBoolean("visited", false));
        colony.setAttribute("census.active", json.optBoolean("active", false));

        final Object updatedObject = json.opt("modified");
        if (updatedObject == null || JSONObject.NULL.equals(
                updatedObject) || !(updatedObject instanceof String)) {
            // Set to now
            colony.setUpdateTime(DateTime.now());
        } else {
            try {
                final DateTime updatedTime = ISODateTimeFormat.dateTimeParser()
                        .parseDateTime((String) updatedObject);
                colony.setUpdateTime(updatedTime);
            } catch (IllegalArgumentException e) {
                // Could not parse time
                // Set to now
                colony.setUpdateTime(DateTime.now());
            }
        }
        return colony;
    }

    protected static JSONObject toJSON(Colony value) throws JSONException {
        final JSONObject json = new JSONObject();
        // Put the ID as an integer if it is valid
        try {
            json.put("id", Integer.valueOf(value.getID()));
        } catch (NumberFormatException e) {
            json.put("id", value.getID());
        }

        json.put("x", value.getX());
        json.put("y", value.getY());
        if (value.hasAttribute("census.visited")) {
            json.put("visited", value.getAttribute("census.visited"));
        }
        if (value.hasAttribute("census.active")) {
            json.put("active", value.getAttribute("census.active"));
        }

        final String updatedString = ISODateTimeFormat.dateTimeNoMillis()
                .withZoneUTC()
                .print(value.getUpdateTime());
        json.put("modified", updatedString);
        return json;
    }

    @Override
    public Colony parseOne(String oneString) {
        try {
            final JSONObject json = new JSONObject(oneString);
            return fromJSON(json);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Parse a JSON array of colonies into a set of colonies
     *
     * @param colonyArray The colonies to parse
     * @return The parsed colonies
     */
    public ColonySet parseAll(JSONArray colonyArray) {
        ColonySet colonies = new ColonySet();

        for (int i = 0, max = colonyArray.length(); i < max; i++) {
            try {

                JSONObject colonyObject = colonyArray.getJSONObject(i);
                colonies.put(fromJSON(colonyObject));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return colonies;
    }

    @Override
    public String encodeOne(Colony value) {
        try {
            return toJSON(value).toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return "{}";
        }
    }

}
