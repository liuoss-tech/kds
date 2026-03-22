# Tasks

## 1. 数据库变更

- [ ] Task 1.1: 扩展 busi_attendance_record 表，添加 sign_in_time 和 sign_out_time 字段
  - 使用 ALTER TABLE 语句添加字段
  - 更新数据字典文档

## 2. 后端实体类

- [ ] Task 2.1: 创建 BusiAttendanceRecord 实体类
  - 基于数据字典的 busi_attendance_record 表结构
  - 包含新增的 sign_in_time 和 sign_out_time 字段

## 3. DTO 类

- [ ] Task 3.1: 创建 AttendanceFormDTO（签到/签退请求DTO）
- [ ] Task 3.2: 创建 AttendanceQueryDTO（考勤查询DTO）
- [ ] Task 3.3: 创建 AttendanceVO（考勤展示VO）

## 4. Mapper 层

- [ ] Task 4.1: 创建 BusiAttendanceRecordMapper 接口
  - 继承 IService<BusiAttendanceRecord>

## 5. Service 层

- [ ] Task 5.1: 创建 BusiAttendanceRecordService 接口
  - 定义 getAttendancePage 方法
  - 定义 signIn 方法
  - 定义 signOut 方法

- [ ] Task 5.2: 创建 BusiAttendanceRecordServiceImpl 实现类
  - 实现分页查询（关联请假表获取请假状态）
  - 实现签到逻辑
  - 实现签退逻辑
  - 实现自动标记缺勤逻辑
  - 实现数据隔离（仅查询所任班级）

## 6. Controller 层

- [ ] Task 6.1: 创建 BusiAttendanceController
  - GET /api/busi/attendance/page - 获取考勤列表
  - POST /api/busi/attendance/sign-in - 签到
  - POST /api/busi/attendance/sign-out - 签退

## 7. 权限配置

- [ ] Task 7.1: 添加考勤模块权限数据到 sys_permission 表
  - 权限ID: 13，名称: 考勤管理，路由: /busi/attendance
  - 更新数据字典文档

## 8. 接口测试

- [ ] Task 8.1: 测试获取考勤列表接口
- [ ] Task 8.2: 测试签到接口
- [ ] Task 8.3: 测试签退接口

## Task Dependencies

- Task 2 依赖 Task 1（需要先有表结构）
- Task 3 依赖 Task 2（需要先有实体类）
- Task 4 依赖 Task 2（需要先有实体类）
- Task 5 依赖 Task 3、Task 4（需要DTO和Mapper）
- Task 6 依赖 Task 5（需要Service）
- Task 7 可与 Task 2-6 并行（在 Controller 完成后添加权限）
- Task 8 依赖 Task 6（需要 Controller 完成后才能测试）
