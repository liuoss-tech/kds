<template>
  <div class="page-container">
    <div class="page-header">
      <div class="header-decoration">
        <span class="deco-flower">✿</span>
      </div>
      <h2 class="page-title">通知详情</h2>
      <p class="page-subtitle">查看通知内容</p>
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

      <div class="confirm-section">
        <el-button
          v-if="noticeDetail?.confirmStatus !== 1"
          type="primary"
          class="confirm-btn"
          :loading="confirmLoading"
          @click="handleConfirm"
        >
          <span class="btn-icon">✨</span>
          确认已读
        </el-button>
        <el-button
          v-else
          type="info"
          class="confirm-btn confirmed"
          disabled
        >
          <span class="btn-icon">✅</span>
          已确认 ✓
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script>
import { getNoticeDetail, confirmNotice } from '@/api/notice'
import { ElMessage } from 'element-plus'

export default {
  name: 'NoticeViewDetail',
  data() {
    return {
      noticeId: null,
      detailLoading: false,
      confirmLoading: false,
      noticeDetail: null
    }
  },
  created() {
    this.noticeId = this.$route.params.id
    if (this.noticeId) {
      this.loadNoticeDetail()
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
    async handleConfirm() {
      this.confirmLoading = true
      try {
        await confirmNotice(this.noticeId)
        ElMessage.success('🎉 确认成功')
        this.loadNoticeDetail()
        this.$router.push('/busi/notice/view/list')
      } catch (error) {
        console.error('确认通知失败:', error)
        ElMessage.error('确认失败，请重试')
      } finally {
        this.confirmLoading = false
      }
    },
    getTypeTag(noticeType) {
      const map = { 1: '', 2: 'warning', 3: 'success' }
      return map[noticeType] || ''
    },
    getLevelTag(noticeLevel) {
      const map = { 1: 'info', 2: 'warning', 3: 'danger' }
      return map[noticeLevel] || 'info'
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
.confirm-section { padding: 30px 0 10px; display: flex; justify-content: center; }
.confirm-btn { border-radius: 25px; padding: 15px 50px; font-size: 16px; font-family: 'Noto Sans SC', sans-serif; font-weight: 600; border: none; transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1); display: inline-flex; align-items: center; gap: 8px; background: linear-gradient(135deg, #FFB5B5 0%, #FF8E8E 100%); color: #fff; box-shadow: 0 6px 20px rgba(255, 107, 107, 0.3); }
.confirm-btn:hover { transform: translateY(-3px); box-shadow: 0 10px 30px rgba(255, 107, 107, 0.4); }
.confirm-btn.confirmed { background: linear-gradient(135deg, #C0C0C0 0%, #A0A0A0 100%); color: #fff; cursor: not-allowed; box-shadow: none; }
.confirm-btn.confirmed:hover { transform: none; }
.btn-icon { font-size: 18px; }

@media (max-width: 768px) {
  .page-title { font-size: 22px; }
  .notice-meta { flex-direction: column; align-items: flex-start; }
  .meta-separator { display: none; }
  .confirm-btn { width: 100%; padding: 14px 20px; }
}
</style>