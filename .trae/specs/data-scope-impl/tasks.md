# Tasks - 数据隔离功能实现

## 实现任务清单

### Task 1: 创建用户上下文工具类 LoginHelper
- [x] 1.1 创建 `com/kds/common/context/LoginHelper.java`
- [x] 1.2 实现获取当前登录用户ID的方法
- [x] 1.3 实现获取用户角色的方法
- [x] 1.4 实现获取用户 data_scope 的方法
- [x] 1.5 实现获取老师班级IDs的方法（查询 busi_teacher_class 表）
- [x] 1.6 实现获取家长绑定孩子IDs的方法（查询 busi_student_parent 表）

### Task 2: 创建数据权限处理器 CustomDataPermissionHandler
- [x] 2.1 创建 `com/kds/config/CustomDataPermissionHandler.java`
- [x] 2.2 实现 IDataPermissionHandler 接口
- [x] 2.3 实现根据 data_scope 动态构建过滤条件的逻辑

### Task 3: 修改 MybatisPlusConfig 注册拦截器
- [x] 3.1 修改 `com/kds/config/MybatisPlusConfig.java`
- [x] 3.2 添加 DataPermissionInterceptor Bean
- [x] 3.3 注册自定义数据权限处理器

### Task 4: 配置需要数据隔离的 Mapper
- [x] 4.1 在幼儿 Mapper 上添加 @TableLogic 注解（如需要）
- [x] 4.2 确认业务表的字段映射正确

### Task 5: 验证数据隔离功能
- [ ] 5.1 管理员账号登录测试全园数据可见
- [ ] 5.2 老师账号登录测试仅见分配班级数据
- [ ] 5.3 家长账号登录测试仅见自己孩子数据

# Task Dependencies
- Task 1 是 Task 2 的前置依赖
- Task 2 是 Task 3 的前置依赖
- Task 3 是 Task 4 的前置依赖
- Task 4 是 Task 5 的前置依赖
