# 食谱管理模块开发任务

## 一、后端开发任务

### 1.1 实体类
- [ ] 创建 `BusiRecipe.java` 实体类，字段与数据库 busi_recipe 表对应

### 1.2 DTO/VO
- [ ] 创建 `RecipeFormDTO.java` 表单提交DTO
- [ ] 创建 `RecipeVO.java` 响应视图对象

### 1.3 Mapper
- [ ] 创建 `BusiRecipeMapper.java` Mapper接口

### 1.4 Service
- [ ] 创建 `BusiRecipeService.java` 服务接口
- [ ] 创建 `BusiRecipeServiceImpl.java` 服务实现（含新增/更新逻辑）

### 1.5 Controller
- [ ] 创建 `BusiRecipeController.java` 控制器
- [ ] GET `/api/busi/recipe` - 获取食谱详情
- [ ] POST `/api/busi/recipe` - 保存食谱

### 1.6 权限配置
- [ ] 在 sys_permission 表中新增食谱管理权限（id=14，父ID=6）
- [ ] 创建 `add_recipe_permission.sql` 脚本

### 1.7 后端测试
- [ ] 使用 PowerShell 测试获取食谱接口
- [ ] 使用 PowerShell 测试保存食谱接口（新增）
- [ ] 使用 PowerShell 测试保存食谱接口（更新）

---

## 二、前端开发任务

### 2.1 API封装
- [ ] 创建 `api/recipe.js` API接口封装

### 2.2 页面组件
- [ ] 创建 `Recipe.vue` 食谱管理主页面
  - 班级下拉选择
  - 日期导航条（左右箭头 + 日历选择 + 今日按钮）
  - 餐次卡片（早餐、早点、午餐、午点、晚餐）
  - 保存按钮

### 2.3 路由配置
- [ ] 在 `router/index.js` 中添加食谱管理路由

---

## 三、文档更新任务

### 3.1 API文档
- [ ] 更新 `docs/后端API.md` 添加食谱管理接口

### 3.2 功能清单
- [ ] 更新 `docs/模块文档/11-已实现功能清单.md`

### 3.3 新增模块文档
- [ ] 创建 `docs/模块文档/20-食谱管理模块.md`

---

## 四、任务依赖

- [ ] 后端实体类、DTO/VO、Mapper、Service 完成后，才能开发 Controller
- [ ] Controller 完成后，才能进行后端测试
- [ ] API 封装完成后，前端页面才能调用接口
- [ ] 前后端都完成后，更新文档
