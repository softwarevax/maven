package com.github.softwarevax.dict.core.database;

import com.github.softwarevax.dict.core.utils.StringUtils;

public enum DatabaseType {
    MYSQL("MYSQL"), ORACLE("ORACLE");

    private String text;

    DatabaseType(String text) {
        this.text = text;
    }

    public static DatabaseType ofType(String text) {
        if(StringUtils.isBlank(text)) {
            return null;
        }
        if(MYSQL.text.toLowerCase().equals(text.toLowerCase())) {
            return MYSQL;
        }
        if(ORACLE.text.toLowerCase().equals(text.toLowerCase())) {
            return ORACLE;
        }
        return null;
    }
}
