# 角色管理功能开发规范

## 背景
为了实现真正的RBAC权限管理，需要让前端动态获取数据库中的角色列表，而不是硬编码角色选项。同时需要提供完整的角色管理界面（增删改查），让超级管理员可以动态管理角色。

## 改造内容

### 第一部分：后端角色管理接口
1. 创建角色查询DTO - RoleQueryDTO.java
2. 创建角色表单DTO - RoleFormDTO.java  
3. 创建SysRoleService服务接口
4. 创建SysRoleServiceImpl服务实现类
5. 创建SysRoleController控制器
   - GET /api/system/role/list - 获取所有角色列表（下拉框用）
   - GET /api/system/role/page - 分页查询角色列表
   - POST /api/system/role/add - 新增角色
   - POST /api/system/role/update - 修改角色
   - POST /api/system/role/delete/{id} - 删除角色

### 第二部分：前端角色管理界面
1. 创建 Role.vue 角色管理页面
2. 修改 User.vue 动态获取角色下拉框数据
3. 在路由中添加角色管理页面
4. 在左侧菜单中添加角色管理入口

## 影响范围
- **后端受影响文件**：
  - `demo/src/main/java/com/kds/model/dto/RoleQueryDTO.java` - 新建
  - `demo/src/main/java/com/kds/model/dto/RoleFormDTO.java` - 新建
  - `demo/src/main/java/com/kds/service/SysRoleService.java` - 新建
  - `demo/src/main/java/com/kds/service/impl/SysRoleServiceImpl.java` - 新建
  - `demo/src/main/java/com/kds/controller/system/SysRoleController.java` - 新建
  - `demo/src/main/java/com/kds/mapper/SysRoleMapper.java` - 已有

- **前端受影响文件**：
  - `frontend/src/views/system/Role.vue` - 新建
  - `frontend/src/views/system/User.vue` - 修改
  - `frontend/src/router/index.js` - 修改

## 新增需求

### 需求：角色下拉框动态获取
系统应从后端动态获取角色列表，而非硬编码

#### 场景：获取角色列表
- **当** 前端页面加载时
- **应自动请求** /api/system/role/list 接口
- **应将** 返回的角色数据填充到下拉框中

### 需求：角色管理页面
系统应提供完整的角色管理界面，支持增删改查

#### 场景：查询角色
- **当** 用户输入角色名称或编码并点击查询
- **应看到** 符合条件的角色列表，支持分页

#### 场景：新增角色
- **当** 点击新增角色按钮
- **应看到** 弹窗表单，包含角色名称、编码、描述
- **当** 填写完整并提交
- **应创建** 新角色

#### 场景：编辑角色
- **当** 点击编辑按钮
- **应看到** 弹窗表单，预填当前角色信息
- **当** 修改并提交
- **应更新** 角色信息

#### 场景：删除角色
- **当** 点击删除按钮
- **应弹出** 二次确认框
- **当** 确认删除
- **应删除** 该角色（如有用户关联则禁止删除）

## 技术栈
- 前端：Vue 3 + Element Plus
- 后端：Spring Boot + MyBatis Plus
- 数据库：MySQL
