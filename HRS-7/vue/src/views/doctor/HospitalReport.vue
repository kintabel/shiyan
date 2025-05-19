<template>
  <div class="doctor-page-table">
    <div v-if="isLoading" class="loading-container">
      <el-spinner />
      <span>正在加载数据...</span>
    </div>
    <div v-else>
      <el-table :data="transformData(report)" style="width: 100%;">
        <el-table-column>
          <template slot-scope="scope">
            <span>{{ scope.row.label }}</span>
          </template>
        </el-table-column>
        <el-table-column>
          <template slot-scope="scope">
            <span>{{ scope.row.value }}</span>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>

</template>

<script>
import { parseTime } from '@/utils'

export default {
  name: 'HospitalReport-doctor',
  data() {
    return {
      report: null,
      isLoading: true,
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      reportId: this.$route.query.id,
      keyToLabel: {
        reportType: '报告类型',
        userName: '患者姓名',
        patientGender: '患者性别',
        patientAge: '患者年龄',
        patientHeight: '患者身高',
        patientWeight: '患者体重',
        doctorName: '主治医师',
        hospitalName: '医院',
        departmentName: '科室',
        bloodPressure: '血压',
        heartRate: '心率',
        completeBloodCount: '血常规',
        urinalysis: '尿常规',
        contagiousDisease: '传染病史',
        informationId: '其他信息'
      }
    }
  },

  async created() {
    const reportId = this.$route.query.id;
    await this.fetchHospitalizationReport(reportId);
  },
  methods: {
    transformData(data) {
      return Object.entries(data)
          .filter(([key]) => key!== 'id')
          .map(([key, value]) => ({
            label: this.keyToLabel[key] || key,
            value
          }));
    },
    async fetchHospitalizationReport(reportId) {
      try {
        const response = await this.$request.get(`/user/selectReport/${reportId}`);
        if (response.data) {
          this.report = response.data;
          console.log('报告单:', this.report);
        } else {
          this.handleError('获取报告单详细信息失败', response.data);
        }
      } catch (error) {
        this.handleError('获取报告单详细信息失败', error);
      } finally {
        this.isLoading = false;
      }
    },

    handleError(message, error) {
      console.error(message, error);
      this.$message.error(message);
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
  display: none;
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