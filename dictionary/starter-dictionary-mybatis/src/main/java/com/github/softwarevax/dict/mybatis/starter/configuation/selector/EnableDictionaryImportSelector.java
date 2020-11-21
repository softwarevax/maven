package com.github.softwarevax.dict.mybatis.starter.configuation.selector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.MultiValueMap;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author twcao
 * @Title: EnableUaacImportSelector
 * @ProjectName support-spring-boot-starter
 * @Description: uaac配置文件导入类
 * @date 2018/12/6/006 22:59
 * @company iflytek
 */
public class EnableDictionaryImportSelector implements ImportSelector {

    private static final Log logger = LogFactory.getLog(EnableDictionaryImportSelector.class);

    /**
     * support配置类
     */
    public static final String DICTIONARY_DEFAULT_CONFIGURATION = "com.github.softwarevax.dict.mybatis.starter.configuation.DictionaryAutoConfiguration";

    /**
     * support启动配置
     */
    public static final String DICTIONARY_ENABLE_ANNOTATION = "com.github.softwarevax.dict.mybatis.starter.configuation.annoation.EnableDictionary";

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        MultiValueMap<String, Object> valueMap =  importingClassMetadata.getAllAnnotationAttributes(DICTIONARY_ENABLE_ANNOTATION);
        List<Object> enableFalgList = valueMap.get("value");
        boolean enableFlag = (boolean) enableFalgList.get(0);
        if(!enableFlag) {
            return new String[]{};
        }
        Set<String> configuration = new HashSet<>();
        configuration.add(DICTIONARY_DEFAULT_CONFIGURATION);
        String[] configComponent =new String[configuration.size()];
        configuration.toArray(configComponent);
        return enableFlag ? new String[]{DICTIONARY_DEFAULT_CONFIGURATION} : new String[]{};
    }
}
