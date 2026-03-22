# 通知管理模块开发任务

## 后端开发任务

### Task 1: 数据库操作

- [x] 创建 `add_notice_permission.sql` 权限脚本
  - 权限19: 通知管理 (parent_id=6, perm_type=1)
  - 权限20: 通知查看 (新父节点, perm_type=1)
  - 为 SUPER_ADMIN, PRINCIPAL, HEAD_TEACHER, ASSISTANT_TEACHER 分配权限
- [x] 执行 SQL 脚本

### Task 2: 创建实体类

- [x] 创建 `com.kds.model.entity.BusiNotice.java`
  - 字段: id, title, content, noticeType, noticeLevel, targetScope, targetClassId, publisherId, publishTime, createTime, updateTime, isDeleted
  - 使用 @TableName("busi_notice") 注解
  - 使用 @TableField 映射数据库字段
  - 使用 @TableId 主键策略 AUTO

- [x] 创建 `com.kds.model.entity.BusiNoticeReceipt.java`
  - 字段: id, noticeId, studentId, parentUserId, isConfirmed, confirmTime
  - 使用 @TableName("busi_notice_receipt") 注解

### Task 3: 创建 DTO 类

- [x] 创建 `com.kds.model.dto.NoticeFormDTO.java`
  - 属性: title, content, noticeType, noticeLevel, targetScope, targetClassId
  - 添加 Jakarta Validation 注解 (@NotBlank, @NotNull)
  - 参考 LeaveFormDTO.java 的风格

- [x] 创建 `com.kds.model.dto.NoticeQueryDTO.java`
  - 属性: title, noticeType, noticeLevel, startDate, endDate, page, size
  - 参考 ClassQueryDTO.java 的风格

### Task 4: 创建 VO 类

- [x] 创建 `com.kds.model.vo.NoticeVO.java` (通知列表项)
  - 属性: id, title, noticeType, noticeTypeText, noticeLevel, noticeLevelText, targetScope, targetScopeText, targetClassId, targetClassName, publisherId, publisherName, publishTime, createTime

- [x] 创建 `com.kds.model.vo.NoticeDetailVO.java` (通知详情)
  - 属性: NoticeVO + content

- [x] 创建 `com.kds.model.vo.NoticeReceiptVO.java` (确认明细)
  - 属性: id, noticeId, studentId, studentName, parentUserId, parentName, relation, isConfirmed, confirmTime

- [x] 创建 `com.kds.model.vo.NoticeReceiptSummaryVO.java` (确认汇总)
  - 属性: total, confirmed, unconfirmed, details (List<NoticeReceiptVO>)

### Task 5: 创建 Mapper 接口

- [x] 创建 `com.kds.mapper.BusiNoticeMapper.java`
  - 继承 BaseMapper<BusiNotice>

- [x] 创建 `com.kds.mapper.BusiNoticeReceiptMapper.java`
  - 继承 BaseMapper<BusiNoticeReceipt>
  - 添加 selectConfirmedCount, selectUnconfirmedCount 方法

### Task 6: 创建 Service 层

- [x] 创建 `com.kds.service.BusiNoticeService.java` 接口
  - 方法: getManagePage(), publishNotice(), getNoticeDetail(), getNoticeReceipts()

- [x] 创建 `com.kds.service.impl.BusiNoticeServiceImpl.java` 实现
  - 使用 @Service 注解
  - 注入 LoginHelper, Mapper 等依赖
  - 实现所有接口方法
  - 遵循现有 Service 实现风格（如 BusiActivityServiceImpl）
  - 复用 LoginHelper.getClassIds(), getChildIds(), isTeacher(), isParent()
  - 实现权限校验：管理员可发全园，教师只能发本班

### Task 7: 创建 Controller 层

- [x] 创建 `com.kds.controller.busi.BusiNoticeController.java`
  - 使用 @RestController, @RequestMapping("/api/busi/notice")
  - 接口列表:
    - GET /manage/page - 管理端通知列表
    - POST - 发布通知
    - GET /{id} - 通知详情
    - GET /{id}/receipts - 确认情况
    - GET /view/page - 查看端通知列表
    - POST /{id}/confirm - 确认通知
  - 遵循现有 Controller 风格（如 BusiActivityController）

---

## 前端开发任务

### Task 8: 创建 API 封装

- [x] 创建 `src/api/notice.js`
  - 函数: getManagePage(), publishNotice(), getNoticeDetail(), getNoticeReceipts(), getViewPage(), confirmNotice()
  - 参考 src/utils/request.js 的封装方式
  - 参考 src/api/activity.js 的风格

### Task 9: 创建通知管理页面

- [x] 创建 `src/views/busi/notice/NoticeManageList.vue`
  - 布局参考 TeacherActivityList.vue
  - 功能: 通知列表展示、类型/级别筛选、分页、跳转到发布/详情页

- [x] 创建 `src/views/busi/notice/NoticeForm.vue`
  - 功能: 发布通知表单（标题、类型、级别、范围、内容）
  - 参考 TeacherActivityForm.vue 的表单风格

- [x] 创建 `src/views/busi/notice/NoticeReceiptDetail.vue`
  - 功能: 通知详情展示 + 确认情况表格
  - 显示已确认/未确认幼儿列表

### Task 10: 创建通知查看页面

- [x] 创建 `src/views/busi/notice/NoticeViewList.vue`
  - 功能: 通知列表（状态角标 + 筛选）
  - 筛选: 通知类型、通知级别、确认状态
  - 布局参考 LeaveParent.vue

- [x] 创建 `src/views/busi/notice/NoticeViewDetail.vue`
  - 功能: 通知详情展示 + 确认按钮
  - 已确认状态显示灰色按钮

### Task 11: 配置前端路由

- [x] 修改 `src/router/index.js`
  - 通知管理路由挂载在业务管理(6)下: /busi/notice/manage/*
  - 通知查看路由挂载在新父节点下: /busi/notice/view/*

---

## 任务依赖

- Task 2-7 相互独立，可并行开发
- Task 8 依赖 Task 7（API 接口定义完成后才能封装）
- Task 9-10 依赖 Task 8
- Task 11 依赖 Task 9-10

---

## 参考文件

### 后端参考
- `com.kds.controller.busi.BusiActivityController.java` - Controller 风格
- `com.kds.service.impl.BusiActivityServiceImpl.java` - Service 实现风格
- `com.kds.model.dto.LeaveFormDTO.java` - DTO 风格
- `com.kds.model.vo.ActivityVO.java` - VO 风格

### 前端参考
- `src/views/busi/activity/TeacherActivityList.vue` - 列表页风格
- `src/views/busi/activity/TeacherActivityForm.vue` - 表单页风格
- `src/api/activity.js` - API 封装风格
- `src/router/index.js` - 路由配置风格