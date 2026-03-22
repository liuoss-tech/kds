# Checklist - 幼儿管理模块

## 后端开发检查点

- [x] 数据库变更正确执行，avatar 字段已添加
- [x] BusiStudent 实体类字段完整，包含所有数据字典中的字段
- [x] StudentFormDTO 包含所有表单字段，包含家长多选字段
- [x] StudentQueryDTO 包含分页和搜索条件
- [x] StudentVO 包含列表展示所需字段
- [x] StudentDetailVO 包含完整信息及家长列表
- [x] BusiStudentMapper 继承 BaseMapper，正确配置
- [x] BusiStudentParentMapper 继承 BaseMapper
- [x] BusiStudentService 包含增删改查方法声明
- [x] BusiStudentServiceImpl 实现完整逻辑，包括家长绑定
- [x] BusiStudentController 包含所有 API 接口
- [x] 使用 @Validated 进行参数校验

## 前端开发检查点

- [x] 路由配置正确，路径为 /busi/student
- [x] Student.vue 页面风格与 Class.vue 一致
- [x] 表格展示幼儿信息完整
- [x] 搜索功能正常（按姓名搜索）
- [x] 分页功能正常
- [x] 新增弹窗表单字段完整
- [x] 编辑弹窗能正确回显数据
- [x] 删除功能带确认提示
- [x] 家长下拉多选功能正常
- [x] 家长可选列表只显示家长用户
- [x] 必填字段校验正常

## 接口测试检查点

- [x] GET /api/busi/student/page 分页查询正常
- [x] GET /api/busi/student/page 带姓名搜索正常
- [x] GET /api/busi/student/{id} 获取详情正常
- [x] POST /api/busi/student/add 新增成功
- [x] POST /api/busi/student/update 编辑成功
- [x] POST /api/busi/student/delete/{id} 删除成功
- [x] GET /api/busi/student/parent-list 获取家长列表正常
- [x] GET /api/busi/student/class-list 获取班级列表正常
- [x] 接口返回格式统一为 { code, message, data }

## 文档更新检查点

- [x] 数据字典中 busi_student 表包含 avatar 字段
- [x] 后端API文档包含幼儿管理所有接口
- [x] 新增幼儿管理模块文档（15-幼儿管理模块.md）
