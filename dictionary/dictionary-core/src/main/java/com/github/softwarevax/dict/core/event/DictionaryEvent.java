package com.github.softwarevax.dict.core.event;


/**
 * @author ctw
 * @Project： plugin-parent
 * @Package: com.github.softwarevax.dict.core.event
 * @Description:
 * @date 2020/11/21 12:17
 */
public interface DictionaryEvent {

    DictionaryEventType getEventType();

    void callBack(Object obj);
}
