package com.github.softwarevax.dict.core.database;


import com.github.softwarevax.dict.core.utils.StringUtils;

import java.util.Arrays;
import java.util.Objects;

public class DatabaseTable extends AbstractDbTable {

    private String[] column;

    private String[] conditions;

    public String[] getColumn() {
        return column;
    }

    public void setColumn(String[] column) {
        this.column = column;
    }

    public String[] getConditions() {
        return conditions;
    }

    public void setConditions(String[] conditions) {
        this.conditions = conditions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DatabaseTable table = (DatabaseTable) o;
        return StringUtils.equals(column, table.column) &&
                StringUtils.equals(conditions, table.conditions) &&
                StringUtils.equals(tableName, table.tableName);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(tableName);
        result = 31 * result + Arrays.hashCode(column);
        result = 31 * result + Arrays.hashCode(conditions);
        return result;
    }
}
