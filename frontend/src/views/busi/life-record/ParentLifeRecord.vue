<template>
  <div class="page-container">
    <div class="page-header">
      <div class="header-decoration">
        <span class="deco-flower">✿</span>
      </div>
      <h2 class="page-title">🍀 生活记录</h2>
      <p class="page-subtitle">查看孩子每日生活情况</p>
    </div>

    <el-card shadow="never" class="content-card">
      <div class="control-bar">
        <el-select v-model="query.studentId" placeholder="请选择幼儿" clearable class="cute-select child-select" @change="handleStudentChange">
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

      <div v-loading="loading" class="record-card">
        <div class="record-header">
          <span class="child-icon">👶</span>
          <span class="child-name">{{ recordData.studentName || '--' }}</span>
          <span class="record-date">{{ formatRecordDate(recordData.recordDate) }}</span>
        </div>

        <div class="record-section">
          <div class="section-title">
            <span class="section-icon">🍽️</span>
            <span>饮食情况</span>
          </div>
          <div class="section-content">
            <div class="info-row">
              <span class="info-label">午餐摄入量：</span>
              <span class="info-value">{{ recordData.lunchIntakeText || '--' }}</span>
            </div>
            <div class="info-row">
              <span class="info-label">喝水次数：</span>
              <span class="info-value">{{ recordData.waterCount != null ? recordData.waterCount + '次' : '--' }}</span>
            </div>
          </div>
        </div>

        <div class="record-section">
          <div class="section-title">
            <span class="section-icon">😴</span>
            <span>午睡情况</span>
          </div>
          <div class="section-content">
            <div class="info-row">
              <span class="info-label">入睡时间：</span>
              <span class="info-value">{{ formatSleepTime(recordData.sleepStartTime) }}</span>
            </div>
            <div class="info-row">
              <span class="info-label">睡眠时长：</span>
              <span class="info-value">{{ recordData.sleepDuration != null ? recordData.sleepDuration + '分钟' : '--' }}</span>
            </div>
            <div class="info-row">
              <span class="info-label">睡眠质量：</span>
              <span class="info-value">{{ recordData.sleepQualityText || '--' }}</span>
            </div>
          </div>
        </div>

        <div class="record-section">
          <div class="section-title">
            <span class="section-icon">🚽</span>
            <span>如厕情况</span>
          </div>
          <div class="section-content">
            <div class="info-row">
              <span class="info-label">如厕次数：</span>
              <span class="info-value">{{ recordData.toiletCount != null ? recordData.toiletCount + '次' : '--' }}</span>
            </div>
            <div class="info-row">
              <span class="info-label">异常情况：</span>
              <span class="info-value">{{ recordData.toiletAbnormal || '--' }}</span>
            </div>
          </div>
        </div>

        <div class="record-section">
          <div class="section-title">
            <span class="section-icon">🌡️</span>
            <span>健康监测</span>
          </div>
          <div class="section-content">
            <div class="info-row">
              <span class="info-label">晨检体温：</span>
              <span class="info-value">{{ recordData.morningTemp != null ? recordData.morningTemp + '℃' : '--' }}</span>
            </div>
            <div class="info-row">
              <span class="info-label">午检体温：</span>
              <span class="info-value">{{ recordData.afternoonTemp != null ? recordData.afternoonTemp + '℃' : '--' }}</span>
            </div>
            <div class="info-row">
              <span class="info-label">不适症状：</span>
              <span class="info-value">{{ recordData.healthSymptoms || '--' }}</span>
            </div>
          </div>
        </div>

        <div class="record-section">
          <div class="section-title">
            <span class="section-icon">📝</span>
            <span>备注</span>
          </div>
          <div class="section-content remark-content">
            <span class="info-value">{{ recordData.teacherRemark || '暂无备注' }}</span>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script>
import { getParentLifeRecord, getParentChildren } from '@/api/lifeRecord';
import { ElMessage } from 'element-plus';

export default {
  name: 'ParentLifeRecord',
  data() {
    return {
      childrenList: [],
      query: {
        studentId: undefined,
        date: this.formatDate(new Date())
      },
      currentDate: this.formatDate(new Date()),
      recordData: {
        studentName: '',
        className: '',
        recordDate: null,
        lunchIntake: null,
        lunchIntakeText: '',
        waterCount: null,
        sleepStartTime: '',
        sleepDuration: null,
        sleepQuality: null,
        sleepQualityText: '',
        toiletCount: null,
        toiletAbnormal: '',
        morningTemp: null,
        afternoonTemp: null,
        healthSymptoms: '',
        teacherRemark: ''
      },
      weekdayNames: ['周日', '周一', '周二', '周三', '周四', '周五', '周六'],
      datePickerRef: null,
      loading: false
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
          this.query.studentId = this.childrenList[0].studentId;
          this.loadRecord();
        }
      });
    },
    loadRecord() {
      if (!this.query.studentId) {
        ElMessage.warning('请先选择幼儿');
        return;
      }
      this.loading = true;
      const params = {
        studentId: this.query.studentId,
        date: this.query.date
      };
      getParentLifeRecord(params).then(res => {
        const data = res.data || {};
        this.recordData = {
          studentName: data.studentName || '',
          className: data.className || '',
          recordDate: data.recordDate || this.query.date,
          lunchIntake: data.lunchIntake,
          lunchIntakeText: data.lunchIntakeText || '',
          waterCount: data.waterCount,
          sleepStartTime: data.sleepStartTime || '',
          sleepDuration: data.sleepDuration,
          sleepQuality: data.sleepQuality,
          sleepQualityText: data.sleepQualityText || '',
          toiletCount: data.toiletCount,
          toiletAbnormal: data.toiletAbnormal || '',
          morningTemp: data.morningTemp,
          afternoonTemp: data.afternoonTemp,
          healthSymptoms: data.healthSymptoms || '',
          teacherRemark: data.teacherRemark || ''
        };
      }).catch(() => {
        this.resetRecordData();
      }).finally(() => {
        this.loading = false;
      });
    },
    resetRecordData() {
      this.recordData = {
        studentName: this.childrenList.find(c => c.studentId === this.query.studentId)?.studentName || '',
        className: this.childrenList.find(c => c.studentId === this.query.studentId)?.className || '',
        recordDate: this.query.date,
        lunchIntake: null,
        lunchIntakeText: '',
        waterCount: null,
        sleepStartTime: '',
        sleepDuration: null,
        sleepQuality: null,
        sleepQualityText: '',
        toiletCount: null,
        toiletAbnormal: '',
        morningTemp: null,
        afternoonTemp: null,
        healthSymptoms: '',
        teacherRemark: ''
      };
    },
    handleStudentChange() {
      this.loadRecord();
    },
    handleDateChange() {
      this.query.date = this.currentDate;
      this.loadRecord();
    },
    openDatePicker() {
      this.$refs.datePickerRef?.handleFocus();
    },
    prevDay() {
      const date = new Date(this.currentDate);
      date.setDate(date.getDate() - 1);
      this.currentDate = this.formatDate(date);
      this.query.date = this.currentDate;
      this.loadRecord();
    },
    nextDay() {
      const date = new Date(this.currentDate);
      date.setDate(date.getDate() + 1);
      this.currentDate = this.formatDate(date);
      this.query.date = this.currentDate;
      this.loadRecord();
    },
    goToToday() {
      this.currentDate = this.formatDate(new Date());
      this.query.date = this.currentDate;
      this.loadRecord();
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
    formatRecordDate(dateStr) {
      if (!dateStr) return '';
      const date = new Date(dateStr);
      const month = date.getMonth() + 1;
      const day = date.getDate();
      return `${month}月${day}日`;
    },
    formatSleepTime(timeStr) {
      if (!timeStr) return '--';
      if (timeStr.length >= 5) {
        return timeStr.substring(0, 5);
      }
      return timeStr;
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
.child-select { min-width: 180px; }
.date-navigator { display: flex; align-items: center; gap: 8px; }
.nav-btn { width: 36px; height: 36px; padding: 0; border-radius: 50%; background: linear-gradient(135deg, #FFF0F0 0%, #FFE8E8 100%); border: none; color: #FF6B6B; font-size: 12px; }
.nav-btn:hover { transform: scale(1.1); }
.current-date { text-align: center; min-width: 100px; }
.date-text { display: block; font-size: 16px; font-weight: 600; color: #5D4E4E; }
.weekday-text { display: block; font-size: 12px; color: #9B8E8E; }
.calendar-btn { width: 36px; height: 36px; padding: 0; border-radius: 50%; background: linear-gradient(135deg, #FFF0F0 0%, #FFE8E8 100%); border: none; font-size: 16px; }
.today-btn { padding: 8px 16px; font-size: 13px; }

.cute-select :deep(.el-select__wrapper) { border-radius: 12px; box-shadow: 0 0 0 1px #F0E0E0 inset; background: #fff; }
.hidden-date-picker { position: absolute; opacity: 0; width: 0; height: 0; overflow: hidden; }

.record-card { background: linear-gradient(135deg, #FFFAFA 0%, #FFF5F5 100%); border-radius: 16px; border: 2px solid rgba(255, 182, 193, 0.3); overflow: hidden; }
.record-header { display: flex; align-items: center; padding: 16px 20px; background: linear-gradient(135deg, #FFF0F0 0%, #FFE8E8 100%); border-bottom: 1px solid rgba(255, 182, 193, 0.2); gap: 10px; }
.child-icon { font-size: 24px; }
.child-name { font-size: 18px; font-weight: 600; color: #5D4E4E; flex: 1; }
.record-date { font-size: 14px; color: #9B8E8E; }

.record-section { padding: 16px 20px; border-bottom: 1px solid rgba(255, 182, 193, 0.15); }
.record-section:last-child { border-bottom: none; }
.section-title { display: flex; align-items: center; gap: 8px; margin-bottom: 12px; font-weight: 600; color: #5D4E4E; font-size: 15px; }
.section-icon { font-size: 18px; }
.section-content { padding-left: 26px; }
.info-row { display: flex; align-items: center; margin-bottom: 8px; }
.info-row:last-child { margin-bottom: 0; }
.info-label { color: #9B8E8E; font-size: 14px; min-width: 80px; }
.info-value { color: #5D4E4E; font-size: 14px; }
.remark-content { padding: 12px; background: rgba(255, 255, 255, 0.6); border-radius: 12px; }

.cute-btn { border-radius: 12px; padding: 10px 20px; font-family: 'Noto Sans SC', sans-serif; font-weight: 500; border: none; transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1); display: inline-flex; align-items: center; gap: 6px; background: linear-gradient(135deg, #FFF0F0 0%, #FFE8E8 100%); color: #5D4E4E; }
.cute-btn:hover { transform: translateY(-2px); box-shadow: 0 6px 20px rgba(255, 107, 107, 0.2); }

@media (max-width: 768px) {
  .page-title { font-size: 22px; }
  .control-bar { flex-direction: column; align-items: stretch; }
  .child-select { width: 100%; }
  .date-navigator { justify-content: center; }
}
</style>
