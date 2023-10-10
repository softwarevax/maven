package com.github.softwarevax.dict.core;

import java.lang.annotation.*;

@Inherited
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Dict {

    /**
     * 表名
     * @return 表名
     */
    String table() default "";

    /**
     * 属性(实体中的属性) eg: prop1
     * @return 属性
     */
    String property() default "";

    /**
     * text(数据库表中的列名) eg: col
     * @return 列名
     */
    String column() default "";

    /**
     * value(数据库表中的列名) eg: val
     * @return 列名
     */
    String value() default "";

    /**
     * 额外条件，默认没有额外条件 eg: val1 = val and val2 = val2 .... and valn = valn
     * @return 条件数组
     */
    String[] conditions() default "";

    /**
     * 自定义sql, 自定义sql查询，eg: select [col1, col2, col3 ... coln] from [table] where [val1 = #{val1} and val2 = #{val2} .... and valn = #{valn}]
     */
    //String sql() default "";

}
