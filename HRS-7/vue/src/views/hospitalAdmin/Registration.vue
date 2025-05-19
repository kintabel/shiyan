<template>
  <div class="app-container">
    <el-card>
      <div slot="header" class="clearfix">
        <span>挂号单管理</span>
        <div style="float: right">
          <el-button type="primary" @click="refreshRegister" :loading="loading">
            刷新列表
          </el-button>
        </div>
      </div>

      <el-table
          v-loading="loading"
          :data="registerList"
          border
          style="width: 100%"
      >
        <el-table-column prop="order" label="订单号" width="180" />
        <el-table-column prop="hospitalName" label="医院" width="120" />
        <el-table-column prop="departmentName" label="科室" width="120" />
        <el-table-column prop="doctorName" label="医生" width="120" />
        <el-table-column label="患者信息" width="180">
          <template #default="scope">
            <div>{{ scope.row.userName }}</div>
            <div>{{ formatPhone(scope.row.userPhone) }}</div>
            <div>{{ scope.row.userGender }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="orderTime" label="下单时间" width="160" />
        <el-table-column prop="registerTime" label="预约时间" width="160" />
        <el-table-column prop="price" label="价格" width="100" />
        <el-table-column label="状态" width="100">
          <template #default="scope">
            {{ formatStatus(scope.row.status) }}
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="currentPage"
          :page-sizes="[10, 20, 30, 50]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          style="margin-top: 20px"
      ></el-pagination>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'RegisterManagement',
  data() {
    return {
      user: JSON.parse(localStorage.getItem("xm-user") || '{}'),
      loading: false,
      registerList: [],
      currentPage: 1,
      pageSize: 10,
      total: 0
    };
  },
  methods: {
    refreshRegister() {
      this.getRegisterList();
    },

    getRegisterList() {
      this.loading = true;
      this.$request.get('/admin/selectRegisterPage', {
        params: {
          adminId: this.user.id,
          pageNum: this.currentPage,
          pageSize: this.pageSize
        }
      })
          .then(response => {
            if (response.code == 200) {
              this.registerList = response.data.list;
              this.total = response.data.total;
            } else {
              this.$message.error(response.data.message || '获取挂号单失败');
            }
          })
          .catch(error => {
            console.error('获取挂号单失败:', error);
            this.$message.error('请求失败，请稍后重试');
          })
          .finally(() => {
            this.loading = false;
          });
    },

    formatPhone(phone) {
      if (phone && phone.length === 11) {
        return phone.substring(0, 3) + '****' + phone.substring(7);
      }
      return phone;
    },

    formatStatus(status) {
      switch (status) {
        case 0: return '待问诊'
        case 1: return '已问诊'
        case 2: return '已开药'
        case 3: return '已完成'
        case 4: return '已取消'
        case 5: return '复诊'
        default: return '未知状态'
      }
    },


    handleSizeChange(val) {
      this.pageSize = val;
      this.getRegisterList();
    },

    handleCurrentChange(val) {
      this.currentPage = val;
      this.getRegisterList();
    }
  },
  mounted() {
    this.getRegisterList();
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

.el-table {
  margin-top: 10px;
}
</style>