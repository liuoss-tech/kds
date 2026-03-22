# 通知管理模块开发检查清单

## 后端开发检查

### Task 2: 实体类检查
- [ ] BusiNotice.java 字段映射正确（使用 @TableField）
- [ ] BusiNoticeReceipt.java 字段映射正确
- [ ] 主键策略正确（AUTO）
- [ ] 逻辑删除注解正确（@TableLogic）

### Task 3: DTO 检查
- [ ] NoticeFormDTO.java 校验注解正确
- [ ] NoticeQueryDTO.java 分页参数正确

### Task 4: VO 检查
- [ ] NoticeVO.java 包含所有列表展示字段
- [ ] NoticeDetailVO.java 包含正文内容
- [ ] NoticeReceiptVO.java 包含确认明细字段
- [ ] NoticeReceiptSummaryVO.java 包含汇总统计

### Task 6: Service 检查
- [ ] 权限校验：管理员可发全园，教师只能发本班
- [ ] 发布通知时自动生成 busi_notice_receipt 记录
- [ ] 确认通知时更新 is_confirmed 和 confirm_time
- [ ] 确认逻辑：一个幼儿多个家长，任一确认即已确认

### Task 7: Controller 检查
- [ ] 接口路径正确
- [ ] 参数校验正确
- [ ] 权限注解正确

### 权限脚本检查
- [ ] 权限ID不冲突（19, 20）
- [ ] 父级ID正确
- [ ] 角色权限分配正确

## 前端开发检查

### Task 8: API 检查
- [ ] API 函数正确封装
- [ ] 请求路径正确
- [ ] 参数传递正确

### Task 9: 通知管理页面检查
- [ ] 列表页布局正确
- [ ] 筛选功能正确
- [ ] 发布表单布局正确
- [ ] 确认情况展示正确

### Task 10: 通知查看页面检查
- [ ] 状态角标显示正确（已确认/待确认）
- [ ] 筛选功能正确
- [ ] 确认按钮逻辑正确

### Task 11: 路由检查
- [ ] 路由路径正确
- [ ] 组件引用正确
- [ ] 菜单层级正确

## 数据隔离检查

- [ ] 教师只能看到本班幼儿的确认情况
- [ ] 家长只能看到本人子女的通知
- [ ] 管理员可以看到所有

## 字段映射检查

### 后端 -> 数据库
- [ ] noticeType -> notice_type
- [ ] noticeLevel -> notice_level
- [ ] targetScope -> target_scope
- [ ] targetClassId -> target_class_id
- [ ] publisherId -> publisher_id
- [ ] publishTime -> publish_time
- [ ] isConfirmed -> is_confirmed
- [ ] confirmTime -> confirm_time

### 前端 -> 后端 DTO
- [ ] 前端字段名与 DTO 属性名一致
- [ ] 类型正确（String, Number）

## 代码复用检查

- [ ] 复用 IService/ServiceImpl 基类
- [ ] 复用 LoginHelper 工具类
- [ ] 复用 Result 统一响应
- [ ] 复用现有 VO/DTO 风格
