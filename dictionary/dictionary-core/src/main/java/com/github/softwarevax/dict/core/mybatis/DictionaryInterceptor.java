package com.github.softwarevax.dict.core.mybatis;

import com.github.softwarevax.dict.core.DictionaryHelper;
import com.github.softwarevax.dict.core.database.DatabaseLoader;
import com.github.softwarevax.dict.core.domain.DictionaryConfigure;
import org.apache.ibatis.executor.resultset.DefaultResultSetHandler;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.plugin.*;

import java.sql.Statement;
import java.util.List;
import java.util.Properties;

@Intercepts({@Signature(type = ResultSetHandler.class, method = "handleResultSets", args = {Statement.class})})
public class DictionaryInterceptor implements Interceptor {

    public DictionaryInterceptor(DatabaseLoader dbLoader, DictionaryConfigure configure) {
        DictionaryHelper.addLoader(dbLoader);
        DictionaryHelper.configure(configure);
    }

    public DictionaryInterceptor(DatabaseLoader dbLoader) {
        this(dbLoader, new DictionaryConfigure());
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        DefaultResultSetHandler handler = (DefaultResultSetHandler) invocation.getTarget();
        Statement statement = (Statement) invocation.getArgs()[0];
        List<Object> result = handler.handleResultSets(statement);
        DictionaryHelper.resultWrapper(result);
        return result;
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof ResultSetHandler) {
            return Plugin.wrap(target, this);
        } else {
            return target;
        }
    }

    @Override
    public void setProperties(Properties properties) {
    }
}
