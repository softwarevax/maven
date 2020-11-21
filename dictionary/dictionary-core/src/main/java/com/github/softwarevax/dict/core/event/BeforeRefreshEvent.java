package com.github.softwarevax.dict.core.event;

/**
 * @author ctw
 * @Project： plugin-parent
 * @Package: com.github.softwarevax.dict.core.event
 * @Description:
 * @date 2020/11/21 12:24
 */
public interface BeforeRefreshEvent extends DictionaryEvent {
    @Override
    default DictionaryEventType getEventType() {
        return DictionaryEventType.BEFORE_REFRESH;
    }
}
