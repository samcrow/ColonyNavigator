package org.samcrow.colonynavigator.data4;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides database access for new colonies
 */
public class NewColonyDatabase extends SQLiteOpenHelper {

	private static final String TABLE_NAME = "new_colonies";
	private static final int VERSION = 1;
	private static final String TAG = NewColonyDatabase.class.getSimpleName();

	public NewColonyDatabase(Context ctx) {
		super(ctx, TABLE_NAME, null, VERSION);
	}

	public List<NewColony> getNewColonies() throws SQLException {
		SQLiteDatabase db = null;
		Cursor result = null;
		try {
			db = getWritableDatabase();
			result = db.query(TABLE_NAME, null, null, null, null, null, null);
			final List<NewColony> colonies = new ArrayList<>(result.getCount());
			while(result.moveToNext()) {
				final double x = result.getDouble(result.getColumnIndex("x"));
				final double y = result.getDouble(result.getColumnIndex("y"));
				final String name = result.getString(result.getColumnIndex("name"));
				final String notes = result.getString(result.getColumnIndex("notes"));
				final NewColony colony = new NewColony(x, y, name, notes);
				colonies.add(colony);
			}
			return colonies;
		}
		finally {
			if(db != null) {
				db.close();
			}
			if(result != null) {
				result.close();
			}
		}
	}

	public void insertNewColony(NewColony colony) throws SQLException {
		if(colony == null) {
			throw new NullPointerException("colony must not be null");
		}
		SQLiteDatabase db = null;
		try {
			db = getWritableDatabase();
			final ContentValues values = new ContentValues();
			values.put("x", colony.getX());
			values.put("y", colony.getY());
			values.put("name", colony.getName());
			values.put("notes", colony.getNotes());
			db.insert(TABLE_NAME, null, values);
		}
		finally {
			if(db != null) {
				db.close();
			}
		}
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE \"" + TABLE_NAME + "\" ( \"id\" INTEGER PRIMARY KEY, \"x\" REAL NOT NULL DEFAULT 0," +
				" \"y\" REAL NOT NULL DEFAULT 0, \"name\" TEXT NOT NULL DEFAULT \"\"," +
				" \"notes\" TEXT NOT NULL DEFAULT \"\" )", new Object[0]);
	}


	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Implement later when other versions are available
	}
}
