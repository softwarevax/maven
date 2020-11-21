/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50550
Source Host           : localhost:3306
Source Database       : app

Target Server Type    : MYSQL
Target Server Version : 50550
File Encoding         : 65001

Date: 2020-11-21 15:42:14
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for app_clock_record
-- ----------------------------
DROP TABLE IF EXISTS `app_clock_record`;
CREATE TABLE `app_clock_record` (
  `id` varchar(255) COLLATE utf8_bin NOT NULL,
  `habit_id` varchar(255) COLLATE utf8_bin NOT NULL,
  `clock_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of app_clock_record
-- ----------------------------
INSERT INTO `app_clock_record` VALUES ('16fe10f5823d11ea94d354ee75dd4fca', 'd99839ee821511ea94d354ee75dd4fca', '2020-04-19 21:18:45');

-- ----------------------------
-- Table structure for app_habit
-- ----------------------------
DROP TABLE IF EXISTS `app_habit`;
CREATE TABLE `app_habit` (
  `id` varchar(255) COLLATE utf8_bin NOT NULL,
  `habit_name` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `start_date` datetime DEFAULT NULL,
  `end_date` datetime DEFAULT NULL,
  `clock_start_time` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `clock_end_time` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `publish_time` datetime DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `create_user_id` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `update_user_id` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `state` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '1:进行中，2:完成，-1:中断',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of app_habit
-- ----------------------------
INSERT INTO `app_habit` VALUES ('d99839ee821511ea94d354ee75dd4fca', '哈哈哈', '2020-04-19 00:00:00', '2020-05-10 00:00:00', '22:00', '23:30', '2020-04-19 16:14:33', '2020-04-19 16:14:33', '1', '2020-04-19 16:14:33', '1', 'ON');

-- ----------------------------
-- Table structure for app_user
-- ----------------------------
DROP TABLE IF EXISTS `app_user`;
CREATE TABLE `app_user` (
  `id` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `name` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `create_time` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of app_user
-- ----------------------------
INSERT INTO `app_user` VALUES ('1', 'vax', '2020-05-10 18:27:01');

-- ----------------------------
-- Table structure for sys_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `label` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `value` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `type` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `description` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `sort` int(11) DEFAULT NULL,
  `parent_id` int(11) DEFAULT '0',
  `app_id` varchar(255) COLLATE utf8_bin DEFAULT '',
  `create_user_id` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `update_user_id` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `remarks` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `del_flag` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of sys_config
-- ----------------------------
INSERT INTO `sys_config` VALUES ('1', '男', 'MAN', 'sex', '性别', '1', '0', 'common', '1', '1', '2020-05-10 18:38:07', '2020-05-10 18:38:09', '性别:男', '0');
INSERT INTO `sys_config` VALUES ('2', '进行中', 'ON', 'habit_state', '习惯状态', '1', '0', 'common', '1', '1', '2020-05-10 18:44:05', '2020-05-10 18:44:08', '习惯状态:进行中', '0');
