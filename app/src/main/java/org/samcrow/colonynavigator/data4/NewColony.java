package org.samcrow.colonynavigator.data4;

import org.joda.time.DateTime;

/**
 * A potentially new colony on the map, added by the user
 */
public class NewColony extends Positioned {

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
        if(name == null) {
            throw new NullPointerException("name must not be null");
        }
        if(notes == null) {
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
}
