-- =============================================
-- 增量SQL：添加家长端生活记录查看权限
-- 执行时间：2026-03-22
-- 说明：在数据看板下添加生活记录查看菜单
-- parent_id=20 为数据看板
-- =============================================

-- 1. 添加生活记录查看菜单权限（parent_id=20 为数据看板）
INSERT INTO sys_permission (id, parent_id, perm_name, perm_type, route_path, sort_order, api_url)
SELECT COALESCE(MAX(id), 0) + 1, 20, '生活记录查看', 2, '/busi/life-record/parent', 8, '/api/busi/life-record/parent/*'
FROM sys_permission;

-- 获取刚插入的权限ID
SELECT @life_record_view_perm_id := id FROM sys_permission WHERE route_path = '/busi/life-record/parent';

-- 2. 为家长角色分配生活记录查看权限
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT id, @life_record_view_perm_id FROM sys_role WHERE role_code = 'PARENT';

-- 验证结果
SELECT '权限配置完成' AS status;
SELECT id, parent_id, perm_name, perm_type, route_path FROM sys_permission WHERE perm_name = '生活记录查看';
