# 权限分配功能开发规范

## 背景
为了实现完整的 RBAC 权限管理系统，需要为角色分配权限功能开发后端接口和前端界面。根据数据库中的 `sys_permission`（菜单与权限表）和 `sys_role_permission`（角色权限关联表），需要提供获取权限树、获取角色已有权限、保存权限分配等功能。

## 改造内容

### 第一部分：后端权限分配接口
1. 创建权限相关实体类
   - SysPermission.java（已有表结构）
   - SysRolePermission.java（已有表结构）
2. 创建数据传输对象
   - RoleAssignDTO.java（权限分配请求）
   - PermissionTreeVO.java（权限树 VO）
3. 创建 Mapper 接口
   - SysPermissionMapper.java
   - SysRolePermissionMapper.java
4. 创建 Service 接口和实现
   - SysPermissionService.java + SysPermissionServiceImpl.java
   - SysRolePermissionService.java + SysRolePermissionServiceImpl.java
5. 在 SysRoleController 中添加 3 个接口
   - GET /api/system/role/permission/tree - 获取完整权限树
   - GET /api/system/role/permission/own/{roleId} - 获取角色已有权限
   - POST /api/system/role/permission/assign - 保存权限分配

### 第二部分：前端权限分配界面
1. 修改 Role.vue 角色管理页面
   - 添加"分配权限"按钮
   - 添加权限树弹窗
   - 实现权限树获取和回显
   - 实现权限提交保存

### 第三部分：文档更新
1. 更新 `docs/后端API.md` - 添加权限分配接口文档
2. 更新 `docs/数据字典.md` - 添加 sys_permission 和 sys_role_permission 表说明

## 影响范围
- **后端受影响文件**：
  - `demo/src/main/java/com/kds/model/entity/SysPermission.java` - 新建
  - `demo/src/main/java/com/kds/model/entity/SysRolePermission.java` - 新建
  - `demo/src/main/java/com/kds/model/dto/RoleAssignDTO.java` - 新建
  - `demo/src/main/java/com/kds/model/vo/PermissionTreeVO.java` - 新建
  - `demo/src/main/java/com/kds/mapper/SysPermissionMapper.java` - 新建
  - `demo/src/main/java/com/kds/mapper/SysRolePermissionMapper.java` - 新建
  - `demo/src/main/java/com/kds/service/SysPermissionService.java` - 新建
  - `demo/src/main/java/com/kds/service/impl/SysPermissionServiceImpl.java` - 新建
  - `demo/src/main/java/com/kds/service/SysRolePermissionService.java` - 新建
  - `demo/src/main/java/com/kds/service/impl/SysRolePermissionServiceImpl.java` - 新建
  - `demo/src/main/java/com/kds/controller/system/SysRoleController.java` - 修改

- **前端受影响文件**：
  - `frontend/src/views/system/Role.vue` - 修改

## 新增需求

### 需求：获取权限树
系统应返回树形结构的权限列表，供前端展示

#### 场景：获取完整权限树
- **当** 前端请求获取权限树接口
- **应返回** 树形结构数据，包含 id、parentId、name、children

### 需求：获取角色已有权限
系统应返回指定角色已分配的权限 ID 列表

#### 场景：获取角色已有权限
- **当** 前端请求获取某个角色的已有权限
- **应返回** 该角色关联的所有权限 ID 列表

### 需求：保存权限分配
系统应保存角色与权限的关联关系

#### 场景：保存权限分配
- **当** 前端提交权限分配请求（包含角色 ID 和权限 ID 列表）
- **应先删除** 该角色的所有原有权限
- **应再插入** 新勾选的权限
- **应返回** 保存成功提示

### 需求：前端权限分配界面
前端应提供可视化的权限树和勾选功能

#### 场景：打开权限分配弹窗
- **当** 用户点击角色的"分配权限"按钮
- **应弹出** 权限树弹窗
- **应加载** 完整权限树
- **应回显** 该角色已勾选的权限

#### 场景：提交权限分配
- **当** 用户勾选权限并点击确定
- **应获取** 全选和半选节点
- **应提交** 到后端保存

## 技术栈
- 前端：Vue 3 + Element Plus (el-tree 组件)
- 后端：Spring Boot + MyBatis Plus
- 数据库：MySQL

## 数据库表结构（已有）
- sys_permission：菜单与权限表
- sys_role_permission：角色权限关联表
