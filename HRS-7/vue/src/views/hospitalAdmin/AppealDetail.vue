<template>
  <div class="appeal-detail">
    <el-button type="text" @click="$router.go(-1)">返回</el-button>
    <h1>申诉详情</h1>
    <el-descriptions :column="1" border>
      <el-descriptions-item label="申诉单号">{{ appeal.appealOrder }}</el-descriptions-item>
      <el-descriptions-item label="患者姓名">{{ appeal.patientName }}</el-descriptions-item>
      <el-descriptions-item label="患者电话">{{ formatPhone(appeal.patientPhone) }}</el-descriptions-item>
      <el-descriptions-item label="患者性别">{{ appeal.patientGender }}</el-descriptions-item>
      <el-descriptions-item label="医院">{{ appeal.hospital }}</el-descriptions-item>
      <el-descriptions-item label="部门">{{ appeal.department }}</el-descriptions-item>
      <el-descriptions-item label="医生">{{ appeal.doctor }}</el-descriptions-item>
      <el-descriptions-item label="附件">
        <span @click="viewAttachment(appeal.file)" style="color: blue; cursor: pointer;">
          {{ appeal.file }}
        </span>
      </el-descriptions-item>
      <el-descriptions-item label="申诉内容">{{ appeal.appealContent }}</el-descriptions-item>
      <el-descriptions-item label="申诉时间">{{ appeal.appealTime }}</el-descriptions-item>
      <el-descriptions-item label="状态">{{ formatStatus(appeal.status) }}</el-descriptions-item>

    </el-descriptions>
    <!-- 根据状态动态显示按钮文本 -->
    <el-button type="primary" @click="appeal.status === 0 ? handleAppeal() : $router.go(-1)">
      <span v-if="appeal.status === 0">处理申诉</span>
      <span v-else-if="appeal.status === 1">提交</span>
    </el-button>
    <p v-if="error" style="color: red;">{{ error }}</p>
  </div>
</template>

<script>
import axios from 'axios';
export default {
  name: 'AppealDetail',
  data() {
    return {
      user: JSON.parse(localStorage.getItem("xm-user") || '{}'),
      appeal: {},
      appealOrder: this.$route.params.appealOrder,
      error: null,
      loading: true
    };
  },
  created() {
    this.fetchAppealDetail();
  },
  methods: {
    fetchAppealDetail() {
      this.$request.get('/registration/selectByAppealOrder', {
        params: {
          appealOrder: this.appealOrder,
        },
      }).then(response => {
        if (response.code == 200) {
          this.appeal = response.data;
        } else {
          this.error = response.data.message || '获取申诉失败';
        }
      })
          .catch(error => {
            console.error('获取申诉失败:', error);
            this.error = '获取申诉失败，请稍后重试';
          })
          .finally(() => {
            this.loading = false;
          });
    },
    formatStatus(status) {
      switch (status) {
        case 0:
          return '待处理';
        case 1:
          return '处理完成';
        case 2:
          return '已取消';
        default:
          return '未知状态';
      }
    },
    formatPhone(phone) {
      if (phone && phone.length === 11) {
        return phone.substring(0, 3) + '****' + phone.substring(7);
      }
      return phone;
    },
    handleAppeal() {
      console.log('开始处理申诉，申诉单号：', this.appeal.appealOrder);
      this.$request.post(`/registration/handleAppeal/${this.appeal.appealOrder}`).then(response => {
        if (response.code == 200) {
          console.log('申诉处理成功');
          this.appeal.status = 1; // 假设处理中状态码为 1
        } else {
          this.error = response.data.message || '处理申诉失败';
        }
      }).catch(error => {
        console.error('处理申诉失败:', error);
        this.error = '处理申诉失败，请稍后重试';
      });
    },
    viewAttachment(file) {
      if (file) {
        window.open(this.$baseUrl + '/files/preview/' + file)
      } else {
        console.log('没有附件文件');
      }
    },

  }
};
</script>

<style scoped>
.appeal-detail {
  padding: 20px;
}

.el-descriptions {
  margin-top: 20px;
}
</style>