<template>
  <div class="doctor-page-table">
    <div v-if="isLoading" class="loading-container">
      <el-spinner />
      <span>正在加载数据...</span>
    </div>
        <h4>住院档案</h4>
        <el-table :data="hospitalizationRecords" stripe>
          <el-table-column  label="医院">
            <template slot-scope="scope">
              <span>{{ scope.row.hospitalName }}</span>
            </template>
          </el-table-column>

          <el-table-column  label="科室">
            <template slot-scope="scope">
              <span>{{ scope.row.departmentName }}</span>
            </template>
          </el-table-column>

          <el-table-column  label="医生">
            <template slot-scope="scope">
              <span>{{ scope.row.doctorName }}</span>
            </template>
          </el-table-column>

          <el-table-column  label="病人姓名">
            <template slot-scope="scope">
              <span>{{ scope.row.userName }}</span>
            </template>
            </el-table-column>

          <el-table-column label="操作">
            <template slot-scope="scope">
              <el-button @click="viewArchive(scope.row.id)">查看档案</el-button>
              <el-button @click="addArchive(scope.row.id)">添加信息</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
</template>

<script>
import { parseTime } from '@/utils'

export default {
  name: 'ArchivesManagement',
  data() {
    return {
      userInfo: null,
      isLoading: true,
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      hospitalizationRecords: [],
    }
  },

  async created() {
    const doctorId = this.user.id;
    try {
      await this.fetchHospitalizationRecords(doctorId);
    } catch (error) {
      console.error('数据加载出错:', error);
    }
  },
  methods: {

    async fetchHospitalizationRecords(doctorId) {
      try {
        const response = await this.$request.get(`/doctor/selectArchives/${doctorId}`);
        if (response.data) {
          this.hospitalizationRecords = response.data;
          console.log('住院记录:', this.hospitalizationRecords);
        } else {
          this.handleError('获取住院详细信息失败', response.data);
        }
      } catch (error) {
        this.handleError('获取住院详细信息失败', error);
      } finally {
        this.isLoading = false;
      }
    },

    addArchive(hospitalizationId){
      console.log('添加信息:', hospitalizationId);
      this.$router.push({
        path: 'archivesAddDoctor',
        query: { hospitalizationId: hospitalizationId }
      })
    },

    handleError(message, error) {
      console.error(message, error);
      this.$message.error(message);
    },

    viewArchive(hospitalizationId) {
      console.log('查看档案:', hospitalizationId);
      this.$router.push({
        path: 'archivesViewDoctor',
        query: { hospitalizationId: hospitalizationId }
      })
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
</style>