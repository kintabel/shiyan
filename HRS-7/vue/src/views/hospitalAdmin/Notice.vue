<template>
  <div class="app-container">
    <el-card>
      <div slot="header" class="clearfix">
        <span>医院动态管理</span>
        <el-button
            type="primary"
            size="small"
            @click="showAddDialog"
            style="float: right"
        >
          发布新动态
        </el-button>
      </div>

      <el-table :data="noticeList" border>
        <el-table-column prop="title" label="标题" width="200" />
        <el-table-column prop="publishTime" label="发布时间" width="120" />
        <el-table-column label="状态" width="80">
          <template #default="scope">
            <el-tag :type="scope.row.status === 0 ? 'success' : 'info'">
              {{ scope.row.status === 0 ? '显示' : '隐藏' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="scope">
            <el-button
                size="mini"
                @click="toggleStatus(scope.row.id)"
            >
              {{ scope.row.status === 0 ? '隐藏' : '显示' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 添加动态对话框 -->
    <el-dialog title="发布新动态" :visible.sync="dialogVisible" width="50%">
      <el-form :model="notice" label-width="80px">
        <el-form-item label="标题">
          <el-input v-model="notice.title" maxlength="50" show-word-limit />
        </el-form-item>
        <el-form-item label="内容">
          <el-input
              type="textarea"
              v-model="notice.content"
              :rows="5"
              maxlength="500"
              show-word-limit
          />
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitNews">发布</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  data() {
    return {
      user: JSON.parse(localStorage.getItem("xm-user") || '{}'),
      noticeList: [],
      dialogVisible: false,
      notice: {
        title: '',
        content: '',
        hospitalId: '',
      }
    }
  },
  methods: {
    loadNews() {
      this.$request.get('/admin/selectNotice',{
        params:{
          id:this.user.id,
          pageNum:1,
          pageSize:10
        }
      })
          .then(res => {
            this.noticeList = res.data.list;
          });
    },
    showAddDialog() {
      this.notice = { title: '', content: '' };
      this.dialogVisible = true;
    },
    submitNews() {
      this.notice.hospitalId = this.user.id;
      this.$request.post('/admin/addNotice', this.notice)
          .then(() => {
            this.$message.success('发布成功');
            this.dialogVisible = false;
            this.loadNews();
          });
    },
    toggleStatus(id) {
      this.$request.post(`/admin/toggleNewsStatus/${id}`)
          .then(() => {
            this.$message.success('状态已更新');
            this.loadNews();
          });
    }
  },
  mounted() {
    this.loadNews();
  }
}
</script>

<style scoped>
/* 保持与系统一致的极简样式 */
</style>