# 权限分配功能开发检查清单

## 后端代码检查

- [x] SysPermission.java 实体类创建正确，包含 id、parentId、permName、permType 等字段
- [x] SysRolePermission.java 实体类创建正确，包含 id、roleId、permissionId 字段
- [x] RoleAssignDTO.java 创建正确，包含 roleId 和 permissionIds 字段
- [x] PermissionTreeVO.java 创建正确，包含 id、parentId、name、children 字段
- [x] SysPermissionMapper.java 创建正确，继承 BaseMapper<SysPermission>
- [x] SysRolePermissionMapper.java 创建正确，继承 BaseMapper<SysRolePermission>
- [x] SysPermissionService.java 接口定义正确
- [x] SysPermissionServiceImpl.java 实现 getPermissionTree() 方法
- [x] SysRolePermissionService.java 接口定义正确
- [x] SysRolePermissionServiceImpl.java 实现 assignPermissions() 方法（先删后插）
- [x] SysRoleController.java 添加了 3 个新接口

## 前端代码检查

- [x] Role.vue 添加了"分配权限"按钮
- [x] Role.vue 添加了 el-dialog 权限分配弹窗
- [x] Role.vue 添加了 el-tree 树形控件
- [x] handleAssign() 方法正确加载权限树和已有权限
- [x] submitAssign() 方法正确获取选中节点并提交

## 文档更新检查

- [x] docs/后端API.md 添加了权限分配接口文档
- [x] docs/数据字典.md 添加了 sys_permission 表说明（已存在）
- [x] docs/数据字典.md 添加了 sys_role_permission 表说明（已存在）
- [x] docs/数据字典.md 添加了权限类型字典说明

## 功能测试检查

- [ ] 后端启动成功，无编译错误
- [ ] GET /api/system/role/permission/tree 接口返回正确的树形结构
- [ ] GET /api/system/role/permission/own/{roleId} 接口返回权限 ID 列表
- [ ] POST /api/system/role/permission/assign 接口成功保存权限分配
- [ ] 前端页面正常显示权限树
- [ ] 前端能够正确回显已有权限
- [ ] 前端提交权限分配后数据库正确更新
