package com.github.softwarevax.dict.mybatis.starter.configuation;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author twcao
 * Dictionary常量设置
 * 2018/10/22 0022 18:32
 */
@ConfigurationProperties(prefix = "dictionary")
public class DictionaryConstant {

    /**
     * database type
     */
    private DatabaseTable[] dbTable;

    /**
     * dictionary configuration
     */
    private DictionaryConfigure configure;

    public DatabaseTable[] getDbTable() {
        return dbTable;
    }

    public void setDbTable(DatabaseTable[] dbTable) {
        this.dbTable = dbTable;
    }

    public DictionaryConfigure getConfigure() {
        return configure;
    }

    public void setConfigure(DictionaryConfigure configure) {
        this.configure = configure;
    }

    public static class DictionaryConfigure extends com.github.softwarevax.dict.core.domain.DictionaryConfigure {
    }

    public static class DatabaseTable extends com.github.softwarevax.dict.core.database.DatabaseTable {
    }
}
