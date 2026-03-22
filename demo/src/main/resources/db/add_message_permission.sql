-- 私信模块数据库准备脚本
-- 执行日期：2026-03-19

USE my_database;

-- 1. 将"首页看板"目录改名为"消息中心"
UPDATE sys_permission SET perm_name = '消息中心' WHERE perm_name = '首页看板';

-- 2. 在 sys_permission 表中添加"私信"菜单权限记录
-- 检查是否已存在私信菜单
SELECT @exists := COUNT(*) FROM sys_permission WHERE route_path = '/message';

-- 如果不存在，则插入
INSERT INTO sys_permission (parent_id, perm_name, perm_type, route_path, api_url, sort_order)
SELECT 4, '私信', 2, '/message', '/api/busi/message/*', 2
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE route_path = '/message');

-- 3. 获取私信权限ID（假设插入后ID为9，实际需要查询）
SELECT @message_perm_id := id FROM sys_permission WHERE route_path = '/message';

-- 4. 为所有角色分配私信菜单权限
-- 先删除可能存在的重复记录
DELETE FROM sys_role_permission WHERE permission_id = @message_perm_id;

-- 插入新的权限分配（所有角色）
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT id, @message_perm_id FROM sys_role;

-- 5. 在 sys_message 表增加 idx_sender_receiver 索引（如果不存在）
SET @index_exists = (
    SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS
    WHERE TABLE_SCHEMA = 'my_database'
    AND TABLE_NAME = 'sys_message'
    AND INDEX_NAME = 'idx_sender_receiver'
);

SET @sql = IF(@index_exists = 0,
    'CREATE INDEX idx_sender_receiver ON sys_message (sender_id, receiver_id)',
    'SELECT ''Index already exists'' AS result');

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 验证结果
SELECT '权限配置完成' AS status;
SELECT id, parent_id, perm_name, perm_type, route_path FROM sys_permission WHERE perm_name IN ('消息中心', '首页', '私信');
