package org.apache.log4j;

import android.util.Log;

/**
 * A wrapper for the Android logging APIs that provides a log4j-like API
 */
public class Logger {

    /**
     * The tag provided to the Android logging system
     */
    private final String tag;

    private Logger(String tag) {
        this.tag = tag;
    }

    public static Logger getLogger(Class userClass) {
        return getLogger(userClass.getSimpleName());
    }

    public static Logger getLogger(String name) {
        return new Logger(name);
    }

    private static String objectToString(Object o) {
        if (o != null) {
            return o.toString();
        } else {
            return "null";
        }
    }

    public void debug(Object message) {
        Log.d(tag, objectToString(message));
    }

    public void debug(Object message, Throwable t) {
        Log.d(tag, objectToString(message), t);
    }

    public void error(Object message) {
        Log.e(tag, objectToString(message));
    }

    public void error(Object message, Throwable t) {
        Log.e(tag, objectToString(message), t);
    }

    public void fatal(Object message) {
        Log.wtf(tag, objectToString(message));
    }

    public void fatal(Object message, Throwable t) {
        Log.wtf(tag, objectToString(message), t);
    }

    public void info(Object message) {
        Log.i(tag, objectToString(message));
    }

    public void info(Object message, Throwable t) {
        Log.i(tag, objectToString(message), t);
    }

    public void warn(Object message) {
        Log.w(tag, objectToString(message));
    }

    public void warn(Object message, Throwable t) {
        Log.w(tag, objectToString(message), t);
    }

    public boolean isInfoEnabled() {
        return Log.isLoggable(tag, Log.INFO);
    }

    public boolean isDebugEnabled() {
        return Log.isLoggable(tag, Log.DEBUG);
    }
}
