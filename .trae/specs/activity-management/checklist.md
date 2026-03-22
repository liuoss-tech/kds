# 活动管理模块开发检查清单

## 数据库

- [x] busi_activity 表已添加 location 字段

## 后端实体类

- [x] BusiActivity.java 字段与数据库 busi_activity 表一致
- [x] BusiActivityRegistration.java 字段与数据库 busi_activity_registration 表一致
- [x] 使用正确的 @TableName 和 @TableField 注解

## 后端 DTO/VO

- [x] ActivityFormDTO.java 包含所有发布活动需要的字段和校验注解
- [x] ActivityVO.java 包含活动列表展示需要的字段
- [x] ActivityDetailVO.java 包含活动详情展示需要的字段
- [x] RegistrationVO.java 包含报名记录展示需要的字段

## 后端 Mapper

- [x] BusiActivityMapper.java 继承 BaseMapper<BusiActivity>
- [x] BusiActivityRegistrationMapper.java 继承 BaseMapper<BusiActivityRegistration>

## 后端 Service

- [x] BusiActivityService.java 定义所有接口方法
- [x] BusiActivityServiceImpl.java 实现所有方法
- [x] getTeacherActivityPage() 只返回 publisher_id = 当前用户ID 的活动
- [x] getParentActivityPage() 只返回目标班级在家长孩子所在班级的活动
- [x] publishActivity() 验证 targetClassId 在 LoginHelper.getClassIds() 中
- [x] registerActivity() 验证 studentId 在家长绑定孩子中
- [x] registerActivity() 防止重复报名
- [x] cancelRegistration() 验证只能取消自己的报名
- [x] calculateActivityStatus() 正确计算活动状态

## 后端 Controller

- [x] BusiActivityController.java 定义所有 API 端点
- [x] API 路径符合 RESTful 规范
- [x] 响应格式符合 { code, message, data } 结构

## 前端 API

- [x] src/api/activity.js 封装所有后端接口
- [x] 使用与 request.js 一致的封装方式

## 前端教师端

- [x] TeacherActivityList.vue 活动列表页功能完整
- [x] TeacherActivityList.vue 支持状态筛选
- [x] TeacherActivityList.vue 跳转到发布/详情页正常
- [x] TeacherActivityForm.vue 发布表单功能完整
- [x] TeacherActivityForm.vue 班级下拉只显示所管班级
- [x] TeacherActivityForm.vue 表单校验正常
- [x] TeacherActivityDetail.vue 活动详情展示完整
- [x] TeacherActivityDetail.vue 报名情况表格显示正确

## 前端家长端

- [x] ParentActivityList.vue 进行中/历史记录 Tab 切换正常
- [x] ParentActivityList.vue 只显示本班活动
- [x] ParentActivityList.vue 显示是否已报名标识
- [x] ParentActivityDetail.vue 活动详情展示完整
- [x] ParentActivityDetail.vue 幼儿下拉不显示已报名幼儿
- [x] ParentActivityDetail.vue 报名功能正常
- [x] ParentActivityDetail.vue 已报名状态显示正确
- [x] ParentActivityDetail.vue 取消报名功能正常

## 前端路由

- [x] src/router/index.js 已添加活动模块路由
- [x] 路由路径正确对应组件

## 数据隔离验证

- [ ] 教师只能看到自己发布的活动
- [ ] 家长只能看到孩子所在班级的活动
- [ ] 教师只能向所管班级发布活动
- [ ] 家长只能为自己绑定的孩子报名

## 权限验证

- [ ] 活动发布需要教师权限
- [ ] 活动报名需要家长权限
- [ ] 查看报名情况需要教师权限