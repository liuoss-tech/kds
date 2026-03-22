# 角色管理功能开发检查清单

## 后端检查项
- [x] RoleQueryDTO.java 文件正确创建，包含 page、size、roleName、roleCode 字段
- [x] RoleFormDTO.java 文件正确创建，包含 id、roleName、roleCode、description 字段，包含校验注解
- [x] SysRoleService.java 接口正确创建，包含所有方法声明
- [x] SysRoleServiceImpl.java 实现类正确创建，包含业务逻辑
- [x] SysRoleController.java 控制器正确创建，包含所有REST接口
- [x] 后端代码编译通过，无语法错误

## 前端检查项
- [x] Role.vue 页面文件正确创建
- [x] Role.vue 包含搜索表单、数据表格、分页组件
- [x] Role.vue 包含新增/编辑弹窗功能
- [x] Role.vue 包含删除功能（含用户关联的提示）
- [x] User.vue 已修改，添加动态角色下拉框
- [x] 路由配置已添加角色管理页面
- [x] 前端页面可正常访问（npm run build 成功）

## 接口测试检查项
- [ ] GET /api/system/role/list 接口返回角色列表
- [ ] GET /api/system/role/page 接口支持分页查询
- [ ] POST /api/system/role/add 接口可正常新增角色
- [ ] POST /api/system/role/update 接口可正常修改角色
- [ ] POST /api/system/role/delete/{id} 接口可正常删除角色
- [ ] 删除有关联用户的角色时，返回正确的错误提示

## 集成测试检查项
- [ ] 角色管理页面可以正常显示角色列表
- [ ] 新增角色后，角色列表自动刷新
- [ ] 编辑角色后，角色列表自动刷新
- [ ] 删除角色后，角色列表自动刷新
- [ ] 用户管理页面的角色下拉框显示正确的角色列表
