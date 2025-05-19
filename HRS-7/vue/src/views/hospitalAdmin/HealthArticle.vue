<template>
  <div class="app-container">
    <el-card>
      <div slot="header" class="clearfix">
        <span>发布健康科普文章</span>
        <div style="float: right">
          <el-button
              type="success"
              @click="submitForm('publish')"
              :loading="loading"
          >
            立即发布
          </el-button>
        </div>
      </div>

      <el-form
          :model="articleForm"
          :rules="rules"
          ref="articleForm"
          label-width="100px"
      >
        <el-form-item label="文章标题" prop="title">
          <el-input
              v-model="articleForm.title"
              placeholder="请输入文章标题"
              maxlength="50"
              show-word-limit
          ></el-input>
        </el-form-item>

        <el-form-item label="文章分类" prop="category">
          <el-select
              v-model="articleForm.category"
              placeholder="请选择分类"
              style="width: 100%"
          >
            <el-option
                v-for="item in categories"
                :key="item.value"
                :label="item.label"
                :value="item.value"
            ></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="文章内容" prop="content">
          <!-- 富文本编辑器 -->
          <el-input
              type="textarea"
              v-model="articleForm.content"
              :rows="10"
              placeholder="请输入文章内容"
          ></el-input>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'HealthArticlePublish',
  data() {
    return {
      user: JSON.parse(localStorage.getItem("xm-user") || '{}'),
      loading: false,
      articleForm: {
        title: '',
        content: '',
        category: '',
        hospitalId: '',
      },
      categories: [
        { value: '饮食健康', label: '饮食健康' },
        { value: '运动保健', label: '运动保健' },
        { value: '疾病预防', label: '疾病预防' },
        { value: '心理健康', label: '心理健康' },
        { value: '育儿知识', label: '育儿知识' }
      ],
      rules: {
        title: [
          { required: true, message: '请输入文章标题', trigger: 'blur' },
          { min: 5, max: 50, message: '长度在 5 到 50 个字符', trigger: 'blur' }
        ],
        category: [
          { required: true, message: '请选择文章分类', trigger: 'change' }
        ],
        content: [
          { required: true, message: '请输入文章内容', trigger: 'blur' },
          { min: 100, message: '内容至少需要100个字符', trigger: 'blur' }
        ]
      }
    };
  },
  methods: {
    submitForm() {
      this.$refs.articleForm.validate(valid => {
        if (valid) {
          this.loading = true;
          this.articleForm.hospitalId = this.user.id;
          this.$request.post('admin/addHealthArticle', this.articleForm)
              .then(response => {
                if (response.code == 200) {
                  this.$message.success( '发布成功');
                  this.resetForm();
                } else {
                  this.$message.error(response.message || '操作失败');
                }
              })
              .catch(error => {
                console.error('操作失败:', error);
                this.$message.error('操作失败，请稍后重试');
              })
              .finally(() => {
                this.loading = false;
              });
        }
      });
    },

    resetForm() {
      this.$refs.articleForm.resetFields();
    }
  }
};
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

.app-container {
  padding: 20px;
}

.el-form {
  margin-top: 20px;
}

.el-textarea {
  font-family: "Helvetica Neue", Helvetica, "PingFang SC", "Hiragino Sans GB", Arial, sans-serif;
}
</style>