<template>
  <div class="page-container">
    <div class="page-header">
      <div class="header-decoration">
        <span class="deco-flower">✿</span>
      </div>
      <h2 class="page-title">📊 考勤情况统计</h2>
      <p class="page-subtitle">查看班级出勤率与考勤状态分布</p>
    </div>

    <el-card shadow="never" class="content-card">
      <div class="control-bar" :class="{ 'loading': isLoading }">
        <el-radio-group v-model="timeDimension" @change="handleDimensionChange" class="time-radio">
          <el-radio-button label="today">
            <span class="radio-icon">📅</span> 本日
          </el-radio-button>
          <el-radio-button label="week">
            <span class="radio-icon">📆</span> 本周
          </el-radio-button>
          <el-radio-button label="month">
            <span class="radio-icon">🗓️</span> 本月
          </el-radio-button>
          <el-radio-button label="custom">
            <span class="radio-icon">📝</span> 自定义
          </el-radio-button>
        </el-radio-group>

        <transition name="slide-fade">
          <div v-if="timeDimension === 'custom'" class="date-range-picker">
            <el-date-picker
              v-model="customDateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
              :clearable="false"
              :disabled-date="disabledFutureDate"
              @change="handleCustomDateChange"
              class="cute-date-picker"
            />
          </div>
        </transition>

        <transition name="slide-fade">
          <el-select
            v-if="showClassFilter"
            v-model="query.classId"
            placeholder="全部班级"
            clearable
            class="class-select cute-select"
            @change="handleQuery"
          >
            <el-option 
              v-for="cls in classList" 
              :key="cls.id" 
              :label="getGradeEmoji(cls.grade) + ' ' + cls.className" 
              :value="cls.id" 
            />
          </el-select>
        </transition>
      </div>

      <transition name="shake">
        <el-alert
          v-if="isWeekend"
          title="周末无考勤记录"
          type="warning"
          :closable="false"
          show-icon
          class="weekend-alert"
        >
          所选日期范围均为周末，不进行考勤统计。
        </el-alert>
      </transition>

      <el-row :gutter="20" class="stat-row">
        <el-col :span="12">
          <div class="stat-card overview-card" :class="{ 'card-animated': animateOverview }">
            <div class="stat-card-header">
              <span class="stat-icon">📈</span>
              <span class="stat-title">本期出勤率统计</span>
            </div>
            <div class="rate-display">
              <transition name="count-up" appear>
                <span class="big-rate">{{ animatedRate }}</span>
              </transition>
              <span class="rate-unit">%</span>
            </div>
            <div class="stat-detail-grid">
              <div class="stat-detail-item" v-for="(item, index) in detailItems" :key="item.label" :style="{ '--delay': index * 0.1 + 's' }">
                <span class="detail-icon">{{ item.icon }}</span>
                <span class="detail-label">{{ item.label }}</span>
                <span class="detail-value" :class="item.class">{{ animatedDetails[item.key] || 0 }}{{ item.unit }}</span>
              </div>
            </div>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="stat-card chart-card">
            <div class="stat-card-header">
              <span class="stat-icon">🥧</span>
              <span class="stat-title">考勤状态分布</span>
            </div>
            <div ref="pieChartRef" class="chart-container pie-chart" @mouseenter="pauseAnimation" @mouseleave="resumeAnimation"></div>
            <div class="legend-container">
              <div 
                v-for="(item, index) in overviewData.statusDistribution" 
                :key="item.status" 
                class="legend-item"
                :style="{ '--delay': index * 0.05 + 's' }"
              >
                <span class="legend-dot" :style="{ background: statusColors[item.status] }"></span>
                <span class="legend-text">{{ item.statusText }} {{ item.rate || 0 }}%</span>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>

      <el-row :gutter="20" class="stat-row">
        <el-col :span="12">
          <div class="stat-card" :class="{ 'card-animated': animateCharts }">
            <div class="stat-card-header">
              <span class="stat-icon">🏆</span>
              <span class="stat-title">各班出勤率统计</span>
            </div>
            <div ref="barChartRef" class="chart-container bar-chart"></div>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="stat-card" :class="{ 'card-animated': animateCharts }">
            <div class="stat-card-header">
              <span class="stat-icon">📉</span>
              <span class="stat-title">出勤趋势图</span>
            </div>
            <div ref="lineChartRef" class="chart-container line-chart"></div>
          </div>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script>
import * as echarts from 'echarts';
import { getAttendanceOverview, getAttendanceClassRateList, getAttendanceTrend } from '@/api/attendance-stat';
import { ElMessage } from 'element-plus';
import request from '@/utils/request';

export default {
  name: 'AttendanceStat',
  data() {
    return {
      timeDimension: 'today',
      customDateRange: [],
      isLoading: false,
      query: {
        startDate: '',
        endDate: '',
        classId: null
      },
      showClassFilter: true,
      classList: [],
      overviewData: {
        totalShouldAttend: 0,
        totalPresent: 0,
        totalAbsent: 0,
        totalLeave: 0,
        attendanceRate: 0,
        statusDistribution: []
      },
      classRateData: [],
      trendData: [],
      pieChart: null,
      barChart: null,
      lineChart: null,
      animatedRate: 0,
      animatedDetails: {
        totalShouldAttend: 0,
        totalPresent: 0,
        totalAbsent: 0,
        totalLeave: 0
      },
      animateOverview: false,
      animateCharts: false,
      statusColors: {
        1: '#6BCB77',
        2: '#FF6B6B',
        3: '#FFD93D',
        4: '#909399',
        5: '#C9B1FF'
      },
      isWeekend: false,
      chartAnimationTimer: null
    };
  },
  computed: {
    detailItems() {
      return [
        { label: '应到', key: 'totalShouldAttend', unit: '人次', icon: '📋', class: '' },
        { label: '出勤', key: 'totalPresent', unit: '人次', icon: '✅', class: 'present' },
        { label: '缺勤', key: 'totalAbsent', unit: '人次', icon: '❌', class: 'absent' },
        { label: '请假', key: 'totalLeave', unit: '人次', icon: '📝', class: 'leave' }
      ];
    }
  },
  mounted() {
    this.animateOverview = true;
    this.checkUserRole();
    this.initDateRange();
    this.fetchData();
  },
  beforeUnmount() {
    this.disposeCharts();
    if (this.chartAnimationTimer) {
      clearTimeout(this.chartAnimationTimer);
    }
  },
  methods: {
    checkUserRole() {
      request.get('/api/auth/info').then(res => {
        const roleCode = res.data?.roleCode;
        const isParent = roleCode === 'PARENT';
        this.showClassFilter = !isParent;
        this.getClassList();
        this.handleQuery();
      });
    },
    getClassList() {
      request.get('/api/busi/teacher-class/my-classes').then(res => {
        this.classList = res.data || [];
      });
    },
    initDateRange() {
      this.updateDateRange();
    },
    updateDateRange() {
      const today = new Date();
      const todayStr = this.formatDate(today);

      if (this.timeDimension === 'today') {
        this.query.startDate = todayStr;
        this.query.endDate = todayStr;
      } else if (this.timeDimension === 'week') {
        const monday = new Date(today);
        const day = monday.getDay();
        const diff = monday.getDate() - day + (day === 0 ? -6 : 1);
        monday.setDate(diff);
        this.query.startDate = this.formatDate(monday);
        this.query.endDate = todayStr;
      } else if (this.timeDimension === 'month') {
        const firstDay = new Date(today.getFullYear(), today.getMonth(), 1);
        this.query.startDate = this.formatDate(firstDay);
        this.query.endDate = todayStr;
      } else if (this.timeDimension === 'custom' && this.customDateRange.length === 2) {
        this.query.startDate = this.customDateRange[0];
        this.query.endDate = this.customDateRange[1];
      }
    },
    handleDimensionChange() {
      this.animateOverview = false;
      this.animateCharts = false;
      this.updateDateRange();
      this.fetchData();
      setTimeout(() => {
        this.animateOverview = true;
      }, 100);
    },
    handleCustomDateChange() {
      this.animateOverview = false;
      this.animateCharts = false;
      this.updateDateRange();
      this.fetchData();
      setTimeout(() => {
        this.animateOverview = true;
      }, 100);
    },
    handleQuery() {
      this.animateOverview = false;
      this.animateCharts = false;
      this.fetchData();
      setTimeout(() => {
        this.animateOverview = true;
      }, 100);
    },
    fetchData() {
      if (!this.query.startDate || !this.query.endDate) {
        return;
      }
      this.isLoading = true;
      this.updateWeekendStatus();
      
      Promise.all([
        this.fetchOverview(),
        this.fetchClassRate(),
        this.fetchTrend()
      ]).finally(() => {
        this.isLoading = false;
        this.chartAnimationTimer = setTimeout(() => {
          this.animateCharts = true;
        }, 300);
      });
    },
    fetchOverview() {
      return getAttendanceOverview(this.query).then(res => {
        const data = res.data || {};
        this.overviewData = data;
        this.animateNumbers(data);
        setTimeout(() => {
          this.renderPieChart();
        }, 100);
      }).catch(() => {
        ElMessage.error('获取考勤概览失败');
      });
    },
    fetchClassRate() {
      return getAttendanceClassRateList(this.query).then(res => {
        this.classRateData = res.data || [];
        this.$nextTick(() => {
          this.renderBarChart();
        });
      }).catch(() => {
        ElMessage.error('获取班级出勤率失败');
      });
    },
    fetchTrend() {
      return getAttendanceTrend(this.query).then(res => {
        this.trendData = res.data || [];
        this.$nextTick(() => {
          this.renderLineChart();
        });
      }).catch(() => {
        ElMessage.error('获取出勤趋势失败');
      });
    },
    animateNumbers(data) {
      const duration = 1000;
      const steps = 60;
      const interval = duration / steps;
      
      const targetRate = data.attendanceRate || 0;
      const targets = {
        totalShouldAttend: data.totalShouldAttend || 0,
        totalPresent: data.totalPresent || 0,
        totalAbsent: data.totalAbsent || 0,
        totalLeave: data.totalLeave || 0
      };
      
      let currentStep = 0;
      const timer = setInterval(() => {
        currentStep++;
        const progress = this.easeOutQuart(currentStep / steps);
        
        this.animatedRate = Math.round(targetRate * progress);
        
        for (const key in targets) {
          this.animatedDetails[key] = Math.round(targets[key] * progress);
        }
        
        if (currentStep >= steps) {
          clearInterval(timer);
          this.animatedRate = targetRate;
          for (const key in targets) {
            this.animatedDetails[key] = targets[key];
          }
        }
      }, interval);
    },
    easeOutQuart(t) {
      return 1 - Math.pow(1 - t, 4);
    },
    renderPieChart() {
      if (!this.$refs.pieChartRef) return;
      
      if (!this.pieChart) {
        this.pieChart = echarts.init(this.$refs.pieChartRef);
      }
      
      const data = this.overviewData.statusDistribution || [];
      const pieData = data.map(item => ({
        name: item.statusText,
        value: item.count || 0
      }));
      
      const hasData = pieData.some(item => item.value > 0);
      
      const option = {
        tooltip: {
          trigger: 'item',
          formatter: '{b}: {c} ({d}%)',
          backgroundColor: 'rgba(255, 255, 255, 0.95)',
          borderColor: '#FFB5B5',
          borderWidth: 1,
          textStyle: {
            color: '#5D4E4E'
          }
        },
        color: [this.statusColors[1], this.statusColors[2], this.statusColors[3], this.statusColors[4], this.statusColors[5]],
        series: [{
          type: 'pie',
          radius: hasData ? ['45%', '75%'] : ['0%', '0%'],
          avoidLabelOverlap: false,
          itemStyle: {
            borderRadius: 12,
            borderColor: '#fff',
            borderWidth: 3,
            shadowBlur: 10,
            shadowColor: 'rgba(255, 107, 107, 0.2)'
          },
          label: {
            show: false
          },
          emphasis: {
            scale: true,
            scaleSize: 10,
            label: {
              show: true,
              fontSize: 14,
              fontWeight: 'bold',
              color: '#5D4E4E'
            },
            itemStyle: {
              shadowBlur: 20,
              shadowColor: 'rgba(255, 107, 107, 0.4)'
            }
          },
          animationType: 'scale',
          animationEasing: 'elasticOut',
          animationDelay: (idx) => idx * 100,
          data: pieData
        }]
      };
      
      this.pieChart.setOption(option, true);
      
      this.$nextTick(() => {
        if (this.pieChart) {
          this.pieChart.resize();
        }
      });
    },
    renderBarChart() {
      if (!this.$refs.barChartRef) return;
      
      if (!this.barChart) {
        this.barChart = echarts.init(this.$refs.barChartRef);
      }
      
      const data = this.classRateData || [];
      const xData = data.map(item => item.className);
      const yData = data.map(item => item.attendanceRate);
      
      const option = {
        tooltip: {
          trigger: 'axis',
          formatter: '{b}: {c}%',
          backgroundColor: 'rgba(255, 255, 255, 0.95)',
          borderColor: '#FFB5B5',
          borderWidth: 1,
          textStyle: {
            color: '#5D4E4E'
          }
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          top: '10%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: xData,
          axisLine: {
            lineStyle: {
              color: '#F0E0E0'
            }
          },
          axisLabel: {
            interval: 0,
            rotate: 0,
            fontSize: 11,
            color: '#9B8E8E'
          }
        },
        yAxis: {
          type: 'value',
          min: 0,
          max: 100,
          axisLine: {
            show: false
          },
          axisLabel: {
            formatter: '{value}%',
            color: '#9B8E8E'
          },
          splitLine: {
            lineStyle: {
              color: 'rgba(255, 181, 181, 0.2)'
            }
          }
        },
        series: [{
          type: 'bar',
          data: yData.map((value, index) => ({
            value,
            itemStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: index % 2 === 0 ? '#6BCB77' : '#4D96FF' },
                { offset: 1, color: index % 2 === 0 ? '#95D475' : '#7FB2F0' }
              ]),
              borderRadius: [8, 8, 0, 0],
              shadowColor: 'rgba(107, 203, 119, 0.3)',
              shadowBlur: 8,
              shadowOffsetY: 4
            }
          })),
          barWidth: '50%',
          showBackground: true,
          backgroundStyle: {
            color: 'rgba(255, 230, 230, 0.3)',
            borderRadius: [8, 8, 0, 0]
          },
          animationDelay: (idx) => idx * 100,
          animationEasing: 'elasticOut'
        }]
      };
      
      this.barChart.setOption(option);
    },
    renderLineChart() {
      if (!this.$refs.lineChartRef) return;
      
      if (!this.lineChart) {
        this.lineChart = echarts.init(this.$refs.lineChartRef);
      }
      
      const data = this.trendData || [];
      const xData = data.map(item => item.date ? item.date.substring(5) : '');
      const yData = data.map(item => item.attendanceRate);
      
      const option = {
        tooltip: {
          trigger: 'axis',
          formatter: '{b}: {c}%',
          backgroundColor: 'rgba(255, 255, 255, 0.95)',
          borderColor: '#FFB5B5',
          borderWidth: 1,
          textStyle: {
            color: '#5D4E4E'
          }
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          top: '10%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: xData,
          boundaryGap: false,
          axisLine: {
            lineStyle: {
              color: '#F0E0E0'
            }
          },
          axisLabel: {
            interval: 0,
            rotate: 0,
            fontSize: 11,
            color: '#9B8E8E'
          }
        },
        yAxis: {
          type: 'value',
          min: 0,
          max: 100,
          axisLine: {
            show: false
          },
          axisLabel: {
            formatter: '{value}%',
            color: '#9B8E8E'
          },
          splitLine: {
            lineStyle: {
              color: 'rgba(255, 181, 181, 0.2)'
            }
          }
        },
        series: [{
          type: 'line',
          data: yData,
          smooth: 0.4,
          symbol: 'circle',
          symbolSize: 10,
          lineStyle: {
            color: '#6BCB77',
            width: 4,
            shadowColor: 'rgba(107, 203, 119, 0.3)',
            shadowBlur: 10,
            shadowOffsetY: 5
          },
          itemStyle: {
            color: '#6BCB77',
            borderColor: '#fff',
            borderWidth: 3,
            shadowColor: 'rgba(107, 203, 119, 0.4)',
            shadowBlur: 8
          },
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: 'rgba(107, 203, 119, 0.35)' },
              { offset: 0.5, color: 'rgba(107, 203, 119, 0.1)' },
              { offset: 1, color: 'rgba(107, 203, 119, 0.02)' }
            ])
          },
          animationDelay: (idx) => idx * 50,
          animationDuration: 1500,
          animationEasing: 'cubicOut'
        }]
      };
      
      this.lineChart.setOption(option);
    },
    pauseAnimation() {
      if (this.pieChart) {
        this.pieChart.dispatchAction({ type: 'pauseAnimation' });
      }
    },
    resumeAnimation() {
      if (this.pieChart) {
        this.pieChart.dispatchAction({ type: 'resumeAnimation' });
      }
    },
    disposeCharts() {
      if (this.pieChart) {
        this.pieChart.dispose();
        this.pieChart = null;
      }
      if (this.barChart) {
        this.barChart.dispose();
        this.barChart = null;
      }
      if (this.lineChart) {
        this.lineChart.dispose();
        this.lineChart = null;
      }
    },
    formatDate(date) {
      const d = new Date(date);
      const year = d.getFullYear();
      const month = String(d.getMonth() + 1).padStart(2, '0');
      const day = String(d.getDate()).padStart(2, '0');
      return `${year}-${month}-${day}`;
    },
    isAllWeekends(startDate, endDate) {
      const start = new Date(startDate);
      const end = new Date(endDate);
      let allWeekend = true;
      let current = new Date(start);

      while (current <= end) {
        const day = current.getDay();
        if (day !== 0 && day !== 6) {
          allWeekend = false;
          break;
        }
        current.setDate(current.getDate() + 1);
      }
      return allWeekend;
    },
    updateWeekendStatus() {
      if (this.query.startDate && this.query.endDate) {
        this.isWeekend = this.isAllWeekends(this.query.startDate, this.query.endDate);
      } else {
        this.isWeekend = false;
      }
    },
    disabledFutureDate(date) {
      return date > new Date();
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

.page-header {
  text-align: center;
  padding: 20px 0 24px;
  position: relative;
}

.header-decoration {
  position: absolute;
  top: 10px;
  left: 50%;
  transform: translateX(-50%);
}

.deco-flower {
  font-size: 24px;
  opacity: 0.3;
  animation: flowerFloat 3s ease-in-out infinite;
}

@keyframes flowerFloat {
  0%, 100% { transform: translateY(0) rotate(0deg); }
  50% { transform: translateY(-5px) rotate(10deg); }
}

.page-title {
  font-family: 'ZCOOL KuaiLe', 'Noto Sans SC', sans-serif;
  font-size: 28px;
  color: #5D4E4E;
  margin: 0 0 8px;
}

.page-subtitle {
  font-size: 14px;
  color: #9B8E8E;
  margin: 0;
}

.content-card {
  border-radius: 20px;
  border: none;
  box-shadow: 0 8px 30px rgba(255, 182, 193, 0.15);
}

.control-bar {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px 24px;
  background: linear-gradient(135deg, #FFF9F5 0%, #FFF0F0 100%);
  border-radius: 16px;
  margin-bottom: 20px;
  flex-wrap: wrap;
  transition: all 0.3s;
}

.control-bar.loading {
  opacity: 0.7;
  pointer-events: none;
}

.time-radio {
  display: flex;
  gap: 8px;
}

.time-radio :deep(.el-radio-button__inner) {
  border-radius: 12px !important;
  border: none !important;
  background: rgba(255, 255, 255, 0.8);
  color: #5D4E4E;
  padding: 10px 16px;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.time-radio :deep(.el-radio-button__original-radio:checked + .el-radio-button__inner) {
  background: linear-gradient(135deg, #FFB5B5 0%, #FF8E8E 100%);
  color: #fff;
  box-shadow: 0 6px 20px rgba(255, 107, 107, 0.3);
  transform: translateY(-2px);
}

.time-radio :deep(.el-radio-button:hover .el-radio-button__inner) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 107, 107, 0.2);
}

.radio-icon {
  margin-right: 4px;
}

.date-range-picker {
  display: flex;
  align-items: center;
  gap: 8px;
}

.cute-date-picker :deep(.el-input__wrapper) {
  border-radius: 12px;
  box-shadow: 0 0 0 1px #F0E0E0 inset;
  background: #fff;
  transition: all 0.3s;
}

.cute-date-picker :deep(.el-input__wrapper:hover),
.cute-date-picker :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px rgba(255, 107, 107, 0.2), 0 0 0 1px #FFB5B5 inset;
}

.class-select {
  min-width: 140px;
}

.cute-select :deep(.el-select__wrapper) {
  border-radius: 12px;
  box-shadow: 0 0 0 1px #F0E0E0 inset;
  background: #fff;
  transition: all 0.3s;
}

.cute-select :deep(.el-select__wrapper:hover),
.cute-select :deep(.el-select__wrapper.is-focused) {
  box-shadow: 0 0 0 2px rgba(255, 107, 107, 0.2), 0 0 0 1px #FFB5B5 inset;
}

.slide-fade-enter-active {
  transition: all 0.3s ease-out;
}

.slide-fade-leave-active {
  transition: all 0.2s ease-in;
}

.slide-fade-enter-from,
.slide-fade-leave-to {
  transform: translateX(10px);
  opacity: 0;
}

.shake-enter-active {
  animation: shake 0.5s ease-in-out;
}

@keyframes shake {
  0%, 100% { transform: translateX(0); }
  25% { transform: translateX(-5px); }
  75% { transform: translateX(5px); }
}

.weekend-alert {
  margin-bottom: 20px;
  border-radius: 12px;
  background: linear-gradient(135deg, #FFF8E1 0%, #FFECB3 100%);
  border: 1px solid #FFD93D;
}

.stat-row {
  margin-bottom: 20px;
}

.stat-card {
  background: linear-gradient(135deg, #FFFAFA 0%, #FFF5F5 100%);
  border-radius: 20px;
  border: 2px solid rgba(255, 182, 193, 0.3);
  padding: 16px;
  min-height: 320px;
  transition: all 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
  opacity: 0;
  transform: translateY(20px);
}

.stat-card.card-animated {
  opacity: 1;
  transform: translateY(0);
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 40px rgba(255, 107, 107, 0.2);
  border-color: rgba(255, 182, 193, 0.5);
}

.stat-card.card-animated:hover {
  transform: translateY(-4px);
}

.stat-card-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 20px;
}

.stat-icon {
  font-size: 24px;
  animation: iconBounce 2s ease-in-out infinite;
}

@keyframes iconBounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-3px); }
}

.stat-title {
  font-size: 16px;
  font-weight: 600;
  color: #5D4E4E;
}

.overview-card {
  display: flex;
  flex-direction: column;
}

.rate-display {
  display: flex;
  align-items: baseline;
  justify-content: center;
  margin-bottom: 24px;
}

.big-rate {
  font-family: 'ZCOOL KuaiLe', sans-serif;
  font-size: 72px;
  color: #6BCB77;
  font-weight: bold;
  line-height: 1;
  text-shadow: 0 4px 20px rgba(107, 203, 119, 0.3);
}

.rate-unit {
  font-size: 28px;
  color: #6BCB77;
  margin-left: 4px;
}

.count-up-enter-active {
  transition: all 1s ease-out;
}

.count-up-enter-from {
  opacity: 0;
  transform: scale(0.5);
}

.stat-detail-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.stat-detail-item {
  background: #fff;
  border-radius: 16px;
  padding: 16px;
  text-align: center;
  transition: all 0.3s;
  opacity: 0;
  transform: translateY(10px);
  animation: detailSlideIn 0.5s ease-out forwards;
  animation-delay: var(--delay);
}

@keyframes detailSlideIn {
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.stat-card.card-animated .stat-detail-item {
  opacity: 1;
}

.stat-detail-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 25px rgba(255, 107, 107, 0.15);
}

.detail-icon {
  font-size: 20px;
  display: block;
  margin-bottom: 4px;
}

.detail-label {
  display: block;
  font-size: 12px;
  color: #9B8E8E;
  margin-bottom: 4px;
}

.detail-value {
  font-size: 20px;
  font-weight: 700;
  color: #5D4E4E;
  transition: all 0.3s;
}

.detail-value.present {
  color: #6BCB77;
}

.detail-value.absent {
  color: #FF6B6B;
}

.detail-value.leave {
  color: #FFD93D;
}

.chart-card {
  display: flex;
  flex-direction: column;
  min-height: 320px;
  opacity: 1 !important;
  transform: translateY(0) !important;
}

.chart-container {
  flex: 1;
  min-height: 220px;
  position: relative;
}

.pie-chart {
  width: 100%;
  height: 220px;
  cursor: pointer;
}

.bar-chart {
  height: 220px;
}

.line-chart {
  height: 220px;
}

.legend-container {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  justify-content: center;
  margin-top: 16px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 20px;
  transition: all 0.3s;
  opacity: 0;
  animation: legendFadeIn 0.5s ease-out forwards;
  animation-delay: var(--delay);
}

@keyframes legendFadeIn {
  from {
    opacity: 0;
    transform: scale(0.8);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

.stat-card.card-animated .legend-item {
  opacity: 1;
}

.legend-item:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.legend-dot {
  width: 14px;
  height: 14px;
  border-radius: 50%;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.legend-text {
  font-size: 13px;
  color: #5D4E4E;
  font-weight: 500;
}

@media (max-width: 768px) {
  .page-title {
    font-size: 22px;
  }
  .control-bar {
    flex-direction: column;
    align-items: stretch;
  }
  .time-radio {
    justify-content: center;
  }
  .date-range-picker {
    justify-content: center;
  }
  .class-select {
    width: 100%;
  }
  .stat-row .el-col {
    margin-bottom: 16px;
  }
  .big-rate {
    font-size: 56px;
  }
}

@media (prefers-reduced-motion: reduce) {
  *,
  *::before,
  *::after {
    animation: none !important;
    transition: none !important;
  }
}
</style>
