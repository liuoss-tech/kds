# 食谱管理模块验收清单

## 一、后端验收

- [ ] BusiRecipe.java 字段与数据库字段一致
- [ ] RecipeFormDTO.java 字段验证注解正确
- [ ] RecipeVO.java 包含所有展示字段
- [ ] BusiRecipeMapper.java 继承 BaseMapper
- [ ] BusiRecipeServiceImpl 新增/更新逻辑正确
- [ ] BusiRecipeController 接口路径正确
- [ ] 获取食谱接口返回完整数据
- [ ] 保存食谱接口新增成功
- [ ] 保存食谱接口更新成功
- [ ] 权限配置新增成功

## 二、前端验收

- [ ] api/recipe.js 接口封装正确
- [ ] Recipe.vue 页面布局与设计一致
- [ ] 班级下拉能正确加载班级列表
- [ ] 日期导航条功能正常
- [ ] 餐次卡片显示/隐藏功能正常
- [ ] 保存按钮校验必填项
- [ ] 保存成功后显示提示
- [ ] 样式与项目风格一致

## 三、路由验收

- [ ] 路由路径 `/busi/recipe`
- [ ] 菜单名称 "食谱管理"
- [ ] 路由在业务管理下

## 四、接口测试验收

- [ ] 登录获取 token
- [ ] GET /api/busi/recipe?classId=1&date=2026-03-20 返回正确
- [ ] POST /api/busi/recipe 新增食谱成功
- [ ] POST /api/busi/recipe 更新食谱成功

## 五、文档验收

- [ ] 后端API.md 更新完成
- [ ] 已实现功能清单.md 更新完成
- [ ] 食谱管理模块.md 新增完成
