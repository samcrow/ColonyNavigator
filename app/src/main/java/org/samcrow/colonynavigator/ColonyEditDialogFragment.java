package org.samcrow.colonynavigator;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import android.view.View;
import android.widget.CheckBox;

import org.samcrow.colonynavigator.data4.Colony;

/**
 * A fragment that creates a dialog that can be used to edit a colony.
 * <p/>
 * When the user presses OK, if the Activity implements ColonyChangeListener,
 * its callback is called with the new colony information.
 *
 * @author Sam Crow
 */
public class ColonyEditDialogFragment extends AppCompatDialogFragment {

    public static ColonyEditDialogFragment newInstance(Colony colony) {
        final ColonyEditDialogFragment fragment = new ColonyEditDialogFragment();
        final Bundle args = new Bundle();

        args.putString("colony_id", colony.getID());
        args.putBoolean("colony_visited", colony.getAttribute("census.visited", false));
        args.putBoolean("colony_active", colony.getAttribute("census.active", false));

        fragment.setArguments(args);

        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Bundle args = getArguments();
        final String colonyId = args.getString("colony_id");
        final boolean colonyVisited = args.getBoolean("colony_visited");
        final boolean colonyActive = args.getBoolean("colony_active");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Colony " + colonyId);

        // Inflate the view from the XML file
        final View view = getActivity().getLayoutInflater().inflate(R.layout.colony_edit, null);
        builder.setView(view);

        final CheckBox visitedBox = (CheckBox) view.findViewById(R.id.visited_checkbox);
        visitedBox.setChecked(colonyVisited);
        final CheckBox activeBox = (CheckBox) view.findViewById(R.id.active_checkbox);
        activeBox.setChecked(colonyActive);

        // Set up buttons
        builder.setPositiveButton(R.string.save_action, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (getActivity() instanceof ColonyChangeListener) {

                    // Save colony
                    Bundle newData = new Bundle();
                    newData.putString("colony_id", colonyId);
                    newData.putBoolean("colony_visited", visitedBox.isChecked());
                    newData.putBoolean("colony_active", activeBox.isChecked());

                    ((ColonyChangeListener) getActivity()).onColonyChanged(newData);
                }
                ColonyEditDialogFragment.this.getDialog().dismiss();
            }
        });

        builder.setNegativeButton(R.string.cancel_action, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ColonyEditDialogFragment.this.getDialog().cancel();
            }
        });

        return builder.create();
    }


    public interface ColonyChangeListener {
        /**
         * Called when a colony has been edited and new information is available.
         *
         * @param colonyData A bundle containing changed information.
         *                   It will always contain the colony ID as an integer with the key "colony_id".
         *                   It may contain the colony's visited state as "colony_visited" and/or
         *                   its active state as "colony_active"
         */
        void onColonyChanged(Bundle colonyData);
    }

}
