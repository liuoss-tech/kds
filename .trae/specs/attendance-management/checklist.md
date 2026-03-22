# Checklist

## 数据库变更

- [ ] busi_attendance_record 表已添加 sign_in_time 字段
- [ ] busi_attendance_record 表已添加 sign_out_time 字段
- [ ] 数据字典文档已更新

## 后端实体类

- [ ] BusiAttendanceRecord.java 已创建
- [ ] 字段与数据库表结构一致
- [ ] 包含 Lombok @Data 注解
- [ ] 包含 MyBatis Plus 注解

## DTO 类

- [ ] AttendanceFormDTO.java 已创建
- [ ] AttendanceQueryDTO.java 已创建
- [ ] AttendanceVO.java 已创建
- [ ] 字段验证注解已添加

## Mapper 层

- [ ] BusiAttendanceRecordMapper.java 已创建
- [ ] 继承 IService<BusiAttendanceRecord>

## Service 层

- [ ] BusiAttendanceRecordService.java 接口已创建
- [ ] BusiAttendanceRecordServiceImpl.java 实现类已创建
- [ ] getAttendancePage 方法正确实现（关联请假表）
- [ ] signIn 方法正确实现（支持批量）
- [ ] signOut 方法正确实现（支持批量）
- [ ] 自动标记缺勤逻辑正确实现
- [ ] 数据隔离逻辑正确实现（按所任班级过滤）

## Controller 层

- [ ] BusiAttendanceController.java 已创建
- [ ] GET /api/busi/attendance/page 接口已实现
- [ ] POST /api/busi/attendance/sign-in 接口已实现
- [ ] POST /api/busi/attendance/sign-out 接口已实现

## 权限配置

- [ ] sys_permission 表已添加考勤管理权限数据
- [ ] 数据字典文档中的权限配置已更新

## 接口测试

- [ ] 获取考勤列表接口测试通过
- [ ] 签到接口测试通过
- [ ] 签退接口测试通过
- [ ] 日期限制（不能选明天）已生效
- [ ] 批量操作跳过请假幼儿已生效
