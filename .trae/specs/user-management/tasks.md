# 用户管理界面开发任务清单

## 任务 1: 创建前端用户管理页面
- [x] 1.1 创建 `src/views/system` 文件夹
- [x] 1.2 创建 `src/views/system/User.vue` 页面
  - [x] 实现搜索表单（姓名、手机号查询）
  - [x] 实现操作按钮区（新增、批量导入）
  - [x] 实现数据表格（序号、姓名、手机号、用户类型、角色、状态、创建时间、操作）
  - [x] 实现分页组件
  - [x] 实现新增/编辑弹窗表单
  - [x] 实现状态开关、编辑、删除功能

## 任务 2: 配置前端路由
- [x] 2.1 在 `src/router/index.js` 中添加账号管理路由

## 任务 3: 创建后端 DTO 类
- [x] 3.1 创建 `UserQueryDTO.java`（分页查询参数）
- [x] 3.2 创建 `UserFormDTO.java`（新增/编辑表单）

## 任务 4: 创建后端 VO 类
- [x] 4.1 创建 `UserVO.java`（列表视图对象）

## 任务 5: 创建后端 Controller
- [x] 5.1 创建 `UserController.java`
  - [x] 分页查询接口 GET /api/system/user/page
  - [x] 新增用户接口 POST /api/system/user/add
  - [x] 修改用户接口 POST /api/system/user/update
  - [x] 修改状态接口 POST /api/system/user/status
  - [x] 删除用户接口 POST /api/system/user/delete/{id}

## 任务 6: 实现 Service 业务逻辑
- [x] 6.1 修改 `SysUserService.java` 添加接口方法
- [x] 6.2 修改 `SysUserServiceImpl.java` 实现业务逻辑
  - [x] 分页查询（含角色名称关联查询）
  - [x] 新增用户（含账号唯一性校验、角色关联）
  - [x] 修改用户
  - [x] 删除用户（逻辑删除）
  - [x] 账号唯一性校验方法

## 任务依赖
- 任务 1.1 → 任务 1.2
- 任务 3、4、5 可并行执行
- 任务 6 依赖任务 3、4、5
- 任务 2 依赖任务 1.2
