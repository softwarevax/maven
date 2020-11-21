package com.github.softwarevax.dict.core.database;

/**
 * @author ctw
 * 数据库工具类
 * 2020/11/21 10:55
 */
public class DatabaseUtils {

    public static String getWrapper(DatabaseType dbType) {
        if(dbType == null) {
            return "";
        }
        switch (dbType) {
            case MYSQL:
                return "`";
            case ORACLE:
                return "\"";
            default:
                return "";
        }
    }
}
