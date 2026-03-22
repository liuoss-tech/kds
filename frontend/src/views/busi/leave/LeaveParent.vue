<template>
  <div class="page-container">
    <div class="page-header">
      <div class="header-decoration">
        <span class="deco-flower">✿</span>
      </div>
      <h2 class="page-title">请假申请</h2>
      <p class="page-subtitle">为您的孩子提交请假申请</p>
    </div>

    <el-card shadow="never" class="content-card">
      <div class="toolbar">
        <el-button type="primary" class="cute-btn primary add-btn" @click="handleAdd">
          <span>➕</span> 新增请假
        </el-button>
      </div>

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
        <el-table-column prop="createTime" label="申请时间" min-width="160" align="center">
          <template #default="{ row }">
            <span class="time-text">{{ row.createTime }}</span>
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

    <el-dialog v-model="dialogVisible" title="📝 新增请假" width="520px" @close="resetForm" class="cute-dialog">
      <div class="dialog-decoration">
        <span class="deco-flower">📋</span>
      </div>
      <el-form ref="dataForm" :model="form" :rules="rules" label-width="100px" class="cute-form">
        <el-form-item label="选择幼儿" prop="studentId">
          <el-select v-model="form.studentId" placeholder="请选择幼儿" style="width: 100%;" class="cute-select">
            <el-option
              v-for="student in students"
              :key="student.id"
              :label="`${student.studentName} (${student.className})`"
              :value="student.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="请假类型" prop="leaveType">
          <el-select v-model="form.leaveType" placeholder="请选择请假类型" style="width: 100%;" class="cute-select">
            <el-option label="🏖️ 事假" :value="1" />
            <el-option label="🤒 病假" :value="2" />
            <el-option label="📌 其他" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="开始日期" prop="startDate">
          <el-date-picker
            v-model="form.startDate"
            type="date"
            placeholder="选择开始日期"
            style="width: 100%;"
            value-format="YYYY-MM-DD"
            class="cute-picker"
          />
        </el-form-item>
        <el-form-item label="结束日期" prop="endDate">
          <el-date-picker
            v-model="form.endDate"
            type="date"
            placeholder="选择结束日期"
            style="width: 100%;"
            value-format="YYYY-MM-DD"
            class="cute-picker"
          />
        </el-form-item>
        <el-form-item label="请假原因" prop="reason">
          <el-input
            v-model="form.reason"
            type="textarea"
            :rows="3"
            placeholder="请输入请假原因"
            class="cute-input"
          />
        </el-form-item>
        <el-form-item label="证明材料">
          <el-input
            v-model="form.proofUrl"
            placeholder="请输入证明材料URL（可选）"
            class="cute-input"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button class="cute-btn" @click="dialogVisible = false">取 消</el-button>
          <el-button type="primary" class="cute-btn primary" @click="submitData" :loading="submitLoading">
            <span>✨</span> 提 交
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
  name: 'LeaveParent',
  data() {
    return {
      list: [],
      total: 0,
      listLoading: true,
      students: [],
      listQuery: {
        pageNum: 1,
        pageSize: 10,
        status: undefined,
        leaveType: undefined
      },

      dialogVisible: false,
      submitLoading: false,
      form: {
        studentId: undefined,
        leaveType: undefined,
        startDate: '',
        endDate: '',
        reason: '',
        proofUrl: ''
      },
      rules: {
        studentId: [{ required: true, message: '请选择幼儿', trigger: 'change' }],
        leaveType: [{ required: true, message: '请选择请假类型', trigger: 'change' }],
        startDate: [{ required: true, message: '请选择开始日期', trigger: 'change' }],
        endDate: [{ required: true, message: '请选择结束日期', trigger: 'change' }],
        reason: [{ required: true, message: '请填写请假原因', trigger: 'blur' }]
      }
    };
  },
  created() {
    this.getMyStudents();
    this.getList();
  },
  methods: {
    getMyStudents() {
      request.get('/api/busi/leave/students').then(res => {
        this.students = res.data || [];
        if (this.students.length === 0) {
          ElMessage.warning('您还没有绑定幼儿，无法提交请假');
        }
      });
    },
    getList() {
      this.listLoading = true;
      const params = { ...this.listQuery };
      if (params.leaveType === '') params.leaveType = undefined;
      if (params.status === '') params.status = undefined;

      request.get('/api/busi/leave/page', { params }).then(res => {
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

    handleAdd() {
      if (this.students.length === 0) {
        ElMessage.warning('您还没有绑定幼儿，无法提交请假');
        return;
      }
      this.form = {
        studentId: undefined,
        leaveType: undefined,
        startDate: '',
        endDate: '',
        reason: '',
        proofUrl: ''
      };
      this.dialogVisible = true;
    },
    submitData() {
      this.$refs.dataForm.validate((valid) => {
        if (valid) {
          if (this.form.startDate > this.form.endDate) {
            ElMessage.warning('结束日期不能早于开始日期');
            return;
          }
          this.submitLoading = true;
          request.post('/api/busi/leave/add', this.form).then(() => {
            ElMessage.success('🎉 请假申请已提交');
            this.dialogVisible = false;
            this.getList();
          }).finally(() => {
            this.submitLoading = false;
          });
        }
      });
    },

    resetForm() {
      this.$refs.dataForm?.clearValidate();
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
.cute-picker { width: 100%; }
.cute-picker :deep(.el-input__wrapper) { border-radius: 12px; box-shadow: 0 0 0 1px #F0E0E0 inset; background: #fff; }
.cute-btn { border-radius: 12px; padding: 10px 20px; font-family: 'Noto Sans SC', sans-serif; font-weight: 500; border: none; transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1); display: inline-flex; align-items: center; gap: 6px; background: linear-gradient(135deg, #FFF0F0 0%, #FFE8E8 100%); color: #5D4E4E; }
.cute-btn:hover { transform: translateY(-2px); box-shadow: 0 6px 20px rgba(255, 107, 107, 0.2); }
.cute-btn.primary { background: linear-gradient(135deg, #FFB5B5 0%, #FF8E8E 100%); color: #fff; }
.cute-btn.primary:hover { background: linear-gradient(135deg, #FF6B6B 0%, #FF8E53 100%); box-shadow: 0 8px 25px rgba(255, 107, 107, 0.35); }
.toolbar { padding: 0 20px 20px; display: flex; gap: 12px; }
.add-btn { padding: 12px 24px; font-size: 15px; }
.cute-table { border-radius: 16px; overflow: hidden; }
.cute-table :deep(.el-table__header-wrapper th) { background: linear-gradient(135deg, #FFF0F0 0%, #FFE8E8 100%) !important; color: #5D4E4E; font-weight: 600; }
.cute-table :deep(.el-table__row) { transition: all 0.3s; }
.cute-table :deep(.el-table__row:hover) { background: rgba(255, 230, 230, 0.5) !important; }
.row-number { display: inline-flex; align-items: center; justify-content: center; width: 28px; height: 28px; background: linear-gradient(135deg, #FFF0F0 0%, #FFE8E8 100%); border-radius: 50%; font-size: 12px; font-weight: 600; color: #FF6B6B; }
.student-info { display: flex; align-items: center; justify-content: center; gap: 8px; }
.student-icon { font-size: 18px; }
.student-name { font-weight: 600; color: #5D4E4E; }
.cute-tag { border-radius: 20px; font-weight: 500; }
.date-text, .time-text, .days-text { color: #5D4E4E; font-size: 13px; }
.reason-text { display: inline-block; max-width: 150px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; cursor: pointer; }
.no-reason { color: #CCC; font-style: italic; }
.pagination-container { padding: 20px; display: flex; justify-content: flex-end; }
.cute-pagination :deep(.el-pager li.is-active) { background: linear-gradient(135deg, #FFB5B5 0%, #FF8E8E 100%) !important; }
.cute-dialog :deep(.el-dialog__header) { background: linear-gradient(135deg, #FFF0F0 0%, #FFE8E8 100%); padding: 20px; }
.cute-dialog :deep(.el-dialog__title) { font-family: 'ZCOOL KuaiLe', sans-serif; color: #5D4E4E; }
.dialog-footer { display: flex; justify-content: flex-end; gap: 12px; }
.dialog-decoration { text-align: center; margin-bottom: 20px; }

@media (max-width: 768px) {
  .page-title { font-size: 22px; }
  .search-form :deep(.el-form-item) { display: block; margin-bottom: 12px; }
  .search-actions { margin-left: 0; display: flex; gap: 8px; }
}
</style>
