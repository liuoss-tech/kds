-- =============================================
-- 通知管理模块权限脚本
-- 执行时间: 2026-03-20
-- =============================================

-- 1. 添加通知管理目录和通知列表权限
INSERT INTO sys_permission (id, parent_id, perm_name, perm_type, route_path, sort_order) VALUES
(19, 6, '通知管理', 1, '', 7),
(21, 19, '通知列表', 2, '/busi/notice/manage/list', 1);

-- 2. 添加数据看板目录和通知查看权限
INSERT INTO sys_permission (id, parent_id, perm_name, perm_type, route_path, sort_order) VALUES
(20, 0, '数据看板', 1, '', 8),
(22, 20, '通知查看', 2, '/busi/notice/view/list', 1);

-- 3. 为超级管理员(role_id=2)分配通知管理权限
INSERT INTO sys_role_permission (role_id, permission_id) VALUES (2, 19), (2, 20), (2, 21), (2, 22);

-- 4. 为园长(role_id=3)分配通知管理权限
INSERT INTO sys_role_permission (role_id, permission_id) VALUES (3, 19), (3, 20), (3, 21), (3, 22);

-- 5. 为班主任(role_id=1)分配通知管理权限
INSERT INTO sys_role_permission (role_id, permission_id) VALUES (1, 19), (1, 21);

-- 6. 为配班老师(role_id=4)分配通知管理权限
INSERT INTO sys_role_permission (role_id, permission_id) VALUES (4, 19), (4, 21);

-- 7. 为家长(role_id=5)分配通知查看权限
INSERT INTO sys_role_permission (role_id, permission_id) VALUES (5, 20), (5, 22);
