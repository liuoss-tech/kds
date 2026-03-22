# 考勤管理模块 Spec

## Why

幼儿园需要一套完整的考勤模块，让老师能够对幼儿进行每日的签到和签退操作，并与请假系统联动自动识别请假幼儿，减轻老师工作负担，提高考勤效率。

## What Changes

### 数据库变更

**扩展 `busi_attendance_record` 表**

新增字段：

| 字段名 | 类型 | 说明 |
|:---|:---|:---|
| `sign_in_time` | datetime | 签到时间 |
| `sign_out_time` | datetime | 签退时间 |

### 新增功能

1. **考勤列表查询**：根据班级和日期查询幼儿考勤状态
2. **签到功能**：支持单个和批量签到
3. **签退功能**：支持单个和批量签退
4. **请假联动**：自动识别已审批的请假幼儿并标记
5. **缺勤标记**：截止到当天结束仍未签到的幼儿自动标记为缺勤

### 考勤状态说明

| 状态值 | 名称 | 说明 |
|:---:|:---|:---|
| 1 | 出勤 | 已签到并签退 |
| 2 | 缺勤 | 既未签到也无请假 |
| 3 | 事假 | 请假（事假） |
| 4 | 病假 | 请假（病假） |
| 5 | 其他请假 | 请假（其他） |

## Impact

- ** Affected specs **：业务管理模块新增考勤管理功能
- ** Affected code **：
  - `model/entity/BusiAttendanceRecord.java` - 考勤记录实体
  - `model/dto/AttendanceFormDTO.java` - 考勤操作DTO
  - `model/dto/AttendanceQueryDTO.java` - 考勤查询DTO
  - `model/vo/AttendanceVO.java` - 考勤展示VO
  - `mapper/BusiAttendanceRecordMapper.java` - Mapper接口
  - `service/BusiAttendanceRecordService.java` - Service接口
  - `service/impl/BusiAttendanceRecordServiceImpl.java` - Service实现
  - `controller/busi/BusiAttendanceController.java` - Controller

## ADDED Requirements

### Requirement: 考勤列表查询

系统 SHALL 提供考勤列表查询功能，支持按班级和日期筛选。

#### Scenario: 查询考勤列表
- **WHEN** 教师选择班级和日期（今天或过去）并点击查询
- **THEN** 系统返回该班级所有在读幼儿的考勤状态列表

### Requirement: 签到功能

系统 SHALL 支持单个和批量签到操作。

#### Scenario: 单个签到
- **WHEN** 教师点击某个幼儿的"签到"按钮
- **THEN** 系统记录签到时间和考勤状态为"出勤"

#### Scenario: 批量签到
- **WHEN** 教师勾选多个幼儿并点击"批量签到"
- **THEN** 系统依次为每个幼儿记录签到时间，跳过已请假的幼儿

### Requirement: 签退功能

系统 SHALL 支持单个和批量签退操作。

#### Scenario: 单个签退
- **WHEN** 教师点击某个已签到幼儿的"签退"按钮
- **THEN** 系统记录签退时间

### Requirement: 请假联动

系统 SHALL 自动识别已审批的请假幼儿并显示为请假状态。

#### Scenario: 请假幼儿识别
- **WHEN** 系统加载考勤列表时
- **THEN** 对于有已审批请假单（audit_status=1或3）的幼儿，自动标记为请假状态

### Requirement: 缺勤标记

系统 SHALL 自动将未签到且无请假的幼儿标记为缺勤。

#### Scenario: 缺勤识别
- **WHEN** 幼儿在考勤日期结束时仍未签到且无请假记录
- **THEN** 系统自动标记状态为"缺勤"

### Requirement: 日期限制

系统 SHALL 禁止对未来日期进行考勤操作。

#### Scenario: 日期限制
- **WHEN** 教师选择明天或更远的日期
- **THEN** 系统禁止选择，明天不可选

### Requirement: 数据隔离

系统 SHALL 确保教师只能操作所任班级的幼儿。

#### Scenario: 数据隔离
- **WHEN** 教师查询考勤列表
- **THEN** 系统仅返回教师所任班级的幼儿数据

## API 设计

### 获取考勤列表

```
GET /api/busi/attendance/page?classId=1&date=2026-03-18
```

响应：

```json
{
  "code": 200,
  "data": {
    "records": [
      {
        "id": 1,
        "studentId": 1,
        "studentName": "张三",
        "classId": 1,
        "className": "小一班",
        "recordDate": "2026-03-18",
        "status": 1,
        "statusText": "出勤",
        "signInTime": "2026-03-18 08:00:00",
        "signOutTime": "2026-03-18 17:00:00",
        "leaveType": null,
        "leaveApplicationId": null
      }
    ],
    "total": 1,
    "size": 10,
    "current": 1
  }
}
```

### 签到

```
POST /api/busi/attendance/sign-in
Content-Type: application/json

{
  "studentIds": [1, 2, 3],
  "classId": 1,
  "date": "2026-03-18"
}
```

### 签退

```
POST /api/busi/attendance/sign-out
Content-Type: application/json

{
  "studentIds": [1, 2, 3],
  "classId": 1,
  "date": "2026-03-18"
}
```

## 权限配置

| 权限ID | 权限名称 | 路由路径 | 父级ID |
|:---|:---|:---|:---|
| 13 | 考勤管理 | /busi/attendance | 6（业务管理） |

| 角色 | 权限 |
|:---|:---|
| 教职工（userType=1） | 可进行签到、签退操作 |
| 家长（userType=2） | 无权限 |

## 技术栈

- Spring Boot 2.7.18
- MyBatis Plus 3.5.5
- Sa-Token 1.37.0
- Java 17
