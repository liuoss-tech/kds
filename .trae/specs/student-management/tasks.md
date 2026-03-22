# Tasks - 幼儿管理模块

## 后端开发任务

- [ ] Task 1: 数据库变更 - 在 busi_student 表添加 avatar 字段
  - [ ] SubTask 1.1: 编写 ALTER TABLE 语句添加 avatar 字段

- [ ] Task 2: 创建幼儿实体类 BusiStudent
  - [ ] SubTask 2.1: 在 demo/src/main/java/com/kds/model/entity/ 下创建 BusiStudent.java

- [ ] Task 3: 创建幼儿 DTO/VO 类
  - [ ] SubTask 3.1: 创建 StudentFormDTO.java（新增/编辑表单）
  - [ ] SubTask 3.2: 创建 StudentQueryDTO.java（查询条件）
  - [ ] SubTask 3.3: 创建 StudentVO.java（列表展示）
  - [ ] SubTask 3.4: 创建 StudentDetailVO.java（详情，含家长信息）

- [ ] Task 4: 创建幼儿 Mapper
  - [ ] SubTask 4.1: 创建 BusiStudentMapper.java
  - [ ] SubTask 4.2: 创建 BusiStudentParentMapper.java

- [ ] Task 5: 创建幼儿 Service
  - [ ] SubTask 5.1: 创建 BusiStudentService.java 接口
  - [ ] SubTask 5.2: 创建 BusiStudentServiceImpl.java 实现

- [ ] Task 6: 创建幼儿 Controller
  - [ ] SubTask 6.1: 创建 BusiStudentController.java

- [ ] Task 7: 配置权限和数据隔离
  - [ ] SubTask 7.1: 添加幼儿管理权限标识到数据库

## 前端开发任务

- [ ] Task 8: 添加路由配置
  - [ ] SubTask 8.1: 在 frontend/src/router/index.js 添加 /busi/student 路由

- [ ] Task 9: 创建幼儿管理页面
  - [ ] SubTask 9.1: 创建 frontend/src/views/busi/Student.vue 页面
  - [ ] SubTask 9.2: 实现列表展示、搜索、分页
  - [ ] SubTask 9.3: 实现新增/编辑/删除功能
  - [ ] SubTask 9.4: 实现家长多选功能

- [ ] Task 10: 添加菜单配置（如需要）
  - [ ] SubTask 10.1: 在侧边栏添加幼儿管理菜单入口

## 测试任务

- [ ] Task 11: 接口测试
  - [ ] SubTask 11.1: 测试分页查询接口
  - [ ] SubTask 11.2: 测试新增幼儿接口
  - [ ] SubTask 11.3: 测试编辑幼儿接口
  - [ ] SubTask 11.4: 测试删除幼儿接口
  - [ ] SubTask 11.5: 测试获取家长列表接口
  - [ ] SubTask 11.6: 测试权限隔离（不同角色查看不同范围数据）

## 文档更新任务

- [ ] Task 12: 更新文档
  - [ ] SubTask 12.1: 更新数据字典文档
  - [ ] SubTask 12.2: 更新后端API文档
  - [ ] SubTask 12.3: 新增幼儿管理模块文档

---

## Task Dependencies

- Task 2 依赖 Task 1（数据库字段）
- Task 3 依赖 Task 2（实体类）
- Task 4 依赖 Task 3（DTO/VO）
- Task 5 依赖 Task 4（Mapper）
- Task 6 依赖 Task 5（Service）
- Task 9 依赖 Task 6（后端接口）
- Task 11 依赖 Task 6 和 Task 9
- Task 12 依赖所有任务

---

## 并行执行建议

以下任务可并行执行：
- Task 1, Task 2（数据库和实体类可同时进行）
- Task 3 中的 SubTask 可并行
- Task 8 和 Task 9（路由和页面可同时规划）
