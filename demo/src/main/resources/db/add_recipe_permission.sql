-- 食谱管理权限配置
-- 权限ID: 9
-- 父级ID: 6 (业务管理)
-- 路由路径: /busi/recipe

INSERT INTO sys_permission (id, parent_id, perm_name, perm_type, route_path, api_url, sort_order, create_time, is_deleted)
VALUES (9, 6, '食谱管理', 2, '/busi/recipe', '/api/busi/recipe', 3, NOW(), 0)
ON DUPLICATE KEY UPDATE perm_name = VALUES(perm_name);