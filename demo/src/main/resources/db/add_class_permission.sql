-- =============================================
-- 增量SQL：添加班级管理权限
-- 执行时间：2026-03-15
-- 说明：将班级管理模块添加到权限表中
-- =============================================

-- 添加业务管理父级菜单
INSERT INTO sys_permission (id, parent_id, perm_name, perm_type, route_path, sort_order) 
VALUES (6, 0, '业务管理', 1, '', 3);

-- 添加班级管理子菜单
INSERT INTO sys_permission (id, parent_id, perm_name, perm_type, route_path, sort_order) 
VALUES (7, 6, '班级管理', 2, '/busi/class', 1);

-- 为超级管理员角色分配班级管理权限
INSERT INTO sys_role_permission (role_id, permission_id) VALUES (1, 6);
INSERT INTO sys_role_permission (role_id, permission_id) VALUES (1, 7);

-- 为园长角色分配班级管理权限
INSERT INTO sys_role_permission (role_id, permission_id) VALUES (2, 6);
INSERT INTO sys_role_permission (role_id, permission_id) VALUES (2, 7);

-- 为班主任角色分配班级管理权限
INSERT INTO sys_role_permission (role_id, permission_id) VALUES (3, 6);
INSERT INTO sys_role_permission (role_id, permission_id) VALUES (3, 7);
