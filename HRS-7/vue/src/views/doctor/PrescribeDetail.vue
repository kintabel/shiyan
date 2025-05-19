<template>
  <div class="prescription-list">
    <h2>处方列表</h2>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading">加载中...</div>

    <!-- 错误提示 -->
    <div v-if="error" class="error">{{ error }}</div>

    <!-- 处方表格 -->
    <el-table v-if="prescriptions.length > 0" :data="prescriptions" stripe>
      <el-table-column prop="order" label="订单号" width="120"></el-table-column>
      <el-table-column prop="hospitalName" label="医院名称" width="150"></el-table-column>
      <el-table-column prop="departmentName" label="科室名称" width="150"></el-table-column>
      <el-table-column prop="doctorName" label="医生姓名" width="120"></el-table-column>
      <el-table-column prop="date" label="日期" width="120"></el-table-column>
      <el-table-column prop="price" label="费用" width="80">
        <template slot-scope="scope">
          {{ scope.row.price }} 元
        </template>
      </el-table-column>
      <el-table-column prop="userName" label="患者姓名" width="120"></el-table-column>
      <el-table-column prop="userGender" label="患者性别" width="80"></el-table-column>
      <el-table-column prop="userPhone" label="患者手机号" width="140">
        <template slot-scope="scope">
          {{ formatPhone(scope.row.userPhone) }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="订单状态" width="120">
        <template slot-scope="scope">
          {{ formatStatus(scope.row.status) }}
        </template>
      </el-table-column>
      <el-table-column prop="medicines" label="药品名称" width="150"></el-table-column>
      <el-table-column prop="number" label="数量" width="80"></el-table-column>
      <el-table-column prop="content" label="备注" width="200"></el-table-column>
    </el-table>

    <!-- 显示总计价格 -->
    <div v-if="prescriptions.length > 0" class="total-price">
      总计价格：{{ totalPrice }} 元
    </div>

    <!-- 无数据提示 -->
    <div v-else class="no-data">暂无处方信息</div>

    <!-- 返回按钮 -->
    <div class="actions">
      <el-button type="primary" @click="handleBack">返回</el-button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'PrescriptionList',
  data() {
    return {
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'), // 当前用户信息
      prescriptions: [], // 处方列表信息
      loading: false, // 加载状态
      error: '', // 错误信息
      totalPrice: 0 // 总计价格
    };
  },
  created() {
    this.fetchPrescriptions(); // 初始化加载处方信息
  },
  methods: {
    // 获取处方列表信息
    fetchPrescriptions() {
      this.loading = true;
      this.error = '';
      const userId = this.user.id;
      console.log(this.$route.params.orderId);
      this.$request.get('/prescribe/selectPrescribeDetail', {
        params: { userId,orderId: this.$route.params.orderId }
      })
          .then(response => {
            if (response.data) {
              this.prescriptions = response.data;
              // 计算总计价格
              this.totalPrice = this.prescriptions.reduce((sum, prescription) => {
                return sum + prescription.price;
              }, 0);
            } else {
              this.error = response.data.message || '获取处方列表失败';
            }
          })
          .catch(error => {
            console.error('获取处方列表失败:', error);
            this.error = '获取处方列表失败，请稍后重试';
          })
          .finally(() => {
            this.loading = false;
          });
    },

    // 格式化手机号（脱敏显示）
    formatPhone(phone) {
      if (phone && phone.length === 11) {
        return phone.substring(0, 3) + '****' + phone.substring(7);
      }
      return phone;
    },

    // 格式化订单状态
    formatStatus(status) {
      switch (status) {
        case 0:
          return '待处理';
        case 1:
          return '已处理';
        case 2:
          return '已完成';
        case 3:
          return '已完成';
        case 5:
          return '复诊中';
        default:
          return '未知状态';
      }
    },

    // 返回上一页
    handleBack() {
      this.$router.go(-1);
    }
  }
};
</script>

<style scoped>
.prescription-list {
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

.total-price {
  text-align: right;
  margin-top: 10px;
  font-weight: bold;
}

.total-price {
  text-align: right;
  margin-top: 10px;
  font-weight: bold;
  font-size: 18px; /* 增大字体大小 */
  color: #333; /* 修改字体颜色 */
  background-color: #f9f9f9; /* 添加背景色 */
  border: 1px solid #e0e0e0; /* 添加边框 */
  padding: 10px; /* 添加内边距 */
  border-radius: 4px; /* 圆角边框 */
}

.actions {
  margin-top: 20px;
  text-align: center;
}
</style>