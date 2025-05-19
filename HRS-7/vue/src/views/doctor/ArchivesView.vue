<template>
  <div class="doctor-page-container">
  <div class="doctor-page-table">
    <div v-if="isLoading" class="loading-container">
      <el-spinner />
      <span>正在加载数据...</span>
    </div>
    <div v-else>
      <el-table :data="transformData(hospitalizationRecord)" style="width: 100%;">
        <el-table-column>
          <template slot-scope="scope">
            <span>{{ scope.row.label }}</span>
          </template>
        </el-table-column>
        <el-table-column>
          <template slot-scope="scope">
        <span v-if="!['inReportId', 'outReportId', 'dailySituationId', 'costId'].includes(scope.row.key)">
          {{ scope.row.value }}
        </span>
            <el-button v-else-if="scope.row.key === 'inReportId'" @click="viewInReport(scope.row.value)">查看入院报告</el-button>
            <el-button v-else-if="scope.row.key === 'outReportId'" @click="viewOutReport(scope.row.value)">查看出院报告</el-button>
            <el-button
                v-else-if="scope.row.key === 'dailySituationId'"
                @click="viewDailySituation(hospitalizationRecord.inReportId)">
              查看每日情况
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>

  <el-dialog
      title="每日住院详情"
      :visible.sync="dailyDialogVisible"
      width="70%"
      center>
    <el-table :data="dailyCheckLists" border style="width: 100%">
      <el-table-column prop="date" label="日期" width="120"></el-table-column>
      <el-table-column prop="medicine" label="用药情况"></el-table-column>
      <el-table-column prop="transfusion" label="输液情况"></el-table-column>
      <el-table-column prop="surgery" label="手术情况"></el-table-column>
      <el-table-column prop="surgeryTime" label="手术时间" width="150"></el-table-column>
      <el-table-column prop="dailySituation" label="每日情况"></el-table-column>
      <el-table-column prop="cost" label="花费" width="100"></el-table-column>
    </el-table>
    <span slot="footer" class="dialog-footer">
        <el-button @click="dailyDialogVisible = false">关闭</el-button>
      </span>
  </el-dialog>
  </div>
</template>

<script>
import { parseTime } from '@/utils'

export default {
  name: 'ArchivesView',
  data() {
    return {
      hospitalizationRecord: null,
      isLoading: true,
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      keyToLabel: {
        userName: '患者姓名',
        doctorName: '医生姓名',
        hospitalName: '医院',
        departmentName: '科室',
        admissionTime: '入院时间',
        totalCost: '总花费',
        dischargeTime: '出院时间',
        disease: '疾病名称',
        dischargeOrders: '出院医嘱',
        inReportId: '入院报告',
        outReportId: '出院报告',
        surgery: '手术',
        dailySituationId: '每日住院详情',
      },
      dailyDialogVisible: false,
      dailyCheckLists: []
    }
  },

  async created() {
    const hospitalizationId = this.$route.query.hospitalizationId;
    await this.fetchHospitalizationRecord(hospitalizationId);
  },
  methods: {
    transformData(data) {
      return Object.entries(data).map(([key, value]) => ({
        key,
        label: this.keyToLabel[key],
        value
      }));
    },
    async fetchHospitalizationRecord(hospitalizationId) {
      try {
        const response = await this.$request.get(`/user/selectHospitalizationById/${hospitalizationId}`);
        if (response.data) {
          this.hospitalizationRecord = response.data;
        } else {
          this.handleError('获取住院详细信息失败', response.data);
        }
      } catch (error) {
        this.handleError('获取住院详细信息失败', error);
      } finally {
        this.isLoading = false;
      }
    },
    handleError(message, error) {
      console.error(message, error);
      this.$message.error(message);
    },
    viewInReport(id) {
      console.log('查看入院报告，ID:', id);
      this.$router.push({
        path: '/archives-management/archives-view/doctor-report',
        query: { id: id }
      })
    },
    viewOutReport(id) {
      console.log('查看出院报告，ID:', id);
      this.$router.push({
        path: '/archives-management/archives-view/doctor-report',
        query: { id: id }
      })
    },
    async viewDailySituation(id) {
      try {
        this.isLoading = true;
        const response = await this.$request.get(`/user/dailyCheckList/getByHospitalizationId/${id}`);
        if (response.data && response.data.length > 0) {
          this.dailyCheckLists = response.data;
          this.dailyDialogVisible = true;
        } else {
          this.$message.warning('暂无每日住院详情数据');
        }
      } catch (error) {
        this.handleError('获取每日住院详情失败', error);
      } finally {
        this.isLoading = false;
      }
    }
  }
}
</script>

<style scoped>
.doctor-page-table {
  padding: 20px;
}

.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 50px;
  margin-bottom: 20px;
}

.el-button.is-disabled {
  cursor: not-allowed;
}

.el-table {
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  overflow: hidden;
}

.el-table__header-wrapper {
  background-color: #f4f4f4;
}

.el-table__row--striped {
  background-color: #f9fafc;
}

.el-table__cell {
  border-right: 1px solid #e4e7ed;
  border-bottom: 1px solid #e4e7ed;
  padding: 10px;
}

.el-table__cell:last-child {
  border-right: none;
}

.el-table__row:last-child .el-table__cell {
  border-bottom: none;
}
</style>