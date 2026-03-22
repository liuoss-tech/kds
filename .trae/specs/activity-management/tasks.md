# 活动管理模块开发任务

## 后端开发任务

### Task 1: 数据库添加 location 字段

- [x] 在 busi_activity 表添加 location 字段（varchar 255）
- [x] SQL: `ALTER TABLE busi_activity ADD COLUMN location VARCHAR(255) DEFAULT NULL COMMENT '活动地点' AFTER deadline_time;`

### Task 2: 创建实体类

- [x] 创建 `com.kds.model.entity.BusiActivity.java`
  - 字段: id, title, content, activityType, activityTime, deadlineTime, location, targetScope, targetClassId, publisherId, createTime, isDeleted
  - 使用 @TableName("busi_activity") 注解
  - 使用 @TableField 映射数据库字段
  - 使用 @TableId 主键策略 AUTO

- [x] 创建 `com.kds.model.entity.BusiActivityRegistration.java`
  - 字段: id, activityId, studentId, parentUserId, participantCount, remark, status, createTime, updateTime
  - 使用 @TableName("busi_activity_registration") 注解

### Task 3: 创建 DTO 类

- [x] 创建 `com.kds.model.dto.ActivityFormDTO.java`
  - 属性: title, activityType, activityTime, deadlineTime, location, content, targetScope, targetClassId
  - 添加 Jakarta Validation 注解 (@NotBlank, @NotNull)
  - 参考 LeaveFormDTO.java 的风格

### Task 4: 创建 VO 类

- [x] 创建 `com.kds.model.vo.ActivityVO.java` (活动列表项)
  - 属性: id, title, activityType, activityTypeText, activityTime, deadlineTime, location, targetClassId, targetClassName, status, statusText, registrationCount, createTime

- [x] 创建 `com.kds.model.vo.ActivityDetailVO.java` (活动详情)
  - 属性: id, title, activityType, activityTypeText, activityTime, deadlineTime, location, content, targetScope, targetClassId, targetClassName, status, statusText, publisherId, publisherName, createTime

- [x] 创建 `com.kds.model.vo.RegistrationVO.java` (报名记录)
  - 属性: id, activityId, studentId, studentName, parentUserId, parentRelation, participantCount, remark, status, createTime

### Task 5: 创建 Mapper 接口

- [x] 创建 `com.kds.mapper.BusiActivityMapper.java`
  - 继承 BaseMapper<BusiActivity>

- [x] 创建 `com.kds.mapper.BusiActivityRegistrationMapper.java`
  - 继承 BaseMapper<BusiActivityRegistration>

### Task 6: 创建 Service 层

- [x] 创建 `com.kds.service.BusiActivityService.java` 接口
  - 方法: getTeacherActivityPage(), getParentActivityPage(), publishActivity(), getActivityDetail(), getActivityRegistrations(), registerActivity(), cancelRegistration(), getAvailableStudentIds()

- [x] 创建 `com.kds.service.impl.BusiActivityServiceImpl.java` 实现
  - 使用 @Service 注解
  - 注入 LoginHelper, Mapper 等依赖
  - 实现所有接口方法
  - 遵循现有 Service 实现风格（如 BusiLifeRecordServiceImpl）
  - 复用 LoginHelper.getClassIds(), getChildIds(), isTeacher(), isParent()
  - 实现 calculateActivityStatus() 计算活动状态
  - 实现防重复报名校验

### Task 7: 创建 Controller 层

- [x] 创建 `com.kds.controller.busi.BusiActivityController.java`
  - 使用 @RestController, @RequestMapping("/api/busi/activity")
  - 接口列表:
    - GET /teacher/list - 教师端活动列表
    - GET /parent/list - 家长端活动列表
    - POST - 发布活动
    - GET /{id} - 活动详情
    - GET /{id}/registrations - 报名情况
    - POST /registration - 活动报名
    - PUT /registration/{id}/cancel - 取消报名
    - GET /{id}/available-students - 可报名幼儿列表
  - 遵循现有 Controller 风格（如 BusiLeaveApplicationController）

---

## 前端开发任务

### Task 8: 创建 API 封装

- [x] 创建 `src/api/activity.js`
  - 函数: getTeacherActivityList(), getParentActivityList(), getActivityDetail(), publishActivity(), getActivityRegistrations(), registerActivity(), cancelRegistration(), getAvailableStudents()
  - 参考 src/utils/request.js 的封装方式
  - 参考其他 api 文件（如有）的风格

### Task 9: 创建教师端页面

- [x] 创建 `src/views/busi/activity/TeacherActivityList.vue`
  - 布局参考 Attendance.vue 和 LeaveTeacher.vue
  - 功能: 活动列表展示、状态筛选、分页、跳转到发布/详情页
  - 样式: 复用现有 .page-container, .content-card, .cute-table 等类名

- [x] 创建 `src/views/busi/activity/TeacherActivityForm.vue`
  - 功能: 发布活动表单（名称、类型、地点、时间、详情、班级选择）
  - 参考 LeaveTeacher.vue 的表单风格
  - 班级下拉从 localStorage 获取（kds_class_list）

- [x] 创建 `src/views/busi/activity/TeacherActivityDetail.vue`
  - 功能: 活动详情展示 + 报名情况表格
  - 参考 LeaveTeacher.vue 的详情页风格

### Task 10: 创建家长端页面

- [x] 创建 `src/views/busi/activity/ParentActivityList.vue`
  - 功能: 活动列表（进行中/历史记录 Tab）、是否已报名标识
  - 参考 LeaveParent.vue 的列表风格

- [x] 创建 `src/views/busi/activity/ParentActivityDetail.vue`
  - 功能: 活动详情 + 报名表单（幼儿选择、参与人数、备注）
  - 已报名状态显示 + 取消报名按钮
  - 参考 LeaveParent.vue 的表单风格

### Task 11: 配置前端路由

- [x] 修改 `src/router/index.js`
  - 添加活动模块路由（教师端: /busi/activity/teacher/*, 家长端: /busi/activity/parent/*）
  - 路由路径和组件路径对应

---

## 任务依赖

- Task 2-7 相互独立，可并行开发
- Task 8 依赖 Task 7（API 接口定义完成后才能封装）
- Task 9-10 依赖 Task 8
- Task 11 依赖 Task 9-10

---

## 参考文件

### 后端参考
- `com.kds.controller.busi.BusiLifeRecordController.java` - Controller 风格
- `com.kds.service.impl.BusiLifeRecordServiceImpl.java` - Service 实现风格
- `com.kds.model.dto.LeaveFormDTO.java` - DTO 风格
- `com.kds.model.vo.LifeRecordVO.java` - VO 风格

### 前端参考
- `src/views/busi/Attendance.vue` - 列表页风格
- `src/views/busi/leave/LeaveParent.vue` - 表单和列表风格
- `src/utils/request.js` - API 封装方式
- `src/router/index.js` - 路由配置风格
