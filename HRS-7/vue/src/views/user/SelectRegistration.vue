<template>
  <div class="today-appointments">
    <h2>挂号信息</h2>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading">加载中...</div>

    <!-- 错误提示 -->
    <div v-if="error" class="error">{{ error }}</div>

    <!-- 挂号信息表格 -->
    <el-table v-if="registrations.length > 0" :data="registrations" stripe>
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
      <el-table-column prop="registerTime" label="预约时间" width="180"></el-table-column>

      <!-- 操作 -->
      <el-table-column label="操作" width="200">
        <template slot-scope="scope">
          <el-button type="primary" size="small" @click="handleViewDetails(scope.row)">查看详情</el-button>

        </template>
      </el-table-column>
    </el-table>

    <!-- 无数据提示 -->
    <div v-else class="no-data">暂无挂号信息</div>

    <!-- 分页 -->
    <el-pagination
        background
        layout="prev, pager, next"
        :current-page="currentPage"
        :page-size="pageSize"
        :total="totalRegistrations"
        @current-change="handlePageChange"
    ></el-pagination>
  </div>
</template>

<script>
export default {
  name: 'SelectRegistration',
  data() {
    return {
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'), // 当前用户信息
      registrations: [], // 挂号信息列表
      loading: false, // 加载状态
      error: '', // 错误信息
      currentPage: 1, // 当前页码
      pageSize: 10, // 每页显示条数
      totalRegistrations: 0 // 总条数
    };
  },
  created() {
    this.fetchRegistrations(1); // 初始化加载今日预约信息
  },
  methods: {
    // 获取今日预约信息
    fetchRegistrations(pageNumber) {
      this.loading = true;
      this.error = '';
      this.currentPage = pageNumber;

      this.$request.get('/user/selectRegistrations', {
        params: {
          pageNum: this.currentPage,
          pageSize: this.pageSize,
          userId: this.user.id // 传递当前用户 ID
        }
      })
          .then(response => {
            if (response.data && response.data.list) {
              this.registrations = response.data.list;
            }
            if (response.data && response.data.total) {
              this.totalRegistrations = response.data.total;
            }
          })
          .catch(error => {
            console.error('获取用户挂号信息失败:', error);
            this.error = '获取用户挂号信息失败，请稍后重试';
          })
          .finally(() => {
            this.loading = false;
          });
    },

    // 分页切换
    handlePageChange(newPage) {
      this.fetchRegistrations(newPage); // 修改为调用正确的方法
    },

    // 查看挂号详情
    handleViewDetails(row) {
      console.log(row.order);
      this.$router.push({
        name: 'SelectRegistrationDetail',
        params: {id: row.order} // 传递挂号 ID
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