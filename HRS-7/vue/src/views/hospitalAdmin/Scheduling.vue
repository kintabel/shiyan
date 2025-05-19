<template>
  <div class="app-container">
    <el-card>
      <!-- 原有头部代码保持不变 -->
      <div slot="header" class="clearfix">
        <span>医生排班管理</span>
        <el-button
            style="float: right; padding: 3px 0"
            type="text"
            @click="showNextWeek"
        >
          排下一周班次
        </el-button>
      </div>

      <el-row :gutter="20">
        <el-col :span="6">
          <el-card>
            <div slot="header" class="clearfix">
              <span>科室列表</span>
            </div>
            <el-tree
                :data="departments"
                :props="defaultProps"
                @node-click="handleDepartmentClick"
            ></el-tree>
          </el-card>
        </el-col>

        <el-col :span="18">
          <el-card>
            <div slot="header" class="clearfix">
              <span>排班表 - {{ selectedDepartment ? selectedDepartment.name : '请选择科室' }}</span>
              <div style="float: right">
                <el-date-picker
                    v-model="currentWeek"
                    type="week"
                    format="yyyy 第 WW 周"
                    placeholder="选择周"
                    @change="fetchSchedules"
                >
                </el-date-picker>
              </div>
            </div>

            <el-table
                v-loading="loading"
                :data="scheduleData"
                border
                style="width: 100%"
            >
              <!-- 原有表格列保持不变 -->
              <el-table-column prop="doctorName" label="医生" width="150" fixed>
                <template slot-scope="scope">
                  {{ scope.row.doctorName }}
                </template>
              </el-table-column>
              <el-table-column
                  v-for="day in weekDays"
                  :key="day.date"
                  :label="day.label"
                  width="150"
              >
                <template slot-scope="scope">
                  <el-select
                      v-model="scope.row.schedules[day.date]"
                      placeholder="班次"
                      size="mini"
                      @change="handleShiftChange(scope.row.doctorId, day.date, $event)"
                  >
                    <el-option label="白班" value="DAY"></el-option>
                    <el-option label="夜班" value="NIGHT"></el-option>
                    <el-option label="休息" :value="null"></el-option>
                  </el-select>
                </template>
              </el-table-column>

              <!-- 修改后的请假信息列 -->
              <el-table-column label="请假信息" width="180" fixed="right">
                <template slot-scope="scope">
                  <div v-if="hasLeaveInfo(scope.row.doctorId)" class="leave-info">
                    <el-tag
                        v-for="leave in getLeaveInfo(scope.row.doctorId)"
                        :key="leave.id"
                        :type="getLeaveTagType(leave.status)"
                        size="mini"
                        style="margin-right: 5px; margin-bottom: 5px; cursor: pointer;"
                        @click="showLeaveDetail(leave, scope.row.doctorName)"
                    >
                      {{ formatLeaveDate(leave.leaveDate) }}: {{ getLeaveTypeName(leave.type) }}
                    </el-tag>
                  </div>
                  <div v-else class="no-leave">
                    无请假
                  </div>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-col>
      </el-row>
    </el-card>

    <!-- 请假详情对话框 -->
    <el-dialog
        title="请假详情"
        :visible.sync="leaveDetailVisible"
        width="500px"
        :close-on-click-modal="false"
    >
      <div v-if="currentLeave" class="leave-detail">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="医生姓名">{{ currentDoctorName }}</el-descriptions-item>
          <el-descriptions-item label="请假日期">{{ formatLeaveDate(currentLeave.leaveDate) }}</el-descriptions-item>
          <el-descriptions-item label="请假类型">{{ getLeaveTypeName(currentLeave.leaveType) }}</el-descriptions-item>
          <el-descriptions-item label="请假原因">{{ currentLeave.reason }}</el-descriptions-item>

          <el-descriptions-item label="申请时间">{{ formatDateTime(currentLeave.createTime) }}</el-descriptions-item>
        </el-descriptions>

        <div v-if="currentLeave.approvalStatus == '0'" class="leave-actions">
          <el-button type="success" @click="handleLeaveAction('1')">通过申请</el-button>
          <el-button type="danger" @click="handleLeaveAction('0')">拒绝申请</el-button>
        </div>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="leaveDetailVisible = false">关 闭</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import dayjs from 'dayjs'
import {
  getSchedulesByDepartment,
  addSchedule,
  updateSchedule,
  deleteSchedule
} from '@/api/scheduleApi'
import { parseTime } from '@/utils'

export default {
  name: 'Schedule',
  data() {
    return {
      user: JSON.parse(localStorage.getItem("xm-user") || '{}'),
      loading: false,
      hospitalId: null,
      departments: [],
      selectedDepartment: null,
      doctors: [],
      currentWeek: this.getNextWeekStartDate(),
      weekDays: [],
      scheduleData: [],
      leaveInfo: [],
      defaultProps: {
        children: 'children',
        label: 'name'
      },
      // 新增的请假详情相关数据
      leaveDetailVisible: false,
      currentLeave: null,
      currentDoctorName: ''
    }
  },
  created() {
    this.fetchDepartments()
    this.generateWeekDays()
  },
  methods: {
    getNextWeekStartDate() {
      const today = new Date()
      const nextMonday = new Date(today)
      nextMonday.setDate(today.getDate() + ((1 + 7 - today.getDay()) % 7 || 7))
      return nextMonday
    },
    generateWeekDays() {
      const startDate = new Date(this.currentWeek)
      this.weekDays = []
      for (let i = 0; i < 7; i++) {
        const date = new Date(startDate)
        date.setDate(startDate.getDate() + i)
        this.weekDays.push({
          date: parseTime(date, '{y}-{m}-{d}'),
          label: parseTime(date, '{m}-{d} 周') + ['日', '一', '二', '三', '四', '五', '六'][date.getDay()]
        })
      }
    },
    fetchDepartments() {
      this.$request.get('/hospital/departmentList', {
        params: {
          userId: this.user.id
        }
      }).then(response => {
        if (response.code == 200) {
          this.departments = response.data
        } else {
          this.$message.error(response.data.message || '获取科室列表失败')
        }
      })
    },
    async handleDepartmentClick(data) {
      this.selectedDepartment = data
      await this.fetchDoctors(data.id)
      await this.fetchSchedules()
      await this.fetchLeaveInfo()
    },
    fetchDoctors(departmentId) {
      this.$request.get('/doctor/selectDocByAdminIdAndDepId', {
        params: {
          userId: this.user.id,
          departmentId: departmentId
        }
      }).then(response => {
        if (response.code == 200) {
          this.doctors = response.data
          this.prepareScheduleData()
        } else {
          this.$message.error(response.data.message || '获取医生列表失败')
        }
      })
    },
    prepareScheduleData() {
      this.scheduleData = this.doctors.map(doctor => {
        const schedules = {}
        this.weekDays.forEach(day => {
          schedules[day.date] = null
        })
        return {
          doctorId: doctor.id,
          doctorName: doctor.name,
          schedules: schedules
        }
      })
    },
    async fetchSchedules() {
      if (!this.selectedDepartment) return

      this.loading = true
      this.generateWeekDays()
      this.prepareScheduleData()
      this.fetchLeaveInfo()
      try {
        const startDate = new Date(this.currentWeek)
        const endDate = new Date(startDate)
        endDate.setDate(startDate.getDate() + 6)

        const response = await getSchedulesByDepartment(
            this.user.id,
            this.selectedDepartment.id,
            startDate,
            endDate
        )

        response.data.forEach(schedule => {
          const scheduleDate = parseTime(schedule.scheduleDate, '{y}-{m}-{d}')
          const doctorSchedule = this.scheduleData.find(
              item => item.doctorId === schedule.doctorId
          )
          if (doctorSchedule) {
            doctorSchedule.schedules[scheduleDate] = schedule.shift_type
          }
        })
      } catch (error) {
        console.error('获取排班信息失败:', error)
      } finally {
        this.loading = false
      }
    },
    async fetchLeaveInfo() {
      if (!this.selectedDepartment) return;

      try {
        const startDate = new Date(this.currentWeek)
        const endDate = new Date(startDate)
        endDate.setDate(startDate.getDate() + 6)

        const response = await this.$request.get('/api/schedule/leave/list', {
          params: {
            hospitalAdminId: this.user.id,
            departmentId: this.selectedDepartment.id,
            startDate: startDate.toISOString().split('T')[0],
            endDate: endDate.toISOString().split('T')[0]
          }
        });

        if (response.code == 200) {
          this.leaveInfo = response.data || [];
        } else {
          this.$message.error(response.message || '获取请假信息失败');
        }
      } catch (error) {
        console.error('获取请假信息失败:', error);
        this.$message.error('获取请假信息失败');
      }
    },
    hasLeaveInfo(doctorId) {
      return this.leaveInfo.some(leave => leave.doctorId === doctorId);
    },
    getLeaveInfo(doctorId) {
      return this.leaveInfo.filter(leave => leave.doctorId === doctorId);
    },
    formatLeaveDate(date) {
      return dayjs(date).format('MM-DD');
    },
    formatDateTime(date) {
      return dayjs(date).format('YYYY-MM-DD HH:mm:ss');
    },
    getLeaveTypeName(type) {
      const map = {
        personal: '事假',
        sick: '病假',
        annual: '年假',
        other: '其他'
      };
      return map[type] || type;
    },
    getLeaveTagType(status) {
      const map = {
        PENDING: 'warning',
        APPROVED: 'success',
        REJECTED: 'danger',
        CANCELLED: 'info'
      };
      return map[status] || '';
    },
    getLeaveStatusName(status) {
      const map = {
        PENDING: '待审批',
        APPROVED: '已通过',
        REJECTED: '已拒绝',
        CANCELLED: '已取消'
      };
      return map[status] || status;
    },
    async handleShiftChange(doctorId, date, shiftType) {
      try {
        const existingSchedule = await this.findExistingSchedule(doctorId, date)

        if (shiftType) {
          if (existingSchedule) {
            await updateSchedule({
              id: existingSchedule.id,
              shift_type: shiftType
            })
          } else {
            await addSchedule({
              hospitalId: this.user.id,
              departmentId: this.selectedDepartment.id,
              doctorId: doctorId,
              scheduleDate: date,
              shiftType: shiftType,
              fatigueScore: 0
            })
          }
        } else {
          if (existingSchedule) {
            await deleteSchedule(existingSchedule.id)
          }
        }

        this.$message.success('排班更新成功')
      } catch (error) {
        console.error('更新排班失败:', error)
        this.$message.error('排班更新失败')
      }
    },
    async findExistingSchedule(doctorId, date) {
      try {
        const response = await getSchedulesByDepartment(
            this.user.id,
            this.selectedDepartment.id,
            new Date(date))
        return response.data.find(
            item => item.doctorId === doctorId &&
                parseTime(item.scheduleDate, '{y}-{m}-{d}') === date
        )
      } catch (error) {
        console.error('查找排班记录失败:', error)
        return null
      }
    },
    showNextWeek() {
      const nextWeek = new Date(this.currentWeek)
      nextWeek.setDate(nextWeek.getDate() + 7)
      this.currentWeek = nextWeek
      this.fetchSchedules()
      this.fetchLeaveInfo()
    },

    showLeaveDetail(leave, doctorName) {
      this.currentLeave = leave;
      this.currentDoctorName = doctorName;
      this.leaveDetailVisible = true;
    },

    async handleLeaveAction(action) {
      if(action == 1){
        try {
          console.log(action)
          this.loading = true;
          const id = this.currentLeave.id;
          const response = await this.$request.post(`/api/schedule/leave/approve/${id}`);
          if (response.code == 200) {
            this.$message.success(`请假${action == '1' ? '通过' : '拒绝'}成功`);
            await this.fetchLeaveInfo(); // 重新加载请假信息
            this.leaveDetailVisible = false; // 关闭对话框
          } else {
            this.$message.error(response.message || '操作失败');
          }
        } catch (error) {
          console.error('处理请假失败:', error);
          this.$message.error('操作失败');
        } finally {
          this.loading = false;
        }
      }else{
        await this.fetchLeaveInfo(); // 重新加载请假信息
        this.leaveDetailVisible = false; // 关闭对话框
      }

    }
  }
}
</script>

<style scoped>
.clearfix:before,
.clearfix:after {
  display: table;
  content: "";
}
.clearfix:after {
  clear: both;
}

.leave-info {
  min-height: 40px;
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  padding: 5px 0;
}

.no-leave {
  color: #999;
  font-size: 12px;
  text-align: center;
  padding: 10px 0;
}

.app-container {
  padding: 20px;
}

/* 新增样式 */
.leave-detail {
  padding: 10px;
}

.leave-actions {
  margin-top: 20px;
  text-align: center;
}

.leave-actions .el-button {
  margin: 0 10px;
}
</style>