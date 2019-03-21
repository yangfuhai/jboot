/*
 Navicat Premium Data Transfer

 Source Server         : 39.107.108.127
 Source Server Type    : MySQL
 Source Server Version : 50724
 Source Host           : 39.107.108.127:9527
 Source Schema         : ilife-mall2.0

 Target Server Type    : MySQL
 Target Server Version : 50724
 File Encoding         : 65001

 Date: 21/03/2019 14:23:54
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for fescar_account
-- ----------------------------
DROP TABLE IF EXISTS `fescar_account`;
CREATE TABLE `fescar_account`  (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Account` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `Money` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`ID`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of fescar_account
-- ----------------------------
INSERT INTO `fescar_account` VALUES (1, 'Hobbit', 1000);

-- ----------------------------
-- Table structure for fescar_stock
-- ----------------------------
DROP TABLE IF EXISTS `fescar_stock`;
CREATE TABLE `fescar_stock`  (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Account` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `Stock` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`ID`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of fescar_stock
-- ----------------------------
INSERT INTO `fescar_stock` VALUES (1, 'Hobbit', 1000);

SET FOREIGN_KEY_CHECKS = 1;
