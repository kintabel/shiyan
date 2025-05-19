<template>
  <div class="doctor-page-table">
    <div v-if="isLoading" class="loading-container">
      <el-spinner />
      <span>正在加载数据...</span>
    </div>
    <div v-else>
      <el-table :data="[userInfo]" stripe>
        <el-table-column label="姓名">
          <template slot-scope="scope">
            <span>{{ scope.row.name }}</span>
          </template>
        </el-table-column>
        <el-table-column label="联系方式">
          <template slot-scope="scope">
            <span>{{ scope.row.phone }}</span>
          </template>
        </el-table-column>
        <el-table-column label="邮箱">
          <template slot-scope="scope">
            <span>{{ scope.row.email }}</span>
          </template>
        </el-table-column>
        <el-table-column label="性别">
          <template slot-scope="scope">
            <span>{{ scope.row.gender }}</span>
          </template>
        </el-table-column>
      </el-table>

      <div v-if="hospitalizationRecords != null">
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

          <el-table-column label="操作">
            <template slot-scope="scope">
              <el-button @click="viewArchive(scope.row.id)">查看档案</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
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
    const userId = this.user.id;
    try {
      await this.fetchUser(userId);
      await this.fetchHospitalizationRecords(userId);
    } catch (error) {
      console.error('数据加载出错:', error);
    }
  },
  methods: {
    async fetchUser(userId) {
      try {
        const response = await this.$request.get(`/user/selectById/${userId}`);
        if (response.data) {
          this.userInfo = response.data;
        } else {
          this.handleError('获取用户详细信息失败', response.data);
        }
      } catch (error) {
        this.handleError('获取用户详细信息失败', error);
      } finally {
        this.isLoading = false;
      }
    },

    async fetchHospitalizationRecords(userId) {
      try {
        const response = await this.$request.get(`/user/selectArchives/${userId}`);
        if (response.data) {
          this.hospitalizationRecords = response.data;
          console.log('住院记录:', this.hospitalizationRecords);
        } else {
          this.handleError('获取住院详细信息失败', response.data);
        }
      } catch (error) {
        this.handleError('获取住院详细信息失败', error);
      }
    },

    handleError(message, error) {
      console.error(message, error);
      this.$message.error(message);
    },

    viewArchive(hospitalizationId) {
      console.log('查看档案:', hospitalizationId);
      this.$router.push({
        path: '/archives-management/archives-view',
        query: { informationId: hospitalizationId }
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