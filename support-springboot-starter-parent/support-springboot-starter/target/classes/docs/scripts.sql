-- mysql 脚本
-- lock
DROP TABLE IF EXISTS `distribute_lock`;
CREATE TABLE `distribute_lock`  (
  `lock_key` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '锁名称',
  `lock_val` tinyint(255) NULL DEFAULT 1 COMMENT '值',
  `create_time` bigint NULL DEFAULT NULL COMMENT '更新时间',
  UNIQUE INDEX `KEY_INDEX`(`lock_key`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

-- method
CREATE TABLE `t_method` (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `application` varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '应用名',
    `method` varchar(200) COLLATE utf8_bin DEFAULT NULL COMMENT '方法简称',
    `full_method_name` varchar(500) COLLATE utf8_bin DEFAULT NULL COMMENT '方法全称',
    `return_type` varchar(200) COLLATE utf8_bin DEFAULT NULL COMMENT '返回类型',
    `parameter` varchar(500) COLLATE utf8_bin DEFAULT NULL COMMENT '参数列表',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='应用方法';

