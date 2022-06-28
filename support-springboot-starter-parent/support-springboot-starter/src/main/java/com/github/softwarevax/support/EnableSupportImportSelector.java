package com.github.softwarevax.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

public class EnableSupportImportSelector implements ImportSelector {

    private static final Logger logger = LoggerFactory.getLogger(EnableSupportImportSelector.class);

    public static final String LOCK_DEFAULT_CONFIGURATION = "com.github.softwarevax.support.SupportAutoConfiguration";

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        return new String[]{LOCK_DEFAULT_CONFIGURATION};
    }
}
