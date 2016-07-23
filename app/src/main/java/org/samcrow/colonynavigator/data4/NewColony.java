package org.samcrow.colonynavigator.data4;

import android.os.Parcel;
import android.os.Parcelable;

import org.joda.time.DateTime;

/**
 * A potentially new colony on the map, added by the user
 */
public class NewColony extends Positioned implements Parcelable {

    public static final Parcelable.Creator CREATOR = new Creator();


    private final String name;
    private final String notes;
    private final DateTime timeCreated;
    /**
     * The identifier of this colony, used in the database
     */
    private final int id;

    public NewColony(double x, double y, String name, String notes) {
        this(0, x, y, name, notes);
    }

    public NewColony(int id, double x, double y, String name, String notes) {
        super(x, y);
        this.id = id;
        if (name == null) {
            throw new NullPointerException("name must not be null");
        }
        if (notes == null) {
            throw new NullPointerException("notes must not be null");
        }
        this.name = name;
        this.notes = notes;
        timeCreated = DateTime.now();
    }

    public String getName() {
        return name;
    }

    public String getNotes() {
        return notes;
    }

    public DateTime getTimeCreated() {
        return timeCreated;
    }

    public int getID() {
        return id;
    }

    @Override
    public String toString() {
        return "New colony " + name + " at (" + getX() + ", " + getY() + "): \"" + notes + "\"";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeDouble(getX());
        dest.writeDouble(getY());
        dest.writeString(name);
        dest.writeString(notes);
    }

    private static class Creator implements Parcelable.Creator<NewColony> {

        @Override
        public NewColony createFromParcel(Parcel source) {
            final int id = source.readInt();
            final double x = source.readDouble();
            final double y = source.readDouble();
            final String name = source.readString();
            final String notes = source.readString();
            return new NewColony(id, x, y, name, notes);
        }

        @Override
        public NewColony[] newArray(int size) {
            return new NewColony[size];
        }
    }
}
