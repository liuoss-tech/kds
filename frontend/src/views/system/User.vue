<template>
  <div class="page-container">
    <div class="page-header">
      <div class="header-decoration">
        <span class="deco-flower">✿</span>
      </div>
      <h2 class="page-title">账号管理</h2>
      <p class="page-subtitle">管理系统用户账号</p>
    </div>

    <el-card shadow="never" class="content-card">
      <el-form :inline="true" :model="listQuery" class="search-form">
        <el-form-item label="真实姓名" class="search-item">
          <el-input 
            v-model="listQuery.realName" 
            placeholder="请输入姓名" 
            clearable 
            @keyup.enter="handleFilter"
            class="cute-input"
          >
            <template #prefix>
              <span class="input-icon">👤</span>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="手机/账号" class="search-item">
          <el-input 
            v-model="listQuery.username" 
            placeholder="请输入手机号" 
            clearable 
            @keyup.enter="handleFilter"
            class="cute-input"
          >
            <template #prefix>
              <span class="input-icon">📱</span>
            </template>
          </el-input>
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

      <div class="toolbar">
        <el-button type="primary" class="cute-btn primary add-btn" @click="handleAdd">
          <span>➕</span> 新增用户
        </el-button>
        <el-button type="success" class="cute-btn success" plain>
          <span>📥</span> 批量导入
        </el-button>
      </div>

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
        <el-table-column prop="realName" label="真实姓名" width="120" align="center">
          <template #default="{ row }">
            <div class="user-info">
              <span class="user-avatar">{{ row.realName?.charAt(0) || '用' }}</span>
              <span class="user-name">{{ row.realName }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="username" label="手机号/账号" width="150" align="center">
          <template #default="{ row }">
            <span class="phone-text">{{ row.username }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="userType" label="用户类型" width="100" align="center">
          <template #default="scope">
            <el-tag v-if="scope.row" :type="scope.row.userType === 1 ? 'success' : 'warning'" class="cute-tag" effect="plain">
              {{ scope.row.userType === 1 ? '教职工' : '家长' }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="roleName" label="系统角色" width="120" align="center">
          <template #default="{ row }">
            <span class="role-tag">
              <span class="role-icon">👑</span>
              {{ row.roleName }}
            </span>
          </template>
        </el-table-column>

        <el-table-column prop="status" label="账号状态" width="100" align="center">
          <template #default="scope">
            <span v-if="scope.row">
              <el-switch
                v-model="scope.row.status"
                :active-value="1"
                :inactive-value="0"
                @change="handleStatusChange(scope.row)"
                class="cute-switch"
              />
            </span>
          </template>
        </el-table-column>

        <el-table-column prop="createTime" label="创建时间" min-width="160" align="center">
          <template #default="{ row }">
            <span class="time-text">{{ row.createTime }}</span>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="180" align="center" fixed="right">
          <template #default="scope">
            <div class="action-buttons">
              <el-button size="small" type="primary" link class="action-btn edit" @click="handleEdit(scope.row)">
                <span>✏️</span> 编辑
              </el-button>
              <el-button size="small" type="danger" link class="action-btn delete" @click="handleDelete(scope.row)">
                <span>🗑️</span> 删除
              </el-button>
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" @close="resetForm" class="cute-dialog">
      <div class="dialog-decoration">
        <span class="deco-flower">🏠</span>
      </div>
      <el-form ref="dataForm" :model="form" :rules="rules" label-width="100px" class="cute-form">
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="form.realName" placeholder="请输入姓名" class="cute-input">
            <template #prefix>
              <span>👤</span>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="手机号" prop="username">
          <el-input v-model="form.username" placeholder="将作为登录账号" class="cute-input">
            <template #prefix>
              <span>📱</span>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="登录密码" :prop="form.id ? '' : 'password'">
          <el-input v-model="form.password" type="password" placeholder="默认密码可设为 123456" show-password class="cute-input">
            <template #prefix>
              <span>🔒</span>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="用户身份" prop="userType">
          <el-select v-model="form.userType" placeholder="请选择用户身份" style="width: 100%;" class="cute-select">
            <el-option label="教职工" :value="1">
              <span>👨‍🏫 教职工</span>
            </el-option>
            <el-option label="家长" :value="2">
              <span>👨‍👩‍👧 家长</span>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="用户角色" prop="roleId">
          <el-select v-model="form.roleId" placeholder="请选择角色" style="width: 100%;" class="cute-select">
            <el-option 
              v-for="role in roleOptions" 
              :key="role.id" 
              :label="role.roleName" 
              :value="role.id">
              <span>👑 {{ role.roleName }}</span>
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button class="cute-btn" @click="dialogVisible = false">取 消</el-button>
          <el-button type="primary" class="cute-btn primary" @click="submitData" :loading="submitLoading">
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
  name: 'UserManagement',
  data() {
    return {
      roleOptions: [],
      list: [],
      total: 0,
      listLoading: true,
      listQuery: {
        page: 1,
        size: 10,
        realName: '',
        username: ''
      },

      dialogVisible: false,
      dialogTitle: '',
      submitLoading: false,
      form: {
        id: undefined,
        realName: '',
        username: '',
        password: '',
        userType: 1,
        roleId: undefined
      },
      rules: {
        realName: [{ required: true, message: '真实姓名不能为空', trigger: 'blur' }],
        username: [{ required: true, message: '手机号不能为空', trigger: 'blur' }],
        password: [{ required: true, message: '密码不能为空', trigger: 'blur' }],
        userType: [{ required: true, message: '请选择用户身份', trigger: 'change' }],
        roleId: [{ required: true, message: '请选择角色', trigger: 'change' }]
      }
    };
  },
  created() {
    this.getList();
    this.getRoleList();
  },
  methods: {
    getRoleList() {
      request.get('/api/system/role/list').then(res => {
        this.roleOptions = res.data || [];
      });
    },
    getList() {
      this.listLoading = true;
      request.get('/api/system/user/page', { params: this.listQuery }).then(res => {
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
      this.listQuery = { page: 1, size: 10, realName: '', username: '' };
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

    handleAdd() {
      this.form = { id: undefined, realName: '', username: '', password: '', userType: 1, roleId: undefined };
      this.dialogTitle = '➕ 新增用户';
      this.dialogVisible = true;
    },
    handleEdit(row) {
      this.form = { ...row, password: '' };
      this.dialogTitle = '✏️ 编辑用户';
      this.dialogVisible = true;
    },
    submitData() {
      this.$refs.dataForm.validate((valid) => {
        if (valid) {
          this.submitLoading = true;
          const isEdit = !!this.form.id;
          const url = isEdit ? '/api/system/user/update' : '/api/system/user/add';

          request.post(url, this.form).then(() => {
            ElMessage.success(isEdit ? '🎉 修改成功' : '🎉 新增成功');
            this.dialogVisible = false;
            this.getList();
          }).finally(() => {
            this.submitLoading = false;
          });
        }
      });
    },

    handleStatusChange(row) {
      request.post('/api/system/user/status', { id: row.id, status: row.status }).then(() => {
        ElMessage.success('✨ 状态更新成功');
      }).catch(() => {
        row.status = row.status === 1 ? 0 : 1;
      });
    },

    handleDelete(row) {
      ElMessageBox.confirm(`确认删除用户 [${row.realName}] 吗？此操作不可恢复！`, '🗑️ 确认删除', {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        request.post(`/api/system/user/delete/${row.id}`).then(() => {
          ElMessage.success('🎉 删除成功');
          if (this.list.length === 1 && this.listQuery.page > 1) {
            this.listQuery.page--;
          }
          this.getList();
        }).catch((err) => {
          console.error('删除失败:', err);
          ElMessage.error('删除失败，请重试');
        });
      }).catch(() => {});
    },

    resetForm() {
      this.$refs.dataForm?.clearValidate();
    }
  }
};
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=ZCOOL+KuaiLe&family=Noto+Sans+SC:wght@400;500;700&display=swap');

.page-container {
  padding: 0;
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

.search-form {
  padding: 20px;
  background: linear-gradient(135deg, #FFF9F5 0%, #FFF0F0 100%);
  border-radius: 16px;
  margin-bottom: 20px;
}

.search-item {
  margin-bottom: 0;
}

.search-actions {
  margin-left: auto;
}

.cute-input :deep(.el-input__wrapper) {
  border-radius: 12px;
  box-shadow: 0 0 0 1px #F0E0E0 inset;
  background: #fff;
  transition: all 0.3s;
}

.cute-input :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #FFB5B5 inset;
}

.cute-input :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px rgba(255, 107, 107, 0.2), 0 0 0 1px #FF6B6B inset;
}

.cute-input :deep(.el-input__inner) {
  font-family: 'Noto Sans SC', sans-serif;
}

.input-icon {
  font-size: 14px;
}

.cute-select :deep(.el-select__wrapper) {
  border-radius: 12px;
  box-shadow: 0 0 0 1px #F0E0E0 inset;
  background: #fff;
}

.cute-select :deep(.el-select__wrapper:hover) {
  box-shadow: 0 0 0 1px #FFB5B5 inset;
}

.cute-btn {
  border-radius: 12px;
  padding: 10px 20px;
  font-family: 'Noto Sans SC', sans-serif;
  font-weight: 500;
  border: none;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  display: inline-flex;
  align-items: center;
  gap: 6px;
  background: linear-gradient(135deg, #FFF0F0 0%, #FFE8E8 100%);
  color: #5D4E4E;
}

.cute-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(255, 107, 107, 0.2);
}

.cute-btn.primary {
  background: linear-gradient(135deg, #FFB5B5 0%, #FF8E8E 100%);
  color: #fff;
}

.cute-btn.primary:hover {
  background: linear-gradient(135deg, #FF6B6B 0%, #FF8E53 100%);
  box-shadow: 0 8px 25px rgba(255, 107, 107, 0.35);
}

.cute-btn.success {
  background: linear-gradient(135deg, #E8F5E9 0%, #C8E6C9 100%);
  color: #4CAF50;
}

.cute-btn.success:hover {
  background: linear-gradient(135deg, #C8E6C9 0%, #A5D6A7 100%);
  box-shadow: 0 6px 20px rgba(76, 175, 80, 0.2);
}

.toolbar {
  padding: 0 20px 20px;
  display: flex;
  gap: 12px;
}

.add-btn {
  padding: 12px 24px;
  font-size: 15px;
}

.cute-table {
  border-radius: 16px;
  overflow: hidden;
}

.cute-table :deep(.el-table__header-wrapper th) {
  background: linear-gradient(135deg, #FFF0F0 0%, #FFE8E8 100%) !important;
  color: #5D4E4E;
  font-weight: 600;
  font-family: 'Noto Sans SC', sans-serif;
}

.cute-table :deep(.el-table__body-wrapper) {
  background: #fff;
}

.cute-table :deep(.el-table__row) {
  transition: all 0.3s;
}

.cute-table :deep(.el-table__row:hover) {
  background: rgba(255, 230, 230, 0.5) !important;
}

.row-number {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  background: linear-gradient(135deg, #FFF0F0 0%, #FFE8E8 100%);
  border-radius: 50%;
  font-size: 12px;
  font-weight: 600;
  color: #FF6B6B;
}

.user-info {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.user-avatar {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  background: linear-gradient(135deg, #FFB5B5 0%, #FF8E8E 100%);
  border-radius: 50%;
  font-size: 14px;
  font-weight: 600;
  color: #fff;
}

.user-name {
  font-weight: 500;
  color: #5D4E4E;
}

.phone-text {
  font-family: 'Consolas', monospace;
  color: #4D96FF;
}

.cute-tag {
  border-radius: 20px;
  font-weight: 500;
}

.role-tag {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  color: #9B59B6;
  font-size: 13px;
}

.role-icon {
  font-size: 12px;
}

.time-text {
  color: #9B8E8E;
  font-size: 13px;
}

.cute-switch :deep(.el-switch.is-checked .el-switch__core) {
  background: linear-gradient(135deg, #FFB5B5 0%, #FF8E8E 100%);
  border-color: #FF6B6B;
}

.action-buttons {
  display: flex;
  justify-content: center;
  gap: 8px;
}

.action-btn {
  padding: 6px 12px;
  border-radius: 8px;
  font-size: 13px;
  transition: all 0.3s;
}

.action-btn.edit:hover {
  background: rgba(64, 158, 255, 0.1);
  transform: scale(1.05);
}

.action-btn.delete:hover {
  background: rgba(245, 108, 108, 0.1);
  transform: scale(1.05);
}

.pagination-container {
  padding: 20px;
  display: flex;
  justify-content: flex-end;
}

.cute-pagination {
  border-radius: 12px;
  overflow: hidden;
}

.cute-pagination :deep(.el-pager li) {
  border-radius: 8px;
  margin: 0 4px;
}

.cute-pagination :deep(.el-pager li.is-active) {
  background: linear-gradient(135deg, #FFB5B5 0%, #FF8E8E 100%) !important;
}

.cute-pagination :deep(.btn-prev),
.cute-pagination :deep(.btn-next) {
  border-radius: 8px;
}

.cute-dialog {
  border-radius: 24px;
  overflow: hidden;
}

.cute-dialog :deep(.el-dialog__header) {
  background: linear-gradient(135deg, #FFF0F0 0%, #FFE8E8 100%);
  padding: 20px;
}

.cute-dialog :deep(.el-dialog__title) {
  font-family: 'ZCOOL KuaiLe', 'Noto Sans SC', sans-serif;
  color: #5D4E4E;
}

.cute-dialog :deep(.el-dialog__body) {
  padding: 30px;
}

.dialog-decoration {
  position: absolute;
  top: 20px;
  right: 60px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}
</style>
