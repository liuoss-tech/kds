# 家长端食谱查看功能验收检查清单

## 一、后端验收

- [x] `BusiStudentService` 新增 `getParentChildren` 方法存在
- [x] `BusiStudentServiceImpl` 实现了该方法
- [x] `BusiStudentParentMapper` 包含根据家长ID查询幼儿的查询方法（复用 `selectChildIdsByParentUserId`）
- [x] `BusiRecipeController` 包含 `GET /api/busi/recipe/parent` 接口
- [x] `BusiStudentController` 包含 `GET /api/busi/student/parent-children` 接口
- [x] 家长端接口正确接收 `studentId` 和 `date` 参数
- [x] 接口返回完整的食谱数据（含幼儿姓名、班级名称）
- [x] 接口对未绑定幼儿返回错误（通过 `loginHelper.getChildIds()` 校验）
- [x] `add_parent_recipe_permission.sql` 脚本包含权限数据（id=23，父ID=20）
- [x] 脚本能为家长角色分配食谱查看权限

## 二、前端验收

- [x] `api/recipe.js` 包含 `getParentRecipe(params)` 方法
- [x] `api/recipe.js` 包含 `getParentChildren()` 方法
- [x] `views/busi/ParentRecipe.vue` 文件已创建
- [x] 页面包含幼儿下拉选择器
- [x] 幼儿下拉显示"幼儿姓名（班级名）"格式
- [x] 页面包含日期导航条（左右箭头、日历、今日按钮）
- [x] 页面包含餐次卡片展示（早餐、早点、午餐、午点、晚餐）
- [x] 餐次卡片为只读状态，无编辑功能
- [x] 空内容显示"（暂无内容）"
- [x] `router/index.js` 包含路由 `/busi/recipe/parent`

## 三、界面风格验收

- [x] 页面布局与现有 `Recipe.vue` 风格一致
- [x] 使用相同的 CSS 类名（`.page-container`、`.page-header`、`.meal-card` 等）
- [x] 使用相同的颜色变量和渐变背景
- [x] 响应式布局正常（移动端适配）

## 四、功能流程验收

- [x] 页面初始化时自动加载第一个幼儿的当天食谱
- [x] 切换幼儿后自动加载对应班级食谱
- [x] 日期导航左右箭头切换正常
- [x] 日历选择器可以跳转任意日期
- [x] 今日按钮可以快速回到当天

## 五、数据隔离验收

- [x] 家长只能查看自己绑定幼儿的食谱
- [x] 未绑定幼儿的食谱返回错误（"您没有权限查看该幼儿的食谱"）
- [x] 无法通过构造参数查看其他幼儿的食谱（后端安全校验）

## 六、代码复用验收

- [x] 复用了 Recipe.vue 的餐次卡片样式
- [x] 复用了 Recipe.vue 的日期导航组件
- [x] 复用了 Recipe.vue 的 CSS 变量和渐变背景
- [x] 复用了现有 API 封装方式
