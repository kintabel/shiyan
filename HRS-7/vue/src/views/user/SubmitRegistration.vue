<template>
  <div class="submit-registration-page">
    <!-- 数据加载中提示 -->
    <div v-if="isLoading" class="loading-container">
      <el-spinner />
      <span>正在加载数据...</span>
    </div>
    <div v-else>
      <h2>提交挂号信息</h2>
      <el-form label-width="120px" v-if="doctor">
        <el-form-item label="医生姓名">
          <span>{{ doctor.name }} - {{ getDoctorTitle(doctor.status) }}</span>
        </el-form-item>
        <el-form-item label="班次类型">
          <el-tag :type="shiftType === 1 ? 'success' : 'warning'">
            {{ shiftType === 1 ? '白班' : '夜班' }}
          </el-tag>
        </el-form-item>
        <el-form-item label="挂号日期">
          <span>{{ formatDate(registrationDate) }}</span>
        </el-form-item>
        <el-form-item label="时间段">
          <span>{{ registrationTimeSlot }}</span>
        </el-form-item>
        <el-form-item label="价格">
          <span>{{ registrationPrice }} 元</span>
        </el-form-item>
        <el-form-item label="患者姓名">
          <span>{{ nowUser.name }}</span>
        </el-form-item>
        <el-form-item label="联系方式">
          <span>{{ nowUser.phone }}</span>
        </el-form-item>
        <el-form-item>
          <el-button
              @click="submitRegistration"
              type="primary"
              :loading="submitting"
          >
            确认提交
          </el-button>
          <el-button @click="goBack">返回修改</el-button>
        </el-form-item>
      </el-form>
      <div v-else>
        <el-empty description="未获取到医生信息">
          <el-button type="primary" @click="goBack">返回选择</el-button>
        </el-empty>
      </div>
    </div>
  </div>
</template>

<script>
import { parseTime } from '@/utils'

export default {
  name: 'SubmitRegistration',
  data() {
    return {
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      doctor: null,
      nowUser: null,
      registrationDate: this.$route.query.date || parseTime(new Date(), '{y}-{m}-{d}'),
      registrationTimeSlot: this.$route.query.timeSlot,
      registrationPrice: this.$route.query.price,
      shiftType: parseInt(this.$route.query.shiftType) || 1, // 1-白天 2-晚上
      isLoading: true,
      submitting: false,
      status: this.$route.query.status || 0
    }
  },
  created() {
    const doctorId = this.$route.query.doctorId
    const userId = this.user.id

    // 检查必要参数
    if (!doctorId || !this.registrationTimeSlot) {
      this.$message.error('缺少必要参数，请重新选择')
      this.$router.go(-1)
      return
    }

    this.fetchDoctorInfo(doctorId)
    this.fetchUserInfo(userId)
  },
  methods: {
    // 格式化日期显示
    formatDate(date) {
      return parseTime(date, '{y}年{m}月{d}日')
    },

    // 获取医生职称
    getDoctorTitle(status) {
      switch (status) {
        case 1: return '医师'
        case 2: return '副主治医师'
        case 3: return '主治医师'
        case 4: return '专家'
        case 5: return '急诊'
        default: return '未知职称'
      }
    },

    // 获取医生信息
    fetchDoctorInfo(doctorId) {
      this.isLoading = true
      this.$request.get('/doctor/selectDoctorById', {
        params: { doctorId }
      })
          .then(response => {
            if (response.data) {
              this.doctor = response.data
            } else {
              this.handleError('获取医生详细信息失败', response.data)
            }
          })
          .catch(error => {
            this.handleError('获取医生详细信息失败', error)
          })
          .finally(() => {
            this.isLoading = false
          })
    },

    // 获取用户信息
    fetchUserInfo(userId) {
      this.$request.get(`/user/selectById/${userId}`)
          .then(response => {
            if (response.data) {
              this.nowUser = response.data
            } else {
              this.handleError('获取用户详细信息失败', response.data)
            }
          })
          .catch(error => {
            this.handleError('获取用户详细信息失败', error)
          })
    },

    // 提交挂号信息
    submitRegistration() {
      if (!this.doctor || !this.nowUser) {
        this.$message.error('信息不完整，无法提交')
        return
      }

      this.submitting = true

      const registrationData = {
        doctorId: this.doctor.id,
        userId: this.nowUser.id,
        registrationDate: this.registrationDate,
        registrationTimeSlot: this.registrationTimeSlot,
        registrationPrice: this.registrationPrice,
        shiftType: this.shiftType === 1? 'DAY' : 'NIGHT',
        status: this.status,
      }

      this.$request.post('/registration/insert', registrationData)
          .then(response => {
            if (response.code == 200) {
              const order = response.data?.order
              this.$message.success('挂号成功')
              this.$router.push({
                name: 'RegistrationSuccess',
                query: { order }
              })
            } else {
              this.handleError('挂号失败', response.msg)
            }
          })
          .catch(error => {
            this.handleError('提交挂号信息失败', error)
          })
          .finally(() => {
            this.submitting = false
          })
    },

    // 返回上一页
    goBack() {
      this.$router.go(-1)
    },

    // 错误处理
    handleError(message, error) {
      console.error(message, error)
      this.$message.error(`${message}，请稍后重试`)
    }
  }
}
</script>

<style scoped>
.submit-registration-page {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 50px;
  margin-bottom: 20px;
}

.el-form-item {
  margin-bottom: 15px;
}

.el-form-item__content span {
  font-size: 16px;
  color: #333;
}

.el-tag {
  margin-left: 5px;
}

.el-button {
  margin-right: 10px;
}
</style>