<template>
  <div class="page-container">
    <div class="page-header">
      <div class="header-decoration">
        <span class="deco-flower">✿</span>
      </div>
      <h2 class="page-title">🍽️ 食谱管理</h2>
      <p class="page-subtitle">为班级规划每日营养食谱</p>
    </div>

    <el-card shadow="never" class="content-card">
      <div class="control-bar">
        <el-select v-model="query.classId" placeholder="请选择班级" clearable class="cute-select class-select" @change="handleClassChange">
          <el-option v-for="cls in classList" :key="cls.id" :label="getGradeEmoji(cls.grade) + ' ' + cls.className" :value="cls.id" />
        </el-select>

        <div class="date-navigator">
          <el-button circle class="nav-btn" @click="prevDay" title="前一天">
            <span>◀</span>
          </el-button>
          <div class="current-date">
            <span class="date-text">{{ formatDateDisplay(currentDate) }}</span>
            <span class="weekday-text">{{ getWeekday(currentDate) }}</span>
          </div>
          <el-button circle class="nav-btn" @click="nextDay" title="后一天">
            <span>▶</span>
          </el-button>
        </div>

        <el-date-picker
          ref="datePickerRef"
          v-model="currentDate"
          type="date"
          format="YYYY-MM-DD"
          value-format="YYYY-MM-DD"
          :clearable="false"
          class="hidden-date-picker"
          @change="handleDateChange"
        />

        <el-button circle class="calendar-btn" title="选择日期" @click="openDatePicker">
          <span>📅</span>
        </el-button>

        <el-button class="cute-btn today-btn" @click="goToToday">
          今日
        </el-button>
      </div>

      <div class="recipe-cards">
        <div class="meal-card" :class="{ collapsed: !showBreakfast }">
          <div class="meal-header" @click="showBreakfast = !showBreakfast">
            <span class="meal-icon">🌅</span>
            <span class="meal-name">早餐</span>
            <span class="required-tag">必填</span>
            <span class="toggle-icon">{{ showBreakfast ? '▼' : '▶' }}</span>
          </div>
          <div class="meal-body" v-show="showBreakfast">
            <el-input
              v-model="form.breakfast"
              type="textarea"
              :rows="3"
              placeholder="请输入早餐内容，如：鸡蛋、牛奶、面包"
              class="cute-textarea"
            />
          </div>
        </div>

        <div class="meal-card" :class="{ collapsed: !showMorningSnack }">
          <div class="meal-header" @click="showMorningSnack = !showMorningSnack">
            <span class="meal-icon">🍎</span>
            <span class="meal-name">早点</span>
            <span class="toggle-icon">{{ showMorningSnack ? '▼' : '▶' }}</span>
          </div>
          <div class="meal-body" v-show="showMorningSnack">
            <el-input
              v-model="form.morningSnack"
              type="textarea"
              :rows="3"
              placeholder="请输入早点内容，如：水果、坚果"
              class="cute-textarea"
            />
          </div>
        </div>

        <div class="meal-card" :class="{ collapsed: !showLunch }">
          <div class="meal-header" @click="showLunch = !showLunch">
            <span class="meal-icon">🍚</span>
            <span class="meal-name">午餐</span>
            <span class="required-tag">必填</span>
            <span class="toggle-icon">{{ showLunch ? '▼' : '▶' }}</span>
          </div>
          <div class="meal-body" v-show="showLunch">
            <el-input
              v-model="form.lunch"
              type="textarea"
              :rows="3"
              placeholder="请输入午餐内容，如：米饭、红烧肉、青菜、番茄蛋汤"
              class="cute-textarea"
            />
          </div>
        </div>

        <div class="meal-card" :class="{ collapsed: !showAfternoonSnack }">
          <div class="meal-header" @click="showAfternoonSnack = !showAfternoonSnack">
            <span class="meal-icon">🍪</span>
            <span class="meal-name">午点</span>
            <span class="toggle-icon">{{ showAfternoonSnack ? '▼' : '▶' }}</span>
          </div>
          <div class="meal-body" v-show="showAfternoonSnack">
            <el-input
              v-model="form.afternoonSnack"
              type="textarea"
              :rows="3"
              placeholder="请输入午点内容，如：酸奶、小蛋糕"
              class="cute-textarea"
            />
          </div>
        </div>

        <div class="meal-card optional" :class="{ collapsed: !showDinner }">
          <div class="meal-header" @click="showDinner = !showDinner">
            <span class="meal-icon">🌙</span>
            <span class="meal-name">晚餐（可选）</span>
            <span class="toggle-icon">{{ showDinner ? '▼' : '▶' }}</span>
          </div>
          <div class="meal-body" v-show="showDinner">
            <el-input
              v-model="form.dinner"
              type="textarea"
              :rows="3"
              placeholder="请输入晚餐内容（如有）"
              class="cute-textarea"
            />
          </div>
        </div>
      </div>

      <div class="save-section">
        <el-button type="primary" class="cute-btn primary save-btn" @click="handleSave" :loading="saving">
          <span>💾</span> 保存食谱
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script>
import { getRecipe, saveRecipe } from '@/api/recipe';
import { ElMessage } from 'element-plus';
import request from '@/utils/request';

export default {
  name: 'Recipe',
  data() {
    return {
      classList: [],
      query: {
        classId: undefined,
        date: this.formatDate(new Date())
      },
      currentDate: this.formatDate(new Date()),
      saving: false,
      form: {
        id: null,
        breakfast: '',
        morningSnack: '',
        lunch: '',
        afternoonSnack: '',
        dinner: ''
      },
      showBreakfast: true,
      showMorningSnack: false,
      showLunch: true,
      showAfternoonSnack: true,
      showDinner: false,
      weekdayNames: ['周日', '周一', '周二', '周三', '周四', '周五', '周六'],
      datePickerRef: null
    };
  },
  created() {
    this.getClassList();
  },
  methods: {
    getClassList() {
      request.get('/api/busi/student/class-list').then(res => {
        this.classList = res.data || [];
        if (this.classList.length > 0 && !this.query.classId) {
          this.query.classId = this.classList[0].id;
          this.loadRecipe();
        }
      });
    },
    loadRecipe() {
      if (!this.query.classId) {
        ElMessage.warning('请先选择班级');
        return;
      }
      const params = {
        classId: this.query.classId,
        date: this.query.date
      };
      getRecipe(params).then(res => {
        const data = res.data || {};
        this.form.id = data.id || null;
        this.form.breakfast = data.breakfast || '';
        this.form.morningSnack = data.morningSnack || '';
        this.form.lunch = data.lunch || '';
        this.form.afternoonSnack = data.afternoonSnack || '';
        this.form.dinner = data.dinner || '';
      }).catch(() => {
        this.resetForm();
      });
    },
    resetForm() {
      this.form = {
        id: null,
        breakfast: '',
        morningSnack: '',
        lunch: '',
        afternoonSnack: '',
        dinner: ''
      };
    },
    handleClassChange() {
      this.loadRecipe();
    },
    handleDateChange() {
      this.query.date = this.currentDate;
      this.loadRecipe();
    },
    openDatePicker() {
      this.$refs.datePickerRef?.handleFocus();
    },
    prevDay() {
      const date = new Date(this.currentDate);
      date.setDate(date.getDate() - 1);
      this.currentDate = this.formatDate(date);
      this.query.date = this.currentDate;
      this.loadRecipe();
    },
    nextDay() {
      const date = new Date(this.currentDate);
      date.setDate(date.getDate() + 1);
      this.currentDate = this.formatDate(date);
      this.query.date = this.currentDate;
      this.loadRecipe();
    },
    goToToday() {
      this.currentDate = this.formatDate(new Date());
      this.query.date = this.currentDate;
      this.loadRecipe();
    },
    handleSave() {
      if (!this.query.classId) {
        ElMessage.warning('请先选择班级');
        return;
      }
      if (!this.form.breakfast || !this.form.breakfast.trim()) {
        ElMessage.warning('请填写早餐内容');
        return;
      }
      if (!this.form.lunch || !this.form.lunch.trim()) {
        ElMessage.warning('请填写午餐内容');
        return;
      }
      this.saving = true;
      const data = {
        id: this.form.id,
        classId: this.query.classId,
        targetDate: this.query.date,
        breakfast: this.form.breakfast,
        morningSnack: this.form.morningSnack,
        lunch: this.form.lunch,
        afternoonSnack: this.form.afternoonSnack,
        dinner: this.form.dinner
      };
      saveRecipe(data).then(() => {
        ElMessage.success('🎉 食谱保存成功');
        this.loadRecipe();
      }).catch(() => {
        ElMessage.error('保存失败，请重试');
      }).finally(() => {
        this.saving = false;
      });
    },
    formatDate(date) {
      const d = new Date(date);
      const year = d.getFullYear();
      const month = String(d.getMonth() + 1).padStart(2, '0');
      const day = String(d.getDate()).padStart(2, '0');
      return `${year}-${month}-${day}`;
    },
    formatDateDisplay(dateStr) {
      const date = new Date(dateStr);
      const month = date.getMonth() + 1;
      const day = date.getDate();
      return `${month}月${day}日`;
    },
    getWeekday(dateStr) {
      const date = new Date(dateStr);
      return this.weekdayNames[date.getDay()];
    },
    getGradeEmoji(grade) {
      const map = { 1: '🍼', 2: '👶', 3: '🎈', 4: '🎓' };
      return map[grade] || '🏫';
    }
  }
};
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=ZCOOL+KuaiLe&family=Noto+Sans+SC:wght@400;500;700&display=swap');

.page-container {
  padding: 0;
  height: 100%;
  min-height: calc(100vh - 120px);
  overflow-y: auto;
}
.page-header { text-align: center; padding: 20px 0 24px; position: relative; }
.header-decoration { position: absolute; top: 10px; left: 50%; transform: translateX(-50%); }
.deco-flower { font-size: 24px; opacity: 0.3; animation: flowerFloat 3s ease-in-out infinite; }
@keyframes flowerFloat { 0%, 100% { transform: translateY(0) rotate(0deg); } 50% { transform: translateY(-5px) rotate(10deg); } }
.page-title { font-family: 'ZCOOL KuaiLe', 'Noto Sans SC', sans-serif; font-size: 28px; color: #5D4E4E; margin: 0 0 8px; }
.page-subtitle { font-size: 14px; color: #9B8E8E; margin: 0; }
.content-card { border-radius: 20px; border: none; box-shadow: 0 8px 30px rgba(255, 182, 193, 0.15); }

.control-bar { display: flex; align-items: center; gap: 12px; padding: 16px 20px; background: linear-gradient(135deg, #FFF9F5 0%, #FFF0F0 100%); border-radius: 16px; margin-bottom: 20px; flex-wrap: wrap; }
.class-select { min-width: 140px; }
.date-navigator { display: flex; align-items: center; gap: 8px; }
.nav-btn { width: 36px; height: 36px; padding: 0; border-radius: 50%; background: linear-gradient(135deg, #FFF0F0 0%, #FFE8E8 100%); border: none; color: #FF6B6B; font-size: 12px; }
.nav-btn:hover { transform: scale(1.1); }
.current-date { text-align: center; min-width: 100px; }
.date-text { display: block; font-size: 16px; font-weight: 600; color: #5D4E4E; }
.weekday-text { display: block; font-size: 12px; color: #9B8E8E; }
.calendar-btn { width: 36px; height: 36px; padding: 0; border-radius: 50%; background: linear-gradient(135deg, #FFF0F0 0%, #FFE8E8 100%); border: none; font-size: 16px; }
.today-btn { padding: 8px 16px; font-size: 13px; }

.cute-select :deep(.el-select__wrapper) { border-radius: 12px; box-shadow: 0 0 0 1px #F0E0E0 inset; background: #fff; }
.cute-date-picker { width: 100%; }
.cute-date-picker :deep(.el-input__wrapper) { border-radius: 12px; box-shadow: 0 0 0 1px #F0E0E0 inset; background: #fff; }
.hidden-date-picker { position: absolute; opacity: 0; width: 0; height: 0; overflow: hidden; }

.recipe-cards { display: flex; flex-direction: column; gap: 16px; padding: 0 8px; }
.meal-card { background: linear-gradient(135deg, #FFFAFA 0%, #FFF5F5 100%); border-radius: 16px; border: 2px solid rgba(255, 182, 193, 0.3); overflow: hidden; transition: all 0.3s; }
.meal-card:hover { border-color: rgba(255, 107, 107, 0.4); box-shadow: 0 4px 16px rgba(255, 107, 107, 0.15); }
.meal-card.collapsed { background: linear-gradient(135deg, #FAFAFA 0%, #F5F5F5 100%); }
.meal-card.optional { border-color: rgba(180, 180, 180, 0.3); }
.meal-card.optional:hover { border-color: rgba(150, 150, 150, 0.4); }

.meal-header { display: flex; align-items: center; padding: 14px 18px; cursor: pointer; gap: 10px; }
.meal-icon { font-size: 22px; }
.meal-name { font-size: 16px; font-weight: 600; color: #5D4E4E; flex: 1; }
.required-tag { font-size: 11px; padding: 2px 8px; background: linear-gradient(135deg, #FFB5B5 0%, #FF8E8E 100%); color: #fff; border-radius: 10px; }
.toggle-icon { font-size: 12px; color: #9B8E8E; }
.meal-body { padding: 0 18px 16px; }
.cute-textarea :deep(.el-textarea__inner) { border-radius: 12px; border-color: rgba(255, 182, 193, 0.5); resize: none; font-size: 14px; line-height: 1.8; }
.cute-textarea :deep(.el-textarea__inner:focus) { border-color: #FF8E8E; box-shadow: 0 0 0 2px rgba(255, 107, 107, 0.2); }

.save-section { padding: 24px 8px 8px; text-align: center; }
.save-btn { padding: 14px 48px; font-size: 16px; border-radius: 24px; }
.cute-btn { border-radius: 12px; padding: 10px 20px; font-family: 'Noto Sans SC', sans-serif; font-weight: 500; border: none; transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1); display: inline-flex; align-items: center; gap: 6px; background: linear-gradient(135deg, #FFF0F0 0%, #FFE8E8 100%); color: #5D4E4E; }
.cute-btn:hover { transform: translateY(-2px); box-shadow: 0 6px 20px rgba(255, 107, 107, 0.2); }
.cute-btn.primary { background: linear-gradient(135deg, #FFB5B5 0%, #FF8E8E 100%); color: #fff; }
.cute-btn.primary:hover { background: linear-gradient(135deg, #FF6B6B 0%, #FF8E53 100%); box-shadow: 0 8px 25px rgba(255, 107, 107, 0.35); }

@media (max-width: 768px) {
  .page-title { font-size: 22px; }
  .control-bar { flex-direction: column; align-items: stretch; }
  .class-select { width: 100%; }
  .date-navigator { justify-content: center; }
}
</style>
