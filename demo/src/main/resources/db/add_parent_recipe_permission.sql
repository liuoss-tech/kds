-- =============================================
-- 食谱管理模块权限脚本
-- 执行时间: 2026-03-22
-- =============================================

-- 1. 添加食谱查看权限
INSERT INTO sys_permission (id, parent_id, perm_name, perm_type, route_path, sort_order) VALUES
(24, 20, '食谱查看', 2, '/busi/recipe/parent', 2);

-- 2. 为家长(role_id=4)分配食谱查看权限
INSERT INTO sys_role_permission (role_id, permission_id) VALUES (4, 24);
