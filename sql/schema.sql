-- ========================================
-- 景区共享单车租赁系统 - 建表脚本
-- ========================================

CREATE DATABASE IF NOT EXISTS bike_app DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE bike_app;

-- ----------------------------------------
-- 1. 系统用户表
-- ----------------------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `password` VARCHAR(128) NOT NULL COMMENT '密码（BCrypt加密）',
  `nickname` VARCHAR(50) DEFAULT '' COMMENT '昵称',
  `phone` VARCHAR(20) DEFAULT '' COMMENT '手机号',
  `avatar` VARCHAR(255) DEFAULT '' COMMENT '头像URL',
  `user_type` TINYINT NOT NULL DEFAULT 0 COMMENT '类型：0-后台管理员, 1-小程序用户',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0-正常, 1-禁用',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- ----------------------------------------
-- 2. 角色表
-- ----------------------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `role_name` VARCHAR(50) NOT NULL COMMENT '角色名称',
  `role_code` VARCHAR(50) NOT NULL COMMENT '角色编码',
  `description` VARCHAR(200) DEFAULT '' COMMENT '描述',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0-正常, 1-禁用',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_code` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- ----------------------------------------
-- 3. 菜单表
-- ----------------------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `parent_id` BIGINT NOT NULL DEFAULT 0 COMMENT '父菜单ID',
  `menu_name` VARCHAR(50) NOT NULL COMMENT '菜单名称',
  `menu_type` TINYINT NOT NULL COMMENT '类型：0-目录, 1-菜单, 2-按钮',
  `path` VARCHAR(200) DEFAULT '' COMMENT '路由路径',
  `component` VARCHAR(200) DEFAULT '' COMMENT '组件路径',
  `permission` VARCHAR(100) DEFAULT '' COMMENT '权限标识',
  `icon` VARCHAR(50) DEFAULT '' COMMENT '图标',
  `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0-正常, 1-禁用',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单表';

-- ----------------------------------------
-- 4. 用户角色关联表
-- ----------------------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `role_id` BIGINT NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`user_id`, `role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- ----------------------------------------
-- 5. 角色菜单关联表
-- ----------------------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `role_id` BIGINT NOT NULL COMMENT '角色ID',
  `menu_id` BIGINT NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_menu` (`role_id`, `menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色菜单关联表';

-- ----------------------------------------
-- 6. 操作日志表
-- ----------------------------------------
DROP TABLE IF EXISTS `operation_log`;
CREATE TABLE `operation_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT DEFAULT NULL COMMENT '操作人ID',
  `username` VARCHAR(50) DEFAULT '' COMMENT '操作人用户名',
  `operation` VARCHAR(200) NOT NULL COMMENT '操作内容',
  `method` VARCHAR(200) DEFAULT '' COMMENT '请求方法',
  `params` TEXT COMMENT '请求参数',
  `ip` VARCHAR(50) DEFAULT '' COMMENT 'IP地址',
  `execute_time` INT DEFAULT 0 COMMENT '执行时长(ms)',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- ----------------------------------------
-- 7. 车辆表
-- ----------------------------------------
DROP TABLE IF EXISTS `bike`;
CREATE TABLE `bike` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `bike_code` VARCHAR(32) NOT NULL COMMENT '唯一标识码（对应二维码）',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0-待租, 1-使用中, 2-报修, 3-报废',
  `lng` DECIMAL(10,6) DEFAULT NULL COMMENT '当前经度',
  `lat` DECIMAL(10,6) DEFAULT NULL COMMENT '当前纬度',
  `area_id` BIGINT DEFAULT NULL COMMENT '所属停车区域ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_bike_code` (`bike_code`),
  KEY `idx_area_id` (`area_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='车辆表';

-- ----------------------------------------
-- 8. 景点表
-- ----------------------------------------
DROP TABLE IF EXISTS `scenic_spot`;
CREATE TABLE `scenic_spot` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL COMMENT '景点名称',
  `description` TEXT COMMENT '景点描述',
  `lng` DECIMAL(10,6) DEFAULT NULL COMMENT '经度',
  `lat` DECIMAL(10,6) DEFAULT NULL COMMENT '纬度',
  `cover_image` VARCHAR(255) DEFAULT '' COMMENT '封面图片URL',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='景点表';

-- ----------------------------------------
-- 9. 停车区域表
-- ----------------------------------------
DROP TABLE IF EXISTS `parking_area`;
CREATE TABLE `parking_area` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL COMMENT '区域名称',
  `center_lng` DECIMAL(10,6) DEFAULT NULL COMMENT '区域中心经度',
  `center_lat` DECIMAL(10,6) DEFAULT NULL COMMENT '区域中心纬度',
  `radius` INT NOT NULL DEFAULT 200 COMMENT '允许停车半径（米）',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='停车区域表';

-- ----------------------------------------
-- 10. 订单表
-- ----------------------------------------
DROP TABLE IF EXISTS `bike_order`;
CREATE TABLE `bike_order` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `order_no` VARCHAR(32) NOT NULL COMMENT '订单编号',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `bike_id` BIGINT NOT NULL COMMENT '车辆ID',
  `start_time` DATETIME NOT NULL COMMENT '开始骑行时间',
  `end_time` DATETIME DEFAULT NULL COMMENT '结束骑行时间',
  `start_lng` DECIMAL(10,6) DEFAULT NULL COMMENT '起始经度',
  `start_lat` DECIMAL(10,6) DEFAULT NULL COMMENT '起始纬度',
  `end_lng` DECIMAL(10,6) DEFAULT NULL COMMENT '结束经度',
  `end_lat` DECIMAL(10,6) DEFAULT NULL COMMENT '结束纬度',
  `duration` INT DEFAULT NULL COMMENT '骑行时长（分钟）',
  `amount` DECIMAL(8,2) DEFAULT NULL COMMENT '订单金额（元）',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0-骑行中, 1-已完成, 2-已取消',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_bike_id` (`bike_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- ----------------------------------------
-- 11. 报修表
-- ----------------------------------------
DROP TABLE IF EXISTS `bike_repair`;
CREATE TABLE `bike_repair` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `bike_id` BIGINT NOT NULL COMMENT '车辆ID',
  `user_id` BIGINT DEFAULT NULL COMMENT '上报用户ID',
  `description` TEXT COMMENT '故障描述',
  `images` VARCHAR(1000) DEFAULT '' COMMENT '图片URL列表（逗号分隔）',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0-待处理, 1-处理中, 2-已完成',
  `admin_remark` VARCHAR(255) DEFAULT '' COMMENT '管理员处理备注',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `handle_time` DATETIME DEFAULT NULL COMMENT '处理时间',
  PRIMARY KEY (`id`),
  KEY `idx_bike_id` (`bike_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报修表';
