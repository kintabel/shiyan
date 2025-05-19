<template>
  <div class="app-container">
    <el-card>
      <div slot="header" class="clearfix">
        <span>操作日志追踪</span>
      </div>

      <!-- 查询表单 -->
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="操作模块">
          <el-input
              v-model="queryParams.module"
              placeholder="请输入模块名称"
              clearable
          ></el-input>
        </el-form-item>
        <el-form-item label="操作人员">
          <el-input
              v-model="queryParams.userName"
              placeholder="请输入操作人员"
              clearable
          ></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 日志表格 -->
      <el-table
          :data="logList"
          border
          style="width: 100%"
          v-loading="loading"
      >
        <el-table-column prop="module" label="操作模块" width="120"></el-table-column>
        <el-table-column prop="type" label="操作类型" width="100"></el-table-column>
        <el-table-column prop="userName" label="操作人员" width="120"></el-table-column>
        <el-table-column prop="description" label="操作描述"></el-table-column>
        <el-table-column prop="method" label="请求方法" width="120"></el-table-column>
        <el-table-column prop="createTime" label="操作时间" width="160"></el-table-column>
        <el-table-column label="操作" width="80">
          <template #default="scope">
            <el-button
                size="mini"
                type="text"
                @click="showDetail(scope.row)"
            >详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="queryParams.pageNum"
          :page-sizes="[10, 20, 30, 50]"
          :page-size="queryParams.pageSize"
          layout="total, sizes, prev, pager, next"
          :total="total"
          style="margin-top: 20px"
      ></el-pagination>

      <!-- 日志详情对话框 -->
      <el-dialog title="操作日志详情" :visible.sync="detailVisible" width="50%">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="操作模块">{{ currentLog.module }}</el-descriptions-item>
          <el-descriptions-item label="操作类型">{{ currentLog.type }}</el-descriptions-item>
          <el-descriptions-item label="操作人员">{{ currentLog.userName }}</el-descriptions-item>
          <el-descriptions-item label="请求方法">{{ currentLog.method }}</el-descriptions-item>
          <el-descriptions-item label="操作时间">{{ currentLog.createTime }}</el-descriptions-item>
          <el-descriptions-item label="操作描述">{{ currentLog.description }}</el-descriptions-item>
          <el-descriptions-item label="请求参数">
            <pre style="margin: 0">{{ formatParams(currentLog.params) }}</pre>
          </el-descriptions-item>
        </el-descriptions>
        <span slot="footer" class="dialog-footer">
          <el-button @click="detailVisible = false">关闭</el-button>
        </span>
      </el-dialog>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'OperationLog',
  data() {
    return {
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        module: '',
        userName: ''
      },
      loading: false,
      logList: [],
      total: 0,
      detailVisible: false,
      currentLog: {}
    }
  },
  methods: {
    // 获取日志列表
    getList() {
      this.loading = true
      this.$request.get('/admin/selectAllOperationLog', {
        params: this.queryParams
      }).then(response => {
        this.logList = response.data.list
        this.total = response.data.total
      }).finally(() => {
        this.loading = false
      })
    },

    // 查询操作
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },

    // 重置查询
    resetQuery() {
      this.queryParams = {
        pageNum: 1,
        pageSize: 10,
        module: '',
        userName: ''
      }
      this.getList()
    },

    // 分页大小变化
    handleSizeChange(val) {
      this.queryParams.pageSize = val
      this.getList()
    },

    // 当前页变化
    handleCurrentChange(val) {
      this.queryParams.pageNum = val
      this.getList()
    },

    // 显示详情
    showDetail(row) {
      this.currentLog = row
      this.detailVisible = true
    },

    // 格式化参数
    formatParams(params) {
      try {
        const obj = JSON.parse(params)
        return JSON.stringify(obj, null, 2)
      } catch (e) {
        return params
      }
    }
  },
  mounted() {
    this.getList()
  }
}
</script>

<style scoped>
.search-form {
  margin-bottom: 20px;
}
</style>