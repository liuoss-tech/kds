<template>
  <div class="page-container">
    <div class="page-header">
      <div class="header-decoration">
        <span class="deco-flower">✿</span>
      </div>
      <h2 class="page-title">考勤管理</h2>
      <p class="page-subtitle">管理幼儿每日签到签退</p>
    </div>

    <el-card shadow="never" class="content-card">
      <el-form :inline="true" :model="listQuery" class="search-form">
        <el-form-item label="班级" class="search-item">
          <el-select v-model="listQuery.classId" placeholder="请选择班级" clearable class="cute-select class-select" @change="handleFilter">
            <el-option v-for="cls in classList" :key="cls.id" :label="cls.className" :value="cls.id">
              <span>{{ getGradeEmoji(cls.grade) }} {{ cls.className }}</span>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="考勤日期" class="search-item">
          <el-date-picker
            v-model="listQuery.date"
            type="date"
            placeholder="请选择日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            :disabled-date="disabledFutureDate"
            class="cute-date-picker"
            @change="handleFilter"
          />
        </el-form-item>
        <el-form-item class="search-actions">
          <el-button type="primary" class="cute-btn primary" @click="handleFilter">
            <span>✨</span> 查询
          </el-button>
          <el-button class="cute-btn" @click="resetQuery">
            <span>🔄</span> 重置
          </el-button>
        </el-form-item>
      </el-form>

      <div class="toolbar" v-if="selectedStudents.length > 0">
        <el-button type="success" class="cute-btn primary" @click="handleBatchSignIn" :loading="batchLoading">
          <span>✅</span> 批量签到 ({{ selectedStudents.length }}人)
        </el-button>
        <el-button type="warning" class="cute-btn" @click="handleBatchSignOut" :loading="batchLoading">
          <span>🔻</span> 批量签退 ({{ selectedStudents.length }}人)
        </el-button>
        <el-button class="cute-btn" @click="clearSelection">
          <span>❌</span> 取消选择
        </el-button>
      </div>

      <el-table
        ref="tableRef"
        v-loading="listLoading"
        :data="list"
        border
        stripe
        class="cute-table"
        row-key="studentId"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="60" align="center" :selectable="checkSelectable" />
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
        <el-table-column prop="className" label="所属班级" min-width="120" align="center">
          <template #default="{ row }">
            <span class="class-name">{{ row.className || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="statusText" label="考勤状态" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" class="cute-tag" effect="plain">
              {{ row.statusText }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="signInTime" label="签到时间" width="180" align="center">
          <template #default="{ row }">
            <span v-if="row.signInTime" class="time-text sign-in">
              <span class="time-icon">🌅</span> {{ formatTime(row.signInTime) }}
            </span>
            <span v-else class="no-time">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="signOutTime" label="签退时间" width="180" align="center">
          <template #default="{ row }">
            <span v-if="row.signOutTime" class="time-text sign-out">
              <span class="time-icon">🌙</span> {{ formatTime(row.signOutTime) }}
            </span>
            <span v-else class="no-time">-</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="scope">
            <div class="action-buttons" v-if="!isLeaveStatus(scope.row)">
              <el-button
                v-if="!scope.row.signInTime"
                size="small"
                type="success"
                link
                class="action-btn sign-in"
                @click="handleSingleSignIn(scope.row)"
              >
                <span>🌅</span> 签到
              </el-button>
              <el-button
                v-if="scope.row.signInTime && !scope.row.signOutTime"
                size="small"
                type="warning"
                link
                class="action-btn sign-out"
                @click="handleSingleSignOut(scope.row)"
              >
                <span>🌙</span> 签退
              </el-button>
              <span v-if="scope.row.signInTime && scope.row.signOutTime" class="completed-text">
                <span>✅</span> 已完成
              </span>
            </div>
            <div v-else class="leave-info">
              <span class="leave-icon">🏠</span>
              <span class="leave-text">{{ scope.row.leaveTypeText }}</span>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="listQuery.page"
          :page-sizes="[10, 20, 50, 100]"
          :page-size="listQuery.size"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          background
          class="cute-pagination"
        />
      </div>
    </el-card>
  </div>
</template>

<script>
import request from '@/utils/request';
import { ElMessage } from 'element-plus';
import { ref } from 'vue';

export default {
  name: 'AttendanceManagement',
  data() {
    return {
      list: [],
      total: 0,
      listLoading: true,
      classList: [],
      selectedStudents: [],
      batchLoading: false,
      tableRef: ref(null),
      listQuery: {
        classId: undefined,
        date: this.formatDate(new Date()),
        page: 1,
        size: 10
      }
    };
  },
  created() {
    this.getClassList();
  },
  methods: {
    getClassList() {
      request.get('/api/busi/student/class-list').then(res => {
        this.classList = res.data || [];
        if (this.classList.length > 0 && !this.listQuery.classId) {
          this.listQuery.classId = this.classList[0].id;
        }
        if (this.listQuery.classId) {
          this.getList();
        }
      });
    },
    getList() {
      if (!this.listQuery.classId) {
        ElMessage.warning('请先选择班级');
        return;
      }
      this.listLoading = true;
      const params = {
        classId: this.listQuery.classId,
        date: this.listQuery.date,
        page: this.listQuery.page,
        size: this.listQuery.size
      };

      request.get('/api/busi/attendance/page', { params }).then(res => {
        this.list = res.data.records || [];
        this.total = res.data.total || 0;
      }).finally(() => {
        this.listLoading = false;
      });
    },
    handleFilter() {
      this.listQuery.page = 1;
      this.getList();
    },
    resetQuery() {
      this.listQuery = {
        classId: this.classList.length > 0 ? this.classList[0].id : undefined,
        date: this.formatDate(new Date()),
        page: 1,
        size: 10
      };
      this.getList();
    },
    handleSizeChange(val) {
      this.listQuery.size = val;
      this.getList();
    },
    handleCurrentChange(val) {
      this.listQuery.page = val;
      this.getList();
    },
    handleSelectionChange(selection) {
      this.selectedStudents = selection;
    },
    checkSelectable(row) {
      return !this.isLeaveStatus(row);
    },
    clearSelection() {
      this.$refs.tableRef?.clearSelection();
    },
    isLeaveStatus(row) {
      return row.status === 3 || row.status === 4 || row.status === 5;
    },
    getGradeEmoji(grade) {
      const map = { 1: '🍼', 2: '👶', 3: '🎈', 4: '🎓' };
      return map[grade] || '🏫';
    },
    getStatusType(status) {
      const map = {
        1: 'success',
        2: 'danger',
        3: 'warning',
        4: 'danger',
        5: 'info'
      };
      return map[status] || 'info';
    },
    formatTime(time) {
      if (!time) return '';
      if (typeof time === 'string') {
        return time.replace('T', ' ').substring(0, 19);
      }
      return time;
    },
    formatDate(date) {
      const d = new Date(date);
      const year = d.getFullYear();
      const month = String(d.getMonth() + 1).padStart(2, '0');
      const day = String(d.getDate()).padStart(2, '0');
      return `${year}-${month}-${day}`;
    },
    disabledFutureDate(date) {
      const today = new Date();
      today.setHours(0, 0, 0, 0);
      return date.getTime() > today.getTime();
    },
    handleSingleSignIn(row) {
      this.doSignIn([row.studentId]);
    },
    handleSingleSignOut(row) {
      this.doSignOut([row.studentId]);
    },
    handleBatchSignIn() {
      if (this.selectedStudents.length === 0) {
        ElMessage.warning('请先选择要签到的幼儿');
        return;
      }
      const studentIds = this.selectedStudents.map(s => s.studentId);
      this.doSignIn(studentIds);
    },
    handleBatchSignOut() {
      if (this.selectedStudents.length === 0) {
        ElMessage.warning('请先选择要签退的幼儿');
        return;
      }
      const studentIds = this.selectedStudents.map(s => s.studentId);
      this.doSignOut(studentIds);
    },
    doSignIn(studentIds) {
      this.batchLoading = true;
      request.post('/api/busi/attendance/sign-in', {
        studentIds: studentIds,
        classId: this.listQuery.classId,
        date: this.listQuery.date
      }).then(() => {
        ElMessage.success('🎉 签到成功');
        this.clearSelection();
        this.getList();
      }).finally(() => {
        this.batchLoading = false;
      });
    },
    doSignOut(studentIds) {
      this.batchLoading = true;
      request.post('/api/busi/attendance/sign-out', {
        studentIds: studentIds,
        classId: this.listQuery.classId,
        date: this.listQuery.date
      }).then(() => {
        ElMessage.success('🎉 签退成功');
        this.clearSelection();
        this.getList();
      }).finally(() => {
        this.batchLoading = false;
      });
    }
  }
};
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
.search-form { padding: 20px; background: linear-gradient(135deg, #FFF9F5 0%, #FFF0F0 100%); border-radius: 16px; margin-bottom: 20px; }
.search-item { margin-bottom: 0; }
.search-actions { margin-left: auto; }
.class-select { min-width: 160px; }
.cute-select :deep(.el-select__wrapper) { border-radius: 12px; box-shadow: 0 0 0 1px #F0E0E0 inset; background: #fff; }
.cute-date-picker { width: 160px; }
.cute-date-picker :deep(.el-input__wrapper) { border-radius: 12px; box-shadow: 0 0 0 1px #F0E0E0 inset; background: #fff; }
.cute-btn { border-radius: 12px; padding: 10px 20px; font-family: 'Noto Sans SC', sans-serif; font-weight: 500; border: none; transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1); display: inline-flex; align-items: center; gap: 6px; background: linear-gradient(135deg, #FFF0F0 0%, #FFE8E8 100%); color: #5D4E4E; }
.cute-btn:hover { transform: translateY(-2px); box-shadow: 0 6px 20px rgba(255, 107, 107, 0.2); }
.cute-btn.primary { background: linear-gradient(135deg, #FFB5B5 0%, #FF8E8E 100%); color: #fff; }
.cute-btn.primary:hover { background: linear-gradient(135deg, #FF6B6B 0%, #FF8E53 100%); box-shadow: 0 8px 25px rgba(255, 107, 107, 0.35); }
.toolbar { padding: 0 20px 20px; display: flex; gap: 12px; }
.cute-table { border-radius: 16px; overflow: hidden; }
.cute-table :deep(.el-table__header-wrapper th) { background: linear-gradient(135deg, #FFF0F0 0%, #FFE8E8 100%) !important; color: #5D4E4E; font-weight: 600; }
.cute-table :deep(.el-table__row) { transition: all 0.3s; }
.cute-table :deep(.el-table__row:hover) { background: rgba(255, 230, 230, 0.5) !important; }
.cute-table :deep(.el-table__row.is-leave) { background: #F5F5F5 !important; opacity: 0.7; }
.row-number { display: inline-flex; align-items: center; justify-content: center; width: 28px; height: 28px; background: linear-gradient(135deg, #FFF0F0 0%, #FFE8E8 100%); border-radius: 50%; font-size: 12px; font-weight: 600; color: #FF6B6B; }
.student-info { display: flex; align-items: center; justify-content: center; gap: 8px; }
.student-icon { font-size: 18px; }
.student-name, .class-name { font-weight: 600; color: #5D4E4E; }
.cute-tag { border-radius: 20px; font-weight: 500; }
.time-text { display: flex; align-items: center; gap: 4px; font-size: 13px; color: #5D4E4E; }
.time-text.sign-in { color: #67C23A; }
.time-text.sign-out { color: #E6A23C; }
.time-icon { font-size: 14px; }
.no-time { color: #CCC; font-style: italic; }
.action-buttons { display: flex; justify-content: center; gap: 8px; }
.action-btn { padding: 6px 10px; border-radius: 8px; font-size: 13px; transition: all 0.3s; }
.action-btn.sign-in { color: #67C23A; }
.action-btn.sign-out { color: #E6A23C; }
.completed-text { display: flex; align-items: center; gap: 4px; color: #67C23A; font-size: 13px; }
.leave-info { display: flex; align-items: center; justify-content: center; gap: 6px; }
.leave-icon { font-size: 16px; }
.leave-text { color: #909399; font-size: 13px; }
.pagination-container { padding: 20px; display: flex; justify-content: flex-end; }
.cute-pagination :deep(.el-pager li.is-active) { background: linear-gradient(135deg, #FFB5B5 0%, #FF8E8E 100%) !important; }

@media (max-width: 768px) {
  .page-title { font-size: 22px; }
  .search-form :deep(.el-form-item) { display: block; margin-bottom: 12px; }
  .search-actions { margin-left: 0; display: flex; gap: 8px; }
}
</style>
