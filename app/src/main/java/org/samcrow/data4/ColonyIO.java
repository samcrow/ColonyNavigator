package org.samcrow.data4;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Provides utilities for colony reading/writing
 */
public class ColonyIO {
    private ColonyIO() {}

    public static JSONObject attributesToJSON(Map<String, Object> attributes) {
        return new JSONObject(attributes);
    }

    public static Map<String, Object> jsonToAttributes(JSONObject json) {
        final Map<String, Object> map = new HashMap<>(json.length());

        for(Iterator<String> iter = json.keys(); iter.hasNext(); ) {
            final String key = iter.next();
            if(!json.isNull(key)) {
                final Object value = json.opt(key);
                if (value instanceof JSONObject) {
                    map.put(key, jsonToAttributes((JSONObject) value));
                } else if (value instanceof JSONArray) {
                    map.put(key, jsonToAttributes((JSONArray) value));
                } else {
                    map.put(key, value);
                }
            }
        }

        return map;
    }

    public static List<Object> jsonToAttributes(JSONArray json) {
        final List<Object> list = new ArrayList<>(json.length());
        for(int i = 0; i < json.length(); i++) {
            if(!json.isNull(i)) {
                final Object value = json.opt(i);
                if (value instanceof JSONObject) {
                    list.add(jsonToAttributes((JSONObject) value));
                } else if (value instanceof JSONArray) {
                    list.add(jsonToAttributes((JSONArray) value));
                } else {
                    list.add(value);
                }
            }
        }
        return list;
    }

}
