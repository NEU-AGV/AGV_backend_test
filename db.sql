-- 创建数据库
DROP DATABASE IF EXISTS agv;
CREATE DATABASE agv DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- 创建用户
DROP USER IF EXISTS 'agv'@'%';
CREATE USER 'agv'@'%' IDENTIFIED BY 'agv';

-- 授权
GRANT ALL PRIVILEGES ON agv.* TO 'agv'@'%';
FLUSH PRIVILEGES;

-- 切换数据库
USE agv;

-- 用户表
CREATE TABLE `user` (
                        `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
                        `username` VARCHAR(64) NOT NULL UNIQUE,
                        `real_name` VARCHAR(64),
                        `department_id` BIGINT,
                        `phone` VARCHAR(20),
                        `password` VARCHAR(255) NOT NULL,
                        `email` VARCHAR(100),
                        `role` VARCHAR(64),
                        `avatar_url` VARCHAR(255),
                        `status` VARCHAR(20),
                        `login_time` DATETIME,
                        `created_at` DATETIME,
                        `updated_at` DATETIME
);

-- 角色表
CREATE TABLE `role` (
                        `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
                        `role_name` VARCHAR(64) NOT NULL,
                        `role_code` VARCHAR(64) NOT NULL UNIQUE,
                        `description` VARCHAR(255),
                        `status` VARCHAR(20),
                        `created_at` DATETIME,
                        `updated_at` DATETIME
);

-- 用户角色关联表
CREATE TABLE `user_role` (
                             `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
                             `user_id` BIGINT NOT NULL,
                             `role_id` BIGINT NOT NULL,
                             `created_at` DATETIME,
                             UNIQUE KEY (`user_id`, `role_id`)
);

-- 部门表
CREATE TABLE `department` (
                              `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
                              `parent_id` BIGINT,
                              `dept_name` VARCHAR(100) NOT NULL,
                              `order_num` INT,
                              `leader` VARCHAR(64),
                              `phone` VARCHAR(20),
                              `email` VARCHAR(100),
                              `status` VARCHAR(20),
                              `created_at` DATETIME,
                              `updated_at` DATETIME
);

-- 菜单表
CREATE TABLE `menu` (
                        `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
                        `parent_id` BIGINT,
                        `menu_name` VARCHAR(100) NOT NULL,
                        `menu_type` VARCHAR(10),
                        `icon` VARCHAR(100),
                        `path` VARCHAR(255),
                        `component` VARCHAR(255),
                        `perms` VARCHAR(100),
                        `order_num` INT,
                        `status` VARCHAR(20),
                        `created_at` DATETIME,
                        `updated_at` DATETIME
);

-- 角色菜单表
CREATE TABLE `role_menu` (
                             `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
                             `role_id` BIGINT NOT NULL,
                             `menu_id` BIGINT NOT NULL,
                             `created_at` DATETIME,
                             UNIQUE KEY (`role_id`, `menu_id`)
);

-- 任务表
CREATE TABLE `task` (
                        `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
                        `task_id` VARCHAR(64) NOT NULL UNIQUE,
                        `task_name` VARCHAR(100),
                        `task_type` VARCHAR(64),
                        `priority` VARCHAR(20),
                        `description` TEXT,
                        `creator` VARCHAR(64),
                        `executor` VARCHAR(64),
                        `helper` VARCHAR(64),
                        `planned_start_time` DATETIME,
                        `planned_end_time` DATETIME,
                        `actual_start_time` DATETIME,
                        `actual_end_time` DATETIME,
                        `line` VARCHAR(64),
                        `start_location` VARCHAR(64),
                        `end_location` VARCHAR(64),
                        `scope` VARCHAR(64),
                        `status` VARCHAR(20),
                        `progress` INT,
                        `result` TEXT,
                        `problems_found` INT,
                        `upload_time` DATETIME,
                        `created_at` DATETIME,
                        `updated_at` DATETIME
);

-- 缺陷表
CREATE TABLE `defect` (
                          `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
                          `defect_id` VARCHAR(64) NOT NULL UNIQUE,
                          `task_id` VARCHAR(64),
                          `defect_type` VARCHAR(64),
                          `description` TEXT,
                          `location` VARCHAR(128),
                          `image_url` VARCHAR(255),
                          `severity` VARCHAR(20),
                          `is_verified` BOOLEAN,
                          `current_status` VARCHAR(64),
                          `discoverer` VARCHAR(64),
                          `discovery_time` DATETIME,
                          `discovery_method` VARCHAR(64),
                          `handler` VARCHAR(64),
                          `created_at` DATETIME,
                          `updated_at` DATETIME
);


