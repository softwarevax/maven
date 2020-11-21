package com.github.softwarevax.dict.core.event;

/**
 * @author ctw
 * 2020/11/21 12:26
 */
public interface AfterRefreshEvent extends DictionaryEvent {
    @Override
    default DictionaryEventType getEventType() {
        return DictionaryEventType.AFTER_REFRESH;
    }
}
