package com.github.softwarevax.lock.configuration.selector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

public class EnableLockImportSelector implements ImportSelector {

    private static final Logger logger = LoggerFactory.getLogger(EnableLockImportSelector.class);

    public static final String LOCK_DEFAULT_CONFIGURATION = "com.github.softwarevax.lock.configuration.LockAutoConfiguration";

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        return new String[]{LOCK_DEFAULT_CONFIGURATION};
    }
}
