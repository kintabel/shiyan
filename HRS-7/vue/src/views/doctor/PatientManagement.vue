<template>
  <div class="patient-management">
    <h2>患者管理</h2>

    <!-- 搜索和筛选区域 -->
    <div class="filter-container">

      <el-select
          v-model="filterReportType"
          placeholder="选择报告类型"
          clearable
          style="margin-left: 10px; width: 180px"
          @change="handleFilter"
      >
        <el-option
            v-for="type in reportTypes"
            :key="type"
            :label="type"
            :value="type"
        ></el-option>
      </el-select>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading">加载中...</div>

    <!-- 错误提示 -->
    <div v-if="error" class="error">{{ error }}</div>

    <!-- 患者信息表格 -->
    <el-table
        v-if="patients.length > 0"
        :data="patients"
        stripe
        style="width: 100%"
        :default-sort="{ prop: 'id', order: 'descending' }"
    >
      <el-table-column prop="id" label="id" width="120"></el-table-column>
      <!-- 患者姓名 -->
      <el-table-column prop="userName" label="姓名" width="120"></el-table-column>

      <!-- 患者年龄 -->
      <el-table-column prop="patientAge" label="年龄" width="80"></el-table-column>

      <!-- 患者性别 -->
      <el-table-column prop="patientGender" label="性别" width="80"></el-table-column>

      <!-- 报告类型 -->
      <el-table-column prop="reportType" label="报告类型" width="150"></el-table-column>

      <!-- 医院名称 -->
      <el-table-column prop="hospitalName" label="医院" width="180"></el-table-column>

      <!-- 科室名称 -->
      <el-table-column prop="departmentName" label="科室" width="150"></el-table-column>

      <!-- 操作 -->
      <el-table-column label="操作" width="300">
        <template slot-scope="scope">
          <el-button
              type="primary"
              size="small"
              @click="handleViewDetails(scope.row)"
          >查看详情
          </el-button>

          <template v-if="scope.row.reportType === '入院报告'">
            <el-button
                type="success"
                size="small"
                @click="admission(scope.row)"
            >入院办理
            </el-button>
            <el-button
                type="warning"
                size="small"
                @click="discharge(scope.row)"
            >出院办理
            </el-button>
          </template>
        </template>
      </el-table-column>
    </el-table>

    <!-- 无数据提示 -->
    <div v-else class="no-data">暂无患者数据</div>

    <!-- 分页 -->
    <el-pagination
        background
        layout="prev, pager, next, sizes, total"
        :current-page="currentPage"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="pageSize"
        :total="totalPatients"
        @current-change="handlePageChange"
        @size-change="handleSizeChange"
    ></el-pagination>

    <!-- 患者详情对话框 -->
    <el-dialog
        title="患者详情"
        :visible.sync="detailDialogVisible"
        width="60%"
        :before-close="handleCloseDialog"
    >
      <div v-if="currentPatient" class="patient-detail">
        <!-- 基本信息 -->
        <div class="detail-section">
          <h3>基本信息</h3>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="姓名">{{ currentPatient.userName }}</el-descriptions-item>
            <el-descriptions-item label="年龄">{{ currentPatient.patientAge }}</el-descriptions-item>
            <el-descriptions-item label="性别">{{ currentPatient.patientGender }}</el-descriptions-item>
            <el-descriptions-item label="身高">{{ currentPatient.patientHeight }} cm</el-descriptions-item>
            <el-descriptions-item label="体重">{{ currentPatient.patientWeight }} kg</el-descriptions-item>
            <el-descriptions-item label="报告类型">{{ currentPatient.reportType }}</el-descriptions-item>
          </el-descriptions>
        </div>

        <!-- 医疗信息 -->
        <div class="detail-section">
          <h3>医疗信息</h3>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="医院">{{ currentPatient.hospitalName }}</el-descriptions-item>
            <el-descriptions-item label="科室">{{ currentPatient.departmentName }}</el-descriptions-item>
            <el-descriptions-item label="医生">{{ currentPatient.doctorName }}</el-descriptions-item>
            <el-descriptions-item label="血压">{{ currentPatient.bloodPressure }}</el-descriptions-item>
            <el-descriptions-item label="心率">{{ currentPatient.heartRate }}</el-descriptions-item>
          </el-descriptions>
        </div>

        <!-- 检查结果 -->
        <div class="detail-section">
          <h3>检查结果</h3>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="血常规">{{ currentPatient.completeBloodCount }}</el-descriptions-item>
            <el-descriptions-item label="尿常规">{{ currentPatient.urinalysis }}</el-descriptions-item>
            <el-descriptions-item label="传染病">{{ currentPatient.contagiousDisease }}</el-descriptions-item>
          </el-descriptions>
        </div>
      </div>

      <span slot="footer" class="dialog-footer">
        <el-button @click="detailDialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="detailDialogVisible = false">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'PatientManagement',
  data() {
    return {
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'), // 当前用户信息
      patients: [], // 患者列表数据
      currentPatient: null, // 当前查看的患者详情
      loading: false, // 加载状态
      error: '', // 错误信息
      currentPage: 1, // 当前页码
      pageSize: 10, // 每页显示条数
      totalPatients: 0, // 总条数
      filterReportType: '', // 报告类型筛选
      reportTypes: ['入院报告', '出院报告'], // 报告类型选项
      detailDialogVisible: false // 详情对话框显示状态
    }
  },
  created() {
    this.fetchPatients()
  },
  methods: {
    // 获取患者列表
    fetchPatients() {
      this.loading = true
      this.error = ''

      this.$request.get('/report/selectReportByDoctorId', {
        params: {
          pageNum: this.currentPage,
          pageSize: this.pageSize,
          doctorUserId: this.user.id,
          reportType: this.filterReportType
        }
      })
          .then(response => {
            if (response.data && response.data.list) {
              this.patients = response.data.list
            }
            if (response.data && response.data.total) {
              this.totalPatients = response.data.total
            }
          })
          .catch(error => {
            console.error('获取患者列表失败:', error)
            this.error = '获取患者列表失败，请稍后重试'
          })
          .finally(() => {
            this.loading = false
          })
    },

    // 分页切换
    handlePageChange(newPage) {
      this.currentPage = newPage
      this.fetchPatients()
    },

    // 每页条数改变
    handleSizeChange(newSize) {
      this.pageSize = newSize
      this.fetchPatients()
    },

    // 搜索患者
    handleSearch() {
      this.currentPage = 1
      this.fetchPatients()
    },

    // 筛选患者
    handleFilter() {
      this.currentPage = 1
      this.fetchPatients()
    },

    // 查看患者详情
    handleViewDetails(row) {
      this.currentPatient = row
      this.detailDialogVisible = true
    },

    // 关闭对话框
    handleCloseDialog(done) {
      this.$confirm('确认关闭？')
          .then(_ => {
            done()
          })
          .catch(_ => {
          })
    },

    admission(row) {
      this.$router.push({
            name: 'Admission',
            params: {
              id:row.id,
              userName:row.userName,
              departmentName:row.departmentName,
              hospitalName:row.hospitalName,
              doctorName:row.doctorName
            }
          }
      )
    },
    discharge(row) {
      this.$prompt('请输入出院医嘱', '出院办理', {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        inputType: 'textarea',
        inputPlaceholder: '请输入详细的出院医嘱...',
        inputValidator: (value) => {
          if (!value || value.trim().length === 0) {
            return '出院医嘱不能为空';
          }
        }
      }).then(({ value }) => {
        const dischargeOrders = value.trim();
        this.loading = true;

        // 直接传递参数，不使用DTO格式
        this.$request.post('/doctor/patient/discharge', null, {
          params: {
            reportId: row.id,
            dischargeOrders: dischargeOrders
          }
        }).then(response => {
          if (response.code == 200) {
            this.$message.success('出院办理成功');
            this.fetchPatients(); // 刷新列表
          } else {
            this.$message.error(response.message || '出院办理失败');
          }
        }).catch(error => {
          console.error('出院办理失败:', error);
          this.$message.error('出院办理失败');
        }).finally(() => {
          this.loading = false;
        });
      }).catch(() => {
        this.$message.info('已取消出院办理');
      });
    }
  }
}
</script>

<style scoped>
.patient-management {
  padding: 20px;
}

.filter-container {
  margin-bottom: 20px;
}

.loading,
.error,
.no-data {
  text-align: center;
  margin: 20px 0;
  font-size: 16px;
  color: #666;
}

.error {
  color: #f56c6c;
}

.patient-detail {
  padding: 0 20px;
}

.detail-section {
  margin-bottom: 30px;
}

.detail-section h3 {
  margin-bottom: 15px;
  color: #409eff;
  font-size: 16px;
}

.el-pagination {
  margin-top: 20px;
  text-align: right;
}
</style>