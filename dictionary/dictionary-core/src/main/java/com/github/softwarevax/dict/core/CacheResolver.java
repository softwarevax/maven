package com.github.softwarevax.dict.core;

import com.github.softwarevax.dict.core.enums.DictField;
import com.github.softwarevax.dict.core.enums.DictFieldType;
import com.github.softwarevax.dict.core.resolver.ArrayConverter;
import com.github.softwarevax.dict.core.resolver.CollectionConverter;
import com.github.softwarevax.dict.core.resolver.IConverter;
import com.github.softwarevax.dict.core.utils.BeanUtils;
import com.github.softwarevax.dict.core.utils.ListUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

/**
 * @author ctw
 * 缓存解析器
 * 2020/11/22 13:23
 */
public class CacheResolver {

    private Map<DictFieldType, IConverter> converterMap = new HashMap();

    private Class<? extends Annotation> annotation;

    {
        converterMap.put(DictFieldType.COLLECTION, new CollectionConverter());
        converterMap.put(DictFieldType.ARRAY, new ArrayConverter());
    }

    public CacheResolver(Class<? extends Annotation> annotation) {
        this.annotation = annotation;
    }

    /**
     * 解析对象中，所有标注了注解的字段(可嵌套)
     * @param result 待处理的数据
     * @return 所有备注解了的字段
     */
    public List<DictField> resolve(List<Object> result) {
        if(ListUtils.isEmpty(result)) {
            return new ArrayList<>();
        }
        List<DictField> dictFields = new ArrayList<>();
        for(Object obj : result) {
            Class<?> clazz = obj.getClass();
            Field[] fields= clazz.getDeclaredFields();
            for(Field field : fields) {
                if(field.getDeclaredAnnotation(annotation) == null) {
                    continue;
                }
                Object fieldVal = BeanUtils.get(obj, field.getName());
                if(fieldVal == null) {
                    continue;
                }
                Class fieldClazz = field.getType();
                if(BeanUtils.isSimpleType(fieldClazz)) { //简单类型
                    dictFields.add(new DictField(obj, field, fieldVal));
                    continue;
                }
                List<Object> converter = null;
                if(Collection.class.isAssignableFrom(fieldClazz)) { //集合类型
                    converter = converterMap.get(DictFieldType.COLLECTION).converter(fieldVal);
                } else if(fieldClazz.isArray()) { // 数组类型
                    converter = converterMap.get(DictFieldType.ARRAY).converter(fieldVal);
                } else { // 对象类型
                    converter = Arrays.asList(fieldVal);
                }
                dictFields.addAll(resolve(converter));
            }
        }
        return dictFields;
    }
}
