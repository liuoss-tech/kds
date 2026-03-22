<template>
  <div class="dashboard-container">
    <div class="welcome-section">
      <div class="welcome-card">
        <div class="welcome-decoration">
          <span class="deco-dot"></span>
          <span class="deco-dot"></span>
          <span class="deco-dot"></span>
        </div>
        <div class="welcome-content">
          <h1 class="welcome-title">🎉 欢迎来到 KDS 幼儿园</h1>
          <p class="welcome-subtitle">家校联系系统</p>
        </div>
        <div class="welcome-illustration">
          <span class="illustration">🌻</span>
        </div>
      </div>
    </div>

    <el-row :gutter="24" class="info-row">
      <el-col :span="12">
        <div class="info-card role-card">
          <div class="card-icon">👤</div>
          <div class="card-content">
            <span class="card-label">您的身份</span>
            <span class="card-value">{{ roleName }}</span>
          </div>
          <div class="card-decoration"></div>
        </div>
      </el-col>
      <el-col :span="12">
        <div class="info-card date-card">
          <div class="card-icon">📅</div>
          <div class="card-content">
            <span class="card-label">今日日期</span>
            <span class="card-value">{{ currentDate }}</span>
          </div>
          <div class="card-decoration"></div>
        </div>
      </el-col>
    </el-row>

    <el-card class="notice-card" shadow="hover" v-if="userType !== 2">
      <template #header>
        <div class="notice-header">
          <span class="notice-icon">📢</span>
          <span>系统公告</span>
        </div>
      </template>
      <el-alert
        title="系统提示"
        type="success"
        description="您的权限菜单已加载完毕，请通过左侧菜单进行业务操作。"
        :closable="false"
        show-icon
        class="custom-alert"
      >
      </el-alert>
    </el-card>

    <el-card class="notice-card" shadow="hover" v-else>
      <template #header>
        <div class="notice-header">
          <span class="notice-icon">👶</span>
          <span>今日考勤</span>
          <span class="attendance-date">{{ currentDate }}</span>
        </div>
      </template>
      <div v-if="children.length > 0">
        <el-tabs v-model="activeChildId" @tab-change="handleChildChange">
          <el-tab-pane
            v-for="child in children"
            :key="child.id"
            :label="child.studentName"
            :name="child.id"
          >
          </el-tab-pane>
        </el-tabs>
        <div class="attendance-content" v-if="currentAttendance">
          <el-row :gutter="20">
            <el-col :span="12">
              <div class="attendance-item">
                <div class="attendance-label">班级</div>
                <div class="attendance-value">{{ currentAttendance.className || '-' }}</div>
              </div>
            </el-col>
            <el-col :span="12">
              <div class="attendance-item">
                <div class="attendance-label">考勤状态</div>
                <div class="attendance-value" :class="getStatusClass(currentAttendance.status)">
                  {{ currentAttendance.statusText || '未考勤' }}
                </div>
              </div>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <div class="attendance-item">
                <div class="attendance-label">到校时间</div>
                <div class="attendance-value time-value">
                  {{ formatTime(currentAttendance.signInTime) }}
                </div>
              </div>
            </el-col>
            <el-col :span="12">
              <div class="attendance-item">
                <div class="attendance-label">离校时间</div>
                <div class="attendance-value time-value">
                  {{ formatTime(currentAttendance.signOutTime) }}
                </div>
              </div>
            </el-col>
          </el-row>
          <el-row :gutter="20" v-if="currentAttendance.leaveTypeText">
            <el-col :span="24">
              <div class="attendance-item">
                <div class="attendance-label">请假类型</div>
                <div class="attendance-value leave-value">
                  {{ currentAttendance.leaveTypeText }}
                </div>
              </div>
            </el-col>
          </el-row>
        </div>
        <div v-else class="no-attendance">
          <el-empty description="暂无考勤记录" :image-size="60" />
        </div>
      </div>
      <div v-else class="no-children">
        <el-empty description="您还未绑定幼儿信息" :image-size="60" />
      </div>
    </el-card>

    <el-row :gutter="24" class="quick-actions" v-if="userType !== 2">
      <el-col
        v-for="(menu, index) in quickMenus"
        :key="menu.path"
        :span="8"
      >
        <el-card
          class="action-card"
          shadow="hover"
          :style="{ animationDelay: `${index * 0.1}s` }"
          @click="$router.push(menu.path)"
        >
          <div class="action-content">
            <div class="action-icon">{{ menu.icon }}</div>
            <div class="action-text">
              <span class="action-title">{{ menu.title }}</span>
              <span class="action-desc">{{ menu.desc }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import request from '../utils/request';

export default {
  name: 'Dashboard',
  data() {
    return {
      roleName: '',
      userType: null,
      currentDate: new Date().toLocaleDateString('zh-CN', {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        weekday: 'long'
      }),
      quickMenus: [],
      children: [],
      activeChildId: null,
      attendanceList: []
    };
  },
  computed: {
    currentAttendance() {
      if (!this.activeChildId) return null;
      return this.attendanceList.find(a => a.studentId === this.activeChildId);
    }
  },
  mounted() {
    this.fetchUserInfo();
  },
  methods: {
    async fetchUserInfo() {
      try {
        const res = await request.get('/api/auth/info');
        if (res.data) {
          this.roleName = res.data.roleName;
          this.userType = res.data.userType;
          if (this.userType === 2) {
            this.fetchChildren();
          }
        }
      } catch (error) {
        console.error('获取用户信息失败:', error);
        this.roleName = '未知角色';
      }
    },
    async fetchChildren() {
      try {
        const res = await request.get('/api/busi/leave/students');
        if (res.data && res.data.length > 0) {
          this.children = res.data;
          this.activeChildId = this.children[0].id;
          this.fetchAttendanceList();
        }
      } catch (error) {
        console.error('获取幼儿列表失败:', error);
      }
    },
    async fetchAttendanceList() {
      if (!this.children || this.children.length === 0) return;
      const today = new Date().toISOString().split('T')[0];
      try {
        const promises = this.children.map(child =>
          request.get('/api/busi/attendance/parent/list', {
            params: { studentId: child.id, date: today }
          })
        );
        const results = await Promise.all(promises);
        this.attendanceList = results.map(res => res.data).filter(Boolean);
      } catch (error) {
        console.error('获取考勤记录失败:', error);
      }
    },
    handleChildChange(childId) {
      this.activeChildId = childId;
    },
    formatTime(time) {
      if (!time) return '-';
      if (typeof time === 'string') {
        const date = new Date(time);
        return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' });
      }
      return time;
    },
    getStatusClass(status) {
      if (!status) return 'status-none';
      switch (status) {
        case 1: return 'status-normal';
        case 2: return 'status-absent';
        case 3: return 'status-leave';
        case 4: return 'status-leave';
        case 5: return 'status-leave';
        default: return 'status-none';
      }
    }
  }
};
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=ZCOOL+KuaiLe&family=Noto+Sans+SC:wght@400;500;700&display=swap');

.dashboard-container {
  max-width: 1200px;
  margin: 0 auto;
}

.welcome-section {
  margin-bottom: 24px;
}

.welcome-card {
  background: linear-gradient(135deg, #FF6B6B 0%, #FF8E53 100%);
  border-radius: 24px;
  padding: 40px;
  position: relative;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(255, 107, 107, 0.3);
}

.welcome-card::before {
  content: '';
  position: absolute;
  width: 200%;
  height: 200%;
  background: url("data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cg fill='%23ffffff' fill-opacity='0.08'%3E%3Cpath d='M36 34v-4h-2v4h-4v2h4v4h2v-4h4v-2h-4zm0-30V0h-2v4h-4v2h4v4h2V6h4V4h-4zM6 34v-4H4v4H0v2h4v4h2v-4h4v-2H6zM6 4V0H4v4H0v2h4v4h2V6h4V4H6z'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E");
  animation: patternMove 30s linear infinite;
}

@keyframes patternMove {
  0% { transform: translate(0, 0); }
  100% { transform: translate(-30px, -30px); }
}

.welcome-decoration {
  position: absolute;
  top: 20px;
  right: 30px;
  display: flex;
  gap: 8px;
}

.deco-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.3);
  animation: pulse 2s ease-in-out infinite;
}

.deco-dot:nth-child(2) { animation-delay: 0.3s; }
.deco-dot:nth-child(3) { animation-delay: 0.6s; }

@keyframes pulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.3); }
}

.welcome-content {
  position: relative;
  z-index: 1;
}

.welcome-title {
  font-family: 'ZCOOL KuaiLe', 'Noto Sans SC', sans-serif;
  font-size: 36px;
  color: #fff;
  margin: 0 0 8px 0;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.1);
  animation: slideUp 0.6s ease-out;
}

.welcome-subtitle {
  font-family: 'Noto Sans SC', sans-serif;
  font-size: 20px;
  color: rgba(255, 255, 255, 0.9);
  margin: 0;
  font-weight: 500;
  animation: slideUp 0.6s ease-out 0.1s backwards;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.welcome-illustration {
  position: absolute;
  right: 40px;
  bottom: 20px;
  font-size: 80px;
  animation: float 3s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

.info-row {
  margin-bottom: 24px;
}

.info-card {
  border-radius: 20px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  position: relative;
  overflow: hidden;
  transition: all 0.3s ease;
}

.info-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.1);
}

.role-card {
  background: linear-gradient(135deg, #FFF5F5 0%, #FFE8E8 100%);
  border: 2px solid #FFB5B5;
}

.date-card {
  background: linear-gradient(135deg, #F0F9FF 0%, #E0F2FE 100%);
  border: 2px solid #BAE6FD;
}

.card-icon {
  font-size: 32px;
  width: 56px;
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 16px;
  background: #fff;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.role-card .card-icon { background: #FFF0F0; }
.date-card .card-icon { background: #F0F9FF; }

.card-content {
  display: flex;
  flex-direction: column;
}

.card-label {
  font-size: 14px;
  color: #9CA3AF;
  margin-bottom: 4px;
}

.card-value {
  font-family: 'ZCOOL KuaiLe', 'Noto Sans SC', sans-serif;
  font-size: 20px;
  font-weight: 600;
  color: #5D4E4E;
}

.role-card .card-value { color: #FF6B6B; }
.date-card .card-value { color: #0EA5E9; }

.card-decoration {
  position: absolute;
  right: -20px;
  bottom: -20px;
  width: 100px;
  height: 100px;
  border-radius: 50%;
  opacity: 0.1;
}

.role-card .card-decoration { background: #FF6B6B; }
.date-card .card-decoration { background: #0EA5E9; }

.notice-card {
  border-radius: 20px;
  margin-bottom: 24px;
  border: none;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.05);
}

.notice-header {
  font-family: 'Noto Sans SC', sans-serif;
  font-size: 18px;
  font-weight: 600;
  color: #5D4E4E;
  display: flex;
  align-items: center;
  gap: 8px;
}

.notice-icon {
  font-size: 20px;
}

.custom-alert {
  border-radius: 12px !important;
  background: linear-gradient(135deg, #F0FDF4 0%, #DCFCE7 100%) !important;
  border: 1px solid #86EFAC !important;
}

.quick-actions {
  margin-top: 8px;
}

.action-card {
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  border: none;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.05);
  animation: fadeInUp 0.5s ease-out backwards;
  margin-bottom: 16px;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.action-card:hover {
  transform: translateY(-8px) scale(1.02);
  box-shadow: 0 20px 50px rgba(255, 107, 107, 0.2);
}

.action-content {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 8px;
}

.action-icon {
  font-size: 36px;
  width: 64px;
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #FFF5F5 0%, #FFE8E8 100%);
  border-radius: 16px;
  transition: transform 0.3s ease;
}

.action-card:hover .action-icon {
  transform: scale(1.1) rotate(5deg);
}

.action-text {
  display: flex;
  flex-direction: column;
}

.action-title {
  font-family: 'Noto Sans SC', sans-serif;
  font-size: 16px;
  font-weight: 600;
  color: #5D4E4E;
}

.action-desc {
  font-size: 13px;
  color: #9CA3AF;
  margin-top: 4px;
}

@media (max-width: 768px) {
  .welcome-card {
    padding: 24px;
  }

  .welcome-title {
    font-size: 24px;
  }

  .welcome-subtitle {
    font-size: 16px;
  }

  .welcome-illustration {
    display: none;
  }

  .el-col {
    margin-bottom: 16px;
  }
}

.attendance-date {
  font-size: 14px;
  color: #9CA3AF;
  font-weight: normal;
  margin-left: 12px;
}

.attendance-content {
  padding: 16px 8px;
}

.attendance-item {
  margin-bottom: 16px;
}

.attendance-label {
  font-size: 13px;
  color: #9CA3AF;
  margin-bottom: 4px;
}

.attendance-value {
  font-size: 16px;
  font-weight: 500;
  color: #5D4E4E;
}

.attendance-value.time-value {
  font-family: 'ZCOOL KuaiLe', 'Noto Sans SC', sans-serif;
  font-size: 20px;
  color: #0EA5E9;
}

.attendance-value.leave-value {
  color: #F59E0B;
}

.status-normal {
  color: #10B981 !important;
}

.status-absent {
  color: #EF4444 !important;
}

.status-leave {
  color: #F59E0B !important;
}

.status-none {
  color: #9CA3AF !important;
}

.no-attendance,
.no-children {
  padding: 20px 0;
}
</style>
