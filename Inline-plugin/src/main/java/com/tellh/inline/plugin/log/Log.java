package com.tellh.inline.plugin.log;


import com.tellh.inline.plugin.log.Impl.SystemLoggerImpl;


public class Log {
    private static ILogger logger = new SystemLoggerImpl();
    private static Level level = Level.INFO;
    public static final String DEFAULT_TAG = "Inline";

    public static void setLevel(Level l) {
        level = l;
    }

    public static void setImpl(ILogger l) {
        logger = l;
    }

    public static void d(String msg) {
        if (level.compareTo(Level.DEBUG) <= 0) {
            logger.d(DEFAULT_TAG, msg);
        }
    }

    public static void i(String msg) {
        if (level.compareTo(Level.INFO) <= 0) {
            logger.i(DEFAULT_TAG, msg);
        }
    }

    public static void w(String msg) {
        w(msg, null);
    }

    public static void w(String msg, Throwable t) {
        if (level.compareTo(Level.WARN) <= 0) {
            logger.w(DEFAULT_TAG, msg, t);
        }
    }

    public static void e(String msg) {
        e(msg, null);
    }

    public static void e(String msg, Throwable t) {
        if (level.compareTo(Level.ERROR) <= 0) {
            logger.e(DEFAULT_TAG, msg, t);
        }
    }

    public enum Level {
        DEBUG("DEBUG"), INFO("INFO"), WARN("WARN"), ERROR("ERROR");
        String value;

        Level(String value) {
            this.value = value;
        }
    }
}
