<template>
  <div class="page-container">
    <div class="page-header">
      <div class="header-decoration">
        <span class="deco-flower">✿</span>
      </div>
      <h2 class="page-title">活动详情</h2>
      <p class="page-subtitle">查看活动详细信息及报名情况</p>
    </div>

    <el-card shadow="never" class="content-card" v-loading="detailLoading">
      <div class="activity-header">
        <div class="activity-icon">🎪</div>
        <div class="activity-title">
          <h3>{{ activityData.title }}</h3>
          <el-tag :type="getStatusType(activityData.status)" class="cute-tag" effect="plain">
            {{ activityData.statusText }}
          </el-tag>
        </div>
      </div>

      <div class="activity-info-grid">
        <div class="info-item">
          <span class="info-label">📋 活动类型</span>
          <span class="info-value">{{ activityData.activityTypeText }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">📍 活动地点</span>
          <span class="info-value">{{ activityData.location || '-' }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">🕐 活动时间</span>
          <span class="info-value">{{ activityData.activityTime || '-' }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">⏰ 报名截止</span>
          <span class="info-value">{{ activityData.deadlineTime || '-' }}</span>
        </div>
      </div>

      <div class="activity-description" v-if="activityData.content">
        <div class="description-label">📝 活动详情</div>
        <div class="description-content">{{ activityData.content }}</div>
      </div>

      <div class="participating-classes" v-if="activityData.targetClassName">
        <div class="classes-label">🏫 参与班级</div>
        <div class="classes-tags">
          <el-tag
            v-for="(cls, index) in activityData.targetClassName.split(',')"
            :key="index"
            class="class-tag"
            effect="plain"
          >
            {{ cls.trim() }}
          </el-tag>
        </div>
      </div>
    </el-card>

    <el-card shadow="never" class="content-card registration-card" v-loading="registrationLoading">
      <div class="section-header">
        <h4 class="section-title">📋 报名情况</h4>
        <span class="total-count">共 {{ registrations.length }} 人报名</span>
      </div>

      <el-table
        :data="registrations"
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
        <el-table-column prop="studentName" label="幼儿姓名" min-width="120" align="center">
          <template #default="{ row }">
            <div class="student-info">
              <span class="student-icon">👶</span>
              <span class="student-name">{{ row.studentName }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="parentRelation" label="家长姓名" min-width="100" align="center">
          <template #default="{ row }">
            <span class="parent-name">{{ row.parentRelation || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="150" align="center">
          <template #default="{ row }">
            <span v-if="row.remark" class="remark-text">{{ row.remark }}</span>
            <span v-else class="no-remark">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="报名时间" min-width="160" align="center">
          <template #default="{ row }">
            <span class="time-text">{{ row.createTime }}</span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <div class="back-btn-container">
      <el-button class="cute-btn" @click="handleBack">
        <span>⬅️</span> 返回列表
      </el-button>
    </div>
  </div>
</template>

<script>
import { getActivityDetail, getActivityRegistrations } from '@/api/activity'
import { ElMessage } from 'element-plus'

export default {
  name: 'TeacherActivityDetail',
  data() {
    return {
      activityId: null,
      detailLoading: true,
      registrationLoading: true,
      activityData: {},
      registrations: []
    }
  },
  created() {
    this.activityId = this.$route.params.id
    if (this.activityId) {
      this.loadActivityDetail()
      this.loadRegistrations()
    }
  },
  methods: {
    async loadActivityDetail() {
      this.detailLoading = true
      try {
        const res = await getActivityDetail(this.activityId)
        this.activityData = res.data || {}
      } catch (error) {
        ElMessage.error('加载活动详情失败')
      } finally {
        this.detailLoading = false
      }
    },
    async loadRegistrations() {
      this.registrationLoading = true
      try {
        const res = await getActivityRegistrations(this.activityId)
        this.registrations = res.data || []
      } catch (error) {
        console.error('加载报名情况失败:', error)
      } finally {
        this.registrationLoading = false
      }
    },
    getStatusType(status) {
      const map = { 1: 'success', 2: 'warning', 3: 'primary', 4: 'info' }
      return map[status] || 'info'
    },
    handleBack() {
      this.$router.push('/busi/activity/teacher/list')
    }
  }
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=ZCOOL+KuaiLe&family=Noto+Sans+SC:wght@400;500;700&display=swap');

.page-container {
  padding: 0;
  height: 100%;
  overflow-y: auto;
  box-sizing: border-box;
}
.page-header { text-align: center; padding: 20px 0 24px; position: relative; }
.header-decoration { position: absolute; top: 10px; left: 50%; transform: translateX(-50%); }
.deco-flower { font-size: 24px; opacity: 0.3; animation: flowerFloat 3s ease-in-out infinite; }
@keyframes flowerFloat { 0%, 100% { transform: translateY(0) rotate(0deg); } 50% { transform: translateY(-5px) rotate(10deg); } }
.page-title { font-family: 'ZCOOL KuaiLe', 'Noto Sans SC', sans-serif; font-size: 28px; color: #5D4E4E; margin: 0 0 8px; }
.page-subtitle { font-size: 14px; color: #9B8E8E; margin: 0; }
.content-card { border-radius: 20px; border: none; box-shadow: 0 8px 30px rgba(255, 182, 193, 0.15); margin-bottom: 20px; }
.activity-header { display: flex; align-items: center; gap: 16px; padding-bottom: 20px; border-bottom: 1px dashed #FFE8E8; margin-bottom: 20px; }
.activity-icon { font-size: 48px; }
.activity-title h3 { font-size: 24px; color: #5D4E4E; margin: 0 0 8px; font-family: 'ZCOOL KuaiLe', sans-serif; }
.cute-tag { border-radius: 20px; font-weight: 500; }
.activity-info-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 20px; padding: 0 10px; }
.info-item { display: flex; flex-direction: column; gap: 4px; }
.info-label { font-size: 13px; color: #9B8E8E; }
.info-value { font-size: 15px; color: #5D4E4E; font-weight: 500; }
.activity-description { margin-top: 20px; padding-top: 20px; border-top: 1px dashed #FFE8E8; }
.description-label { font-size: 13px; color: #9B8E8E; margin-bottom: 10px; }
.description-content { font-size: 14px; color: #5D4E4E; line-height: 1.8; white-space: pre-wrap; }
.participating-classes { margin-top: 20px; padding-top: 20px; border-top: 1px dashed #FFE8E8; }
.classes-label { font-size: 13px; color: #9B8E8E; margin-bottom: 10px; }
.classes-tags { display: flex; flex-wrap: wrap; gap: 8px; }
.class-tag { border-radius: 12px; }
.registration-card { margin-top: 20px; }
.section-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.section-title { font-size: 16px; color: #5D4E4E; margin: 0; font-family: 'ZCOOL KuaiLe', sans-serif; }
.total-count { font-size: 14px; color: #FF6B6B; font-weight: 500; }
.cute-table { border-radius: 16px; overflow: hidden; }
.cute-table :deep(.el-table__header-wrapper th) { background: linear-gradient(135deg, #FFF0F0 0%, #FFE8E8 100%) !important; color: #5D4E4E; font-weight: 600; }
.cute-table :deep(.el-table__row) { transition: all 0.3s; }
.cute-table :deep(.el-table__row:hover) { background: rgba(255, 230, 230, 0.5) !important; }
.row-number { display: inline-flex; align-items: center; justify-content: center; width: 28px; height: 28px; background: linear-gradient(135deg, #FFF0F0 0%, #FFE8E8 100%); border-radius: 50%; font-size: 12px; font-weight: 600; color: #FF6B6B; }
.student-info { display: flex; align-items: center; justify-content: center; gap: 8px; }
.student-icon { font-size: 18px; }
.student-name { font-weight: 600; color: #5D4E4E; }
.parent-name { color: #5D4E4E; font-size: 13px; }
.remark-text { color: #5D4E4E; font-size: 13px; }
.no-remark { color: #CCC; font-style: italic; }
.time-text { color: #5D4E4E; font-size: 13px; }
.back-btn-container { display: flex; justify-content: center; margin-top: 10px; padding-bottom: 20px; }
.cute-btn { border-radius: 12px; padding: 10px 20px; font-family: 'Noto Sans SC', sans-serif; font-weight: 500; border: none; transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1); display: inline-flex; align-items: center; gap: 6px; background: linear-gradient(135deg, #FFF0F0 0%, #FFE8E8 100%); color: #5D4E4E; }
.cute-btn:hover { transform: translateY(-2px); box-shadow: 0 6px 20px rgba(255, 107, 107, 0.2); }

@media (max-width: 768px) {
  .page-title { font-size: 22px; }
  .activity-info-grid { grid-template-columns: 1fr; }
}
</style>