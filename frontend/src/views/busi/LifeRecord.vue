<template>
  <div class="page-container">
    <div class="page-header">
      <div class="header-decoration">
        <span class="deco-flower">✿</span>
      </div>
      <div class="sync-status" :class="syncStatusClass">
        <span class="sync-icon">{{ syncIcon }}</span>
        <span class="sync-text">{{ syncText }}</span>
      </div>
      <h2 class="page-title">生活记录</h2>
      <p class="page-subtitle">记录幼儿每日生活情况</p>
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
        <el-form-item label="记录日期" class="search-item">
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

      <el-table
        ref="tableRef"
        v-loading="listLoading"
        :data="list"
        border
        stripe
        class="cute-table"
        row-key="studentId"
        :scrollbar-always-on="true"
      >
        <el-table-column type="index" label="序号" width="80" align="center" fixed>
          <template #default="{ $index }">
            <span class="row-number">{{ $index + 1 }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="studentName" label="幼儿姓名" min-width="120" align="center" fixed>
          <template #default="{ row }">
            <div class="student-info">
              <span class="student-icon">👶</span>
              <span class="student-name">{{ row.studentName }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="饮食情况" align="center" class-name="group-column">
          <template #header>
            <div class="group-header">
              <span class="group-icon">🍽️</span>
              <span class="group-title">饮食情况</span>
              <el-button type="warning" size="small" circle class="quick-fill-btn" @click="showQuickFill('lunch')" title="快速填充">
                <span>⚡</span>
              </el-button>
            </div>
          </template>
          <el-table-column prop="lunchAmount" label="午餐摄入量" width="150" align="center">
            <template #default="{ row }">
              <el-select
                v-model="row.lunchAmount"
                placeholder="请选择"
                class="cute-select small-select"
                @change="handleFieldChange(row, 'lunchAmount')"
              >
                <el-option :label="lunchAmountMap[1]" :value="1" />
                <el-option :label="lunchAmountMap[2]" :value="2" />
                <el-option :label="lunchAmountMap[3]" :value="3" />
                <el-option :label="lunchAmountMap[4]" :value="4" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column prop="waterTimes" label="喝水次数" width="120" align="center">
            <template #header>
              <div class="group-header">
                <span class="group-title">喝水次数</span>
                <el-button type="warning" size="small" circle class="quick-fill-btn" @click="showQuickFill('waterTimes')" title="快速填充">
                  <span>⚡</span>
                </el-button>
              </div>
            </template>
            <template #default="{ row }">
              <el-input-number
                v-model="row.waterTimes"
                :min="0"
                :max="20"
                size="small"
                class="cute-number"
                @change="handleFieldChange(row, 'waterTimes')"
              />
            </template>
          </el-table-column>
        </el-table-column>

        <el-table-column label="午睡情况" align="center" class-name="group-column">
          <template #header>
            <div class="group-header">
              <span class="group-icon">😴</span>
              <span class="group-title">午睡情况</span>
            </div>
          </template>
          <el-table-column prop="sleepTime" label="入睡时间" width="240" align="center">
            <template #default="{ row }">
              <el-time-picker
                v-model="row.sleepTime"
                format="HH:mm"
                value-format="HH:mm"
                size="small"
                class="cute-time-picker"
                placeholder="选择时间"
                @change="handleFieldChange(row, 'sleepTime')"
              />
            </template>
          </el-table-column>
          <el-table-column prop="sleepDuration" label="睡眠时长(分钟)" width="140" align="center">
            <template #default="{ row }">
              <el-input-number
                v-model="row.sleepDuration"
                :min="0"
                :max="300"
                size="small"
                class="cute-number"
                @change="handleFieldChange(row, 'sleepDuration')"
              />
            </template>
          </el-table-column>
          <el-table-column prop="sleepQuality" label="睡眠质量" width="130" align="center">
            <template #header>
              <div class="group-header">
                <span class="group-title">睡眠质量</span>
                <el-button type="warning" size="small" circle class="quick-fill-btn" @click="showQuickFill('sleepQuality')" title="快速填充">
                  <span>⚡</span>
                </el-button>
              </div>
            </template>
            <template #default="{ row }">
              <el-select
                v-model="row.sleepQuality"
                placeholder="请选择"
                class="cute-select small-select"
                @change="handleFieldChange(row, 'sleepQuality')"
              >
                <el-option :label="sleepQualityMap[1]" :value="1" />
                <el-option :label="sleepQualityMap[2]" :value="2" />
                <el-option :label="sleepQualityMap[3]" :value="3" />
              </el-select>
            </template>
          </el-table-column>
        </el-table-column>

        <el-table-column label="如厕情况" align="center" class-name="group-column">
          <template #header>
            <div class="group-header">
              <span class="group-icon">🚽</span>
              <span class="group-title">如厕情况</span>
            </div>
          </template>
          <el-table-column prop="toiletTimes" label="如厕次数" width="120" align="center">
            <template #default="{ row }">
              <el-input-number
                v-model="row.toiletTimes"
                :min="0"
                :max="20"
                size="small"
                class="cute-number"
                @change="handleFieldChange(row, 'toiletTimes')"
              />
            </template>
          </el-table-column>
          <el-table-column prop="toiletRemark" label="异常情况" width="160" align="center">
            <template #default="{ row }">
              <el-input
                v-model="row.toiletRemark"
                placeholder="无异常"
                size="small"
                class="cute-input"
                @blur="handleFieldChange(row, 'toiletRemark')"
              />
            </template>
          </el-table-column>
        </el-table-column>

        <el-table-column label="健康监测" align="center" class-name="group-column">
          <template #header>
            <div class="group-header">
              <span class="group-icon">🌡️</span>
              <span class="group-title">健康监测</span>
            </div>
          </template>
          <el-table-column prop="morningTemp" label="晨检体温(℃)" width="140" align="center">
            <template #default="{ row }">
              <el-input-number
                v-model="row.morningTemp"
                :min="35"
                :max="42"
                :step="0.1"
                :precision="1"
                size="small"
                class="cute-number temp-number"
                @change="handleFieldChange(row, 'morningTemp')"
              />
            </template>
          </el-table-column>
          <el-table-column prop="afternoonTemp" label="午检体温(℃)" width="140" align="center">
            <template #default="{ row }">
              <el-input-number
                v-model="row.afternoonTemp"
                :min="35"
                :max="42"
                :step="0.1"
                :precision="1"
                size="small"
                class="cute-number temp-number"
                @change="handleFieldChange(row, 'afternoonTemp')"
              />
            </template>
          </el-table-column>
          <el-table-column prop="discomfort" label="不适症状" width="160" align="center">
            <template #default="{ row }">
              <el-input
                v-model="row.discomfort"
                placeholder="无"
                size="small"
                class="cute-input"
                @blur="handleFieldChange(row, 'discomfort')"
              />
            </template>
          </el-table-column>
        </el-table-column>

        <el-table-column label="备注" align="center" class-name="group-column">
          <template #header>
            <div class="group-header">
              <span class="group-icon">📝</span>
              <span class="group-title">备注</span>
            </div>
          </template>
          <el-table-column prop="teacherRemark" label="教师备注" width="200" align="center">
            <template #default="{ row }">
              <el-input
                v-model="row.teacherRemark"
                type="textarea"
                :rows="2"
                placeholder="请输入备注"
                size="small"
                class="cute-textarea"
                @blur="handleFieldChange(row, 'teacherRemark')"
              />
            </template>
          </el-table-column>
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

    <el-dialog v-model="quickFillDialogVisible" title="快速填充确认" width="400px" class="cute-dialog">
      <div class="quick-fill-content">
        <p>确定要快速填充 <strong>{{ quickFillTarget.label }}</strong> 吗？</p>
        <p class="quick-fill-info">将填充所有幼儿的 <strong>{{ quickFillTarget.value }}</strong> 字段</p>
      </div>
      <template #footer>
        <el-button class="cute-btn" @click="quickFillDialogVisible = false">取消</el-button>
        <el-button type="primary" class="cute-btn primary" @click="confirmQuickFill">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { getLifeRecordPage, saveLifeRecord, batchSaveLifeRecord } from '@/utils/api';
import { ElMessage } from 'element-plus';
import request from '@/utils/request';

export default {
  name: 'LifeRecord',
  data() {
    return {
      list: [],
      total: 0,
      listLoading: true,
      classList: [],
      tableRef: null,
      syncStatus: 'synced',
      savingRows: new Set(),
      listQuery: {
        classId: undefined,
        date: this.formatDate(new Date()),
        page: 1,
        size: 10
      },
      lunchAmountMap: {
        1: '1-全吃光',
        2: '2-吃大半',
        3: '3-吃小半',
        4: '4-没胃口'
      },
      sleepQualityMap: {
        1: '1-深睡',
        2: '2-浅睡',
        3: '3-易醒'
      },
      quickFillDialogVisible: false,
      quickFillTarget: {
        field: '',
        label: '',
        value: ''
      },
      quickFillOptions: {
        lunch: { field: 'lunchAmount', label: '午餐摄入量', value: '午餐摄入量' },
        waterTimes: { field: 'waterTimes', label: '喝水次数', value: '喝水次数（默认3次）' },
        sleepQuality: { field: 'sleepQuality', label: '睡眠质量', value: '睡眠质量（默认深睡）' }
      }
    };
  },
  computed: {
    syncIcon() {
      return { synced: '🟢', syncing: '🟡', failed: '🔴' }[this.syncStatus] || '🟢';
    },
    syncText() {
      return { synced: '已同步', syncing: '同步中', failed: '同步失败' }[this.syncStatus] || '已同步';
    },
    syncStatusClass() {
      return `sync-${this.syncStatus}`;
    }
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

      getLifeRecordPage(params).then(res => {
        const records = res.data.records || [];
        this.list = records.map(item => ({
          ...item,
          lunchAmount: item.lunchIntake,
          sleepTime: item.sleepStartTime,
          waterTimes: item.waterCount,
          toiletTimes: item.toiletCount,
          toiletRemark: item.toiletAbnormal,
          discomfort: item.healthSymptoms
        }));
        this.total = res.data.total || 0;
        this.savingRows.clear();
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
    handleFieldChange(row, field) {
      if (this.savingRows.has(row.studentId)) {
        return;
      }
      this.savingRows.add(row.studentId);
      this.syncStatus = 'syncing';

      const fieldMap = {
        lunchAmount: 'lunchIntake',
        sleepTime: 'sleepStartTime',
        waterTimes: 'waterCount',
        toiletTimes: 'toiletCount',
        toiletRemark: 'toiletAbnormal',
        discomfort: 'healthSymptoms'
      };

      const data = {
        studentId: row.studentId,
        classId: this.listQuery.classId,
        recordDate: this.listQuery.date,
        [fieldMap[field] || field]: row[field]
      };

      saveLifeRecord(data).then((res) => {
        this.syncStatus = 'synced';
        row.saved = true;
        setTimeout(() => {
          row.saved = false;
        }, 2000);
      }).catch((err) => {
        this.syncStatus = 'failed';
      }).finally(() => {
        this.savingRows.delete(row.studentId);
      });
    },
    showQuickFill(type) {
      this.quickFillTarget = this.quickFillOptions[type];
      this.quickFillTarget.type = type;
      this.quickFillDialogVisible = true;
    },
    confirmQuickFill() {
      const type = this.quickFillTarget.type;
      let defaultValue;
      switch (type) {
        case 'lunch': defaultValue = 1; break;
        case 'waterTimes': defaultValue = 3; break;
        case 'sleepQuality': defaultValue = 1; break;
        default: defaultValue = null;
      }

      if (defaultValue === null) {
        this.quickFillDialogVisible = false;
        return;
      }

      this.syncStatus = 'syncing';
      const fieldMap = {
        sleepTime: 'sleepStartTime',
        waterTimes: 'waterCount',
        toiletTimes: 'toiletCount'
      };
      const field = this.quickFillTarget.field;
      const backendField = fieldMap[field] || field;
      const records = this.list.map(item => ({
        studentId: item.studentId,
        [backendField]: defaultValue
      }));

      batchSaveLifeRecord({
        classId: this.listQuery.classId,
        recordDate: this.listQuery.date,
        records: records
      }).then(() => {
        ElMessage.success('🎉 快速填充成功');
        this.quickFillDialogVisible = false;
        this.getList();
      }).catch(() => {
        this.syncStatus = 'failed';
        ElMessage.error('快速填充失败');
      });
    },
    getGradeEmoji(grade) {
      const map = { 1: '🍼', 2: '👶', 3: '🎈', 4: '🎓' };
      return map[grade] || '🏫';
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
    }
  }
};
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=ZCOOL+KuaiLe&family=Noto+Sans+SC:wght@400;500;700&display=swap');

.page-container { padding: 0; position: relative; }
.page-header { text-align: center; padding: 20px 0 24px; position: relative; }
.header-decoration { position: absolute; top: 10px; left: 50%; transform: translateX(-50%); }
.deco-flower { font-size: 24px; opacity: 0.3; animation: flowerFloat 3s ease-in-out infinite; }
@keyframes flowerFloat { 0%, 100% { transform: translateY(0) rotate(0deg); } 50% { transform: translateY(-5px) rotate(10deg); } }
.sync-status { position: absolute; top: 20px; right: 20px; padding: 6px 12px; border-radius: 20px; font-size: 13px; display: flex; align-items: center; gap: 4px; }
.sync-synced { background: rgba(103, 194, 58, 0.15); color: #67C23A; }
.sync-syncing { background: rgba(230, 162, 60, 0.15); color: #E6A23C; }
.sync-failed { background: rgba(245, 108, 108, 0.15); color: #F56C6C; }
.sync-icon { font-size: 14px; }
.page-title { font-family: 'ZCOOL KuaiLe', 'Noto Sans SC', sans-serif; font-size: 28px; color: #5D4E4E; margin: 0 0 8px; }
.page-subtitle { font-size: 14px; color: #9B8E8E; margin: 0; }
.content-card { border-radius: 20px; border: none; box-shadow: 0 8px 30px rgba(255, 182, 193, 0.15); }
.search-form { padding: 20px; background: linear-gradient(135deg, #FFF9F5 0%, #FFF0F0 100%); border-radius: 16px; margin-bottom: 20px; }
.search-item { margin-bottom: 0; }
.search-actions { margin-left: auto; }
.class-select { min-width: 160px; }
.cute-select :deep(.el-select__wrapper) { border-radius: 12px; box-shadow: 0 0 0 1px #F0E0E0 inset; background: #fff; }
.cute-select.small-select { width: 100%; }
.cute-select.small-select :deep(.el-select__wrapper) { border-radius: 8px; min-height: 32px; }
.cute-date-picker { width: 160px; }
.cute-date-picker :deep(.el-input__wrapper) { border-radius: 12px; box-shadow: 0 0 0 1px #F0E0E0 inset; background: #fff; }
.cute-btn { border-radius: 12px; padding: 10px 20px; font-family: 'Noto Sans SC', sans-serif; font-weight: 500; border: none; transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1); display: inline-flex; align-items: center; gap: 6px; background: linear-gradient(135deg, #FFF0F0 0%, #FFE8E8 100%); color: #5D4E4E; }
.cute-btn:hover { transform: translateY(-2px); box-shadow: 0 6px 20px rgba(255, 107, 107, 0.2); }
.cute-btn.primary { background: linear-gradient(135deg, #FFB5B5 0%, #FF8E8E 100%); color: #fff; }
.cute-btn.primary:hover { background: linear-gradient(135deg, #FF6B6B 0%, #FF8E53 100%); box-shadow: 0 8px 25px rgba(255, 107, 107, 0.35); }
.cute-table { border-radius: 16px; overflow: visible; }
.cute-table :deep(.el-table__header-wrapper th) { background: linear-gradient(135deg, #FFF0F0 0%, #FFE8E8 100%) !important; color: #5D4E4E; font-weight: 600; }
.cute-table :deep(.el-table__row) { transition: all 0.3s; }
.cute-table :deep(.el-table__row:hover) { background: rgba(255, 230, 230, 0.5) !important; }
.cute-table :deep(.el-table__body-wrapper) { overflow-x: auto; }
.cute-table :deep(.el-table__body-wrapper .el-table__body) { width: max-content !important; }
.cute-table :deep(.group-column) { background: rgba(255, 240, 245, 0.3); }
.group-header { display: flex; align-items: center; justify-content: center; gap: 6px; }
.group-icon { font-size: 16px; }
.group-title { font-weight: 600; }
.quick-fill-btn { width: 24px; height: 24px; padding: 0; display: flex; align-items: center; justify-content: center; background: linear-gradient(135deg, #FFD700 0%, #FFA500 100%); border: none; }
.quick-fill-btn:hover { transform: scale(1.1); }
.quick-fill-btn span { font-size: 12px; }
.row-number { display: inline-flex; align-items: center; justify-content: center; width: 28px; height: 28px; background: linear-gradient(135deg, #FFF0F0 0%, #FFE8E8 100%); border-radius: 50%; font-size: 12px; font-weight: 600; color: #FF6B6B; }
.student-info { display: flex; align-items: center; justify-content: center; gap: 8px; }
.student-icon { font-size: 18px; }
.student-name { font-weight: 600; color: #5D4E4E; }
.cute-number { width: 100%; }
.cute-number :deep(.el-input__wrapper) { border-radius: 8px; }
.cute-number.temp-number :deep(.el-input__inner) { color: #5D4E4E; }
.cute-input :deep(.el-input__wrapper) { border-radius: 8px; }
.cute-textarea :deep(.el-textarea__inner) { border-radius: 8px; resize: none; }
.cute-time-picker { width: 100%; }
.cute-time-picker :deep(.el-input__wrapper) { border-radius: 8px; }
.quick-fill-content { text-align: center; padding: 20px; color: #5D4E4E; }
.quick-fill-content p { margin: 10px 0; }
.quick-fill-info { color: #9B8E8E; font-size: 13px; }
.cute-dialog :deep(.el-dialog) { border-radius: 20px; }
.cute-dialog :deep(.el-dialog__header) { background: linear-gradient(135deg, #FFF0F0 0%, #FFE8E8 100%); border-radius: 20px 20px 0 0; }
.cute-dialog :deep(.el-dialog__title) { color: #5D4E4E; font-weight: 600; }
.pagination-container { padding: 20px; display: flex; justify-content: flex-end; }
.cute-pagination :deep(.el-pager li.is-active) { background: linear-gradient(135deg, #FFB5B5 0%, #FF8E8E 100%) !important; }

@media (max-width: 768px) {
  .page-title { font-size: 22px; }
  .sync-status { position: static; margin-bottom: 10px; display: inline-flex; }
  .search-form :deep(.el-form-item) { display: block; margin-bottom: 12px; }
  .search-actions { margin-left: 0; display: flex; gap: 8px; }
}
</style>
