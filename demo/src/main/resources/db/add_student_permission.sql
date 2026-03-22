-- =============================================
-- 增量SQL：添加幼儿管理权限
-- 执行时间：2026-03-18
-- 说明：将幼儿管理模块添加到权限表中
-- =============================================

-- 添加幼儿管理子菜单（parent_id=6 为业务管理）
INSERT INTO sys_permission (id, parent_id, perm_name, perm_type, route_path, sort_order) 
VALUES (8, 6, '幼儿管理', 2, '/busi/student', 2);

-- 为超级管理员角色分配幼儿管理权限
INSERT INTO sys_role_permission (role_id, permission_id) VALUES (1, 8);

-- 为园长角色分配幼儿管理权限
INSERT INTO sys_role_permission (role_id, permission_id) VALUES (2, 8);

-- 为班主任角色分配幼儿管理权限
INSERT INTO sys_role_permission (role_id, permission_id) VALUES (3, 8);
