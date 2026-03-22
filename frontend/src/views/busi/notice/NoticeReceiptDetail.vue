<template>
  <div class="page-container">
    <div class="page-header">
      <div class="header-decoration">
        <span class="deco-flower">✿</span>
      </div>
      <h2 class="page-title">通知详情</h2>
      <p class="page-subtitle">查看通知内容及确认情况</p>
    </div>

    <el-card shadow="never" class="content-card" v-loading="detailLoading">
      <div class="notice-header" v-if="noticeDetail">
        <div class="notice-title-row">
          <span v-if="noticeDetail.noticeLevel === 2" class="level-icon">🔺</span>
          <span v-else-if="noticeDetail.noticeLevel === 3" class="level-icon">🔴</span>
          <h3 class="notice-title">{{ noticeDetail.title }}</h3>
        </div>
        <div class="notice-meta">
          <el-tag :type="getTypeTag(noticeDetail.noticeType)" class="cute-tag" effect="plain">
            {{ noticeDetail.noticeTypeText }}
          </el-tag>
          <el-tag :type="getLevelTag(noticeDetail.noticeLevel)" class="cute-tag" effect="plain">
            {{ noticeDetail.noticeLevelText }}
          </el-tag>
          <span class="meta-text">{{ noticeDetail.targetScopeText }}</span>
          <span class="meta-separator">|</span>
          <span class="meta-text">发布于 {{ noticeDetail.publishTime }}</span>
        </div>
      </div>

      <el-divider />

      <div class="notice-content" v-if="noticeDetail">
        <h4 class="content-label">📝 通知正文：</h4>
        <div class="content-text">{{ noticeDetail.content }}</div>
      </div>

      <el-divider />

      <div class="receipt-section" v-if="receiptData">
        <div class="receipt-header">
          <h4 class="section-title">📊 确认情况统计</h4>
        </div>
        <div class="receipt-stats">
          <div class="stat-item total">
            <span class="stat-icon">👥</span>
            <span class="stat-label">全班人数</span>
            <span class="stat-value">{{ receiptData.total }}人</span>
          </div>
          <div class="stat-item confirmed">
            <span class="stat-icon">✅</span>
            <span class="stat-label">已确认</span>
            <span class="stat-value">{{ receiptData.confirmed }}人</span>
          </div>
          <div class="stat-item unconfirmed">
            <span class="stat-icon">⚠️</span>
            <span class="stat-label">未确认</span>
            <span class="stat-value">{{ receiptData.unconfirmed }}人</span>
          </div>
        </div>

        <el-progress
          :percentage="confirmationRate"
          :color="progressColor"
          :stroke-width="12"
          class="cute-progress"
        />

        <div class="receipt-detail">
          <h4 class="section-title">确认明细</h4>
          <el-table
            :data="receiptData.details"
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
            <el-table-column prop="studentName" label="幼儿姓名" width="150" align="center">
              <template #default="{ row }">
                <span class="student-name">{{ row.studentName }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="parentName" label="家长姓名" width="150" align="center">
              <template #default="{ row }">
                <span class="parent-name">{{ row.parentName }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="relation" label="关系" width="100" align="center">
              <template #default="{ row }">
                <span class="relation-text">{{ row.relation || '-' }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="isConfirmed" label="确认状态" width="120" align="center">
              <template #default="{ row }">
                <el-tag :type="row.isConfirmed ? 'success' : 'warning'" class="cute-tag" effect="plain">
                  <span v-if="row.isConfirmed">✅ 已确认</span>
                  <span v-else>⚠️ 待确认</span>
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="confirmTime" label="确认时间" width="180" align="center">
              <template #default="{ row }">
                <span class="time-text">{{ row.confirmTime || '-' }}</span>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>

      <div class="form-actions">
        <el-button class="cute-btn" @click="handleBack">
          <span>⬅️</span> 返回列表
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script>
import { getNoticeDetail, getNoticeReceipts } from '@/api/notice'
import { ElMessage } from 'element-plus'

export default {
  name: 'NoticeReceiptDetail',
  data() {
    return {
      noticeId: null,
      detailLoading: false,
      noticeDetail: null,
      receiptData: null
    }
  },
  computed: {
    confirmationRate() {
      if (!this.receiptData || this.receiptData.total === 0) return 0
      return Math.round((this.receiptData.confirmed / this.receiptData.total) * 100)
    },
    progressColor() {
      const rate = this.confirmationRate
      if (rate >= 80) return '#67C23A'
      if (rate >= 50) return '#E6A23C'
      return '#F56C6C'
    }
  },
  created() {
    this.noticeId = this.$route.params.id
    if (this.noticeId) {
      this.loadNoticeDetail()
      this.loadReceiptData()
    }
  },
  methods: {
    async loadNoticeDetail() {
      this.detailLoading = true
      try {
        const res = await getNoticeDetail(this.noticeId)
        this.noticeDetail = res.data || {}
      } catch (error) {
        console.error('加载通知详情失败:', error)
        ElMessage.error('加载通知详情失败')
      } finally {
        this.detailLoading = false
      }
    },
    async loadReceiptData() {
      try {
        const res = await getNoticeReceipts(this.noticeId)
        this.receiptData = res.data || { total: 0, confirmed: 0, unconfirmed: 0, details: [] }
      } catch (error) {
        console.error('加载确认情况失败:', error)
        ElMessage.error('加载确认情况失败')
      }
    },
    getTypeTag(noticeType) {
      const map = { 1: '', 2: 'warning', 3: 'success' }
      return map[noticeType] || ''
    },
    getLevelTag(noticeLevel) {
      const map = { 1: 'info', 2: 'warning', 3: 'danger' }
      return map[noticeLevel] || 'info'
    },
    handleBack() {
      this.$router.push('/busi/notice/manage/list')
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
.notice-header { padding: 10px 0; }
.notice-title-row { display: flex; align-items: center; gap: 10px; margin-bottom: 12px; }
.level-icon { font-size: 20px; }
.notice-title { font-family: 'ZCOOL KuaiLe', 'Noto Sans SC', sans-serif; font-size: 24px; color: #5D4E4E; margin: 0; }
.notice-meta { display: flex; align-items: center; gap: 12px; flex-wrap: wrap; }
.meta-text { color: #5D4E4E; font-size: 14px; }
.meta-separator { color: #DCDFE6; }
.cute-tag { border-radius: 20px; font-weight: 500; }
.notice-content { padding: 20px 0; }
.content-label { font-size: 16px; color: #5D4E4E; margin: 0 0 12px; }
.content-text { font-size: 15px; color: #5D4E4E; line-height: 1.8; white-space: pre-wrap; }
.receipt-section { padding: 10px 0; }
.receipt-header { margin-bottom: 20px; }
.section-title { font-size: 18px; color: #5D4E4E; margin: 0; }
.receipt-stats { display: flex; gap: 20px; margin-bottom: 20px; flex-wrap: wrap; }
.stat-item { display: flex; align-items: center; gap: 8px; padding: 12px 20px; border-radius: 12px; background: #f5f5f5; }
.stat-icon { font-size: 20px; }
.stat-label { font-size: 14px; color: #9B8E8E; }
.stat-value { font-size: 18px; font-weight: 600; color: #5D4E4E; }
.stat-item.total { background: linear-gradient(135deg, #E8F4FD 0%, #D4E9FC 100%); }
.stat-item.confirmed { background: linear-gradient(135deg, #E8F8F0 0%, #D4F5E6 100%); }
.stat-item.unconfirmed { background: linear-gradient(135deg, #FFF5E6 0%, #FFE8CC 100%); }
.cute-progress { margin: 20px 0; }
.cute-progress :deep(.el-progress__text) { font-weight: 600; }
.receipt-detail { margin-top: 20px; }
.cute-table { border-radius: 16px; overflow: hidden; }
.cute-table :deep(.el-table__header-wrapper th) { background: linear-gradient(135deg, #FFF0F0 0%, #FFE8E8 100%) !important; color: #5D4E4E; font-weight: 600; }
.cute-table :deep(.el-table__row) { transition: all 0.3s; }
.cute-table :deep(.el-table__row:hover) { background: rgba(255, 230, 230, 0.5) !important; }
.row-number { display: inline-flex; align-items: center; justify-content: center; width: 28px; height: 28px; background: linear-gradient(135deg, #FFF0F0 0%, #FFE8E8 100%); border-radius: 50%; font-size: 12px; font-weight: 600; color: #FF6B6B; }
.student-name, .parent-name { font-weight: 600; color: #5D4E4E; }
.relation-text, .time-text { color: #5D4E4E; font-size: 13px; }
.form-actions { padding: 30px 0 10px; display: flex; justify-content: center; }
.cute-btn { border-radius: 12px; padding: 10px 20px; font-family: 'Noto Sans SC', sans-serif; font-weight: 500; border: none; transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1); display: inline-flex; align-items: center; gap: 6px; background: linear-gradient(135deg, #FFF0F0 0%, #FFE8E8 100%); color: #5D4E4E; }
.cute-btn:hover { transform: translateY(-2px); box-shadow: 0 6px 20px rgba(255, 107, 107, 0.2); }

@media (max-width: 768px) {
  .page-title { font-size: 22px; }
  .receipt-stats { flex-direction: column; }
  .notice-meta { flex-direction: column; align-items: flex-start; }
  .meta-separator { display: none; }
}
</style>