# 数据隔离功能实现规范

## Why
实现幼儿园管理系统中的数据权限隔离，确保不同角色只能访问其权限范围内的数据：管理员/园长查看全园数据，老师查看分配班级的数据，家长仅查看自己孩子的数据。

## What Changes
- 新增用户上下文工具类 `LoginHelper.java`，获取当前登录用户信息
- 新增数据权限拦截器 `DataScopeInterceptor.java`，动态注入数据过滤条件
- 修改 `MybatisPlusConfig.java`，注册数据权限拦截器
- 老师通过 `busi_teacher_class` 表获取分配班级
- 家长通过 `busi_student_parent` 表获取绑定孩子

## Impact
- Affected specs: 用户角色权限系统、数据查询功能
- Affected code: 
  - `com/kds/common/context/LoginHelper.java` (新增)
  - `com/kds/config/DataScopeInterceptor.java` (新增)
  - `com/kds/config/MybatisPlusConfig.java` (修改)

## ADDED Requirements

### Requirement: 数据权限隔离
系统应根据用户角色自动过滤数据范围

#### Scenario: 管理员/园长查询
- **WHEN** 角色为 ADMIN 或 PRINCIPAL (data_scope=1) 的用户查询数据
- **THEN** 不添加任何数据过滤条件，可查看全园数据

#### Scenario: 老师查询
- **WHEN** 角色为 TEACHER/HEAD_TEACHER (data_scope=2) 的用户查询数据
- **THEN** 自动注入 WHERE class_id IN (分配的班级ID列表) 条件

#### Scenario: 家长查询
- **WHEN** 角色为 PARENT (data_scope=4) 的用户查询数据
- **THEN** 自动注入 WHERE id IN (绑定孩子ID列表) 条件

## MODIFIED Requirements

### Requirement: 家长权限限制
家长角色只能查看数据，不能进行增删改操作
- **修改**: 在 Service 层增加角色判断，增删改操作仅对老师及以上角色开放

## Implementation Notes

### 技术选型
- 使用 MyBatis Plus 3.5.5 内置的 `DataPermissionInterceptor` 机制
- 通过自定义 `IDataPermissionHandler` 实现数据过滤逻辑
- 利用 Sa-Token 1.37.0 获取当前登录用户信息

### 数据范围对应表
| data_scope | 角色 | 过滤逻辑 |
|------------|------|----------|
| 1 | ADMIN/PRINCIPAL | 无限制 |
| 2 | TEACHER/HEAD_TEACHER | WHERE class_id IN (老师班级IDs) |
| 4 | PARENT | WHERE id IN (孩子IDs) |

### 依赖版本确认
- MyBatis Plus: 3.5.5 ✓
- Spring Boot: 2.7.18 ✓
- Java: 17 ✓
