package com.github.softwarevax.dict.core.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * Created by twcao
 */
public class BeanUtils {

    /**
     *属性分隔符
     *
     */
    public static final String PROPERTY_DELIMITER = "\\.";

    /**
     * 合并两个实体,target的属性,都将被source属性覆盖,即时该属性为empty
     * @param target
     * @param source
     * @param <T>
     * @return
     */
    public static <T> T merge(T target, T source) {
        try{
            Class<?> clzz = source.getClass();
            Field[] sourceFields = clzz.getDeclaredFields();
            List<Field> sourceFieldList = Arrays.asList(sourceFields);
            for(Field sourceField: sourceFieldList) {
                sourceField.setAccessible(true);
                String fieldName = sourceField.getName();
                Object fieldValue = getSimplePropertyValue(source, fieldName);
                if(fieldValue != null) {
                    set(target, fieldName, fieldValue, sourceField.getType());
                }
           }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return target;
    }

    /**
     * 实体中简单属性的getter方法，内部使用
     * @param t
     * @param propertyName
     * @param <T>
     * @return
     */
    private static <T> Object getSimplePropertyValue(T t, String propertyName) {
        if("".equals(propertyName) || propertyName == null) {
            return null;
        }
        try{
            Class<?> clzz = t.getClass();
            String methodName = "get" + propertyName.substring(0, 1).toUpperCase().concat(propertyName.substring(1));
            Method method = clzz.getDeclaredMethod(methodName);
            return method.invoke(t);
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 简单属性和复杂属性的getter方法
     * @param t
     * @param propertyName
     * @param <T>
     * @return
     */
    public static <T> Object get(T t, String propertyName) {
        if(t == null || StringUtils.isBlank(propertyName))  {
            return null;
        }
        List<String> propertyList = Arrays.asList(propertyName.split(PROPERTY_DELIMITER));
        int size = propertyList.size();
        if(size == 0) {
            return null;
        }
        if(size == 1) {
            return getSimplePropertyValue(t, propertyName);
        }
        Object currPropertyValue = t;
        String currProperty = null;
        for (int i = 0; i < size; i++) {
            //当前属性的名称
            currProperty =  propertyList.get(i);
            //当前属性对应的值
            currPropertyValue = getSimplePropertyValue(currPropertyValue, currProperty);
            if(size - 1 > i) {
                currProperty = propertyList.get(i + 1);
            }
        }
        return currPropertyValue;
    }

    /**
     * 获得复杂属性的参数类型
     * @param t
     * @param propertyName
     * @param <T>
     * @return
     */
    public static <T> Class<?> getPropertyType(T t, String propertyName) {
        if(t == null || StringUtils.isBlank(propertyName))  {
            return null;
        }
        try {
            Class<?> clzz = t.getClass();
            List<String> propertyList = Arrays.asList(propertyName.split(PROPERTY_DELIMITER));
            int size = propertyList.size();
            if(size == 0) {
                return null;
            }
            if(size == 1) {
                Field field = clzz.getDeclaredField(propertyName);
                return field.getType();
            }
            String property = null;
            Object propertyValue = t;
            for (int j = 0; j < size; j++) {
                //当前属性的名称
                property =  propertyList.get(j);
                //当前属性对应的值
                propertyValue = getSimplePropertyValue(propertyValue, property);
                if(size - 1 > j) {
                    property = propertyList.get(j + 1);
                }
            }
            return propertyValue.getClass();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 实体的setter方法
     * @param t
     * @param propertyName
     * @param propertyValue
     * @param propertyClass
     * @param <T>
     * @return
     */
    public static <T> void set(T t, String propertyName, Object propertyValue, Class<?> propertyClass) {
        if("".equals(propertyName) || propertyName == null) {
            return;
        }
        try{
            Class<?> clzz = t.getClass();
            String methodName = "set" + propertyName.substring(0, 1).toUpperCase().concat(propertyName.substring(1));
            Method method = clzz.getDeclaredMethod(methodName, propertyClass);
            method.invoke(t, propertyValue);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
