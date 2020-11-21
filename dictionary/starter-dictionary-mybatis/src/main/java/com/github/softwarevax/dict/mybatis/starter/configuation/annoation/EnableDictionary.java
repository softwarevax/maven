package com.github.softwarevax.dict.mybatis.starter.configuation.annoation;

import com.github.softwarevax.dict.mybatis.starter.configuation.selector.EnableDictionaryImportSelector;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * @author twcao
 * 是否开启
 * 2018/12/6/006 12:52
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(EnableDictionaryImportSelector.class)
@Documented
public @interface EnableDictionary {

    @AliasFor("autowired")
    boolean value() default true;

    /**
     * 是否注入
     * @return 是否启用
     */
    @AliasFor("value")
    boolean autowired() default true;
}
