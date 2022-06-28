-- mysql 脚本
DROP TABLE IF EXISTS `distribute_lock`;
CREATE TABLE `distribute_lock`  (
  `lock_key` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '锁名称',
  `lock_val` tinyint(255) NULL DEFAULT 1 COMMENT '值',
  `create_time` bigint NULL DEFAULT NULL COMMENT '更新时间',
  UNIQUE INDEX `KEY_INDEX`(`lock_key`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;