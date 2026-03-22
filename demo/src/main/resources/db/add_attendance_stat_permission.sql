-- =============================================
-- 增量SQL：添加考勤情况统计权限
-- 执行时间：2026-03-21
-- 说明：将考勤情况统计作为业务管理的子菜单
-- parent_id=6 为业务管理
-- =============================================

-- 添加考勤情况统计菜单
INSERT INTO sys_permission (id, parent_id, perm_name, perm_type, route_path, sort_order) 
VALUES (14, 6, '考勤情况统计', 2, '/busi/attendance-stat', 7);

-- 为超级管理员角色分配考勤统计权限
INSERT INTO sys_role_permission (role_id, permission_id) VALUES (1, 14);

-- 为园长角色分配考勤统计权限
INSERT INTO sys_role_permission (role_id, permission_id) VALUES (2, 14);

-- 为班主任角色分配考勤统计权限
INSERT INTO sys_role_permission (role_id, permission_id) VALUES (3, 14);

-- 为配班老师角色分配考勤统计权限
INSERT INTO sys_role_permission (role_id, permission_id) VALUES (5, 14);