<template>
  <div class="patient-appeal-list">
    <!-- 页面标题 -->
    <h1>患者申诉列表</h1>
    <!-- 表格 -->
    <el-table :data="appealList" stripe>
      <el-table-column prop="appealOrder" label="申诉单号"></el-table-column>
      <el-table-column prop="patientName" label="患者姓名"></el-table-column>
<!--      <el-table-column prop="hospital" label="医院"></el-table-column>-->
<!--      <el-table-column prop="department" label="部门"></el-table-column>-->
<!--      <el-table-column prop="doctor" label="医生"></el-table-column>-->
<!--      <el-table-column prop="file" label="附件"></el-table-column>-->
<!--      <el-table-column prop="appealContent" label="申诉内容"></el-table-column>-->
      <el-table-column prop="appealTime" label="申诉时间"></el-table-column>
      <el-table-column label="操作">
        <template #default="scope">
          <el-button type="primary" size="small" @click="viewAppealDetail(scope.row.appealOrder)">查看详情</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
        background
        layout="prev, pager, next"
        :current-page="currentPage"
        :page-size="pageSize"
        :total="totalAppointments"
        @current-change="handlePageChange"
    ></el-pagination>

  </div>
</template>

<script>
export default {
  name: 'PatientAppealList',
  data() {
    return {
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'), // 当前用户信息
      appealList: [], // 申诉列表数据
      currentPage: 1, // 当前页码
      pageSize: 10, // 每页显示条数
      totalAppointments: 0, // 总条数
      dialogVisible: false, // 详情对话框是否显示
      selectedAppeal: {}, // 选中的申诉详情
      loading: false, // 加载状态
      error: '', // 错误信息
      userId: this.$route.query.hospitalId, // 医院ID
    };
  },
  created() {
    this.fetchAppealList(1); // 初始化加载申诉列表
  },
  methods: {
    // 获取申诉列表
    fetchAppealList(pageNumber) {
      this.loading = true;
      this.error = '';
      this.currentPage = pageNumber;
      const userId = this.$route.query.userId; // 获取医院ID

      this.$request.get('/registration/patient-appeal', {
        params: {
          pageNum: this.currentPage,
          pageSize: this.pageSize,
          userId: userId // 传递医院ID
        }
      })
          .then(response => {
            this.appealList = response.data.list;
            this.total = response.data.total;
          })
          .catch(error => {
            console.error('获取申诉列表失败', error);
          });
    },
    handlePageChange(newPage) {
      this.fetchTodayAppointments(newPage);
    },
    // 当前页码变化时触发
    handleCurrentChange(newPage) {
      this.currentPage = newPage;
      this.fetchAppealList();
    },
    // 查看申诉详情
    viewAppealDetail(appealOrder) {
      console.log(appealOrder);
      this.$router.push({
        name: 'AppealDetail',
        params: {
          appealOrder: appealOrder
        }
      })
    }
  }
};
</script>

<style scoped>
.patient-appeal-list {
  padding: 20px;
}
</style>