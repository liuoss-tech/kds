# 私信模块规格说明书

## 一、模块概述

### 1.1 业务背景
幼儿园家校联系系统中，家长和教师需要进行一对一私密沟通。本模块实现私信的发送、接收、查看及已读未读状态显示功能。

### 1.2 技术栈
- 后端：Spring Boot 2.7.18 + Java 17 + MyBatis Plus 3.5.5
- 前端：Vue 3.5.30 + Element Plus 2.13.5
- 数据库：MySQL 8.0+
- 实时通知：前端轮询（每10秒查询一次）

### 1.3 设计原则
- 一对一私聊，不支持群聊
- 显示消息已读/未读状态
- 所有用户默认可使用私信功能（类似首页）

---

## 二、功能需求

### 2.1 聊天对象选择

| 用户角色 | 选择方式 | 可联系人范围 |
|---------|---------|-------------|
| 教师 | 按班级 → 选择该班家长 | 本班幼儿的家长（通过幼儿-家长绑定关系查找） |
| 家长 | 按幼儿 → 选择该幼儿班级的老师 | 孩子班级的老师（班主任、配班老师等） |

### 2.2 消息状态

| 状态 | 显示 | 说明 |
|-----|------|------|
| 未读 | 灰色"未读"文字 | 对方尚未查看 |
| 已读 | 蓝色"已读"文字 | 对方已查看 |

### 2.3 新消息提醒

- 顶部导航栏消息图标显示红色圆点数字
- 点击进入私信页面

### 2.4 菜单位置

- 原"首页看板"目录改名为**"消息中心"**
- 目录包含：**首页**、**私信**（新增）
- 所有用户默认可见，属于基础权限，不可删除

---

## 三、API 设计

### 3.1 后端接口

| 接口路径 | 请求方式 | 功能 |
|---------|---------|------|
| `/api/busi/message/conversations` | GET | 获取会话列表 |
| `/api/busi/message/history` | GET | 获取与指定用户的聊天记录 |
| `/api/busi/message/send` | POST | 发送私信 |
| `/api/busi/message/mark-read` | POST | 标记消息为已读 |
| `/api/busi/message/unread-count` | GET | 获取未读消息数量 |
| `/api/busi/message/contacts` | GET | 获取可联系的人列表 |

### 3.2 请求/响应格式

#### 3.2.1 获取会话列表
```
GET /api/busi/message/conversations
响应：
{
  "code": 200,
  "data": [
    {
      "contactUserId": 10,
      "contactUserName": "张爸爸",
      "contactUserType": 2,
      "lastMessage": "明天要带什么？",
      "lastMessageTime": "2026-03-19 10:30:00",
      "unreadCount": 2
    }
  ]
}
```

#### 3.2.2 获取聊天记录
```
GET /api/busi/message/history?contactUserId=10&page=1&size=20
响应：
{
  "code": 200,
  "data": {
    "records": [
      {
        "id": 1,
        "senderId": 1,
        "senderName": "李老师",
        "receiverId": 10,
        "content": "明天要带什么？",
        "isRead": true,
        "createTime": "2026-03-19 10:30:00"
      }
    ],
    "total": 50,
    "size": 20,
    "current": 1
  }
}
```

#### 3.2.3 发送私信
```
POST /api/busi/message/send
{
  "receiverId": 10,
  "content": "明天要带水彩笔"
}
响应：
{
  "code": 200,
  "message": "发送成功",
  "data": null
}
```

#### 3.2.4 标记已读
```
POST /api/busi/message/mark-read
{
  "contactUserId": 10
}
响应：
{
  "code": 200,
  "message": "标记成功",
  "data": null
}
```

#### 3.2.5 获取未读数量
```
GET /api/busi/message/unread-count
响应：
{
  "code": 200,
  "data": 5
}
```

#### 3.2.6 获取可联系人列表

教师调用时：
```
GET /api/busi/message/contacts
响应：
{
  "code": 200,
  "data": [
    {
      "classId": 1,
      "className": "大一班",
      "studentId": 1,
      "studentName": "张小明",
      "parents": [
        {
          "userId": 10,
          "realName": "张爸爸",
          "relation": "爸爸"
        }
      ]
    }
  ]
}
```

家长调用时：
```
GET /api/busi/message/contacts
响应：
{
  "code": 200,
  "data": [
    {
      "classId": 1,
      "className": "大一班",
      "studentId": 1,
      "studentName": "张小明",
      "teachers": [
        {
          "userId": 1,
          "realName": "李老师",
          "isHeadTeacher": true
        }
      ]
    }
  ]
}
```

---

## 四、数据库设计

### 4.1 现有表结构：sys_message

根据数据字典，sys_message 表结构如下：

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | bigint | 主键ID |
| sender_id | bigint | 发送者ID (0代表系统自动发送) |
| receiver_id | bigint | 接收者ID |
| message_type | tinyint | 消息类型：1-系统自动提醒 2-家校私信 3-审批结果通知 |
| content | varchar(1000) | 消息内容 |
| related_id | bigint | 关联业务ID |
| is_read | tinyint | 是否已读：0-未读 1-已读 |
| read_time | datetime | 阅读时间 |
| create_time | datetime | 发送时间 |
| is_deleted | tinyint | 逻辑删除 |

### 4.2 消息类型扩展

| message_type | 说明 |
|-------------|------|
| 1 | 系统自动提醒(催办未读) |
| 2 | 家校私信 |
| 3 | 审批结果通知 |

### 4.3 新增索引

为提升查询性能，在 sys_message 表增加索引：
```sql
KEY `idx_sender_receiver` (`sender_id`, `receiver_id`)
```

---

## 五、数据权限设计

### 5.1 权限校验点

| 操作 | 校验内容 |
|-----|---------|
| 发送消息 | 校验接收者必须在可联系范围内（教师→本班家长，家长→孩子班级老师） |
| 查看消息 | 校验当前用户必须是发送者或接收者 |
| 会话列表 | 只显示当前用户参与的会话 |
| 联系人列表 | 根据用户类型过滤可联系的人 |

### 5.2 隔离逻辑

#### 教师发送消息
```
1. 获取教师分配的班级列表
2. 获取这些班级的幼儿列表
3. 获取这些幼儿绑定的家长列表
4. 校验接收者是否在上述家长列表中
```

#### 家长发送消息
```
1. 获取家长绑定的幼儿列表
2. 获取这些幼儿所在班级
3. 获取这些班级的老师列表
4. 校验接收者是否在上述老师列表中
```

---

## 六、前端页面设计

### 6.1 页面路由

| 路径 | 页面 | 访问角色 |
|------|------|----------|
| /message | 私信页面 | 所有用户 |

### 6.2 布局设计

采用左侧会话列表 + 右侧聊天窗口的布局（类似微信）：

```
+---------------------------------------------------+
|  消息中心                                          |
+--------+------------------------------------------+
|        |  聊天对象：李老师                [关闭]    |
| 会话1  |  ----------------------------------------|
|        |                                          |
| 会话2  |  [消息气泡]                              |
|        |                                          |
| 会话3  |         [我的消息]                       |
|        |                                          |
|        |  ----------------------------------------|
|        |  [输入框................] [发送]        |
+--------+------------------------------------------+
```

### 6.3 组件结构

```
Message.vue (私信主页面)
├── ConversationList (左侧会话列表)
│   ├── ConversationItem (会话项)
│   └── EmptyState (空状态)
├── ChatWindow (右侧聊天窗口)
│   ├── ChatHeader (聊天对象信息)
│   ├── MessageList (消息列表)
│   │   ├── MessageBubble (消息气泡)
│   │   └── ReadStatus (已读/未读状态)
│   └── MessageInput (输入框和发送按钮)
└── ContactSelector (选择聊天对象弹窗)
    ├── TeacherSide (教师选择家长)
    └── ParentSide (家长选择老师)
```

### 6.4 轮询机制

- 前端每 10 秒调用 `/api/busi/message/unread-count` 查询未读数量
- 如果未读数量变化，更新顶部导航栏红点
- 用户进入聊天窗口时，自动标记消息为已读

---

## 七、菜单与权限配置

### 7.1 目录更名

将"首页看板"目录改名为"消息中心"：
```sql
UPDATE sys_permission SET perm_name = '消息中心' WHERE perm_name = '首页看板';
```

### 7.2 私信菜单

| 字段 | 值 |
|-----|---|
| perm_name | 私信 |
| parent_id | (消息中心目录ID=4) |
| perm_type | 2 (菜单) |
| route_path | /message |
| api_url | /api/busi/message/* |

### 7.3 权限分配

所有角色默认分配私信菜单权限：
```sql
-- 为所有角色添加私信菜单权限
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT r.id, p.id FROM sys_role r, sys_permission p
WHERE p.route_path = '/message';
```

---

## 八、可复用代码

### 8.1 后端
- 使用现有的 `LoginHelper` 获取当前用户信息
- 使用现有的 `Result` 统一响应格式
- 使用现有的 `Page` 分页查询模式
- 复用 `busi_` 包下的业务模块结构
- 复用现有的异常处理机制

### 8.2 前端
- 复用现有的 `request.js` 封装
- 复用现有的页面布局和样式
- 复用 Element Plus 组件
- 复用幼儿园主题风格（粉色渐变、圆角设计等）

---

## 九、测试计划

### 9.1 接口测试
- [x] 获取会话列表
- [x] 获取聊天记录（分页）
- [x] 发送私信
- [x] 标记消息已读
- [x] 获取未读消息数量
- [x] 获取可联系人列表（教师端）
- [x] 获取可联系人列表（家长端）

### 9.2 角色权限测试
- [x] 教师只能给本班家长发消息
- [x] 家长只能给孩子班级老师发消息
- [x] 用户只能查看自己参与的消息
- [x] 无法给无关人员发送消息

### 9.3 消息状态测试
- [x] 发送消息后对方显示未读
- [x] 对方点开聊天后显示已读
- [x] 会话列表显示未读消息数量

### 9.4 UI测试
- [x] 左侧会话列表正常显示
- [x] 右侧聊天窗口正常显示
- [x] 消息气泡样式正确
- [x] 已读/未读状态正确显示
- [x] 顶部导航栏未读红点正确显示

---

## 十、注意事项

1. **实时性**：短期使用轮询机制，后续可升级为 WebSocket
2. **数据隔离**：私信模块不使用 data_scope，需在业务层手动实现权限校验
3. **消息类型**：私信使用 message_type=2
4. **菜单配置**：所有用户默认有私信权限
5. **目录更名**：需同步修改前端菜单名称
