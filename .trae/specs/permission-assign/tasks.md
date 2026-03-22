# 权限分配功能开发任务清单

## 任务 1: 后端 - 创建权限相关实体类

- [x] 1.1 创建 SysPermission.java（权限实体）
- [x] 1.2 创建 SysRolePermission.java（角色权限关联实体）

## 任务 2: 后端 - 创建 DTO 和 VO

- [x] 2.1 创建 RoleAssignDTO.java（权限分配请求 DTO）
- [x] 2.2 创建 PermissionTreeVO.java（权限树 VO）

## 任务 3: 后端 - 创建 Mapper 接口

- [x] 3.1 创建 SysPermissionMapper.java
- [x] 3.2 创建 SysRolePermissionMapper.java

## 任务 4: 后端 - 创建权限服务

- [x] 4.1 创建 SysPermissionService.java 接口
- [x] 4.2 创建 SysPermissionServiceImpl.java 实现类
  - [x] 实现 getPermissionTree() 方法 - 构建树形结构
  - [x] 实现递归方法 getChildren() 查找子节点

## 任务 5: 后端 - 创建角色权限服务

- [x] 5.1 创建 SysRolePermissionService.java 接口
- [x] 5.2 创建 SysRolePermissionServiceImpl.java 实现类
  - [x] 实现 assignPermissions() 方法 - 先删后插
  - [x] 实现 getPermissionIdsByRoleId() 方法 - 获取角色已有权限

## 任务 6: 后端 - 在 SysRoleController 中添加接口

- [x] 6.1 添加 GET /permission/tree - 获取权限树
- [x] 6.2 添加 GET /permission/own/{roleId} - 获取角色已有权限
- [x] 6.3 添加 POST /permission/assign - 保存权限分配

## 任务 7: 前端 - 修改 Role.vue 添加权限分配功能

- [x] 7.1 添加"分配权限"按钮
- [x] 7.2 添加权限树弹窗 (el-dialog + el-tree)
- [x] 7.3 添加相关 data 变量
- [x] 7.4 实现 handleAssign() 方法 - 打开弹窗并加载数据
- [x] 7.5 实现 submitAssign() 方法 - 提交权限分配

## 任务 8: 文档更新

- [x] 8.1 更新 docs/后端API.md - 添加权限分配接口文档
- [x] 8.2 更新 docs/数据字典.md - 添加权限类型字典说明

## 任务依赖关系
- [任务 1] 是 [任务 4] 的前置
- [任务 2] 是 [任务 6] 的前置
- [任务 3] 是 [任务 4、5] 的前置
- [任务 4、5] 是 [任务 6] 的前置
- [任务 6] 是 [任务 7] 的前置
- [任务 7] 无前端依赖
