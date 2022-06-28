package com.github.softwarevax.support.page;

import com.github.pagehelper.PageHelper;
import com.github.softwarevax.support.utils.StringUtils;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.parser.SimpleNode;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 表明为a，列名为 pkg_id, 实体属性为 pkgId
 * 1、配置了resultMap
 *   eg:
 *   1.1、resultMap: property = pkgId, column = plan_pkg_id
 *   1.2、sql      : select pkg_id as plan_pkg_id from a
 *   1.3、取出plan_pkg_id排序
 *
 * 2、配置了resultType, 没配置列别名
 *   eg:
 *   2.1、resultType: pkg_id  mybatis下划线转驼峰映射  pkgId
 *   2.2、sql       : select pkg_id from a
 *   2.3、取出pkg_id排序
 *
 *  3、配置了resultType，配置了别名
 *  eg:
 *  3.1、resultType: pkgid  同名属性映射  pkgId
 *  3.2、sql       : select pkg_id pkgid from a
 *  3.3、取出别名pkgid排序
 *
 *  注意：1、若列名为pkg_id，mybatis可以将值映射给pkgId，也可以将值映射给pkgid，但是两个属性都存在时，mybatis将不知映射给哪个字段，导致两个字段都为null
 *       2、若属性为pkgId，则列名可能是pkg_id，也可能是pkgid（列名不分大小写），故根据属性反推列名时，需考虑这种情况
 *       3、暂时不支持嵌套属性排序，如：
 *       SpPlanVO:
 *        id
 *        name
 *        SpMember:
 *          id
 *       无法根据SpPlanVO.SpMember.id属性排序
 *
 *  待扩展：
 *      实现嵌套属性排序思路
 *      1、嵌套实体属性，一定是有别名的，可能是resultMap，也可能是resultType
 *      2、resultMap可从MappedStatement跟对应的结构取出
 *      3[待验证]、resultType 有嵌套，应该只支持实体嵌套，无法支持列表嵌套，如：
 *      SpPlanVO:
 *        id
 *        name
 *        List<SpMember>:
 *          id
 *        若支持实体嵌套，列别名样式可能为：SpPlanVO.SpMember
 *
 *  使用方式：
 *      OrderByInterceptor.setOrderBy(query.getOrders());            // 设置排序，若不设置排序，插件会因为没有排序字段而直接跳过，不会影响分页插件
 *      PageHelper.startPage(query.getPage(), query.getPageSize());  //设置分页
 */
@Intercepts({
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})
        })
public class OrderByInterceptor implements Interceptor {

    /**
     * 日志
     */
    public static Logger logger = LoggerFactory.getLogger(OrderByInterceptor.class);

    private static ThreadLocal<String> threadOrders = new ThreadLocal();

    public static void setOrderBy(String orderBy) {
        OrderByInterceptor.threadOrders.set(orderBy);
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        String orders = OrderByInterceptor.threadOrders.get();
        if(StringUtils.isBlank(orders)) { // 如果不需要排序，跳过
            return invocation.proceed();
        }
        // 若ThreadLocal.get()不为空，则finally中移除，防止内存泄露
        try {
            MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
            List<ResultMap> resultMaps = mappedStatement.getResultMaps();
            if(CollectionUtils.isEmpty(resultMaps)) {
                // 如果没有结果映射，查询会报错，但此插件不报，由mybatis框架检查报错，这里跳过
                return invocation.proceed();
            }
            ResultMap resultMap = resultMaps.get(0);
            List<ResultMapping> resultMappings = resultMap.getResultMappings();
            // property:column
            Map<String, String> propertyColumnMap = new HashMap<>();
            if(CollectionUtils.isEmpty(resultMappings)) {
                // 若配置的是resultType
                Object parameter = invocation.getArgs()[1];
                BoundSql boundSql = mappedStatement.getBoundSql(parameter);
                String sql = boundSql.getSql();
                propertyColumnMap = getSelectColumn(sql, resultMap.getType());
            } else {
                // 若配置了resultMap
                List<ResultMapping> propertyResultMappings = resultMap.getPropertyResultMappings();
                // 获得resultMap[property:column]映射
                propertyColumnMap = propertyResultMappings.stream()
                        .filter(row -> StringUtils.isNotEmpty(row.getColumn()))
                        .collect(Collectors.toMap(ResultMapping::getProperty, ResultMapping::getColumn));
            }
            String orderBy = parsePropertyToColumn(orders, propertyColumnMap);
            // 如果转换排序字段时，返回了空，则不进行排序
            if(StringUtils.isBlank(orderBy)) {
                invocation.proceed();
            }
            PageHelper.orderBy(orderBy);
        } finally {
            OrderByInterceptor.threadOrders.remove();
        }
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }

    /**
     *
     * @param sql
     * @return
     * @throws JSQLParserException
     */
    private Map<String, String> getSelectColumn(String sql, Class<?> type) throws JSQLParserException {
        // 获取所有的属性
        Map<String, String> map = getColumn(type);
        // 查询语句
        Select select = (Select) CCJSqlParserUtil.parse(sql);
        PlainSelect selectBody = (PlainSelect) select.getSelectBody();
        List<SelectItem> selectItems = selectBody.getSelectItems();
        for (int i = 0, size = selectItems.size(); i < size ; i++) {
            SelectItem item = selectItems.get(i);
            if(!(item instanceof SelectExpressionItem)) {
                continue;
            }
            SelectExpressionItem expressionItem = (SelectExpressionItem) item;
            String columnName = "";
            Alias alias = expressionItem.getAlias();
            Expression expression = expressionItem.getExpression();
            if(expression instanceof CaseExpression || expression instanceof TimeKeyExpression) {
                // case 或 日期
                columnName = alias.getName();
            } else if(expression instanceof LongValue || expression instanceof StringValue || expression instanceof DateValue || expression instanceof DoubleValue) {
                // 值表达式
                columnName = Objects.nonNull(alias.getName()) ? alias.getName() : expression.getASTNode().jjtGetValue().toString();
            } else {
                if (alias != null) {
                    columnName = alias.getName();
                } else {
                    SimpleNode node = expression.getASTNode();
                    Object value = node.jjtGetValue();
                    if (value instanceof Column) {
                        columnName = ((Column) value).getColumnName();
                    } else if (value instanceof Function) {
                        columnName = value.toString();
                    } else {
                        // 增加对select 'aaa' from table; 的支持
                        columnName = String.valueOf(value);
                        columnName = columnName.replace("'", "");
                        columnName = columnName.replace("\"", "");
                        columnName = columnName.replace("`", "");
                    }
                }
            }
            if(map.containsKey(StringUtils.lowerCase(columnName))) {
                String property = map.get(StringUtils.lowerCase(columnName));
                map.put(property, columnName);
            }
        }
        return map;
    }

    /**
     * @param type
     * @return
     */
    private Map<String, String> getColumn(Class<?> type) {
        Map<String, String> columnMap = new HashMap<>();
        Field[] fields = FieldUtils.getAllFields(type);
        for (int i = 0, size = fields.length; i < size; i++) {
            // lowerCase(property):property
            columnMap.put(StringUtils.lowerCase(fields[i].getName()), fields[i].getName());
            // lowerCase(toUnderScoreCase(property)):property  驼峰转下划线，再转小写
            columnMap.put(StringUtils.lowerCase(StringUtils.toUnderScoreCase(fields[i].getName())), fields[i].getName());
        }
        return columnMap;
    }

    /**
     * 将orderBy中的实体属性，换成表中的列名(列别名)
     * @param orderBy
     * @param propertyColumnMap
     * @return
     */
    private String parsePropertyToColumn(String orderBy, Map<String, String> propertyColumnMap) {
        List<String> propertyOrderBy = StringUtils.splitToList(orderBy, ",");
        if(CollectionUtils.isEmpty(propertyOrderBy)) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        Map<String, String> propertyOrderByMap = StringUtils.splitToMap(propertyOrderBy, " ");
        Iterator<Map.Entry<String, String>> iterator = propertyOrderByMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> next = iterator.next();
            String property = next.getKey();
            String order = next.getValue();
            Assert.isTrue(propertyColumnMap.containsKey(property), "属性[" + property + "]没有映射的列");
            String columnAlias = propertyColumnMap.get(property);
            sb.append(columnAlias).append(" ").append(order);
            if(iterator.hasNext()) {
                sb.append(",");
            }
        }
        logger.info("orderBy = {}", sb);
        return sb.toString();
    }
}
