-- 修改通知列表的父级为业务管理(6)，直接从业务管理下显示
UPDATE sys_permission SET parent_id = 6 WHERE id = 21;

-- 可选：删除通知管理目录（如果不需要可以保留）
DELETE FROM sys_permission WHERE id = 19;

-- 重新分配角色权限（移除通知管理目录，只保留通知列表）
DELETE FROM sys_role_permission WHERE permission_id = 19;

-- 先删除旧记录
DELETE FROM sys_role_permission WHERE permission_id = 21;
-- 重新分配通知列表权限给各角色
INSERT INTO sys_role_permission (role_id, permission_id) VALUES
(2, 21),  -- 超级管理员
(3, 21),  -- 园长
(1, 21),  -- 班主任
(4, 21);  -- 配班老师
