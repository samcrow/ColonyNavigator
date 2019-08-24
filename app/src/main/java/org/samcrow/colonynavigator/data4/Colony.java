package org.samcrow.colonynavigator.data4;

import org.joda.time.DateTime;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Represents a colony
 */
public class Colony extends Positioned {


    /**
     * The colony's identifier
     */
    private final String id;
    /**
     * The time this colony was updated
     */
    private DateTime updateTime;
    /**
     * The attributes of this colony
     */
    private Map<String, Object> attributes;
    /**
     * The change listener
     */
    private transient ColonyChangeListener listener = null;

    public Colony(String id) {
        this(id, 0, 0, false);
    }

    public Colony(String id, double x, double y, boolean active) {
        super(x, y);
        this.id = id;
        updateTime = DateTime.now();
        attributes = new HashMap<>();
        this.setAttribute("census.active", active);
    }

    public String getID() {
        return id;
    }

    @Override
    public void setX(double x) {
        super.setX(x);
        markUpdated();
        notifyChanged();
    }

    @Override
    public void setY(double y) {
        super.setY(y);
        markUpdated();
        notifyChanged();
    }

    public DateTime getUpdateTime() {
        return updateTime;
    }

    /**
     * Sets the update time of the colony
     *
     * @param newTime the update time
     */
    public void setUpdateTime(DateTime newTime) {
        notifyChanged();
        updateTime = newTime;
    }

    /**
     * Updates this colony's update time by setting it to the current time
     */
    protected void markUpdated() {
        updateTime = DateTime.now();
    }

    /**
     * Gets an attribute of this colony
     *
     * @param attributeName the name of the attribute
     * @return the specified attribute, or null if this colony
     * does not have the specified attribute
     */
    public Object getAttribute(String attributeName) {
        return attributes.get(attributeName);
    }

    /**
     * Gets an attribute of this colony, returning a default value if this colony does not have
     * the requested attribute
     *
     * @param attributeName the name of the attribute
     * @param defaultValue  the value to return if this colony does not have the requested attribute
     * @param <T>           the type of the attribute
     * @return the value of the requested property, or defaultValue if this colony does not have
     * the requested attribute or the value of the attribute cannot be cast to T
     */
    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String attributeName, T defaultValue) {
        final Object mapValue = attributes.get(attributeName);
        if (mapValue != null) {
            try {
                return (T) mapValue;
            } catch (ClassCastException e) {
                return defaultValue;
            }
        } else {
            return defaultValue;
        }
    }

    public void setAttribute(String name, Object value) {
        if (name == null) {
            throw new NullPointerException("name must not be null");
        }
        if (value == null) {
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

    public Map<String, Object> getAttributes() {
        return new HashMap<>(attributes);
    }

    public void setAttributes(Map<String, Object> attrs) {
        markUpdated();
        notifyChanged();
        attributes = new HashMap<>(attrs);
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

    /**
     * A listener that can be notified when a colony's drawable changes
     *
     * @author samcrow
     */
    public interface ColonyChangeListener {
        void onColonyChanged();
    }

    public static Colony fromNewColony(NewColony newColony) {
        final Colony colony = new Colony(newColony.getName(), newColony.getX(), newColony.getY(), false);
        colony.setAttribute("census.visited", true);
        colony.setAttribute("census.active", true);
        return colony;
    }
}
