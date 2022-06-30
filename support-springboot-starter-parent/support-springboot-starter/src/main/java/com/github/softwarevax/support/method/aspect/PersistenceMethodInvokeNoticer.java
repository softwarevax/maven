package com.github.softwarevax.support.method.aspect;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.softwarevax.support.method.MethodSQL;
import com.github.softwarevax.support.method.bean.*;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.util.Assert;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class PersistenceMethodInvokeNoticer implements MethodInvokeNoticer {

    private Logger logger = LoggerFactory.getLogger(PersistenceMethodInvokeNoticer.class);

    private Map<String, MethodPo> methodMaps = new HashMap<>();

    private JdbcTemplate template;

    @Override
    public void callBack(InvokeMethod method) {
        Assert.notNull(template, "JdbcTemplate获取失败");
        MethodPo staticInfo = getMethodStaticInfo(method);
        if(!methodMaps.containsKey(staticInfo.getFullMethodName())) {
            insertMethod(staticInfo);
            insertMethodInterface(method, staticInfo.getId());
            methodMaps.put(staticInfo.getFullMethodName(), staticInfo);
        }
        DynamicInfoMethod methodDynamicInfo = getMethodDynamicInfo(method);
        insertMethodInvoke(methodDynamicInfo);
        insertMethodInterfaceInvoke(method, methodDynamicInfo.getId());;
    }

    /**
     * 获取方法的静态信息
     * @param method
     * @return
     */
    private MethodPo getMethodStaticInfo(InvokeMethod method) {
        MethodPo po = new MethodPo();
        po.setApplication(method.getApplication());
        po.setLaunchTime(method.getLaunchTime());
        po.setExpose(method.getExpose());
        po.setMethod(method.getMethodName());
        po.setFullMethodName(method.getFullMethodName());
        po.setReturnType(method.getReturnType());
        po.setParameter(method.getArg());
        return po;
    }

    private DynamicInfoMethod getMethodDynamicInfo(InvokeMethod method) {
        DynamicInfoMethod dynamicInfo = new DynamicInfoMethod();
        dynamicInfo.setElapsedTime(method.getElapsedTime());
        dynamicInfo.setInvokeId(method.getInvokeId());
        dynamicInfo.setExpose(method.getExpose());
        dynamicInfo.setStartTime(new Date(method.getStartTime()));
        MethodPo methodPo = methodMaps.get(method.getFullMethodName());
        dynamicInfo.setSessionId(method.getSessionId());
        dynamicInfo.setMethodId(methodPo.getId());
        String parameter = getParameter(method.getArgs(), method.getArgsObj());
        if(StringUtils.length(parameter) > MethodSQL.FIELD_MAX_LENGTH) {
            parameter = StringUtils.substring(parameter, 0, MethodSQL.FIELD_MAX_LENGTH);
        }
        dynamicInfo.setParameterVal(parameter);
        String returnVal = JSON.toJSONString(method.getReturnObj());
        if(StringUtils.length(returnVal) > MethodSQL.FIELD_MAX_LENGTH) {
            returnVal = StringUtils.substring(returnVal, 0, MethodSQL.FIELD_MAX_LENGTH);
        }
        dynamicInfo.setReturnVal(returnVal);
        dynamicInfo.setSessionId(method.getSessionId());
        return dynamicInfo;
    }

    public void setTemplate(JdbcTemplate template) {
        this.template = template;
    }

    public boolean checkTable() {
        // 检查相关的表
        try {
            template.execute(MethodSQL.CHECK_SQL);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    /**
     * 序列化参数
     * @param argNames
     * @param argValues
     * @return
     */
    private String getParameter(String[] argNames, Object[] argValues) {
        JSONObject obj = new JSONObject();
        Assert.isTrue(ArrayUtils.getLength(argNames) == ArrayUtils.getLength(argValues), "参数名和参数值数量不匹配");
        int length = ArrayUtils.getLength(argNames);
        for (int i = 0; i < length; i++) {
            obj.put(argNames[i], argValues[i]);
        }
        return obj.toJSONString();
    }

    public boolean insertMethod(MethodPo po) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        Object[] sqlArgs = po.getSQLArgs();
        int rowNum = template.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(MethodSQL.INSERT_METHOD_SQL, Statement.RETURN_GENERATED_KEYS);
            ps.setObject(1, sqlArgs[0]);
            ps.setObject(2, sqlArgs[1]);
            ps.setObject(3, sqlArgs[2]);
            ps.setObject(4, sqlArgs[3]);
            ps.setObject(5, sqlArgs[4]);
            ps.setObject(6, sqlArgs[5]);
            ps.setObject(7, sqlArgs[6]);
            return ps;
        }, keyHolder);
        po.setId(keyHolder.getKey().intValue());
        return rowNum > 0;
    }

    public boolean insertMethodInterface(InvokeMethod method, int methodId) {
        if(!method.getExpose()) {
            return false;
        }
        MethodInterface methodInterface = new MethodInterface();
        WebInterface interfaces = method.getInterfaces();
        String methods = interfaces.getMapping().getMethodsCondition().getMethods().stream().map(row -> row.name()).collect(Collectors.joining(","));
        methodInterface.setMethod(methods);
        PatternsRequestCondition pattern = interfaces.getMapping().getPatternsCondition();
        String mappings = pattern.getPatterns().stream().collect(Collectors.joining(","));
        methodInterface.setMappings(mappings);
        methodInterface.setMethodId(methodId);
        int rowNum = template.update(MethodSQL.INSERT_METHOD_INTERFACE_SQL, methodInterface.getSQLArgs());
        return rowNum > 0;
    }

    public boolean insertMethodInvoke(DynamicInfoMethod dynamicInfo) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        Object[] sqlArgs = dynamicInfo.getSQLArgs();
        int rowNum = template.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(MethodSQL.INSERT_METHOD_INVOKE_SQL, Statement.RETURN_GENERATED_KEYS);
            ps.setObject(2, sqlArgs[1]);
            ps.setObject(1, sqlArgs[0]);
            ps.setObject(3, sqlArgs[2]);
            ps.setObject(4, sqlArgs[3]);
            ps.setObject(5, sqlArgs[4]);
            ps.setObject(6, sqlArgs[5]);
            ps.setObject(7, sqlArgs[6]);
            ps.setObject(8, sqlArgs[7]);
            return ps;
        }, keyHolder);
        dynamicInfo.setId(keyHolder.getKey().intValue());
        return rowNum > 0;
    }

    public boolean insertMethodInterfaceInvoke(InvokeMethod method, int invokeId) {
        if(!method.getExpose()) {
            return false;
        }
        MethodInterfaceInvoke interfaceInvoke = method.getInterfaceInvoke();
        interfaceInvoke.setInvokeId(invokeId);
        int rowNum = template.update(MethodSQL.INSERT_METHOD_INTERFACE_INVOKE_SQL, interfaceInvoke.getSQLArgs());
        return rowNum > 0;
    }
}
