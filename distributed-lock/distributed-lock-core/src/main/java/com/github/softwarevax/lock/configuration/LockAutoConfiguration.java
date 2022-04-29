package com.github.softwarevax.lock.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = {"com.github.softwarevax"})
@EnableConfigurationProperties(value = {LockConstant.class})
public class LockAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(LockAutoConfiguration.class);

}
