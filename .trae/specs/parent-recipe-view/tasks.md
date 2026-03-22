# 家长端食谱查看功能开发任务

## 一、后端开发任务

### 1.1 扩展幼儿服务
- [x] 在 `BusiStudentService.java` 新增 `getParentChildren(Long parentUserId)` 方法
- [x] 在 `BusiStudentServiceImpl.java` 实现该方法
- [x] 在 `BusiStudentParentMapper.java` 新增根据家长ID查询幼儿列表的查询方法（复用现有方法）

### 1.2 扩展食谱控制器
- [x] 在 `BusiRecipeController.java` 新增 `GET /api/busi/recipe/parent` 家长端获取食谱接口
- [x] 实现根据 studentId 获取食谱的逻辑
- [x] 添加安全校验：校验 studentId 是否属于当前家长
- [x] 在 `BusiStudentController.java` 新增 `GET /api/busi/student/parent-children` 接口

### 1.3 数据库脚本
- [x] 创建 `add_parent_recipe_permission.sql` 脚本，添加食谱查看权限(id=23)

---

## 二、前端开发任务

### 2.1 API 扩展
- [x] 在 `api/recipe.js` 新增 `getParentRecipe(params)` 方法
- [x] 新增 `getParentChildren()` 方法获取幼儿列表

### 2.2 页面组件
- [x] 创建 `views/busi/ParentRecipe.vue` 家长食谱查看页面
  - [x] 幼儿下拉选择组件
  - [x] 日期导航条（复用 Recipe.vue 样式）
  - [x] 只读餐次卡片展示
  - [x] 空内容显示"（暂无内容）"

### 2.3 路由配置
- [x] 在 `router/index.js` 中添加家长食谱查看路由 `/busi/recipe/parent`

---

## 三、任务依赖

- [x] 后端幼儿列表查询完成后，前端才能获取幼儿下拉数据
- [x] 后端食谱接口完成后，前端才能加载食谱数据
- [x] API 封装完成后，前端页面才能调用接口

---

## 四、并行任务

以下任务可并行执行：
- 后端 1.1（幼儿服务扩展）
- 后端 1.3（权限脚本）
- 前端 2.1（API 扩展）
- 前端 2.2（页面组件创建）

---

## 五、实现文件清单

### 后端
- `BusiStudentService.java` - 新增 getParentChildren 方法
- `BusiStudentServiceImpl.java` - 实现 getParentChildren 方法
- `ChildInfoVO.java` - 新增 VO 类
- `BusiRecipeService.java` - 新增 getParentRecipe 方法
- `BusiRecipeServiceImpl.java` - 实现 getParentRecipe 方法
- `BusiRecipeController.java` - 新增 /parent 接口
- `BusiStudentController.java` - 新增 /parent-children 接口
- `add_parent_recipe_permission.sql` - 新增权限脚本

### 前端
- `api/recipe.js` - 新增两个 API 方法
- `ParentRecipe.vue` - 新增家长食谱查看页面
- `router/index.js` - 添加路由配置
