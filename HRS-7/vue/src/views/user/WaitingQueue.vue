<template>
  <div class="waiting-queue-container">
    <!-- 当前挂号信息 -->
    <el-card v-if="currentRegister" class="current-register">
      <div slot="header">
        <span>您的挂号信息</span>
      </div>
      <el-descriptions :column="1" border>
        <el-descriptions-item label="挂号单号">{{ currentRegister.order }}</el-descriptions-item>
        <el-descriptions-item label="就诊时间">{{ currentRegister.registerTime }}</el-descriptions-item>
        <el-descriptions-item label="当前状态">
          <el-tag :type="statusTagType">{{ formatStatus(currentRegister.status) }}</el-tag>
        </el-descriptions-item>
      </el-descriptions>
    </el-card>

    <!-- 候诊队列 -->
    <el-card v-if="queue.length > 0" class="waiting-queue">
      <div slot="header">
        <span>候诊队列</span>
        <el-tag type="info" style="float: right">
          您前面还有 {{ queue.length }} 人
        </el-tag>
      </div>
      <el-table :data="queue" size="small">
        <el-table-column prop="queueNumber" label="排队号" width="80" />
        <el-table-column prop="nameHidden" label="患者" width="100" />
        <el-table-column prop="registerTime" label="预约时间" />
      </el-table>
    </el-card>

    <div v-else class="no-register">
      <el-empty description="今日无挂号记录"></el-empty>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      loading: false,
      currentRegister: null,
      queue: []
    }
  },
  computed: {
    statusTagType() {
      if (!this.currentRegister) return '';
      switch(this.currentRegister.status) {
        case 0: return 'warning'; // 待就诊
        case 1: return 'success'; // 就诊中
        case 2: return 'info';    // 已完成
        default: return '';
      }
    }
  },
  methods: {
    formatStatus(status) {
      const statusMap = {
        0: '待就诊',
        1: '就诊中',
        2: '已完成'
      };
      return statusMap[status] || '未知状态';
    },

    fetchTodayRegister() {
      const userId = this.user.id;
      console.log(userId);
      this.loading = true;
      this.$request.get('/user/register/today', {
        params: { userId: userId }
      })
          .then(response => {
            this.currentRegister = response.data;
            if (this.currentRegister) {
              this.fetchQueue();
            }
          })
          .finally(() => {
            this.loading = false;
          });
    },

    fetchQueue() {
      if (!this.currentRegister) return;


      this.$request.get('/user/selectWaitingQueues', {
        params: {
          orderNumber: this.currentRegister.order,
          registerTime: this.currentRegister.registerTime
        }
      })
          .then(response => {
            this.queue = response.data;
          });
    }
  },
  mounted() {
    this.fetchTodayRegister();
  }
}
</script>

<style scoped>
.waiting-queue-container {
  padding: 20px;
}

.current-register {
  margin-bottom: 20px;
}

.no-register {
  text-align: center;
  padding: 40px 0;
}
</style>