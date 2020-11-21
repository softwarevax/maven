package com.github.softwarevax.dict.core.interfaces;

import java.util.List;
import java.util.Map;

public interface DictionaryLoader {

    /**
     * 新增字典表
     * @param dictTable
     * @return
     */
    boolean addDictionaryTable(DictionaryTable dictTable);

    /**
     * 加载/刷新缓存
     * @return
     */
    Map<DictionaryTable, List<Map<String, Object>>> dictLoader();
}
