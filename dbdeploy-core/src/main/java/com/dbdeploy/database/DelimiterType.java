package com.dbdeploy.database;

public enum DelimiterType {
    /**
     * Delimiter is interpreted whenever it appears at the end of a line
     */
    normal {
        public boolean matches(String line, String delimiter) {
            return line.endsWith(delimiter);
        }
    },

    /**
     * Delimiter must be on a line all to itself
     */
    row {
        public boolean matches(String line, String delimiter) {
            return line.equals(delimiter);
        }
    };

    public abstract boolean matches(String line, String delimiter);
}
