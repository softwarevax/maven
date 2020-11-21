package com.github.softwarevax.dict.mybatis.configure;

import com.github.softwarevax.dict.core.database.DatabaseLoader;
import com.github.softwarevax.dict.core.database.DatabaseTable;
import com.github.softwarevax.dict.core.mybatis.DictionaryInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @author ctw
 * 2020/11/21 13:30
 */
@Configuration
public class BeanRegistry {

    @Bean
    public DictionaryInterceptor dictionaryInterceptor(DataSource dataSource) {
        // config 配置表
        DatabaseTable config = new DatabaseTable();
        String[] configColumn = new String[]{"label", "value", "type"};
        String[] configCondition = new String[]{"1 = 1"};
        String configTableName = "sys_config";
        config.setColumn(configColumn);
        config.setConditions(configCondition);
        config.setTableName(configTableName);
        // user 用户表
        DatabaseTable user = new DatabaseTable();
        String[] userColumn = new String[]{"id", "name"};
        String userTableName = "app_user";
        user.setColumn(userColumn);
        user.setTableName(userTableName);
        DatabaseLoader dbLoader = new DatabaseLoader(dataSource);
        dbLoader.addDictionaryTable(config);
        dbLoader.addDictionaryTable(user);
        DictionaryInterceptor interceptor = new DictionaryInterceptor(dbLoader);
        return interceptor;
    }
}
