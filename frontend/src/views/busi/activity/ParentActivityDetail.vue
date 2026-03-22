<template>
  <div class="page-container">
    <div class="page-header">
      <div class="header-decoration">
        <span class="deco-flower">✿</span>
      </div>
      <h2 class="page-title">活动详情</h2>
      <p class="page-subtitle">查看活动详情并报名参加</p>
    </div>

    <el-card shadow="never" class="content-card" v-loading="detailLoading">
      <div class="activity-header">
        <div class="activity-icon">{{ getTypeIcon(activityData.activityType) }}</div>
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

    <el-card shadow="never" class="content-card registration-card" v-if="activityData.status === 1">
      <div class="registered-info" v-if="myRegistrations && myRegistrations.length > 0">
        <div class="registered-header">
          <span class="registered-icon">✅</span>
          <span class="registered-text">您已报名此活动</span>
        </div>
        <div class="registered-details" v-for="reg in myRegistrations" :key="reg.id">
          <div class="detail-item">
            <span class="detail-label">报名幼儿：</span>
            <span class="detail-value">{{ reg.studentName }}</span>
          </div>
          <div class="detail-item" v-if="reg.remark">
            <span class="detail-label">备注：</span>
            <span class="detail-value">{{ reg.remark }}</span>
          </div>
          <div class="registered-actions">
            <el-button type="danger" class="cute-btn danger" size="small" @click="handleCancelRegistration(reg.id)" :loading="cancelLoading">
              <span>❌</span> 取消报名
            </el-button>
          </div>
        </div>
      </div>

      <div class="register-form" v-if="availableStudents && availableStudents.length > 0">
        <h4 class="form-title">📝 报名参加</h4>
        <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" class="cute-form">
          <el-form-item label="选择幼儿" prop="studentId">
            <el-select v-model="form.studentId" placeholder="请选择要参与的幼儿" style="width: 100%;" class="cute-select">
              <el-option
                v-for="student in availableStudents"
                :key="student.id"
                :label="`${student.studentName} (${student.className})`"
                :value="student.id"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="备注信息">
            <el-input
              v-model="form.remark"
              type="textarea"
              :rows="3"
              placeholder="如有其他要求请备注"
              class="cute-input"
            />
          </el-form-item>

          <el-form-item>
            <div class="form-actions">
              <el-button class="cute-btn" @click="handleBack">
                <span>⬅️</span> 返回
              </el-button>
              <el-button type="primary" class="cute-btn primary" @click="submitRegistration" :loading="submitLoading">
                <span>✨</span> 确认报名
              </el-button>
            </div>
          </el-form-item>
        </el-form>
      </div>
    </el-card>

    <el-card shadow="never" class="content-card" v-else-if="activityData.status !== 1 && !myRegistration">
      <div class="closed-notice">
        <span class="notice-icon">🔒</span>
        <p class="notice-text">此活动已截止报名或已结束</p>
        <el-button class="cute-btn" @click="handleBack">
          <span>⬅️</span> 返回列表
        </el-button>
      </div>
    </el-card>

    <div class="back-btn-container" v-if="activityData.status === 1 && myRegistration">
      <el-button class="cute-btn" @click="handleBack">
        <span>⬅️</span> 返回列表
      </el-button>
    </div>
  </div>
</template>

<script>
import { getActivityDetail, registerActivity, cancelRegistration, getAvailableStudents } from '@/api/activity'
import request from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

export default {
  name: 'ParentActivityDetail',
  data() {
    return {
      activityId: null,
      detailLoading: true,
      submitLoading: false,
      cancelLoading: false,
      activityData: {},
      myRegistrations: [],
      studentList: [],
      form: {
        studentId: undefined,
        activityId: null,
        remark: ''
      },
      rules: {
        studentId: [{ required: true, message: '请选择幼儿', trigger: 'change' }]
      }
    }
  },
  computed: {
    availableStudents() {
      if (!this.myRegistrations || this.myRegistrations.length === 0) {
        return this.studentList
      }
      const registeredIds = this.myRegistrations.map(r => r.studentId)
      return this.studentList.filter(s => !registeredIds.includes(s.id))
    }
  },
  created() {
    this.activityId = this.$route.params.id
    this.form.activityId = this.activityId
    this.loadStudents()
    this.loadActivityDetail()
  },
  methods: {
    loadStudents() {
      request.get('/api/busi/leave/students').then(res => {
        this.studentList = res.data || []
        if (this.studentList.length === 0) {
          ElMessage.warning('您还没有绑定幼儿，无法报名')
        }
      }).catch(err => {
        console.error('加载幼儿列表失败', err)
        this.studentList = []
      })
    },
    async loadActivityDetail() {
      this.detailLoading = true
      try {
        const res = await getActivityDetail(this.activityId)
        this.activityData = res.data || {}
        this.myRegistrations = this.activityData.myRegistrations || []
      } catch (error) {
        ElMessage.error('加载活动详情失败')
      } finally {
        this.detailLoading = false
      }
    },
    async submitRegistration() {
      this.$refs.formRef.validate(async (valid) => {
        if (valid) {
          this.submitLoading = true
          try {
            await registerActivity(this.form)
            ElMessage.success('🎉 报名成功')
            this.loadActivityDetail()
          } catch (error) {
            console.error('报名失败:', error)
          } finally {
            this.submitLoading = false
          }
        }
      })
    },
    handleCancelRegistration(registrationId) {
      ElMessageBox.confirm(
        '确认取消报名此活动吗？',
        '❌ 取消报名',
        {
          confirmButtonText: '确认取消',
          cancelButtonText: '返回',
          type: 'warning'
        }
      ).then(async () => {
        this.cancelLoading = true
        try {
          await cancelRegistration(registrationId)
          ElMessage.success('已取消报名')
          this.loadActivityDetail()
        } catch (error) {
          console.error('取消报名失败:', error)
        } finally {
          this.cancelLoading = false
        }
      }).catch(() => {})
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
    getStatusType(status) {
      const map = { 1: 'success', 2: 'warning', 3: 'primary', 4: 'info' }
      return map[status] || 'info'
    },
    handleBack() {
      this.$router.push('/busi/activity/parent/list')
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
.registered-info { text-align: center; padding: 20px 0; }
.registered-header { display: flex; align-items: center; justify-content: center; gap: 10px; margin-bottom: 20px; }
.registered-icon { font-size: 32px; }
.registered-text { font-size: 18px; color: #67C23A; font-weight: 600; }
.registered-details { background: linear-gradient(135deg, #F0F9EB 0%, #E8F5E1 100%); border-radius: 12px; padding: 20px; margin-bottom: 20px; text-align: left; }
.detail-item { display: flex; margin-bottom: 8px; }
.detail-item:last-child { margin-bottom: 0; }
.detail-label { color: #7D6E6E; font-size: 14px; width: 80px; }
.detail-value { color: #5D4E4E; font-size: 14px; font-weight: 500; }
.registered-actions { display: flex; justify-content: center; padding-bottom: 20px; }
.form-title { font-size: 16px; color: #5D4E4E; margin: 0 0 20px; font-family: 'ZCOOL KuaiLe', sans-serif; text-align: center; }
.cute-form { padding: 10px 20px; }
.cute-input :deep(.el-textarea__inner) { border-radius: 12px; }
.cute-select :deep(.el-select__wrapper) { border-radius: 12px; box-shadow: 0 0 0 1px #F0E0E0 inset; background: #fff; }
.cute-input-number { width: 120px; }
.cute-input-number :deep(.el-input__wrapper) { border-radius: 12px; box-shadow: 0 0 0 1px #F0E0E0 inset; background: #fff; }
.input-hint { margin-left: 8px; color: #9B8E8E; font-size: 14px; }
.cute-btn { border-radius: 12px; padding: 10px 20px; font-family: 'Noto Sans SC', sans-serif; font-weight: 500; border: none; transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1); display: inline-flex; align-items: center; gap: 6px; background: linear-gradient(135deg, #FFF0F0 0%, #FFE8E8 100%); color: #5D4E4E; }
.cute-btn:hover { transform: translateY(-2px); box-shadow: 0 6px 20px rgba(255, 107, 107, 0.2); }
.cute-btn.primary { background: linear-gradient(135deg, #FFB5B5 0%, #FF8E8E 100%); color: #fff; }
.cute-btn.primary:hover { background: linear-gradient(135deg, #FF6B6B 0%, #FF8E53 100%); box-shadow: 0 8px 25px rgba(255, 107, 107, 0.35); }
.cute-btn.danger { background: linear-gradient(135deg, #FF6B6B 0%, #FF8E53 100%); color: #fff; }
.form-actions { display: flex; justify-content: center; gap: 20px; width: 100%; padding-bottom: 20px; }
.closed-notice { text-align: center; padding: 40px 20px; }
.notice-icon { font-size: 48px; display: block; margin-bottom: 16px; }
.notice-text { font-size: 15px; color: #9B8E8E; margin: 0 0 20px; }
.back-btn-container { display: flex; justify-content: center; margin-top: 10px; padding-bottom: 20px; }

@media (max-width: 768px) {
  .page-title { font-size: 22px; }
  .activity-info-grid { grid-template-columns: 1fr; }
}
</style>