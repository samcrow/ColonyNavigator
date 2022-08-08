package org.samcrow.colonynavigator;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.provider.DocumentFile;
import android.util.Log;

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

    public static FileUris getMemoryCardUris(Context ctx, @Nullable Uri chosenUri) {
        if (chosenUri != null) {
            final FileUris chosen = new FileUris(DocumentFile.fromTreeUri(ctx, chosenUri));
            if (chosen.getCsv() != null || chosen.getJson() != null) {
                return chosen;
            } else {
                return searchMemoryCardUris(ctx);
            }
        } else {
            return searchMemoryCardUris(ctx);
        }
    }

    public static FileUris searchMemoryCardUris(Context ctx) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            final String[] storageNames = {
                    "extSdCard",
                    "6133-3731",
                    "1C45-180D",
                    "22C1-11F5",
                    "3466-3339"
            };
            final UriBuilder[] builders = {
                    new Tab27UriBuilder(),
                    new TabActiveUriBuilder(),
            };

            for (String storageName : storageNames) {
                for (UriBuilder builder : builders) {
                    final Uri storageUri = builder.makeUri(storageName);
                    final FileUris uris = new FileUris(DocumentFile.fromTreeUri(ctx, storageUri));
                    // The storage location is valid if either CSV or JSON file exists
                    if (uris.getCsv() != null || uris.getJson() != null) {
                        return uris;
                    }
                }
            }
            return null;
        } else {
            throw new UnsupportedOperationException("Pre-KitKat URIs not yet implemented");
        }
    }


    private interface UriBuilder {
        Uri makeUri(String storageName);
    }
    private static class Tab27UriBuilder implements UriBuilder {
        @Override
        public Uri makeUri(String storageName) {
            return Uri.parse(String.format("content://com.android.externalstorage.documents/tree/%s%%3A/document/%s%%3A", storageName, storageName));
        }
    }
    private static class TabActiveUriBuilder implements UriBuilder {
        @Override
        public Uri makeUri(String storageName) {
            return Uri.parse(String.format("content://com.android.externalstorage.documents/document/%s%%3A", storageName));
        }
    }

    private static boolean fileExistsAtUri(Context ctx, Uri uri) {
        Log.i("Storage", "Checking for file at " + uri);
        final DocumentFile document = DocumentFile.fromSingleUri(ctx, uri);
        if (document != null) {
            return document.exists() && document.isFile();
        } else {
            return false;
        }
    }

    public static class FileUris {

        private static final String CSV_NAME = "colonies.csv";
        private static final String JSON_NAME = "colonies.json";
        private static final String FOCUS_NAME = "focus_colonies.txt";
        private static final String NEW_COLONIES_NAME = "new_colonies.csv";

        private static final String CSV_MIME = "text/csv";
        private static final String TEXT_MIME = "text/plain";
        private static final String JSON_MIME = "application/json";

        /**
         * The directory containing the four files
         */
        private final DocumentFile mFolder;

        public FileUris(DocumentFile folder) {
            mFolder = folder;
        }

        public DocumentFile getCsv() {
            return mFolder.findFile(CSV_NAME);
        }

        public DocumentFile getJson() {
            return mFolder.findFile(JSON_NAME);
        }

        public DocumentFile createJson() {
            return mFolder.createFile(JSON_MIME, JSON_NAME);
        }

        public DocumentFile getFocusColonies() {
            return mFolder.findFile(FOCUS_NAME);
        }

        public DocumentFile getNewColonies() {
            return mFolder.findFile(NEW_COLONIES_NAME);
        }

        public DocumentFile createNewColonies() {
            return mFolder.createFile(CSV_MIME, NEW_COLONIES_NAME);
        }

        @Override
        public String toString() {
            return "FileUris{" +
                    "mFolder = " + mFolder +
                    '}';
        }
    }
}
