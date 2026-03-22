# 私信模块检查清单

## 一、数据库配置检查

- [x] 数据库表 sys_permission 中"首页看板"已更名为"消息中心"
- [x] sys_permission 表中已添加"私信"菜单权限记录
- [x] 私信权限已分配给所有角色
- [x] sys_message 表已添加 idx_sender_receiver 索引

## 二、后端代码检查

### 2.1 实体类与DTO
- [x] SysMessage 实体类属性与数据字典一致
- [x] MessageSendDTO 包含 receiverId 和 content 字段
- [x] ConversationVO 包含联系人信息、最新消息、未读数量
- [x] MessageVO 包含发送者、接收者、内容、已读状态、时间

### 2.2 Mapper层
- [x] SysMessageMapper 接口继承 BaseMapper
- [x] 会话列表查询方法正确（按最新消息排序）
- [x] 聊天记录查询方法支持分页
- [x] 未读消息数量查询方法正确

### 2.3 Service层
- [x] 发送消息时校验接收者在可联系范围内
- [x] 查看消息时校验当前用户是发送者或接收者
- [x] 会话列表只返回当前用户参与的会话
- [x] 教师获取联系人时返回本班幼儿的家长
- [x] 家长获取联系人时返回孩子班级的老师

### 2.4 Controller层
- [x] 会话列表接口 GET /api/busi/message/conversations 正常返回
- [x] 聊天记录接口 GET /api/busi/message/history 支持分页
- [x] 发送私信接口 POST /api/busi/message/send 正常返回
- [x] 标记已读接口 POST /api/busi/message/mark-read 正常返回
- [x] 未读数量接口 GET /api/busi/message/unread-count 正常返回
- [x] 联系人列表接口 GET /api/busi/message/contacts 正常返回

## 三、前端代码检查

### 3.1 页面与路由
- [x] 路由 /message 已正确配置
- [x] Message.vue 页面布局为左侧会话列表 + 右侧聊天窗口
- [x] ConversationList.vue 组件正常显示会话列表
- [x] ChatWindow.vue 组件正常显示聊天详情
- [x] MessageBubble.vue 组件正确显示消息气泡
- [x] ContactSelector.vue 组件正确实现选择联系人功能

### 3.2 API封装
- [x] getConversations 方法正确调用后端接口
- [x] getMessageHistory 方法正确调用后端接口
- [x] sendMessage 方法正确调用后端接口
- [x] markRead 方法正确调用后端接口
- [x] getUnreadCount 方法正确调用后端接口
- [x] getContacts 方法正确调用后端接口

### 3.3 功能检查
- [x] 消息发送后对方可以在会话列表看到
- [x] 点击会话可以查看聊天记录
- [x] 每条消息下方正确显示已读/未读状态
- [x] 对方查看消息后状态变为"已读"
- [x] 顶部导航栏图标显示未读消息红点
- [x] 教师可以在弹窗中选择本班幼儿的家长
- [x] 家长可以在弹窗中选择孩子的老师

### 3.4 轮询机制
- [x] 页面加载后每10秒查询一次未读消息数量
- [x] 未读数量变化时正确更新UI

## 四、菜单配置检查

- [x] 前端菜单中"消息中心"目录正确显示
- [x] "消息中心"目录下包含"首页"和"私信"菜单
- [x] 所有用户登录后可以看到私信菜单

## 五、API测试检查

### 5.1 正常场景
- [x] 测试获取会话列表接口返回正确格式
- [x] 测试获取聊天记录接口返回正确格式
- [x] 测试发送私信接口成功发送
- [x] 测试标记已读接口成功标记
- [x] 测试获取未读数量接口返回正确数字
- [x] 测试获取联系人列表（教师）返回本班家长
- [x] 测试获取联系人列表（家长）返回孩子班级老师

### 5.2 权限场景
- [x] 测试教师给本班家长发消息成功
- [x] 测试教师给非本班家长发消息失败
- [x] 测试家长给孩子班级老师发消息成功
- [x] 测试家长给无关老师发消息失败
- [x] 测试用户查看他人消息失败
- [x] 测试未登录用户无法访问私信接口

### 5.3 消息状态场景
- [x] 测试发送消息后对方显示未读
- [x] 测试对方点开聊天后消息变为已读
- [x] 测试会话列表显示正确的未读数量

## 六、文档更新检查

- [x] 后端API.md 已添加私信模块接口文档
- [x] 数据字典.md 中 sys_message 表结构正确
- [x] 已创建私信模块文档 18-私信模块.md
