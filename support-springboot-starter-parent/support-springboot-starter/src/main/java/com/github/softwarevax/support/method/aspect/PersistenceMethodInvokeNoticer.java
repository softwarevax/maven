package com.github.softwarevax.support.method.aspect;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.softwarevax.support.application.PropertyKey;
import com.github.softwarevax.support.application.SupportHolder;
import com.github.softwarevax.support.method.MethodSQL;
import com.github.softwarevax.support.method.bean.*;
import com.github.softwarevax.support.method.configuration.MethodConstant;
import com.github.softwarevax.support.utils.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;

import java.lang.reflect.AnnotatedElement;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * 1、待完成：插入语句需要批量操作，以提高效率，id需要程序生成，因为有联表数据
 */
public class PersistenceMethodInvokeNoticer implements MethodInvokeNoticer {

    private Logger logger = LoggerFactory.getLogger(PersistenceMethodInvokeNoticer.class);

    private Map<String, MethodPo> methodMaps = new HashMap<>();

    private JdbcTemplate template;

    private Lock lock = new ReentrantLock();

    @Override
    public void callBack(InvokeMethod method) {
        Assert.notNull(template, "JdbcTemplate获取失败");
        MethodPo staticInfo = getMethodStaticInfo(method);
        alreadyIn:
        if(!methodMaps.containsKey(staticInfo.getFullMethodName())) {
            // 若此时两个线程(相同方法,launch_time + full_method_name相等)同时进入
            lock.lock();
            try {
                // thread2:后进入的线程发现map中已经包含了此方法，则跳出去
                if(methodMaps.containsKey(staticInfo.getFullMethodName())) {
                   break alreadyIn;
                }
                // thread1:先进入的线程将方法信息放入map，并完成数据库插入操作
                methodMaps.put(staticInfo.getFullMethodName(), staticInfo);
            } finally {
                lock.unlock();
            }
            insertMethod(staticInfo);
            insertMethodInterface(method, staticInfo.getId());
        }
        DynamicInfoMethod methodDynamicInfo = getMethodDynamicInfo(method);
        insertMethodInvoke(methodDynamicInfo);
        insertMethodInterfaceInvoke(method, methodDynamicInfo.getId());
    }

    /**
     * 获取方法的静态信息
     * @param method
     * @return 方法详情
     */
    private MethodPo getMethodStaticInfo(InvokeMethod method) {
        MethodPo po = new MethodPo();
        MethodConstant constant = SupportHolder.getInstance().get(PropertyKey.METHOD_CONSTANT);
        Class<AnnotatedElement> methodTag = constant.getMethodTag();
        if(method.getAnnotations().containsKey(methodTag)) {
            Map<String, Object> annotationMap = method.getAnnotations().get(methodTag);
            po.setMethodNTag(StringUtils.getFirstNotBlank(String.valueOf(annotationMap.get("value")), String.valueOf(annotationMap.get("name"))));
        }
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

    public boolean checkTable(MethodConstant constant) {
        // 检查相关的表
        try {
            // 检查
            template.execute(MethodSQL.CHECK_METHOD_SQL);
            template.execute(MethodSQL.CHECK_METHOD_INVOKE_SQL);
            template.execute(MethodSQL.CHECK_METHOD_INTERFACE_SQL);
            template.execute(MethodSQL.CHECK_METHOD_INTERFACE_INVOKE_SQL);
            // 是否清空表
            if(constant.getResetEveryTime()) {
                template.execute(MethodSQL.TRUNCATE_METHOD_SQL);
                template.execute(MethodSQL.TRUNCATE_METHOD_INVOKE_SQL);
                template.execute(MethodSQL.TRUNCATE_METHOD_INTERFACE_SQL);
                template.execute(MethodSQL.TRUNCATE_METHOD_INTERFACE_INVOKE_SQL);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            try {
                Resource resource = new ClassPathResource("docs/scripts.sql");
                byte[] bytes = FileCopyUtils.copyToByteArray(resource.getInputStream());
                String content = new String(bytes, "utf-8");
                logger.error(content);
            } catch (Exception e1) {
                logger.error(e1.getMessage(), e1);
            }
            return false;
        }
        return true;
    }

    /**
     * 序列化参数
     * @param argNames
     * @param argValues
     * @return 参数
     */
    private String getParameter(String[] argNames, Object[] argValues) {
        JSONObject obj = new JSONObject();
        Assert.isTrue(ArrayUtils.getLength(argNames) == ArrayUtils.getLength(argValues), "参数名和参数值数量不匹配");
        int length = ArrayUtils.getLength(argNames);
        for (int i = 0; i < length; i++) {
            obj.put(argNames[i], argValues[i]);
        }
        try {
            return obj.toJSONString();
        } catch (Exception e) {
            return StringUtils.EMPTY;
        }
    }

    public boolean insertMethod(MethodPo po) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        Object[] sqlArgs = po.getSQLArgs();
        int rowNum = template.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(MethodSQL.INSERT_METHOD_SQL, Statement.RETURN_GENERATED_KEYS);
            ps.setObject(1, sqlArgs[0]);
            ps.setObject(2, sqlArgs[1]);
            ps.setObject(4, sqlArgs[3]);
            ps.setObject(3, sqlArgs[2]);
            ps.setObject(5, sqlArgs[4]);
            ps.setObject(6, sqlArgs[5]);
            ps.setObject(7, sqlArgs[6]);
            ps.setObject(8, sqlArgs[7]);
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
            ps.setObject(1, sqlArgs[0]);
            ps.setObject(2, sqlArgs[1]);
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
