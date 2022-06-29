package com.github.softwarevax.support.demo.custom;

import com.github.softwarevax.support.method.aspect.MethodListener;
import com.github.softwarevax.support.method.bean.InvokeMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author twcao
 * @title: MyMethodListener
 * @projectName plugin-parent
 * @description: TODO
 * @date 2022/6/29 18:03
 */
public class MyMethodListener implements MethodListener {

    private Logger logger = LoggerFactory.getLogger(MyMethodListener.class);

    @Override
    public void callBack(InvokeMethod method) {
        logger.info("方法[{}]耗时{}毫秒", method.getFullMethodName(), method.getElapsedTime());
    }
}
