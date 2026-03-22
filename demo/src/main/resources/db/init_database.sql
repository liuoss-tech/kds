-- =====================================================
-- 幼儿园管理系统数据库初始化脚本
-- =====================================================
CREATE DATABASE my_database;
USE my_database;
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 1. 系统权限模块表
-- ----------------------------

-- 用户表 (sys_user)
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) NOT NULL COMMENT '登录账号(建议用手机号)',
  `password` varchar(100) NOT NULL COMMENT '登录密码(需哈希加密)',
  `real_name` varchar(50) NOT NULL COMMENT '真实姓名',
  `phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `user_type` tinyint NOT NULL COMMENT '用户类型：1-内部教职工 2-家长',
  `status` tinyint DEFAULT '1' COMMENT '账号状态：0-禁用 1-正常',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '逻辑删除：0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- 角色表 (sys_role)
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_name` varchar(50) NOT NULL COMMENT '角色名称(如：班主任)',
  `role_code` varchar(50) NOT NULL COMMENT '角色编码(如：HEAD_TEACHER)',
  `data_scope` tinyint NOT NULL DEFAULT '4' COMMENT '数据范围：1-全园数据 2-本班数据 3-个人数据 4-仅限绑定关系(家长)',
  `description` varchar(255) DEFAULT NULL COMMENT '角色描述',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '逻辑删除：0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_code` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统角色表';

-- 菜单与权限表 (sys_permission)
DROP TABLE IF EXISTS `sys_permission`;
CREATE TABLE `sys_permission` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `parent_id` bigint DEFAULT '0' COMMENT '父级ID(顶级为0)',
  `perm_name` varchar(50) NOT NULL COMMENT '菜单或按钮名称(如：请假审批)',
  `perm_type` tinyint NOT NULL COMMENT '权限类型：1-目录 2-菜单 3-按钮(接口)',
  `route_path` varchar(100) DEFAULT NULL COMMENT '前端路由地址',
  `api_url` varchar(255) DEFAULT NULL COMMENT '后端接口路径(做越权校验使用)',
  `sort_order` int DEFAULT '0' COMMENT '排序',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '逻辑删除：0-未删除 1-已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单与权限表';

-- 用户-角色关联表 (sys_user_role)
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`user_id`,`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 角色-权限关联表 (sys_role_permission)
DROP TABLE IF EXISTS `sys_role_permission`;
CREATE TABLE `sys_role_permission` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `permission_id` bigint NOT NULL COMMENT '权限ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_perm` (`role_id`,`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

-- 站内消息与私信表 (sys_message)
DROP TABLE IF EXISTS `sys_message`;
CREATE TABLE `sys_message` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `sender_id` bigint NOT NULL COMMENT '发送者ID (0代表系统自动发送)',
  `receiver_id` bigint NOT NULL COMMENT '接收者ID',
  `message_type` tinyint NOT NULL COMMENT '消息类型：1-系统自动提醒(催办未读) 2-家校私信 3-审批结果通知',
  `content` varchar(1000) NOT NULL COMMENT '消息内容',
  `related_id` bigint DEFAULT NULL COMMENT '关联业务ID (如关联通知ID、请假单ID，方便点击跳转)',
  `is_read` tinyint DEFAULT '0' COMMENT '是否已读：0-未读 1-已读',
  `read_time` datetime DEFAULT NULL COMMENT '阅读时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_receiver_read` (`receiver_id`, `is_read`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='站内消息与私信表';

-- ----------------------------
-- 2. 基础业务数据表
-- ----------------------------

-- 班级表 (busi_class)
DROP TABLE IF EXISTS `busi_class`;
CREATE TABLE `busi_class` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `class_name` varchar(50) NOT NULL COMMENT '班级名称(如：大一班)',
  `grade` tinyint NOT NULL COMMENT '年级：1-托班 2-小班 3-中班 4-大班',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '逻辑删除：0-未删除 1-已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='班级信息表';

-- 幼儿表 (busi_student)
DROP TABLE IF EXISTS `busi_student`;
CREATE TABLE `busi_student` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `class_id` bigint NOT NULL COMMENT '所属班级ID',
  `student_name` varchar(50) NOT NULL COMMENT '幼儿姓名',
  `gender` tinyint DEFAULT '0' COMMENT '性别：1-男 2-女 0-未知',
  `birthday` date DEFAULT NULL COMMENT '出生日期',
  `id_card` varchar(20) DEFAULT NULL COMMENT '身份证号/护照号(敏感信息)',
  `admission_date` date DEFAULT NULL COMMENT '入园日期',
  `status` tinyint DEFAULT '1' COMMENT '在园状态：1-在读 2-毕业 3-退园',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '逻辑删除：0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_class_id` (`class_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='幼儿档案表';

-- 幼儿-家长关联表 (busi_student_parent)
DROP TABLE IF EXISTS `busi_student_parent`;
CREATE TABLE `busi_student_parent` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `student_id` bigint NOT NULL COMMENT '幼儿ID',
  `parent_user_id` bigint NOT NULL COMMENT '家长用户ID (关联 sys_user 表的 id)',
  `relation` varchar(20) NOT NULL COMMENT '亲属关系 (如：爸爸、妈妈、爷爷)',
  `is_primary` tinyint DEFAULT '0' COMMENT '是否主监护人：0-否 1-是 (用于紧急情况首选联系)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '绑定时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_student_parent` (`student_id`, `parent_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='幼儿与家长绑定关系表';

-- 教师-班级任教关联表 (busi_teacher_class)
DROP TABLE IF EXISTS `busi_teacher_class`;
CREATE TABLE `busi_teacher_class` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `teacher_user_id` bigint NOT NULL COMMENT '教师用户ID (关联 sys_user 表的 id)',
  `class_id` bigint NOT NULL COMMENT '班级ID',
  `is_head_teacher` tinyint DEFAULT '0' COMMENT '是否为该班班主任：0-否 1-是',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '分配时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_teacher_class` (`teacher_user_id`, `class_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教师任教班级分配表';

-- ----------------------------
-- 3. 生活记录模块表
-- ----------------------------

-- 幼儿每日生活状态记录表 (busi_life_record)
DROP TABLE IF EXISTS `busi_life_record`;
CREATE TABLE `busi_life_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `student_id` bigint NOT NULL COMMENT '幼儿ID (关联 busi_student)',
  `class_id` bigint NOT NULL COMMENT '班级ID (冗余字段，极大提升按班级查报表的性能)',
  `record_date` date NOT NULL COMMENT '记录日期 (如：2024-05-20)',
  `lunch_intake` tinyint DEFAULT '1' COMMENT '午餐摄入量：1-全吃光 2-吃大半 3-吃小半 4-没胃口',
  `water_count` int DEFAULT '0' COMMENT '喝水次数',
  `sleep_start_time` time DEFAULT NULL COMMENT '入睡时间 (如：12:30:00)',
  `sleep_duration` int DEFAULT '0' COMMENT '睡眠时长(分钟)',
  `sleep_quality` tinyint DEFAULT '1' COMMENT '睡眠质量：1-深睡 2-浅睡 3-易醒',
  `toilet_count` int DEFAULT '0' COMMENT '如厕次数',
  `toilet_abnormal` varchar(100) DEFAULT '' COMMENT '异常情况：可存逗号拼接的字典值，如"便秘,腹泻"，为空表示正常',
  `morning_temp` decimal(4,1) DEFAULT NULL COMMENT '晨检体温 (如：36.5)',
  `afternoon_temp` decimal(4,1) DEFAULT NULL COMMENT '午检体温',
  `health_symptoms` varchar(255) DEFAULT '' COMMENT '不适症状：存多选选项，如"咳嗽,流涕"',
  `teacher_remark` varchar(500) DEFAULT NULL COMMENT '特殊情况文字说明',
  `creator_id` bigint NOT NULL COMMENT '录入教师的系统用户ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '逻辑删除：0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_student_date` (`student_id`, `record_date`),
  KEY `idx_class_date` (`class_id`, `record_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='幼儿每日生活记录表';

-- ----------------------------
-- 4. 考勤与请假模块表
-- ----------------------------

-- 请假申请表 (busi_leave_application)
DROP TABLE IF EXISTS `busi_leave_application`;
CREATE TABLE `busi_leave_application` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `student_id` bigint NOT NULL COMMENT '幼儿ID',
  `class_id` bigint NOT NULL COMMENT '班级ID(冗余字段，方便按班级查询待办列表)',
  `parent_user_id` bigint NOT NULL COMMENT '申请人(家长用户ID)',
  `leave_type` tinyint NOT NULL COMMENT '请假类型：1-事假 2-病假 3-其他',
  `start_date` date NOT NULL COMMENT '开始日期',
  `end_date` date NOT NULL COMMENT '结束日期',
  `leave_days` int NOT NULL COMMENT '请假天数(用于判断是否超过7天触发二级审批)',
  `reason` varchar(500) DEFAULT NULL COMMENT '请假事由/备注',
  `proof_url` varchar(255) DEFAULT NULL COMMENT '证明材料图片路径(PRD要求不强制，可为空)',
  `audit_status` tinyint NOT NULL DEFAULT '0' COMMENT '审批状态：0-待审批 1-班主任已批(流程结束) 2-待园长审批(超7天) 3-园长已批(流程结束) 4-已驳回 5-家长已撤销',
  `teacher_id` bigint DEFAULT NULL COMMENT '审批班主任ID',
  `teacher_audit_time` datetime DEFAULT NULL COMMENT '班主任审批时间',
  `teacher_remark` varchar(255) DEFAULT NULL COMMENT '班主任审批意见',
  `admin_id` bigint DEFAULT NULL COMMENT '审批园长/管理员ID',
  `admin_audit_time` datetime DEFAULT NULL COMMENT '园长审批时间',
  `admin_remark` varchar(255) DEFAULT NULL COMMENT '园长审批意见',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '申请提交时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_student_id` (`student_id`),
  KEY `idx_class_status` (`class_id`, `audit_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='幼儿请假申请审批表';

-- 每日考勤记录表 (busi_attendance_record)
DROP TABLE IF EXISTS `busi_attendance_record`;
CREATE TABLE `busi_attendance_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `student_id` bigint NOT NULL COMMENT '幼儿ID',
  `class_id` bigint NOT NULL COMMENT '班级ID (冗余字段，极大提升班级出勤率报表统计速度)',
  `record_date` date NOT NULL COMMENT '考勤日期',
  `status` tinyint NOT NULL COMMENT '考勤状态：1-出勤 2-缺勤 3-事假 4-病假 5-其他请假',
  `leave_application_id` bigint DEFAULT NULL COMMENT '关联的请假单ID(status为3/4/5时有值)',
  `creator_id` bigint NOT NULL COMMENT '考勤记录人ID (系统自动打卡则为0或特定系统ID)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '记录时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_student_date` (`student_id`, `record_date`),
  KEY `idx_class_date` (`class_id`, `record_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='幼儿每日考勤记录表';

-- ----------------------------
-- 5. 通知公告模块表
-- ----------------------------

-- 通知公告发布表 (busi_notice)
DROP TABLE IF EXISTS `busi_notice`;
CREATE TABLE `busi_notice` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `title` varchar(100) NOT NULL COMMENT '通知标题',
  `content` text NOT NULL COMMENT '通知正文(支持富文本)',
  `notice_type` tinyint NOT NULL DEFAULT '1' COMMENT '类型：1-日常通知 2-缴费通知 3-放假通知',
  `notice_level` tinyint NOT NULL DEFAULT '1' COMMENT '级别：1-普通 2-置顶 3-强提醒(如紧急停课)',
  `target_scope` tinyint NOT NULL COMMENT '发送范围：1-全园 2-指定班级',
  `target_class_id` bigint DEFAULT NULL COMMENT '目标班级ID (当scope为2时必填)',
  `publisher_id` bigint NOT NULL COMMENT '发布人ID (园长或班主任)',
  `publish_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_target_class` (`target_class_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知公告表';

-- 通知接收与确认状态表 (busi_notice_receipt)
DROP TABLE IF EXISTS `busi_notice_receipt`;
CREATE TABLE `busi_notice_receipt` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `notice_id` bigint NOT NULL COMMENT '关联通知ID',
  `student_id` bigint NOT NULL COMMENT '幼儿ID (以幼儿为维度发送，因为一个幼儿可能有多个家长)',
  `parent_user_id` bigint DEFAULT NULL COMMENT '实际点击确认的家长用户ID',
  `is_confirmed` tinyint DEFAULT '0' COMMENT '是否已确认：0-未确认 1-已确认',
  `confirm_time` datetime DEFAULT NULL COMMENT '手动点击确认的时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_notice_student` (`notice_id`, `student_id`),
  KEY `idx_parent_confirm` (`parent_user_id`, `is_confirmed`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知接收与确认状态表';

-- ----------------------------
-- 6. 食谱与活动模块表
-- ----------------------------

-- 每周/每日食谱表 (busi_recipe)
DROP TABLE IF EXISTS `busi_recipe`;
CREATE TABLE `busi_recipe` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `class_id` bigint NOT NULL DEFAULT '0' COMMENT '适用班级ID：0表示全园通用食谱，非0表示特色班级专属食谱',
  `target_date` date NOT NULL COMMENT '食谱日期 (如：2024-05-20)',
  `breakfast` varchar(255) DEFAULT NULL COMMENT '早餐',
  `morning_snack` varchar(255) DEFAULT NULL COMMENT '早点',
  `lunch` varchar(255) DEFAULT NULL COMMENT '午餐',
  `afternoon_snack` varchar(255) DEFAULT NULL COMMENT '午点',
  `dinner` varchar(255) DEFAULT NULL COMMENT '晚餐(如有)',
  `publisher_id` bigint NOT NULL COMMENT '发布人(后勤/保健医生ID)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_date_class` (`target_date`, `class_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='每日食谱表';

-- 活动发布表 (busi_activity)
DROP TABLE IF EXISTS `busi_activity`;
CREATE TABLE `busi_activity` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `title` varchar(100) NOT NULL COMMENT '活动名称',
  `content` text NOT NULL COMMENT '活动图文详情',
  `activity_type` tinyint NOT NULL COMMENT '活动类型：1-亲子游戏 2-文艺汇演 3-家长会 4-户外踏青 (用于报表统计类型偏好)',
  `activity_time` datetime NOT NULL COMMENT '活动举办时间',
  `deadline_time` datetime NOT NULL COMMENT '报名截止时间',
  `target_scope` tinyint NOT NULL DEFAULT '1' COMMENT '面向范围：1-全园 2-指定班级',
  `target_class_id` bigint DEFAULT NULL COMMENT '指定班级ID(scope为2时有值)',
  `publisher_id` bigint NOT NULL COMMENT '发布教师ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='活动发布表';

-- 活动报名记录表 (busi_activity_registration)
DROP TABLE IF EXISTS `busi_activity_registration`;
CREATE TABLE `busi_activity_registration` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `activity_id` bigint NOT NULL COMMENT '关联活动ID',
  `student_id` bigint NOT NULL COMMENT '参与幼儿ID (按幼儿维度统计)',
  `parent_user_id` bigint NOT NULL COMMENT '操作报名的家长ID',
  `participant_count` tinyint NOT NULL DEFAULT '1' COMMENT '参加的家长人数 (例如：爸爸妈妈都去就是2)',
  `remark` varchar(255) DEFAULT NULL COMMENT '家长留言/备注',
  `status` tinyint DEFAULT '1' COMMENT '报名状态：1-已报名 2-已取消 (允许家长在截止前取消)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '报名时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_activity_student` (`activity_id`, `student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='活动报名记录表';

SET FOREIGN_KEY_CHECKS = 1;

-- 插入测试用户
INSERT INTO sys_user (username, password, real_name, phone, user_type, status) 
VALUES ('13800138000', '123456', '张老师', '13800138000', 1, 1);

-- 插入超级管理员用户
INSERT INTO sys_user (username, password, real_name, phone, user_type, status) 
VALUES ('admin', '123456', '系统管理员', '13900000000', 1, 1);

-- 插入角色
INSERT INTO sys_role (role_name, role_code, data_scope) VALUES ('超级管理员', 'SUPER_ADMIN', 1);
INSERT INTO sys_role (role_name, role_code, data_scope) VALUES ('园长', 'PRINCIPAL', 1);
INSERT INTO sys_role (role_name, role_code, data_scope) VALUES ('班主任', 'HEAD_TEACHER', 2);

-- 插入权限（菜单与权限表）- 根据实际路由配置
INSERT INTO sys_permission (id, parent_id, perm_name, perm_type, route_path, sort_order) VALUES (1, 0, '系统管理', 1, '', 1);
INSERT INTO sys_permission (id, parent_id, perm_name, perm_type, route_path, sort_order) VALUES (2, 1, '账号管理', 2, '/system/user', 1);
INSERT INTO sys_permission (id, parent_id, perm_name, perm_type, route_path, sort_order) VALUES (3, 1, '角色管理', 2, '/system/role', 2);
INSERT INTO sys_permission (id, parent_id, perm_name, perm_type, route_path, sort_order) VALUES (4, 0, '首页看板', 1, '', 2);
INSERT INTO sys_permission (id, parent_id, perm_name, perm_type, route_path, sort_order) VALUES (5, 4, '首页', 2, '/dashboard', 1);
INSERT INTO sys_permission (id, parent_id, perm_name, perm_type, route_path, sort_order) VALUES (6, 0, '业务管理', 1, '', 3);
INSERT INTO sys_permission (id, parent_id, perm_name, perm_type, route_path, sort_order) VALUES (7, 6, '班级管理', 2, '/busi/class', 1);
INSERT INTO sys_permission (id, parent_id, perm_name, perm_type, route_path, sort_order) VALUES (8, 6, '幼儿管理', 2, '/busi/student', 2);
INSERT INTO sys_permission (id, parent_id, perm_name, perm_type, route_path, sort_order) VALUES (9, 6, '食谱管理', 2, '/busi/recipe', 3);
INSERT INTO sys_permission (id, parent_id, perm_name, perm_type, route_path, sort_order) VALUES (10, 6, '活动管理', 2, '/busi/activity', 4);

-- 为超级管理员角色分配所有权限
INSERT INTO sys_role_permission (role_id, permission_id) VALUES (1, 1);
INSERT INTO sys_role_permission (role_id, permission_id) VALUES (1, 2);
INSERT INTO sys_role_permission (role_id, permission_id) VALUES (1, 3);
INSERT INTO sys_role_permission (role_id, permission_id) VALUES (1, 4);
INSERT INTO sys_role_permission (role_id, permission_id) VALUES (1, 5);
INSERT INTO sys_role_permission (role_id, permission_id) VALUES (1, 6);
INSERT INTO sys_role_permission (role_id, permission_id) VALUES (1, 7);
INSERT INTO sys_role_permission (role_id, permission_id) VALUES (1, 8);
INSERT INTO sys_role_permission (role_id, permission_id) VALUES (1, 9);
INSERT INTO sys_role_permission (role_id, permission_id) VALUES (1, 10);

-- 为园长角色分配所有权限
INSERT INTO sys_role_permission (role_id, permission_id) VALUES (2, 1);
INSERT INTO sys_role_permission (role_id, permission_id) VALUES (2, 2);
INSERT INTO sys_role_permission (role_id, permission_id) VALUES (2, 3);
INSERT INTO sys_role_permission (role_id, permission_id) VALUES (2, 4);
INSERT INTO sys_role_permission (role_id, permission_id) VALUES (2, 5);
INSERT INTO sys_role_permission (role_id, permission_id) VALUES (2, 6);
INSERT INTO sys_role_permission (role_id, permission_id) VALUES (2, 7);
INSERT INTO sys_role_permission (role_id, permission_id) VALUES (2, 8);
INSERT INTO sys_role_permission (role_id, permission_id) VALUES (2, 9);
INSERT INTO sys_role_permission (role_id, permission_id) VALUES (2, 10);

-- 为班主任角色分配部分权限
INSERT INTO sys_role_permission (role_id, permission_id) VALUES (3, 4);
INSERT INTO sys_role_permission (role_id, permission_id) VALUES (3, 5);
INSERT INTO sys_role_permission (role_id, permission_id) VALUES (3, 6);
INSERT INTO sys_role_permission (role_id, permission_id) VALUES (3, 7);
INSERT INTO sys_role_permission (role_id, permission_id) VALUES (3, 8);
INSERT INTO sys_role_permission (role_id, permission_id) VALUES (3, 9);
INSERT INTO sys_role_permission (role_id, permission_id) VALUES (3, 10);

-- 关联用户和角色 (用户ID=1 是张老师，ID=2 是管理员)
INSERT INTO sys_user_role (user_id, role_id) VALUES (1, 3);
INSERT INTO sys_user_role (user_id, role_id) VALUES (2, 1);