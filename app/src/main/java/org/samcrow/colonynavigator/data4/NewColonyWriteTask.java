package org.samcrow.colonynavigator.data4;

import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.provider.DocumentFile;
import android.widget.Toast;

import org.samcrow.colonynavigator.data4.NewColonyWriteTask.Params;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.concurrent.ExecutionException;

/**
 * Writes {@link NewColony} instances to a file
 */
public class NewColonyWriteTask extends AsyncTask<Params, Void, Void> {

    private final Context context;

    public NewColonyWriteTask(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Params... params) {
        final Uri destination = params[0].getDestination();

        final DocumentFile file = DocumentFile.fromSingleUri(context, destination);

        final NewColony[] colonies = params[0].getColonies();
        try (OutputStream out = context.getContentResolver().openOutputStream(destination)) {
            final PrintStream stream = new PrintStream(out);
            stream.println("Name,X,Y,Notes");
            for (NewColony colony : colonies) {
                // Replace line breaks with spaces
                final String notesWithoutLineBreaks = colony.getNotes().replaceAll("\\n", " ");

                stream.print('"');
                stream.print(colony.getName());
                stream.print("\",");
                stream.print(colony.getX());
                stream.print(',');
                stream.print(colony.getY());
                stream.print(",\"");
                stream.print(notesWithoutLineBreaks);
                stream.println('"');

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        try {
            get();
            Toast.makeText(context, "New colonies saved", Toast.LENGTH_SHORT).show();
        } catch (InterruptedException ignored) {

        } catch (ExecutionException e) {
            final Throwable cause = e.getCause();
            if (cause != null) {
                new AlertDialog.Builder(context)
                        .setTitle("Failed to save new colonies")
                        .setMessage(cause.getMessage())
                        .show();
            } else {
                new AlertDialog.Builder(context)
                        .setTitle("Failed to save new colonies")
                        .setMessage(e.getMessage())
                        .show();
            }
        }

    }

    public static class Params {
        private final NewColony[] colonies;
        private final Uri destination;

        public Params(NewColony[] colonies, Uri destination) {
            this.colonies = colonies;
            this.destination = destination;
        }

        public NewColony[] getColonies() {
            return colonies;
        }

        public Uri getDestination() {
            return destination;
        }
    }

}
