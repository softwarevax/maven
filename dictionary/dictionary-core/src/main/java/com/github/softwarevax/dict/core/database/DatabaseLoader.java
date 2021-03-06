package com.github.softwarevax.dict.core.database;


import com.github.softwarevax.dict.core.interfaces.DictionaryLoader;
import com.github.softwarevax.dict.core.interfaces.DictionaryTable;
import com.github.softwarevax.dict.core.utils.ArrayUtils;
import com.github.softwarevax.dict.core.utils.CollectionUtils;
import com.github.softwarevax.dict.core.utils.StringUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据库方式加载字典表
 */
public class DatabaseLoader implements DictionaryLoader {

    /**
     * key列名
     */
    public static final String KEY_COLUMN = "label";

    /**
     * value列名
     */
    public static final String VALUE_COLUMN = "value";

    /**
     * 表名
     */
    public static final String TABLE_NAME = "sys_config";

    /**
     * 数据源，默认获取当前使用的数据源
     */
    private DataSource dataSource;

    /**
     * 通过字典表dictTable加载的字典缓存
     */
    private Map<DictionaryTable, List<Map<String, Object>>> dbCache;

    public DatabaseLoader(DataSource dataSource) {
        this.dataSource = dataSource;
        this.dbCache = new LinkedHashMap<>();
    }

    /**
     * 新增字典表
     * @param dictTable 数据库表
     * @return 是否加载成功
     */
    @Override
    public boolean addDictionaryTable(DictionaryTable dictTable) {
        if(dictTable == null || !(dictTable instanceof DatabaseTable)) {
            // 只处理数据库类型
            return false;
        }
        DatabaseTable dbTable = (DatabaseTable) dictTable;
        String tableName = dbTable.getTableName();
        try(Connection conn = this.dataSource.getConnection()) {
            dbTable.dbType = DatabaseType.ofType(conn.getMetaData().getDatabaseProductName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(StringUtils.isBlank(tableName)) {
            // 没有设置表名，则使用默认表名
            dbTable.setTableName(TABLE_NAME);
        }
        dbTable.setTableName(tableName.trim()); // 防止表名两边的空串
        if(ArrayUtils.length(dbTable.getColumn()) < 2) {
            // 没有设置列名，则使用默认列名
            dbTable.setColumn(new String[] {KEY_COLUMN, VALUE_COLUMN});
        }
        dbTable.setColumn(ArrayUtils.trim(dbTable.getColumn())); // 防止列名两边的空串
        List<String> existTable = dbCache.keySet().stream().map(table -> table.name()).collect(Collectors.toList());
        if(existTable.contains(tableName)) {
            // 不重复添加字典表
            return false;
        }
        List<Map<String, Object>> cache = new ArrayList<>();
        this.dbCache.put(dbTable, cache);
        return true;
    }

    @Override
    public Map<DictionaryTable, List<Map<String, Object>>> dictLoader() {
        if(this.dbCache.size() == 0) {
            // 没有字典表需要加载
            return this.dbCache;
        }
        Iterator<Map.Entry<DictionaryTable, List<Map<String, Object>>>> iterator = dbCache.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<DictionaryTable, List<Map<String, Object>>> entry = iterator.next();
            if(!(entry.getKey() instanceof DatabaseTable)) {
                continue;
            }
            // 表名
            DatabaseTable table = (DatabaseTable) entry.getKey();
            // 缓存
            List<Map<String, Object>> cache = queryCache(table);
            this.dbCache.put(table, cache);
        }
        return this.dbCache;
    }

    /**
     * 重新加载缓存
     */
    @Override
    public Map<DictionaryTable, List<Map<String, Object>>> reload() {
        if(CollectionUtils.isEmpty(this.dbCache)) {
            return new HashMap<>();
        }
        Iterator<DictionaryTable> iterator = this.dbCache.keySet().iterator();
        while (iterator.hasNext()) {
            DictionaryTable next = iterator.next();
            if(!(next instanceof DatabaseTable)) {
                continue;
            }
            this.dbCache.get(next).clear();
        }
        return this.dictLoader();
    }

    /**
     * 查询缓存
     * @param table 数据库表
     * @return 加载成功后的缓存
     */
    private List<Map<String, Object>> queryCache(DatabaseTable table) {
        if(table == null) {
            return new ArrayList<>();
        }
        StringBuffer sb = new StringBuffer(" SELECT ");
        String[] columns = table.getColumn();
        String wrap = DatabaseUtils.getWrapper(table.dbType);
        for(String col : columns) {
            sb.append(StringUtils.wrap(col, wrap)).append(",");
        }
        sb = sb.delete(sb.length() - 1, sb.length());
        sb.append(" FROM ").append(StringUtils.wrap(table.getTableName(), wrap)).append(" WHERE 1 = 1 ");
        String[] condition = table.getConditions();
        if(ArrayUtils.isNotEmpty(condition)) {
            for(String con : condition) {
                if(StringUtils.isBlank(con)) { //条件为空不处理
                    continue;
                }
                String[] cons = ArrayUtils.trim(con.split("="));
                if(ArrayUtils.length(cons) != 2) { // 根据等号分离，长度!=2不处理
                    continue;
                }
                if(ArrayUtils.contains(columns, cons[0])) { // 在列中不存在的条件列不处理
                    cons[0] = cons[0].indexOf(wrap) > -1 ? cons[0] : StringUtils.wrap(cons[0], wrap);
                }
                sb.append(" AND ").append(cons[0]).append(" = ").append(cons[1]);
            }
        }
        String sql = sb.toString();
        return executeSql(sql);
    }

    /**
     * 执行sql
     * @param sql 待执行的sql
     * @return 查询的缓存
     */
    private List<Map<String, Object>> executeSql(String sql) {
        if(this.dataSource == null) {
            return new ArrayList<>();
        }
        List<Map<String, Object>> cache = new ArrayList<>();
        try(Connection conn = this.dataSource.getConnection();
            PreparedStatement stat = conn.prepareStatement(sql);
            ResultSet rs = stat.executeQuery()) {
            ResultSetMetaData metaData = stat.getMetaData();
            int columnCount = metaData.getColumnCount();
            while (rs.next()) {
                // row
                Map<String, Object> row = new LinkedHashMap<>();
                for(int i = 1; i <= columnCount; i++) {
                    // column name
                    String columnName = metaData.getColumnName(i);
                    row.put(columnName, rs.getObject(i));
                }
                cache.add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cache;
    }
}
