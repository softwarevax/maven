-- mysql 建表脚本
-- lock
DROP TABLE IF EXISTS `distribute_lock`;
CREATE TABLE `distribute_lock`  (
    `lock_key` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '锁名称',
    `lock_val` tinyint(255) DEFAULT NULL COMMENT '值',
    `create_time` bigint NULL DEFAULT NULL COMMENT '更新时间',
    UNIQUE INDEX `KEY_INDEX`(`lock_key`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

-- t_method，方法，每次启动，只有一个相同的方法，根据full_method_name判断
DROP TABLE IF EXISTS `t_method`;
CREATE TABLE `t_method` (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `application` varchar(100) COLLATE utf8_bin DEFAULT '' COMMENT '应用名spring.application.name, 如果没设置，则取contextPath',
    `launch_time` varchar(20) COLLATE utf8_bin DEFAULT '' COMMENT '启动时间，格式：yyyyMMddHHmmss',
    `expose` tinyint(1) DEFAULT NULL COMMENT '是否是接口',
    `method` varchar(200) COLLATE utf8_bin DEFAULT '' COMMENT '方法简称',
    `method_tag` varchar(200) COLLATE utf8_bin DEFAULT '' COMMENT '方法标记',
    `full_method_name` varchar(800) COLLATE utf8_bin DEFAULT '' COMMENT '方法全称',
    `return_type` varchar(200) COLLATE utf8_bin DEFAULT '' COMMENT '返回类型',
    `parameter` varchar(500) COLLATE utf8_bin DEFAULT '' COMMENT '参数列表',
    `create_time` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='应用方法';

-- t_method_invoke，方法调用，
DROP TABLE IF EXISTS `t_method_invoke`;
CREATE TABLE `t_method_invoke` (
   `id` int(11) NOT NULL AUTO_INCREMENT,
   `session_id` varchar(40) COLLATE utf8_bin DEFAULT '' COMMENT '会话id',
   `invoke_id` bigint(20) DEFAULT NULL COMMENT '调用id，同一个请求，该值相同',
   `method_id` int(11) DEFAULT NULL COMMENT '方法id,见表t_method',
   `expose` tinyint(1) DEFAULT NULL COMMENT '是否是接口',
   `parameter_val` varchar(4000) COLLATE utf8_bin DEFAULT '' COMMENT '参数值，多个参数会转为json字符串，超过4000字符，会被截取',
   `return_val` varchar(4000) COLLATE utf8_bin DEFAULT '' COMMENT '同参数值',
   `start_time` datetime DEFAULT NULL COMMENT '方法开始时间',
   `elapsed_time` int(11) DEFAULT NULL COMMENT '运行时长，单位：毫秒',
   `create_time` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
   PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='应用方法调用';

-- t_method_interface，接口，每次启动，只有一个相同的接口，根据full_method_name判断，每次请求，会产生最多一个接口
DROP TABLE IF EXISTS `t_method_interface`;
CREATE TABLE `t_method_interface` (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `method_id` int(11) DEFAULT NULL COMMENT '方法id, t_method.id',
    `method` varchar(20) COLLATE utf8_bin DEFAULT '' COMMENT '请求方式，get,post。。。，如果有支持多个，逗号分割',
    `mappings` varchar(500) COLLATE utf8_bin DEFAULT '' COMMENT '请求路径，不含contextPath，如果支持多个，逗号分割',
    `create_time` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='应用接口方法';

DROP TABLE IF EXISTS `t_method_interface_invoke`;
CREATE TABLE `t_method_interface_invoke` (
     `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
     `invoke_id` int(11) DEFAULT NULL COMMENT 't_method_invoke.id',
     `user_id` varchar(50) DEFAULT '' COMMENT '用户标识，可以使用其他类型，如bigint，默认为varchar',
     `scheme` varchar(40) COLLATE utf8_bin DEFAULT '' COMMENT '协议',
     `method` varchar(20) COLLATE utf8_bin DEFAULT '' COMMENT '方法，get，post。。。',
     `remote_addr` varchar(50) COLLATE utf8_bin DEFAULT '' COMMENT '请求端地址',
     `headers` varchar(2000) COLLATE utf8_bin DEFAULT '' COMMENT '请求头，超过2000被截取',
     `payload` varchar(4000) COLLATE utf8_bin DEFAULT '' COMMENT '静荷载，超过4000被截取',
     `response_status` int(11) DEFAULT NULL COMMENT '响应码',
     `response_body` varchar(4000) COLLATE utf8_bin DEFAULT '' COMMENT '响应体',
     PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='应用接口方法调用';


/*
表关系
select tm.full_method_name                  '方法全名',
        tm.method                            '简称',
        minvoke.parameter_val                '请求参数',
        minvoke.invoke_id                    '调用id',
        if(minvoke.expose = '1', '是', '否') '接口',
        minvoke.return_val                   '返回值',
        minvoke.elapsed_time                 '执行时间(ms)',
        tm.method_tag                        '操作',
        interface.mappings                   '接口映射',
        iinvoke.headers                      '请求头',
        iinvoke.user_id                      '用户id',
        iinvoke.payload                      '负载',
        iinvoke.remote_addr                  '请求地址',
        iinvoke.response_status              '响应状态'
from t_method_invoke minvoke
     left join t_method tm on minvoke.method_id = tm.id
     left join t_method_interface interface on tm.id = interface.method_id
     left join t_method_interface_invoke iinvoke on iinvoke.invoke_id = minvoke.id
order by minvoke.invoke_id;
*/
