# 幼儿园管理系统 API 接口文档

> **文档版本**：1.7.0
> **更新时间**：2026-03-21
> **基础路径**：`http://localhost:8080`

***

## 目录

1. [概述](#1-概述)
2. [通用说明](#2-通用说明)
3. [认证接口](#3-认证接口)
4. [用户管理接口](#4-用户管理接口)
5. [角色管理接口](#5-角色管理接口)
6. [权限管理接口](#6-权限管理接口)
7. [班级管理接口](#7-班级管理接口)
8. [幼儿管理接口](#8-幼儿管理接口)
9. [考勤管理接口](#9-考勤管理接口)
10. [考勤统计接口](#10-考勤统计接口)
11. [请假管理接口](#11-请假管理接口)
12. [教师班级关联接口](#11-教师班级关联接口)
13. [通知管理接口](#12-通知管理接口)
11. [教师班级关联接口](#11-教师班级关联接口)
12. [教师班级关联接口](#12-教师班级关联接口)
13. [通知管理接口](#13-通知管理接口)
14. [错误码说明](#14-错误码说明)
13. [错误码说明](#13-错误码说明)

***

## 1. 概述

本文档描述了幼儿园管理系统的后端 RESTful API 接口规范。系统基于 Spring Boot 框架开发，采用 MyBatis Plus 作为数据访问层，使用 Sa-Token 框架实现身份认证与授权。

### 1.1 技术架构

| 层级 | 技术选型 |
|------|----------|
| 后端框架 | Spring Boot 2.7.18 |
| 持久层 | MyBatis Plus 3.5.5 |
| 认证框架 | Sa-Token 1.37.0 |
| 数据库 | MySQL 8.0+ |
| 缓存 | Redis |

### 1.2 接口统计

| 模块 | 接口数量 |
|------|----------|
| 认证模块 | 3 |
| 用户管理 | 6 |
| 角色管理 | 7 |
| 权限管理 | 1 |
| 班级管理 | 4 |
| 幼儿管理 | 7 |
| 考勤管理 | 3 |
| 考勤统计 | 4 |
| 请假管理 | 6 |
| 教师班级关联 | 2 |
| 通知管理 | 6 |
| **合计** | **49** |

***

## 2. 通用说明

### 2.1 接口约定

| 特性 | 说明 |
|------|------|
| 通信协议 | HTTP/1.1 |
| 数据格式 | JSON |
| 字符编码 | UTF-8 |
| 请求超时 | 30 秒 |

### 2.2 请求头要求

| 请求头 | 必填 | 说明 |
|--------|------|------|
| `Content-Type` | 是 | 固定值为 `application/json` |
| `Authorization` | 视情况 | 登录后必填，格式：`Bearer <token>` 或直接传 token 值 |

### 2.3 通用响应结构

#### 成功响应

```json
{
  "code": 200,
  "message": "操作成功",
  "data": { ... }
}
```

#### 失败响应

```json
{
  "code": 500,
  "message": "错误描述信息",
  "data": null
}
```

### 2.4 数据类型说明

| 类型 | 说明 |
|------|------|
| `Integer` | 整数类型 |
| `Long` | 长整数类型 |
| `String` | 字符串类型 |
| `Boolean` | 布尔类型 |
| `Date` | 日期类型 (yyyy-MM-dd) |
| `DateTime` | 日期时间类型 (yyyy-MM-dd HH:mm:ss) |

***

## 3. 认证接口

### 3.1 用户登录

> **接口说明**：用户使用账号密码进行登录，系统验证通过后返回 Token 和用户信息。

| 项目 | 内容 |
|------|------|
| **接口路径** | `/api/auth/login` |
| **请求方法** | `POST` |
| **认证要求** | 否 |

#### 请求参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| `username` | String | 是 | 登录账号（手机号或用户名） |
| `password` | String | 是 | 登录密码 |

#### 请求示例

```json
POST /api/auth/login
{
  "username": "13800138000",
  "password": "123456"
}
```

#### 响应示例

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "token": "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx",
    "userId": 1,
    "realName": "张老师",
    "userType": 1,
    "roleCode": "HEAD_TEACHER"
  }
}
```

#### 字段说明

| 字段 | 类型 | 说明 |
|------|------|------|
| `token` | String | Sa-Token 颁发的身份令牌 |
| `userId` | Long | 用户唯一标识 |
| `realName` | String | 用户真实姓名 |
| `userType` | Integer | 用户类型：1-教职工 2-家长 |
| `roleCode` | String | 角色编码 |

***

### 3.2 获取当前用户信息

> **接口说明**：获取当前登录用户的详细信息，包括角色名称。

| 项目 | 内容 |
|------|------|
| **接口路径** | `/api/auth/info` |
| **请求方法** | `GET` |
| **认证要求** | 是 |

#### 请求头

| 请求头 | 必填 | 说明 |
|--------|------|------|
| `Authorization` | 是 | 格式：`Bearer <token>` 或直接传 token 值 |

#### 响应示例

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "userId": 1,
    "realName": "张老师",
    "userType": 1,
    "roleCode": "HEAD_TEACHER",
    "roleName": "班主任"
  }
}
```

#### 字段说明

| 字段 | 类型 | 说明 |
|------|------|------|
| `userId` | Long | 用户唯一标识 |
| `realName` | String | 用户真实姓名 |
| `userType` | Integer | 用户类型：1-教职工 2-家长 |
| `roleCode` | String | 角色编码 |
| `roleName` | String | 角色名称 |

***

### 3.3 用户退出

> **接口说明**：用户退出登录，系统清除 Token 并返回成功信息。

| 项目 | 内容 |
|------|------|
| **接口路径** | `/api/auth/logout` |
| **请求方法** | `POST` |
| **认证要求** | 是 |

#### 响应示例

```json
{
  "code": 200,
  "message": "退出成功",
  "data": null
}
```

***

## 4. 用户管理接口

### 4.1 分页查询用户列表

> **接口说明**：分页查询用户列表，支持按姓名或手机号搜索。

| 项目 | 内容 |
|------|------|
| **接口路径** | `/api/system/user/page` |
| **请求方法** | `GET` |
| **认证要求** | 是 |

#### 请求参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| `page` | Integer | 否 | 页码，默认1 |
| `size` | Integer | 否 | 每页条数，默认10 |
| `realName` | String | 否 | 真实姓名（模糊查询） |
| `username` | String | 否 | 手机号/账号（模糊查询） |

#### 响应示例

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "id": 1,
        "realName": "张老师",
        "username": "13800138000",
        "userType": 1,
        "roleId": 3,
        "roleName": "班主任",
        "status": 1,
        "createTime": "2026-03-01 10:00:00"
      }
    ],
    "total": 1,
    "size": 10,
    "current": 1
  }
}
```

#### 字段说明

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | Long | 用户ID |
| `realName` | String | 真实姓名 |
| `username` | String | 手机号/账号 |
| `userType` | Integer | 用户类型：1-教职工 2-家长 |
| `roleId` | Long | 角色ID |
| `roleName` | String | 系统角色名称 |
| `status` | Integer | 账号状态：0-禁用 1-启用 |
| `createTime` | DateTime | 创建时间 |

***

### 4.2 新增用户

> **接口说明**：创建新用户账号。

| 项目 | 内容 |
|------|------|
| **接口路径** | `/api/system/user/add` |
| **请求方法** | `POST` |
| **认证要求** | 是 |

#### 请求参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| `realName` | String | 是 | 真实姓名 |
| `username` | String | 是 | 手机号（作为登录账号） |
| `password` | String | 否 | 登录密码，默认123456 |
| `roleId` | Long | 是 | 角色ID（从角色列表接口获取） |

#### 请求示例

```json
{
  "realName": "李老师",
  "username": "13900139000",
  "password": "123456",
  "roleId": 3
}
```

#### 响应示例

```json
{
  "code": 200,
  "message": "新增成功",
  "data": null
}
```

***

### 4.3 修改用户

> **接口说明**：修改用户信息。

| 项目 | 内容 |
|------|------|
| **接口路径** | `/api/system/user/update` |
| **请求方法** | `POST` |
| **认证要求** | 是 |

#### 请求参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| `id` | Long | 是 | 用户ID |
| `realName` | String | 是 | 真实姓名 |
| `username` | String | 是 | 手机号 |
| `password` | String | 否 | 密码，不传则不修改 |
| `roleId` | Long | 是 | 角色ID |

#### 请求示例

```json
{
  "id": 1,
  "realName": "张老师",
  "username": "13800138000",
  "roleId": 2
}
```

#### 响应示例

```json
{
  "code": 200,
  "message": "修改成功",
  "data": null
}
```

***

### 4.4 修改账号状态

> **接口说明**：启用或禁用用户账号。

| 项目 | 内容 |
|------|------|
| **接口路径** | `/api/system/user/status` |
| **请求方法** | `POST` |
| **认证要求** | 是 |

#### 请求参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| `id` | Long | 是 | 用户ID |
| `status` | Integer | 是 | 账号状态：0-禁用 1-启用 |

#### 请求示例

```json
{
  "id": 1,
  "status": 0
}
```

#### 响应示例

```json
{
  "code": 200,
  "message": "状态更新成功",
  "data": null
}
```

***

### 4.5 删除用户

> **接口说明**：逻辑删除用户账号。

| 项目 | 内容 |
|------|------|
| **接口路径** | `/api/system/user/delete/{id}` |
| **请求方法** | `POST` |
| **认证要求** | 是 |

#### 请求参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| `id` | Long | 是 | 用户ID（路径参数） |

#### 响应示例

```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

***

### 4.6 获取教师列表

> **接口说明**：获取教师列表（userType=1且status=1且未删除），用于前端下拉框选择。

| 项目 | 内容 |
|------|------|
| **接口路径** | `/api/system/user/teacher-list` |
| **请求方法** | `GET` |
| **认证要求** | 是 |

#### 响应示例

```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "realName": "张老师"
    },
    {
      "id": 2,
      "realName": "李老师"
    }
  ]
}
```

#### 字段说明

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | Long | 教师用户ID |
| `realName` | String | 教师真实姓名 |

***

## 5. 角色管理接口

### 5.1 获取所有角色列表

> **接口说明**：获取所有角色列表，用于下拉框选择。

| 项目 | 内容 |
|------|------|
| **接口路径** | `/api/system/role/list` |
| **请求方法** | `GET` |
| **认证要求** | 是 |

#### 响应示例

```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "roleName": "超级管理员",
      "roleCode": "SUPER_ADMIN",
      "description": "系统超级管理员，拥有所有权限",
      "createTime": "2026-01-01 10:00:00"
    },
    {
      "id": 2,
      "roleName": "园长",
      "roleCode": "PRINCIPAL",
      "description": "幼儿园园长，管理全园",
      "createTime": "2026-01-01 10:00:00"
    }
  ]
}
```

#### 字段说明

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | Long | 角色ID |
| `roleName` | String | 角色名称 |
| `roleCode` | String | 角色编码（用于权限控制） |
| `description` | String | 角色描述 |
| `createTime` | DateTime | 创建时间 |

***

### 5.2 分页查询角色列表

> **接口说明**：分页查询角色列表，支持按角色名称或编码搜索。

| 项目 | 内容 |
|------|------|
| **接口路径** | `/api/system/role/page` |
| **请求方法** | `GET` |
| **认证要求** | 是 |

#### 请求参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| `page` | Integer | 否 | 页码，默认1 |
| `size` | Integer | 否 | 每页条数，默认10 |
| `roleName` | String | 否 | 角色名称（模糊查询） |
| `roleCode` | String | 否 | 角色编码（模糊查询） |

#### 响应示例

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "id": 1,
        "roleName": "超级管理员",
        "roleCode": "SUPER_ADMIN",
        "description": "系统超级管理员，拥有所有权限",
        "createTime": "2026-01-01 10:00:00"
      }
    ],
    "total": 1,
    "size": 10,
    "current": 1
  }
}
```

***

### 5.3 新增角色

> **接口说明**：创建新角色。

| 项目 | 内容 |
|------|------|
| **接口路径** | `/api/system/role/add` |
| **请求方法** | `POST` |
| **认证要求** | 是 |

#### 请求参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| `roleName` | String | 是 | 角色名称 |
| `roleCode` | String | 是 | 角色编码（全局唯一） |
| `description` | String | 否 | 角色描述 |

#### 请求示例

```json
{
  "roleName": "配班老师",
  "roleCode": "ASSISTANT_TEACHER",
  "description": "协助班主任进行班级管理"
}
```

#### 响应示例

```json
{
  "code": 200,
  "message": "新增角色成功",
  "data": null
}
```

#### 错误响应

```json
{
  "code": 500,
  "message": "角色编码已存在，请更换！",
  "data": null
}
```

***

### 5.4 修改角色

> **接口说明**：修改角色信息。

| 项目 | 内容 |
|------|------|
| **接口路径** | `/api/system/role/update` |
| **请求方法** | `POST` |
| **认证要求** | 是 |

#### 请求参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| `id` | Long | 是 | 角色ID |
| `roleName` | String | 是 | 角色名称 |
| `roleCode` | String | 是 | 角色编码（全局唯一） |
| `description` | String | 否 | 角色描述 |

#### 响应示例

```json
{
  "code": 200,
  "message": "修改角色成功",
  "data": null
}
```

***

### 5.5 删除角色

> **接口说明**：删除角色。如果该角色下还有绑定的用户，则禁止删除。

| 项目 | 内容 |
|------|------|
| **接口路径** | `/api/system/role/delete/{id}` |
| **请求方法** | `POST` |
| **认证要求** | 是 |

#### 请求参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| `id` | Long | 是 | 角色ID（路径参数） |

#### 响应示例

```json
{
  "code": 200,
  "message": "删除角色成功",
  "data": null
}
```

#### 错误响应（有关联用户）

```json
{
  "code": 500,
  "message": "当前角色下还有绑定的用户，无法删除！请先解除用户关联。",
  "data": null
}
```

***

### 5.6 获取权限树

> **接口说明**：获取完整的权限树状图，用于前端权限分配界面的展示。

| 项目 | 内容 |
|------|------|
| **接口路径** | `/api/system/role/permission/tree` |
| **请求方法** | `GET` |
| **认证要求** | 是 |

#### 响应示例

```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "parentId": 0,
      "name": "系统管理",
      "children": [
        {
          "id": 2,
          "parentId": 1,
          "name": "账号管理",
          "children": []
        }
      ]
    }
  ]
}
```

#### 字段说明

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | Long | 权限ID |
| `parentId` | Long | 父级ID（顶级为0） |
| `name` | String | 权限名称 |
| `children` | Array | 子权限列表 |

***

### 5.7 获取角色已有权限

> **接口说明**：获取指定角色当前已分配的权限ID列表，用于权限分配时的回显。

| 项目 | 内容 |
|------|------|
| **接口路径** | `/api/system/role/permission/own/{roleId}` |
| **请求方法** | `GET` |
| **认证要求** | 是 |

#### 请求参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| `roleId` | Long | 是 | 角色ID（路径参数） |

#### 响应示例

```json
{
  "code": 200,
  "message": "操作成功",
  "data": [1, 2, 3, 4, 5]
}
```

***

### 5.8 保存权限分配

> **接口说明**：为角色分配权限，先删除原有权限，再插入新勾选的权限。

| 项目 | 内容 |
|------|------|
| **接口路径** | `/api/system/role/permission/assign` |
| **请求方法** | `POST` |
| **认证要求** | 是 |

#### 请求参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| `roleId` | Long | 是 | 角色ID |
| `permissionIds` | Array | 是 | 权限ID数组（包含全选和半选节点） |

#### 请求示例

```json
{
  "roleId": 1,
  "permissionIds": [1, 2, 3, 4, 5]
}
```

#### 响应示例

```json
{
  "code": 200,
  "message": "权限分配成功！",
  "data": null
}
```

***

## 6. 权限管理接口

### 6.1 获取当前用户菜单

> **接口说明**：获取当前登录用户的菜单权限列表，用于前端渲染侧边栏菜单。

| 项目 | 内容 |
|------|------|
| **接口路径** | `/api/system/permission/my-menus` |
| **请求方法** | `GET` |
| **认证要求** | 是 |

#### 响应示例

```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "parentId": 0,
      "permName": "系统管理",
      "permType": 1,
      "routePath": "/system",
      "apiUrl": null,
      "icon": "setting",
      "sortOrder": 1
    },
    {
      "id": 2,
      "parentId": 1,
      "permName": "账号管理",
      "permType": 2,
      "routePath": "/system/user",
      "apiUrl": null,
      "icon": "user",
      "sortOrder": 1
    }
  ]
}
```

#### 字段说明

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | Long | 权限ID |
| `parentId` | Long | 父级ID（顶级为0） |
| `permName` | String | 权限/菜单名称 |
| `permType` | Integer | 权限类型：1-目录 2-菜单 3-按钮 |
| `routePath` | String | 前端路由路径 |
| `apiUrl` | String | 后端接口路径 |
| `icon` | String | 菜单图标 |
| `sortOrder` | Integer | 排序号 |

***

## 7. 班级管理接口

### 7.1 分页查询班级列表

> **接口说明**：分页查询班级列表，支持按班级名称和年级搜索，返回数据包含班主任姓名。

| 项目 | 内容 |
|------|------|
| **接口路径** | `/api/busi/class/page` |
| **请求方法** | `GET` |
| **认证要求** | 是 |

#### 请求参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| `page` | Integer | 否 | 页码，默认1 |
| `size` | Integer | 否 | 每页条数，默认10 |
| `className` | String | 否 | 班级名称（模糊查询） |
| `grade` | Integer | 否 | 年级：1-托班 2-小班 3-中班 4-大班 |

#### 响应示例

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "id": 1,
        "className": "大一班",
        "grade": 4,
        "headTeacherId": 1,
        "headTeacherName": "张老师",
        "capacity": 30,
        "status": 1,
        "createTime": "2026-03-01 10:00:00"
      }
    ],
    "total": 1,
    "size": 10,
    "current": 1
  }
}
```

#### 字段说明

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | Long | 班级ID |
| `className` | String | 班级名称 |
| `grade` | Integer | 年级：1-托班 2-小班 3-中班 4-大班 |
| `headTeacherId` | Long | 班主任ID |
| `headTeacherName` | String | 班主任姓名 |
| `capacity` | Integer | 班级容量 |
| `status` | Integer | 班级状态：1-正常 0-毕业/注销 |
| `createTime` | DateTime | 创建时间 |

***

### 7.2 新增班级

> **接口说明**：创建新班级。

| 项目 | 内容 |
|------|------|
| **接口路径** | `/api/busi/class/add` |
| **请求方法** | `POST` |
| **认证要求** | 是 |

#### 请求参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| `className` | String | 是 | 班级名称 |
| `grade` | Integer | 是 | 年级：1-托班 2-小班 3-中班 4-大班 |
| `headTeacherId` | Long | 否 | 班主任ID |
| `capacity` | Integer | 否 | 班级容量 |
| `status` | Integer | 否 | 班级状态，默认1（正常） |

#### 请求示例

```json
{
  "className": "小一班",
  "grade": 2,
  "headTeacherId": 1,
  "capacity": 30
}
```

#### 响应示例

```json
{
  "code": 200,
  "message": "新增班级成功",
  "data": null
}
```

#### 错误响应

```json
{
  "code": 500,
  "message": "该班级名称已存在！",
  "data": null
}
```

***

### 7.3 修改班级

> **接口说明**：修改班级信息。

| 项目 | 内容 |
|------|------|
| **接口路径** | `/api/busi/class/update` |
| **请求方法** | `POST` |
| **认证要求** | 是 |

#### 请求参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| `id` | Long | 是 | 班级ID |
| `className` | String | 是 | 班级名称 |
| `grade` | Integer | 是 | 年级：1-托班 2-小班 3-中班 4-大班 |
| `headTeacherId` | Long | 否 | 班主任ID |
| `capacity` | Integer | 否 | 班级容量 |
| `status` | Integer | 否 | 班级状态：1-正常 0-毕业/注销 |

#### 响应示例

```json
{
  "code": 200,
  "message": "修改班级成功",
  "data": null
}
```

***

### 7.4 删除班级

> **接口说明**：逻辑删除班级。

| 项目 | 内容 |
|------|------|
| **接口路径** | `/api/busi/class/delete/{id}` |
| **请求方法** | `POST` |
| **认证要求** | 是 |

#### 请求参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| `id` | Long | 是 | 班级ID（路径参数） |

#### 响应示例

```json
{
  "code": 200,
  "message": "删除班级成功",
  "data": null
}
```

***

## 8. 幼儿管理接口

### 8.1 分页查询幼儿列表

> **接口说明**：分页查询幼儿列表，默认只查询在园状态为"在读"的幼儿。

| 项目 | 内容 |
|------|------|
| **接口路径** | `/api/busi/student/page` |
| **请求方法** | `GET` |
| **认证要求** | 是 |

#### 请求参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| `page` | Integer | 否 | 页码，默认1 |
| `size` | Integer | 否 | 每页条数，默认10 |
| `studentName` | String | 否 | 幼儿姓名（模糊搜索） |
| `classId` | Long | 否 | 班级ID |
| `status` | Integer | 否 | 在园状态，默认1（1-在读） |

#### 响应示例

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "id": 1,
        "studentName": "张三",
        "gender": 1,
        "birthday": "2020-01-01",
        "classId": 1,
        "className": "小一班",
        "grade": 2,
        "avatar": null,
        "status": 1,
        "parents": [
          {
            "parentUserId": 10,
            "parentName": "张爸爸",
            "relation": "爸爸",
            "isPrimary": 1
          }
        ],
        "createTime": "2024-01-01 10:00:00"
      }
    ],
    "total": 1,
    "size": 10,
    "current": 1
  }
}
```

#### 字段说明

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | Long | 幼儿ID |
| `studentName` | String | 幼儿姓名 |
| `gender` | Integer | 性别：1-男 2-女 |
| `birthday` | Date | 出生日期 |
| `classId` | Long | 班级ID |
| `className` | String | 班级名称 |
| `grade` | Integer | 年级 |
| `status` | Integer | 在园状态：1-在读 2-毕业 3-退园 |
| `parents` | Array | 家长信息列表 |

***

### 8.2 获取幼儿详情

> **接口说明**：根据ID获取幼儿详细信息，包含家长信息。

| 项目 | 内容 |
|------|------|
| **接口路径** | `/api/busi/student/{id}` |
| **请求方法** | `GET` |
| **认证要求** | 是 |

#### 请求参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| `id` | Long | 是 | 幼儿ID（路径参数） |

#### 响应示例

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "studentName": "张三",
    "gender": 1,
    "birthday": "2020-01-01",
    "idCard": "110101202001010001",
    "classId": 1,
    "className": "小一班",
    "grade": 2,
    "admissionDate": "2023-09-01",
    "avatar": null,
    "status": 1,
    "parents": [
      {
        "id": 1,
        "parentUserId": 10,
        "parentName": "张爸爸",
        "parentPhone": "13800138000",
        "relation": "爸爸",
        "isPrimary": 1
      }
    ],
    "createTime": "2024-01-01 10:00:00",
    "updateTime": "2024-01-01 10:00:00"
  }
}
```

***

### 8.3 新增幼儿

> **接口说明**：新增幼儿档案，同时可绑定家长。

| 项目 | 内容 |
|------|------|
| **接口路径** | `/api/busi/student/add` |
| **请求方法** | `POST` |
| **认证要求** | 是 |

#### 请求参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| `studentName` | String | 是 | 幼儿姓名 |
| `gender` | Integer | 是 | 性别：1-男 2-女 |
| `birthday` | Date | 是 | 出生日期，格式：YYYY-MM-DD |
| `classId` | Long | 是 | 所属班级ID |
| `admissionDate` | Date | 否 | 入园日期，格式：YYYY-MM-DD |
| `idCard` | String | 否 | 身份证号/护照号 |
| `avatar` | String | 否 | 头像URL |
| `status` | Integer | 否 | 在园状态，默认1（1-在读） |
| `parents` | Array | 否 | 家长信息列表 |

#### parents 数组参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| `parentUserId` | Long | 是 | 家长用户ID |
| `relation` | String | 是 | 亲属关系（爸爸/妈妈/爷爷/奶奶等） |
| `isPrimary` | Integer | 否 | 是否主监护人，0-否 1-是 |

#### 请求示例

```json
{
  "studentName": "张三",
  "gender": 1,
  "birthday": "2020-01-01",
  "classId": 1,
  "admissionDate": "2023-09-01",
  "parents": [
    {
      "parentUserId": 10,
      "relation": "爸爸",
      "isPrimary": 1
    }
  ]
}
```

#### 响应示例

```json
{
  "code": 200,
  "message": "新增幼儿成功",
  "data": null
}
```

***

### 8.4 修改幼儿

> **接口说明**：修改幼儿档案信息，同时更新家长绑定关系。

| 项目 | 内容 |
|------|------|
| **接口路径** | `/api/busi/student/update` |
| **请求方法** | `POST` |
| **认证要求** | 是 |

#### 请求参数

同新增幼儿，需额外传入 `id` 字段。

#### 请求示例

```json
{
  "id": 1,
  "studentName": "张三",
  "gender": 1,
  "birthday": "2020-01-01",
  "classId": 1,
  "parents": [
    {
      "parentUserId": 10,
      "relation": "爸爸",
      "isPrimary": 1
    }
  ]
}
```

#### 响应示例

```json
{
  "code": 200,
  "message": "修改幼儿成功",
  "data": null
}
```

***

### 8.5 删除幼儿

> **接口说明**：逻辑删除幼儿，同时删除幼儿与家长的绑定关系。

| 项目 | 内容 |
|------|------|
| **接口路径** | `/api/busi/student/delete/{id}` |
| **请求方法** | `POST` |
| **认证要求** | 是 |

#### 请求参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| `id` | Long | 是 | 幼儿ID（路径参数） |

#### 响应示例

```json
{
  "code": 200,
  "message": "删除幼儿成功",
  "data": null
}
```

***

### 8.6 获取家长列表

> **接口说明**：获取家长用户列表，用于新增/编辑幼儿时选择家长。只返回 userType=2（家长）的用户。

| 项目 | 内容 |
|------|------|
| **接口路径** | `/api/busi/student/parent-list` |
| **请求方法** | `GET` |
| **认证要求** | 是 |

#### 响应示例

```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "userId": 10,
      "username": "zhangba",
      "realName": "张爸爸",
      "phone": "13800138000"
    }
  ]
}
```

#### 字段说明

| 字段 | 类型 | 说明 |
|------|------|------|
| `userId` | Long | 家长用户ID |
| `username` | String | 家长账号 |
| `realName` | String | 家长姓名 |
| `phone` | String | 联系电话 |

***

### 8.7 获取班级列表

> **接口说明**：获取班级列表，用于下拉选择。

| 项目 | 内容 |
|------|------|
| **接口路径** | `/api/busi/student/class-list` |
| **请求方法** | `GET` |
| **认证要求** | 是 |

#### 响应示例

```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "className": "小一班",
      "grade": 2
    },
    {
      "id": 2,
      "className": "中一班",
      "grade": 3
    }
  ]
}
```

#### 字段说明

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | Long | 班级ID |
| `className` | String | 班级名称 |
| `grade` | Integer | 年级 |

***

## 9. 考勤管理接口

### 9.1 分页查询考勤记录

> **接口说明**：分页查询幼儿考勤记录，支持按班级和日期筛选。

| 项目 | 内容 |
|------|------|
| **接口路径** | `/api/busi/attendance/page` |
| **请求方法** | `GET` |
| **认证要求** | 是 |

#### 请求参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| `page` | Integer | 否 | 页码，默认1 |
| `size` | Integer | 否 | 每页条数，默认10 |
| `classId` | Long | 否 | 班级ID |
| `date` | Date | 否 | 考勤日期，格式：yyyy-MM-dd |

#### 响应示例

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "id": 1,
        "studentId": 1,
        "studentName": "张三",
        "classId": 1,
        "className": "小一班",
        "date": "2026-03-19",
        "signInTime": "08:30:00",
        "signOutTime": "17:00:00",
        "status": 1,
        "remark": null
      }
    ],
    "total": 1,
    "size": 10,
    "current": 1
  }
}
```

#### 字段说明

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | Long | 考勤记录ID |
| `studentId` | Long | 幼儿ID |
| `studentName` | String | 幼儿姓名 |
| `classId` | Long | 班级ID |
| `className` | String | 班级名称 |
| `date` | Date | 考勤日期 |
| `signInTime` | String | 签到时间 |
| `signOutTime` | String | 签退时间 |
| `status` | Integer | 考勤状态：1-正常 2-迟到 3-早退 4-缺勤 |
| `remark` | String | 备注 |

***

### 9.2 签到

> **接口说明**：为幼儿进行签到操作。

| 项目 | 内容 |
|------|------|
| **接口路径** | `/api/busi/attendance/sign-in` |
| **请求方法** | `POST` |
| **认证要求** | 是 |

#### 请求参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| `studentIds` | Array | 是 | 幼儿ID列表（支持批量） |
| `classId` | Long | 是 | 班级ID |
| `date` | Date | 是 | 考勤日期，格式：yyyy-MM-dd |

#### 请求示例

```json
{
  "studentIds": [1, 2, 3],
  "classId": 1,
  "date": "2026-03-19"
}
```

#### 响应示例

```json
{
  "code": 200,
  "message": "签到成功",
  "data": null
}
```

***

### 9.3 签退

> **接口说明**：为幼儿进行签退操作。

| 项目 | 内容 |
|------|------|
| **接口路径** | `/api/busi/attendance/sign-out` |
| **请求方法** | `POST` |
| **认证要求** | 是 |

#### 请求参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| `studentIds` | Array | 是 | 幼儿ID列表（支持批量） |
| `classId` | Long | 是 | 班级ID |
| `date` | Date | 是 | 考勤日期，格式：yyyy-MM-dd |

#### 请求示例

```json
{
  "studentIds": [1, 2, 3],
  "classId": 1,
  "date": "2026-03-19"
}
```

#### 响应示例

```json
{
  "code": 200,
  "message": "签退成功",
  "data": null
}
```

***

## 10. 考勤统计接口

### 10.1 统计概览

> **接口说明**：获取考勤统计概览数据，包含出勤率和各状态人数。

| 项目 | 内容 |
|------|------|
| **接口路径** | `/api/busi/attendance/stat/overview` |
| **请求方法** | `GET` |
| **认证要求** | 是 |

#### 请求参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| `startDate` | String | 是 | 开始日期（yyyy-MM-dd） |
| `endDate` | String | 是 | 结束日期（yyyy-MM-dd），不能超过今天 |
| `classId` | Long | 否 | 班级ID，不传则查所有（受权限控制） |

#### 响应示例

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "totalShouldAttend": 1500,
    "totalPresent": 1358,
    "totalAbsent": 75,
    "totalLeave": 67,
    "attendanceRate": 90.53,
    "presentRate": 90.53,
    "absentRate": 5.0,
    "leaveRate": 4.47,
    "statusDistribution": [
      { "status": 1, "statusText": "出勤", "count": 1358, "rate": 90.53 },
      { "status": 2, "statusText": "缺勤", "count": 75, "rate": 5.0 },
      { "status": 3, "statusText": "事假", "count": 30, "rate": 2.0 },
      { "status": 4, "statusText": "病假", "count": 25, "rate": 1.67 },
      { "status": 5, "statusText": "其他请假", "count": 12, "rate": 0.8 }
    ]
  }
}
```

#### 字段说明

| 字段 | 类型 | 说明 |
|------|------|------|
| `totalShouldAttend` | Integer | 应到总人次 |
| `totalPresent` | Integer | 出勤总人次 |
| `totalAbsent` | Integer | 缺勤总人次 |
| `totalLeave` | Integer | 请假总人次 |
| `attendanceRate` | Double | 出勤率（百分比） |
| `presentRate` | Double | 出勤占比 |
| `absentRate` | Double | 缺勤占比 |
| `leaveRate` | Double | 请假占比 |
| `statusDistribution` | Array | 状态分布列表 |

***

### 10.2 各班出勤率统计

> **接口说明**：获取各班级的出勤率列表。

| 项目 | 内容 |
|------|------|
| **接口路径** | `/api/busi/attendance/stat/class-rate` |
| **请求方法** | `GET` |
| **认证要求** | 是 |

#### 请求参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| `startDate` | String | 是 | 开始日期（yyyy-MM-dd） |
| `endDate` | String | 是 | 结束日期（yyyy-MM-dd） |
| `classId` | Long | 否 | 班级ID，不传则查所有（受权限控制） |

#### 响应示例

```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "classId": 1,
      "className": "小一班",
      "grade": 2,
      "studentCount": 30,
      "workDays": 15,
      "shouldAttend": 450,
      "present": 425,
      "absent": 15,
      "leave": 10,
      "attendanceRate": 94.44
    }
  ]
}
```

#### 字段说明

| 字段 | 类型 | 说明 |
|------|------|------|
| `classId` | Long | 班级ID |
| `className` | String | 班级名称 |
| `grade` | Integer | 年级 |
| `studentCount` | Integer | 幼儿人数 |
| `workDays` | Integer | 工作日天数 |
| `shouldAttend` | Integer | 应到人次 |
| `present` | Integer | 出勤人次 |
| `absent` | Integer | 缺勤人次 |
| `leave` | Integer | 请假人次 |
| `attendanceRate` | Double | 出勤率（百分比） |

***

### 10.3 请假统计

> **接口说明**：获取请假类型统计。

| 项目 | 内容 |
|------|------|
| **接口路径** | `/api/busi/attendance/stat/leave` |
| **请求方法** | `GET` |
| **认证要求** | 是 |

#### 请求参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| `startDate` | String | 是 | 开始日期（yyyy-MM-dd） |
| `endDate` | String | 是 | 结束日期（yyyy-MM-dd） |
| `classId` | Long | 否 | 班级ID |

#### 响应示例

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "totalLeave": 67,
    "leaveDistribution": [
      { "leaveType": 1, "leaveTypeText": "事假", "count": 30, "rate": 44.78 },
      { "leaveType": 2, "leaveTypeText": "病假", "count": 25, "rate": 37.31 },
      { "leaveType": 3, "leaveTypeText": "其他", "count": 12, "rate": 17.91 }
    ]
  }
}
```

#### 字段说明

| 字段 | 类型 | 说明 |
|------|------|------|
| `totalLeave` | Integer | 请假总人数 |
| `leaveDistribution` | Array | 请假分布列表 |

***

### 10.4 趋势统计

> **接口说明**：获取每日出勤率趋势。

| 项目 | 内容 |
|------|------|
| **接口路径** | `/api/busi/attendance/stat/trend` |
| **请求方法** | `GET` |
| **认证要求** | 是 |

#### 请求参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| `startDate` | String | 是 | 开始日期（yyyy-MM-dd） |
| `endDate` | String | 是 | 结束日期（yyyy-MM-dd） |
| `classId` | Long | 否 | 班级ID |

#### 响应示例

```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    { "date": "2026-03-16", "shouldAttend": 300, "present": 280, "attendanceRate": 93.33 },
    { "date": "2026-03-17", "shouldAttend": 300, "present": 275, "attendanceRate": 91.67 },
    { "date": "2026-03-18", "shouldAttend": 300, "present": 285, "attendanceRate": 95.0 }
  ]
}
```

#### 字段说明

| 字段 | 类型 | 说明 |
|------|------|------|
| `date` | String | 日期（yyyy-MM-dd） |
| `shouldAttend` | Integer | 应到人数 |
| `present` | Integer | 出勤人数 |
| `attendanceRate` | Double | 出勤率（百分比） |

***

## 11. 请假管理接口

### 11.1 获取家长绑定的幼儿列表

> **接口说明**：获取当前登录家长绑定的幼儿列表，用于家长提交请假时选择幼儿。

| 项目 | 内容 |
|------|------|
| **接口路径** | `/api/busi/leave/students` |
| **请求方法** | `GET` |
| **认证要求** | 是（家长用户） |

#### 响应示例

```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "studentName": "张三",
      "className": "小一班",
      "classId": 1,
      "gender": 1,
      "birthday": "2020-01-01"
    }
  ]
}
```

#### 字段说明

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | Long | 幼儿ID |
| `studentName` | String | 幼儿姓名 |
| `className` | String | 班级名称 |
| `classId` | Long | 班级ID |
| `gender` | Integer | 性别：1-男 2-女 |
| `birthday` | Date | 出生日期 |

***

### 11.2 家长提交请假申请

> **接口说明**：家长为绑定的幼儿提交请假申请。

| 项目 | 内容 |
|------|------|
| **接口路径** | `/api/busi/leave/add` |
| **请求方法** | `POST` |
| **认证要求** | 是（家长用户） |

#### 请求参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| `studentId` | Long | 是 | 幼儿ID |
| `leaveType` | Integer | 是 | 请假类型：1-事假 2-病假 3-其他 |
| `startDate` | Date | 是 | 开始日期，格式：yyyy-MM-dd |
| `endDate` | Date | 是 | 结束日期，格式：yyyy-MM-dd |
| `reason` | String | 是 | 请假原因/备注 |
| `proofUrl` | String | 否 | 证明材料URL |

#### 请求示例

```json
{
  "studentId": 1,
  "leaveType": 2,
  "startDate": "2026-03-20",
  "endDate": "2026-03-21",
  "reason": "感冒发烧，需要在家休息",
  "proofUrl": ""
}
```

#### 响应示例

```json
{
  "code": 200,
  "message": "请假申请已提交",
  "data": null
}
```

#### 错误响应

```json
{
  "code": 500,
  "message": "请选择要请假的幼儿",
  "data": null
}
```

```json
{
  "code": 500,
  "message": "结束日期不能早于开始日期",
  "data": null
}
```

***

### 11.3 家长获取请假列表

> **接口说明**：家长获取自己孩子（绑定关系）的请假记录列表，支持按状态筛选。

| 项目 | 内容 |
|------|------|
| **接口路径** | `/api/busi/leave/page` |
| **请求方法** | `GET` |
| **认证要求** | 是（家长用户） |

#### 请求参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| `page` | Integer | 否 | 页码，默认1 |
| `size` | Integer | 否 | 每页条数，默认10 |
| `auditStatus` | Integer | 否 | 审批状态筛选：0-待审批 1-已通过 4-已拒绝 5-已撤销 |

#### 响应示例

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "id": 1,
        "studentId": 1,
        "studentName": "张三",
        "className": "小一班",
        "leaveType": 2,
        "leaveTypeName": "病假",
        "startDate": "2026-03-20",
        "endDate": "2026-03-21",
        "leaveDays": 2,
        "reason": "感冒发烧",
        "auditStatus": 0,
        "auditStatusName": "待审批",
        "teacherRemark": null,
        "createTime": "2026-03-19 10:00:00"
      }
    ],
    "total": 1,
    "size": 10,
    "current": 1
  }
}
```

#### 字段说明

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | Long | 请假记录ID |
| `studentId` | Long | 幼儿ID |
| `studentName` | String | 幼儿姓名 |
| `className` | String | 班级名称 |
| `leaveType` | Integer | 请假类型：1-事假 2-病假 3-其他 |
| `leaveTypeName` | String | 请假类型名称 |
| `startDate` | Date | 开始日期 |
| `endDate` | Date | 结束日期 |
| `leaveDays` | Integer | 请假天数 |
| `reason` | String | 请假原因 |
| `auditStatus` | Integer | 审批状态：0-待审批 1-已通过 2-待园长审批 3-园长已通过 4-已拒绝 5-已撤销 |
| `auditStatusName` | String | 审批状态名称 |
| `teacherRemark` | String | 班主任审批意见 |
| `createTime` | DateTime | 申请提交时间 |

***

### 11.4 班主任获取请假列表

> **接口说明**：班主任获取本班幼儿的请假申请列表，支持按状态筛选。

| 项目 | 内容 |
|------|------|
| **接口路径** | `/api/busi/leave/teacher-page` |
| **请求方法** | `GET` |
| **认证要求** | 是（教职工用户） |

#### 请求参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| `page` | Integer | 否 | 页码，默认1 |
| `size` | Integer | 否 | 每页条数，默认10 |
| `auditStatus` | Integer | 否 | 审批状态筛选：0-待审批 1-已通过 4-已拒绝 5-已撤销 |

#### 响应示例

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "id": 1,
        "studentId": 1,
        "studentName": "张三",
        "className": "小一班",
        "parentName": "张爸爸",
        "parentPhone": "13800138000",
        "leaveType": 1,
        "leaveTypeName": "事假",
        "startDate": "2026-03-20",
        "endDate": "2026-03-21",
        "leaveDays": 2,
        "reason": "家里有事",
        "proofUrl": null,
        "auditStatus": 0,
        "auditStatusName": "待审批",
        "createTime": "2026-03-19 10:00:00"
      }
    ],
    "total": 1,
    "size": 10,
    "current": 1
  }
}
```

***

### 11.5 管理员获取请假列表

> **接口说明**：园长/管理员获取全园所有幼儿的请假记录列表，支持按班级、状态筛选。

| 项目 | 内容 |
|------|------|
| **接口路径** | `/api/busi/leave/admin-page` |
| **请求方法** | `GET` |
| **认证要求** | 是（管理员用户） |

#### 请求参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| `page` | Integer | 否 | 页码，默认1 |
| `size` | Integer | 否 | 每页条数，默认10 |
| `classId` | Long | 否 | 班级ID筛选 |
| `auditStatus` | Integer | 否 | 审批状态筛选 |
| `studentName` | String | 否 | 幼儿姓名（模糊查询） |

#### 响应示例

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "id": 1,
        "studentId": 1,
        "studentName": "张三",
        "classId": 1,
        "className": "小一班",
        "parentName": "张爸爸",
        "parentPhone": "13800138000",
        "leaveType": 1,
        "leaveTypeName": "事假",
        "startDate": "2026-03-20",
        "endDate": "2026-03-21",
        "leaveDays": 2,
        "reason": "家里有事",
        "proofUrl": null,
        "auditStatus": 1,
        "auditStatusName": "已通过",
        "teacherName": "李老师",
        "teacherAuditTime": "2026-03-19 14:00:00",
        "teacherRemark": "情况属实，同意请假",
        "adminName": null,
        "adminAuditTime": null,
        "adminRemark": null,
        "createTime": "2026-03-19 10:00:00"
      }
    ],
    "total": 1,
    "size": 10,
    "current": 1
  }
}
```

#### 字段说明

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | Long | 请假记录ID |
| `studentId` | Long | 幼儿ID |
| `studentName` | String | 幼儿姓名 |
| `classId` | Long | 班级ID |
| `className` | String | 班级名称 |
| `parentName` | String | 申请人姓名 |
| `parentPhone` | String | 申请人手机号 |
| `leaveType` | Integer | 请假类型 |
| `leaveTypeName` | String | 请假类型名称 |
| `startDate` | Date | 开始日期 |
| `endDate` | Date | 结束日期 |
| `leaveDays` | Integer | 请假天数 |
| `reason` | String | 请假原因 |
| `proofUrl` | String | 证明材料URL |
| `auditStatus` | Integer | 审批状态 |
| `auditStatusName` | String | 审批状态名称 |
| `teacherName` | String | 班主任审批人姓名 |
| `teacherAuditTime` | DateTime | 班主任审批时间 |
| `teacherRemark` | String | 班主任审批意见 |
| `adminName` | String | 园长审批人姓名 |
| `adminAuditTime` | DateTime | 园长审批时间 |
| `adminRemark` | String | 园长审批意见 |
| `createTime` | DateTime | 申请提交时间 |

***

### 11.6 审批请假

> **接口说明**：班主任或管理员审批幼儿请假申请，支持通过或拒绝。

| 项目 | 内容 |
|------|------|
| **接口路径** | `/api/busi/leave/approve` |
| **请求方法** | `POST` |
| **认证要求** | 是（教职工用户） |

#### 请求参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| `id` | Long | 是 | 请假记录ID |
| `auditStatus` | Integer | 是 | 审批结果：1-通过 4-拒绝 |
| `remark` | String | 否 | 审批意见（拒绝时必填） |

#### 请求示例

```json
{
  "id": 1,
  "auditStatus": 1,
  "remark": "情况属实，同意请假"
}
```

#### 响应示例

```json
{
  "code": 200,
  "message": "审批成功",
  "data": null
}
```

#### 错误响应

```json
{
  "code": 500,
  "message": "只有待审批状态才能审批",
  "data": null
}
```

```json
{
  "code": 500,
  "message": "拒绝审批时必须填写审批意见",
  "data": null
}
```

***

## 12. 教师班级关联接口

### 12.1 获取班级教师列表

> **接口说明**：获取指定班级下的所有教师列表。

| 项目 | 内容 |
|------|------|
| **接口路径** | `/api/busi/teacher-class/list/{classId}` |
| **请求方法** | `GET` |
| **认证要求** | 是 |

#### 请求参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| `classId` | Long | 是 | 班级ID（路径参数） |

#### 响应示例

```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "realName": "张老师",
      "username": "13800138000",
      "userType": 1,
      "status": 1
    }
  ]
}
```

#### 字段说明

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | Long | 教师用户ID |
| `realName` | String | 教师真实姓名 |
| `username` | String | 教师账号/手机号 |
| `userType` | Integer | 用户类型：1-教职工 |
| `status` | Integer | 账号状态：1-正常 |

***

### 12.2 分配教师到班级

> **接口说明**：将一名或多名教师分配到指定班级，可设置是否为班主任。

| 项目 | 内容 |
|------|------|
| **接口路径** | `/api/busi/teacher-class/assign` |
| **请求方法** | `POST` |
| **认证要求** | 是 |

#### 请求参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| `classId` | Long | 是 | 班级ID |
| `teacherIds` | Array | 是 | 教师ID列表 |
| `isHeadTeacher` | Integer | 否 | 是否设置为班主任：0-否 1-是 |

#### 请求示例

```json
{
  "classId": 1,
  "teacherIds": [1, 2],
  "isHeadTeacher": 1
}
```

#### 响应示例

```json
{
  "code": 200,
  "message": "教师分配成功",
  "data": null
}
```

***---

## 13. 通知管理接口

### 13.1 分页查询通知列表（管理端）

> **接口说明**：分页查询通知列表，用于教师/管理员管理通知。

| 项目 | 内容 |
|------|------|
| **接口路径** | `/api/busi/notice/manage/page` |
| **请求方法** | `GET` |
| **认证要求** | 是 |

#### 请求参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| page | Integer | 否 | 页码，默认1 |
| size | Integer | 否 | 每页条数，默认10 |
| title | String | 否 | 通知标题（模糊查询） |
| noticeType | Integer | 否 | 通知类型：1-日常 2-缴费 3-放假 |
| noticeLevel | Integer | 否 | 通知级别：1-普通 2-置顶 3-强提醒 |
| startDate | String | 否 | 开始日期 |
| endDate | String | 否 | 结束日期 |

#### 响应示例

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "id": 1,
        "title": "关于五一放假的通知",
        "noticeType": 3,
        "noticeTypeText": "放假通知",
        "noticeLevel": 2,
        "noticeLevelText": "置顶",
        "targetScope": 1,
        "targetScopeText": "全园",
        "targetClassId": null,
        "targetClassName": null,
        "publisherId": 2,
        "publisherName": "系统管理员",
        "publishTime": "2026-03-20 16:08:06",
        "createTime": "2026-03-20 16:08:06",
        "confirmStatus": null
      }
    ],
    "total": 1,
    "size": 10,
    "current": 1,
    "pages": 1
  }
}
```

#### 字段说明

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 通知ID |
| title | String | 通知标题 |
| noticeType | Integer | 通知类型：1-日常 2-缴费 3-放假 |
| noticeTypeText | String | 通知类型文本 |
| noticeLevel | Integer | 通知级别：1-普通 2-置顶 3-强提醒 |
| noticeLevelText | String | 通知级别文本 |
| targetScope | Integer | 发送范围：1-全园 2-指定班级 |
| targetScopeText | String | 发送范围文本 |
| targetClassId | Long | 目标班级ID |
| targetClassName | String | 目标班级名称 |
| publisherId | Long | 发布人ID |
| publisherName | String | 发布人姓名 |
| publishTime | DateTime | 发布时间 |
| createTime | DateTime | 创建时间 |
| confirmStatus | Integer | 确认状态：1-已确认 2-待确认 |

***

### 13.2 发布通知

> **接口说明**：发布新通知，支持全园或指定班级范围。

| 项目 | 内容 |
|------|------|
| **接口路径** | `/api/busi/notice` |
| **请求方法** | `POST` |
| **认证要求** | 是 |

#### 请求参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| title | String | 是 | 通知标题 |
| content | String | 是 | 通知正文 |
| noticeType | Integer | 是 | 通知类型：1-日常 2-缴费 3-放假 |
| noticeLevel | Integer | 是 | 通知级别：1-普通 2-置顶 3-强提醒 |
| targetScope | Integer | 是 | 发送范围：1-全园 2-指定班级 |
| targetClassId | Long | 否 | 目标班级ID（scope=2时必填） |

#### 请求示例

```json
{
  "title": "关于五一放假的通知",
  "content": "五一假期安排：5月1日-5月5日放假。",
  "noticeType": 3,
  "noticeLevel": 2,
  "targetScope": 1
}
```

#### 响应示例

```json
{
  "code": 200,
  "message": "通知发布成功",
  "data": null
}
```

#### 错误响应

```json
{
  "code": 500,
  "message": "只有管理员才能发布全园通知",
  "data": null
}
```

***

### 13.3 获取通知详情

> **接口说明**：根据ID获取通知详细信息。

| 项目 | 内容 |
|------|------|
| **接口路径** | `/api/busi/notice/{id}` |
| **请求方法** | `GET` |
| **认证要求** | 是 |

#### 响应示例

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "title": "关于五一放假的通知",
    "content": "五一假期安排：5月1日-5月5日放假。",
    "noticeType": 3,
    "noticeTypeText": "放假通知",
    "noticeLevel": 2,
    "noticeLevelText": "置顶",
    "targetScope": 1,
    "targetScopeText": "全园",
    "targetClassId": null,
    "targetClassName": null,
    "publisherId": 2,
    "publisherName": "系统管理员",
    "publishTime": "2026-03-20 16:08:06",
    "createTime": "2026-03-20 16:08:06",
    "confirmStatus": null
  }
}
```

***

### 13.4 获取确认情况

> **接口说明**：获取通知的确认情况统计和明细列表。

| 项目 | 内容 |
|------|------|
| **接口路径** | `/api/busi/notice/{id}/receipts` |
| **请求方法** | `GET` |
| **认证要求** | 是 |

#### 响应示例

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "total": 30,
    "confirmed": 25,
    "unconfirmed": 5,
    "details": [
      {
        "studentId": 1,
        "studentName": "张三",
        "parentName": "张爸爸",
        "relation": "爸爸",
        "isConfirmed": true,
        "confirmTime": "2026-03-20 10:30:00"
      }
    ]
  }
}
```

#### 字段说明

| 字段 | 类型 | 说明 |
|------|------|------|
| total | Integer | 幼儿总数 |
| confirmed | Integer | 已确认人数 |
| unconfirmed | Integer | 未确认人数 |
| details | Array | 确认明细列表 |

***

### 13.5 分页查询通知列表（查看端）

> **接口说明**：家长/教师查看通知列表，支持筛选。

| 项目 | 内容 |
|------|------|
| **接口路径** | `/api/busi/notice/view/page` |
| **请求方法** | `GET` |
| **认证要求** | 是 |

#### 请求参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| page | Integer | 否 | 页码，默认1 |
| size | Integer | 否 | 每页条数，默认10 |
| noticeType | Integer | 否 | 通知类型：1-日常 2-缴费 3-放假 |
| noticeLevel | Integer | 否 | 通知级别：1-普通 2-置顶 3-强提醒 |
| confirmStatus | Integer | 否 | 确认状态：1-已确认 2-待确认 |

#### 响应示例

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "id": 1,
        "title": "关于五一放假的通知",
        "noticeType": 3,
        "noticeTypeText": "放假通知",
        "noticeLevel": 2,
        "noticeLevelText": "置顶",
        "targetScope": 1,
        "targetScopeText": "全园",
        "publishTime": "2026-03-20 16:08:06",
        "confirmStatus": 2
      }
    ],
    "total": 1,
    "size": 10,
    "current": 1
  }
}
```

***

### 13.6 确认通知

> **接口说明**：确认已读某条通知。

| 项目 | 内容 |
|------|------|
| **接口路径** | `/api/busi/notice/{id}/confirm` |
| **请求方法** | `POST` |
| **认证要求** | 是 |

#### 响应示例

```json
{
  "code": 200,
  "message": "确认成功",
  "data": null
}
```

#### 错误响应

```json
{
  "code": 500,
  "message": "该通知不存在",
  "data": null
}
```

---

## 14. 错误码说明

### 14.1 通用错误码

| 错误码 | 错误信息 | 说明 | 处理建议 |
|--------|----------|------|----------|
| 200 | 操作成功 | 请求成功 | 正常处理 |
| 400 | 参数错误 | 请求参数不合法 | 检查请求参数 |
| 401 | 未登录 | Token 无效或已过期 | 引导用户重新登录 |
| 403 | 无权限 | 缺乏必要权限 | 联系管理员授权 |
| 404 | 资源不存在 | 请求的资源不存在 | 检查请求路径 |
| 500 | 操作失败 | 服务器内部错误 | 联系技术支持 |

### 14.2 业务错误码

| 错误信息 | 说明 |
|----------|------|
| 账号或密码错误 | 登录验证失败 |
| 该账号已被禁用，请联系幼儿园管理员 | 用户账号被禁用 |
| 该手机号/账号已被注册！ | 新增/修改用户时手机号重复 |
| 角色编码已存在，请更换！ | 新增/修改角色时编码重复 |
| 当前角色下还有绑定的用户，无法删除！请先解除用户关联。 | 删除角色时有关联用户 |
| 该班级名称已存在！ | 新增/修改班级时名称重复 |
| 请选择要请假的幼儿 | 家长提交请假时未选择幼儿 |
| 结束日期不能早于开始日期 | 日期参数校验失败 |
| 只有家长才能提交请假申请 | 当前用户不是家长类型 |
| 只有家长才能查看请假列表 | 当前用户不是家长类型 |
| 只有管理员才能查看全园请假列表 | 当前用户不是管理员/园长角色 |
| 只有待审批状态才能审批 | 当前请假状态不是待审批 |
| 拒绝审批时必须填写审批意见 | 拒绝时未填写审批意见 |
| 只有班主任才能审批本班请假 | 当前用户不是该幼儿所在班的班主任 |

***

## 附录

### A. 用户类型参考

| 值 | 说明 |
|----|------|
| 1 | 教职工 |
| 2 | 家长 |

### B. 角色编码参考

| 编码 | 说明 |
|------|------|
| SUPER_ADMIN | 超级管理员 |
| PRINCIPAL | 园长 |
| HEAD_TEACHER | 班主任 |
| ASSISTANT_TEACHER | 配班老师 |
| HEALTH_DOCTOR | 保健医生 |
| PARENT | 家长 |
| UNKNOWN | 未分配角色 |

### C. 数据范围参考

| 值 | 说明 |
|----|------|
| 1 | 全园数据 |
| 2 | 本班数据 |
| 3 | 个人数据 |
| 4 | 仅限绑定关系（家长） |

### D. 请假类型参考

| 值 | 说明 |
|----|------|
| 1 | 事假 |
| 2 | 病假 |
| 3 | 其他 |

### E. 审批状态参考

| 值 | 说明 |
|----|------|
| 0 | 待审批 |
| 1 | 班主任已批（流程结束） |
| 2 | 待园长审批（超7天） |
| 3 | 园长已批（流程结束） |
| 4 | 已驳回 |
| 5 | 家长已撤销 |

### F. 考勤状态参考

| 值 | 说明 |
|----|------|
| 1 | 正常 |
| 2 | 迟到 |
| 3 | 早退 |
| 4 | 缺勤 |

### G. 年级参考

| 值 | 说明 |
|----|------|
| 1 | 托班 |
| 2 | 小班 |
| 3 | 中班 |
| 4 | 大班 |

### H. 权限类型参考

| 值 | 说明 |
|----|------|
| 1 | 目录 |
| 2 | 菜单 |
| 3 | 按钮（接口） |

***

> **文档维护人**：KDS开发团队
> **文档版本**：1.6.0
> **更新时间**：2026-03-20
