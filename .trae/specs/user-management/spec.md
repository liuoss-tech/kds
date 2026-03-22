# 用户管理界面开发规范

## 背景
账号管理是整个系统运转的基石，超级管理员需要通过此功能给园长、班主任、家长开通账号并分配角色。

## 改造内容

### 前端用户管理页面
- 新建 `src/views/system/User.vue` 页面
- 包含顶部搜索栏、操作按钮区（新增/批量导入）、数据表格、底部分页
- 包含新增/编辑弹窗表单

### 后端接口开发
- 创建 UserQueryDTO.java（分页查询参数）
- 创建 UserFormDTO.java（新增/编辑表单）
- 创建 UserVO.java（列表视图对象）
- 创建 UserController.java（API控制器）
- 在 SysUserServiceImpl.java 中实现业务逻辑

### 路由配置
- 在 `src/router/index.js` 中添加账号管理路由

## 影响范围
- 受影响文件：
  - `frontend/src/views/system/User.vue` - 用户管理页面（新建）
  - `frontend/src/router/index.js` - 路由配置
  - `demo/src/main/java/com/kds/model/dto/UserQueryDTO.java` - DTO（新建）
  - `demo/src/main/java/com/kds/model/dto/UserFormDTO.java` - DTO（新建）
  - `demo/src/main/java/com/kds/model/vo/UserVO.java` - VO（新建）
  - `demo/src/main/java/com/kds/controller/system/UserController.java` - 控制器（新建）
  - `demo/src/main/java/com/kds/service/SysUserService.java` - 服务接口（修改）
  - `demo/src/main/java/com/kds/service/impl/SysUserServiceImpl.java` - 服务实现（修改）

## 新增需求

### 需求：用户管理页面
系统应提供完整的用户管理界面，支持增删改查和账号状态控制

#### 场景：查询用户
- **当** 用户输入姓名或手机号并点击查询
- **应看到** 符合条件的用户列表，支持分页

#### 场景：新增用户
- **当** 点击新增用户按钮
- **应看到** 弹窗表单，包含姓名、手机号、密码、角色选择
- **当** 填写完整并提交
- **应创建** 新用户并分配角色

#### 场景：编辑用户
- **当** 点击编辑按钮
- **应看到** 弹窗表单，预填当前用户信息
- **当** 修改并提交
- **应更新** 用户信息

#### 场景：删除用户
- **当** 点击删除按钮
- **应弹出** 二次确认框
- **当** 确认删除
- **应逻辑删除** 该用户

#### 场景：启用/禁用账号
- **当** 点击状态开关
- **应更新** 用户账号状态

## 技术栈
- 前端：Vue 2 + Element UI
- 后端：Spring Boot + MyBatis Plus
- 数据库：MySQL
