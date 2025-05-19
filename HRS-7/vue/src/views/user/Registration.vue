<template>
  <div class="registration-page">
    <!-- 医生信息展示 -->
    <div class="doctor-info" v-if="doctor">
      <h2>{{ doctor.name }} - {{ getDoctorTitle(doctor.status) }}</h2>
      <p>{{ doctor.introduction }}</p>
      <el-tag :type="shiftType === 1 ? 'success' : 'warning'">
        {{ shiftType === 1 ? '白班医生' : '夜班医生' }}
      </el-tag>
      <p class="current-time">当前时间: {{ currentTime }}</p>
    </div>

    <!-- 时间段选择 -->
    <div class="time-slot-selection" v-if="doctor">
      <h2>选择预约时间段</h2>

      <el-alert
          :title="timeRangeHint"
          type="info"
          show-icon
          class="time-hint"
      />

      <div v-if="filteredTimeSlots.length > 0">
        <el-radio-group v-model="selectedTimeSlot">
          <el-radio-button
              v-for="(slot, index) in filteredTimeSlots"
              :key="index"
              :label="slot.time"
          >
            {{ slot.time }} ({{ slot.price }}元)
            <span v-if="isSoon(slot)" class="soon-tag">即将开始</span>
          </el-radio-button>
        </el-radio-group>

        <el-button
            type="primary"
            @click="submitRegistration"
            :disabled="!selectedTimeSlot"
            class="submit-btn"
        >
          确认预约
        </el-button>
      </div>

      <div v-else class="no-slots">
        <el-empty description="今日已无可预约时间段"></el-empty>
      </div>
    </div>

    <div v-else>
      <p>未获取到医生信息，请稍后重试。</p>
    </div>
  </div>
</template>

<script>
import { parseTime } from '@/utils'

export default {
  name: 'Registration',
  data() {
    return {
      doctor: null,
      schedule: {},
      shiftType: parseInt(this.$route.query.shiftType) || 1, // 1-白天 2-晚上
      selectedTimeSlot: '',
      availableTimeSlots: [],
      isLoading: true,
      currentTime: parseTime(new Date(), '{h}:{i}:{s}'),
      currentDateTime: new Date()
    }
  },
  computed: {
    // 时间范围提示
    timeRangeHint() {
      return this.shiftType === 1
          ? '白班时间段: 9:00-12:00, 14:00-18:00'
          : '夜班时间段: 18:00-23:00, 0:00-8:00'
    },

    // 过滤出可预约的时间段（当前时间之后的）
    filteredTimeSlots() {
      return this.availableTimeSlots.filter(slot => {
        const slotTime = this.parseSlotTime(slot.time)
        return slotTime > this.currentDateTime
      })
    }
  },
  created() {
    const doctorId = this.$route.params.doctorId
    const doctorName = this.$route.query.doctorName
    const today = parseTime(new Date(), '{y}-{m}-{d}')
    if (doctorId) {
      this.fetchDoctorInfo(doctorId).then(() => {
        this.generateTimeSlots()
      })
    } else if (doctorName) {
      this.fetchDoctorInfoByName(doctorName).then(() => {
      })
      const hospitalName = this.$route.query.hospitalName
      const departmentName = this.$route.query.departmentName
      //查找当前是否复诊的医生是否有排班
      this.fetchDoctorSchedule(doctorName, today, hospitalName, departmentName).then(() => {
        this.shiftType = this.schedule.shiftType === 'DAY' ? 1 : 2
        console.log(this.shiftType)
        this.generateTimeSlots()
      })
    }

    // 每秒更新当前时间
    this.timeInterval = setInterval(() => {
      this.currentTime = parseTime(new Date(), '{h}:{i}:{s}')
      this.currentDateTime = new Date()
    }, 1000)
  },
  beforeDestroy() {
    clearInterval(this.timeInterval)
  },
  methods: {
    // 生成时间段
    generateTimeSlots() {
      console.log('当前医生的班次:', this.shiftType)
      if (this.shiftType === 1) {
        // 白班时间段
        this.availableTimeSlots = [
          {time: '09:00-10:00', price: this.getPrice(true)},
          {time: '10:00-11:00', price: this.getPrice(true)},
          {time: '11:00-12:00', price: this.getPrice(true)},
          {time: '14:00-15:00', price: this.getPrice(true)},
          {time: '15:00-16:00', price: this.getPrice(true)},
          {time: '16:00-17:00', price: this.getPrice(true)},
          {time: '17:00-18:00', price: this.getPrice(true)}
        ]
      } else {
        // 夜班时间段
        this.availableTimeSlots = [
          {time: '18:00-19:00', price: this.getPrice(false)},
          {time: '19:00-20:00', price: this.getPrice(false)},
          {time: '20:00-21:00', price: this.getPrice(false)},
          {time: '21:00-22:00', price: this.getPrice(false)},
          {time: '22:00-23:00', price: this.getPrice(false)},
          {time: '23:00-00:00', price: this.getPrice(false)},
          {time: '00:00-01:00', price: this.getPrice(false)},
          {time: '01:00-02:00', price: this.getPrice(false)},
          {time: '02:00-03:00', price: this.getPrice(false)},
          {time: '03:00-04:00', price: this.getPrice(false)},
          {time: '04:00-05:00', price: this.getPrice(false)},
          {time: '05:00-06:00', price: this.getPrice(false)},
          {time: '06:00-07:00', price: this.getPrice(false)},
          {time: '07:00-08:00', price: this.getPrice(false)}
        ]
      }
      this.isLoading = false
    },

    // 解析时间段为Date对象
    parseSlotTime(slotStr) {
      const today = new Date()
      const [startTime] = slotStr.split('-')
      const [hours, minutes] = startTime.split(':').map(Number)

      const slotDate = new Date(
          today.getFullYear(),
          today.getMonth(),
          today.getDate(),
          hours,
          minutes
      )

      // 处理跨天情况（如23:00-00:00）
      if (hours >= 0 && hours < 8 && this.shiftType === 2) {
        slotDate.setDate(slotDate.getDate() + 1)
      }

      return slotDate
    },

    // 判断时间段是否即将开始（15分钟内）
    isSoon(slot) {
      const slotTime = this.parseSlotTime(slot.time)
      const diff = (slotTime - this.currentDateTime) / (1000 * 60) // 分钟差
      return diff > 0 && diff <= 15
    },

    // 提交预约
    submitRegistration() {
      if (!this.selectedTimeSlot) {
        this.$message.warning('请选择预约时间段')
        return
      }

      this.$router.push({
        name: 'SubmitRegistration',
        query: {
          doctorId: this.doctor.id,
          timeSlot: this.selectedTimeSlot,
          price: this.availableTimeSlots.find(s => s.time === this.selectedTimeSlot).price,
          shiftType: this.shiftType,
          status: this.$route.query.status
        }
      })
    },

    fetchDoctorInfo(doctorId) {
      return this.$request.get('/doctor/selectDoctorById', {
        params: {doctorId}
      })
          .then(response => {
            if (response.data) {
              this.doctor = response.data
            } else {
              console.error('获取医生详细信息失败:', response.data)
              this.$message.error('获取医生详细信息失败，请稍后重试')
            }
          })
          .catch(error => {
            console.error('获取医生详细信息失败:', error)
            this.$message.error('获取医生详细信息失败，请稍后重试')
          })
    },
    fetchDoctorInfoByName(doctorName) {
      return this.$request.get('/doctor/selectDoctorByName', {
        params: {doctorName}
      })
          .then(response => {
            if (response.data) {
              this.doctor = response.data
            } else {
              console.error('获取医生详细信息失败:', response.data)
              this.$message.error('获取医生详细信息失败，请稍后重试')
            }
          })
          .catch(error => {
            console.error('获取医生详细信息失败:', error)
            this.$message.error('获取医生详细信息失败，请稍后重试')
          })
    },

    fetchDoctorSchedule(doctorName, scheduleDate, hospitalName, departmentName) {
      console.log("执行了fetchDoctorSchedule")
     return this.$request.get('/api/schedule/scheduleByDocName/' + doctorName, {
        params: {
          hospitalName: hospitalName,
          departmentName: departmentName,
          scheduleDate: scheduleDate,
        }
      }).then(response => {
        if (response.code == 200) {
          this.schedule = response.data
          console.log(this.schedule.shiftType)
        }
      })
    },

  getDoctorTitle(status) {
    switch (status) {
      case 1:
        return '医师'
      case 2:
        return '副主治医师'
      case 3:
        return '主治医师'
      case 4:
        return '专家'
      case 5:
        return '急诊'
      default:
        return '未知职称'
    }
  },

  getPrice(isMorning) {
    if (!this.doctor) return 0

    switch (this.doctor.status) {
      case 1:
        return isMorning ? 50 : 60
      case 2:
        return isMorning ? 80 : 90
      case 3:
        return isMorning ? 100 : 110
      case 4:
        return isMorning ? 150 : 160
      case 5:
        return isMorning ? 60 : 70
      default:
        return 0
    }
  }
}
}
</script>

<style scoped>
.registration-page {
  padding: 20px;
}

.doctor-info {
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #eee;
}

.current-time {
  color: #666;
  font-size: 14px;
  margin-top: 5px;
}

.time-hint {
  margin-bottom: 20px;
}

.submit-btn {
  margin-top: 20px;
}

.no-slots {
  margin-top: 50px;
}

.soon-tag {
  display: inline-block;
  background-color: #ffba00;
  color: white;
  font-size: 12px;
  padding: 2px 5px;
  border-radius: 3px;
  margin-left: 5px;
}

.el-radio-button {
  margin-right: 10px;
  margin-bottom: 10px;
}
</style>