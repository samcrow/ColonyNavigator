package org.samcrow.data4;

import org.joda.time.DateTime;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Represents a colony
 */
public class Colony {


    /**
     * A listener that can be notified when a colony's drawable changes
     *
     * @author samcrow
     *
     */
    public interface ColonyChangeListener {
        void onColonyChanged();
    }

    /** The colony's identifier */
    private final int id;

    /** The colony's X-coordinate in meters east of the southwest corner */
    private double x;
    /** The colony's Y-coordinate in meters north of the southwest corner */
    private double y;

    /** The time this colony was updated */
    private DateTime updateTime;

    /**
     * The attributes of this colony
     */
    private Map<String, Object> attributes;

    /** The change listener */
    private transient ColonyChangeListener listener = null;

    public Colony(int id) {
        this.id = id;
        updateTime = DateTime.now();
        attributes = new HashMap<>();
    }

    public Colony(int id, double x, double y, boolean active) {
        this(id);
        this.x = x;
        this.y = y;
        this.setAttribute("census.active", active);
    }

    public int getID() {
        return id;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        markUpdated();
        notifyChanged();
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        markUpdated();
        notifyChanged();
        this.y = y;
    }

    /**
     * Sets the update time of the colony
     * @param newTime the update time
     */
    public void setUpdateTime(DateTime newTime) {
        notifyChanged();
        updateTime = newTime;
    }

    public DateTime getUpdateTime() {
        return updateTime;
    }

    /**
     * Updates this colony's update time by setting it to the current time
     */
    protected void markUpdated() {
        updateTime = DateTime.now();
    }

    /**
     * Gets an attribute of this colony
     * @param attributeName the name of the attribute
     * @return the specified attribute, or null if this colony
     * does not have the specified attribute
     */
    public Object getAttribute(String attributeName) {
        return attributes.get(attributeName);
    }

    public <T> T getAttribute(String attributeName, T defaultValue) {
        final Object mapValue = attributes.get(attributeName);
        if(mapValue != null) {
            try {
                return (T) mapValue;
            }
            catch (ClassCastException e) {
                return defaultValue;
            }
        }
        else {
            return defaultValue;
        }
    }

    public void setAttribute(String name, Object value) {
        if(name == null) {
            throw new NullPointerException("name must not be null");
        }
        if(value == null) {
            throw new NullPointerException("value must not be null");
        }
        markUpdated();
        notifyChanged();
        attributes.put(name, value);
    }

    public boolean hasAttribute(String name) {
        return attributes.containsKey(name);
    }

    public Set<String> attributeNames() {
        return Collections.unmodifiableSet(attributes.keySet());
    }

    public void setAttributes(Map<String, Object> attrs) {
        markUpdated();
        notifyChanged();
        attributes = new HashMap<>(attrs);
    }
    public Map<String, Object> getAttributes() {
        return new HashMap<>(attributes);
    }


    /**
     * Notifies the listener that something has changed
     */
    protected void notifyChanged() {
        if (listener != null) {
            listener.onColonyChanged();
        }
    }
    public void setOnChange(
            ColonyChangeListener changelistener) {
        listener = changelistener;
    }
}
