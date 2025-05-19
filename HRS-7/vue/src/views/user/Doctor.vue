<template>
  <div class="doctor-page-table">
    <div v-if="isLoading" class="loading-container">
      <el-spinner />
      <span>正在加载数据...</span>
    </div>
    <div v-else>
      <el-table :data="doctors" stripe>
        <el-table-column prop="name" label="医生姓名"></el-table-column>
        <el-table-column label="医生职称">
          <template slot-scope="scope">
            {{ getDoctorTitle(scope.row.status) }}
          </template>
        </el-table-column>
        <el-table-column label="班次">
          <template slot-scope="scope">
            <el-tag :type="getShiftTagType(scope.row.id)">
              {{ getShiftType(scope.row.id) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template slot-scope="scope">
            <el-button
                @click="goToRegistration(scope.row)"
                type="primary"
                :disabled="!isAvailable(scope.row)"
            >
              {{ getButtonText(scope.row) }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script>
import { parseTime } from '@/utils'

export default {
  name: 'Doctor',
  data() {
    return {
      doctors: [],
      schedules: {}, // 存储医生排班信息 {doctorId: schedule}
      isLoading: true,
      currentHour: new Date().getHours(),
      isAppointment: this.$route.params.isAppointment
    }
  },
  computed: {
    isDayTime() {
      return this.currentHour >= 8 && this.currentHour < 18
    }
  },
  created() {
    this.fetchDoctors().then(() => {
      const isAppointment = this.$route.params.isAppointment
      if (isAppointment) {
        this.fetchAllSchedules(1)
      }else{
        this.fetchAllSchedules(2)
      }
    })
  },
  methods: {
    // 获取单个医生的排班信息
    getDoctorSchedule(doctorId) {
      return this.schedules[doctorId] || null
      console.log(this.schedules)
    },

    // 获取所有医生的排班信息
    async fetchAllSchedules(isAppointment) {
      const hospitalId = this.$route.params.hospitalId
      const departmentId = this.$route.params.departmentId
      var today
      if (isAppointment === 1) {
        today = this.$route.query.scheduleDate
        console.log(today)
      }else if (isAppointment === 2) {
        today = parseTime(new Date(), '{y}-{m}-{d}')
      }
      try {
        const response = await this.$request.get('/api/schedule/departmentByDoc/' + departmentId, {
          params: {
            hospitalId: hospitalId,
            startDate: today,
            endDate: today
          }
        })

        if (response.data) {
          // 将排班信息按doctorId存入schedules对象
          console.log(response.data)
          response.data.forEach(schedule => {
            this.$set(this.schedules, schedule.doctorId, schedule)
          })
        }
      } catch (error) {
        console.error('获取排班信息失败:', error)
        this.$message.error('获取排班信息失败')
      } finally {
        this.isLoading = false
      }
    },

    // 获取医生列表
    async fetchDoctors() {
      const hospitalId = this.$route.params.hospitalId
      const departmentId = this.$route.params.departmentId

      try {
        const response = await this.$request.get('/doctor/selectDoc', {
          params: {
            hospitalId: hospitalId,
            departmentId: departmentId
          }
        })

        if (response.data) {
          this.doctors = response.data
        } else {
          console.error('获取医生列表数据格式错误:', response.data)
          this.$message.error('获取医生列表失败，请稍后重试')
        }
      } catch (error) {
        console.error('获取医生列表失败:', error)
        this.$message.error('获取医生列表失败，请稍后重试')
      }
    },

    // 判断医生是否可预约
    isAvailable(doctor) {
      const schedule = this.getDoctorSchedule(doctor.id)
      if (!schedule) return false

      // 白天可以预约所有医生，晚上只能预约夜班医生
      return this.isDayTime || schedule.shiftType === 'NIGHT'
    },

    // 按钮文本
    getButtonText(doctor) {
      const schedule = this.getDoctorSchedule(doctor.id)
      if (!schedule) return '今日无排班'

      if (!this.isDayTime && schedule.shiftType === 'DAY') {
        return '非夜间时段'
      }
      return '预约挂号'
    },

    // 班次标签样式
    getShiftTagType(doctorId) {
      const schedule = this.getDoctorSchedule(doctorId)
      if (!schedule) return 'info'
      return schedule.shiftType === 'DAY' ? 'success' : 'warning'
    },

    // 获取班次类型文本
    getShiftType(doctorId) {
      const schedule = this.getDoctorSchedule(doctorId)
      if (!schedule) return '无排班'
      return schedule.shiftType === 'DAY' ? '白班' : '夜班'
    },

    // 跳转到挂号页面
    goToRegistration(doctor) {
      const schedule = this.getDoctorSchedule(doctor.id)
      const shiftType = schedule?.shiftType === 'DAY' ? 1 : 2

        this.$router.push({
          name: 'Registration',
          params: { doctorId: doctor.id },
          query: {
            status: 0,
            shiftType: shiftType
          }
        })
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