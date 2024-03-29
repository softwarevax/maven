package com.github.softwarevax.dict.core.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
     * @param target 修改后的对象
     * @param source 修改前的对象
     * @param <T> 泛型约束
     * @return 修改后的对象
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
                    set(target, fieldName, fieldValue);
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
     * @param t 对象
     * @param propertyName 属性名
     * @param <T> 泛型约束
     * @return 对象
     */
    private static <T> Object getSimplePropertyValue(T t, String propertyName) {
        if("".equals(propertyName) || propertyName == null) {
            return null;
        }
        try{
            Class<?> clzz = t.getClass();
            // fixup: 修复无法获取从父类继承的属性值(2022/6/12)
            Field field = FieldUtils.getField(clzz, propertyName, true);
            return FieldUtils.readField(field, t, true);
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 简单属性和复杂属性的getter方法
     * @param t 对象
     * @param propertyName 属性名
     * @param <T> 泛型约束
     * @return 对象
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
     * @param t 对象
     * @param propertyName 属性名
     * @param <T> 泛型约束
     * @return 类型
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
     * @param t 待修改的对象
     * @param propertyName 待修改的属性
     * @param propertyValue 属性值
     * @param <T> 泛型约束
     */
    public static <T> void set(T t, String propertyName, Object propertyValue) {
        if("".equals(propertyName) || propertyName == null) {
            return;
        }
        try{
            Class<?> clzz = t.getClass();
            Field field = FieldUtils.getField(clzz, propertyName, true);
            FieldUtils.writeField(field, t, propertyValue, true);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断是不是简单类型
     * @param clazz class
     * @return 是否是简单类型
     */
    public static boolean isSimpleType(Class<?> clazz) {
        return (ClassUtils.isPrimitiveOrWrapper(clazz) ||
                Enum.class.isAssignableFrom(clazz) ||
                CharSequence.class.isAssignableFrom(clazz) ||
                Number.class.isAssignableFrom(clazz) ||
                Date.class.isAssignableFrom(clazz) ||
                URI.class == clazz || URL.class == clazz ||
                Locale.class == clazz || Class.class == clazz);
    }

    /**
     * 根据类名创建实例
     * @param className 全限定类名
     * @param <T> 返回结果的类型
     * @return 返回结果
     */
    public static <T> T newInstance(String className) {
        if(className == null || "".equals(className)) {
            return null;
        }
        try {
            Class<T> clazz = (Class<T>) Class.forName(className);
            return clazz.newInstance();
        } catch (ClassNotFoundException e) {
        } catch (IllegalAccessException e) {
        } catch (InstantiationException e) {
        }
        try {
            Class<T> clazz = (Class<T>) Class.forName(className);
            Constructor[] constructors = clazz.getConstructors();
            for(Constructor constructor : constructors) {
                Object obj = null;
                try {
                    int parameterCount = constructor.getParameterCount();
                    Object[] args = new Object[parameterCount];
                    obj = constructor.newInstance(args);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                }
                if(obj != null) {
                    return (T)obj;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据类创建实例
     * @param clazz 全限定类名
     * @param <T> 返回结果的类型
     * @return 返回结果
     */
    public static <T> T newInstance(Class<T> clazz) {
        if(clazz == null) {
            return null;
        }
        return newInstance(clazz.getName());
    }
}
