package com.github.softwarevax.dict.core;

import java.lang.annotation.*;

@Inherited
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Dictionary {

    /**
     * 表名
     * @return
     */
    String table() default "";

    /**
     * 属性(实体中的属性) eg: prop1
     * @return
     */
    String property() default "";

    /**
     * text(数据库表中的列名) eg: col
     * @return
     */
    String column() default "";

    /**
     * value(数据库表中的列名) eg: val
     * @return
     */
    String value() default "";

    /**
     * 额外条件，默认没有额外条件 eg: val1 = val and val2 = val2 .... and valn = valn
     * @return
     */
    String[] conditions() default "";

    /**
     * 自定义sql, 自定义sql查询，eg: select [col1, col2, col3 ... coln] from [table] where [val1 = #{val1} and val2 = #{val2} .... and valn = #{valn}]
     * @return
     */
    //String sql() default "";

}
