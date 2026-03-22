<template>
  <div class="page-container">
    <div class="page-header">
      <div class="header-decoration">
        <span class="deco-flower">✿</span>
      </div>
      <h2 class="page-title">{{ isEdit ? '编辑活动' : '发布活动' }}</h2>
      <p class="page-subtitle">{{ isEdit ? '修改活动信息' : '创建新的班级活动' }}</p>
    </div>

    <el-card shadow="never" class="content-card">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="120px" class="cute-form">
        <el-form-item label="活动名称" prop="title">
          <el-input
            v-model="form.title"
            placeholder="请输入活动名称"
            class="cute-input"
          />
        </el-form-item>

        <el-form-item label="活动类型" prop="activityType">
          <el-select v-model="form.activityType" placeholder="请选择活动类型" style="width: 100%;" class="cute-select">
            <el-option label="🏃 亲子游戏" :value="1" />
            <el-option label="🎭 文艺汇演" :value="2" />
            <el-option label="👨‍👩‍👧 家长会" :value="3" />
            <el-option label="🌳 户外踏青" :value="4" />
          </el-select>
        </el-form-item>

        <el-form-item label="活动地点" prop="location">
          <el-input
            v-model="form.location"
            placeholder="请输入活动地点"
            class="cute-input"
          />
        </el-form-item>

        <el-form-item label="活动时间" prop="activityTime">
          <el-date-picker
            v-model="form.activityTime"
            type="datetime"
            placeholder="选择活动时间"
            format="YYYY-MM-DD HH:mm"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%;"
            class="cute-picker"
          />
        </el-form-item>

        <el-form-item label="报名截止" prop="deadlineTime">
          <el-date-picker
            v-model="form.deadlineTime"
            type="datetime"
            placeholder="选择报名截止时间"
            format="YYYY-MM-DD HH:mm"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%;"
            class="cute-picker"
          />
        </el-form-item>

        <el-form-item label="参与班级" prop="targetClassId">
          <el-select v-model="form.targetClassId" placeholder="请选择参与班级" style="width: 100%;" class="cute-select">
            <el-option
              v-for="cls in classList"
              :key="cls.id"
              :label="cls.className"
              :value="cls.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="活动详情" prop="content">
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="5"
            placeholder="请输入活动详情描述"
            class="cute-input"
          />
        </el-form-item>

        <el-form-item>
          <div class="form-actions">
            <el-button class="cute-btn" @click="handleBack">
              <span>⬅️</span> 返回
            </el-button>
            <el-button type="primary" class="cute-btn primary" @click="submitForm" :loading="submitLoading">
              <span>✨</span> {{ isEdit ? '保存修改' : '立即发布' }}
            </el-button>
          </div>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import request from '@/utils/request'
import { publishActivity, getActivityDetail } from '@/api/activity'
import { ElMessage } from 'element-plus'

export default {
  name: 'TeacherActivityForm',
  data() {
    return {
      isEdit: false,
      submitLoading: false,
      classList: [],
      form: {
        title: '',
        activityType: undefined,
        location: '',
        activityTime: '',
        deadlineTime: '',
        targetClassId: undefined,
        content: '',
        targetScope: 2
      },
      rules: {
        title: [{ required: true, message: '请输入活动名称', trigger: 'blur' }],
        activityType: [{ required: true, message: '请选择活动类型', trigger: 'change' }],
        location: [{ required: true, message: '请输入活动地点', trigger: 'blur' }],
        activityTime: [{ required: true, message: '请选择活动时间', trigger: 'change' }],
        deadlineTime: [{ required: true, message: '请选择报名截止时间', trigger: 'change' }],
        targetClassId: [{ required: true, message: '请选择参与班级', trigger: 'change' }]
      }
    }
  },
  created() {
    if (this.$route.query.id) {
      this.isEdit = true
      this.loadActivityDetail(this.$route.query.id)
    }
    this.loadClassList()
  },
  methods: {
    loadClassList() {
      request.get('/api/busi/student/class-list').then(res => {
        this.classList = res.data || []
      }).catch(err => {
        console.error('加载班级列表失败', err)
        this.classList = []
      })
    },
    async loadActivityDetail(id) {
      try {
        const res = await getActivityDetail(id)
        const data = res.data || {}
        this.form = {
          title: data.title || '',
          activityType: data.activityType,
          location: data.location || '',
          activityTime: data.activityTime || '',
          deadlineTime: data.deadlineTime || '',
          targetClassId: data.targetClassId,
          content: data.content || ''
        }
      } catch (error) {
        ElMessage.error('加载活动详情失败')
      }
    },
    submitForm() {
      this.$refs.formRef.validate(async (valid) => {
        if (valid) {
          if (new Date(this.form.deadlineTime) >= new Date(this.form.activityTime)) {
            ElMessage.warning('报名截止时间必须早于活动时间')
            return
          }
          this.submitLoading = true
          try {
            await publishActivity(this.form)
            ElMessage.success('🎉 活动发布成功')
            this.handleBack()
          } catch (error) {
            console.error('发布活动失败:', error)
          } finally {
            this.submitLoading = false
          }
        }
      })
    },
    handleBack() {
      this.$router.push('/busi/activity/teacher/list')
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
.content-card { border-radius: 20px; border: none; box-shadow: 0 8px 30px rgba(255, 182, 193, 0.15); max-width: 700px; margin: 0 auto; }
.cute-form { padding: 30px 20px; }
.cute-input :deep(.el-input__inner),
.cute-input :deep(.el-textarea__inner) { border-radius: 12px; }
.cute-select :deep(.el-select__wrapper) { border-radius: 12px; box-shadow: 0 0 0 1px #F0E0E0 inset; background: #fff; }
.cute-picker { width: 100%; }
.cute-picker :deep(.el-input__wrapper) { border-radius: 12px; box-shadow: 0 0 0 1px #F0E0E0 inset; background: #fff; }
.cute-btn { border-radius: 12px; padding: 10px 20px; font-family: 'Noto Sans SC', sans-serif; font-weight: 500; border: none; transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1); display: inline-flex; align-items: center; gap: 6px; background: linear-gradient(135deg, #FFF0F0 0%, #FFE8E8 100%); color: #5D4E4E; }
.cute-btn:hover { transform: translateY(-2px); box-shadow: 0 6px 20px rgba(255, 107, 107, 0.2); }
.cute-btn.primary { background: linear-gradient(135deg, #FFB5B5 0%, #FF8E8E 100%); color: #fff; }
.cute-btn.primary:hover { background: linear-gradient(135deg, #FF6B6B 0%, #FF8E53 100%); box-shadow: 0 8px 25px rgba(255, 107, 107, 0.35); }
.form-actions { display: flex; justify-content: center; gap: 20px; width: 100%; }

@media (max-width: 768px) {
  .page-title { font-size: 22px; }
}
</style>