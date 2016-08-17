package org.samcrow.colonynavigator;

import android.content.Context;
import android.os.Environment;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Utilities for storage and file management
 */
public class Storage {
    private Storage() {
    }

    /**
     * Returns a File object containing the path to a file containing the specified resource
     *
     * @param resid the ID of the raw resource to get
     * @return a file containing the content of the resource
     */
    public static File getResourceAsFile(Context ctx, int resid) throws IOException {
        final File storedFile = pathForResource(ctx, resid);
        if (storedFile.exists()) {
            return storedFile;
        } else {
            InputStream stream = null;
            OutputStream fileOut = null;
            try {
                stream = ctx.getResources().openRawResource(resid);
                fileOut = new FileOutputStream(storedFile);
                IOUtils.copy(stream, fileOut);
                return storedFile;
            } finally {
                if (stream != null) {
                    stream.close();
                }
                if (fileOut != null) {
                    fileOut.close();
                }
            }
        }
    }

    private static File pathForResource(Context ctx, int resid) {
        return new File(ctx.getCacheDir().getAbsolutePath(), resid + ".resource");
    }

    /**
     * Tries to find the location of the SD card on the file system. If no known directories
     * are available, returns some other external storage directory.
     *
     * @return a directory for storage, which may be a memory card
     */
    public static File getMemoryCard() {
        File dir = new File("/mnt/extSdCard");
        if (dir.exists() && dir.isDirectory()) {
            return dir;
        }
        dir = new File("/Removable/MicroSD");
        if (dir.exists() && dir.isDirectory()) {
            return dir;
        }
        dir = new File("/storage/extSdCard");
        if (dir.exists() && dir.isDirectory()) {
            return dir;
        }
        dir = new File("/storage/6133-3731");
        if (dir.exists() && dir.isDirectory()) {
            return dir;
        }
        return Environment.getExternalStorageDirectory();
    }
}
