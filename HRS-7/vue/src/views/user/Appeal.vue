<template>
  <div class="appeal-container">
    <h2>挂号申诉</h2>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading">加载中...</div>

    <!-- 错误提示 -->
    <div v-if="error" class="error">{{ error }}</div>

    <!-- 申诉表单 -->
    <div v-if="!loading &&!error" class="appeal-form">
      <el-form :model="appealForm" ref="appealFormRef" label-width="120px">
        <el-form-item label="订单号">
          <el-input v-model="appealForm.orderId" disabled></el-input>
        </el-form-item>
        <el-form-item label="申诉内容" prop="content">
          <el-input type="textarea" v-model="appealForm.content" placeholder="请输入申诉内容" rows="4"></el-input>
        </el-form-item>
        <el-form-item label="附件" prop="attachment">
          <el-upload
              style="display: inline-block; margin-right: 10px"
              :action="uploadUrl"
              :headers="{ token: this.user.token }"
              :show-file-list="false"
              :on-change="handleAttachmentChange"
              :on-success="handleFileSuccess"
          >
            <el-button size="small" type="primary">点击上传</el-button>
          </el-upload>
          <span v-if="appealForm.attachment">{{ appealForm.attachment.name }}</span>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitAppeal">提交申诉</el-button>
          <el-button @click="handleBack">返回</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Appeal',
  data() {
    return {
      appealForm: {
        orderId: this.$route.query.orderId || '',
        content: '',
        attachment: null
      },
      uploadUrl: '',
      loading: false,
      error: '',
      appealFormRef: null,
      user:JSON.parse(localStorage.getItem('xm-user') || '{}')
    };
  },
  created() {
    this.uploadUrl = this.$baseUrl + '/files/add'
  },
  methods: {
    // 处理附件上传变化
    handleAttachmentChange(file) {
      this.appealForm.attachment = file;
    },
    submitAppeal() {
      this.$refs.appealFormRef.validate((valid) => {
        if (valid) {
          this.loading = true;
          this.error = '';

          // 创建一个 FormData 对象来处理文件上传
          const formData = new FormData();
          formData.append('orderId', this.appealForm.orderId);
          formData.append('content', this.appealForm.content);
          if (this.appealForm.attachment) {
            // 获取文件的原始名字
            const fileName = this.appealForm.attachment.name;
            formData.append('attachment', this.appealForm.attachment);
            // 添加文件原始名字到 FormData
            formData.append('fileName', fileName);
          }
          console.log(formData.get('fileName'));
          this.$request.post('/registration/appeal', formData, {
            headers: {
              'Content-Type': 'multipart/form-data'
            }
          })
              .then(response => {
                this.loading = false;
                if (response.code == 200) {
                  this.$message.success('申诉提交成功');
                  this.handleBack();
                } else {
                  this.$message.error(response.data.message || '申诉提交失败，请稍后重试');
                }
              })
              .catch(error => {
                this.loading = false;
                console.error('申诉提交失败:', error);
                this.$message.error('申诉提交失败，请稍后重试');
              });
        } else {
          this.$message.error('请填写完整的申诉信息');
        }
      });
    },
    handleFileSuccess(response) {
      if (response.code == 200) {
        this.appealForm.attachmentUrl = response.data;
        this.$message.success('文件上传成功');
      } else {
        this.$message.error('文件上传失败');
      }
    },
    // 返回上一页
    handleBack() {
      this.$router.go(-1);
    }
  }
};
</script>

<style scoped>
.appeal-container {
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

.appeal-form {
  margin-top: 20px;
}
</style>