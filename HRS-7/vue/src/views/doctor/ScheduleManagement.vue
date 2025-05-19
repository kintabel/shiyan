<template>
  <div class="doctor-page-table">
    <div v-if="isLoading" class="loading-container">
      <el-spinner />
      <span>正在加载数据...</span>
    </div>
    <div v-else>
      <el-table :data="[doctorInfo]" stripe>
        <el-table-column label="姓名">
          <template slot-scope="scope">
            <span>{{ scope.row.name }}</span>
          </template>
        </el-table-column>
        <el-table-column label="联系方式">
          <template slot-scope="scope">
            <span>{{ scope.row.phone }}</span>
          </template>
        </el-table-column>
        <el-table-column label="性别">
          <template slot-scope="scope">
            <span>{{ scope.row.gender }}</span>
          </template>
        </el-table-column>
      </el-table>

      <h4>当前周排班信息</h4>
      <el-table :data="weeklySchedules" stripe>
        <el-table-column label="日期">
          <template slot-scope="scope">
            <span>{{ formatDate(scope.row.scheduleDate) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="班次">
          <template slot-scope="scope">
            <el-tag :type="getShiftTagType(scope.row.shiftType)">
              {{ getShiftType(scope.row.shiftType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template slot-scope="scope">
            <el-button
                @click="openLeavePrompt(scope.row)"
                type="danger"
                :disabled="!scope.row.shiftType"
            >
              请假
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <h4>当前周请假信息</h4>
      <el-table :data="leaveApplications" stripe v-if="leaveApplications.length > 0">
        <el-table-column label="请假日期" width="180">
          <template slot-scope="scope">
            {{ formatDate(scope.row.leaveDate) }}
          </template>
        </el-table-column>
        <el-table-column label="请假类型">
          <template slot-scope="scope">
            <el-tag :type="getLeaveTagType(scope.row.leaveType)">
              {{ getLeaveTypeName(scope.row.leaveType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="请假原因" prop="reason"></el-table-column>
        <el-table-column label="状态" width="120">
          <template slot-scope="scope">
            <el-tag :type="getStatusTagType(scope.row.approvalStatus)">
              {{ getStatusName(scope.row.approvalStatus) }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
      <div v-else class="no-leave-info">
        本周暂无请假记录
      </div>
    </div>
  </div>
</template>

<script>
import { getCurrentWeekDates } from '@/utils/date.js'
import dayjs from 'dayjs'

export default {
  name: 'ScheduleManagement',
  data() {
    return {
      doctorInfo: null,
      isLoading: true,
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      weeklySchedules: [],
      currentWeekDates: [],
      leaveApplications: [] // 新增：存储请假信息
    }
  },
  computed: {
    // 计算是否有待审批的请假
    hasPendingLeave() {
      return this.leaveApplications.some(item => item.status === '1')
    }
  },
  created() {
    this.fetchDoctor(this.user.id)
    this.currentWeekDates = getCurrentWeekDates()
  },
  methods: {
    async fetchDoctor(userId) {
      try {
        const res = await this.$request.get('/doctor/selectDoctorByUserId', {
          params: { userId }
        })
        this.doctorInfo = res.data
        this.fetchWeeklySchedules(res.data)
        this.fetchLeaveApplications(res.data.id) // 新增：获取请假信息
      } catch (error) {
        this.$message.error('获取医生信息失败')
        console.error(error)
      } finally {
        this.isLoading = false
      }
    },
    async fetchWeeklySchedules(doctorInfo) {
      try {
        const res = await this.$request.get(`/api/schedule/doctor/${doctorInfo.id}`, {
          params: {
            startDate: this.currentWeekDates[0],
            endDate: this.currentWeekDates[6]
          }
        })
        this.weeklySchedules = res.data || []
      } catch (error) {
        this.$message.error('获取排班信息失败')
        console.error(error)
      }
    },
    // 新增方法：获取请假信息
    async fetchLeaveApplications(doctorId) {
      try {
        const res = await this.$request.get('/api/schedule/leave/listAll', {
          params: {
            doctorId,
            startDate: this.currentWeekDates[0],
            endDate: this.currentWeekDates[6]
          }
        })
        this.leaveApplications = res.data || []
      } catch (error) {
        this.$message.error('获取请假信息失败')
        console.error(error)
      }
    },
    // 打开请假提示框
    openLeavePrompt(schedule) {
      // 第一步：选择请假类型
      this.$prompt('请输入请假类型（事假/病假/年假/其他）', `请假申请 - ${this.formatDate(schedule.scheduleDate)}`, {
        confirmButtonText: '下一步',
        cancelButtonText: '取消',
        inputPattern: /^(事假|病假|年假|其他)$/,
        inputErrorMessage: '请输入有效的请假类型（事假/病假/年假/其他）'
      }).then(({ value }) => {
        const leaveTypeMap = {
          '事假': 'personal',
          '病假': 'sick',
          '年假': 'annual',
          '其他': 'other'
        };
        const leaveType = leaveTypeMap[value];

        // 第二步：输入原因
        this.$prompt('请输入请假原因', `请假申请 - ${value}`, {
          confirmButtonText: '提交',
          cancelButtonText: '上一步',
          inputType: 'textarea',
          inputPlaceholder: '请详细说明原因（至少10个字）',
          inputValidator: (value) => {
            if (!value) return '原因不能为空';
            if (value.length < 10) return '至少需要10个字符';
          }
        }).then(({ value: reason }) => {
          this.submitLeaveApplication({
            doctorId: this.doctorInfo.id,
            scheduleDate: schedule.scheduleDate,
            shiftType: schedule.shiftType,
            leaveType,
            reason
          });
        }).catch((action) => {
          if (action === 'cancel') {
            this.openLeavePrompt(schedule); // 点击"上一步"重新打开
          }
        });
      }).catch(() => {
        this.$message.info('已取消申请');
      });
    },

    // 新增方法：取消请假
    async cancelLeave(leaveId) {
      try {
        await this.$confirm('确定要取消这条请假申请吗?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        await this.$request.post('/api/schedule/leave/cancel', { id: leaveId })
        this.$message.success('请假已取消')
        this.fetchLeaveApplications(this.doctorInfo.id)
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error(error.response?.data?.message || '取消请假失败')
        }
      }
    },

    // 获取请假类型名称
    getLeaveTypeName(type) {
      const map = {
        personal: '事假',
        sick: '病假',
        annual: '年假',
        other: '其他'
      }
      return map[type] || type
    },

    getStatusName(status) {
      const map = {
        '0': '待审批',
        '1': '已通过',
        '2': '已拒绝'
      }
      return map[status] || status
    },

    // 修改后的状态标签类型（使用数字状态码）
    getStatusTagType(status) {
      const map = {
        '0': 'warning',  // 待审批 - 黄色
        '1': 'success',  // 已通过 - 绿色
        '2': 'danger'    // 已拒绝 - 红色
      }
      return map[status] || ''
    },
    // 新增方法：获取请假标签类型
    getLeaveTagType(type) {
      const map = {
        personal: '',
        sick: 'warning',
        annual: 'success',
        other: 'info'
      }
      return map[type] || ''
    },


    async submitLeaveApplication(data) {
      try {
        console.log('提交的请假申请数据：', data)
        await this.$request.post('/api/schedule/leaveApplication', data).then(res => {
          console.log('提交成功：', res.data)
          this.$message.success(`${this.getLeaveTypeName(data.leaveType)}申请提交成功`)
        })
        this.fetchWeeklySchedules(this.doctorInfo)
        this.fetchLeaveApplications(this.doctorInfo.id) // 新增：提交后刷新请假列表
      } catch (error) {
        this.$message.error(`提交失败：${error.response?.data?.message || error.message}`)
      }
    },

    // 辅助方法
    formatDate(date) {
      return dayjs(date).format('YYYY-MM-DD dddd')
    },
    getShiftTagType(shiftType) {
      return shiftType === 'DAY' ? 'success' : 'warning'
    },
    getShiftType(shiftType) {
      return shiftType === 'DAY' ? '白班' : shiftType === 'NIGHT' ? '夜班' : '无排班'
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
/* 新增样式 */
.no-leave-info {
  padding: 20px;
  text-align: center;
  color: #999;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  margin-top: 20px;
}
h4 {
  margin: 20px 0 15px 0;
  font-weight: normal;
  color: #606266;
}
</style>