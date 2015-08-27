package org.samcrow.colonynavigator.data4;

import org.joda.time.DateTime;

/**
 * A potentially new colony on the map, added by the user
 */
public class NewColony extends Positioned {

    private final String name;
    private final String notes;
    private final DateTime timeCreated;

    public NewColony(double x, double y, String name, String notes) {
        super(x, y);
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

    @Override
    public String toString() {
        return "New colony " + name + ": \"" + notes + "\"";
    }
}
