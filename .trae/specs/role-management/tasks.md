# 角色管理功能开发任务清单

## 任务 1: 后端 - 创建角色查询和表单DTO

- [x] 1.1 创建 RoleQueryDTO.java（分页查询参数）
- [x] 1.2 创建 RoleFormDTO.java（新增/编辑表单）

## 任务 2: 后端 - 创建角色服务层

- [x] 2.1 创建 SysRoleService.java 接口
- [x] 2.2 创建 SysRoleServiceImpl.java 实现类
  - [x] 实现 getRolePage 分页查询
  - [x] 实现 addRole 新增角色（含角色编码唯一性校验）
  - [x] 实现 updateRole 修改角色
  - [x] 实现 deleteRole 删除角色（含用户关联检查）

## 任务 3: 后端 - 创建角色控制器

- [x] 3.1 创建 SysRoleController.java
  - [x] GET /api/system/role/list - 获取所有角色列表
  - [x] GET /api/system/role/page - 分页查询
  - [x] POST /api/system/role/add - 新增角色
  - [x] POST /api/system/role/update - 修改角色
  - [x] POST /api/system/role/delete/{id} - 删除角色

## 任务 4: 前端 - 创建角色管理页面

- [x] 4.1 创建 Role.vue 角色管理页面
  - [x] 实现搜索表单（角色名称、编码查询）
  - [x] 实现操作按钮区（新增）
  - [x] 实现数据表格（序号、角色名称、编码、描述、创建时间、操作）
  - [x] 实现分页组件
  - [x] 实现新增/编辑弹窗表单
  - [x] 实现编辑、删除功能

## 任务 5: 前端 - 修改用户管理页面

- [x] 5.1 修改 User.vue
  - [x] 添加 roleOptions 数组
  - [x] 添加 getRoleList 方法
  - [x] 在 created 钩子中调用 getRoleList
  - [x] 将角色下拉框改为动态 v-for 循环

## 任务 6: 前端 - 配置路由和菜单

- [x] 6.1 在 router/index.js 中添加角色管理路由
- [x] 6.2 在布局菜单中添加角色管理入口
