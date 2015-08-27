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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

/**
 * Reads/writes JSON data to/from files
 * @author Sam Crow
 */
public class JSONFileParser extends JSONParser implements FileParser {

	protected File file;

	/**
	 * Constructor
	 * @param file The file to read from and write to
	 */
	public JSONFileParser(File file) {
		this.file = file;
	}

	@Override
	public ColonySet parse() {
		ColonySet colonies = new ColonySet();

		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));

			//Read the whole text of the file into a string
			String jsonText = "";
			while(true) {
				String line = reader.readLine();
				if(line == null) {
					break;
				}

				jsonText += line;
			}
			reader.close();

			JSONObject jsonRoot = new JSONObject(jsonText);

			JSONArray colonyArray = jsonRoot.getJSONArray("colonies");

			for(int i = 0, max = colonyArray.length(); i < max; i++) {
				try {
					JSONObject colonyObject = colonyArray.getJSONObject(i);

					colonies.put(fromJSON(colonyObject));
				}
				catch(JSONException e) {
					//If an error with this colony was encountered, move on to the next one
					e.printStackTrace();
					continue;
				}
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}


		return colonies;
	}

	@Override
	public void write(Iterable<? extends Colony> values) {

		boolean deleteResult = file.delete();
		if(!deleteResult) {
			System.err.println("Could not delete file!");
		}

		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}

		JSONObject jsonRoot = new JSONObject();

		JSONArray colonyArray = new JSONArray();

		for(Colony colony : values) {
			try {
				colonyArray.put(toJSON(colony));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		try {
			jsonRoot.put("colonies", colonyArray);
			//Add a comment with some information for humans
			jsonRoot.put("comment", "Serialized into JSON by "+toString()+" at "+ DateTime.now().toString(ISODateTimeFormat.basicDateTime()) +".");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		try {
			PrintStream stream = new PrintStream(file);
			stream.println(jsonRoot.toString());
			stream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

}
