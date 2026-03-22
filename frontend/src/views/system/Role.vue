<template>
  <div class="page-container">
    <div class="page-header">
      <div class="header-decoration">
        <span class="deco-flower">❀</span>
      </div>
      <h2 class="page-title">角色管理</h2>
      <p class="page-subtitle">管理系统角色权限</p>
    </div>

    <el-card shadow="never" class="content-card">
      <el-form :inline="true" :model="listQuery" class="search-form">
        <el-form-item label="角色名称" class="search-item">
          <el-input 
            v-model="listQuery.roleName" 
            placeholder="如：园长" 
            clearable 
            @keyup.enter="handleFilter"
            class="cute-input"
          >
            <template #prefix>
              <span class="input-icon">🔍</span>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="角色编码" class="search-item">
          <el-input 
            v-model="listQuery.roleCode" 
            placeholder="如：PRINCIPAL" 
            clearable 
            @keyup.enter="handleFilter"
            class="cute-input"
          >
            <template #prefix>
              <span class="input-icon">🏷️</span>
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
          <span>➕</span> 新增角色
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
        <el-table-column prop="roleName" label="角色名称" min-width="150" align="center">
          <template #default="scope">
            <el-tag size="large" class="cute-tag" effect="plain">
              <span class="tag-icon">👑</span>
              {{ scope.row.roleName }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="roleCode" label="角色编码" width="200" align="center">
          <template #default="{ row }">
            <span class="code-text">{{ row.roleCode }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="角色描述" min-width="200" align="center">
          <template #default="{ row }">
            <span class="desc-text">{{ row.description || '暂无描述' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" align="center">
          <template #default="{ row }">
            <span class="time-text">{{ row.createTime }}</span>
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="240" align="center" fixed="right">
          <template #default="scope">
            <div class="action-buttons">
              <el-button size="small" type="success" link class="action-btn assign" @click="handleAssign(scope.row)">
                <span>⚙️</span> 分配权限
              </el-button>
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
          :page-sizes="[10, 20, 50]"
          :page-size="listQuery.size"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          background
          class="cute-pagination"
        />
      </div>
    </el-card>

    <el-dialog 
      :title="dialogTitle" 
      v-model="dialogVisible" 
      width="500px" 
      @close="resetForm"
      class="cute-dialog"
    >
      <div class="dialog-decoration">
        <span class="deco-flower">🌸</span>
      </div>
      <el-form ref="dataForm" :model="form" :rules="rules" label-width="100px" class="cute-form">
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="form.roleName" placeholder="请输入中文字符，如：配班老师" class="cute-input">
            <template #prefix>
              <span>👑</span>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="角色编码" prop="roleCode">
          <el-input v-model="form.roleCode" placeholder="请输入大写英文字母，如：ASSISTANT_TEACHER" class="cute-input">
            <template #prefix>
              <span>🏷️</span>
            </template>
          </el-input>
          <div class="form-tip">
            <span>💡</span> 注意：角色编码是前端控制菜单权限的唯一凭证，请勿随意修改。
          </div>
        </el-form-item>
        <el-form-item label="角色描述" prop="description">
          <el-input 
            type="textarea" 
            v-model="form.description" 
            placeholder="请输入角色职责描述" 
            :rows="3"
            class="cute-input"
          />
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

    <el-dialog title="分配权限" v-model="assignDialogVisible" width="500px" class="cute-dialog">
      <el-tree
        ref="permTree"
        :data="permissionTree"
        show-checkbox
        node-key="id"
        :props="defaultProps"
        default-expand-all
        check-strictly
      ></el-tree>
      <template #footer>
        <span class="dialog-footer">
          <el-button class="cute-btn" @click="assignDialogVisible = false">取 消</el-button>
          <el-button type="primary" class="cute-btn primary" @click="submitAssign" :loading="assignLoading">确 定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import request from '@/utils/request';
import { ElMessage, ElMessageBox } from 'element-plus';

export default {
  name: 'RoleManagement',
  data() {
    return {
      list: [],
      total: 0,
      listLoading: false,
      listQuery: { page: 1, size: 10, roleName: '', roleCode: '' },
      
      dialogVisible: false,
      dialogTitle: '',
      submitLoading: false,
      form: { id: undefined, roleName: '', roleCode: '', description: '' },
      rules: {
        roleName: [{ required: true, message: '角色名称不能为空', trigger: 'blur' }],
        roleCode: [{ required: true, message: '角色编码不能为空', trigger: 'blur' }]
      },

      assignDialogVisible: false,
      assignLoading: false,
      currentRoleId: null,
      permissionTree: [],
      defaultProps: { children: 'children', label: 'name' }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    getList() {
      this.listLoading = true;
      request.get('/api/system/role/page', { params: this.listQuery }).then(res => {
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
      this.listQuery = { page: 1, size: 10, roleName: '', roleCode: '' };
      this.getList();
    },
    handleSizeChange(val) {
      this.listQuery.size = val;
      this.listQuery.page = 1;
      this.getList();
    },
    handleCurrentChange(val) {
      this.listQuery.page = val;
      this.getList();
    },
    handleAdd() {
      this.form = { id: undefined, roleName: '', roleCode: '', description: '' };
      this.dialogTitle = '✨ 新增角色';
      this.dialogVisible = true;
    },
    handleEdit(row) {
      this.form = { 
        id: row.id, 
        roleName: row.roleName, 
        roleCode: row.roleCode, 
        description: row.description 
      };
      this.dialogTitle = '✏️ 编辑角色';
      this.dialogVisible = true;
    },
    submitData() {
      this.$refs.dataForm.validate((valid) => {
        if (valid) {
          this.submitLoading = true;
          const isEdit = !!this.form.id;
          const url = isEdit ? '/api/system/role/update' : '/api/system/role/add';
          
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
    handleDelete(row) {
      ElMessageBox.confirm(`确认删除角色 [${row.roleName}] 吗？`, '🗑️ 确认删除', {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        request.post(`/api/system/role/delete/${row.id}`).then(() => {
          ElMessage.success('🎉 删除成功');
          this.getList();
        }).catch((err) => {
          if (err.response?.data?.message) {
            ElMessage.error(err.response.data.message);
          }
        });
      }).catch(() => {});
    },
    resetForm() {
      this.$refs.dataForm?.clearValidate();
    },
    handleAssign(row) {
      this.currentRoleId = row.id;
      this.assignDialogVisible = true;
      console.log('=== handleAssign 开始 ===');
      console.log('当前角色ID:', row.id);

      request.get('/api/system/role/permission/tree').then(res => {
        console.log('权限树数据:', res.data);
        this.permissionTree = res.data || [];

        request.get(`/api/system/role/permission/own/${row.id}`).then(res2 => {
          console.log('该角色已有权限ID列表:', res2.data);
          this.$nextTick(() => {
            if (this.$refs.permTree) {
              this.$refs.permTree.setCheckedKeys(res2.data || []);
              console.log('setCheckedKeys 已调用，传入值:', res2.data);

              // 添加调试：检查实际的勾选状态
              const checkedKeys = this.$refs.permTree.getCheckedKeys();
              const halfCheckedKeys = this.$refs.permTree.getHalfCheckedKeys();
              console.log('el-tree 实际 checkedKeys:', checkedKeys);
              console.log('el-tree 实际 halfCheckedKeys:', halfCheckedKeys);
            } else {
              console.log('ERROR: $refs.permTree 不存在');
            }
          });
        }).catch(err2 => {
          console.log('获取角色权限失败:', err2);
        });
      }).catch(err => {
        console.log('获取权限树失败:', err);
      });
    },
    submitAssign() {
      this.assignLoading = true;
      const checkedKeys = this.$refs.permTree.getCheckedKeys();
      const halfCheckedKeys = this.$refs.permTree.getHalfCheckedKeys();
      const allCheckedIds = [...checkedKeys, ...halfCheckedKeys];

      request.post('/api/system/role/permission/assign', {
        roleId: this.currentRoleId,
        permissionIds: allCheckedIds
      }).then(() => {
        ElMessage.success('权限分配成功！');
        this.assignDialogVisible = false;
      }).finally(() => {
        this.assignLoading = false;
      });
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

.toolbar {
  padding: 0 20px 20px;
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

.cute-tag {
  border-radius: 20px;
  border: none;
  background: linear-gradient(135deg, #FFF0F0 0%, #FFE8E8 100%);
  color: #5D4E4E;
  font-weight: 500;
}

.tag-icon {
  margin-right: 4px;
}

.code-text {
  font-family: 'Consolas', monospace;
  font-size: 13px;
  color: #9B59B6;
  background: rgba(155, 89, 182, 0.1);
  padding: 4px 10px;
  border-radius: 6px;
}

.desc-text {
  color: #9B8E8E;
  font-size: 14px;
}

.time-text {
  color: #9B8E8E;
  font-size: 13px;
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

.cute-form .form-tip {
  font-size: 12px;
  color: #9B8E8E;
  margin-top: 8px;
  padding: 8px 12px;
  background: rgba(255, 230, 230, 0.5);
  border-radius: 8px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}
</style>
