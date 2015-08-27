package org.samcrow.colonynavigator;

import android.content.Context;

import org.apache.commons.io.IOUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Utilities for handling Mapsforge map files stored in the application as resources
 */
public class MapFileResource {
    private MapFileResource() {}

    /**
     * Returns a File object containing the path to a file containing the specified resource
     * @param resid the ID of the raw resource to get
     * @return a file containing the content of the resource
     */
    public static File getMapFile(Context ctx, int resid) throws IOException {

        final File storedFile = pathForResource(ctx, resid);
        if(storedFile.exists()) {
            return storedFile;
        }
        else {
            InputStream stream = null;
            OutputStream fileOut = null;
            try {
                stream = ctx.getResources().openRawResource(resid);
                fileOut = new FileOutputStream(storedFile);
                IOUtil.copy(stream, fileOut);
                return storedFile;
            }
            finally {
                if(stream != null) { stream.close(); }
                if(fileOut != null) { fileOut.close(); }
            }
        }
    }

    private static File pathForResource(Context ctx, int resid) {
        return new File(ctx.getFilesDir().getAbsolutePath() + "/" + resid + ".resource");
    }
}
