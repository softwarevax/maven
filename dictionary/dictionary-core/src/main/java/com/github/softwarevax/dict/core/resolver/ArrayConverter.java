package com.github.softwarevax.dict.core.resolver;

import com.github.softwarevax.dict.core.enums.DictFieldType;
import com.github.softwarevax.dict.core.utils.BeanUtils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ctw
 * @Project： plugin-parent
 * @Package: com.github.softwarevax.dict.core.resolver
 * @Description:
 * @date 2020/11/22 16:59
 */
public class ArrayConverter implements IConverter {

    DictFieldType type = DictFieldType.ARRAY;

    @Override
    public List<Object> converter(Object t) {
        List<Object> objects = new ArrayList<>();
        if(t == null || !t.getClass().isArray() || BeanUtils.isSimpleType(t.getClass().getComponentType())) {
            return objects;
        }
        int length = Array.getLength(t);
        for(int i = 0; i < length; i++) {
            objects.add(Array.get(t, i));
        }
        return objects;
    }
}
