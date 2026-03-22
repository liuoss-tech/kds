-- =============================================
-- 活动管理模块权限脚本
-- 执行时间: 2026-03-19
-- =============================================

-- 1. 添加活动管理目录和活动列表权限
INSERT INTO sys_permission (id, parent_id, perm_name, perm_type, route_path, sort_order) VALUES
(16, 6, '活动管理', 1, '', 6),
(17, 16, '活动列表', 2, '/busi/activity/teacher/list', 1);

-- 2. 添加活动通知权限（家长端）
INSERT INTO sys_permission (id, parent_id, perm_name, perm_type, route_path, sort_order) VALUES
(18, 16, '活动通知', 2, '/busi/activity/parent/list', 2);

-- 3. 为超级管理员(role_id=2)分配活动管理权限
INSERT INTO sys_role_permission (role_id, permission_id) VALUES (2, 16), (2, 17);

-- 4. 为园长(role_id=3)分配活动管理权限
INSERT INTO sys_role_permission (role_id, permission_id) VALUES (3, 16), (3, 17);

-- 5. 为班主任(role_id=1)分配活动管理权限
INSERT INTO sys_role_permission (role_id, permission_id) VALUES (1, 16), (1, 17);

-- 6. 为家长(role_id=5)分配活动管理权限
INSERT INTO sys_role_permission (role_id, permission_id) VALUES (5, 16), (5, 17), (5, 18);
