<template>
  <div class="today-appointments">
    <h2>开药管理</h2>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading">加载中...</div>

    <!-- 错误提示 -->
    <div v-if="error" class="error">{{ error }}</div>

    <!-- 挂号信息表格 -->
    <el-table v-if="prescribes.length > 0" :data="prescribes" stripe>
      <el-table-column prop="order" label="订单号" width="180"></el-table-column>
      <!-- 患者姓名 -->
      <el-table-column prop="userName" label="患者姓名" width="120"></el-table-column>

      <!-- 患者性别 -->
      <el-table-column prop="userGender" label="性别" width="80"></el-table-column>

      <!-- 手机号（脱敏显示） -->
      <el-table-column label="手机号" width="140">
        <template slot-scope="scope">
          {{ formatPhone(scope.row.userPhone) }}
        </template>
      </el-table-column>

      <!-- 预约时间 -->
      <el-table-column prop="date" label="开药时间" width="180"></el-table-column>

      <!-- 操作 -->
      <el-table-column label="操作" width="200">
        <template slot-scope="scope">
          <el-button type="primary" size="small" @click="handleViewDetails(scope.row)">查看详情</el-button>

        </template>
      </el-table-column>
    </el-table>

    <!-- 无数据提示 -->
    <div v-else class="no-data">无开药信息</div>

    <!-- 分页 -->
    <el-pagination
        background
        layout="prev, pager, next"
        :current-page="currentPage"
        :page-size="pageSize"
        :total="totalPrescribes"
        @current-change="handlePageChange"
    ></el-pagination>
  </div>
</template>

<script>
export default {
  name: 'PrescribingAdministration',
  data() {
    return {
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'), // 当前用户信息
      prescribes: [],
      loading: false, // 加载状态
      error: '', // 错误信息
      currentPage: 1, // 当前页码
      pageSize: 10, // 每页显示条数
      totalPrescribes: 0 // 总条数
    };
  },
  created() {
    this.fetchPrescribingAdministration(1); // 初始化加载今日预约信息
  },
  methods: {
    // 获取今日预约信息
    fetchPrescribingAdministration(pageNumber) {
      this.loading = true;
      this.error = '';
      this.currentPage = pageNumber;

      this.$request.get('/prescribe/selectPrescribe', {
        params: {
          pageNum: this.currentPage,
          pageSize: this.pageSize,
          userId: this.user.id // 传递当前用户 ID
        }
      })
          .then(response => {
            if (response.data && response.data.list) {
              this.prescribes = response.data.list;
            }
            if (response.data && response.data.total) {
              this.totalPrescribes = response.data.total;
            }
          })
          .catch(error => {
            console.error('获取开药信息失败:', error);
            this.error = '获取开药失败，请稍后重试';
          })
          .finally(() => {
            this.loading = false;
          });
    },

    // 分页切换
    handlePageChange(newPage) {
      this.fetchTodayAppointments(newPage);
    },

    // 查看开药详情
    handleViewDetails(row) {
      console.log(row.order);
      this.$router.push({
        name: 'PrescribeDetail',
        params: {
          orderId: row.order
        }
      });
    },


    // 格式化手机号（脱敏显示）
    formatPhone(phone) {
      if (phone && phone.length === 11) {
        return phone.substring(0, 3) + '****' + phone.substring(7);
      }
      return phone;
    },
  }
};
</script>

<style scoped>
.today-appointments {
  padding: 20px;
}

.loading,
.error,
.no-data {
  text-align: center;
  margin-top: 20px;
  font-size: 16px;
  color: #666;
}

.error {
  color: #f56c6c;
}

.completed-text {
  display: inline-block;
  padding: 4px 8px;
  background-color: #e8f5e9; /* 浅绿色背景 */
  color: #4caf50; /* 绿色文字 */
  border-radius: 4px; /* 圆角 */
  font-size: 12px; /* 调整字体大小 */
  font-weight: bold; /* 加粗字体 */
  margin-left: 10px
}
</style>