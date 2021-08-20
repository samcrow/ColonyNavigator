package org.samcrow.colonynavigator;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.provider.DocumentFile;

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
            try (InputStream stream = ctx.getResources().openRawResource(resid); OutputStream fileOut = new FileOutputStream(storedFile)) {
                IOUtils.copy(stream, fileOut);
                return storedFile;
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
        dir = new File("/storage/1C45-180D");
        if (dir.exists() && dir.isDirectory()) {
            return dir;
        }
        dir = new File("/storage/22C1-11F5");
        if (dir.exists() && dir.isDirectory()) {
            return dir;
        }
        return Environment.getExternalStorageDirectory();
    }

    public static FileUris getMemoryCardUris(Context ctx) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            final String[] storageNames = {
                    "extSdCard",
                    "6133-3731",
                    "1C45-180D",
                    "22C1-11F5"
            };
            for (String storageName : storageNames) {
                final Uri csvUri = makeStorageUri(storageName, "colonies.csv");
                final Uri jsonUri = makeStorageUri(storageName, "colonies.json");
                final Uri focusColoniesUri = makeStorageUri(storageName, "focus_colonies.txt");
                if (fileExistsAtUri(ctx, csvUri) || fileExistsAtUri(ctx, jsonUri)) {
                    return new FileUris(csvUri, jsonUri, focusColoniesUri);
                }
            }
            return null;
        } else {
            throw new UnsupportedOperationException("Pre-KitKat URIs not yet implemented");
        }
    }

    private static Uri makeStorageUri(String storageName, String fileName) {
        return Uri.parse(String.format("content://com.android.externalstorage.documents/tree/%s%%3A/document/%s%%3A%s", storageName, storageName, fileName));
    }

    private static boolean fileExistsAtUri(Context ctx, Uri uri) {
        final DocumentFile document = DocumentFile.fromSingleUri(ctx, uri);
        if (document != null) {
            return document.exists() && document.isFile();
        } else {
            return false;
        }
    }

    public static class FileUris {
        private final Uri mCsv;
        private final Uri mJson;
        private final Uri mFocusColonies;

        public FileUris(Uri csv, Uri json, Uri focusColonies) {
            mCsv = csv;
            mJson = json;
            mFocusColonies = focusColonies;
        }

        public Uri getCsv() {
            return mCsv;
        }

        public Uri getJson() {
            return mJson;
        }

        public Uri getFocusColonies() {
            return mFocusColonies;
        }

        @Override
        public String toString() {
            return "FileUris{" +
                    "csv=" + mCsv +
                    ", json=" + mJson +
                    ", focusColonies=" + mFocusColonies +
                    '}';
        }
    }
}
