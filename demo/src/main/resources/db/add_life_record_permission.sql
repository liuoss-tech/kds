-- =============================================
-- 增量SQL：添加生活记录模块权限
-- 执行时间：2026-03-19
-- 说明：将生活记录模块作为业务管理的子菜单
-- parent_id=6 为业务管理
-- =============================================

-- 1. 添加生活记录菜单权限（parent_id=6 为业务管理）
INSERT INTO sys_permission (id, parent_id, perm_name, perm_type, route_path, sort_order, api_url)
SELECT COALESCE(MAX(id), 0) + 1, 6, '生活记录', 2, '/busi/life-record', 7, '/api/busi/life-record/*'
FROM sys_permission;

-- 获取刚插入的权限ID
SELECT @life_record_perm_id := id FROM sys_permission WHERE route_path = '/busi/life-record';

-- 2. 为超级管理员角色分配生活记录权限
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT id, @life_record_perm_id FROM sys_role WHERE role_code = 'SUPER_ADMIN';

-- 3. 为园长角色分配生活记录权限
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT id, @life_record_perm_id FROM sys_role WHERE role_code = 'PRINCIPAL';

-- 4. 为班主任角色分配生活记录权限
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT id, @life_record_perm_id FROM sys_role WHERE role_code = 'HEAD_TEACHER';

-- 5. 为教师角色分配生活记录权限
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT id, @life_record_perm_id FROM sys_role WHERE role_code = 'TEACHER';

-- 验证结果
SELECT '权限配置完成' AS status;
SELECT id, parent_id, perm_name, perm_type, route_path FROM sys_permission WHERE perm_name = '生活记录';
