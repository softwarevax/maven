package com.github.softwarevax.task.configure;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @author twcao
 * @description spring上下文
 * @project unified-task-schedule
 * @classname ApplicationContext
 * @date 2021/3/16 11:49
 */
@Component
public class ApplicationContextUtils implements ApplicationContextAware {

    private static ApplicationContext CONTEXT;
    /**
     * 设置spring上下文
     * @param ctx spring上下文
     * @throws BeansException
     * */
    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        CONTEXT = ctx;
    }

    /**
     * 获取容器
     * @return
     */
    public static ApplicationContext getApplicationContext() {
        return CONTEXT;
    }

    /**
     * 获取容器对象
     * @param type
     * @param <T>
     * @return
     */
    public static <T> T getBean(Class<T> type) {
        return CONTEXT.getBean(type);
    }

    public static <T> T getBean(String name,Class<T> clazz){
        return CONTEXT.getBean(name, clazz);
    }

    public static Object getBean(String name){
        return CONTEXT.getBean(name);
    }

    /**
     * springboot动态注册bean
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T register(Class<T> clazz) {
		ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) ApplicationContextUtils.getApplicationContext();
		DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) configurableApplicationContext.getBeanFactory();
		BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(clazz);
        if(defaultListableBeanFactory.getBeanNamesForType(clazz).length > 0) {
            return defaultListableBeanFactory.getBean(clazz);
        }
		defaultListableBeanFactory.registerBeanDefinition(clazz.getName(), beanDefinitionBuilder.getRawBeanDefinition());
		return (T) ApplicationContextUtils.getBean(clazz.getName());
    }
}
