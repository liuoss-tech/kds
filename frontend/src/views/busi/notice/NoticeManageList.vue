<template>
  <div class="page-container">
    <div class="page-header">
      <div class="header-decoration">
        <span class="deco-flower">✿</span>
      </div>
      <h2 class="page-title">通知管理</h2>
      <p class="page-subtitle">管理班级通知信息</p>
    </div>

    <el-card shadow="never" class="content-card">
      <div class="toolbar">
        <el-button type="primary" class="cute-btn primary add-btn" @click="handleAdd">
          <span>➕</span> 发布通知
        </el-button>
      </div>

      <el-form :inline="true" :model="listQuery" class="search-form">
        <el-form-item label="通知类型" class="search-item">
          <el-select v-model="listQuery.noticeType" placeholder="请选择类型" clearable class="cute-select type-select">
            <el-option label="全部类型" :value="undefined" />
            <el-option label="📋 日常通知" :value="1" />
            <el-option label="💰 缴费通知" :value="2" />
            <el-option label="🏖️ 放假通知" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="通知级别" class="search-item">
          <el-select v-model="listQuery.noticeLevel" placeholder="请选择级别" clearable class="cute-select level-select">
            <el-option label="全部级别" :value="undefined" />
            <el-option label="普通" :value="1" />
            <el-option label="置顶" :value="2" />
            <el-option label="强提醒" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="日期范围" class="search-item">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            class="cute-picker date-picker"
          />
        </el-form-item>
        <el-form-item class="search-actions">
          <el-button type="primary" class="cute-btn primary" @click="handleFilter">
            <span>✨</span> 查询
          </el-button>
          <el-button class="cute-btn" @click="resetQuery">
            <span>🔄</span> 重置
          </el-button>
        </el-form-item>
      </el-form>

      <el-table
        v-loading="listLoading"
        :data="list"
        border
        stripe
        class="cute-table"
        row-key="id"
      >
        <el-table-column type="index" label="序号" width="80" align="center">
          <template #default="{ $index }">
            <span class="row-number">{{ $index + 1 }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="通知标题" min-width="200" align="center">
          <template #default="{ row }">
            <div class="notice-info">
              <span v-if="row.noticeLevel === 2" class="level-icon">🔺</span>
              <span v-else-if="row.noticeLevel === 3" class="level-icon">🔴</span>
              <span class="notice-title">{{ row.title }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="noticeTypeText" label="类型" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="getTypeTag(row.noticeType)" class="cute-tag" effect="plain">
              {{ row.noticeTypeText }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="noticeLevelText" label="级别" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getLevelTag(row.noticeLevel)" class="cute-tag" effect="plain">
              {{ row.noticeLevelText }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="targetScopeText" label="范围" width="120" align="center">
          <template #default="{ row }">
            <span class="scope-text">{{ row.targetScopeText }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="publishTime" label="发布时间" width="180" align="center">
          <template #default="{ row }">
            <span class="time-text">{{ row.publishTime }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" align="center" fixed="right">
          <template #default="scope">
            <el-button size="small" type="primary" link class="action-btn" @click="handleDetail(scope.row)">
              <span>👁️</span> 查看详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="listQuery.pageNum"
          :page-sizes="[10, 20, 50, 100]"
          :page-size="listQuery.pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          background
          class="cute-pagination"
        />
      </div>
    </el-card>
  </div>
</template>

<script>
import { getManagePage } from '@/api/notice'
import { ElMessage } from 'element-plus'

export default {
  name: 'NoticeManageList',
  data() {
    return {
      list: [],
      total: 0,
      listLoading: true,
      dateRange: null,
      listQuery: {
        pageNum: 1,
        pageSize: 10,
        noticeType: undefined,
        noticeLevel: undefined,
        startDate: undefined,
        endDate: undefined
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    async getList() {
      this.listLoading = true
      try {
        const params = { ...this.listQuery }
        if (params.noticeType === '') params.noticeType = undefined
        if (params.noticeLevel === '') params.noticeLevel = undefined
        if (this.dateRange) {
          params.startDate = this.dateRange[0]
          params.endDate = this.dateRange[1]
        }
        const res = await getManagePage(params)
        this.list = res.data.records || []
        this.total = res.data.total || 0
      } catch (error) {
        console.error('获取通知列表失败:', error)
      } finally {
        this.listLoading = false
      }
    },
    handleFilter() {
      this.listQuery.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.dateRange = null
      this.listQuery = {
        pageNum: 1,
        pageSize: 10,
        noticeType: undefined,
        noticeLevel: undefined,
        startDate: undefined,
        endDate: undefined
      }
      this.getList()
    },
    handleSizeChange(val) {
      this.listQuery.pageSize = val
      this.getList()
    },
    handleCurrentChange(val) {
      this.listQuery.pageNum = val
      this.getList()
    },
    getTypeTag(noticeType) {
      const map = { 1: '', 2: 'warning', 3: 'success' }
      return map[noticeType] || ''
    },
    getLevelTag(noticeLevel) {
      const map = { 1: 'info', 2: 'warning', 3: 'danger' }
      return map[noticeLevel] || 'info'
    },
    handleAdd() {
      this.$router.push('/busi/notice/manage/form')
    },
    handleDetail(row) {
      this.$router.push(`/busi/notice/manage/detail/${row.id}`)
    }
  }
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=ZCOOL+KuaiLe&family=Noto+Sans+SC:wght@400;500;700&display=swap');

.page-container { padding: 0; }
.page-header { text-align: center; padding: 20px 0 24px; position: relative; }
.header-decoration { position: absolute; top: 10px; left: 50%; transform: translateX(-50%); }
.deco-flower { font-size: 24px; opacity: 0.3; animation: flowerFloat 3s ease-in-out infinite; }
@keyframes flowerFloat { 0%, 100% { transform: translateY(0) rotate(0deg); } 50% { transform: translateY(-5px) rotate(10deg); } }
.page-title { font-family: 'ZCOOL KuaiLe', 'Noto Sans SC', sans-serif; font-size: 28px; color: #5D4E4E; margin: 0 0 8px; }
.page-subtitle { font-size: 14px; color: #9B8E8E; margin: 0; }
.content-card { border-radius: 20px; border: none; box-shadow: 0 8px 30px rgba(255, 182, 193, 0.15); }
.search-form { padding: 20px; background: linear-gradient(135deg, #FFF9F5 0%, #FFF0F0 100%); border-radius: 16px; margin-bottom: 20px; }
.search-item { margin-bottom: 0; }
.search-actions { margin-left: auto; }
.type-select, .level-select { min-width: 140px; }
.date-picker { width: 260px; }
.cute-select :deep(.el-select__wrapper) { border-radius: 12px; box-shadow: 0 0 0 1px #F0E0E0 inset; background: #fff; }
.cute-picker { width: 100%; }
.cute-picker :deep(.el-input__wrapper) { border-radius: 12px; box-shadow: 0 0 0 1px #F0E0E0 inset; background: #fff; }
.cute-btn { border-radius: 12px; padding: 10px 20px; font-family: 'Noto Sans SC', sans-serif; font-weight: 500; border: none; transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1); display: inline-flex; align-items: center; gap: 6px; background: linear-gradient(135deg, #FFF0F0 0%, #FFE8E8 100%); color: #5D4E4E; }
.cute-btn:hover { transform: translateY(-2px); box-shadow: 0 6px 20px rgba(255, 107, 107, 0.2); }
.cute-btn.primary { background: linear-gradient(135deg, #FFB5B5 0%, #FF8E8E 100%); color: #fff; }
.cute-btn.primary:hover { background: linear-gradient(135deg, #FF6B6B 0%, #FF8E53 100%); box-shadow: 0 8px 25px rgba(255, 107, 107, 0.35); }
.toolbar { padding: 0 20px 20px; display: flex; gap: 12px; }
.add-btn { padding: 12px 24px; font-size: 15px; }
.cute-table { border-radius: 16px; overflow: hidden; }
.cute-table :deep(.el-table__header-wrapper th) { background: linear-gradient(135deg, #FFF0F0 0%, #FFE8E8 100%) !important; color: #5D4E4E; font-weight: 600; }
.cute-table :deep(.el-table__row) { transition: all 0.3s; }
.cute-table :deep(.el-table__row:hover) { background: rgba(255, 230, 230, 0.5) !important; }
.row-number { display: inline-flex; align-items: center; justify-content: center; width: 28px; height: 28px; background: linear-gradient(135deg, #FFF0F0 0%, #FFE8E8 100%); border-radius: 50%; font-size: 12px; font-weight: 600; color: #FF6B6B; }
.notice-info { display: flex; align-items: center; justify-content: center; gap: 8px; }
.level-icon { font-size: 16px; }
.notice-title { font-weight: 600; color: #5D4E4E; }
.cute-tag { border-radius: 20px; font-weight: 500; }
.scope-text, .time-text { color: #5D4E4E; font-size: 13px; }
.action-btn { padding: 6px 10px; border-radius: 8px; font-size: 13px; transition: all 0.3s; }
.pagination-container { padding: 20px; display: flex; justify-content: flex-end; }
.cute-pagination :deep(.el-pager li.is-active) { background: linear-gradient(135deg, #FFB5B5 0%, #FF8E8E 100%) !important; }

@media (max-width: 768px) {
  .page-title { font-size: 22px; }
  .search-form :deep(.el-form-item) { display: block; margin-bottom: 12px; }
  .search-actions { margin-left: 0; display: flex; gap: 8px; }
}
</style>