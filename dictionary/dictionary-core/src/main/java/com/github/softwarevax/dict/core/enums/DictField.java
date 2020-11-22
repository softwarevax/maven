package com.github.softwarevax.dict.core.enums;

import java.lang.reflect.Field;

/**
 * @author ctw
 * @Project： plugin-parent
 * @Package: com.github.softwarevax.dict.core.enums
 * @Description:
 * @date 2020/11/22 15:10
 */
public class DictField {

    private Object obj;

    private Field field;

    private Object fieldVal;

    public DictField(Object obj, Field field, Object fieldVal) {
        this.obj = obj;
        this.field = field;
        this.fieldVal = fieldVal;
    }

    public DictField() {
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public Object getFieldVal() {
        return fieldVal;
    }

    public void setFieldVal(Object fieldVal) {
        this.fieldVal = fieldVal;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }
}
