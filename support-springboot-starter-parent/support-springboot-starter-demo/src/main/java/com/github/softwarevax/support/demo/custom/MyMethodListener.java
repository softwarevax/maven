package com.github.softwarevax.support.demo.custom;

import com.github.softwarevax.support.method.aspect.MethodListener;
import com.github.softwarevax.support.method.bean.InvokeMethod;

/**
 * @author twcao
 * @title: MyMethodListener
 * @projectName plugin-parent
 * @description: TODO
 * @date 2022/6/29 18:03
 */
public class MyMethodListener implements MethodListener {
    @Override
    public void callBack(InvokeMethod method) {
        System.out.println(method.getFullMethodName() + ":" + method.getElapsedTime());
    }
}
