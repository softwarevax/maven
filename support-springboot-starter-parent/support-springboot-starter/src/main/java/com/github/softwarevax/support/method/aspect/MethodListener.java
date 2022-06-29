package com.github.softwarevax.support.method.aspect;

import com.github.softwarevax.support.method.bean.InvokeMethod;

/**
 * @author twcao
 * @title: MethodListener
 * @projectName plugin-parent
 * @description: 方法监听
 * @date 2022/6/29 17:54
 */
public interface MethodListener {

    void callBack(InvokeMethod method);
}
