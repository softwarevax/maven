package com.github.softwarevax.dict.mybatis.starter.configuation;

import com.github.softwarevax.dict.core.database.DatabaseLoader;
import com.github.softwarevax.dict.core.mybatis.DictionaryInterceptor;
import com.github.softwarevax.dict.core.utils.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import javax.sql.DataSource;

/**
 * @author ctw
 * 2018/12/6/006 12:46
 */
@ComponentScan(basePackages = {"com.github.softwarevax"})
@EnableConfigurationProperties(value = {DictionaryConstant.class})
public class DictionaryAutoConfiguration {

    @Autowired
    private DictionaryConstant constant;

    @Bean
    public DictionaryInterceptor dictionaryInterceptor(DataSource dataSource) {
        DatabaseLoader dbLoader = new DatabaseLoader(dataSource);
        DictionaryConstant.DatabaseTable[] tables = constant.getDbTable();
        if(ArrayUtils.isNotEmpty(tables)) {
            for(DictionaryConstant.DatabaseTable table : tables) {
                dbLoader.addDictionaryTable(table);
            }
        }
        DictionaryInterceptor interceptor = new DictionaryInterceptor(dbLoader);
        return interceptor;
    }
}
