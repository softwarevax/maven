package com.github.softwarevax.dict.core.database;


import com.github.softwarevax.dict.core.interfaces.DictionaryTable;
import com.github.softwarevax.dict.core.enums.DictionaryType;


public abstract class AbstractDbTable implements DictionaryTable {

    protected String tableName;

    protected DatabaseType dbType;

    @Override
    public DictionaryType dictType() {
        return DictionaryType.DATABASE;
    }

    @Override
    public String name() {
        return this.tableName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public DatabaseType getDbType() {
        return dbType;
    }

    public void setDbType(DatabaseType dbType) {
        this.dbType = dbType;
    }
}
