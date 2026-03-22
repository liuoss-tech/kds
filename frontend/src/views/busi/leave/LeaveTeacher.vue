<template>
  <div class="page-container">
    <div class="page-header">
      <div class="header-decoration">
        <span class="deco-flower">✿</span>
      </div>
      <h2 class="page-title">请假审批</h2>
      <p class="page-subtitle">审批本班幼儿的请假申请</p>
    </div>

    <el-card shadow="never" class="content-card">
      <el-form :inline="true" :model="listQuery" class="search-form">
        <el-form-item label="审批状态" class="search-item">
          <el-select v-model="listQuery.status" placeholder="请选择状态" clearable class="cute-select status-select">
            <el-option label="全部状态" :value="undefined" />
            <el-option label="待审批" :value="0">
              <span>⏳ 待审批</span>
            </el-option>
            <el-option label="已通过" :value="1">
              <span>✅ 已通过</span>
            </el-option>
            <el-option label="已拒绝" :value="4">
              <span>❌ 已拒绝</span>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="请假类型" class="search-item">
          <el-select v-model="listQuery.leaveType" placeholder="请选择类型" clearable class="cute-select type-select">
            <el-option label="全部类型" :value="undefined" />
            <el-option label="事假" :value="1" />
            <el-option label="病假" :value="2" />
            <el-option label="其他" :value="3" />
          </el-select>
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

      <el-table
        v-loading="listLoading"
        :data="list"
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
        <el-table-column prop="className" label="所属班级" min-width="120" align="center">
          <template #default="{ row }">
            <span class="class-name">{{ row.className || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="leaveTypeText" label="请假类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getLeaveTypeTag(row.leaveType)" class="cute-tag" effect="plain">
              {{ row.leaveTypeText }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="startDate" label="开始日期" width="120" align="center">
          <template #default="{ row }">
            <span class="date-text">{{ row.startDate }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="endDate" label="结束日期" width="120" align="center">
          <template #default="{ row }">
            <span class="date-text">{{ row.endDate }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="leaveDays" label="请假天数" width="100" align="center">
          <template #default="{ row }">
            <span class="days-text">{{ row.leaveDays }}天</span>
          </template>
        </el-table-column>
        <el-table-column prop="reason" label="请假原因" min-width="150" align="center">
          <template #default="{ row }">
            <el-tooltip :content="row.reason" placement="top" v-if="row.reason">
              <span class="reason-text">{{ row.reason }}</span>
            </el-tooltip>
            <span v-else class="no-reason">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="auditStatus" label="审批状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.auditStatus)" class="cute-tag" effect="plain">
              {{ row.auditStatusText }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="parentUserName" label="申请人" width="100" align="center">
          <template #default="{ row }">
            <span class="applicant-name">{{ row.parentUserName || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="申请时间" min-width="160" align="center">
          <template #default="{ row }">
            <span class="time-text">{{ row.createTime }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" align="center" fixed="right">
          <template #default="scope">
            <div class="action-buttons" v-if="scope.row.auditStatus === 0">
              <el-button size="small" type="success" link class="action-btn approve" @click="handleApprove(scope.row, 1)">
                <span>✅</span> 通过
              </el-button>
              <el-button size="small" type="danger" link class="action-btn reject" @click="handleReject(scope.row)">
                <span>❌</span> 拒绝
              </el-button>
            </div>
            <span v-else class="no-action">-</span>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="listQuery.pageNum"
          :page-sizes="[10, 20, 50, 100]"
          :page-size="listQuery.pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          background
          class="cute-pagination"
        />
      </div>
    </el-card>

    <el-dialog v-model="rejectDialogVisible" title="❌ 拒绝请假" width="450px" class="cute-dialog">
      <div class="reject-info">
        <div class="reject-student">
          <span class="reject-icon">👶</span>
          <span class="reject-name">{{ currentLeave?.studentName }}</span>
          <span class="reject-type">({{ currentLeave?.leaveTypeText }})</span>
        </div>
        <div class="reject-dates">
          <span>{{ currentLeave?.startDate }} 至 {{ currentLeave?.endDate }} ({{ currentLeave?.leaveDays }}天)</span>
        </div>
      </div>
      <el-form ref="rejectForm" :model="rejectForm" :rules="rejectRules" label-width="100px" class="cute-form">
        <el-form-item label="审批意见" prop="teacherRemark">
          <el-input
            v-model="rejectForm.teacherRemark"
            type="textarea"
            :rows="4"
            placeholder="请填写审批意见"
            class="cute-input"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button class="cute-btn" @click="rejectDialogVisible = false">取 消</el-button>
          <el-button type="danger" class="cute-btn danger" @click="submitReject" :loading="rejectLoading">
            <span>✨</span> 确 定
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import request from '@/utils/request';
import { ElMessage, ElMessageBox } from 'element-plus';

export default {
  name: 'LeaveTeacher',
  data() {
    return {
      list: [],
      total: 0,
      listLoading: true,
      listQuery: {
        pageNum: 1,
        pageSize: 10,
        status: undefined,
        leaveType: undefined
      },

      rejectDialogVisible: false,
      rejectLoading: false,
      currentLeave: null,
      rejectForm: {
        id: undefined,
        auditStatus: 4,
        teacherRemark: ''
      },
      rejectRules: {
        teacherRemark: [{ required: true, message: '请填写审批意见', trigger: 'blur' }]
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    getList() {
      this.listLoading = true;
      const params = { ...this.listQuery };
      if (params.leaveType === '') params.leaveType = undefined;
      if (params.status === '') params.status = undefined;

      request.get('/api/busi/leave/teacher-page', { params }).then(res => {
        this.list = res.data.records || [];
        this.total = res.data.total || 0;
      }).finally(() => {
        this.listLoading = false;
      });
    },
    handleFilter() {
      this.listQuery.pageNum = 1;
      this.getList();
    },
    resetQuery() {
      this.listQuery = { pageNum: 1, pageSize: 10, status: undefined, leaveType: undefined };
      this.getList();
    },
    handleSizeChange(val) {
      this.listQuery.pageSize = val;
      this.getList();
    },
    handleCurrentChange(val) {
      this.listQuery.pageNum = val;
      this.getList();
    },

    getLeaveTypeTag(type) {
      const map = { 1: '', 2: 'danger', 3: 'info' };
      return map[type] || '';
    },
    getStatusType(status) {
      const map = { 0: 'warning', 1: 'success', 2: 'warning', 3: 'success', 4: 'danger', 5: 'info' };
      return map[status] || 'info';
    },

    handleApprove(row, status) {
      ElMessageBox.confirm(
        `确认通过 [${row.studentName}] 的 ${row.leaveTypeText} 申请吗？`,
        '✅ 确认审批',
        {
          confirmButtonText: '确认通过',
          cancelButtonText: '取消',
          type: 'success'
        }
      ).then(() => {
        request.post('/api/busi/leave/approve', {
          id: row.id,
          auditStatus: status
        }).then(() => {
          ElMessage.success('🎉 审批通过');
          this.getList();
        });
      }).catch(() => {});
    },
    handleReject(row) {
      this.currentLeave = row;
      this.rejectForm = {
        id: row.id,
        auditStatus: 4,
        teacherRemark: ''
      };
      this.rejectDialogVisible = true;
    },
    submitReject() {
      this.$refs.rejectForm.validate((valid) => {
        if (valid) {
          this.rejectLoading = true;
          request.post('/api/busi/leave/approve', this.rejectForm).then(() => {
            ElMessage.success('🎉 已拒绝该请假申请');
            this.rejectDialogVisible = false;
            this.getList();
          }).finally(() => {
            this.rejectLoading = false;
          });
        }
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
.status-select, .type-select { min-width: 140px; }
.cute-input :deep(.el-textarea__inner) { border-radius: 12px; }
.cute-select :deep(.el-select__wrapper) { border-radius: 12px; box-shadow: 0 0 0 1px #F0E0E0 inset; background: #fff; }
.cute-btn { border-radius: 12px; padding: 10px 20px; font-family: 'Noto Sans SC', sans-serif; font-weight: 500; border: none; transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1); display: inline-flex; align-items: center; gap: 6px; background: linear-gradient(135deg, #FFF0F0 0%, #FFE8E8 100%); color: #5D4E4E; }
.cute-btn:hover { transform: translateY(-2px); box-shadow: 0 6px 20px rgba(255, 107, 107, 0.2); }
.cute-btn.primary { background: linear-gradient(135deg, #FFB5B5 0%, #FF8E8E 100%); color: #fff; }
.cute-btn.primary:hover { background: linear-gradient(135deg, #FF6B6B 0%, #FF8E53 100%); box-shadow: 0 8px 25px rgba(255, 107, 107, 0.35); }
.cute-btn.danger { background: linear-gradient(135deg, #FF6B6B 0%, #FF8E53 100%); color: #fff; }
.cute-table { border-radius: 16px; overflow: hidden; }
.cute-table :deep(.el-table__header-wrapper th) { background: linear-gradient(135deg, #FFF0F0 0%, #FFE8E8 100%) !important; color: #5D4E4E; font-weight: 600; }
.cute-table :deep(.el-table__row) { transition: all 0.3s; }
.cute-table :deep(.el-table__row:hover) { background: rgba(255, 230, 230, 0.5) !important; }
.row-number { display: inline-flex; align-items: center; justify-content: center; width: 28px; height: 28px; background: linear-gradient(135deg, #FFF0F0 0%, #FFE8E8 100%); border-radius: 50%; font-size: 12px; font-weight: 600; color: #FF6B6B; }
.student-info { display: flex; align-items: center; justify-content: center; gap: 8px; }
.student-icon { font-size: 18px; }
.student-name, .class-name { font-weight: 600; color: #5D4E4E; }
.cute-tag { border-radius: 20px; font-weight: 500; }
.date-text, .time-text, .days-text { color: #5D4E4E; font-size: 13px; }
.reason-text { display: inline-block; max-width: 150px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; cursor: pointer; }
.no-reason { color: #CCC; font-style: italic; }
.applicant-name { color: #5D4E4E; font-size: 13px; }
.action-buttons { display: flex; justify-content: center; gap: 8px; }
.action-btn { padding: 6px 10px; border-radius: 8px; font-size: 13px; transition: all 0.3s; }
.no-action { color: #CCC; }
.pagination-container { padding: 20px; display: flex; justify-content: flex-end; }
.cute-pagination :deep(.el-pager li.is-active) { background: linear-gradient(135deg, #FFB5B5 0%, #FF8E8E 100%) !important; }
.cute-dialog :deep(.el-dialog__header) { background: linear-gradient(135deg, #FFF0F0 0%, #FFE8E8 100%); padding: 20px; }
.cute-dialog :deep(.el-dialog__title) { font-family: 'ZCOOL KuaiLe', sans-serif; color: #5D4E4E; }
.dialog-footer { display: flex; justify-content: flex-end; gap: 12px; }
.reject-info { text-align: center; padding: 10px 0 20px; border-bottom: 1px dashed #FFE8E8; margin-bottom: 20px; }
.reject-student { display: flex; align-items: center; justify-content: center; gap: 8px; margin-bottom: 8px; }
.reject-icon { font-size: 24px; }
.reject-name { font-size: 20px; font-weight: 600; color: #FF6B6B; }
.reject-type { font-size: 14px; color: #9B8E8E; }
.reject-dates { font-size: 14px; color: #5D4E4E; }

@media (max-width: 768px) {
  .page-title { font-size: 22px; }
  .search-form :deep(.el-form-item) { display: block; margin-bottom: 12px; }
  .search-actions { margin-left: 0; display: flex; gap: 8px; }
}
</style>
