<template>
  <div class="consultation">
    <!-- 添加返回按钮 -->
    <el-button type="text" @click="$router.go(-1)">返回</el-button>
    <h2>问诊</h2>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading">加载中...</div>

    <!-- 错误提示 -->
    <div v-if="error" class="error">{{ error }}</div>

    <!-- 问诊信息 -->
    <div v-if="consultation" class="consultation-info">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="患者姓名">{{ consultation.userName }}</el-descriptions-item>
        <el-descriptions-item label="患者性别">{{ consultation.userGender }}</el-descriptions-item>
        <el-descriptions-item label="患者手机号">{{ formatPhone(consultation.userPhone) }}</el-descriptions-item>
        <el-descriptions-item label="预约时间">{{ consultation.registerTime }}</el-descriptions-item>
        <el-descriptions-item label="问诊状态">{{ formatStatus(consultation.status) }}</el-descriptions-item>
      </el-descriptions>
    </div>

    <!-- 问诊记录 -->
    <div class="consultation-records">
      <h3>问诊记录</h3>
      <el-table :data="records" stripe>
        <el-table-column prop="consultationDate" label="时间" width="180"></el-table-column>
        <el-table-column prop="content" label="内容"></el-table-column>
        <el-table-column label="操作" width="120">
          <template slot-scope="scope">
            <el-button type="text" @click="handleDeleteRecord(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 新增问诊记录 -->
    <div class="add-record">
      <h3>新增问诊记录</h3>
      <el-input
          type="textarea"
          :rows="4"
          v-model="newRecord"
          placeholder="请输入问诊记录"
      ></el-input>
      <el-button type="primary" @click="handleAddRecord">提交</el-button>
    </div>

    <!-- 操作按钮 -->
    <div class="actions">
      <el-button type="primary" @click="handleCompleteConsultation">完成问诊</el-button>
      <el-button type="danger" @click="handleCancelConsultation">取消问诊</el-button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Consultation',
  data() {
    return {
      consultation: null, // 问诊信息
      records: [], // 问诊记录
      newRecord: '', // 新增问诊记录内容
      loading: false, // 加载状态
      error: '' // 错误信息
    };
  },
  created() {
    this.fetchConsultation(); // 初始化加载问诊信息
    this.fetchRecords(); // 初始化加载问诊记录
  },
  methods: {
    // 获取问诊信息
    fetchConsultation() {
      this.loading = true;
      this.error = '';
      const id = this.$route.params.id; // 从路由参数中获取问诊 ID
      const name = this.$route.params.name; // 从路由参数中获取患者姓名
      console.log(name);
      console.log(id);
      this.$request.get('/registration/detail', {
        params: { orderId : id }
      })
          .then(response => {
            if (response.data) {
              this.consultation = response.data;
            } else {
              this.error = response.data.message || '获取问诊信息失败';
            }
          })
          .catch(error => {
            console.error('获取问诊信息失败:', error);
            this.error = '获取问诊信息失败，请稍后重试';
          })
          .finally(() => {
            this.loading = false;
          });
    },

    // 获取问诊记录
    fetchRecords() {
      const id = this.$route.params.id; // 从路由参数中获取问诊 ID
      const name = this.$route.params.name;
      console.log(name);
      this.$request.get('/doctor/getConsultationRecords', {
        params: { userName : name }
      })
          .then(response => {
            if (response.data) {
              this.records = response.data;
            } else {
              this.error = response.data.message || '获取问诊记录失败';
            }
          })
          .catch(error => {
            console.error('获取问诊记录失败:', error);
            this.error = '获取问诊记录失败，请稍后重试';
          });
    },

    // 格式化手机号（脱敏显示）
    formatPhone(phone) {
      if (phone && phone.length === 11) {
        return phone.substring(0, 3) + '****' + phone.substring(7);
      }
      return phone;
    },

    // 格式化问诊状态
    formatStatus(status) {
      switch (status) {
        case 0:
          return '进行中';
        case 1:
          return '待开药';
        case 2:
          return '待提交';
        case 5:
          return '复诊中';
        default:
          return '未知状态';
      }
    },

    // 新增问诊记录
    handleAddRecord() {
      if (!this.newRecord.trim()) {
        this.$message.warning('请输入问诊记录内容');
        return;
      }

      const name = this.$route.params.name;
      const id = this.$route.params.id;
      console.log(name);
      this.$request.post('/doctor/addConsultationRecord', {
        orderId: id,
        userName: name,
        content: this.newRecord
      })
          .then(response => {
            if (response.data) {
              this.$message.success('问诊记录添加成功');
              this.newRecord = ''; // 清空输入框
              this.fetchRecords(); // 重新加载问诊记录
            } else {
              this.$message.error(response.data.message || '问诊记录添加失败');
            }
          })
          .catch(error => {
            console.error('问诊记录添加失败:', error);
            this.$message.error('问诊记录添加失败，请稍后重试');
          });
    },

    // 删除问诊记录
    handleDeleteRecord(recordId) {
      this.$confirm('确定删除该问诊记录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$request.delete(`/doctor/deleteConsultationRecord/${recordId}`)
            .then(response => {
              if (response.code == 200) {
                this.$message.success('问诊记录删除成功');
                this.fetchRecords(); // 重新加载问诊记录
              } else {
                this.$message.error(response.data.message || '问诊记录删除失败');
              }
            })
            .catch(error => {
              console.error('问诊记录删除失败:', error);
              this.$message.error('问诊记录删除失败，请稍后重试');
            });
      }).catch(() => {
        // 用户取消操作
      });
    },

    // 完成问诊
    handleCompleteConsultation() {
      this.$confirm('确定完成问诊吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        const id = this.$route.params.id; // 从路由参数中获取问诊 ID

        this.$request.post(`/doctor/completeConsultation/${id}`)
            .then(response => {
              if (response.code == 200) {
                this.$message.success('问诊已完成');
                this.fetchConsultation(); // 重新加载问诊信息
              } else {
                this.$message.error(response.data.message || '完成问诊失败');
              }
            })
            .catch(error => {
              console.error('完成问诊失败:', error);
              this.$message.error('完成问诊失败，请稍后重试');
            });
      }).catch(() => {
        // 用户取消操作
      });
    },

    // 取消问诊
    handleCancelConsultation() {
      this.$confirm('确定取消问诊吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        const id = this.$route.params.id; // 从路由参数中获取问诊 ID
        console.log("当前:" + id);
        this.$request.post(`/doctor/cancelConsultation/${id}`)
            .then(response => {
              if (response.code == 200) {
                this.$message.success('问诊已取消');
                this.$router.go(-1);
              } else {
                this.$message.error(response.data.message || '取消问诊失败');
              }
            })
            .catch(error => {
              console.error('取消问诊失败:', error);
              this.$message.error('取消问诊失败，请稍后重试');
            });
      }).catch(() => {
        // 用户取消操作
      });
    }
  }
};
</script>

<style scoped>
.consultation {
  padding: 20px;
}

.loading,
.error {
  text-align: center;
  margin-top: 20px;
  font-size: 16px;
  color: #666;
}

.error {
  color: #f56c6c;
}

.consultation-info {
  margin-top: 20px;
}

.consultation-records {
  margin-top: 20px;
}

.add-record {
  margin-top: 20px;
}

.actions {
  margin-top: 20px;
  text-align: center;
}
</style>