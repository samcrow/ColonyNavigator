package org.samcrow.colonynavigator.data4;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the selected colony, and provides notification when it is changed
 *
 * @author samcrow
 */
public class ColonySelection {

    private Colony selectedColony;
    private List<Listener> listeners = new ArrayList<>();

    /**
     * @return The current selected colony, which may be null
     */
    public Colony getSelectedColony() {
        return selectedColony;
    }

    /**
     * Sets the selected colony
     *
     * @param newColony The new selected colony. This may be null.
     */
    public void setSelectedColony(Colony newColony) {

        final Colony oldColony = selectedColony;
        selectedColony = newColony;

        notifyListeners(oldColony, newColony);
    }

    public void addChangeListener(Listener listener) {
        listeners.add(listener);
    }

    private void notifyListeners(Colony oldColony, Colony newColony) {
        for (Listener listener : listeners) {
            listener.selectedColonyChanged(oldColony, newColony);
        }
    }


    /**
     * An interface for something that can be notified when the colony selection changes
     */
    public interface Listener {
        /**
         * Called when the colony selection changes.
         * <p/>
         * When this method is called, the selection will already have been changed and {@link #getSelectedColony()}
         * will return newColony.
         *
         * @param oldColony The colony that was previously selected
         * @param newColony The colony that has been newly selected
         */
        void selectedColonyChanged(Colony oldColony, Colony newColony);
    }
}
