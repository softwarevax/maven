package com.github.softwarevax.support;

import com.github.softwarevax.support.lock.configuration.LockConstant;
import com.github.softwarevax.support.page.configuration.PaginationConstant;
import com.github.softwarevax.support.result.configuration.ResultConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = {"com.github.softwarevax.support"})
@EnableConfigurationProperties(value = {LockConstant.class, ResultConstant.class, PaginationConstant.class})
public class SupportAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(SupportAutoConfiguration.class);

}
