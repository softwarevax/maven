package com.github.softwarevax.dict.mybatis.starter.web;

import com.github.softwarevax.dict.core.DictionaryHelper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 字典接口
 * @author ctw
 * 2020/11/22 21:01
 */
@RestController
public class DictionaryController {

    @RequestMapping("/dict/refresh")
    public boolean refresh() {
        DictionaryHelper.reLoad();
        return true;
    }
}
