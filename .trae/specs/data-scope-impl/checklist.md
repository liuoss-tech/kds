# Checklist - 数据隔离功能实现

## 代码实现检查

- [x] LoginHelper.java 正确获取当前登录用户信息
- [x] LoginHelper.java 正确获取用户角色和 data_scope
- [x] LoginHelper.java 正确查询老师分配的班级IDs
- [x] LoginHelper.java 正确查询家长绑定的孩子IDs
- [x] CustomDataPermissionHandler.java 正确实现数据过滤逻辑
- [x] CustomDataPermissionHandler.java 对 data_scope=1 返回 null（不过滤）
- [x] CustomDataPermissionHandler.java 对 data_scope=2 正确注入 class_id 条件
- [x] CustomDataPermissionHandler.java 对 data_scope=4 正确注入 id 条件
- [x] MybatisPlusConfig.java 正确注册 DataPermissionInterceptor
- [x] 代码与 MyBatis Plus 3.5.5 版本兼容
- [x] 代码与 Spring Boot 2.7.18 版本兼容
- [x] 代码与 Sa-Token 1.37.0 版本兼容

## 功能验证检查

- [ ] 管理员/园长登录后可查看全园数据
- [ ] 老师登录后仅能查看分配班级的数据
- [ ] 家长登录后仅能查看自己孩子的数据
- [ ] 数据过滤条件在 SQL 查询中正确注入

## 代码质量检查

- [x] 代码无语法错误
- [x] 遵循项目现有的代码风格
- [x] 必要的空值判断处理
- [x] 日志记录完善
