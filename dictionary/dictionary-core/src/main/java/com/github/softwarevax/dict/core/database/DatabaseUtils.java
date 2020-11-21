package com.github.softwarevax.dict.core.database;

/**
 * @author ctw
 * @Project： plugin-parent
 * @Package: com.github.softwarevax.dict.core.database
 * @Description:
 * @date 2020/11/21 10:55
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
