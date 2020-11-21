package com.github.softwarevax.dict.core.database;


public class DatabaseTable extends AbstractDbTable {

    private String[] column;

    private String[] conditions;

    private int interval;

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

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }
}
