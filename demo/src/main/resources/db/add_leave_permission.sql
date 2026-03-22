-- =============================================
-- 增量SQL：添加请假管理权限
-- 执行时间：2026-03-18
-- 说明：将请假管理模块作为业务管理的子菜单
-- parent_id=6 为业务管理
-- =============================================

-- 添加家长请假菜单（直接挂在业务管理下）
INSERT INTO sys_permission (id, parent_id, perm_name, perm_type, route_path, sort_order) 
VALUES (10, 6, '家长请假', 2, '/busi/leave/parent', 4);

-- 添加班主任审批菜单
INSERT INTO sys_permission (id, parent_id, perm_name, perm_type, route_path, sort_order) 
VALUES (11, 6, '请假审批', 2, '/busi/leave/teacher', 5);

-- 添加管理员请假菜单
INSERT INTO sys_permission (id, parent_id, perm_name, perm_type, route_path, sort_order) 
VALUES (12, 6, '全园请假', 2, '/busi/leave/admin', 6);

-- 为超级管理员角色分配请假管理权限
INSERT INTO sys_role_permission (role_id, permission_id) VALUES (1, 10);
INSERT INTO sys_role_permission (role_id, permission_id) VALUES (1, 11);
INSERT INTO sys_role_permission (role_id, permission_id) VALUES (1, 12);

-- 为园长角色分配请假管理权限
INSERT INTO sys_role_permission (role_id, permission_id) VALUES (2, 10);
INSERT INTO sys_role_permission (role_id, permission_id) VALUES (2, 11);
INSERT INTO sys_role_permission (role_id, permission_id) VALUES (2, 12);

-- 为班主任角色分配请假管理权限（只能审批）
INSERT INTO sys_role_permission (role_id, permission_id) VALUES (3, 11);

-- 为家长角色分配请假管理权限（只能提交）
INSERT INTO sys_role_permission (role_id, permission_id) VALUES (4, 10);

-- 为教师角色分配请假管理权限（只能审批）
INSERT INTO sys_role_permission (role_id, permission_id) VALUES (5, 11);
