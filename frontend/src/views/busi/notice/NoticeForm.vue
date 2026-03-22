<template>
  <div class="page-container">
    <div class="page-header">
      <div class="header-decoration">
        <span class="deco-flower">✿</span>
      </div>
      <h2 class="page-title">{{ isEdit ? '编辑通知' : '发布通知' }}</h2>
      <p class="page-subtitle">{{ isEdit ? '修改通知信息' : '创建新的班级通知' }}</p>
    </div>

    <el-card shadow="never" class="content-card">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="120px" class="cute-form">
        <el-form-item label="通知标题" prop="title">
          <el-input
            v-model="form.title"
            placeholder="请输入通知标题"
            class="cute-input"
            maxlength="100"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="通知类型" prop="noticeType">
          <el-select v-model="form.noticeType" placeholder="请选择通知类型" style="width: 100%;" class="cute-select">
            <el-option label="📋 日常通知" :value="1" />
            <el-option label="💰 缴费通知" :value="2" />
            <el-option label="🏖️ 放假通知" :value="3" />
          </el-select>
        </el-form-item>

        <el-form-item label="通知级别" prop="noticeLevel">
          <el-select v-model="form.noticeLevel" placeholder="请选择通知级别" style="width: 100%;" class="cute-select">
            <el-option label="普通" :value="1" />
            <el-option label="🔺 置顶" :value="2" />
            <el-option label="🔴 强提醒" :value="3" />
          </el-select>
        </el-form-item>

        <el-form-item label="发送范围" prop="targetScope">
          <div class="scope-selector">
            <el-radio-group v-model="form.targetScope" class="scope-radio">
              <el-radio :value="1" :disabled="!canSelectAll">
                <span>🌐 全园</span>
              </el-radio>
              <el-radio :value="2">
                <span>🏠 指定班级</span>
              </el-radio>
            </el-radio-group>
            <el-select
              v-if="form.targetScope === 2"
              v-model="form.targetClassId"
              placeholder="请选择班级"
              style="width: 200px;"
              class="cute-select class-select"
            >
              <el-option
                v-for="cls in classList"
                :key="cls.id"
                :label="cls.className"
                :value="cls.id"
              />
            </el-select>
          </div>
          <div v-if="!canSelectAll" class="scope-tip">
            <span class="tip-icon">💡</span> 您只能向本班发送通知
          </div>
        </el-form-item>

        <el-form-item label="通知正文" prop="content">
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="6"
            placeholder="请输入通知正文内容"
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
import { publishNotice, getNoticeDetail } from '@/api/notice'
import { ElMessage } from 'element-plus'

export default {
  name: 'NoticeForm',
  data() {
    return {
      isEdit: false,
      submitLoading: false,
      classList: [],
      canSelectAll: false,
      form: {
        title: '',
        noticeType: undefined,
        noticeLevel: 1,
        targetScope: 2,
        targetClassId: undefined,
        content: ''
      },
      rules: {
        title: [{ required: true, message: '请输入通知标题', trigger: 'blur' }],
        noticeType: [{ required: true, message: '请选择通知类型', trigger: 'change' }],
        noticeLevel: [{ required: true, message: '请选择通知级别', trigger: 'change' }],
        content: [{ required: true, message: '请输入通知正文', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.checkUserRole()
    if (this.$route.query.id) {
      this.isEdit = true
      this.loadNoticeDetail(this.$route.query.id)
    }
    this.loadClassList()
  },
  watch: {
    'form.targetScope'(newVal) {
      if (newVal === 1) {
        this.form.targetClassId = undefined
      }
    }
  },
  methods: {
    checkUserRole() {
      const userRole = localStorage.getItem('kds_role')
      if (userRole === 'SUPER_ADMIN' || userRole === 'PRINCIPAL') {
        this.canSelectAll = true
      }
    },
    loadClassList() {
      request.get('/api/busi/student/class-list').then(res => {
        this.classList = res.data || []
        if (this.classList.length > 0 && !this.isEdit) {
          this.form.targetClassId = this.classList[0].id
        }
      }).catch(err => {
        console.error('加载班级列表失败', err)
        this.classList = []
      })
    },
    async loadNoticeDetail(id) {
      try {
        const res = await getNoticeDetail(id)
        const data = res.data || {}
        this.form = {
          title: data.title || '',
          noticeType: data.noticeType,
          noticeLevel: data.noticeLevel,
          targetScope: data.targetScope,
          targetClassId: data.targetClassId,
          content: data.content || ''
        }
      } catch (error) {
        ElMessage.error('加载通知详情失败')
      }
    },
    submitForm() {
      this.$refs.formRef.validate(async (valid) => {
        if (valid) {
          if (this.form.targetScope === 2 && !this.form.targetClassId) {
            ElMessage.warning('请选择发送班级')
            return
          }
          this.submitLoading = true
          try {
            await publishNotice(this.form)
            ElMessage.success('🎉 通知发布成功')
            this.handleBack()
          } catch (error) {
            console.error('发布通知失败:', error)
          } finally {
            this.submitLoading = false
          }
        }
      })
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
.content-card { border-radius: 20px; border: none; box-shadow: 0 8px 30px rgba(255, 182, 193, 0.15); max-width: 800px; margin: 0 auto; }
.cute-form { padding: 30px 20px; }
.cute-input :deep(.el-input__inner),
.cute-input :deep(.el-textarea__inner) { border-radius: 12px; }
.cute-select :deep(.el-select__wrapper) { border-radius: 12px; box-shadow: 0 0 0 1px #F0E0E0 inset; background: #fff; }
.scope-selector { display: flex; align-items: center; gap: 20px; flex-wrap: wrap; }
.scope-radio { display: flex; gap: 20px; }
.class-select { margin-left: 10px; }
.scope-tip { margin-top: 10px; font-size: 13px; color: #9B8E8E; }
.tip-icon { margin-right: 4px; }
.cute-btn { border-radius: 12px; padding: 10px 20px; font-family: 'Noto Sans SC', sans-serif; font-weight: 500; border: none; transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1); display: inline-flex; align-items: center; gap: 6px; background: linear-gradient(135deg, #FFF0F0 0%, #FFE8E8 100%); color: #5D4E4E; }
.cute-btn:hover { transform: translateY(-2px); box-shadow: 0 6px 20px rgba(255, 107, 107, 0.2); }
.cute-btn.primary { background: linear-gradient(135deg, #FFB5B5 0%, #FF8E8E 100%); color: #fff; }
.cute-btn.primary:hover { background: linear-gradient(135deg, #FF6B6B 0%, #FF8E53 100%); box-shadow: 0 8px 25px rgba(255, 107, 107, 0.35); }
.form-actions { display: flex; justify-content: center; gap: 20px; width: 100%; }

@media (max-width: 768px) {
  .page-title { font-size: 22px; }
  .scope-selector { flex-direction: column; align-items: flex-start; }
  .class-select { margin-left: 0; margin-top: 10px; }
}
</style>