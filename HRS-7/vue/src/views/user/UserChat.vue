<template>
  <div class="app-container">
    <el-card>
      <div slot="header" class="clearfix">
        <span>医患在线沟通</span>
      </div>

      <!-- 医院、科室、医生选择区域 -->
      <el-row :gutter="20" class="selection-area">
        <el-col :span="8">
          <el-card shadow="hover">
            <div slot="header">
              <span>选择医院</span>
            </div>
            <div>
              <el-select
                  v-model="selectedHospital"
                  placeholder="请选择医院"
                  filterable
                  style="width: 100%"
                  @change="loadDepartments"
                  :loading="hospitalLoading"
              >
                <el-option
                    v-for="hospital in hospitals"
                    :key="hospital.id"
                    :label="hospital.name"
                    :value="hospital.id"
                />
              </el-select>
              <el-pagination
                  small
                  @current-change="handleHospitalPageChange"
                  :current-page="hospitalPageNum"
                  :page-size="hospitalPageSize"
                  layout="prev, pager, next"
                  :total="hospitalTotal"
                  style="margin-top: 15px"
              />
            </div>
          </el-card>
        </el-col>

        <el-col :span="8">
          <el-card shadow="hover">
            <div slot="header">
              <span>选择科室</span>
            </div>
            <el-select
                v-model="selectedDepartment"
                placeholder="请选择科室"
                style="width: 100%"
                @change="loadDoctors"
                :disabled="!selectedHospital"
                :loading="departmentLoading"
            >
              <el-option
                  v-for="dept in departments"
                  :key="dept.id"
                  :label="dept.name"
                  :value="dept.id"
              />
            </el-select>
          </el-card>
        </el-col>

        <el-col :span="8">
          <el-card shadow="hover">
            <div slot="header">
              <span>选择医生</span>
            </div>
            <div style="display: flex">
              <el-select
                  v-model="selectedDoctor"
                  placeholder="请选择医生"
                  style="flex: 1"
                  :disabled="!selectedDepartment"
                  :loading="doctorLoading"
              >
                <el-option
                    v-for="doctor in doctors"
                    :key="doctor.id"
                    :label="doctor.name"
                    :value="doctor.id"
                />
              </el-select>
              <el-button
                  type="primary"
                  @click="startChat"
                  :disabled="!selectedDoctor"
                  style="margin-left: 10px"
              >
                开始对话
              </el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 聊天区域 -->
      <el-card v-if="chatStarted" shadow="hover" style="margin-top: 20px">
        <div slot="header">
          <span>与 {{ currentDoctorName }} 医生的对话</span>
          <el-button
              type="text"
              @click="refreshMessages"
              style="float: right; padding: 3px 0"
          >
            <i class="el-icon-refresh"></i> 刷新消息
          </el-button>
        </div>

        <div class="chat-area">
          <div class="message-list">
            <div
                v-for="(msg, index) in messages"
                :key="index"
                :class="['message', msg.senderType === 'patient' ? 'sent' : 'received']"
            >
              <div class="message-content">{{ msg.content }}</div>
              <div class="message-meta">
                <span class="message-sender">{{ msg.senderName }}</span>
                <span class="message-time">{{ formatTime(msg.timestamp) }}</span>
              </div>
            </div>
          </div>

          <div class="input-area">
            <el-input
                type="textarea"
                :rows="3"
                v-model="newMessage"
                placeholder="输入消息..."
                resize="none"
            />
            <el-button
                type="primary"
                @click="sendMessage"
                :disabled="!newMessage.trim()"
                style="margin-top: 10px; float: right"
            >
              发送
            </el-button>
          </div>
        </div>
      </el-card>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'PatientChat',
  data() {
    return {
      newMessage: '',
      // 当前用户信息
      user: JSON.parse(localStorage.getItem('xm-user') || {}),
          // 医院选择相关
          hospitals: [],
        selectedHospital: '',
        hospitalLoading: false,
        hospitalPageNum: 1,
        hospitalPageSize: 10,
        hospitalTotal: 0,
        // 科室相关
        departments: [],
        selectedDepartment: '',
        departmentLoading: false,
        // 医生相关
        doctors: [],
        selectedDoctor: '',
        doctorLoading: false,
        // 聊天状态
        chatStarted: false,
        currentDoctorName: '',
        messages: [],
        refreshInterval: null
  };
  },
  computed: {
    patientId() {
      return this.user.id || ''
    },
    patientName() {
      return this.user.name || '患者'
    }
  },
  methods: {
    // 加载医院列表（分页）
    loadHospitals() {
      this.hospitalLoading = true
      const params = {
        pageNum: this.hospitalPageNum,
        pageSize: this.hospitalPageSize
      }

      this.$request.get('/hospital/selectPage', { params })
          .then(response => {
            if (response.code == 200) {
              this.hospitals = response.data.list || []
              this.hospitalTotal = response.data.total || 0
            }
          })
          .catch(error => {
            console.error('加载医院列表失败:', error)
            this.$message.error('加载医院列表失败')
          })
          .finally(() => {
            this.hospitalLoading = false
          })
    },

    // 医院分页变化
    handleHospitalPageChange(page) {
      this.hospitalPageNum = page
      this.loadHospitals()
    },

    // 加载科室列表
    loadDepartments() {
      if (!this.selectedHospital) return

      this.departmentLoading = true
      this.$request.get(`/hospital/selectAllData?id=${this.selectedHospital}`)
          .then(response => {
            this.departments = response.data?.departments || []
            this.selectedDepartment = ''
            this.doctors = []
          })
          .catch(error => {
            console.error('加载科室列表失败:', error)
            this.$message.error('加载科室列表失败')
          })
          .finally(() => {
            this.departmentLoading = false
          })
    },

    // 加载医生列表
    loadDoctors() {
      if (!this.selectedDepartment) return

      this.doctorLoading = true
      this.$request.get('/doctor/selectDoc', {
        params: {
          hospitalId: this.selectedHospital,
          departmentId: this.selectedDepartment }
      })
          .then(response => {
            this.doctors = response.data || []
            this.selectedDoctor = ''
          })
          .catch(error => {
            console.error('加载医生列表失败:', error)
            this.$message.error('加载医生列表失败')
          })
          .finally(() => {
            this.doctorLoading = false
          })
    },

    // 开始聊天
    startChat() {
      const doctor = this.doctors.find(d => d.id === this.selectedDoctor)
      if (!doctor) {
        this.$message.warning('医生信息获取失败')
        return
      }

      this.currentDoctorName = doctor.name
      this.chatStarted = true

      // 加载历史消息
      this.loadHistory()

      // 设置定时刷新
      this.setupRefresh()
    },

    // 设置消息刷新
    setupRefresh() {
      // 先清除已有定时器
      if (this.refreshInterval) {
        clearInterval(this.refreshInterval)
      }
      // 每5秒刷新一次消息
      this.refreshInterval = setInterval(() => {
        this.loadHistory()
      }, 5000)
    },

    // 手动刷新消息
    refreshMessages() {
      this.loadHistory()
      this.$message.success('消息已刷新')
    },

    // 加载历史消息
    loadHistory() {
      if (!this.selectedDoctor) return
      console.log(this.selectedDoctor,this.patientId)
      const role = 'patient'
      this.$request.get(`/chat/messages/${this.patientId}/${this.selectedDoctor}/${role}`)
          .then(response => {
            if (response.code == 200) {
              this.messages = response.data || []
              // 滚动到底部
              this.$nextTick(() => {
                const container = this.$el.querySelector('.message-list')
                if (container) {
                  container.scrollTop = container.scrollHeight
                }
              })
            }
          })
          .catch(error => {
            console.error('加载历史消息失败:', error)
          })
    },

    // 发送消息
    sendMessage() {
      if (!this.newMessage.trim()) {
        this.$message.warning('消息不能为空');
        return;
      }
      if (!this.selectedDoctor || this.selectedDoctor === '') {
        this.$message.warning('请先选择医生');
        return;
      }
      const message = {
        content: this.newMessage,
        senderType: 'patient',
        senderId: this.patientId,
        receiverId: this.selectedDoctor,
      };
      this.$request.post('/chat/send', message)
          .then(response => {
            if (1) {
              // 直接将新消息添加到消息列表
              this.messages.push(message);
              this.newMessage = '';
            }
          })
          .catch(error => {
            console.error('发送消息失败:', error);
            this.$message.error('发送消息失败');
          });
    },

    // 格式化时间
    formatTime(timestamp) {
      if (!timestamp) return ''
      const date = new Date(timestamp)
      return `${date.getFullYear()}-${(date.getMonth()+1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`
    }
  },
  mounted() {
    this.loadHospitals()
  },
  beforeDestroy() {
    // 清除定时器
    if (this.refreshInterval) {
      clearInterval(this.refreshInterval)
    }
  }
}
</script>

<style scoped>
.app-container {
  padding: 20px;
}

.selection-area {
  margin-bottom: 20px;
}

.chat-area {
  padding: 10px;
}

.message-list {
  height: 400px;
  overflow-y: auto;
  margin-bottom: 20px;
  padding: 10px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  background-color: #fafafa;
}

.message {
  margin: 10px 0;
  padding: 10px 15px;
  border-radius: 8px;
  max-width: 80%;
  word-break: break-word;
  box-shadow: 0 1px 2px rgba(0,0,0,0.1);
}

.message-content {
  margin-bottom: 5px;
  line-height: 1.5;
}

.message-meta {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #999;
  margin-top: 5px;
}

.sent {
  background-color: #e1f3d8;
  margin-left: auto;
  border-bottom-right-radius: 0;
}

.received {
  background-color: #f2f6fc;
  margin-right: auto;
  border-bottom-left-radius: 0;
}

.input-area {
  margin-top: 20px;
}

.el-icon-refresh {
  margin-right: 5px;
}
</style>