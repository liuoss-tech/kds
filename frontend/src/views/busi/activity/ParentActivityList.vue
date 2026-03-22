<template>
  <div class="page-container">
    <div class="page-header">
      <div class="header-decoration">
        <span class="deco-flower">✿</span>
      </div>
      <h2 class="page-title">活动列表</h2>
      <p class="page-subtitle">浏览并报名幼儿园活动</p>
    </div>

    <el-card shadow="never" class="content-card">
      <el-tabs v-model="activeTab" class="cute-tabs" @tab-change="handleTabChange">
        <el-tab-pane label="🔥 进行中" name="ongoing">
          <template #label>
            <span class="tab-label">🔥 进行中</span>
          </template>
        </el-tab-pane>
        <el-tab-pane label="📋 历史记录" name="history">
          <template #label>
            <span class="tab-label">📋 历史记录</span>
          </template>
        </el-tab-pane>
      </el-tabs>

      <div class="activity-list" v-loading="listLoading">
        <div
          v-for="item in list"
          :key="item.id"
          class="activity-card"
          @click="handleDetail(item)"
        >
          <div class="activity-card-header">
            <span class="activity-type-icon">{{ getTypeIcon(item.activityType) }}</span>
            <span class="activity-type-text">{{ item.activityTypeText }}</span>
            <el-tag
              v-if="item.isRegistered"
              type="success"
              class="registered-tag"
              effect="plain"
            >
              ✓ 已报名
            </el-tag>
          </div>
          <div class="activity-card-body">
            <h4 class="activity-name">{{ item.title }}</h4>
            <div class="activity-info-row">
              <span class="info-item">
                <span class="info-icon">📍</span>
                {{ item.location || '待定' }}
              </span>
            </div>
            <div class="activity-info-row">
              <span class="info-item">
                <span class="info-icon">🕐</span>
                {{ item.activityTime }}
              </span>
            </div>
            <div class="activity-info-row">
              <span class="info-item">
                <span class="info-icon">⏰</span>
                报名截止: {{ item.deadlineTime }}
              </span>
            </div>
          </div>
          <div class="activity-card-footer">
            <span class="registration-count">
              👨‍👩‍👧 {{ item.registrationCount || 0 }} 人已报名
            </span>
            <span class="view-detail">点击查看详情 →</span>
          </div>
        </div>

        <div v-if="list.length === 0 && !listLoading" class="empty-state">
          <span class="empty-icon">🎪</span>
          <p class="empty-text">{{ activeTab === 'ongoing' ? '暂无进行中的活动' : '暂无历史活动' }}</p>
        </div>
      </div>

      <div class="pagination-container" v-if="total > 0">
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
import { getParentActivityList } from '@/api/activity'
import { ElMessage } from 'element-plus'

export default {
  name: 'ParentActivityList',
  data() {
    return {
      activeTab: 'ongoing',
      list: [],
      total: 0,
      listLoading: true,
      listQuery: {
        pageNum: 1,
        pageSize: 10,
        status: 1
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
        const res = await getParentActivityList(params)
        this.list = res.data.records || []
        this.total = res.data.total || 0
      } catch (error) {
        console.error('获取活动列表失败:', error)
      } finally {
        this.listLoading = false
      }
    },
    handleTabChange(tabName) {
      this.listQuery.pageNum = 1
      if (tabName === 'ongoing') {
        this.listQuery.status = 1
      } else {
        this.listQuery.status = 4
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
    getTypeIcon(activityType) {
      const map = {
        1: '🏃',
        2: '🎨',
        3: '👨‍👩‍👧',
        4: '🌳',
        5: '🎪'
      }
      return map[activityType] || '🎪'
    },
    handleDetail(item) {
      this.$router.push(`/busi/activity/parent/detail/${item.id}`)
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
.cute-tabs :deep(.el-tabs__header) { margin-bottom: 20px; }
.cute-tabs :deep(.el-tabs__nav-wrap::after) { background: #FFE8E8; height: 2px; }
.cute-tabs :deep(.el-tabs__item) { font-size: 15px; color: #9B8E8E; }
.cute-tabs :deep(.el-tabs__item.is-active) { color: #FF6B6B; font-weight: 600; }
.cute-tabs :deep(.el-tabs__active-bar) { background: linear-gradient(135deg, #FFB5B5 0%, #FF8E8E 100%); height: 3px; border-radius: 2px; }
.tab-label { font-family: 'Noto Sans SC', sans-serif; }
.activity-list { display: flex; flex-direction: column; gap: 16px; }
.activity-card { background: linear-gradient(135deg, #FFFAF8 0%, #FFF5F5 100%); border: 1px solid #FFE8E8; border-radius: 16px; padding: 20px; cursor: pointer; transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1); }
.activity-card:hover { transform: translateY(-4px); box-shadow: 0 8px 25px rgba(255, 107, 107, 0.15); border-color: #FFB5B5; }
.activity-card-header { display: flex; align-items: center; gap: 8px; margin-bottom: 12px; }
.activity-type-icon { font-size: 20px; }
.activity-type-text { font-size: 13px; color: #9B8E8E; flex: 1; }
.registered-tag { border-radius: 20px; font-weight: 500; }
.activity-card-body { margin-bottom: 12px; }
.activity-name { font-size: 18px; color: #5D4E4E; margin: 0 0 12px; font-family: 'ZCOOL KuaiLe', sans-serif; font-weight: 600; }
.activity-info-row { display: flex; align-items: center; margin-bottom: 6px; }
.info-item { display: flex; align-items: center; gap: 6px; font-size: 13px; color: #7D6E6E; }
.info-icon { font-size: 14px; }
.activity-card-footer { display: flex; justify-content: space-between; align-items: center; padding-top: 12px; border-top: 1px dashed #FFE8E8; }
.registration-count { font-size: 13px; color: #9B8E8E; }
.view-detail { font-size: 13px; color: #FF6B6B; font-weight: 500; }
.empty-state { text-align: center; padding: 60px 20px; }
.empty-icon { font-size: 64px; display: block; margin-bottom: 16px; }
.empty-text { font-size: 15px; color: #9B8E8E; margin: 0; }
.pagination-container { padding: 20px; display: flex; justify-content: flex-end; }
.cute-pagination :deep(.el-pager li.is-active) { background: linear-gradient(135deg, #FFB5B5 0%, #FF8E8E 100%) !important; }

@media (max-width: 768px) {
  .page-title { font-size: 22px; }
}
</style>