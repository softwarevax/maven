package com.github.softwarevax.dict.core.resolver;


import java.util.List;

/**
 * 2020/11/22 15:15
 */
public interface IConverter {
    <T> List<T> converter(Object t);
}
