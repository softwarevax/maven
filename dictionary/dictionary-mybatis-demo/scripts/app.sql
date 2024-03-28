/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80012 (8.0.12)
 Source Host           : localhost:3306
 Source Schema         : dictionary

 Target Server Type    : MySQL
 Target Server Version : 80012 (8.0.12)
 File Encoding         : 65001

 Date: 28/03/2024 16:18:10
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for app_habit
-- ----------------------------
DROP TABLE IF EXISTS `app_habit`;
CREATE TABLE `app_habit`  (
                            `id` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                            `habit_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                            `create_time` datetime NULL DEFAULT NULL,
                            `create_user_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                            `update_time` datetime NULL DEFAULT NULL,
                            `update_user_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                            `state` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '1:进行中，2:完成，-1:中断',
                            PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of app_habit
-- ----------------------------
INSERT INTO `app_habit` VALUES ('d99839ee821511ea94d354ee75dd4fca', '早起', '2020-04-19 16:14:33', '1', '2020-04-19 16:14:33', '1', '1');

-- ----------------------------
-- Table structure for app_user
-- ----------------------------
DROP TABLE IF EXISTS `app_user`;
CREATE TABLE `app_user`  (
                           `id` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                           `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                           `create_time` datetime NULL DEFAULT NULL,
                           `state` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                           `sex` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of app_user
-- ----------------------------
INSERT INTO `app_user` VALUES ('1', '枫叶', '2020-05-10 18:27:01', 'ON', 'MAN');
INSERT INTO `app_user` VALUES ('2', '张三', '2024-03-28 10:24:20', 'OFF', 'WOMAN');

-- ----------------------------
-- Table structure for sys_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config`  (
                             `id` int(11) NOT NULL AUTO_INCREMENT,
                             `label` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                             `value` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                             `type` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                             `description` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                             `sort` int(11) NULL DEFAULT NULL,
                             `parent_id` int(11) NULL DEFAULT 0,
                             `app_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT '',
                             `create_user_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                             `update_user_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                             `create_time` datetime NULL DEFAULT NULL,
                             `update_time` datetime NULL DEFAULT NULL,
                             `remarks` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                             `del_flag` tinyint(4) NULL DEFAULT NULL,
                             PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_config
-- ----------------------------
INSERT INTO `sys_config` VALUES (1, '男', 'MAN', 'sex', '性别', 1, 0, 'common', '1', '1', '2020-05-10 18:38:07', '2020-05-10 18:38:09', '性别:男', 0);
INSERT INTO `sys_config` VALUES (2, '女', 'WOMAN', 'sex', '性别', 2, 0, 'common', '1', '1', '2024-03-28 10:26:18', '2024-03-28 10:26:21', '性别:女', 0);
INSERT INTO `sys_config` VALUES (3, '进行中', '1', 'habit_state', '习惯状态', 1, 0, 'common', '1', '1', '2020-05-10 18:44:05', '2020-05-10 18:44:08', '习惯状态:进行中', 0);
INSERT INTO `sys_config` VALUES (4, '完成', '2', 'habit_state', '习惯状态', 2, 0, 'common', '1', '1', '2020-05-10 18:44:05', '2020-05-10 18:44:08', '习惯状态:完成', 0);
INSERT INTO `sys_config` VALUES (5, '中断', '-1', 'habit_state', '习惯状态', 2, 0, 'common', '1', '1', '2020-05-10 18:44:05', '2020-05-10 18:44:08', '习惯状态:中断', 1);
INSERT INTO `sys_config` VALUES (6, '登录中', 'ON', 'user_state', '登录状态', 1, 0, 'common', '1', '1', '2020-05-10 18:44:05', '2020-05-10 18:44:08', '登录状态:登录中', 0);
INSERT INTO `sys_config` VALUES (7, '离线', 'OFF', 'user_state', '登录状态', 1, 0, 'common', '1', '1', '2020-05-10 18:44:05', '2020-05-10 18:44:08', '登录状态:登录中', 0);

SET FOREIGN_KEY_CHECKS = 1;
