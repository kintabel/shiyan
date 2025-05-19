<template>
  <div class="logistics-application">
    <h2>物流申请</h2>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading">提交中...</div>

    <!-- 错误提示 -->
    <div v-if="error" class="error">{{ error }}</div>

    <!-- 物流申请表单 -->
    <el-form :model="logisticsForm" :rules="rules" ref="logisticsForm" label-width="100px">
      <!-- 收货人姓名 -->
      <el-form-item label="收货人姓名" prop="userName">
        <el-input v-model="logisticsForm.userName" placeholder="请输入收货人姓名"></el-input>
      </el-form-item>

      <!-- 手机号 -->
      <el-form-item label="手机号" prop="phone">
        <el-input v-model="logisticsForm.phone" placeholder="请输入收货人手机号"></el-input>
      </el-form-item>

      <!-- 收货地址 -->
      <el-form-item label="收货地址" prop="inAddress">
        <el-input
            type="textarea"
            v-model="logisticsForm.inAddress"
            placeholder="请输入详细收货地址"
            :rows="3"
        ></el-input>
      </el-form-item>

      <!-- 提交按钮 -->
      <el-form-item>
        <el-button type="primary" @click="submitApplication">提交申请</el-button>
        <el-button @click="resetForm">重置</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
export default {
  name: 'LogisticsApplication',
  data() {
    // 先获取用户信息
    const user = JSON.parse(localStorage.getItem('xm-user') || '{}');

    // 手机号验证规则
    const validatePhone = (rule, value, callback) => {
      if (!value) {
        return callback(new Error('请输入手机号'));
      }
      if (!/^1[3-9]\d{9}$/.test(value)) {
        return callback(new Error('请输入正确的手机号'));
      }
      callback();
    };

    return {
      user: user, // 将用户信息保存到组件数据中
      logisticsForm: {
        doctorId: user.id || '', // 使用局部变量user
        userName: '',    // 收货人姓名
        phone: '',       // 手机号
        inAddress: ''   // 收货地址
      },
      rules: {
        userName: [
          { required: true, message: '请输入收货人姓名', trigger: 'blur' },
          { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
        ],
        phone: [
          { required: true, validator: validatePhone, trigger: 'blur' }
        ],
        inAddress: [
          { required: true, message: '请输入收货地址', trigger: 'blur' },
          { min: 5, max: 100, message: '长度在 5 到 100 个字符', trigger: 'blur' }
        ]
      },
      loading: false,
      error: ''
    };
  },
  methods: {
    // 提交物流申请
    submitApplication() {
      // 确保每次提交都使用最新的用户ID
      this.logisticsForm.doctorId = this.user.id;
      console.log(this.logisticsForm);
      this.$refs.logisticsForm.validate(valid => {
        if (valid) {
          this.loading = true;
          this.error = '';
          this.$request.post('/doctor/applicationLogistics', this.logisticsForm)
              .then(response => {
                if (response.code == 200) {
                  this.$message.success('物流申请提交成功');
                  this.resetForm();
                } else {
                  this.error = response.message || '提交失败';
                }
              })
              .catch(error => {
                console.error('物流申请提交失败:', error);
                this.error = '物流申请提交失败，请稍后重试';
              })
              .finally(() => {
                this.loading = false;
              });
        } else {
          return false;
        }
      });
    },

    // 重置表单
    resetForm() {
      this.$refs.logisticsForm.resetFields();
      // 重置后保留doctorId
      this.logisticsForm.doctorId = this.user.id;
    }
  }
};
</script>

<style scoped>
.logistics-application {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.loading,
.error {
  text-align: center;
  margin: 20px 0;
  font-size: 16px;
}

.error {
  color: #f56c6c;
}

.el-form {
  background: #fff;
  padding: 20px;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}
</style>