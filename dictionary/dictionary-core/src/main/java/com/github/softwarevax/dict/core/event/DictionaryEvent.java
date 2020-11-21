package com.github.softwarevax.dict.core.event;


/**
 * @author ctw
 * 2020/11/21 12:17
 */
public interface DictionaryEvent {

    DictionaryEventType getEventType();

    void callBack(Object obj);
}
