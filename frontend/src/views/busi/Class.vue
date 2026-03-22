<template>
  <div class="page-container">
    <div class="page-header">
      <div class="header-decoration">
        <span class="deco-flower">✿</span>
      </div>
      <h2 class="page-title">班级管理</h2>
      <p class="page-subtitle">管理幼儿园班级信息</p>
    </div>

    <el-card shadow="never" class="content-card">
      <el-form :inline="true" :model="listQuery" class="search-form">
        <el-form-item label="班级名称" class="search-item">
          <el-input 
            v-model="listQuery.className" 
            placeholder="请输入班级名称" 
            clearable 
            @keyup.enter="handleFilter"
            class="cute-input"
          >
            <template #prefix>
              <span class="input-icon">🏫</span>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="年级" class="search-item">
          <el-select v-model="listQuery.grade" placeholder="请选择年级" clearable class="cute-select grade-select">
            <el-option label="全部年级" :value="undefined" />
            <el-option label="托班" :value="1">
              <span>🍼 托班</span>
            </el-option>
            <el-option label="小班" :value="2">
              <span>👶 小班</span>
            </el-option>
            <el-option label="中班" :value="3">
              <span>🎈 中班</span>
            </el-option>
            <el-option label="大班" :value="4">
              <span>🎓 大班</span>
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
          <span>➕</span> 新增班级
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
        <el-table-column prop="className" label="班级名称" min-width="140" align="center">
          <template #default="{ row }">
            <div class="class-info">
              <span class="class-icon">{{ getGradeEmoji(row.grade) }}</span>
              <span class="class-name">{{ row.className }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="grade" label="年级" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getGradeType(row.grade)" class="cute-tag" effect="plain">
              {{ getGradeName(row.grade) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="teachers" label="任课教师" min-width="180" align="center">
          <template #default="{ row }">
            <div v-if="row.teachers && row.teachers.length > 0" class="teachers-list">
              <el-tooltip 
                v-for="teacher in row.teachers.slice(0, 3)" 
                :key="teacher.id"
                :content="teacher.isHeadTeacher === 1 ? '班主任' : '任课教师'"
                placement="top"
              >
                <span class="teacher-chip" :class="{ 'is-head': teacher.isHeadTeacher === 1 }">
                  {{ teacher.realName?.charAt(0) || '教' }}
                </span>
              </el-tooltip>
              <span v-if="row.teachers.length > 3" class="more-teachers">
                +{{ row.teachers.length - 3 }}
              </span>
            </div>
            <span v-else class="no-teacher">未分配</span>
          </template>
        </el-table-column>

        <el-table-column prop="createTime" label="创建时间" min-width="160" align="center">
          <template #default="{ row }">
            <span class="time-text">{{ row.createTime }}</span>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="140" align="center" fixed="right">
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

    <!-- 新增/编辑班级弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px" @close="resetForm" class="cute-dialog">
      <div class="dialog-decoration">
        <span class="deco-flower">🏠</span>
      </div>
      <el-form ref="dataForm" :model="form" :rules="rules" label-width="100px" class="cute-form">
        <el-form-item label="班级名称" prop="className">
          <el-input v-model="form.className" placeholder="如：大一班" class="cute-input">
            <template #prefix>
              <span>🏫</span>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="年级" prop="grade">
          <el-select v-model="form.grade" placeholder="请选择年级" style="width: 100%;" class="cute-select">
            <el-option label="🍼 托班" :value="1" />
            <el-option label="👶 小班" :value="2" />
            <el-option label="🎈 中班" :value="3" />
            <el-option label="🎓 大班" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="任课教师" prop="teachers">
          <div class="teachers-select-container">
            <div v-if="form.teachers && form.teachers.length > 0" class="selected-teachers">
              <div v-for="(item, index) in form.teachers" :key="index" class="teacher-item">
                <el-select
                  v-model="item.teacherUserId"
                  placeholder="选择教师"
                  filterable
                  class="teacher-select"
                  @focus="loadParentList"
                >
                  <el-option
                    v-for="t in availableTeachers"
                    :key="t.id"
                    :label="`${t.realName} (${t.username})`"
                    :value="t.id"
                    :disabled="isTeacherDisabled(t.id, index)"
                  />
                </el-select>
                <el-select v-model="item.isHeadTeacher" placeholder="角色" class="role-select">
                  <el-option label="👨‍🏫 班主任" :value="1" />
                  <el-option label="👩‍🏫 任课教师" :value="0" />
                </el-select>
                <el-button type="danger" link @click="removeTeacher(index)" class="remove-teacher-btn">
                  <span>❌</span>
                </el-button>
              </div>
            </div>
            <el-button type="primary" link @click="addTeacher" class="add-teacher-btn">
              <span>➕</span> 添加教师
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
  name: 'ClassManagement',
  data() {
    return {
      list: [],
      total: 0,
      listLoading: true,
      availableTeachers: [],
      avatarColors: ['#FF6B6B', '#4ECDC4', '#45B7D1', '#96CEB4', '#FFEAA7', '#DDA0DD', '#98D8C8', '#F7DC6F'],
      listQuery: {
        page: 1,
        size: 10,
        className: '',
        grade: undefined
      },

      dialogVisible: false,
      dialogTitle: '',
      submitLoading: false,
      form: {
        id: undefined,
        className: '',
        grade: undefined,
        teachers: []
      },
      rules: {
        className: [{ required: true, message: '班级名称不能为空', trigger: 'blur' }],
        grade: [{ required: true, message: '请选择年级', trigger: 'change' }]
      }
    };
  },
  created() {
    this.getList();
    this.loadParentList();
  },
  methods: {
    loadParentList() {
      if (this.availableTeachers.length === 0) {
        request.get('/api/system/user/teacher-list').then(res => {
          this.availableTeachers = res.data || [];
        });
      }
    },
    getList() {
      this.listLoading = true;
      const params = { ...this.listQuery };
      if (params.grade === '') params.grade = undefined;
      
      request.get('/api/busi/class/page', { params }).then(res => {
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
      this.listQuery = { page: 1, size: 10, className: '', grade: undefined };
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
    getGradeType(grade) {
      const map = { 1: 'info', 2: 'success', 3: 'warning', 4: 'primary' };
      return map[grade] || 'info';
    },
    getTeacherAvatarStyle(teacherId) {
      const index = teacherId % this.avatarColors.length;
      return { background: this.avatarColors[index] };
    },

    handleAdd() {
      this.form = { id: undefined, className: '', grade: undefined, teachers: [] };
      this.dialogTitle = '➕ 新增班级';
      this.dialogVisible = true;
    },
    handleEdit(row) {
      this.form = { 
        id: row.id, 
        className: row.className, 
        grade: row.grade,
        teachers: row.teachers && row.teachers.length > 0 
          ? row.teachers.map(t => ({
              teacherUserId: t.id,
              isHeadTeacher: t.isHeadTeacher
            }))
          : []
      };
      this.dialogTitle = '✏️ 编辑班级';
      this.dialogVisible = true;
    },
    addTeacher() {
      if (!this.form.teachers) {
        this.form.teachers = [];
      }
      this.form.teachers.push({
        teacherUserId: null,
        isHeadTeacher: 0
      });
    },
    removeTeacher(index) {
      this.form.teachers.splice(index, 1);
    },
    isTeacherDisabled(teacherId, currentIndex) {
      if (!this.form.teachers) return false;
      return this.form.teachers.some((t, idx) => idx !== currentIndex && t.teacherUserId === teacherId);
    },
    submitData() {
      this.$refs.dataForm.validate((valid) => {
        if (valid) {
          this.submitLoading = true;
          const isEdit = !!this.form.id;
          const classData = {
            className: this.form.className,
            grade: this.form.grade
          };

          if (isEdit) {
            classData.id = this.form.id;
          }

          const saveClass = () => {
            return isEdit 
              ? request.post('/api/busi/class/update', classData)
              : request.post('/api/busi/class/add', classData);
          };

          saveClass().then((res) => {
            const classId = isEdit ? this.form.id : res.data;
            
            if (this.form.teachers && this.form.teachers.length > 0) {
              const teacherIds = this.form.teachers.map(t => t.teacherUserId);
              return request.post('/api/busi/teacher-class/assign', {
                classId: classId,
                teacherIds: teacherIds,
                isHeadTeacher: 1
              });
            }
          }).then(() => {
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
      ElMessageBox.confirm(`确认删除班级 [${row.className}] 吗？此操作不可恢复！`, '🗑️ 确认删除', {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        request.post(`/api/busi/class/delete/${row.id}`).then(() => {
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
.grade-select { min-width: 140px; }
.cute-form .grade-select { width: 100%; }
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
.class-info { display: flex; align-items: center; justify-content: center; gap: 8px; }
.class-icon { font-size: 18px; }
.class-name { font-weight: 600; color: #5D4E4E; }
.cute-tag { border-radius: 20px; font-weight: 500; }
.teachers-list { display: flex; align-items: center; justify-content: center; gap: 4px; }
.teacher-chip { display: inline-flex; align-items: center; justify-content: center; width: 28px; height: 28px; background: linear-gradient(135deg, #9B59B6 0%, #8E44AD 100%); border-radius: 50%; font-size: 12px; font-weight: 600; color: #fff; cursor: pointer; transition: transform 0.2s; }
.teacher-chip:hover { transform: scale(1.1); }
.teacher-chip.is-head { background: linear-gradient(135deg, #FF6B6B 0%, #FF8E53 100%); box-shadow: 0 2px 8px rgba(255, 107, 107, 0.4); }
.more-teachers { font-size: 12px; color: #9B8E8E; margin-left: 4px; }
.no-teacher { color: #CCC; font-style: italic; }
.time-text { color: #9B8E8E; font-size: 13px; }
.action-buttons { display: flex; justify-content: center; gap: 4px; }
.action-btn { padding: 6px 10px; border-radius: 8px; font-size: 13px; transition: all 0.3s; }
.pagination-container { padding: 20px; display: flex; justify-content: flex-end; }
.cute-pagination :deep(.el-pager li.is-active) { background: linear-gradient(135deg, #FFB5B5 0%, #FF8E8E 100%) !important; }
.cute-dialog :deep(.el-dialog__header) { background: linear-gradient(135deg, #FFF0F0 0%, #FFE8E8 100%); padding: 20px; }
.cute-dialog :deep(.el-dialog__title) { font-family: 'ZCOOL KuaiLe', sans-serif; color: #5D4E4E; }
.dialog-footer { display: flex; justify-content: flex-end; gap: 12px; }
.teachers-select-container { width: 100%; }
.selected-teachers { display: flex; flex-direction: column; gap: 10px; margin-bottom: 10px; }
.teacher-item { display: flex; align-items: center; gap: 8px; }
.teacher-select { width: 180px; }
.role-select { width: 120px; }
.remove-teacher-btn { padding: 4px 8px; }
.add-teacher-btn { margin-top: 5px; }

@media (max-width: 768px) {
  .page-title { font-size: 22px; }
  .search-form :deep(.el-form-item) { display: block; margin-bottom: 12px; }
  .search-actions { margin-left: 0; display: flex; gap: 8px; }
  .action-buttons { flex-direction: column; gap: 2px; }
}
</style>
