# 通知管理模块规范

## 1. 概述

通知模块是幼儿园管理系统中连接园方与家庭的重要沟通渠道。模块分为两个子模块：
- **通知管理**：供教师/管理员发布通知，并查看确认情况
- **通知查看**：供家长和教师查看通知并进行确认

## 2. 权限设计

### 2.1 发布权限矩阵

| 角色 | 发布范围 | 路由路径 |
|:---|:---|:---|
| 超级管理员 | 全园 + 所有班级 | /busi/notice/manage/* |
| 园长 | 全园 + 所有班级 | /busi/notice/manage/* |
| 班主任 | 本班 | /busi/notice/manage/* |
| 教师 | 本班 | /busi/notice/manage/* |

### 2.2 确认权限

| 角色 | 是否需要确认 | 说明 |
|:---|:---|:---|
| 家长 | 是 | 确认本人子女的通知 |
| 教师 | 是 | 但自动过滤本人发布的通知 |

## 3. 数据模型

### 3.1 总线通知表 (busi_notice)

| 字段 | 类型 | 说明 |
|:---|:---|:---|
| id | bigint | 主键 |
| title | varchar(100) | 通知标题 |
| content | text | 通知正文 |
| notice_type | tinyint | 类型：1-日常 2-缴费 3-放假 |
| notice_level | tinyint | 级别：1-普通 2-置顶 3-强提醒 |
| target_scope | tinyint | 范围：1-全园 2-指定班级 |
| target_class_id | bigint | 目标班级ID |
| publisher_id | bigint | 发布人ID |
| publish_time | datetime | 发布时间 |
| create_time | datetime | 创建时间 |
| update_time | datetime | 更新时间 |
| is_deleted | tinyint | 逻辑删除 |

### 3.2 通知确认表 (busi_notice_receipt)

| 字段 | 类型 | 说明 |
|:---|:---|:---|
| id | bigint | 主键 |
| notice_id | bigint | 通知ID |
| student_id | bigint | 幼儿ID |
| parent_user_id | bigint | 确认的家长用户ID |
| is_confirmed | tinyint | 是否已确认：0-否 1-是 |
| confirm_time | datetime | 确认时间 |

### 3.3 确认逻辑

- 以**幼儿**为维度存储确认记录
- 同一幼儿多个家长，任何一人确认即视为该幼儿已确认
- `parent_user_id` 记录实际点击确认的家长

## 4. API 设计

### 4.1 通知管理接口

| 方法 | 路径 | 说明 |
|:---|:---|:---|
| GET | /api/busi/notice/manage/page | 分页查询（管理端） |
| POST | /api/busi/notice | 发布通知 |
| GET | /api/busi/notice/{id} | 通知详情 |
| GET | /api/busi/notice/{id}/receipts | 确认情况明细 |

### 4.2 通知查看接口

| 方法 | 路径 | 说明 |
|:---|:---|:---|
| GET | /api/busi/notice/view/page | 分页查询（查看端） |
| POST | /api/busi/notice/{id}/confirm | 确认通知 |

### 4.3 权限树更新

新增权限节点：

| 权限ID | 权限名称 | 父级ID | 路由路径 |
|:---|:---|:---|:---|
| 19 | 通知管理 | 6 | /busi/notice/manage/* |
| 20 | 通知查看 | 新节点 | /busi/notice/view/* |

注：需要为所有角色分配通知管理权限（除了家长角色）

## 5. 前端页面

### 5.1 通知管理页面（业务管理下）

```
通知管理
├── 通知列表（发布通知按钮）
├── 发布通知（表单）
└── 通知详情（确认情况）
```

### 5.2 通知查看页面（新权限节点下）

```
数据看板（家长/教师共用）
├── 考勤查看
├── 生活记录查看
└── 通知查看（状态角标 + 筛选）
```

## 6. 技术要求

- 复用现有 Service 基类（IService, ServiceImpl）
- 复用 LoginHelper 获取当前用户信息
- 复用现有 DTO/VO 风格
- 遵循 MyBatis Plus 使用规范
- 前端使用 Vue3 + Composition API
