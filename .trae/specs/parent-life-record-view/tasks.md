# 家长端生活记录查看功能 - 任务清单

## 任务总览

| # | 任务 | 状态 | 依赖 |
| :--- | :--- | :---: | :---: |
| 1 | 创建后端家长端生活记录 VO 类 | 待开始 | - |
| 2 | 后端 BusiLifeRecordService 新增家长端查询方法 | 待开始 | 1 |
| 3 | 后端 BusiLifeRecordServiceImpl 实现家长端查询逻辑 | 待开始 | 2 |
| 4 | 后端 BusiLifeRecordController 新增家长端接口 | 待开始 | 3 |
| 5 | 创建前端 API 接口封装 | 待开始 | - |
| 6 | 创建前端家长端生活记录页面 | 待开始 | 5 |
| 7 | 前端路由配置 | 待开始 | 6 |
| 8 | 数据库权限配置 SQL | 待开始 | - |

---

## 详细任务

### 任务 1：创建后端家长端生活记录 VO 类

- [ ] 1.1 创建 `ParentLifeRecordVO.java`
- [ ] 1.2 包含字段：id, studentId, studentName, className, recordDate
- [ ] 1.3 包含字段：lunchIntake, lunchIntakeText, waterCount
- [ ] 1.4 包含字段：sleepStartTime, sleepDuration, sleepQuality, sleepQualityText
- [ ] 1.5 包含字段：toiletCount, toiletAbnormal
- [ ] 1.6 包含字段：morningTemp, afternoonTemp, healthSymptoms
- [ ] 1.7 包含字段：teacherRemark

**依赖**：无

---

### 任务 2：后端 BusiLifeRecordService 新增家长端查询方法

- [ ] 2.1 在 `BusiLifeRecordService.java` 新增接口方法
- [ ] 2.2 方法签名：`ParentLifeRecordVO getParentLifeRecord(Long studentId, LocalDate date)`
- [ ] 2.3 方法说明：根据幼儿ID和日期查询生活记录

**依赖**：任务 1

---

### 任务 3：后端 BusiLifeRecordServiceImpl 实现家长端查询逻辑

- [ ] 3.1 在 `BusiLifeRecordServiceImpl.java` 实现家长端查询
- [ ] 3.2 权限校验：校验当前用户是家长（user_type=2）
- [ ] 3.3 关联校验：校验该幼儿是否属于当前家长
- [ ] 3.4 数据查询：查询指定幼儿指定日期的生活记录
- [ ] 3.5 枚举转换：lunchIntake → lunchIntakeText（1-全吃光, 2-吃大半, 3-吃小半, 4-没胃口）
- [ ] 3.6 枚举转换：sleepQuality → sleepQualityText（1-深睡, 2-浅睡, 3-易醒）

**依赖**：任务 2

---

### 任务 4：后端 BusiLifeRecordController 新增家长端接口

- [ ] 4.1 在 `BusiLifeRecordController.java` 新增接口
- [ ] 4.2 接口路径：`GET /api/busi/life-record/parent/page`
- [ ] 4.3 请求参数：studentId (Long), date (String)
- [ ] 4.4 调用 Service 层方法返回数据

**依赖**：任务 3

---

### 任务 5：创建前端 API 接口封装

- [ ] 5.1 新建 `frontend/src/api/lifeRecord.js`
- [ ] 5.2 新增 `getParentLifeRecord(params)` 方法
- [ ] 5.3 复用 `getParentChildren` 从 `recipe.js` 或新建

**依赖**：无

---

### 任务 6：创建前端家长端生活记录页面

- [ ] 6.1 新建 `frontend/src/views/busi/life-record/ParentLifeRecord.vue`
- [ ] 6.2 参考 ParentRecipe.vue 实现控制栏布局
- [ ] 6.3 实现幼儿下拉选择器
- [ ] 6.4 实现日期导航器（◀ 日期 ▶ + 日历按钮 + 今日按钮）
- [ ] 6.5 实现生活记录卡片展示（一体化信息流）
- [ ] 6.6 实现分类展示：饮食、午睡、如厕、健康、备注
- [ ] 6.7 枚举值显示中文文本
- [ ] 6.8 无数据显示"--"，备注为空显示"暂无备注"
- [ ] 6.9 样式与现有页面保持一致（粉色可爱风格）

**依赖**：任务 5

---

### 任务 7：前端路由配置

- [ ] 7.1 在 `frontend/src/router/index.js` 添加路由
- [ ] 7.2 路由路径：`/busi/life-record/parent`
- [ ] 7.3 路由名称：`ParentLifeRecord`
- [ ] 7.4 组件指向：`@/views/busi/life-record/ParentLifeRecord.vue`
- [ ] 7.5 路由标题：`生活记录查看`

**依赖**：任务 6

---

### 任务 8：数据库权限配置 SQL

- [ ] 8.1 新建 `add_parent_life_record_permission.sql`
- [ ] 8.2 在 `sys_permission` 表添加菜单权限（父级为数据看板 id=20）
- [ ] 8.3 为家长角色（role_code=PARENT）分配权限

**依赖**：无

---

## 任务依赖关系图

```
任务1 ──┬── 任务2 ─── 任务3 ─── 任务4
        │                          │
        └──────────┬───────────────┘
                   │
              (后端完成)
                   │
        ┌──────────┴──────────┐
        │                      │
     任务5                   任务8
        │                      │
     任务6                      │
        │                      │
     任务7 ─────────────────────┘
        │
   (前端完成)
```

---

## 验收检查点

1. [ ] 家长登录后访问 `/busi/life-record/parent` 可查看生活记录
2. [ ] 可切换幼儿查看不同孩子的记录
3. [ ] 可切换日期查看历史记录
4. [ ] 所有字段只读，不可编辑
5. [ ] 只能查看自己孩子的记录，无法查看其他孩子
6. [ ] 界面风格与 ParentRecipe.vue 一致
7. [ ] 枚举值正确显示中文文本
8. [ ] 无数据字段正确显示占位符
