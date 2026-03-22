<template>
  <div class="page-container">
    <div class="page-header">
      <div class="header-decoration">
        <span class="deco-flower">✿</span>
      </div>
      <h2 class="page-title">🍽️ 食谱查看</h2>
      <p class="page-subtitle">查看孩子班级的每日食谱</p>
    </div>

    <el-card shadow="never" class="content-card">
      <div class="control-bar">
        <el-select v-model="query.studentId" placeholder="请选择幼儿" clearable class="cute-select class-select" @change="handleStudentChange">
          <el-option v-for="child in childrenList" :key="child.studentId" :label="child.studentName + '（' + child.className + '）'" :value="child.studentId" />
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
        <div class="meal-card">
          <div class="meal-header">
            <span class="meal-icon">🌅</span>
            <span class="meal-name">早餐</span>
          </div>
          <div class="meal-body">
            <div class="content-display">{{ recipeData.breakfast || '（暂无内容）' }}</div>
          </div>
        </div>

        <div class="meal-card">
          <div class="meal-header">
            <span class="meal-icon">🍎</span>
            <span class="meal-name">早点</span>
          </div>
          <div class="meal-body">
            <div class="content-display">{{ recipeData.morningSnack || '（暂无内容）' }}</div>
          </div>
        </div>

        <div class="meal-card">
          <div class="meal-header">
            <span class="meal-icon">🍚</span>
            <span class="meal-name">午餐</span>
          </div>
          <div class="meal-body">
            <div class="content-display">{{ recipeData.lunch || '（暂无内容）' }}</div>
          </div>
        </div>

        <div class="meal-card">
          <div class="meal-header">
            <span class="meal-icon">🍪</span>
            <span class="meal-name">午点</span>
          </div>
          <div class="meal-body">
            <div class="content-display">{{ recipeData.afternoonSnack || '（暂无内容）' }}</div>
          </div>
        </div>

        <div class="meal-card optional">
          <div class="meal-header">
            <span class="meal-icon">🌙</span>
            <span class="meal-name">晚餐</span>
          </div>
          <div class="meal-body">
            <div class="content-display">{{ recipeData.dinner || '（暂无内容）' }}</div>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script>
import { getParentRecipe, getParentChildren } from '@/api/recipe';
import { ElMessage } from 'element-plus';

export default {
  name: 'ParentRecipe',
  data() {
    return {
      childrenList: [],
      query: {
        studentId: undefined,
        date: this.formatDate(new Date())
      },
      currentDate: this.formatDate(new Date()),
      recipeData: {
        breakfast: '',
        morningSnack: '',
        lunch: '',
        afternoonSnack: '',
        dinner: ''
      },
      weekdayNames: ['周日', '周一', '周二', '周三', '周四', '周五', '周六'],
      datePickerRef: null
    };
  },
  created() {
    this.getChildrenList();
  },
  methods: {
    getChildrenList() {
      getParentChildren().then(res => {
        this.childrenList = res.data || [];
        if (this.childrenList.length > 0 && !this.query.studentId) {
          this.query.studentId = this.childrenList[0].id;
          this.loadRecipe();
        }
      });
    },
    loadRecipe() {
      if (!this.query.studentId) {
        ElMessage.warning('请先选择幼儿');
        return;
      }
      const params = {
        studentId: this.query.studentId,
        date: this.query.date
      };
      getParentRecipe(params).then(res => {
        const data = res.data || {};
        this.recipeData.breakfast = data.breakfast || '';
        this.recipeData.morningSnack = data.morningSnack || '';
        this.recipeData.lunch = data.lunch || '';
        this.recipeData.afternoonSnack = data.afternoonSnack || '';
        this.recipeData.dinner = data.dinner || '';
      }).catch(() => {
        this.resetRecipeData();
      });
    },
    resetRecipeData() {
      this.recipeData = {
        breakfast: '',
        morningSnack: '',
        lunch: '',
        afternoonSnack: '',
        dinner: ''
      };
    },
    handleStudentChange() {
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
.class-select { min-width: 180px; }
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
.meal-card.optional { border-color: rgba(180, 180, 180, 0.3); }
.meal-card.optional:hover { border-color: rgba(150, 150, 150, 0.4); }

.meal-header { display: flex; align-items: center; padding: 14px 18px; gap: 10px; }
.meal-icon { font-size: 22px; }
.meal-name { font-size: 16px; font-weight: 600; color: #5D4E4E; flex: 1; }
.meal-body { padding: 0 18px 16px; }
.content-display { font-size: 14px; line-height: 1.8; color: #5D4E4E; padding: 12px; background: rgba(255, 255, 255, 0.6); border-radius: 12px; min-height: 60px; }

.cute-btn { border-radius: 12px; padding: 10px 20px; font-family: 'Noto Sans SC', sans-serif; font-weight: 500; border: none; transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1); display: inline-flex; align-items: center; gap: 6px; background: linear-gradient(135deg, #FFF0F0 0%, #FFE8E8 100%); color: #5D4E4E; }
.cute-btn:hover { transform: translateY(-2px); box-shadow: 0 6px 20px rgba(255, 107, 107, 0.2); }

@media (max-width: 768px) {
  .page-title { font-size: 22px; }
  .control-bar { flex-direction: column; align-items: stretch; }
  .class-select { width: 100%; }
  .date-navigator { justify-content: center; }
}
</style>
