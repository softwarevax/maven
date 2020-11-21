package com.github.softwarevax.dict.core.interfaces;

import java.util.List;
import java.util.Map;

public interface DictionaryLoader {

    /**
     * 新增字典表
     * @param dictTable 字典表
     * @return 是否添加成功
     */
    boolean addDictionaryTable(DictionaryTable dictTable);

    /**
     * 加载/刷新缓存
     * @return 加载后的缓存
     */
    Map<DictionaryTable, List<Map<String, Object>>> dictLoader();
}
