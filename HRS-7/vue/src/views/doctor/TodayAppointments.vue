<template>
  <div class="today-appointments">
    <h2>今日预约</h2>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading">加载中...</div>

    <!-- 错误提示 -->
    <div v-if="error" class="error">{{ error }}</div>

    <!-- 挂号信息表格 -->
    <el-table v-if="appointments.length > 0" :data="appointments" stripe>
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
      <!-- 操作 -->
      <el-table-column label="操作" width="300">
        <template slot-scope="scope">
          <el-button type="primary" size="small" @click="handleViewDetails(scope.row)">查看详情</el-button>

          <!-- 根据 status 显示不同的按钮或文本 -->
          <el-button
              v-if="scope.row.status === 0"
              type="success"
              size="small"
              @click="handleConsult(scope.row)"
          >
            问诊
          </el-button>

          <el-button
              v-else-if="scope.row.status === 1"
              type="warning"
              size="small"
              @click="handlePrescribe(scope.row)"
          >
            开药
          </el-button>

          <el-button
              v-else-if="scope.row.status === 2"
              type="info"
              size="small"
              @click="handleSubmit(scope.row)"
          >
            提交
          </el-button>

          <span
              v-else-if="scope.row.status === 3"
              class="completed-text"
          >
      已完成
    </span>

          <el-button
              v-else-if="scope.row.status === 5"
              type="primary"
              size="small"
              @click="handleFollowUp(scope.row)"
          >
            复诊
          </el-button>

          <el-button type="primary" size="small" @click="createArchive(scope.row)">建立住院档案</el-button>

        </template>
      </el-table-column>
    </el-table>

    <!-- 无数据提示 -->
    <div v-else class="no-data">今日暂无预约信息</div>

    <!-- 分页 -->
    <el-pagination
        background
        layout="prev, pager, next"
        :current-page="currentPage"
        :page-size="pageSize"
        :total="totalAppointments"
        @current-change="handlePageChange"
    ></el-pagination>
  </div>
</template>

<script>
export default {
  name: 'TodayAppointments',
  data() {
    return {
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'), // 当前用户信息
      appointments: [], // 挂号信息列表
      loading: false, // 加载状态
      error: '', // 错误信息
      currentPage: 1, // 当前页码
      pageSize: 10, // 每页显示条数
      totalAppointments: 0 // 总条数
    };
  },
  created() {
    this.fetchTodayAppointments(1); // 初始化加载今日预约信息
  },
  methods: {
    // 获取今日预约信息
    fetchTodayAppointments(pageNumber) {
      this.loading = true;
      this.error = '';
      this.currentPage = pageNumber;

      this.$request.get('/doctor/selectAppointments', {
        params: {
          pageNum: this.currentPage,
          pageSize: this.pageSize,
          userId: this.user.id // 传递当前用户 ID
        }
      })
          .then(response => {
            if (response.data && response.data.list) {
              this.appointments = response.data.list;
            }
            if (response.data && response.data.total) {
              this.totalAppointments = response.data.total;
            }
          })
          .catch(error => {
            console.error('获取今日预约信息失败:', error);
            this.error = '获取今日预约信息失败，请稍后重试';
          })
          .finally(() => {
            this.loading = false;
          });
    },

    // 分页切换
    handlePageChange(newPage) {
      this.fetchTodayAppointments(newPage);
    },

    // 查看挂号详情
    handleViewDetails(row) {
      console.log(row.order);
      this.$router.push({
        name: 'RegistrationDetail',
        params: {id: row.order} // 传递挂号 ID
      });
    },

    // 问诊
    handleConsult(row) {
      this.$confirm('是否开始问诊？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        console.log(row.userName);
        this.$router.push({
          name: 'Consultation',
          params: {
            id: row.order,
            name: row.userName
          } // 传递挂号 ID
        });
      }).catch(() => {
        // 用户取消操作
      });
    },

    // 格式化手机号（脱敏显示）
    formatPhone(phone) {
      if (phone && phone.length === 11) {
        return phone.substring(0, 3) + '****' + phone.substring(7);
      }
      return phone;
    },
    handlePrescribe(row) {
      //开药
      this.$confirm('是否开始开药？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(()=>{
        this.$router.push({
          name: 'Prescription',
          params: {
            name: row.userName
          } // 传递挂号 ID
        });
      }).catch(() => {
        // 用户取消操作
      });
    },
    handleSubmit(scope){
      //提交
      this.$confirm('是否提交？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(()=>{
        console.log(scope.order);
        this.$request.post(`/doctor/submitOrder/${scope.order}`)
            .then(response => {
          if (response.code == 200) {
            this.$message.success('提交成功');
            this.fetchTodayAppointments(this.currentPage); // 重新加载今日预约信息
          } else {
            this.$message.error(response.data.message || '提交失败失败');
          }
        })
      }).catch(() => {
        // 用户取消操作
      });
    },
    handleFollowUp(row){
      //复诊
      this.$confirm('是否开始复诊？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        console.log(row.userName);
        this.$router.push({
          name: 'Consultation',
          params: {
            id: row.order,
            name: row.userName
          } // 传递挂号 ID
        });
      }).catch(() => {
        // 用户取消操作
      });
    },
    createArchive(row){
      //建立档案
      this.$confirm('是否开始建立住院档案？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        console.log(row.order);
        this.$router.push({
          name: 'CreateArchive',
          params: {
            orderId: row.order
          } // 传递挂号 ID
        });
      }).catch(() => {
        // 用户取消操作
      });
    }
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