package org.samcrow.colonynavigator.data4;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Provides access to a database of colonies
 */
public class ColonyDatabase extends SQLiteOpenHelper {

    /**
     * Database schema version
     */
    private static final int VERSION = 1;
    /*
     * Schema change log
     *
     * Version 1: Created
     */

    private static final String TABLE_NAME = "colonies";

    public ColonyDatabase(Context context) {
        super(context, TABLE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.beginTransaction();
            db.execSQL("CREATE TABLE ? (\"id\" integer primary key NOT NULL, \"x\" double NOT NULL, " +
                    "\"y\" double NOT NULL, \"data\" text NOT NULL DEFAULT \"{}\", \"update_time\" " +
                    "text NOT NULL DEFAULT \"\" );", new Object[]{TABLE_NAME});
        } finally {
            db.endTransaction();
        }
    }

    public Colony getColony(int id) {
        final SQLiteDatabase db = getWritableDatabase();
        Cursor result = null;
        try {
            result = db.query(TABLE_NAME, new String[]{"x", "y", "data", "update_time"}, "\"id\" = ?",
                    new String[]{Integer.toString(id)}, null, null, null, null);

            if (!result.moveToFirst()) {
                // No such colony
                return null;
            }

            final Colony colony = new Colony(id);
            colony.setX(result.getDouble(result.getColumnIndex("x")));
            colony.setY(result.getDouble(result.getColumnIndex("y")));
            final String updateTimeString = result.getString(result.getColumnIndex("update_time"));
            try {
                final DateTimeFormatter format = ISODateTimeFormat.dateTimeNoMillis();
                final DateTime updateTime = format.parseDateTime(updateTimeString);
                colony.setUpdateTime(updateTime);
            }
            catch (IllegalArgumentException e) {
                // Could not parse time
                colony.setUpdateTime(DateTime.now());
            }

            final String dataString = result.getString(result.getColumnIndex("data"));
            try {
                final JSONObject data = new JSONObject(dataString);
                colony.setAttributes(ColonyIO.jsonToAttributes(data));
            }
            catch (JSONException e) {
                // Invalid JSON
                // Replace with an empty object
                final ContentValues clearData = new ContentValues();
                clearData.put("data", "{}");
                db.update(TABLE_NAME, clearData, "\"id\" = ?", new String[]{Integer.toString(id)});
            }

            return colony;
        }
        finally {
            if(result != null) {
                result.close();
            }
            close();
        }
    }



    public ColonySet getColonies() {
        final SQLiteDatabase db = getWritableDatabase();
        Cursor result = null;
        try {
            result = db.query(TABLE_NAME, new String[]{"id", "x", "y", "data", "update_time"}, null, null, null, null, null, null);
            final ColonySet set = new ColonySet();
            while(result.moveToNext()) {
                final int id = result.getInt(result.getColumnIndex("id"));
                final Colony colony = new Colony(id);
                colony.setX(result.getDouble(result.getColumnIndex("x")));
                colony.setY(result.getDouble(result.getColumnIndex("y")));

                final String updateTimeString = result.getString(result.getColumnIndex("update_time"));
                try {
                    final DateTimeFormatter format = ISODateTimeFormat.dateTimeNoMillis();
                    final DateTime updateTime = format.parseDateTime(updateTimeString);
                    colony.setUpdateTime(updateTime);
                }
                catch (IllegalArgumentException e) {
                    // Could not parse time
                    colony.setUpdateTime(DateTime.now());
                }

                try {
                    final String dataString = result.getString(result.getColumnIndex("update_time"));
                    final JSONObject data = new JSONObject(dataString);
                    colony.setAttributes(ColonyIO.jsonToAttributes(data));
                }
                catch (JSONException e) {
                    // Invalid JSON
                    // Delete it from the database
                    final ContentValues clearData = new ContentValues();
                    clearData.put("data", "");
                    db.update(TABLE_NAME, clearData, "\"id\" = ?", new String[]{Integer.toString(id)});
                }
                set.put(colony);
            }

            return set;
        }
        finally {
            if(result != null) {
                result.close();
            }
            close();
        }
    }

    public void persist(Colony colony) {
        final SQLiteDatabase db = getWritableDatabase();
        try {
            final ContentValues values = new ContentValues();
            values.put("id", colony.getID());
            values.put("x", colony.getX());
            values.put("y", colony.getY());

            // Format in ISO 8601 in the UTC time zone
            final DateTimeFormatter format = ISODateTimeFormat.dateTimeNoMillis().withZoneUTC();
            final String timeString = format.print(colony.getUpdateTime());
            values.put("update_time", timeString);

            try {
                final JSONObject propertiesJSON = ColonyIO.attributesToJSON(colony.getAttributes());
                final byte[] propertiesBytes = propertiesJSON.toString(0).getBytes();
                values.put("data", propertiesBytes);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(colonyExists(db, colony.getID())) {
                db.update(TABLE_NAME, null, "\"id\" = ?", new String[]{Integer.toString(colony.getID())});
            }
            else {
                db.insert(TABLE_NAME, null, values);
            }
        }
        finally {
            close();
        }
    }

    private boolean colonyExists(SQLiteDatabase db, int id) {
        final Cursor result = db.query(TABLE_NAME, new String[]{"x", "y", "data"}, "\"id\" = ?",
                new String[]{Integer.toString(id)}, null, null, null, null);
        return result.moveToFirst();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Implement later when new versions are available
    }
}
