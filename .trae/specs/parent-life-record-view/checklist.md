# 家长端生活记录查看功能 - 检查清单

## 后端检查

### VO 类创建
- [ ] ParentLifeRecordVO.java 已创建
- [ ] 包含所有必要字段（id, studentId, studentName, className, recordDate）
- [ ] 包含饮食相关字段（lunchIntake, lunchIntakeText, waterCount）
- [ ] 包含午睡相关字段（sleepStartTime, sleepDuration, sleepQuality, sleepQualityText）
- [ ] 包含如厕相关字段（toiletCount, toiletAbnormal）
- [ ] 包含健康监测字段（morningTemp, afternoonTemp, healthSymptoms）
- [ ] 包含备注字段（teacherRemark）
- [ ] 已添加 @Data 注解

### Service 层
- [ ] BusiLifeRecordService.java 新增 getParentLifeRecord 方法
- [ ] BusiLifeRecordServiceImpl.java 实现该方法
- [ ] 权限校验：判断 user_type=2（家长）
- [ ] 关联校验：校验幼儿是否属于当前家长
- [ ] 枚举转换逻辑正确（lunchIntake → lunchIntakeText）
- [ ] 枚举转换逻辑正确（sleepQuality → sleepQualityText）

### Controller 层
- [ ] 新增接口 GET /api/busi/life-record/parent/page
- [ ] 请求参数正确（studentId, date）
- [ ] 返回 Result<ParentLifeRecordVO>
- [ ] 异常处理正确

## 前端检查

### API 封装
- [ ] frontend/src/api/lifeRecord.js 已创建
- [ ] getParentLifeRecord 方法已实现
- [ ] 接口路径正确

### 页面组件
- [ ] ParentLifeRecord.vue 已创建
- [ ] 页面路径正确（views/busi/life-record/ParentLifeRecord.vue）
- [ ] 幼儿下拉选择器已实现
- [ ] 日期导航器已实现（◀ ▶ + 日历 + 今日）
- [ ] 生活记录卡片已实现
- [ ] 分类展示正确（饮食、午睡、如厕、健康、备注）
- [ ] 枚举值显示中文文本
- [ ] 无数据占位符正确（"--", "暂无备注"）

### 样式
- [ ] 粉色可爱风格与现有页面一致
- [ ] 控制栏布局与 ParentRecipe.vue 类似
- [ ] 卡片样式与整体风格统一

### 路由配置
- [ ] router/index.js 添加路由
- [ ] 路由路径：/busi/life-record/parent
- [ ] 路由名称：ParentLifeRecord
- [ ] meta.title 正确设置

## 数据库检查

### 权限配置
- [ ] add_parent_life_record_permission.sql 已创建
- [ ] sys_permission 表新增记录正确
- [ ] 父级 ID 正确（数据看板 id=20）
- [ ] 为家长角色分配了权限

## 功能验证

### 功能测试
- [ ] 家长可正常访问页面
- [ ] 页面加载时自动获取幼儿列表并选中第一个
- [ ] 切换幼儿后正确加载对应记录
- [ ] 日期导航器◀ ▶ 按钮正确切换日期
- [ ] 日历按钮可打开日期选择器
- [ ] 今日按钮可快速回到当天
- [ ] 记录数据正确显示
- [ ] 枚举值正确显示中文

### 权限验证
- [ ] 家长只能看自己孩子的记录
- [ ] 尝试查看其他幼儿记录返回错误
- [ ] 非家长用户无法访问该页面

### 边界情况
- [ ] 无记录时显示正确（各字段显示"--"）
- [ ] 备注为空时显示"暂无备注"
- [ ] 多幼儿情况下切换正常
- [ ] 日期切换后数据正确刷新
