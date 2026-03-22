# 幼儿管理模块 Spec

## Why
幼儿园需要一个专门的模块来管理幼儿档案信息，包括幼儿基本信息、班级分配、家长绑定等功能，是整个家校系统的核心数据之一。

## What Changes

### 1. 数据库变更
- 在 `busi_student` 表添加 `avatar` 字段（头像URL），用于存储幼儿头像

### 2. 后端 API 设计
| API | 方法 | 说明 |
|:---|:---:|:---|
| `/api/busi/student/page` | GET | 分页查询幼儿列表（默认只查在读状态） |
| `/api/busi/student/{id}` | GET | 获取幼儿详情（含家长信息） |
| `/api/busi/student/add` | POST | 新增幼儿 |
| `/api/busi/student/update` | POST | 修改幼儿 |
| `/api/busi/student/delete/{id}` | POST | 删除幼儿 |
| `/api/busi/student/parent-list` | GET | 获取家长用户列表（用于选择） |
| `/api/busi/student/class-list` | GET | 获取班级列表（用于下拉选择） |

### 3. 前端页面设计
- 新增 `/busi/student` 路由，对应 `Student.vue` 页面
- 风格与现有 Class.vue 一致（Element Plus + 粉色可爱风格）
- 表格形式展示，支持搜索、分页
- 新增/编辑弹窗，支持多家长选择

### 4. 权限与数据隔离
- **权限要求**：需要新增 `student:list`、`student:add`、`student:edit`、`student:delete` 权限标识
- **数据隔离**：
  - 班主任/配班老师：只能操作本班幼儿
  - 超级管理员/园长：可操作全园幼儿
  - 数据权限注解 `@DataScope` 控制

## Impact

- Affected specs: 需更新数据字典文档、权限分配模块文档
- Affected code:
  - 后端：新增 Entity、DTO、VO、Mapper、Service、Controller
  - 前端：新增 Student.vue、路由配置
  - 数据库：ALTER TABLE 添加 avatar 字段

## ADDED Requirements

### Requirement: 幼儿列表查询
系统 SHALL 提供幼儿分页列表查询功能，默认只显示在园状态为"在读"的幼儿。

#### Scenario: 查询幼儿列表
- **WHEN** 用户访问幼儿管理页面
- **THEN** 系统默认加载第一页，显示10条记录，只包含状态为"在读"的幼儿
- **AND** 显示幼儿姓名、性别、出生日期、所属班级、家长信息

#### Scenario: 搜索幼儿
- **WHEN** 用户输入姓名并点击查询
- **THEN** 系统返回匹配的幼儿记录

### Requirement: 新增幼儿
系统 SHALL 提供新增幼儿功能，必填字段：姓名、性别、出生日期、所属班级、至少一个家长。

#### Scenario: 新增幼儿成功
- **WHEN** 用户填写必填信息并提交
- **THEN** 系统创建幼儿档案，同时创建幼儿-家长绑定关系
- **AND** 返回成功提示

#### Scenario: 家长多选
- **WHEN** 用户在新增/编辑弹窗中选择多个家长
- **THEN** 系统为每个家长创建一条 `busi_student_parent` 记录
- **AND** 用户可设置每个家长的亲属关系

### Requirement: 家长选择
系统 SHALL 提供从系统用户中选择家长的功能，只显示 userType=2（家长）的用户。

#### Scenario: 加载家长列表
- **WHEN** 打开新增/编辑弹窗
- **THEN** 系统加载所有"家长"角色的用户列表
- **AND** 显示用户名/姓名供选择

### Requirement: 头像上传
系统 SHALL 支持幼儿头像上传功能。

#### Scenario: 上传头像
- **WHEN** 用户在新增/编辑时选择头像图片
- **THEN** 系统将图片上传到服务器，返回URL并保存到数据库

## MODIFIED Requirements

### Requirement: 数据隔离
原有数据隔离规则适用于幼儿管理模块：
- 班主任/配班老师：只能查看/操作本班幼儿
- 超级管理员/园长：可查看/操作全园幼儿

## REMOVED Requirements

无

---

## 技术实现要点

### 1. 复用原则
- 复用现有的 `Result`、`Page` 等通用结构
- 复用现有的 `DataScope` 数据权限机制
- 复用现有的文件上传机制（如有）

### 2. 版本兼容
- 后端：Spring Boot 2.x + MyBatis Plus 3.x
- 前端：Vue 3 + Element Plus
- 确保与现有系统兼容

### 3. 接口设计风格
- 与现有 BusiClassController 保持一致
- 使用 `@Validated` 进行参数校验
- 返回 `Result<?>` 统一格式
