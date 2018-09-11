package com.tellh.inline.plugin;

import com.tellh.inline.plugin.log.Log;

public class AccessInlineExtension {
    private Log.Level level = Log.Level.INFO;

    public void logLevel(String level) {
        this.level = Log.Level.valueOf(level);
    }

    public Log.Level getLogLevel() {
        return level;
    }
}
