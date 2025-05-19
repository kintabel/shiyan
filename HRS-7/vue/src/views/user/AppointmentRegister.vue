<template>
  <div class="appointment-page">
    <!-- 数据加载中提示 -->
    <div v-if="isLoading" class="loading-container">
      <el-spinner />
      <span>正在加载数据...</span>
    </div>

    <div v-else>
      <!-- 医院和科室信息展示 -->
      <div class="header-info">
        <h2>{{ hospitalName }} - {{ departmentName }}</h2>
        <p>请选择预约日期</p>
      </div>

      <!-- 日期选择 -->
      <div class="date-selection">
        <el-date-picker
            v-model="selectedDate"
            type="date"
            placeholder="选择预约日期"
            :picker-options="pickerOptions"
            :disabled-date="disabledDate"
            @change="handleDateChange"
        />
      </div>

      <!-- 操作按钮 -->
      <div class="action-buttons" v-if="selectedDate">
        <el-button
            type="primary"
            @click="goToDoctorSchedule"
            :disabled="!selectedDate"
        >
          查看当天排班医生
        </el-button>
      </div>
    </div>
  </div>
</template>

<script>
import { parseTime } from '@/utils'

export default {
  name: 'AppointmentRegister',
  data() {
    return {
      isLoading: false,
      hospitalId: this.$route.params.hospitalId,
      departmentId: this.$route.params.departmentId,
      hospitalName: '',
      departmentName: '',
      selectedDate: new Date(), // 默认今天
      pickerOptions: {
        shortcuts: [{
          text: '今天',
          onClick(picker) {
            picker.$emit('pick', new Date())
          }
        }, {
          text: '明天',
          onClick(picker) {
            const date = new Date()
            date.setDate(date.getDate() + 1)
            picker.$emit('pick', date)
          }
        }, {
          text: '一周后',
          onClick(picker) {
            const date = new Date()
            date.setDate(date.getDate() + 7)
            picker.$emit('pick', date)
          }
        }]
      }
    }
  },
  created() {
    this.fetchHospitalAndDepartmentInfo()
  },
  methods: {
    // 获取医院和科室信息
    async fetchHospitalAndDepartmentInfo() {
      try {
        this.isLoading = true

        // 获取医院信息
        const hospitalRes = await this.$request.get(`/hospital/selectById/${this.hospitalId}`)
        if (hospitalRes.data) {
          this.hospitalName = hospitalRes.data
          console.log(hospitalRes.data)
        }

        // 获取科室信息
        const deptRes = await this.$request.get(`/hospital/selectDepById/${this.departmentId}`)
        if (deptRes.data) {
          this.departmentName = deptRes.data
        }
      } catch (error) {
        console.error('获取医院或科室信息失败:', error)
        this.$message.error('获取信息失败，请稍后重试')
      } finally {
        this.isLoading = false
      }
    },

    // 禁用过去的日期
    disabledDate(time) {
      return time.getTime() < Date.now() - 24 * 60 * 60 * 1000
    },

    // 日期变化处理
    handleDateChange() {
    },

    // 跳转到医生排班界面
    goToDoctorSchedule() {
      if (!this.selectedDate) {
        this.$message.warning('请先选择预约日期')
        return
      }

      const formattedDate = parseTime(this.selectedDate, '{y}-{m}-{d}')

      this.$router.push({
        name: 'Doctor',
        params: {
          hospitalId: this.hospitalId,
          departmentId: this.departmentId,
          isAppointment: true,
        },
        query: {
          scheduleDate: formattedDate
        }
      })
    },

    // 格式化日期显示
    formatDate(date) {
      return parseTime(date, '{y}年{m}月{d}日')
    }
  }
}
</script>

<style scoped>
.appointment-page {
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

.header-info {
  margin-bottom: 30px;
  text-align: center;
}

.header-info h2 {
  margin-bottom: 10px;
  color: #333;
}

.date-selection {
  display: flex;
  justify-content: center;
  margin-bottom: 30px;
}

.action-buttons {
  display: flex;
  justify-content: center;
}

.el-date-editor {
  width: 300px;
}
</style>