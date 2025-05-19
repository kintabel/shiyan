<template>
  <div class="registration-success-page">
    <h2>挂号信息提交成功！</h2>
    <button @click="goHome">返回首页</button>
    <button @click="goOrderDetail">查看订单详情</button>
  </div>
</template>

<script>
export default {
  name: 'RegistrationSuccess',
  data() {
    return {
      order: null
    };
  },
  created() {
    this.order = Number(this.$route.query.order);
  },
  methods: {
    goHome() {
      // 跳转到首页，需根据实际路由配置修改路径
      this.$router.push('/');
    },
    goOrderDetail() {
      if (this.order) {
        this.$router.push({
          name: 'OrderDetail',
          query: {
            order: this.order
          }
        });
      } else {
        console.error('订单号参数缺失，无法查看订单详情');
        this.$message.error('订单号参数缺失，无法查看订单详情');
      }
    }
  }
};
</script>

<style scoped>
.registration-success-page {
  padding: 20px;
  text-align: center;
}

button {
  margin: 10px;
}
</style>