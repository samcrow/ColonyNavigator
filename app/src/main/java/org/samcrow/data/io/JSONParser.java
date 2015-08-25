package org.samcrow.data.io;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.samcrow.data4.Colony;
import org.samcrow.data4.ColonySet;

import java.util.Set;

/**
 * Parses and encodes JSON
 * @author samcrow
 */
public class JSONParser implements Parser<Colony> {

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

	protected static Colony fromJSON(JSONObject json) throws JSONException {
		Colony colony = new Colony(json.getInt("id"));
		colony.setX(json.getDouble("x"));
		colony.setY(json.getDouble("y"));

		colony.setAttribute("census.visited", json.optBoolean("visited", false));
		colony.setAttribute("census.active", json.optBoolean("active", false));

		final Object updatedObject = json.opt("updated");
		if(updatedObject == null || JSONObject.NULL.equals(updatedObject) || !(updatedObject instanceof  String)) {
			// Set to now
			colony.setUpdateTime(DateTime.now());
		}
		else {
			try {
				final DateTime updatedTime = ISODateTimeFormat.dateTimeParser().parseDateTime((String) updatedObject);
				colony.setUpdateTime(updatedTime);
			}
			catch (IllegalArgumentException e) {
				// Could not parse time
				// Set to now
				colony.setUpdateTime(DateTime.now());
			}
		}
		return colony;
	}

	/**
	 * Parse a JSON array of colonies into a set of colonies
	 * @param colonyArray The colonies to parse
	 * @return The parsed colonies
	 */
	public ColonySet parseAll(JSONArray colonyArray) {
		ColonySet colonies = new ColonySet();

		for(int i = 0, max = colonyArray.length(); i < max; i++) {
			try {

				JSONObject colonyObject = colonyArray.getJSONObject(i);
				colonies.put(fromJSON(colonyObject));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return colonies;
	}

	/**
	 * Encode all the colonies in a set into a JSON array of data
	 * @param colonies The colonies to encode
	 * @return The data in JSON array format
	 */
	public JSONArray encodeAll(Set<Colony> colonies) {
		JSONArray array = new JSONArray();

		return array;
	}

	protected static JSONObject toJSON(Colony value) throws JSONException {
		final JSONObject json = new JSONObject();
		json.put("id", value.getID());
		json.put("x", value.getX());
		json.put("y", value.getY());
		if(value.hasAttribute("census.visited")) {
			json.put("visited", value.getAttribute("census.visited"));
		}
		if(value.hasAttribute("census.active")) {
			json.put("active", value.getAttribute("census.active"));
		}

		final String updatedString = ISODateTimeFormat.dateTimeNoMillis().withZoneUTC().print(value.getUpdateTime());
		json.put("updated", updatedString);
		return json;
	}

	@Override
	public String encodeOne(Colony value) {
		try {
			return toJSON(value).toString();
		}
		catch (JSONException e) {
			e.printStackTrace();
			return "{}";
		}
	}

}
