package com.github.softwarevax.dict.core.resolver;

import com.github.softwarevax.dict.core.enums.DictFieldType;
import com.github.softwarevax.dict.core.utils.BeanUtils;
import com.github.softwarevax.dict.core.utils.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * 集合解析器
 * 2020/11/22 15:12
 */
public class CollectionConverter implements IConverter {

    DictFieldType type = DictFieldType.COLLECTION;

    @Override
    public List<Object> converter(Object t) {
        if(t == null || !Collection.class.isAssignableFrom(t.getClass())) {
            return new ArrayList<>();
        }
        Collection coll = (Collection) t;
        List<Object> objects = new ArrayList<>();
        if(CollectionUtils.isEmpty(coll)) {
            return objects;
        }
        Iterator iterator = coll.iterator();
        while (iterator.hasNext()) {
            Object obj = iterator.next();
            if(BeanUtils.isSimpleType(obj.getClass())) {
                continue;
            }
            objects.add(obj);
        }
        return objects;
    }
}
