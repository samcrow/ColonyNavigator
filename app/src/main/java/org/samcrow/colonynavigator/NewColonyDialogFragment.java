package org.samcrow.colonynavigator;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

/**
 * A dialog allowing the user to create a {@link org.samcrow.colonynavigator.data4.NewColony}
 */
public class NewColonyDialogFragment extends DialogFragment {

	public interface NewColonyListener {
		void createColony(String name, String notes);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);

		builder.setTitle("New colony");
		// Inflate the view from the XML file
		final View view = getActivity().getLayoutInflater().inflate(R.layout.new_colony, null);
		builder.setView(view);

		final EditText nameField = (EditText) view.findViewById(R.id.new_colony_name);
		final EditText notesField = (EditText) view.findViewById(R.id.new_colony_notes);

		// Set up buttons
		builder.setPositiveButton(R.string.save_action, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

				final String name = nameField.getText().toString();
				final String notes = notesField.getText().toString();
				if(name.isEmpty()) {
					showErrorDialog(view.getContext(), "No name entered", "Please enter a name for this colony");
					return;
				}
				if(notes.isEmpty()) {
					showErrorDialog(view.getContext(), "No notes entered", "Please enter a location, and any other relevant information, for this colony");
					return;
				}


				if(getActivity() instanceof NewColonyListener) {
					((NewColonyListener) getActivity()).createColony(name, notes);
				}
				NewColonyDialogFragment.this.getDialog().dismiss();
			}
		});

		builder.setNegativeButton(R.string.cancel_action, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				NewColonyDialogFragment.this.getDialog().cancel();
			}
		});

		return builder.create();
	}

	private void showErrorDialog(Context ctx, String title, String message) {
		new AlertDialog.Builder(ctx)
				.setTitle(title)
				.setMessage(message)
				.setNeutralButton(R.string.ok, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// Nothing
					}
				})
				.show();
	}
}
