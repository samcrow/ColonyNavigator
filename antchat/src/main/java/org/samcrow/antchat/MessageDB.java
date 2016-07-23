package org.samcrow.antchat;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.samcrow.antchat.Message.Direction;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides database storage for {@link Message Messages}
 */
public class MessageDB extends SQLiteOpenHelper {

    private static final String TABLE_NAME = "messages";
    private static final int VERSION = 1;

    public MessageDB(Context context) {
        super(context, TABLE_NAME, null, VERSION);
    }

    public List<Message> getMessages() {
        SQLiteDatabase db = null;
        Cursor result = null;
        try {
            final List<Message> messages = new ArrayList<>();

            db = getWritableDatabase();
            result = db.query(TABLE_NAME, null, null, null, null, null, null);

            while (result.moveToNext()) {
                try {
                    final DateTime time = ISODateTimeFormat.dateTimeParser()
                            .parseDateTime(result.getString(result.getColumnIndex("time")));
                    final String text = result.getString(result.getColumnIndex("text"));
                    final Direction direction = Direction.valueOf(
                            result.getString(result.getColumnIndex("direction")));

                    messages.add(new Message(text, time, direction));
                } catch (IllegalArgumentException e) {
                    // Invalid value in database
                    // continue
                }
            }

            return messages;
        } finally {
            if (result != null) {
                result.close();
            }
            if (db != null) {
                db.close();
            }
        }
    }

    public void insertMessage(Message message) {
        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();

            final ContentValues values = new ContentValues();
            values.put("time", ISODateTimeFormat.dateTime().print(message.getTime()));
            values.put("text", message.getText());
            values.put("direction", message.getDirection().name());

            db.insert(TABLE_NAME, null, values);
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    public void deleteAllMessages() {
        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();
            db.execSQL("DELETE FROM \"" + TABLE_NAME + "\"");
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE \"" + TABLE_NAME + "\" (" +
                "\"id\" INTEGER PRIMARY KEY," +
                "\"time\" TEXT NOT NULL DEFAULT \"\"," +
                "\"text\" TEXT NOT NULL DEFAULT \"\"," +
                "\"direction\" TEXT NOT NULL DEFAULT \"Received\" )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Implement when later versions are available
    }
}
