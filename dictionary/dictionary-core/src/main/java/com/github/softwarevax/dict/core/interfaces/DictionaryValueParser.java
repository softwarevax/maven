package com.github.softwarevax.dict.core.interfaces;

public interface DictionaryValueParser {

    <T> T parse(Object val, Class<T> clazz);
}
