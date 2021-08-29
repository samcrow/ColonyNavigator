package org.samcrow.colonynavigator;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.samcrow.colonynavigator.data4.NewColony;
import org.samcrow.colonynavigator.data4.NewColonyWriteTask;
import org.samcrow.colonynavigator.data4.NewColonyWriteTask.Params;

/**
 * Displays a list of {@link org.samcrow.colonynavigator.data4.NewColony NewColonies}
 */
public class NewColonyListDialogFragment extends AppCompatDialogFragment {


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("New colonies");
        // Inflate the view from the XML file
        final View view = getActivity().getLayoutInflater().inflate(R.layout.new_colony_list, null);
        builder.setView(view);

        final ListView list = (ListView) view.findViewById(R.id.new_colony_list);

        // Load new colonies from the arguments
        final Bundle args = getArguments();
        final NewColony[] colonies = (NewColony[]) args.getParcelableArray("colonies");
        list.setAdapter(new NewColonyAdapter(colonies));

        // Set up buttons

        final Button exportButton = (Button) view.findViewById(R.id.save_new_colonies_button);
        exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Storage.FileUris uris = Storage.getMemoryCardUris(view.getContext());
                assert uris != null;
                final NewColonyWriteTask task = new NewColonyWriteTask(view.getContext());
                task.execute(new Params(colonies, uris.getNewColonies()));
            }
        });

        builder.setPositiveButton(R.string.save_action, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });


        return builder.create();
    }

    private static class NewColonyAdapter implements ListAdapter {

        private final NewColony[] colonies;

        public NewColonyAdapter(NewColony[] colonies) {
            this.colonies = colonies;
        }

        @Override
        public void registerDataSetObserver(DataSetObserver observer) {
            // Nothing
        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {
            // Nothing
        }

        @Override
        public int getCount() {
            return colonies.length;
        }

        @Override
        public Object getItem(int position) {
            return colonies[position];
        }

        @Override
        public long getItemId(int position) {
            return colonies[position].getID();
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final NewColony colony = colonies[position];
            if (convertView != null && convertView instanceof EditText) {
                ((TextView) convertView).setText(colony.toString());
                return convertView;
            } else {
                final TextView view = new TextView(parent.getContext());
                view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
                view.setPadding(10, 10, 10, 10);
                view.setText(colony.toString());
                return view;
            }
        }

        @Override
        public int getItemViewType(int position) {
            return 0;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public boolean isEmpty() {
            return colonies.length == 0;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return true;
        }

        @Override
        public boolean isEnabled(int position) {
            return true;
        }
    }
}
