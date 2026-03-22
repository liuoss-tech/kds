<template>
  <div class="page-container">
    <div class="page-header">
      <div class="header-decoration">
        <span class="deco-flower">👶</span>
      </div>
      <h2 class="page-title">幼儿管理</h2>
      <p class="page-subtitle">管理幼儿园幼儿档案信息</p>
    </div>

    <el-card shadow="never" class="content-card">
      <el-form :inline="true" :model="listQuery" class="search-form">
        <el-form-item label="幼儿姓名" class="search-item">
          <el-input 
            v-model="listQuery.studentName" 
            placeholder="请输入幼儿姓名" 
            clearable 
            @keyup.enter="handleFilter"
            class="cute-input"
          >
            <template #prefix>
              <span class="input-icon">👶</span>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="所属班级" class="search-item">
          <el-select v-model="listQuery.classId" placeholder="请选择班级" clearable class="cute-select class-select">
            <el-option label="全部班级" :value="undefined" />
            <el-option v-for="cls in classList" :key="cls.id" :label="cls.className" :value="cls.id">
              <span>{{ getGradeEmoji(cls.grade) }} {{ cls.className }}</span>
            </el-option>
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

      <div class="toolbar">
        <el-button type="primary" class="cute-btn primary add-btn" @click="handleAdd">
          <span>➕</span> 新增幼儿
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
        <el-table-column label="头像" width="80" align="center">
          <template #default="{ row }">
            <div class="student-avatar" :style="getAvatarStyle(row.avatar)">
              <img v-if="row.avatar" :src="row.avatar" alt="头像" />
              <span v-else>{{ row.studentName?.charAt(0) }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="studentName" label="幼儿姓名" min-width="120" align="center">
          <template #default="{ row }">
            <span class="student-name">{{ row.studentName }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="gender" label="性别" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.gender === 1 ? 'primary' : 'success'" class="cute-tag" effect="plain">
              {{ row.gender === 1 ? '👦 男' : '👧 女' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="birthday" label="出生日期" width="120" align="center">
          <template #default="{ row }">
            <span class="birthday-text">{{ row.birthday }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="className" label="所属班级" min-width="140" align="center">
          <template #default="{ row }">
            <div class="class-info">
              <span class="class-icon">{{ getGradeEmoji(row.grade) }}</span>
              <span class="class-name">{{ row.className || '未分配' }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="家长信息" min-width="180" align="center">
          <template #default="{ row }">
            <div v-if="row.parents && row.parents.length > 0" class="parents-list">
              <el-tooltip 
                v-for="parent in row.parents.slice(0, 2)" 
                :key="parent.parentUserId"
                :content="`${parent.relation}: ${parent.parentName}`"
                placement="top"
              >
                <span class="parent-chip" :class="{ 'is-primary': parent.isPrimary === 1 }">
                  {{ getRelationEmoji(parent.relation) }}{{ parent.parentName?.charAt(0) || '家' }}
                </span>
              </el-tooltip>
              <span v-if="row.parents.length > 2" class="more-parents">
                +{{ row.parents.length - 2 }}
              </span>
            </div>
            <span v-else class="no-parent">未绑定</span>
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

    <!-- 新增/编辑幼儿弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="580px" @close="resetForm" class="cute-dialog">
      <div class="dialog-decoration">
        <span class="deco-flower">👶</span>
      </div>
      <el-form ref="dataForm" :model="form" :rules="rules" label-width="100px" class="cute-form">
        <el-form-item label="幼儿姓名" prop="studentName">
          <el-input v-model="form.studentName" placeholder="请输入幼儿姓名" class="cute-input">
            <template #prefix>
              <span>👶</span>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="form.gender" class="gender-radio-group">
            <el-radio :label="1" class="gender-radio">
              <span>👦 男</span>
            </el-radio>
            <el-radio :label="2" class="gender-radio">
              <span>👧 女</span>
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="出生日期" prop="birthday">
          <el-date-picker
            v-model="form.birthday"
            type="date"
            placeholder="请选择出生日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            class="cute-date-picker"
          />
        </el-form-item>
        <el-form-item label="所属班级" prop="classId">
          <el-select v-model="form.classId" placeholder="请选择班级" style="width: 100%;" class="cute-select">
            <el-option v-for="cls in classList" :key="cls.id" :label="`${getGradeEmoji(cls.grade)} ${cls.className}`" :value="cls.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="入园日期">
          <el-date-picker
            v-model="form.admissionDate"
            type="date"
            placeholder="请选择入园日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            class="cute-date-picker"
          />
        </el-form-item>
        <el-form-item label="家长信息" prop="parents">
          <div class="parents-select-container">
            <div v-if="form.parents && form.parents.length > 0" class="selected-parents">
              <div v-for="(parent, index) in form.parents" :key="index" class="parent-item">
                <el-select 
                  v-model="parent.parentUserId" 
                  placeholder="选择家长" 
                  filterable
                  class="parent-select"
                  @focus="loadParentList"
                >
                  <el-option
                    v-for="p in availableParents"
                    :key="p.userId"
                    :label="`${p.realName || p.username} (${p.phone || '无电话'})`"
                    :value="p.userId"
                    :disabled="isParentDisabled(p.userId, index)"
                  />
                </el-select>
                <el-select v-model="parent.relation" placeholder="关系" class="relation-select">
                  <el-option label="👨 爸爸" value="爸爸" />
                  <el-option label="👩 妈妈" value="妈妈" />
                  <el-option label="👴 爷爷" value="爷爷" />
                  <el-option label="👵 奶奶" value="奶奶" />
                  <el-option label="👴 外公" value="外公" />
                  <el-option label="👵 外婆" value="外婆" />
                  <el-option label="👔 其他" value="其他" />
                </el-select>
                <el-checkbox v-model="parent.isPrimary" :true-value="1" :false-value="0" class="primary-check">
                  主监护人
                </el-checkbox>
                <el-button type="danger" link @click="removeParent(index)" class="remove-parent-btn">
                  <span>❌</span>
                </el-button>
              </div>
            </div>
            <el-button type="primary" link @click="addParent" class="add-parent-btn">
              <span>➕</span> 添加家长
            </el-button>
          </div>
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
  name: 'StudentManagement',
  data() {
    return {
      list: [],
      total: 0,
      listLoading: true,
      classList: [],
      parentList: [],
      avatarColors: ['#FFB5B5', '#96CEB4', '#FFEAA7', '#DDA0DD', '#87CEEB', '#F7DC6F', '#FFA07A', '#98D8C8'],
      listQuery: {
        page: 1,
        size: 10,
        studentName: '',
        classId: undefined,
        status: 1
      },

      dialogVisible: false,
      dialogTitle: '',
      submitLoading: false,
      form: {
        id: undefined,
        studentName: '',
        gender: undefined,
        birthday: '',
        classId: undefined,
        admissionDate: '',
        idCard: '',
        avatar: '',
        status: 1,
        parents: []
      },
      rules: {
        studentName: [{ required: true, message: '幼儿姓名不能为空', trigger: 'blur' }],
        gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
        birthday: [{ required: true, message: '出生日期不能为空', trigger: 'change' }],
        classId: [{ required: true, message: '请选择所属班级', trigger: 'change' }]
      }
    };
  },
  computed: {
    availableParents() {
      return this.parentList;
    }
  },
  created() {
    this.getList();
    this.getClassList();
  },
  methods: {
    getClassList() {
      request.get('/api/busi/student/class-list').then(res => {
        this.classList = res.data || [];
      });
    },
    loadParentList() {
      if (this.parentList.length === 0) {
        request.get('/api/busi/student/parent-list').then(res => {
          this.parentList = res.data || [];
        });
      }
    },
    getList() {
      this.listLoading = true;
      const params = { ...this.listQuery };
      if (params.classId === '') params.classId = undefined;
      
      request.get('/api/busi/student/page', { params }).then(res => {
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
      this.listQuery = { page: 1, size: 10, studentName: '', classId: undefined, status: 1 };
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

    getGradeName(grade) {
      const map = { 1: '托班', 2: '小班', 3: '中班', 4: '大班' };
      return map[grade] || '未知';
    },
    getGradeEmoji(grade) {
      const map = { 1: '🍼', 2: '👶', 3: '🎈', 4: '🎓' };
      return map[grade] || '🏫';
    },
    getRelationEmoji(relation) {
      const map = { '爸爸': '👨', '妈妈': '👩', '爷爷': '👴', '奶奶': '👵', '外公': '👴', '外婆': '👵', '其他': '👔' };
      return map[relation] || '👔';
    },
    getAvatarStyle(avatar) {
      if (avatar) {
        return { background: 'none', padding: 0 };
      }
      const index = Math.floor(Math.random() * this.avatarColors.length);
      return { background: this.avatarColors[index] };
    },

    isParentDisabled(userId, currentIndex) {
      if (!this.form.parents) return false;
      return this.form.parents.some((p, idx) => idx !== currentIndex && p.parentUserId === userId);
    },

    addParent() {
      if (!this.form.parents) {
        this.form.parents = [];
      }
      this.form.parents.push({
        parentUserId: undefined,
        relation: '',
        isPrimary: this.form.parents.length === 0 ? 1 : 0
      });
    },
    removeParent(index) {
      this.form.parents.splice(index, 1);
    },

    handleAdd() {
      this.loadParentList();
      this.form = {
        id: undefined,
        studentName: '',
        gender: undefined,
        birthday: '',
        classId: undefined,
        admissionDate: '',
        idCard: '',
        avatar: '',
        status: 1,
        parents: []
      };
      this.dialogTitle = '➕ 新增幼儿';
      this.dialogVisible = true;
    },
    handleEdit(row) {
      this.loadParentList();
      request.get(`/api/busi/student/${row.id}`).then(res => {
        const data = res.data || {};
        this.form = {
          id: data.id,
          studentName: data.studentName,
          gender: data.gender,
          birthday: data.birthday,
          classId: data.classId,
          admissionDate: data.admissionDate || '',
          idCard: data.idCard || '',
          avatar: data.avatar || '',
          status: data.status,
          parents: data.parents && data.parents.length > 0 ? data.parents.map(p => ({
            parentUserId: p.parentUserId,
            relation: p.relation,
            isPrimary: p.isPrimary
          })) : []
        };
        this.dialogTitle = '✏️ 编辑幼儿';
        this.dialogVisible = true;
      });
    },
    submitData() {
      this.$refs.dataForm.validate((valid) => {
        if (valid) {
          if (!this.form.parents || this.form.parents.length === 0) {
            ElMessage.warning('请至少添加一位家长');
            return;
          }
          const hasValidParent = this.form.parents.some(p => p.parentUserId && p.relation);
          if (!hasValidParent) {
            ElMessage.warning('请完善家长信息');
            return;
          }

          this.submitLoading = true;
          const isEdit = !!this.form.id;
          const url = isEdit ? '/api/busi/student/update' : '/api/busi/student/add';

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
      ElMessageBox.confirm(`确认删除幼儿 [${row.studentName}] 吗？此操作不可恢复！`, '🗑️ 确认删除', {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        request.post(`/api/busi/student/delete/${row.id}`).then(() => {
          ElMessage.success('🎉 删除成功');
          if (this.list.length === 1 && this.listQuery.page > 1) {
            this.listQuery.page--;
          }
          this.getList();
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
.cute-input :deep(.el-input__wrapper) { border-radius: 12px; box-shadow: 0 0 0 1px #F0E0E0 inset; background: #fff; transition: all 0.3s; }
.cute-input :deep(.el-input__wrapper:hover) { box-shadow: 0 0 0 1px #FFB5B5 inset; }
.cute-input :deep(.el-input__wrapper.is-focus) { box-shadow: 0 0 0 2px rgba(255, 107, 107, 0.2), 0 0 0 1px #FF6B6B inset; }
.input-icon { font-size: 14px; }
.cute-select :deep(.el-select__wrapper) { border-radius: 12px; box-shadow: 0 0 0 1px #F0E0E0 inset; background: #fff; }
.class-select { min-width: 160px; }
.cute-form .class-select { width: 100%; }
.cute-date-picker { width: 100%; }
.cute-date-picker :deep(.el-input__wrapper) { border-radius: 12px; box-shadow: 0 0 0 1px #F0E0E0 inset; background: #fff; }
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
.student-avatar { width: 40px; height: 40px; border-radius: 50%; display: inline-flex; align-items: center; justify-content: center; overflow: hidden; font-size: 16px; font-weight: 600; color: #fff; }
.student-avatar img { width: 100%; height: 100%; object-fit: cover; }
.student-name { font-weight: 600; color: #5D4E4E; }
.birthday-text { color: #9B8E8E; font-size: 13px; }
.class-info { display: flex; align-items: center; justify-content: center; gap: 8px; }
.class-icon { font-size: 18px; }
.class-name { font-weight: 500; color: #5D4E4E; }
.cute-tag { border-radius: 20px; font-weight: 500; }
.parents-list { display: flex; align-items: center; justify-content: center; gap: 4px; }
.parent-chip { display: inline-flex; align-items: center; justify-content: center; width: 28px; height: 28px; background: linear-gradient(135deg, #87CEEB 0%, #6BB3D9 100%); border-radius: 50%; font-size: 12px; font-weight: 600; color: #fff; cursor: pointer; transition: transform 0.2s; }
.parent-chip:hover { transform: scale(1.1); }
.parent-chip.is-primary { background: linear-gradient(135deg, #FFB5B5 0%, #FF8E8E 100%); box-shadow: 0 2px 8px rgba(255, 107, 107, 0.4); }
.more-parents { font-size: 12px; color: #9B8E8E; margin-left: 4px; }
.no-parent { color: #CCC; font-style: italic; }
.time-text { color: #9B8E8E; font-size: 13px; }
.action-buttons { display: flex; justify-content: center; gap: 4px; }
.action-btn { padding: 6px 10px; border-radius: 8px; font-size: 13px; transition: all 0.3s; }
.pagination-container { padding: 20px; display: flex; justify-content: flex-end; }
.cute-pagination :deep(.el-pager li.is-active) { background: linear-gradient(135deg, #FFB5B5 0%, #FF8E8E 100%) !important; }
.cute-dialog :deep(.el-dialog__header) { background: linear-gradient(135deg, #FFF0F0 0%, #FFE8E8 100%); padding: 20px; }
.cute-dialog :deep(.el-dialog__title) { font-family: 'ZCOOL KuaiLe', sans-serif; color: #5D4E4E; }
.dialog-footer { display: flex; justify-content: flex-end; gap: 12px; }
.dialog-decoration { text-align: center; margin-bottom: 10px; }

.gender-radio-group { display: flex; gap: 20px; }
.gender-radio { margin-right: 0; }

.parents-select-container { width: 100%; }
.selected-parents { margin-bottom: 10px; }
.parent-item { display: flex; gap: 8px; margin-bottom: 8px; align-items: center; }
.parent-select { flex: 1; }
.relation-select { width: 100px; }
.primary-check { white-space: nowrap; }
.remove-parent-btn { padding: 4px 8px; }
.add-parent-btn { margin-top: 8px; }

@media (max-width: 768px) {
  .page-title { font-size: 22px; }
  .search-form :deep(.el-form-item) { display: block; margin-bottom: 12px; }
  .search-actions { margin-left: 0; display: flex; gap: 8px; }
  .action-buttons { flex-direction: column; gap: 2px; }
  .parent-item { flex-wrap: wrap; }
  .gender-radio-group { flex-direction: column; gap: 10px; }
}
</style>
