package com.github.softwarevax.dict.core.event;

/**
 * @author ctw
 * 2020/11/21 12:12
 */
public interface BeforeInvokeEvent extends DictionaryEvent {
    @Override
    default DictionaryEventType getEventType() {
        return DictionaryEventType.BEFORE_INVOKE;
    }
}
