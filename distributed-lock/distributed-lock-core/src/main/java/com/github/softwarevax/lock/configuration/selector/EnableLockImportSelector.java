package com.github.softwarevax.lock.configuration.selector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author twcao
 * 2018/12/6/006 22:59
 */
public class EnableLockImportSelector implements ImportSelector {

    private static final Logger logger = LoggerFactory.getLogger(EnableLockImportSelector.class);

    /**
     * support配置类
     */
    public static final String LOCK_DEFAULT_CONFIGURATION = "com.github.softwarevax.lock.configuration.LockAutoConfiguration";

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        return new String[]{LOCK_DEFAULT_CONFIGURATION};
    }
}
