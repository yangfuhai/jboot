
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

CREATE TABLE `undo_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `branch_id` bigint(20) NOT NULL,
  `xid` varchar(100) NOT NULL,
  `rollback_info` longblob NOT NULL,
  `log_status` int(11) NOT NULL,
  `log_created` datetime NOT NULL,
  `log_modified` datetime NOT NULL,
  `ext` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ux_undo_log` (`xid`,`branch_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
