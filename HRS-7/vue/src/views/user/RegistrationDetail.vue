<template>
  <div class="order-detail-page">
    <!-- 数据加载中提示 -->
    <div v-if="isLoading" class="loading-container">
      <el-spinner />
      <span>正在加载订单详情，请稍候...</span>
    </div>
    <div v-else>
      <h2>订单详情</h2>
      <el-form label-width="120px" v-if="registrationDetail">
        <el-form-item label="订单编号">
          <span>{{ registrationDetail.order }}</span>
        </el-form-item>
        <el-form-item label="医院">
          <span>{{ registrationDetail.hospitalName }}</span>
        </el-form-item>
        <el-form-item label="科室">
          <span>{{ registrationDetail.departmentName }}</span>
        </el-form-item>
        <el-form-item label="医生姓名">
          <span>{{ registrationDetail.doctorName }}</span>
        </el-form-item>
        <el-form-item label="挂号时间">
          <span>{{ registrationDetail.registerTime }}</span>
        </el-form-item>
        <el-form-item label="挂号班次">
          <span>{{ registrationDetail.shiftType === 'DAY' ? '白班' : '夜班' }}</span>
        </el-form-item>
        <el-form-item label="订单价格">
          <span>{{ registrationDetail.price }} 元</span>
        </el-form-item>
        <el-form-item label="用户姓名">
          <span>{{ registrationDetail.userName }}</span>
        </el-form-item>
        <el-form-item label="用户性别">
          <span>{{ registrationDetail.userGender }}</span>
        </el-form-item>
        <el-form-item label="用户联系方式">
          <span>{{ registrationDetail.userPhone }}</span>
        </el-form-item>
        <el-form-item label="订单提交日期">
          <span>{{ registrationDetail.orderTime }}</span>
        </el-form-item>
      </el-form>
      <div v-else>
        <p>获取订单详情失败，请稍后重试。</p>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'OrderDetail',
  data() {
    return {
      registrationDetail: null,
      isLoading: true
    };
  },
  created() {
    this.fetchOrderDetail();
  },
  methods: {
    fetchOrderDetail() {
      const orderId = Number(this.$route.query.order);
      if (!orderId) {
        this.handleError('订单号参数缺失', null);
        this.isLoading = false;
        return;
      }
      this.$request.get('/registration/detail', {
        params: {
          orderId: orderId
        }
      })
          .then(response => {
            if (response.data) {
              this.registrationDetail = response.data;
            } else {
              this.handleError('获取订单详情失败', response.data);
            }
          })
          .catch(error => {
            this.handleError('获取订单详情失败', error);
          })
          .finally(() => {
            this.isLoading = false;
          });
    },
    handleError(message, error) {
      console.error(message, error);
      this.$message.error(`${message}，请稍后重试`);
    }
  }
};
</script>

<style scoped>
.order-detail-page {
  padding: 20px;
}

.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 50px;
  margin-bottom: 20px;
}
</style>